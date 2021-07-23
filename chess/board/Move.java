package board;

import board.constants.Flags;
import board.constants.Size;

public class Move {
    private final int fromSquare;
    private final int toSquare;
    /**
     * Flag hold what type of move it is (e.g. PROMOTION |
     * Pieces.QUEEN), for enpassant it contains square (e.g. EN_PASSANT | 32)
     */
    private final int flag;

    public Move(int fromSquare, int toSquare, int flag) {
        this.fromSquare = fromSquare;
        this.toSquare = toSquare;
        this.flag = flag;
    }

    public boolean isCastle() {
        return (flag & Flags.CASTLE) != 0;
    }

    public boolean isPromotion() {
        return (flag & Flags.PROMOTION) != 0;
    }

    public boolean isCapture() {
        return (flag & Flags.CAPTURE) != 0;
    }

    public boolean isEnpassant() {
        return (flag & Flags.EN_PASSANT) != 0;
    }

    public int getFromSquare() {
        return fromSquare;
    }

    public int getToSquare() {
        return toSquare;
    }

    public int getFlag() {
        return flag;
    }

    /**
     * @return side to which piece is castling (e.g. Pieces.KING)
     */
    public int getCastleSide()  {
        return (flag ^ Flags.CASTLE);
    }

    /**
     * @return piece to which pawn will promote to 
     * (e.g. Pieces.QUEEN)
     */
    public int getPromotionPiece() {
        return (flag ^ Flags.PROMOTION ^ (flag & Flags.CAPTURE));
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other.getClass() != this.getClass()) {
            return false;
        }
        final Move otherMove = (Move) other;
        return (fromSquare == otherMove.getFromSquare() && 
                toSquare == otherMove.getToSquare() &&
                flag == otherMove.getFlag()
        );
    }

    @Override
    public String toString() { 
        return ("From: " + Size.SQUARE_TO_ALGEBRAIC[fromSquare] + 
            " to: " + Size.SQUARE_TO_ALGEBRAIC[toSquare] + " flag: " + 
            flag
        );
    } 
    
}
