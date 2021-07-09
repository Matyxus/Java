package board;

import java.util.HashMap;
import java.util.Map;

import board.constants.Colors;
import board.constants.Pieces;
import main.Handler;

public class GameHistory {
    private int round = 0;
    private final HashMap<Integer, HashMap<Integer, String>> pieceToUnicode = 
        new HashMap<Integer, HashMap<Integer, String>>(Map.of(
            // White Pieces
            Colors.WHITE, new HashMap<Integer, String>(Map.of(
                Pieces.KING,   "\u2654",
                Pieces.QUEEN,  "\u2655",
                Pieces.ROOK,   "\u2656",
                Pieces.KNIGHT, "\u2658",
                Pieces.BISHOP, "\u2657",
                Pieces.PAWN,   "\u2659"
            )),
            // Black Pieces
            Colors.BLACK, new HashMap<Integer, String>(Map.of(
                Pieces.KING,   "\u265A",
                Pieces.QUEEN,  "\u265B",
                Pieces.ROOK,   "\u265C",
                Pieces.KNIGHT, "\u265E",
                Pieces.BISHOP, "\u265D",
                Pieces.PAWN,   "\u265F"
            ))
    ));

    private final String[] squareToAlgebraic = {
        "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
        "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
        "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
        "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
        "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
        "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
        "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
        "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1" 
    };

    private final Handler handler;

    public GameHistory(Handler handler) {
        this.handler = handler;
    }

    /**
     * Writes move in text format into JTextArea in Display class
     * @param from Spot class containing original piece
     * @param capture Spot class containign captured piece (null if none)
     * @param to destination
     */
    public void recordMove(Spot from, Spot capture, int to) {
        String result = "Round: " + round + "\n";
        // Piece
        result += pieceToUnicode.get(from.getColor()).get(from.getPiece());
        // From square
        result += (" (" + squareToAlgebraic[from.getSquare()] + ") ");
        result += "  ->  ";
        // To Square
        result += (" (" + squareToAlgebraic[to] + ")\n");
        // Handle Capture
        if (capture != null) {
            // Enpassant
            if (capture.getSquare() != to) {
                result += "ENPASSANT (" + squareToAlgebraic[capture.getSquare()] + ") ";
            }
            result += "CAPTURE: ";
            result += pieceToUnicode.get(capture.getColor()).get(capture.getPiece());
            result += "\n";
        }
        // Write result to JTextArea
        handler.getGame().getDisplay().appendText(result);
        // Increase round
        round++;
    }   

    /**
     * @param round to be set
     */
    public void setRound(int round) {
        this.round = round;
    }
    
    /**
     * @return current round
     */
    public int getRound() {
        return round;
    }
}
