package board.constants;
/**
 * Class holding values of pieces
 */
public class Pieces {
    public static final int KING        = 0;
    public static final int QUEEN       = 1;
    public static final int ROOK        = 2;
    public static final int KNIGHT      = 3;
    public static final int BISHOP      = 4;
    public static final int PAWN        = 5;
    public static final int PIECE_COUNT = 6;

    public static final int[] getPieces() {
        return new int[] {KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN};
    }
}
