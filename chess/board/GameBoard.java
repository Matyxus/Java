package board;
import java.util.ArrayList;

import assets.Pair;
import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;

public class GameBoard {
    private final MoveGen moveGen;
    private final Fen fen;
    private final Castle castle;
    private final Zobrist zobrist;
    private final GameHistory gameHistory;
    /**
     * Current position
     */
    private Position currentPositon;
    
    private final Player[] players = {new Player(Colors.WHITE), new Player(Colors.BLACK)};
    private final int[] enpassantDiff = {8, -8};

    public GameBoard() {
        gameHistory = new GameHistory();
        zobrist = new Zobrist();
        castle = new Castle();
        currentPositon = new Position(Colors.WHITE, 0, 0, castle.getFullCastleRights());
        fen = new Fen();
        moveGen = new MoveGen(new Rays(), players, castle);
        System.out.println("GameBoard created");
    }

    /**
     * Deletes pieces on board and resets
     * associated values with them (e.g. bitboards)
     */
    public void reset() {
        System.out.println("Reseting to default values");
        for (Player player : players) {
            player.resetVals();
        }
    }

    /**
     * @param color of piece
     * @param square of piece
     * @return removed piece
     */
    public int removePiece(int color, int square) {
        return players[color].removePiece(square);
    }

    public void updatePieces(ArrayList<Move> move_list) {
        moveGen.updatePiecesMoves(currentPositon, move_list);
    }

    /**
     * Removes piece if any is placed on square before adding new one
     * @param piece to be added
     * @param color of piece
     * @param square of piece
     */
    public void addPiece(int piece, int color, int square) {
        if (containsPiece(square) != null) {
            Pair<Integer, Integer> target = containsPiece(square);
            removePiece(target.getValue(), square);
        }
        players[color].addPiece(piece, square);
    }

    /**
     * @param square on board
     * @return Pair, where key is piece, value is its color,
     * null if no piece is on given square
     */
    public Pair<Integer, Integer> containsPiece(int square) {
        // Out of bounds
        if (square < 0 || square >= Size.BOARD_SIZE) {
            return null;
        }
        if (players[Colors.WHITE].containsPiece(square) != -1) {
            return new Pair<Integer,Integer>(players[Colors.WHITE].containsPiece(square), Colors.WHITE);
        } else if (players[Colors.BLACK].containsPiece(square) != -1) {
            return new Pair<Integer,Integer>(players[Colors.BLACK].containsPiece(square), Colors.BLACK);
        }
        return null;
    }
    
    /**
     * @param fen string to load
     * @return true if fen is correct, false otherwise
     */
    public boolean loadFen(String fen) {
        ArrayList<Spot> pieces = this.fen.interpret(fen, currentPositon);
        if (pieces != null && !pieces.isEmpty()) {
            reset();
            // Load pieces
            pieces.forEach((spot) -> addPiece(
                spot.getPiece(), spot.getColor(), spot.getSquare())
            );
            currentPositon.setHash(zobrist.createHash(this));
            return true;
        }
        return false;
    }

    public String recordMove(Move move, int capture) {
        final Player enemy = players[Colors.opposite_color(currentPositon.getSideToMove())];
        final int piece = (move.isPromotion()) ? Pieces.PAWN : enemy.getPlacedPieces()[move.getToSquare()];
        return gameHistory.recordMove(piece, currentPositon.getSideToMove(), capture, move);
    }

    /**
     * @param move to be executed
     * @return captured piece, -1 if none was captured
     */
    public int applyMove(Move move) {
        final int piece = players[currentPositon.getSideToMove()].getPlacedPieces()[move.getFromSquare()];
        final long moveMask = players[currentPositon.getSideToMove()].movePiece(move.getFromSquare(), move.getToSquare());
        // Change hash
        long key = zobrist.getPieceHash(currentPositon.getSideToMove()*6+piece, move.getFromSquare());
        key ^= zobrist.getPieceHash(currentPositon.getSideToMove()*6+piece, move.getToSquare());
        // Remove previous enpassant hash
        if (currentPositon.getEnpassant() != 0) {
            key ^= zobrist.getEnpassantHash(currentPositon.getEnpassant());
        }
        // Reset enpassant on board for current player
        currentPositon.setEnpassant(0);

        int capture = -1;
        if (move.isCastle()) {
            // Move rook
            Pair<Integer, Integer> tmp = castle.getCastleRookSquares(currentPositon.getSideToMove(), move.getCastleSide());
            players[currentPositon.getSideToMove()].movePiece(tmp.getKey(), tmp.getValue());
            // Change hash
            key ^= zobrist.getPieceHash(currentPositon.getSideToMove()*6 + Pieces.ROOK, tmp.getKey());
            key ^= zobrist.getPieceHash(currentPositon.getSideToMove()*6 + Pieces.ROOK, tmp.getValue());
            // Change hash
            if (currentPositon.getCastlingRights()[currentPositon.getSideToMove()][Pieces.KING]) {
                key ^= zobrist.getCastlingRightsHash(currentPositon.getSideToMove(), Pieces.KING);
            }
            if (currentPositon.getCastlingRights()[currentPositon.getSideToMove()][Pieces.QUEEN]) {
                key ^= zobrist.getCastlingRightsHash(currentPositon.getSideToMove(), Pieces.QUEEN);
            }
            currentPositon.clearCastleRights(currentPositon.getSideToMove());
        } else {
            // Check castling rights
            for (int color = Colors.WHITE; color < Colors.COLOR_COUNT; color++) {
                for (int side = Pieces.KING; side < 2; side++) {
                    if (currentPositon.getCastlingRights()[color][side] && 
                        (moveMask & castle.getCastleMask()[color][side]) != 0) {
                        currentPositon.setCastlingRight(color, side, false);
                        // Change hash
                        key ^= zobrist.getCastlingRightsHash(color, side);
                    }
                }
            }
            // Capture
            if (move.isCapture()) {
                int to = move.getToSquare();
                // Enpassant Capture
                if (move.isEnpassant()) {
                    to += enpassantDiff[currentPositon.getSideToMove()];
                }
                capture = removePiece((currentPositon.getSideToMove()+1) & 1, to);
                // Change hash
                key ^= zobrist.getPieceHash(((currentPositon.getSideToMove()+1) & 1)*6+capture, to);
            } else if (move.isEnpassant()) { // Move generating enpassant
                // Set enpassant
                currentPositon.setEnpassant(
                    (move.getToSquare() + enpassantDiff[currentPositon.getSideToMove()])
                );
                // Change hash
                key ^= zobrist.getEnpassantHash(move.getToSquare()+enpassantDiff[currentPositon.getSideToMove()]);
            }
            // Promotion
            if (move.isPromotion()) {
                final int promoteTo = move.getPromotionPiece();
                players[currentPositon.getSideToMove()].promotePiece(move.getToSquare(), promoteTo);
                // Change hash
                key ^= zobrist.getPieceHash(currentPositon.getSideToMove()*6+piece, move.getToSquare());
                key ^= zobrist.getPieceHash(currentPositon.getSideToMove()*6+promoteTo, move.getToSquare());
            }
        }
        // Swap player
        currentPositon.swapPlayers();
        // Change hash
        currentPositon.modifyHash(key ^ zobrist.getBlackToMove());
        return capture;
    }

    /**
     * @param move to be reversed
     * @param capture Spot class previously captured
     */
    public void undoMove(Move move, int capture, Position previous) {
        // Load back previous state
        currentPositon = previous.deepCopy();
        if (move.isCastle()) {
            // Move rook
            Pair<Integer, Integer> tmp = castle.getCastleRookSquares(currentPositon.getSideToMove(), move.getCastleSide());
            players[currentPositon.getSideToMove()].movePiece(tmp.getValue(), tmp.getKey());
        } else {
            // Promotion
            if (move.isPromotion()) {
                players[currentPositon.getSideToMove()].promotePiece(move.getToSquare(), Pieces.PAWN);
            }
            // Add captured piece back
            if (capture != -1) {
                int to = move.getToSquare();
                // Captured using enpassant
                if (move.isEnpassant()) {
                    to += enpassantDiff[currentPositon.getSideToMove()];
                }
                players[(currentPositon.getSideToMove()+1) & 1].addPiece(capture, to);
            }
        }
        players[currentPositon.getSideToMove()].movePiece(move.getToSquare(), move.getFromSquare());
    }

    public int[] getPieces(int color) {
        return players[color].getPlacedPieces();
    }

    public int[] getCurrentPieces() {
        return players[getCurrentPlayer()].getPlacedPieces();
    }

    public Player getPlayer(int color) {
        return players[color];
    }


    public void setCurrentPlayer(int currentPlayer) {
        currentPositon.setSideToMove(currentPlayer);
    }
    
    public int getCurrentPlayer() {
        return currentPositon.getSideToMove();
    }

    /**
     * @return Fen String of current board
     */
    public String createFen() {
        return fen.createFen(this);
    }

    public Position getCurrentPositon() {
        return currentPositon;
    }

    @Override
    public String toString() {
        String result = "   __ __ __ __ __ __ __ __\n";
        int[] whitePieces = getPieces(Colors.WHITE);
        int[] blackPieces = getPieces(Colors.BLACK);
        for (int i = 0; i < Size.COLS; i++) {
            result += (Size.COLS -i)+ " |";
            for (int j = 0; j < Size.ROWS; j++) {
                String tmp = " ";
                if (whitePieces[8*i+j] != -1) {
                    tmp = Pieces.pieceToUnicode[Colors.WHITE][whitePieces[8*i+j]];
                } else if (blackPieces[8*i+j] != -1) {
                    tmp = Pieces.pieceToUnicode[Colors.BLACK][blackPieces[8*i+j]];
                }
                result += tmp + " |";
            }
            result += "\n";
        }
        result += "   A  B  C  D  E  F  G  H\n";
        result += "Fen: " + createFen() + "\n";
        return result;
    }

    public GameHistory getGameHistory() {
        return gameHistory;
    }
}
