package board;

public class Square {
    private final int piece;
    private final int color;

    private boolean hasMoved = false;
    private long moves;
    private long attacks;

    public Square(int piece, int color){
        this.piece = piece;
        this.color = color;
    }
    
    /**
     * @return the color of piece
     */
    public int getColor() {
        return color;
    }

    /**
     * @return 
     * current legal moves of piece
     */
    public long getMoves() {
        return moves;
    }

    /**
     * @return 
     * position of pieces attack by this piece
     */
    public long getAttacks() {
        return attacks;
    }
    
    /**
     * 
     * @param moves
     * Sets new moves for this piece.
     */
    public void setMoves(long moves) {
        this.moves = moves;
    }

    /**
     * @return the piece
     * 0:King, 1:Queen, 2:Rook, 3:Knight, 4:Bishop, 5:Pawn
     */
    public int getPiece() {
        return piece;
    }

    /**
     * 
     * @return true if piece has moved, false otherwise.
     */
    public boolean getHasMoved() {
        return hasMoved;
    }
    
    /**
     * 
     * @param path modifies current moves, so that
     * they are only on path.
     */
    public void modifyMoves(long path){
        moves &= path;
    }

    /**
     * 
     * @param attacks
     * Sets new attacks for this piece.
     */
    public void setAttacks(long attacks) {
        this.attacks = attacks;
    }
}
