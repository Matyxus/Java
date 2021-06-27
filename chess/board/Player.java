package board;

import java.util.HashMap;

import board.constants.Pieces;
import board.constants.Size;

public class Player {

    private long allPieces = 0;
    private final long[] individualPieces;
    // HashMaps holding current placed pieces (key = index of square on board, topleft = 0)
    private final HashMap<Integer, Spot> placedPieces;
    private final int color;
    
    public Player(int color) {
        this.color = color;
        individualPieces = new long[Pieces.PIECE_COUNT];
        placedPieces = new HashMap<>();
        resetVals();
    }

    /**
     * Resets bitboards to 0, and clears hashmap of pieces.
     */
    public void resetVals() {
        allPieces = 0;
        for (int i = 0; i < individualPieces.length; i++) {
            individualPieces[i] = 0;
        }
        placedPieces.clear();
    }

    /**
     * @param piece to be placed
     * @param square at which piece is placed
     */
    public void addPiece(int piece, int square) {
        placedPieces.put(square, new Spot(piece, color, square, false));
        final long targetSquare = Size.ONE << square;
        allPieces |= targetSquare;
        individualPieces[piece] |= targetSquare;
    }

    /**
     * @param square where piece is
     */
    public void removePiece(int square) {
        final int removedPiece = (placedPieces.remove(square)).getPiece();
        final long targetSquare = Size.ONE << square;
        allPieces ^= targetSquare;
        individualPieces[removedPiece] ^= targetSquare;
    }

    /**
     * @param square where piece is
     * @return the square, or null if there is none
     */
    public Spot containsPiece(int square) {
        return placedPieces.get(square);
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
     * @return HashMap of all placed pieces
     */
    public HashMap<Integer, Spot> getPlacedPieces() {
        return placedPieces;
    }

    /**
     * @return color of this player
     */
    public int getColor() {
        return color;
    }

    /**
     * @return the square king is currently on
     */
    public int getKingSquare() {
        return Long.numberOfTrailingZeros(individualPieces[Pieces.KING]);
    }
    
}
