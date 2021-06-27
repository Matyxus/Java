package board;

import board.constants.AntiDiagonals;
import board.constants.Colors;
import board.constants.Diagonals;
import board.constants.Directions;
import board.constants.Files;
import board.constants.Size;

// https://www.chessprogramming.org/Bitboards
// https://www.chessprogramming.org/BitScan
// https://www.chessprogramming.org/Rays
// https://www.chessprogramming.org/Efficient_Generation_of_Sliding_Piece_Attacks
// https://peterellisjones.com/posts/generating-legal-chess-moves-efficiently/
// https://rhysre.net/2019/01/15/magic-bitboards.html (text before "Magic Introduction")

/**
 * Class holding legal moves of pieces (on empty board) in arrays, indexed
 * by their position on board
 */
public class Rays {
    private final long[][] slidingMoves;
    private final long[] knightMoves;
    private final long[] kingMoves;
    private final long[][] pawnMoves;
    private final int BITS_OFFSET = 63;
    private final boolean[] withMask = {false, true, false, true, false, false, true, true};
    
    public Rays(){ // acts as init, only accesible in Pieces
        this.slidingMoves = new long[Directions.DIRECTION_COUNT][Size.BOARD_SIZE];
        this.kingMoves = new long[Size.BOARD_SIZE];
        this.knightMoves = new long[Size.BOARD_SIZE];
        this.pawnMoves = new long[2][Size.BOARD_SIZE];
        long attackBb;
        long whitePawnAttack;
        long blackPawnAttack;
        for (int square = 0; square < Size.BOARD_SIZE; square++) {
            long start = Size.ONE << square;
            // ------------------ Knight ------------------
            attackBb = ((((start << 15) | (start >>> 17)) & ~Files.FILE_H) | // Left 1
                (((start >>> 15) | (start << 17)) & ~Files.FILE_A) | // Right 1
                (((start << 6) | (start >>> 10)) & ~(Files.FILE_G | Files.FILE_H)) | // Left 2
                (((start >>> 6) | (start << 10)) & ~(Files.FILE_A | Files.FILE_B))); // Right 2
        
            knightMoves[square] = attackBb;
            // ------------------ King ------------------
            attackBb = ((((start << 7) | (start >>> 9) | (start >>> 1)) & (~Files.FILE_H)) | // top 3 moves (removes if king is on top)
            (((start << 9) | (start >>> 7) | (start << 1)) & (~Files.FILE_A)) | // bottom 3 moves (removes if king is on bottom)
            ((start >>> 8) | (start << 8))); // middle 2 moves, removed on either FILE_A / FILE_H
            kingMoves[square] = attackBb;
            // ------------------ Pawns ------------------
            blackPawnAttack = ((start << 9) & ~Files.FILE_A) | ((start << 7) & ~Files.FILE_H); // left, right
            whitePawnAttack = ((start >>> 9) & ~Files.FILE_H) | ((start >>> 7) & ~Files.FILE_A); // left, right
            pawnMoves[Colors.WHITE][square] = whitePawnAttack;
            pawnMoves[Colors.BLACK][square] = blackPawnAttack;
            // ------------------ Rook, Bishop ------------------
            // South
            slidingMoves[Directions.SOUTH][square] = popFirst(Files.FILE_A) << square;
            // North
            slidingMoves[Directions.NORTH][square] =  popLast(Files.FILE_H) >>> (BITS_OFFSET - square);
            // East
            slidingMoves[Directions.EAST][square] = 2 * ((Size.ONE << (square | 7)) - (Size.ONE << square));
            // West
            slidingMoves[Directions.WEST][square] = (Size.ONE << square) - (Size.ONE << (square & 56)); 
            // South West
            slidingMoves[Directions.SOUTH_WEST][square] = westN(popFirst(AntiDiagonals.DIAG_7), 7 - col(square)) << (row(square) * 8);
            // South East
            slidingMoves[Directions.SOUTH_EAST][square] = eastN(popFirst(Diagonals.DIAG_7), col(square)) << (row(square) * 8);
            // North West
            slidingMoves[Directions.NORTH_WEST][square] = westN(popLast(Diagonals.DIAG_7), 7 - col(square)) >>> ((7 - row(square)) * 8);
            // North East
            slidingMoves[Directions.NORTH_EAST][square] = eastN(popLast(AntiDiagonals.DIAG_7), col(square)) >>> ((7 - row(square)) * 8);
        }
    }

    /**
     * @param num
     * @return number without first bit
     */
    private final long popFirst(long num) {
        return (num & (num-1));
    }

    /**
     * @param num
     * @return number without last bit
     */
    private final long popLast(long num){
        return (num ^ Long.highestOneBit(num));
    }

    /**
     * @param square on board
     * @return y coordinate
     */
    private final int row(int square) {
        return square / Size.COLS;
    }

    /**
     * @param square on board
     * @return x coordinate
     */
    private final int col(int square) {
        return square % Size.ROWS;
    }

    /**
     * @param board
     * @param n
     * @return ray in diagonal direction
     */
    private long eastN(long board, int n) {
        long newBoard = board;
        for (int i = 0; i < n; i++) {
            newBoard = ((newBoard << 1) & (~Files.FILE_A));
        }
        return newBoard;
    }

    /**
     * @param board
     * @param n
     * @return ray in diagonal direction
     */
    private long westN(long board, int n) {
        long newBoard = board;
        for (int i = 0; i < n; i++) {
            newBoard = ((newBoard >>> 1) & (~Files.FILE_H));
        }
        return newBoard;
    }

    /**
     * @param dir direction
     * @param square on board
     * @return ray in given direction from given square on empty board
     */
    public long getRay(int dir, int square) {
        return slidingMoves[dir][square];
    }

    /**
     * @param square on board
     * @return moves of knight on given square on empty board
     */
    public long getKnight(int square) {
        return knightMoves[square];
    }

    /**
     * @param square on board
     * @return moves of king on given square on empty board
     */
    public long getKing(int square) {
        return kingMoves[square];
    }

    /**
     * @param square on board
     * @return attacks of pawn on given square on empty board
     */
    public long getPawn(int square, int color) {
        return pawnMoves[color][square];
    }

    // ------------------ Directions ------------------
    
    /**
     * @param dir direction
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in given direction, stopped by first piece on its path
     */
    public long getDirection(int dir, int square, long blockers) {
        long tmp = slidingMoves[dir][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = ((withMask[dir]) ? 
                BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers) :
                Long.numberOfTrailingZeros(tmp & blockers)
            );
            tmp &= ~slidingMoves[dir][blockerIndex];
        }
        return tmp;
    }
    
    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in south direction, stopped by first piece on its path
     */
    public long South(int square, long blockers) {
        long tmp = slidingMoves[Directions.SOUTH][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.SOUTH][blockerIndex];
        }
        return tmp;
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in north direction, stopped by first piece on its path
     */
    public long North(int square, long blockers) {
        long tmp = slidingMoves[Directions.NORTH][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.NORTH][blockerIndex];
        }
        return tmp;
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in east direction, stopped by first piece on its path
     */
    public long East(int square, long blockers) {
        long tmp = slidingMoves[Directions.EAST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.EAST][blockerIndex];
        }
        return tmp;
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in west direction, stopped by first piece on its path
     */
    public long West(int square, long blockers) {
        long tmp = slidingMoves[Directions.WEST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.WEST][blockerIndex];
        }
        return tmp;
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in south west direction, stopped by first piece on its path
     */
    public long southWest(int square, long blockers) {
        long tmp = slidingMoves[Directions.SOUTH_WEST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.SOUTH_WEST][blockerIndex];
        }
        return tmp;
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in south east direction, stopped by first piece on its path
     */
    public long southEast(int square, long blockers) {
        long tmp = slidingMoves[Directions.SOUTH_EAST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.SOUTH_EAST][blockerIndex];
        }
        return tmp;
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in north west direction, stopped by first piece on its path
     */
    public long northWest(int square, long blockers) {
        long tmp = slidingMoves[Directions.NORTH_WEST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.NORTH_WEST][blockerIndex];
        }
        return tmp;
        
    }

    /**
     * @param square on board
     * @param blockers all pieces on board
     * @return ray in north east direction, stopped by first piece on its path
     */
    public long northEast(int square, long blockers) {
        long tmp = slidingMoves[Directions.NORTH_EAST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Directions.NORTH_EAST][blockerIndex];
        }
        return tmp;
    }
    
}
