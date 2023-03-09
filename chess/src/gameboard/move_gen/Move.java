package gameboard.move_gen;

import gameboard.constants.Flags;
import gameboard.constants.Board;

/**
 * Class represention movement of pieces on board
 */
public class Move {
    private final int fromSquare;
    private final int toSquare;

    /**
     * Flag hold what type of move it is (e.g. Flag.PROMOTION | Pieces.QUEEN),
     * for enpassant it contains square (e.g. Flags.EN_PASSANT | 32)
     */
    private final int flag;

    public Move(int fromSquare, int toSquare, int flag) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.flag = flag;
    }

    // ------------------------------ Movement types ------------------------------

    /**
     * @return true if this move is castling
     */
    public boolean isCastle() {
        return (flag & Flags.CASTLE) != 0;
    }

    /**
     * @return true if this move is
     * pawn promotion
     */
    public boolean isPromotion() {
        return (flag & Flags.PROMOTION) != 0;
    }

    /**
     * @return true if this move is capture,
     * false otherwise
     */
    public boolean isCapture() {
        return (flag & Flags.CAPTURE) != 0;
    }

    /**
     * @return true if this move is enpassant move,
     * false otherise
     */
    public boolean isEnpassant() {
        return (flag & Flags.EN_PASSANT) != 0;
    }

    // ------------------------------ Getters ------------------------------

    /**
     * @return original square of this move
     */
    public int getFromSquare() {
        return fromSquare;
    }

    /**
     * @return target square of this move
     */
    public int getToSquare() {
        return toSquare;
    }

    /**
     * @return flag of this move
     */
    public int getFlag() {
        return flag;
    }

    /**
     * @return side to which piece is castling (e.g. Pieces.KING)
     */
    public int getCastleSide() {
        return (flag ^ Flags.CASTLE);
    }

    /**
     * @return piece to which pawn will promote to 
     * (e.g. Pieces.QUEEN)
     */
    public int getPromotionPiece() {
        return (flag ^ Flags.PROMOTION ^ (flag & Flags.CAPTURE));
    }

    // ------------------------------ Utils ------------------------------

    @Override
    public boolean equals(Object other) {
        // Check for null and invalid instance
        if (other == null || !(other instanceof Move)) {
            return false;
        }
        final Move otherMove = (Move) other;
        return (
            fromSquare == otherMove.getFromSquare() && 
            toSquare == otherMove.getToSquare() &&
            flag == otherMove.getFlag()
        );
    }

    @Override
    public String toString() { 
        return (
            String.format(
                "From: %s, to: %s, flag: %d", 
                Board.SQUARE_TO_ALGEBRAIC[fromSquare], 
                Board.SQUARE_TO_ALGEBRAIC[toSquare],
                flag
            )
        );
    } 
    
}
