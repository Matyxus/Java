package utils;

import java.util.ArrayList;
import java.util.HashMap;

import gameboard.GameBoard;
import gameboard.Position;
import gameboard.move_gen.MoveGen;
import gameboard.hash.Cache;
import gameboard.history.Fen;
import gameboard.move_gen.Move;
import gameboard.constants.Pieces;
import gameboard.constants.Board;

/**
 * Class exploring position of chess board up to given depth and
 * returns number of all possible moves
 * https://www.chessprogramming.org/Perft
 */
public class Perft {
    private final GameBoard board;
    private final MoveGen moveGen;
    private final Fen fen;
    private long nodes = 0;
    /**
     * https://www.chessprogramming.org/Perft#Divide
     */
    private final HashMap<String, Long> divide = new HashMap<>();
    private final Cache cache = new Cache();
    private int maxDepth = 0;
    
    public Perft() {
        board = new GameBoard();
        moveGen = new MoveGen(board.getPlayers());
        fen = new Fen();
    }

    /**
     * @param depth how many moves ahead should perft search
     * @param fen of board to be searched
     * @param detailedOutput true if info message should be printed
     * @return Pair<all moves, time (seconds)>
     */
    public Pair<Long, Long> run_perft(int depth, String fen, boolean detailedOutput) {
        // Check if Fen is legal
        // if (this.fen.isLegal(fen)) {
        //    return null;
        // }
        if (detailedOutput) {
            System.out.println("Testing perft to depth: " + depth);
            System.out.println(board);
            System.out.println("Fen: " + fen);
        }
        // Run
        maxDepth = depth;
        long now = System.nanoTime();
        nodes = perft(depth);
        long after = System.nanoTime() - now;
        if (detailedOutput) {
            System.out.println("Found nodes: " + nodes);
            System.out.println("Took sec: " + after/1000000000);
            System.out.println("Took milisec: " + after/1000000);
            printDivide();
            System.out.println("**************************** Finished ****************************");
        }
        reset();
        return new Pair<Long, Long>(nodes, after/1000000000);
    }

    private long perft(int depth) {
        Pair<Integer, Long> entry = cache.contains(board.getCurrentPositon().getHash());
        if (entry != null && entry.getKey() == depth) {
            return entry.getValue();
        }
        // Copy placed pieces
        ArrayList<Move> move_list = new ArrayList<>();
        moveGen.updatePiecesMoves(board.getCurrentPositon(), move_list);
        // Count moves
        if (depth == 1) {
            // Divide
            if (depth == maxDepth) {
                for (Move move : move_list) {
                    String movement = (Board.SQUARE_TO_ALGEBRAIC[move.getFromSquare()] + Board.SQUARE_TO_ALGEBRAIC[move.getToSquare()]);
                    if (move.isPromotion()) {
                        movement += Pieces.pieceToChar.get(move.getPromotionPiece());
                    }
                    divide.put(movement, Board.ONE);
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
                String movement = (Board.SQUARE_TO_ALGEBRAIC[move.getFromSquare()] + Board.SQUARE_TO_ALGEBRAIC[move.getToSquare()]);
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
     * Clears memory, resets values to default
     */
    public void reset() {
        board.reset();
        cache.clear();
        divide.clear();
        nodes = 0;
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
