package board;

/**
 * Class containing piece, its color, square on board, moves
 */
public class Spot {
    private final int piece;
    private final int color;
    private final int square;
    private boolean hasMoved;
    private long moves;
    private long attacks;

    public Spot(int piece, int color, int square, boolean hasMoved) {
        this.piece = piece;
        this.color = color;
        this.square = square;
        this.hasMoved = hasMoved;
    }
    
    /**
     * @param path by which moves get modified, meaning
     * all moves must be on path
     */
    public void modifyMoves(long path) {
        moves &= path;
    }

    /**
     * @param moves to be assigned
     */
    public void setMoves(long moves) {
        this.moves = moves;
    }

    /**
     * @param attacks to be assigned
     */
    public void setAttacks(long attacks) {
        this.attacks = attacks;
    }

    /**
     * @return the color of piece
     */
    public int getColor() {
        return color;
    }

    /**
     * @return legal moves
     */
    public long getMoves() {
        return moves;
    }

    /**
     * @return the piece
     */
    public int getPiece() {
        return piece;
    }

    /**
     * @return true if pieces has moved, false otherwise
     */
    public boolean getHasMoved() {
        return hasMoved;
    }
    
    /**
     * @return pieces that are under attack by piece on this spot
     */
    public long getAttacks() {
        return attacks;
    }

    /**
     * @return the square on board
     */
    public int getSquare() {
        return square;
    }
}
