package board;

import java.util.HashMap;

import board.constants.Colors;
import board.constants.Pieces;

public class Player {

    private long allPieces;
    private final long[] individualPieces;
    // HashMaps holding current placed pieces (key = index of square on board, topleft = 0)
    private final HashMap<Integer, Square> placedPieces;
    private final int color;
    
    public Player(Colors color) {
        this.color = color.ordinal();
        allPieces = Holder.ZERO;
        individualPieces = new long[Pieces.PIECES_COUNT.ordinal()];
        placedPieces = new HashMap<>();
    }

    /**
     * Resets values to 0, and clears hashmap of pieces.
     */
    public void resetVals() {
        allPieces = Holder.ZERO;
        for (int i = 0; i < individualPieces.length; i++) {
            individualPieces[i] = Holder.ZERO;
        }
        placedPieces.clear();
    }

    /**
     * 
     * @param piece to be placed
     * @param square at which piece is placed
     */
    public void addPiece(int piece, int square) {
        placedPieces.put(square, new Square(piece, color)); // Color could be redundant in Square.
        long targetSquare = Holder.ONE << square;
        allPieces |= targetSquare;
        individualPieces[piece] |= targetSquare;
    }

    /**
     * 
     * @param square where piece is
     */
    public void removePiece(int square) {
        int removedPiece = placedPieces.remove(square).getPiece();
        long targetSquare = Holder.ONE << square;
        allPieces ^= targetSquare;
        individualPieces[removedPiece] ^= targetSquare;
    }

    /**
     * 
     * @param square where piece is
     * @return the square, or null if there is none
     */
    public Square containsPiece(int square) {
        return placedPieces.get(square);
    }

    /**
     * 
     * @return bitboard of all pieces
     */
    public long getAllPieces() {
        return allPieces;
    }

    /**
     * 
     * @param piece of given type
     * @return bitboards containing all pieces of given type
     */
    public long getIndividualPieces(int piece) {
        return individualPieces[piece];
    }

    /**
     * 
     * @return HashMap of all placed pieces of this players
     * color
     */
    public HashMap<Integer, Square> getPlacedPieces() {
        return placedPieces;
    }

    /**
     * 
     * @return color of this player
     */
    public int getColor() {
        return color;
    }


    
}
