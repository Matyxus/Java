package board.constants;

public class Flags {
    public static final int CASTLE = 128;
    public static final int QUEEN_CASTLE = CASTLE | Pieces.QUEEN;
    // King is 0 -> has same value as CASTLE
    public static final int KING_CASTLE = CASTLE | Pieces.KING;
    public static final int CAPTURE = 256;
    public static final int PROMOTION = 512;
    public static final int EN_PASSANT = 1024;
    public static final int MOVE = 0;
}
