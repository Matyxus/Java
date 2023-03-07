package gameboard.constants;
/**
 * Class holding constant associated with chess board
 */
public class Board {
    public static final int BOARD_SIZE = 64;
    public static final int ROWS = 8;
    public static final int COLS = 8;
    public static final long ONE = 0b1L;
    public static final long ZERO = 0b0L;
    /**
     * Square diffference for enpassant capture,
     * indexed by color
     */
    public static final int[] enpassantDiff = {ROWS, -ROWS};

    public static final String[] SQUARE_TO_ALGEBRAIC = {
        "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
        "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
        "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
        "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
        "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
        "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
        "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
        "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1" 
    };

    /**
     * @param square on board in String format (e.g. "a8")
     * @return square in an integer format
     */
    public static int algebraicToSquare(String square) {
        assert (square != null && square.length() == 2);
        return (square.charAt(0)-'a')+ROWS*(ROWS-Character.getNumericValue(square.charAt(1)));
    }
}