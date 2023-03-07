package gameboard.history;

import gameboard.move_gen.Move;
import java.util.ArrayList;

/**
 * Clas recording history of game (e.g. Fen representation of rounds)
 */
public class GameHistory {
    private int round = 0;
    /**
     * Fen strings representing history of current game
     */
    private final ArrayList<String> fenHistory;
    
    public GameHistory() {
        System.out.println("Game History created");
    }

    /**
     * @param piece moving piece
     * @param color of moving piece
     * @param capture captured piece, -1 if none
     * @param move Move class, contaning type of move, and origin + destination
     * @return String containing text representation of move
     */
    public String recordMove(int piece, int color, int capture, Move move) {
        String result = "Round: " + round + "\n";
        // Piece
        result += Pieces.pieceToUnicode[color][piece];
        // From square
        result += (" " + Board.SQUARE_TO_ALGEBRAIC[move.getFromSquare()]);
        result += "   \u279D   ";
        // To Square
        result += (Board.SQUARE_TO_ALGEBRAIC[move.getToSquare()] + "\n");
        if (move.isCastle()) {
            result += "Castling: ";
            result += Pieces.pieceToUnicode[color][move.getCastleSide()] + " side\n";
        } else {
            // Handle Capture
            if (capture != -1) {
                result += "Capture: ";
                result += Pieces.pieceToUnicode[Colors.opposite_color(color)][capture];
                result += "\n";
            }

            if (move.isPromotion()) {
                result += "Promoting to: ";
                result += Pieces.pieceToUnicode[color][move.getPromotionPiece()];
                result += "\n";
            }
        }
        // Increase round
        round++;
        return result;
    }   

    /**
     * @param round to be set
     */
    public void setRound(int round) {
        this.round = (round < 0) ? 0 : round;
    }
    
    /**
     * @return current round
     */
    public int getRound() {
        return round;
    }
}
