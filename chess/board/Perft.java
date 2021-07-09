package board;

import java.math.BigInteger;
import java.util.HashMap;

// https://www.chessprogramming.org/Perft_Results

import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;

public class Perft {
    private final GameBoard board;
    public static long nodes = 0;
    
    public Perft() {
        nodes = 0;
        board = new GameBoard(null);
    }

    public boolean init(int depth, boolean hashing) {
        addAllPieces();
        long now = System.nanoTime();
        perft(depth, Colors.WHITE);
        long after = System.nanoTime() - now;
        System.out.println("Found nodes: " + nodes);
        System.out.println("Took sec: " + after/1000000000);
        System.out.println("Took milisec: " + after/1000000);
        System.out.println("Took mircrosec: " + after/1000);
        System.out.println("Took nanosec: " + after);
        return true;
    }

    private void perft(int depth, int color) {
        board.updatePieces(color);
        if (depth == 1) {
            board.getPieces(color).forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            return;
        }
        final HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getPieces(color).forEach((square, spot)-> {
            final Spot temp = new Spot(spot.getPiece(), color, square, false);
            temp.setMoves(spot.getMoves());
            moves.put(square, temp);
        });
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result = 0;
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (Size.ONE << result);
                Spot capture = board.movePiece(square, result, color, piece);
                perft(depth-1, (color+1) & 1);
                undoMove(piece, color, square, result);
                if (capture != null) {
                    board.addPiece(capture.getPiece(), capture.getColor(), capture.getSquare());
                }
            }
        });
    }

    private void undoMove(int piece, int color, int toSquare, int fromSquare) {
        board.removePiece(color, fromSquare);
        board.addPiece(piece, color, toSquare);
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

