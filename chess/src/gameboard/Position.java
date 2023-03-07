package gameboard;

import gameboard.constants.Colors;
import gameboard.constants.Pieces;
import gameboard.constants.Board;

/**
 * Class representing current chess position, contains:
 *  which side is moving, enpassant board, hash of board, castling rights
 */
public class Position {
    private int sideToMove;
    private int enpassant;
    private long hash;
    /**
     * Indexed by [Colors.WHITE / Colors.BLACK][Pieces.KING / Pieces.QUEEN] 
     */
    private final boolean[][] castlingRights;
    

    public Position(int sideToMove, int enpassant, long hash, boolean[][] castlingRights) {
        this.sideToMove = sideToMove;
        this.enpassant = enpassant;
        this.hash = hash;
        this.castlingRights = castlingRights;
    }
        
    // ------------------------------ Setters ------------------------------

    /**
     * @param enpassant square on which enpassant capture is possible
     */
    public void setEnpassant(int enpassant) {
        this.enpassant = enpassant;
    }

    /**
     * @param hash to be set
     */
    public void setHash(long hash) {
        this.hash = hash;
    }

    /**
     * @param hash which will be XORed with current hash
     */
    public void modifyHash(long hash) {
        this.hash ^= hash;
    }

    /**
     * @param sideToMove to be set
     */
    public void setSideToMove(int sideToMove) {
        this.sideToMove = sideToMove;
    }

    /**
     * @param color of player
     * @param side to castle
     * @param can boolean if its possible to castle
     */
    public void setCastlingRight(int color, int side, boolean can) {
        castlingRights[color][side] = can;
    }

    /**
     * @param color of player, which lost both castle rights
     */
    public void clearCastleRights(int color) {
        castlingRights[color][Pieces.KING]  = false;
        castlingRights[color][Pieces.QUEEN] = false;
    }

    /**
     * Swap sideToMove to enemy player
     */
    public void swapPlayers() {
        sideToMove = (sideToMove + 1) & 1;
    }

    // ------------------------------ Getters ------------------------------

    /**
     * @return enpassant square
     */
    public int getEnpassant() {
        return enpassant;
    }

    /**
     * @return hash recorded on this position
     */
    public long getHash() {
        return hash;
    }

    /**
     * @return color of player which is currently playing
     */
    public int getSideToMove() {
        return sideToMove;
    }

    /**
     * @return castling rights for both players
     */
    public boolean[][] getCastlingRights() {
        return castlingRights;
    }

    // ------------------------------ Utils ------------------------------

    /**
     * @return deepcopy of this position
     */
    public Position deepCopy() {
        boolean[][] temp = new boolean[][] {
            {castlingRights[Colors.WHITE][Pieces.KING], castlingRights[Colors.WHITE][Pieces.QUEEN]},
            {castlingRights[Colors.BLACK][Pieces.KING], castlingRights[Colors.BLACK][Pieces.QUEEN]}
        };
        return new Position(sideToMove, enpassant, hash, temp);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        final Position otherPos = (Position) other;
        for (int i = 0; i < castlingRights.length; i++) {
            for (int j = 0; j < castlingRights[i].length; j++) {
                if (!(castlingRights[i][j] == otherPos.getCastlingRights()[i][j])) {
                    return false;
                }
            }
        }
        return (
            (sideToMove == otherPos.getSideToMove()) && 
            (enpassant == otherPos.getEnpassant()) && 
            (hash == otherPos.getHash())
        );
    }

    @Override
    public String toString() {
        String result = "";
        final String enpassantSquare = (
            (enpassant == 0) ? 
            "None" : Board.SQUARE_TO_ALGEBRAIC[Long.numberOfTrailingZeros(enpassant)]
        );
        result += "Side to move: " + Colors.COLOR_TO_STRING[sideToMove] + "\n";
        result += "Enpassant Square: " + enpassantSquare + "\n";
        result += "Hash: " + hash + "\n";
        // Castle rights
        for (int color = Colors.WHITE; color < Colors.COLOR_COUNT; color++) {
            result += (Colors.COLOR_TO_STRING[color] + " castling rights:\n");
            for (int piece : Pieces.CASTLING_SIDES) {
                result += (
                    Pieces.pieceToString.get(piece) + " side: " + 
                    castlingRights[color][piece]
                );
            }
        }
        return result;
    }
}
