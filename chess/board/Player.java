package board;

import board.constants.Pieces;
import board.constants.Size;

public class Player {

    private long allPieces = 0;
    private final long[] individualPieces;
    // HashMaps holding current placed pieces (key = index of square on board, top left = 0)
    private final int[] placedPieces;
    private final int color;
    
    public Player(int color) {
        this.color = color;
        individualPieces = new long[Pieces.PIECE_COUNT];
        placedPieces = new int[64];
        resetVals();
    }

    /**
     * Resets bitboards to 0, and clears hashmap of placed pieces
     */
    public void resetVals() {
        allPieces = 0;
        for (int i = 0; i < individualPieces.length; i++) {
            individualPieces[i] = 0;
        }
        for (int i = 0; i < placedPieces.length; i++) {
            placedPieces[i] = -1;
        }
    }

    /**
     * @param piece to be placed
     * @param square at which piece is placed
     */
    public void addPiece(int piece, int square) {
        placedPieces[square] = piece;
        final long targetSquare = Size.ONE << square;
        allPieces |= targetSquare;
        individualPieces[piece] |= targetSquare;
    }

    /**
     * @param square where piece is
     * @return Spot class
     */
    public int removePiece(int square) {
        final int removedPiece = placedPieces[square];
        placedPieces[square] = -1;
        final long targetSquare = Size.ONE << square;
        allPieces ^= targetSquare;
        individualPieces[removedPiece] ^= targetSquare;
        return removedPiece;
    }

    /**
     * @param from square
     * @param to square
     */
    public long movePiece(int from, int to) {
        final int tmp = placedPieces[from];
        placedPieces[from] = -1;
        final long targetSquare = (Size.ONE << from) | (Size.ONE << to);
        allPieces ^= targetSquare;
        individualPieces[tmp] ^= targetSquare;
        placedPieces[to] = tmp;
        return targetSquare;
    }

    public long getSliders() {
        return (
            individualPieces[Pieces.BISHOP] | 
            individualPieces[Pieces.ROOK]   |
            individualPieces[Pieces.QUEEN]
        );
    }

    /**
     * @param square where piece is
     * @return the square, or null if there is none
     */
    public int containsPiece(int square) {
        return placedPieces[square];
    }

    /**
     * @return bitboard of all pieces
     */
    public long getAllPieces() {
        return allPieces;
    }

    /**
     * @param piece of given type
     * @return bitboards containing all pieces of given type
     */
    public long getIndividualPieces(int piece) {
        return individualPieces[piece];
    }

     /**
     * @return bitboards containing all pieces
     */
    public long[] getAllIndividualPieces() {
        return individualPieces;
    }

    /**
     * @return HashMap of all placed pieces
     */
    public int[] getPlacedPieces() {
        return placedPieces;
    }

    /**
     * @return color of this player
     */
    public int getColor() {
        return color;
    }

    /**
     * @return the square king is currently on, 64 if king is not present
     */
    public int getKingSquare() {
        return Long.numberOfTrailingZeros(individualPieces[Pieces.KING]);
    }

    /**
     * @param square where piece is
     * @param promoteTo type of piece piece on square will become
     */
    public void promotePiece(int square, int promoteTo) {
        final long targetSquare = Size.ONE << square;
        // Remove original piece
        individualPieces[placedPieces[square]] ^= targetSquare;
        // Add new Piece
        placedPieces[square] = promoteTo;
        individualPieces[promoteTo] |= targetSquare;
    }
}
