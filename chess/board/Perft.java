package board;

import java.math.BigInteger;
import java.util.HashMap;

import assets.Pair;

// https://www.chessprogramming.org/Perft_Results

import board.constants.Size;

public class Perft {
    private final GameBoard board;
    private long nodes = 0;
    
    public Perft() {
        board = new GameBoard();
    }

    /**
     * @param depth how many moves ahead should perft search
     * @param color of player to start
     * @param fen of board to be searched
     * @return Pair, where key is sum of all moves, value is time
     * taken in miliseconds
     */
    public Pair<Long, Long> init(int depth, int color, String fen) {
        if (!board.loadFen(fen)) {
            System.out.println("Incorrect fen:" + fen);
            return null;
        }
        nodes = 0;
        long now = System.nanoTime();
        perft(depth, color);
        long after = System.nanoTime() - now;
        System.out.println("Found nodes: " + nodes);
        System.out.println("Took sec: " + after/1000000000);
        System.out.println("Took milisec: " + after/1000000);
        System.out.println("Took mircrosec: " + after/1000);
        System.out.println("Took nanosec: " + after);
        return new Pair<Long, Long>(nodes, after/1000000);
    }

    private void perft(int depth, int color) {
        board.updatePieces(color);
        // Count moves
        if (depth == 1) {
            board.getPieces(color).forEach((square, spot)->nodes+=Long.bitCount(spot.getMoves()));
            return;
        }
        // Copy placed pieces
        final HashMap<Integer, Spot> moves = new HashMap<Integer, Spot>();
        board.getPieces(color).forEach((square, spot)-> {
            final Spot temp = new Spot(spot.getPiece(), color, square, false);
            temp.setMoves(spot.getMoves());
            moves.put(square, temp);
        });
        // Recursion
        moves.forEach((square, spot)->{
            int piece = spot.getPiece();
            long move = spot.getMoves();
            int result = 0;
            // Play all moves
            while (move != 0) {
                result = Long.numberOfTrailingZeros(move);
                move ^= (Size.ONE << result);
                // Do move
                Spot capture = board.movePiece(square, result, color, piece);
                // Switch player
                perft(depth-1, (color+1) & 1);
                // Undo move
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
    public final void boardPrinter(long bitBoard) {
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
}

