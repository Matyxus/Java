package chess2.board;
// class that holds constants
public class Holder {
    public static final int BOARD_SIZE = 64;
    public static final int ROWS = 8; // == cols
    static final long ONE = 0b1L;
    static final long ZERO = 0b0L;
    // Pieces
    public static final int KING = 0;
    public static final int QUEEN = 1;
    public static final int ROOK = 2;
    public static final int KNIGHT = 3;
    public static final int BISHOP = 4;
    public static final int PAWN = 5;
    //Directions
    public static final int SOUTH = 0;
    public static final int NORTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int SOUTH_WEST = 4;
    public static final int SOUTH_EAST = 5;
    public static final int NORTH_WEST = 6;
    public static final int NORTH_EAST = 7;
    public static final int[] ORTHOGONAL = {SOUTH, NORTH, EAST, WEST};
    public static final int[] DIAGONAL = {SOUTH_WEST, SOUTH_EAST, NORTH_WEST, NORTH_EAST};
    //Files
    public static final long FILE_H = 0b1000000010000000100000001000000010000000100000001000000010000000L;
    public static final long FILE_G = 0b0100000001000000010000000100000001000000010000000100000001000000L;
    public static final long FILE_F = 0b0010000000100000001000000010000000100000001000000010000000100000L;
    public static final long FILE_E = 0b0001000000010000000100000001000000010000000100000001000000010000L;
    public static final long FILE_D = 0b0000100000001000000010000000100000001000000010000000100000001000L;
    public static final long FILE_C = 0b0000010000000100000001000000010000000100000001000000010000000100L;
    public static final long FILE_B = 0b0000001000000010000000100000001000000010000000100000001000000010L;
    public static final long FILE_A = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    public static final long[] FILES = {FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};
    //Ranks
    public static final long RANK_1 = 0b0000000000000000000000000000000000000000000000000000000011111111L;
    public static final long RANK_2 = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    public static final long RANK_3 = 0b0000000000000000000000000000000000000000111111110000000000000000L;
    public static final long RANK_4 = 0b0000000000000000000000000000000011111111000000000000000000000000L;
    public static final long RANK_5 = 0b0000000000000000000000001111111100000000000000000000000000000000L;
    public static final long RANK_6 = 0b0000000000000000111111110000000000000000000000000000000000000000L;
    public static final long RANK_7 = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    public static final long RANK_8 = 0b1111111100000000000000000000000000000000000000000000000000000000L;
    public static final long[] RANKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};
    //Diags - Only used in Rays
    static final long LEFT_DIAG =  0b0000000100000010000001000000100000010000001000000100000010000000L;
    static final long RIGHT_DIAG = 0b1000000001000000001000000001000000001000000001000000001000000001L;
    //Colors
    public static final int WHITE = 0;
    public static final int BLACK = 1;
}

