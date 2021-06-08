package board;
//https://www.chessprogramming.org/Bitboards
//https://www.chessprogramming.org/BitScan
//https://www.chessprogramming.org/Rays
//https://www.chessprogramming.org/Efficient_Generation_of_Sliding_Piece_Attacks
//https://peterellisjones.com/posts/generating-legal-chess-moves-efficiently/
//https://rhysre.net/2019/01/15/magic-bitboards.html (text before "Magic Introduction")

import java.util.EnumMap;
import java.util.Map;

import board.constants.AntiDiagonals;
import board.constants.Colors;
import board.constants.Diagonals;
import board.constants.Directions;
import board.constants.Files;
import board.constants.Size;

public class Rays {
    private final EnumMap<Directions, long[]> slidingMoves;
    private final EnumMap<Colors, long[]> pawnMoves;
    private final EnumMap<Directions, Boolean> withMask;
    private final long[] knightMoves;
    private final long[] kingMoves;
    
    private final int BITS_OFFSET = 63;
    
    public Rays(Holder holder) {
        this.withMask = new EnumMap<Directions, Boolean>(Map.of(
            Directions.SOUTH,      false,
            Directions.NORTH,      true,
            Directions.EAST,       false,
            Directions.WEST,       true,
            Directions.SOUTH_WEST, false,
            Directions.SOUTH_EAST, false,
            Directions.NORTH_WEST, true,
            Directions.NORTH_EAST, true
        ));

        this.slidingMoves = new EnumMap<Directions, long[]>(Map.of(
            Directions.SOUTH,      new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.NORTH,      new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.EAST,       new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.WEST,       new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.SOUTH_WEST, new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.SOUTH_EAST, new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.NORTH_WEST, new long[holder.getSize(Size.BOARD_SIZE)],
            Directions.NORTH_EAST, new long[holder.getSize(Size.BOARD_SIZE)]
        ));

        this.pawnMoves = new EnumMap<Colors, long[]>(Map.of(
            Colors.WHITE, new long[holder.getSize(Size.BOARD_SIZE)],
            Colors.BLACK, new long[holder.getSize(Size.BOARD_SIZE)]
        ));
        
        this.kingMoves = new long[holder.getSize(Size.BOARD_SIZE)];
        this.knightMoves = new long[holder.getSize(Size.BOARD_SIZE)];
        generateMoves(holder);
    }

    private void generateMoves(Holder holder) {
        long attackBb;
        long whitePawnAttack;
        long blackPawnAttack;
        for (int square = 0; square < holder.getSize(Size.BOARD_SIZE); square++) {
            long start = Holder.ONE << square;
            // Knight
            attackBb = ((((start << 15) | (start >>> 17)) & ~holder.getFile(Files.FILE_H)) | // Left 1
                (((start >>> 15) | (start << 17)) & ~holder.getFile(Files.FILE_A)) | // Right 1
                (((start << 6) | (start >>> 10)) & ~(holder.getFile(Files.FILE_G) | holder.getFile(Files.FILE_H))) | // Left 2
                (((start >>> 6) | (start << 10)) & ~(holder.getFile(Files.FILE_A) | holder.getFile(Files.FILE_B)))); // Right 2
        
            knightMoves[square] = attackBb;
            // King
            attackBb = ((((start << 7) | (start >>> 9) | (start >>> 1)) & (~holder.getFile(Files.FILE_H))) | // top 3 moves (removes if king is on top)
            (((start << 9) | (start >>> 7) | (start << 1)) & (~holder.getFile(Files.FILE_A))) | // bottom 3 moves (removes if king is on bottom)
            ((start >>> 8) | (start << 8))); // middle 2 moves, removed on either FILE_A / FILE_H
            kingMoves[square] = attackBb;
            // Pawn
            blackPawnAttack = ((start << 9) & ~holder.getFile(Files.FILE_A)) | ((start << 7) & ~holder.getFile(Files.FILE_H)); // left, right
            whitePawnAttack = ((start >>> 9) & ~holder.getFile(Files.FILE_H)) | ((start >>> 7) & ~holder.getFile(Files.FILE_A)); // left, right
            pawnMoves.get(Colors.WHITE)[square] = whitePawnAttack;
            pawnMoves.get(Colors.BLACK)[square] = blackPawnAttack;
            // Sliding (Queen, rooks, bishop).
            // Orthogonal
            slidingMoves.get(Directions.SOUTH)[square] = popFirst(holder.getFile(Files.FILE_A)) << square;
            slidingMoves.get(Directions.NORTH)[square] =  popLast(holder.getFile(Files.FILE_H)) >>> (BITS_OFFSET - square);
            slidingMoves.get(Directions.EAST)[square] = 2 * ((Holder.ONE << (square | 7)) - (Holder.ONE << square));
            slidingMoves.get(Directions.WEST)[square] = (Holder.ONE << square) - (Holder.ONE << (square & 56)); 
            // Diagonal
            slidingMoves.get(Directions.SOUTH_WEST)[square] = westN(popFirst(holder.getAntiDiag(AntiDiagonals.DIAG_7)), 
                7 - col(square), holder.getFile(Files.FILE_H)) << (row(square) * 8);
            slidingMoves.get(Directions.SOUTH_EAST)[square] = eastN(popFirst(holder.getDiag(Diagonals.DIAG_7)),
                col(square), holder.getFile(Files.FILE_A)) << (row(square) * 8);
            slidingMoves.get(Directions.NORTH_WEST)[square] = westN(popLast(holder.getDiag(Diagonals.DIAG_7)),
                7 - col(square), holder.getFile(Files.FILE_H)) >>> ((7 - row(square)) * 8);
            slidingMoves.get(Directions.NORTH_EAST)[square] = eastN(popLast(holder.getAntiDiag(AntiDiagonals.DIAG_7)),
                col(square), holder.getFile(Files.FILE_A)) >>> ((7 - row(square)) * 8);
        }
    }

    private final long popFirst(long num) {
        return (num & (num-1));
    }

    private final long popLast(long num){
        return (num ^ Long.highestOneBit(num));
    }

    /**
     * @return x
     */
    private int row(int square) {
        return square / 8;
    }

    /**
     * @return y
     */
    private int col(int square) {
        return square % 8;
    }

    private long eastN(long board, int n, long file) {
        long newBoard = board;
        for (int i = 0; i < n; i++) {
            newBoard = ((newBoard << 1) & (~file));
        }
        return newBoard;
    }

    private long westN(long board, int n, long file) {
        long newBoard = board;
        for (int i = 0; i < n; i++) {
            newBoard = ((newBoard >>> 1) & (~file));
        }
        return newBoard;
    }

    // ------------ MOVES ------------ 

    public long getRay(Directions dir, int square) {
        return slidingMoves.get(dir)[square];
    }

    public long getKnight(int square){
        return knightMoves[square];
    }

    public long getKing(int square){
        return kingMoves[square];
    }

    public long getPawn(Colors color, int square){
        return pawnMoves.get(color)[square];
    }

    // ------------ DIRECTIONS ------------ 

    public long getDirection(Directions direction, int square, long blockers) {
        long tmp = slidingMoves.get(direction)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = ((withMask.get(direction)) ? 
                (BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers)) : 
                Long.numberOfTrailingZeros(tmp & blockers));
            tmp &= ~slidingMoves.get(direction)[blockerIndex];
        }
        return tmp;
    }
    /*
    private long South(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.SOUTH)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.SOUTH)[blockerIndex];
        }
        return tmp;
    }

    private long North(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.NORTH)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.NORTH)[blockerIndex];
        }
        return tmp;
    }

    private long East(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.EAST)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.EAST)[blockerIndex];
        }
        return tmp;
    }

    private long West(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.WEST)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.WEST)[blockerIndex];
        }
        return tmp;
    }

    private long southWest(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.SOUTH_WEST)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.SOUTH_WEST)[blockerIndex];
        }
        return tmp;
    }

    private long southEast(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.SOUTH_EAST)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.SOUTH_EAST)[blockerIndex];
        }
        return tmp;
    }

    private long northWest(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.NORTH_WEST)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.NORTH_WEST)[blockerIndex];
        }
        return tmp;
        
    }

    private long northEast(int square, long blockers) {
        long tmp = slidingMoves.get(Directions.NORTH_EAST)[square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves.get(Directions.NORTH_EAST)[blockerIndex];
        }
        return tmp;
    }
    */
}
