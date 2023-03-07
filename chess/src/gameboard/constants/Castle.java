package gameboard.constants;
import java.util.HashMap;
import java.util.Map;
import utils.Pair;

/**
 * Class holding static values related to castling
 */
public class Castle {
    /**
     * Array holding position of Rook and King they must be in
     * to be able to castle, in bit board format
     * (e.g. castleMask[Colors.WHITE][Pieces.KING])
     */
    public static final long[][] castleMask = {
        { // White
            ((Board.ONE << 60) | (Board.ONE << 63)), // King
            ((Board.ONE << 56) | (Board.ONE << 60))  // Queen
        }, 
        { // Black
            ((Board.ONE << 4) | (Board.ONE << 7)),   // King
            (Board.ONE | (Board.ONE << 4))           // Queen
        }
    };

    /**
     * Array indexed by (color, piece), where color is players color, piece is side
     * to which player is castling (QUEEN/KING), returns bitboard
     * of squares which kings has to go trough to castle 
     * (e.g. castleSquareMasks[Colors.White][Pieces.KING])
     */
    public static final long[][] castleSquareMasks = {
        { // White
            (Board.ONE << 61) | (Board.ONE << 62), // f1, g1
            (Board.ONE << 59) | (Board.ONE << 58) // d1, c1
        }, 
        {  // Black
            (Board.ONE << 5) | (Board.ONE << 6), // f8, g8
            (Board.ONE << 3) | (Board.ONE << 2) // d8, c8

        }
    };

    /**
     * HashMap indexed by (color, piece), where color is players color, piece is side
     * to which player is castling (QUEEN/KING), returns Pair, containing 
     * square from where king is and square where king is going to be after castling
     */
    private static final HashMap<Integer, HashMap<Integer, Pair<Integer, Integer>>> castleKingSquares = 
        new HashMap<Integer, HashMap<Integer, Pair<Integer, Integer>>>(Map.of(
        // White
        Colors.WHITE, new HashMap<Integer, Pair<Integer, Integer>>(Map.of(
            Pieces.KING,   new Pair<Integer, Integer>(60, 62), // e1 -> g1
            Pieces.QUEEN,  new Pair<Integer, Integer>(60, 58)  // e1 -> c1
        )),
        // Black
        Colors.BLACK, new HashMap<Integer, Pair<Integer, Integer>>(Map.of(
            Pieces.KING,   new Pair<Integer, Integer>(4, 6), // e8 -> g8
            Pieces.QUEEN,  new Pair<Integer, Integer>(4, 2)  // e8 -> c8
        ))
    ));

    /**
     * HashMap mapped by (color, piece), where color is players color, piece is side
     * to which player is castling (QUEEN/KING), returns Pair, containing 
     * square from where rook is and square where rook is going to be after castling
     */
    private static final HashMap<Integer, HashMap<Integer, Pair<Integer, Integer>>> castleRookSquares = 
        new HashMap<Integer, HashMap<Integer, Pair<Integer, Integer>>>(Map.of(
        // White
        Colors.WHITE, new HashMap<Integer, Pair<Integer, Integer>>(Map.of(
            Pieces.KING,   new Pair<Integer, Integer>(63, 61), // h1 -> f1
            Pieces.QUEEN,  new Pair<Integer, Integer>(56, 59)  // a1 -> d1
        )),
        // Black
        Colors.BLACK, new HashMap<Integer, Pair<Integer, Integer>>(Map.of(
            Pieces.KING,   new Pair<Integer, Integer>(7, 5), // h8 -> f8
            Pieces.QUEEN,  new Pair<Integer, Integer>(0, 3)  // a8 -> d8
        ))
    ));

    /**
     * Position where king must be to castle
     */
    public static final int[] correctKingPos = {60, 4};

    // ---------------------------- Getters ----------------------------

    /**
     * @param color of player
     * @param side to castle (KING/QUEEN)
     * @return Pair, where key is original position before castle, value 
     * is position after castling for king
     */
    public static Pair<Integer, Integer> getCastleKingSquares(int color, int side) {
        return castleKingSquares.get(color).get(side);
    }

    /**
     * @param color of player
     * @param side to castle (KING/QUEEN)
     * @return Pair, where key is original position before castle, value 
     * is position after castling for rook
     */
    public static Pair<Integer, Integer> getCastleRookSquares(int color, int side) {
        return castleRookSquares.get(color).get(side);
    }

    /**
     * @param color of player
     * @param side to castle (KING/QUEEN)
     * @return square on which rook must be standing
     */
    public static int getCorrectRookPos(int color, int side) {
        return castleRookSquares.get(color).get(side).getKey();
    }
    
    /**
     * @return Array indexed by (color, piece) representing castling rights,
     * all values set to true
     */
    public static boolean[][] getFullCastleRights() {
        return new boolean[][] {{true, true}, {true, true}};
    }

}
