package board;

import java.util.EnumMap;
import java.util.Map;
import board.constants.*;

// class that holds constants
public class Holder {

    private final EnumMap<Size, Integer> sizeMap = new EnumMap<Size, Integer>(Map.of(
        Size.BOARD_SIZE, 64,
        Size.ROWS, 8,
        Size.COLS, 8
    ));

    private final EnumMap<AntiDiagonals, Long> antiDiagMap = new EnumMap<AntiDiagonals, Long>(Map.ofEntries(
        Map.entry(AntiDiagonals.DIAG_0,  0b1000000000000000000000000000000000000000000000000000000000000000L),
        Map.entry(AntiDiagonals.DIAG_1,  0b0100000010000000000000000000000000000000000000000000000000000000L),
        Map.entry(AntiDiagonals.DIAG_2,  0b0010000001000000100000000000000000000000000000000000000000000000L),
        Map.entry(AntiDiagonals.DIAG_3,  0b0001000000100000010000001000000000000000000000000000000000000000L),
        Map.entry(AntiDiagonals.DIAG_4,  0b0000100000010000001000000100000010000000000000000000000000000000L),
        Map.entry(AntiDiagonals.DIAG_5,  0b0000010000001000000100000010000001000000100000000000000000000000L),
        Map.entry(AntiDiagonals.DIAG_6,  0b0000001000000100000010000001000000100000010000001000000000000000L),
        Map.entry(AntiDiagonals.DIAG_7,  0b0000000100000010000001000000100000010000001000000100000010000000L),
        Map.entry(AntiDiagonals.DIAG_8,  0b0000000000000001000000100000010000001000000100000010000001000000L),
        Map.entry(AntiDiagonals.DIAG_9,  0b0000000000000000000000010000001000000100000010000001000000100000L),
        Map.entry(AntiDiagonals.DIAG_10, 0b0000000000000000000000000000000100000010000001000000100000010000L),
        Map.entry(AntiDiagonals.DIAG_11, 0b0000000000000000000000000000000000000001000000100000010000001000L),
        Map.entry(AntiDiagonals.DIAG_12, 0b0000000000000000000000000000000000000000000000010000001000000100L),
        Map.entry(AntiDiagonals.DIAG_13, 0b0000000000000000000000000000000000000000000000000000000100000010L),
        Map.entry(AntiDiagonals.DIAG_14, 0b0000000000000000000000000000000000000000000000000000000000000001L)
    ));

    private final EnumMap<Diagonals, Long> diagMap = new EnumMap<Diagonals, Long>(Map.ofEntries(
        Map.entry(Diagonals.DIAG_0,  0b0000000100000000000000000000000000000000000000000000000000000000L),
        Map.entry(Diagonals.DIAG_1,  0b0000001000000001000000000000000000000000000000000000000000000000L),
        Map.entry(Diagonals.DIAG_2,  0b0000010000000010000000010000000000000000000000000000000000000000L),
        Map.entry(Diagonals.DIAG_3,  0b0000100000000100000000100000000100000000000000000000000000000000L),
        Map.entry(Diagonals.DIAG_4,  0b0001000000001000000001000000001000000001000000000000000000000000L),
        Map.entry(Diagonals.DIAG_5,  0b0010000000010000000010000000010000000010000000010000000000000000L),
        Map.entry(Diagonals.DIAG_6,  0b0100000000100000000100000000100000000100000000100000000100000000L),
        Map.entry(Diagonals.DIAG_7,  0b1000000001000000001000000001000000001000000001000000001000000001L),
        Map.entry(Diagonals.DIAG_8,  0b0000000010000000010000000010000000010000000010000000010000000010L),
        Map.entry(Diagonals.DIAG_9,  0b0000000000000000100000000100000000100000000100000000100000000100L),
        Map.entry(Diagonals.DIAG_10, 0b0000000000000000000000001000000001000000001000000001000000001000L),
        Map.entry(Diagonals.DIAG_11, 0b0000000000000000000000000000000010000000010000000010000000010000L),
        Map.entry(Diagonals.DIAG_12, 0b0000000000000000000000000000000000000000100000000100000000100000L),
        Map.entry(Diagonals.DIAG_13, 0b0000000000000000000000000000000000000000000000001000000001000000L),
        Map.entry(Diagonals.DIAG_14, 0b0000000000000000000000000000000000000000000000000000000010000000L)
    ));
    
    private final EnumMap<Files, Long> fileMap = new EnumMap<Files, Long>(Map.of(
        Files.FILE_A, 0b0000000100000001000000010000000100000001000000010000000100000001L,
        Files.FILE_B, 0b0000001000000010000000100000001000000010000000100000001000000010L,
        Files.FILE_C, 0b0000010000000100000001000000010000000100000001000000010000000100L,
        Files.FILE_D, 0b0000100000001000000010000000100000001000000010000000100000001000L,
        Files.FILE_E, 0b0001000000010000000100000001000000010000000100000001000000010000L,
        Files.FILE_F, 0b0010000000100000001000000010000000100000001000000010000000100000L,
        Files.FILE_G, 0b0100000001000000010000000100000001000000010000000100000001000000L,
        Files.FILE_H, 0b1000000010000000100000001000000010000000100000001000000010000000L
    ));
    
    private final EnumMap<Ranks, Long> rankMap = new EnumMap<Ranks, Long>(Map.of(
        Ranks.RANK_1, 0b0000000000000000000000000000000000000000000000000000000011111111L,
        Ranks.RANK_2, 0b0000000000000000000000000000000000000000000000001111111100000000L,
        Ranks.RANK_3, 0b0000000000000000000000000000000000000000111111110000000000000000L,
        Ranks.RANK_4, 0b0000000000000000000000000000000011111111000000000000000000000000L,
        Ranks.RANK_5, 0b0000000000000000000000001111111100000000000000000000000000000000L,
        Ranks.RANK_6, 0b0000000000000000111111110000000000000000000000000000000000000000L,
        Ranks.RANK_7, 0b0000000011111111000000000000000000000000000000000000000000000000L,
        Ranks.RANK_8, 0b1111111100000000000000000000000000000000000000000000000000000000L
    ));


    public Long getAntiDiag(AntiDiagonals key) {
        return antiDiagMap.get(key);
    }

    public Long getDiag(Diagonals key) {
        return diagMap.get(key);
    }

    public Long getRank(Ranks key) {
        return rankMap.get(key);
    }

    public Long getFile(Files key) {
        return fileMap.get(key);
    }

    public Integer getSize(Size key) {
        return sizeMap.get(key);
    }

    static final long ONE = 0b1L;
    static final long ZERO = 0b0L;
    
}

