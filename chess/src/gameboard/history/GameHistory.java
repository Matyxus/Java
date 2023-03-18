package gameboard.history;
import gameboard.constants.Board;
import gameboard.constants.Colors;
import gameboard.constants.Pieces;
import gameboard.move_gen.Move;
import gameboard.Position;
import utils.Pair;
import java.util.ArrayList;

/**
 * Clas recording history of game (e.g. Fen representation of rounds)
 */
public class GameHistory {
    /**
     * Current round of game, also functions as pointer to current game state
     */
    private int round = 0;
    /**
     * Fen strings representing history of current game
     */
    private final ArrayList<String> fenHistory;
    private final ArrayList<Pair<Move, Integer>> moveHistory;
    private final ArrayList<Position> positionHistory;
    private final ArrayList<String> roundHistory;
    
    public GameHistory() {
        fenHistory = new ArrayList<String>();
        moveHistory = new ArrayList<Pair<Move, Integer>>();
        positionHistory = new ArrayList<Position>();
        roundHistory = new ArrayList<String>();
    }

    /**
     * @param position of board
     * @param fen of board
     */
    public void recordBoard(Position position, String fen) {
        positionHistory.add(position.deepCopy());
        fenHistory.add(fen);
    }

    /**
     * @param piece moving piece
     * @param color of moving piece
     * @param capture captured piece, -1 if none
     * @param move Move class, contaning type of move, and origin + destination
     * @return String containing text representation of move
     */
    public String recordMove(int piece, int color, int capture, Move move) {
        // Construct text represenation of move
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
                result += Pieces.pieceToUnicode[Colors.oppositeColor(color)][capture];
                result += "\n";
            }
            // Promotion
            if (move.isPromotion()) {
                result += "Promoting to: ";
                result += Pieces.pieceToUnicode[color][move.getPromotionPiece()];
                result += "\n";
            }
        }
        roundHistory.add(result);
        moveHistory.add(new Pair<Move, Integer>(move, capture));
        // Increase round
        round++;
        return result;
    }

    /**
     * Decresaes current round by one
     * @return true on success (round is not zero), false otherwise
     */
    public boolean previousRound() {
        if (round == 0) {
            return false;
        }
        round -= 1;
        return true;
    }

    /**
     * Increases current round by one
     * @return true on success (such position was recorded), false otherwise
     */
    public boolean nextRound() {
        if (round + 1 >= positionHistory.size()) {
            return false;
        }
        round += 1;
        return true;
    }

    /**
     * @param round to be set
     */
    public void setRound(int round) {
        this.round = (round < 0) ? 0 : round;
    }
    
    // ------------------- Getters ------------------- 

    /**
     * @return Current FEN of board (depends on current round), null if none exists
     */
    public String getCurrentFen() {
        return (round >= fenHistory.size()) ? null : fenHistory.get(round);
    }

    /**
     * @return Previous Move and capture piece, 
     * (depends on current round), null if none exists
     */
    public Pair<Move, Integer> getCurrentMove() {
        // Subtract one from round, since on initial board there is no Move
        return (round - 1 < 0 || round - 1 >= moveHistory.size()) ? null : moveHistory.get(round - 1);
    }

    /**
     * @return Current Position of board (depends on current round), null if none exists
     */
    public Position getCurrentPosition() {
        return (round >= positionHistory.size()) ? null : positionHistory.get(round);
    }

    /**
     * @return Previous string representation of move, 
     * (depends on current round), null if none exists
     */
    public String getCurrentTextMove() {
         // Subtract one from round, since on initial board there is no record of move
         return (round - 1 < 0 || round - 1 >= roundHistory.size()) ? null : roundHistory.get(round - 1);
    }

    /**
     * @return current round
     */
    public int getRound() {
        return round;
    }
}
