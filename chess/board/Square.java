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

    public boolean getHasMoved() {
        return hasMoved;
    }
    
    public void modifyMoves(long path){
        moves &= path;
    }

    public void setAttacks(long attacks) {
        this.attacks = attacks;
    }
}
