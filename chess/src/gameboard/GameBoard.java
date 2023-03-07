package gameboard;
import gameboard.move_gen.Move;
import gameboard.constants.Colors;
import gameboard.constants.Pieces;
import gameboard.hash.Zobrist;
import gameboard.constants.Board;
import gameboard.constants.Castle;
import utils.Pair;

/**
 * Class representing current board (using Player and Position classes), 
 * provides methods to apply or undo move, add/remove pieces
 */
public class GameBoard {
    /**
     * Current position
     */
    private Position currentPositon;
    /**
     * Hashing of chess positions
     */
    private final Zobrist zobrist;
    private final Player[] players = {new Player(Colors.WHITE), new Player(Colors.BLACK)};

    public GameBoard() {
        zobrist = new Zobrist();
        currentPositon = new Position(Colors.WHITE, 0, 0, Castle.getFullCastleRights());
    }

    /**
     * Deletes pieces on board and resets
     * associated values with them (e.g. bitboards)
     */
    public void reset() {
        for (Player player : players) {
            player.resetVals();
        }
    }

    // ----------------------------------- Pieces ----------------------------------- 

    /**
     * Places piece on board if possible (PAWN cant be placed on first and last row),
     * if square is occupied new piece replaces old one
     * @param piece to be added
     * @param color of piece
     * @param square of piece
     * @return true on success, false otherwise
     */
    public boolean addPiece(int piece, int color, int square) {
        // Check pawn Placement
        if (piece == Pieces.PAWN && (square < 8 || square > 47)) {
            return false;
        }
        // Remove previously placed piece
        if (containsPiece(square) != null) {
            Pair<Integer, Integer> target = containsPiece(square);
            removePiece(target.getValue(), square);
        }
        players[color].addPiece(piece, square);
        return true;
    }

    /**
     * @param color of piece
     * @param square of piece
     * @return removed piece
     */
    public int removePiece(int color, int square) {
        return players[color].removePiece(square);
    }

    /**
     * @param square on board
     * @return Pair<piece, color>, null if no piece is on given square
     */
    public Pair<Integer, Integer> containsPiece(int square) {
        // Out of bounds
        if (square < 0 || square >= Board.BOARD_SIZE) {
            return null;
        } else if (players[Colors.WHITE].containsPiece(square) != -1) {
            return new Pair<Integer,Integer>(players[Colors.WHITE].containsPiece(square), Colors.WHITE);
        } else if (players[Colors.BLACK].containsPiece(square) != -1) {
            return new Pair<Integer,Integer>(players[Colors.BLACK].containsPiece(square), Colors.BLACK);
        }
        return null;
    }

    // ----------------------------------- Move ----------------------------------- 
    
    /**
     * @param move to be executed
     * @return captured piece, "-1" if none was captured
     */
    public int applyMove(Move move) {
        final int piece = players[currentPositon.getSideToMove()].getPlacedPieces()[move.getFromSquare()];
        final int offset = currentPositon.getSideToMove()*6; // Hashing offset
        final long moveMask = players[currentPositon.getSideToMove()].movePiece(move.getFromSquare(), move.getToSquare());
        // Change hash
        long key = zobrist.getPieceHash(offset + piece, move.getFromSquare());
        key ^= zobrist.getPieceHash(offset + piece, move.getToSquare());
        // Remove previous enpassant hash
        if (currentPositon.getEnpassant() != 0) {
            key ^= zobrist.getEnpassantHash(currentPositon.getEnpassant());
        }
        // Reset enpassant on board for current player
        currentPositon.setEnpassant(0);
        int capture = -1;
        if (move.isCastle()) {
            // Move rook
            Pair<Integer, Integer> tmp = Castle.getCastleRookSquares(currentPositon.getSideToMove(), move.getCastleSide());
            players[currentPositon.getSideToMove()].movePiece(tmp.getKey(), tmp.getValue());
            // Change hash
            key ^= zobrist.getPieceHash(offset + Pieces.ROOK, tmp.getKey());
            key ^= zobrist.getPieceHash(offset + Pieces.ROOK, tmp.getValue());
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
                        (moveMask & Castle.castleMask[color][side]) != 0) {
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
                    to += Board.enpassantDiff[currentPositon.getSideToMove()];
                }
                capture = removePiece(Colors.oppositeColor(currentPositon.getSideToMove()), to);
                // Change hash (offest is either 0 or 6 for white/black player)
                key ^= zobrist.getPieceHash((offset ^ 6) + capture, to);
            } else if (move.isEnpassant()) { // Move generating enpassant
                // Set enpassant
                currentPositon.setEnpassant(
                    (move.getToSquare() + Board.enpassantDiff[currentPositon.getSideToMove()])
                );
                // Change hash
                key ^= zobrist.getEnpassantHash(move.getToSquare() + Board.enpassantDiff[currentPositon.getSideToMove()]);
            }
            // Promotion
            if (move.isPromotion()) {
                final int promoteTo = move.getPromotionPiece();
                players[currentPositon.getSideToMove()].promotePiece(move.getToSquare(), promoteTo);
                // Change hash
                key ^= zobrist.getPieceHash(offset + piece, move.getToSquare());
                key ^= zobrist.getPieceHash(offset + promoteTo, move.getToSquare());
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
            Pair<Integer, Integer> tmp = Castle.getCastleRookSquares(currentPositon.getSideToMove(), move.getCastleSide());
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
                    to += Board.enpassantDiff[currentPositon.getSideToMove()];
                }
                players[Colors.oppositeColor(currentPositon.getSideToMove())].addPiece(capture, to);
            }
        }
        players[currentPositon.getSideToMove()].movePiece(move.getToSquare(), move.getFromSquare());
    }

    // ------------------------ Gettters ------------------------ 

    public Player[] getPlayers() {
        return players;
    }

    public Player getPlayer(int color) {
        return players[color];
    }

    public Position getCurrentPositon() {
        return currentPositon;
    }
    
    @Override
    public String toString() {
        String result = "   __ __ __ __ __ __ __ __\n";
        int[] whitePieces = players[Colors.WHITE].getPlacedPieces();
        int[] blackPieces = players[Colors.BLACK].getPlacedPieces();
        // Represent gamebaord as unicode
        for (int i = 0; i < Board.COLS; i++) {
            result += (Board.COLS - i) + " |";
            for (int j = 0; j < Board.ROWS; j++) {
                String tmp = " ";
                if (whitePieces[Board.ROWS * i + j] != -1) {
                    tmp = Pieces.pieceToUnicode[Colors.WHITE][whitePieces[Board.ROWS * i + j]];
                } else if (blackPieces[Board.ROWS * i+ j] != -1) {
                    tmp = Pieces.pieceToUnicode[Colors.BLACK][blackPieces[Board.ROWS * i + j]];
                }
                result += tmp + " |";
            }
            result += "\n";
        }
        result += "   A  B  C  D  E  F  G  H\n";
        return result;
    }
}
