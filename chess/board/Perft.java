package board;

import java.util.ArrayList;
import java.util.HashMap;

import assets.Pair;
import board.constants.Pieces;
import board.constants.Size;

// https://www.chessprogramming.org/Perft

public class Perft {
    private final GameBoard board;
    private long nodes = 0;
    
    /**
     * https://www.chessprogramming.org/Perft#Divide
     */
    private final HashMap<String, Long> divide = new HashMap<>();
    private final Cache cache = new Cache();
    private int maxDepth = 0;
    
    public Perft() {
        board = new GameBoard();
    }

    /**
     * @param depth how many moves ahead should perft search
     * @param fen of board to be searched
     * @return Pair, where key is sum of all moves, value is time
     * taken in seconds
     */
    public Pair<Long, Long> init(int depth, String fen) {
        board.reset();
        if (!board.loadFen(fen)) {
            System.out.println("Incorrect fen:" + fen);
            return null;
        }
        maxDepth = depth;
        System.out.println("Testing perft to depth: " + depth);
        System.out.println(board);
        nodes = 0;
        long now = System.nanoTime();
        nodes = perft(depth);
        long after = System.nanoTime() - now;
        System.out.println("Found nodes: " + nodes);
        System.out.println("Took sec: " + after/1000000000);
        System.out.println("Took milisec: " + after/1000000);
        System.out.println("**************************** Finished ****************************");
        cache.clear();
        return new Pair<Long, Long>(nodes, after/1000000000);
    }

    private long perft(int depth) {
        Pair<Integer, Long> entry = cache.contains(board.getCurrentPositon().getHash());
        if (entry != null && entry.getKey() == depth) {
            return entry.getValue();
        }
        // Copy placed pieces
        ArrayList<Move> move_list = new ArrayList<>();
        board.updatePieces(move_list);
        // Count moves
        if (depth == 1) {
            // Divide
            if (depth == maxDepth) {
                for (Move move : move_list) {
                    String movement = Size.SQUARE_TO_ALGEBRAIC[move.getFromSquare()] + Size.SQUARE_TO_ALGEBRAIC[move.getToSquare()];
                    if (move.isPromotion()) {
                        movement += Pieces.pieceToChar.get(move.getPromotionPiece());
                    }
                    divide.put(movement, 1L);
                }
            }
            cache.insert(board.getCurrentPositon().getHash(), new Pair<Integer,Long>(depth, (long)move_list.size()));
            return move_list.size();
        }
        Position current = board.getCurrentPositon().deepCopy();
        long now = 0;
        // Recursion, play all moves
        for (Move move : move_list) {
            // Do move
            int capture = board.applyMove(move);
            // Switch player
            long before = now;
            now += perft(depth-1);
            // Divide
            if (depth == maxDepth) {
                String movement = Size.SQUARE_TO_ALGEBRAIC[move.getFromSquare()] + Size.SQUARE_TO_ALGEBRAIC[move.getToSquare()];
                if (move.isPromotion()) {
                    movement += Pieces.pieceToChar.get(move.getPromotionPiece());
                }
                divide.put(movement, (now - before));
            }
            // Undo move
            board.undoMove(move, capture, current);
        }
        cache.insert(current.getHash(), new Pair<Integer,Long>(depth, now));
        return now;
    }
    
    /**
     * For comparison with another long values
     * @return number of moves found
     */
    public long getNodes() {
        return nodes;
    }

    /**
     * Prints divide (for debugging purposes)
     */
    public void printDivide() {
        divide.forEach((move, count) -> {
            System.out.println(move + ": " + count);
        });
    }

}

