package gameboard.move_gen.magic;
/**
 * Class holding hashed moves for Rooks/Bishops and unhashing values
 */
public class Struct {
    final long movementMask; // attacks of Rook/Bishop on empty board on certain sqaure (without edges)
	final long magic; 		 // magic 64-bit factor
	final int shift; 		 // shift right
	long[] magicMoves;		 // array of hashed moves
	
    public Struct(long magic, int shift, long movementMask) {
		this.magic = magic;
		this.shift = shift;
		this.movementMask = movementMask;
	}
}