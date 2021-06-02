package chess2.computer;
import chess2.board.GameBoard;
import chess2.board.Holder;
import chess2.board.Spot;
import chess2.board.Zobrist;
import java.math.BigInteger;
import java.util.HashMap;
//doesnt count castling, promotions !
//https://www.chessprogramming.org/Perft_Results

public class Perft {
    private GameBoard board;
    private int maxDepth = 5;
    public static long nodes = 0;
    
    public Perft(){
        nodes = 0;
        board = new GameBoard();
        board.enableEnpassant(true);
    }

    public boolean init(int depth, GameBoard gameBoard, boolean hashing) {
        nodes = 0;
        maxDepth = depth;
        if (depth == 6 || depth == 7) {
            addAllPieces();
        } else {
            if (!(gameBoard.canPlay())) { // usere entered invalid board
                return false;
            }
            board.loadGameBoard(gameBoard.getWhitePieces(), gameBoard.getBlackPieces(), gameBoard.getCurrentPlayer());
        }
        Zobrist.zobrist = 0;
        if (gameBoard.getCurrentPlayer() == Holder.WHITE){
            if (hashing){
                hashPerftWhite(maxDepth);
            } else {
                perftWhite(maxDepth);
            }
        } else {
            if (hashing){
                hashPerftBlack(maxDepth);
            } else {
                perftBlack(maxDepth);
            }
        }
        return true;
    }

    private void hashPerftWhite(int depth) {
        long key = Zobrist.zobrist; // curr key 
        long val = Zobrist.isValid(key, depth);// look for saved moved
        if (val != 0) {// found it
            nodes += val;
            return;
        }
        long nodesNow = nodes;
        board.updateWhite();
        if (depth == 1){
            board.getWhitePieces().forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            Zobrist.save(key, depth, (nodes-nodesNow));
            return;
        }
        HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getWhitePieces().forEach((square, spot)->moves.put(square, new Spot(spot.getPiece(), Holder.WHITE, false)));
        board.getWhitePieces().forEach((square, spot)->moves.get(square).setMoves(spot.getMoves()));
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result = 0;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (0b1L << result);
                //Hash
                Zobrist.zobrist ^= Zobrist.piecesHash[piece][square];
                Zobrist.zobrist ^= Zobrist.piecesHash[piece][result];
                board.clearEnpassant(Holder.WHITE);
                int capture = board.moveHashWhitePiece(square, result, piece);
                int capturedSquare = board.getRemovedPieceSquare();
                hashPerftBlack(depth-1); // rec
                //Undo move hash
                Zobrist.zobrist ^= Zobrist.piecesHash[piece][square];
                Zobrist.zobrist ^= Zobrist.piecesHash[piece][result];
                UndoMoveWhite(piece, square, result);
                if (capture != -1){
                    Zobrist.zobrist ^= Zobrist.piecesHash[capture+Zobrist.PIECE_OFFSET][capturedSquare];
                    board.addPiece(capture, Holder.BLACK, capturedSquare, false);
                }
            }
        });
        board.clearEnpassant(Holder.WHITE);
        Zobrist.save(key, depth,  (nodes-nodesNow));
        return;
    }

    private void hashPerftBlack(int depth) {
        long key = Zobrist.zobrist; // curr key
        key ^= Zobrist.blackToMove; // change side relative to
        long val = Zobrist.isValid(key, depth); // look for saved moved
        if (val != 0) { // found it
            nodes += val;
            return;
        }
        long nodesNow = nodes;
        board.updateBlack();
        if (depth == 1){
            board.getBlackPieces().forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            Zobrist.save(key, depth, (nodes-nodesNow));
            return;
        }
        HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getBlackPieces().forEach((square, spot)->moves.put(square, new Spot(spot.getPiece(), Holder.BLACK, false)));
        board.getBlackPieces().forEach((square, spot)->moves.get(square).setMoves(spot.getMoves()));
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (0b1L << result);
                // Hash move, from-to
                Zobrist.zobrist ^= Zobrist.piecesHash[piece+Zobrist.PIECE_OFFSET][square];
                Zobrist.zobrist ^= Zobrist.piecesHash[piece+Zobrist.PIECE_OFFSET][result];
                //
                board.clearEnpassant(Holder.BLACK);
                int capture = board.moveHashBlackPiece(square, result, piece);
                int capturedSquare = board.getRemovedPieceSquare();
                hashPerftWhite(depth-1); // rec
                //Undo move hash
                Zobrist.zobrist ^= Zobrist.piecesHash[piece+Zobrist.PIECE_OFFSET][square];
                Zobrist.zobrist ^= Zobrist.piecesHash[piece+Zobrist.PIECE_OFFSET][result];
                UndoMoveBlack(piece, square, result);
                if (capture != -1){ // add back Hash capture
                    Zobrist.zobrist ^= Zobrist.piecesHash[capture][capturedSquare];
                    board.addPiece(capture, Holder.WHITE, capturedSquare, false);
                }
            }
        });
        board.clearEnpassant(Holder.BLACK);
        Zobrist.save(key, depth, (nodes-nodesNow));
        return;
    }

    private void perftWhite(int depth){
        board.updateWhite();
        if (depth == 1){
            board.getWhitePieces().forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            return;
        }
        HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getWhitePieces().forEach((square, spot)->moves.put(square, new Spot(spot.getPiece(), Holder.WHITE, false)));
        board.getWhitePieces().forEach((square, spot)->moves.get(square).setMoves(spot.getMoves()));
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result = 0;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (0b1L << result);
                board.clearEnpassant(Holder.WHITE);
                int capture = board.moveWhitePiece(square, result, piece);
                int capturedSquare = board.getRemovedPieceSquare();
                perftBlack(depth-1);
                UndoMoveWhite(piece, square, result);
                if (capture != -1){
                    board.addPiece(capture, Holder.BLACK, capturedSquare, false);
                }
            }
        });
        board.clearEnpassant(Holder.WHITE);
        return;
    }

    private void perftBlack(int depth){
        board.updateBlack();
        if (depth == 1){
            board.getBlackPieces().forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            return;
        }
        HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getBlackPieces().forEach((square, spot)->moves.put(square, new Spot(spot.getPiece(), Holder.BLACK, false)));
        board.getBlackPieces().forEach((square, spot)->moves.get(square).setMoves(spot.getMoves()));
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (0b1L << result);
                board.clearEnpassant(Holder.BLACK);
                int capture = board.moveBlackPiece(square, result, piece);
                int capturedSquare = board.getRemovedPieceSquare();
                perftWhite(depth-1);
                UndoMoveBlack(piece, square, result);
                if (capture != -1){
                    board.addPiece(capture, Holder.WHITE, capturedSquare, false);
                }
            }
        });
        board.clearEnpassant(Holder.BLACK);
        return;
    }
   
    private void UndoMoveWhite(int piece, Integer toSquare, int fromSquare) {
        board.removePiece(Holder.WHITE, fromSquare);
        board.addPiece(piece, Holder.WHITE, toSquare, false);
    }

    private void UndoMoveBlack(int piece, Integer toSquare, int fromSquare) {
        board.removePiece(Holder.BLACK, fromSquare);
        board.addPiece(piece, Holder.BLACK, toSquare, false);
    }
    // for printing board, debugging
    public static final void Printer(long num) {
        String fin = "";
        String txt = "";
        for (int i = 0; i < Holder.BOARD_SIZE; i++) {
            if (BigInteger.valueOf(num).testBit(i)) {
                txt+="1";
            } else {
                txt +="0";
            }
            if (txt.length() == 8){
                fin += txt + "\n";
                txt = "";
            }
        }
        System.out.println(fin);
    }

    private final void addAllPieces(){
        for (int i = 0; i < Holder.ROWS; i++) { // pawns
            board.addPiece(Holder.PAWN, Holder.WHITE, 48+i, false);
            board.addPiece(Holder.PAWN, Holder.BLACK, 8+i, false);
        }
        //Kings
        board.addPiece(Holder.KING, Holder.WHITE, 60, false);
        board.addPiece(Holder.KING, Holder.BLACK, 4, false);
        //Queens
        board.addPiece(Holder.QUEEN, Holder.WHITE, 59, false);
        board.addPiece(Holder.QUEEN, Holder.BLACK, 3, false);
        //Bishops
        board.addPiece(Holder.BISHOP, Holder.WHITE, 58, false);
        board.addPiece(Holder.BISHOP, Holder.WHITE, 61, false);
        board.addPiece(Holder.BISHOP, Holder.BLACK, 2, false);
        board.addPiece(Holder.BISHOP, Holder.BLACK, 5, false);
        //Knights
        board.addPiece(Holder.KNIGHT, Holder.WHITE, 62, false);
        board.addPiece(Holder.KNIGHT, Holder.WHITE, 57, false);
        board.addPiece(Holder.KNIGHT, Holder.BLACK, 1, false);
        board.addPiece(Holder.KNIGHT, Holder.BLACK, 6, false);
        //Rooks
        board.addPiece(Holder.ROOK, Holder.WHITE, 63, false);
        board.addPiece(Holder.ROOK, Holder.WHITE, 56, false);
        board.addPiece(Holder.ROOK, Holder.BLACK, 7, false);
        board.addPiece(Holder.ROOK, Holder.BLACK, 0, false);
    }

    public final void free() {
        Zobrist.destroyMap();
        board = null;
    }
}

