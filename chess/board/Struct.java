package board;

public class Struct {
    final long movementMask; // to mask relevant squares of both lines (no outer squares)
	final long magic; // magic 64-bit factor
	final int shift; // shift right
	long[] magicMoves; // pointer to attack_table for each particular square
	
    public Struct(long magic, int shift, long movementMask) {
		this.magic = magic;
        this.shift = shift;
        this.movementMask = movementMask;
	}
}