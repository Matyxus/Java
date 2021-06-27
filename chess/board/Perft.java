package board;

import java.math.BigInteger;
import java.util.HashMap;

// https://www.chessprogramming.org/Perft_Results

import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;

public class Perft {
    private GameBoard board;
    public static long nodes = 0;
    private long[] pieceNodes = {0, 0, 0, 0, 0, 0};
    
    public Perft() {
        nodes = 0;
        board = new GameBoard();
    }

    public boolean init(int depth, boolean hashing) {
        addAllPieces();
        long now = System.nanoTime();
        perftWhite(depth);
        long after = System.nanoTime() - now;
        for (int i = 0; i < pieceNodes.length; i++) {
            System.out.println("Piece: " + i + " found moves: " + pieceNodes[i]);
            nodes += pieceNodes[i];
        }
        System.out.println("Found nodes: " + nodes);
        System.out.println("Took sec: " + after/1000000000);
        System.out.println("Took milisec: " + after/1000000);
        System.out.println("Took mircrosec: " + after/1000);
        System.out.println("Took nanosec: " + after);
        return true;
    }

    private void perftWhite(int depth){
        board.updateWhite();
        if (depth == 1) {
            board.getWhitePieces().forEach((square, spot)-> { 
                pieceNodes[spot.getPiece()]+=Long.bitCount(spot.getMoves());
            });
            //board.getWhitePieces().forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            return;
        }
        HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getWhitePieces().forEach((square, spot)->moves.put(square, new Spot(spot.getPiece(), Colors.WHITE, square, false)));
        board.getWhitePieces().forEach((square, spot)->moves.get(square).setMoves(spot.getMoves()));
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result = 0;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (Size.ONE << result);
                Spot capture = board.moveWhitePiece(square, result, piece);
                perftBlack(depth-1);
                UndoMoveWhite(piece, square, result);
                if (capture != null) {
                    board.addPiece(capture.getPiece(), Colors.BLACK, capture.getSquare());
                }
            }
        });
    }

    private void perftBlack(int depth){
        board.updateBlack();
        if (depth == 1) {
            board.getBlackPieces().forEach((square, spot)-> { 
                pieceNodes[spot.getPiece()]+=Long.bitCount(spot.getMoves());
            });
            //board.getBlackPieces().forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            return;
        }
        HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getBlackPieces().forEach((square, spot)->moves.put(square, new Spot(spot.getPiece(), Colors.BLACK, square, false)));
        board.getBlackPieces().forEach((square, spot)->moves.get(square).setMoves(spot.getMoves()));
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (Size.ONE << result);
                Spot capture = board.moveBlackPiece(square, result, piece);
                perftWhite(depth-1);
                UndoMoveBlack(piece, square, result);
                if (capture != null){
                    board.addPiece(capture.getPiece(), Colors.WHITE, capture.getSquare());
                }
            }
        });
    }
   
    private void UndoMoveWhite(int piece, Integer toSquare, int fromSquare) {
        board.removePiece(Colors.WHITE, fromSquare);
        board.addPiece(piece, Colors.WHITE, toSquare);
    }

    private void UndoMoveBlack(int piece, Integer toSquare, int fromSquare) {
        board.removePiece(Colors.BLACK, fromSquare);
        board.addPiece(piece, Colors.BLACK, toSquare);
    }
    
    /**
     * @param bitBoard
     * Prints bit board as binary number, 8 numbers on single line
     */
    public static final void Printer(long bitBoard) {
        String fin = "";
        String txt = "";
        for (int i = 0; i < Size.BOARD_SIZE; i++) {
            if (BigInteger.valueOf(bitBoard).testBit(i)) {
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

    /**
     *  Places pieces according to standard chess board starting position
     */
    private final void addAllPieces() {
        for (int i = 0; i < Size.ROWS; i++) { // pawns
            board.addPiece(Pieces.PAWN, Colors.WHITE, 48+i);
            board.addPiece(Pieces.PAWN, Colors.BLACK, 8+i);
        }
        // Kings
        board.addPiece(Pieces.KING, Colors.WHITE, 60);
        board.addPiece(Pieces.KING, Colors.BLACK, 4);
        // Queens
        board.addPiece(Pieces.QUEEN, Colors.WHITE, 59);
        board.addPiece(Pieces.QUEEN, Colors.BLACK, 3);
        // Bishops
        board.addPiece(Pieces.BISHOP, Colors.WHITE, 58);
        board.addPiece(Pieces.BISHOP, Colors.WHITE, 61);
        board.addPiece(Pieces.BISHOP, Colors.BLACK, 2);
        board.addPiece(Pieces.BISHOP, Colors.BLACK, 5);
        // Knights
        board.addPiece(Pieces.KNIGHT, Colors.WHITE, 62);
        board.addPiece(Pieces.KNIGHT, Colors.WHITE, 57);
        board.addPiece(Pieces.KNIGHT, Colors.BLACK, 1);
        board.addPiece(Pieces.KNIGHT, Colors.BLACK, 6);
        // Rooks
        board.addPiece(Pieces.ROOK, Colors.WHITE, 63);
        board.addPiece(Pieces.ROOK, Colors.WHITE, 56);
        board.addPiece(Pieces.ROOK, Colors.BLACK, 7);
        board.addPiece(Pieces.ROOK, Colors.BLACK, 0);
    }
}

