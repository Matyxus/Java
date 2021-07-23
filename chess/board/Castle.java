package board;

import java.util.HashMap;
import java.util.Map;

import assets.Pair;
import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;

public class Castle {
    /**
     * Array holding position of Rook and King they must be in
     * to be able to castle, in bit board format
     * (e.g. castleMask[Colors.WHITE][Pieces.KING])
     */
    private final long[][] castleMask = {
        // White
        // King Side                               Queen side
        {((Size.ONE << 60) | (Size.ONE << 63)), ((Size.ONE << 56) | (Size.ONE << 60))}, 
        // Black
        // King Side                               Queen side
        {((Size.ONE << 4) | (Size.ONE << 7)), (Size.ONE | (Size.ONE << 4))}
    };

    /**
     * HashMap indexed by (color, piece), where color is players color, piece is side
     * to which player is castling (QUEEN/KING), returns Pair, containing 
     * square from where king is and square where king is going to
     */
    private final HashMap<Integer, HashMap<Integer, Pair<Integer, Integer>>> castleKingSquares = 
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
     * HashMap indexed by (color, piece), where color is players color, piece is side
     * to which player is castling (QUEEN/KING), returns Pair, containing 
     * square from where rook is and square where rook is going to
     */
    private final HashMap<Integer, HashMap<Integer, Pair<Integer, Integer>>> castleRookSquares = 
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
     * Map containing squares king has to go trough, to castle.
     */
    private final HashMap<Integer, HashMap<Integer, Long>> castleSquareMasks = 
    new HashMap<Integer, HashMap<Integer, Long>>(Map.of(
        // White
        Colors.WHITE, new HashMap<Integer, Long>(Map.of(
            Pieces.KING, (Size.ONE << 61) | (Size.ONE << 62), // f1, g1
            Pieces.QUEEN, (Size.ONE << 59) | (Size.ONE << 58) // d1, c1
        )),
        // Black
        Colors.BLACK, new HashMap<Integer, Long>(Map.of(
            Pieces.KING, (Size.ONE << 5) | (Size.ONE << 6),  // f8, g8
            Pieces.QUEEN, (Size.ONE << 3) | (Size.ONE << 2)  // d8, c8
        ))
    ));

    /**
     * Position where king must be to castle
     */
    private final int[] correctKingPos = {60, 4};

    public Castle() {};

    /**
     * @param color of player
     * @param side to castle (KING/QUEEN)
     * @return Pair, where key is original position before castle, value 
     * is position after castling for king
     */
    public Pair<Integer, Integer> getCastleKingSquares(int color, int side) {
        return castleKingSquares.get(color).get(side);
    }


    /**
     * @param color of player
     * @param side to castle (KING/QUEEN)
     * @return bitboard, with squares king needs to pass trough to castle
     */
    public long getCastleSquareMask(int color, int side) {
        return castleSquareMasks.get(color).get(side);
    }

    /**
     * @param color of player
     * @param side to castle (KING/QUEEN)
     * @return Pair, where key is original position before castle, value 
     * is position after castling for rook
     */
    public Pair<Integer, Integer> getCastleRookSquares(int color, int side) {
        return castleRookSquares.get(color).get(side);
    }

    public int getCorrectKingPos(int color) {
        return correctKingPos[color];
    }

    public int getCorrectRookPos(int color, int side) {
        return castleRookSquares.get(color).get(side).getKey();
    }

    public boolean[][] getFullCastleRights() {
        boolean[][] temp = new boolean[][] {
            // White Color
            {true, true}, // King, Queen sides
            // Black Color
            {true,true}   // King, Queen sides
        };
        return temp;
    }

    public long[][] getCastleMask() {
        return castleMask;
    }
    
    
}
