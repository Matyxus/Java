package chess2.board;
//https://www.chessprogramming.org/Bitboards
//https://www.chessprogramming.org/BitScan
//https://www.chessprogramming.org/Rays
//https://www.chessprogramming.org/Efficient_Generation_of_Sliding_Piece_Attacks
//https://peterellisjones.com/posts/generating-legal-chess-moves-efficiently/
//https://rhysre.net/2019/01/15/magic-bitboards.html (text before "Magic Introduction")
public class Rays {
    private final long[][] slidingMoves;
    private final long[] knightMoves;
    private final long[] kingMoves;
    private final long[][] pawnMoves;
    private final int BITS_OFFSET = 63;
    
    public Rays(){ // acts as init, only accesible in Pieces
        this.slidingMoves = new long[8][Holder.BOARD_SIZE];
        this.kingMoves = new long[Holder.BOARD_SIZE];
        this.knightMoves = new long[Holder.BOARD_SIZE];
        this.pawnMoves = new long[2][Holder.BOARD_SIZE];
        long attackBb;
        long whitePawnAttack;
        long blackPawnAttack;
        for (int square = 0; square < Holder.BOARD_SIZE; square++) {
            long start = Holder.ONE << square;
            // Knight
            attackBb = ((((start << 15) | (start >>> 17)) & ~Holder.FILE_H) | // Left 1
                (((start >>> 15) | (start << 17)) & ~Holder.FILE_A) | // Right 1
                (((start << 6) | (start >>> 10)) & ~(Holder.FILE_G | Holder.FILE_H)) | // Left 2
                (((start >>> 6) | (start << 10)) & ~(Holder.FILE_A | Holder.FILE_B))); // Right 2
        
            knightMoves[square] = attackBb;
            // King
            attackBb = ((((start << 7) | (start >>> 9) | (start >>> 1)) & (~Holder.FILE_H)) | // top 3 moves (removes if king is on top)
            (((start << 9) | (start >>> 7) | (start << 1)) & (~Holder.FILE_A)) | // bottom 3 moves (removes if king is on bottom)
            ((start >>> 8) | (start << 8))); // middle 2 moves, removed on either FILE_A / FILE_H
            kingMoves[square] = attackBb;
            // Pawn
            blackPawnAttack = ((start << 9) & ~Holder.FILE_A) | ((start << 7) & ~Holder.FILE_H); // left, right
            whitePawnAttack = ((start >>> 9) & ~Holder.FILE_H) | ((start >>> 7) & ~Holder.FILE_A); // left, right
            pawnMoves[Holder.WHITE][square] = whitePawnAttack;
            pawnMoves[Holder.BLACK][square] = blackPawnAttack;
            // South
            slidingMoves[Holder.SOUTH][square] = popFirst(Holder.FILE_A) << square;
            // North
            slidingMoves[Holder.NORTH][square] =  popLast(Holder.FILE_H) >>> (BITS_OFFSET - square);
            // East
            slidingMoves[Holder.EAST][square] = 2 * ((Holder.ONE << (square | 7)) - (Holder.ONE << square));
            // West
            slidingMoves[Holder.WEST][square] = (Holder.ONE << square) - (Holder.ONE << (square & 56)); 
            // South West
            slidingMoves[Holder.SOUTH_WEST][square] = westN(popFirst(Holder.LEFT_DIAG), 7 - col(square)) << (row(square) * 8);
            // South East
            slidingMoves[Holder.SOUTH_EAST][square] = eastN(popFirst(Holder.RIGHT_DIAG), col(square)) << (row(square) * 8);
            // North West
            slidingMoves[Holder.NORTH_WEST][square] = westN(popLast(Holder.RIGHT_DIAG), 7 - col(square)) >>> ((7 - row(square)) * 8);
            // North East
            slidingMoves[Holder.NORTH_EAST][square] = eastN(popLast(Holder.LEFT_DIAG), col(square)) >>> ((7 - row(square)) * 8);
        }
    }

    private final long popFirst(long num) {
        return (num & (num-1));
    }

    private final long popLast(long num){
        return (num ^ Long.highestOneBit(num));
    }

    /**
     * @return y
     */
    private int row(int square) {
        return square / 8;
    }

    /**
     * @return x
     */
    private int col(int square) {
        return square % 8;
    }

    private long eastN(long board, int n) {
        long newBoard = board;
        for (int i = 0; i < n; i++) {
            newBoard = ((newBoard << 1) & (~Holder.FILE_A));
        }
        return newBoard;
    }

    private long westN(long board, int n) {
        long newBoard = board;
        for (int i = 0; i < n; i++) {
            newBoard = ((newBoard >>> 1) & (~Holder.FILE_H));
        }
        return newBoard;
    }

    public long getRay(int dir, int square) {
        return this.slidingMoves[dir][square];
    }

    public long getKnight(int square){
        return knightMoves[square];
    }

    public long getKing(int square){
        return kingMoves[square];
    }

    public long getPawn(int square, int color){
        return this.pawnMoves[color][square];
    }
    //DIRECTIONS
    //numberOfTrailingZeros or numberOfLeadingZeros never returns 64, thanks to  if != 0
    public long South(int square, long blockers) {
        long tmp = slidingMoves[Holder.SOUTH][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.SOUTH][blockerIndex];
        }
        return tmp;
    }

    public long North(int square, long blockers) {
        long tmp = slidingMoves[Holder.NORTH][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.NORTH][blockerIndex];
        }
        return tmp;
    }

    public long East(int square, long blockers) {
        long tmp = slidingMoves[Holder.EAST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.EAST][blockerIndex];
        }
        return tmp;
    }

    public long West(int square, long blockers) {
        long tmp = slidingMoves[Holder.WEST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.WEST][blockerIndex];
        }
        return tmp;
    }

    public long southWest(int square, long blockers) {
        long tmp = slidingMoves[Holder.SOUTH_WEST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.SOUTH_WEST][blockerIndex];
        }
        return tmp;
    }

    public long southEast(int square, long blockers) {
        long tmp = slidingMoves[Holder.SOUTH_EAST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = Long.numberOfTrailingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.SOUTH_EAST][blockerIndex];
        }
        return tmp;
    }

    public long northWest(int square, long blockers) {
        long tmp = slidingMoves[Holder.NORTH_WEST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.NORTH_WEST][blockerIndex];
        }
        return tmp;
        
    }

    public long northEast(int square, long blockers) {
        long tmp = slidingMoves[Holder.NORTH_EAST][square];
        if ((tmp & blockers) != 0) {
            int blockerIndex = BITS_OFFSET-Long.numberOfLeadingZeros(tmp & blockers);
            tmp &= ~slidingMoves[Holder.NORTH_EAST][blockerIndex];
        }
        return tmp;
    }
}
