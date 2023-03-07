package gameboard.constants.bitboard;

/**
 * Class holding bitboard representation of Files on chess board
 */
public class Files {
    public static final long FILE_A = 0b0000000100000001000000010000000100000001000000010000000100000001L;
    public static final long FILE_B = 0b0000001000000010000000100000001000000010000000100000001000000010L;
    public static final long FILE_C = 0b0000010000000100000001000000010000000100000001000000010000000100L;
    public static final long FILE_D = 0b0000100000001000000010000000100000001000000010000000100000001000L;
    public static final long FILE_E = 0b0001000000010000000100000001000000010000000100000001000000010000L;
    public static final long FILE_F = 0b0010000000100000001000000010000000100000001000000010000000100000L;
    public static final long FILE_G = 0b0100000001000000010000000100000001000000010000000100000001000000L;
    public static final long FILE_H = 0b1000000010000000100000001000000010000000100000001000000010000000L;
    public static final long[] ALL_FILES = {FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};

    /**
     * @param square on board
     * @return column of given square
     */
    public static final int getColumn(int square) {
        return (square >>> 3);
    }
}
