package gameboard.constants;

/**
 * Class holding variables for flags related to special pieces movement
 */
public class Flags {
    public static final int MOVE = 0;
    public static final int CASTLE = 128;
    // King is 0 -> has same value as CASTLE
    public static final int KING_CASTLE = CASTLE | Pieces.KING;
    public static final int QUEEN_CASTLE = CASTLE | Pieces.QUEEN;
    public static final int CAPTURE = 256;
    public static final int PROMOTION = 512;
    public static final int EN_PASSANT = 1024;
}
