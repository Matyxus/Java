package gameboard.constants.bitboard;
/**
 * Class holding values of directions
 */
public class Directions {
    public static final int SOUTH           = 0;
    public static final int NORTH           = 1;
    public static final int EAST            = 2;
    public static final int WEST            = 3;
    public static final int SOUTH_WEST      = 4;
    public static final int SOUTH_EAST      = 5;
    public static final int NORTH_WEST      = 6;
    public static final int NORTH_EAST      = 7;
    public static final int DIRECTION_COUNT = 8;
    public static final int[] ORTHOGONAL    = {SOUTH, NORTH, EAST, WEST};
    public static final int[] DIAGONAL      = {SOUTH_WEST, SOUTH_EAST, NORTH_WEST, NORTH_EAST};
}
