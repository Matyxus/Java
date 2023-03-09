package gameboard.constants.bitboard;
/**
 * Class holding bitboard representation of ranks on chess board
 */
public class Ranks  {
    public static final long RANK_1 = 0b0000000000000000000000000000000000000000000000000000000011111111L;
    public static final long RANK_2 = 0b0000000000000000000000000000000000000000000000001111111100000000L;
    public static final long RANK_3 = 0b0000000000000000000000000000000000000000111111110000000000000000L;
    public static final long RANK_4 = 0b0000000000000000000000000000000011111111000000000000000000000000L;
    public static final long RANK_5 = 0b0000000000000000000000001111111100000000000000000000000000000000L;
    public static final long RANK_6 = 0b0000000000000000111111110000000000000000000000000000000000000000L;
    public static final long RANK_7 = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    public static final long RANK_8 = 0b1111111100000000000000000000000000000000000000000000000000000000L;
    public static final long[] ALL_RANKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    /**
     * Ranks for pawn promotion, indexed by color (e.g PROMOTION_RANKS[Colors.WHITE])
     */
    public static final long[] PROMOTION_RANKS = {RANK_1, RANK_8};

    /**
     * 
     * @param square on board
     * @return row of given square
     */
    public static final int getRow(int square) {
        return (square & 7);
    }
}
