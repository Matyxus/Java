package board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;

// https://www.chessprogramming.org/Forsyth-Edwards_Notation

public class Fen {
    final HashMap<Character, Integer> charToPiece = new HashMap<Character, Integer>(Map.of(
        'k', Pieces.KING,
        'q', Pieces.QUEEN,
        'r', Pieces.ROOK,
        'n', Pieces.KNIGHT,
        'b', Pieces.BISHOP,
        'p', Pieces.PAWN
    ));

    final HashMap<Integer, Character> pieceToChar = new HashMap<Integer, Character>(Map.of(
        Pieces.KING,   'k', 
        Pieces.QUEEN,  'q', 
        Pieces.ROOK,   'r',
        Pieces.KNIGHT, 'n', 
        Pieces.BISHOP, 'b', 
        Pieces.PAWN,   'p' 
    ));

    public Fen() {};

    /**
     * @param fen string
     * @return Array of Spots (containing pieces, colors etc..) 
     * if fen string is correct, null otherwise
     */
    public ArrayList<Spot> interpret(String fen) {
        String[] splitted = fen.split("/");
        String[] info = splitted[splitted.length-1].split(" ");
        splitted[splitted.length-1] = info[0];
        /*
        for (String string : info) {
            System.out.println(string);
        }
        */
        // Board
        ArrayList<Spot> pieces = new ArrayList<Spot>();
        int square = 0;
        for (int i = 0; i < splitted.length; i++) {
            for (Character ch : splitted[i].toCharArray()) {
                if (isPiece(ch)) {
                    int color = (Character.isLowerCase(ch)) ? Colors.BLACK : Colors.WHITE;
                    pieces.add(new Spot(charToPiece.get(Character.toLowerCase(ch)), color, square, false));
                    square++;
                } else if (isNum(ch)) {
                    square += Integer.parseInt(String.valueOf(ch));
                } else {
                    System.out.println("Error, incorrect char in fen!: " + ch);
                    return null;
                }
            }
        }
        if (square != Size.BOARD_SIZE) {
            System.out.println("Error, incorrect fen, sum of pieces and empty square is not 64!");
            System.out.println("Squares: " + square);
            return null;
        }
        // Info
        /*
        for (int i = 1; i < info.length; i++) {
            for (Character ch : info[i].toCharArray()) {
                System.out.println(ch);
            }
        }
        */
        return pieces;
    }

    /**
     * @param gameBoard
     * @return FEN notation in String
     */
    public String createFen(GameBoard gameBoard) {
        ArrayList<Spot> pieces = new ArrayList<Spot>();
        gameBoard.getPieces(Colors.BLACK).forEach((square, spot) ->
            pieces.add(spot)
        );
        gameBoard.getPieces(Colors.WHITE).forEach((square, spot) ->
            pieces.add(spot)
        );
        Collections.sort(pieces, 
            (o1, o2) -> ((Integer) o1.getSquare()).compareTo(o2.getSquare())
        );
        String result = "";
        int square = 0;
        int counter = Size.ROWS;
        for (Spot piece : pieces) {
            Character ch = pieceToChar.get(piece.getPiece());
            ch = (piece.getColor() == Colors.WHITE) ? Character.toUpperCase(ch) : ch;
            // Square diff
            if (square != piece.getSquare()) {
                int diff = (piece.getSquare()-square);
                square = piece.getSquare();
                // Something is already written, add part of 
                // diff to have sum of 8 between "/.../"
                if (counter != Size.ROWS && (counter - diff) <= 0) {
                    diff -= counter;
                    result += Integer.toString(counter);
                    result += "/";
                }
                // Eights
                while (diff >= Size.ROWS) {
                    result += "8/";
                    diff -= Size.ROWS;
                }
                // Reset counter
                if (result.length() > 0 && result.charAt(result.length()-1) == '/') {
                    counter = Size.ROWS;
                }
                // Rest of the diff
                if (diff > 0) {
                    result += Integer.toString(diff);
                }
                counter -= diff;
            }
            // Add piece
            result += ch;
            square++;
            counter--;
            if (counter == 0) {
                counter = Size.ROWS;
                result += "/";
            }
        }

        // Square has to be equal to board_size
        if (square != Size.BOARD_SIZE) {
            int diff = (Size.BOARD_SIZE - square);
            if (counter != Size.ROWS && (counter - diff) <= 0) {
                diff -= counter;
                result += Integer.toString(counter);
                result += "/";
            }
            // Diff is equal or more than 8
            while (diff >= Size.ROWS) {
                result += "8/";
                diff -= Size.ROWS;
            }
            // Diff is less than 8
            if (diff > 0) {
                result += Integer.toString(diff);
            }
        }
        
        // Remove last '/' if present
        if (result.length() > 0 && result.charAt(result.length()-1) == '/') {
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    /**
     * @param ch char from fen
     * @return true if char is piece, false otherwise
     */
    private boolean isPiece(char ch) {
        return (charToPiece.get(ch) != null || charToPiece.get(Character.toLowerCase(ch)) != null);
    }

    /**
     * @param ch char from fen
     * @return true if har is number between (0-8, including 0 and 8),
     * false otherwise
     */
    private boolean isNum(char ch) {
        return (ch >= 48 && ch <= 56);
    }

}
