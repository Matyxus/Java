package chess2.board;

public class Struct {
    long movementMask; // to mask relevant squares of both lines (no outer squares)
	long magic; // magic 64-bit factor
	int shift; // shift right
	long[] magicMoves; // pointer to attack_table for each particular square
	
    public Struct(long magic) {
		this.magic = magic;
	}
}