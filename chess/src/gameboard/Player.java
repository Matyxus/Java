package gameboard;

import gameboard.constants.Pieces;
import gameboard.constants.Board;

public class Player {
    /**
     * Biatboard of all pieces
     */
    private long allPieces = 0;
    /**
     * Bitboards of piecese indexed by type (e.g. individualPieces[Pieces.KING])
     */
    private final long[] individualPieces;
    /**
     * Array representing board with pieces, indexed by square (e.g. placedPieces[0])
     */
    private final int[] placedPieces;
    private final int color;
    
    public Player(int color) {
        this.color = color;
        individualPieces = new long[Pieces.PIECE_COUNT];
        placedPieces = new int[Board.BOARD_SIZE];
        resetVals();
    }

    /**
     * Resets bitboards to 0, and clears array of placed pieces
     */
    public void resetVals() {
        allPieces = 0;
        for (int i = 0; i < Pieces.PIECE_COUNT; i++) {
            individualPieces[i] = 0;
        }
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            placedPieces[i] = -1;
        }
    }

    /**
     * @param piece to be placed
     * @param square at which piece is placed
     */
    public void addPiece(int piece, int square) {
        placedPieces[square] = piece;
        final long targetSquare = Board.ONE << square;
        allPieces |= targetSquare;
        individualPieces[piece] |= targetSquare;
    }

    /**
     * @param square where piece is
     * @return removed piece (integer)
     */
    public int removePiece(int square) {
        final int removedPiece = placedPieces[square];
        placedPieces[square] = -1;
        final long targetSquare = Board.ONE << square;
        allPieces ^= targetSquare;
        individualPieces[removedPiece] ^= targetSquare;
        return removedPiece;
    }

    /**
     * @param from square
     * @param to square
     * @return bitboard containing initial and destination position of piece
     */
    public long movePiece(int from, int to) {
        final int tmp = placedPieces[from];
        placedPieces[from] = -1;
        final long targetSquare = (Board.ONE << from) | (Board.ONE << to);
        allPieces ^= targetSquare;
        individualPieces[tmp] ^= targetSquare;
        placedPieces[to] = tmp;
        return targetSquare;
    }

    // ------------------------- Getters ------------------------- 

    /**
     * @return bitboard of all sliding pieces (Bishop/s, Rook/s, Queen/s)
     */
    public long getSliders() {
        return (
            individualPieces[Pieces.BISHOP] | 
            individualPieces[Pieces.ROOK]   |
            individualPieces[Pieces.QUEEN]
        );
    }

    /**
     * @param square to check
     * @return "-1" if there is no piece, else non-negative value
     * representing piece type
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
     * @return Array of all placed pieces
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
        final long targetSquare = Board.ONE << square;
        // Remove original piece
        individualPieces[placedPieces[square]] ^= targetSquare;
        // Add new Piece
        placedPieces[square] = promoteTo;
        individualPieces[promoteTo] |= targetSquare;
    }
}
