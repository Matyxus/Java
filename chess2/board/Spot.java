package chess2.board;

public class Spot {
    private int piece;
    private int color;
    private boolean hasMoved;
    private long moves;
    private long attacks;

    public Spot(int piece, int color, boolean hasMoved){
        this.piece = piece;
        this.color = color;
        this.hasMoved = hasMoved;
    }
    
    /**
     * @return the color, 0=white, 1=black
     */
    public int getColor() {
        return color;
    }

    public long getMoves() {
        return moves;
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

    public long getAttacks() {
        return attacks;
    }

    public void setAttacks(long attacks) {
        this.attacks = attacks;
    }
}
