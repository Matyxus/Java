package board;

import java.util.ArrayList;

/**
 * Class containing piece, its color, square on board, moves
 */
public class Spot {
    private final int piece;
    private final int color;
    private final int square;
    private final ArrayList<Move> moves;

    public Spot(int piece, int color, int square) {
        this.piece = piece;
        this.color = color;
        this.square = square;
        moves = new ArrayList<Move>();
    }
    
    /**
     * @param move_list from which moves are taken
     */
    public void setMoves(ArrayList<Move> move_list) {
        moves.clear();
        move_list.forEach(move -> {
            if (move.getFromSquare() == square) {
                moves.add(move);
            }
        });
    }

    /**
     * @return the color of piece
     */
    public int getColor() {
        return color;
    }

    /**
     * @return legal moves
     */
    public ArrayList<Move> getMoves() {
        return moves;
    }

    /**
     * @return the piece
     */
    public int getPiece() {
        return piece;
    }

    /**
     * @return the square on board
     */
    public int getSquare() {
        return square;
    }
}
