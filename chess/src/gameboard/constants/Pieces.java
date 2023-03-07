package gameboard.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing constants associated with pieces
 */
public class Pieces {
    public static final int KING        = 0;
    public static final int QUEEN       = 1;
    public static final int ROOK        = 2;
    public static final int KNIGHT      = 3;
    public static final int BISHOP      = 4;
    public static final int PAWN        = 5;
    public static final int PIECE_COUNT = 6;
    /**
     * All pieces that pawn can promote to
     */
    public static final int[] PROMOTION_PIECES = {QUEEN, ROOK, KNIGHT, BISHOP};
    public static final int[] ALL_PIECES = {KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN};
    public static final int[] CASTLING_SIDES = {KING, QUEEN};

    /**
     * Map that maps char (e.g. 'k', 'q'..) to piece
     */
    public static final HashMap<Character, Integer> charToPiece = 
        new HashMap<Character, Integer>(Map.of(
            'k', Pieces.KING,
            'q', Pieces.QUEEN,
            'r', Pieces.ROOK,
            'n', Pieces.KNIGHT,
            'b', Pieces.BISHOP,
            'p', Pieces.PAWN
    ));

    /**
     * Map that maps pieces (e.g. KING, QUEEN ..) to char (e.g. 'k', 'q', ..)
     */
    public static final HashMap<Integer, Character> pieceToChar = 
        new HashMap<Integer, Character>(Map.of(
            Pieces.KING,   'k', 
            Pieces.QUEEN,  'q', 
            Pieces.ROOK,   'r',
            Pieces.KNIGHT, 'n', 
            Pieces.BISHOP, 'b', 
            Pieces.PAWN,   'p' 
    ));

    /**
     * Map that maps pieces (e.g. KING, QUEEN ..) to string (e.g. "King", "Queen", ..)
     */
    public static final HashMap<Integer, String> pieceToString = 
        new HashMap<Integer, String>(Map.of(
            Pieces.KING,   "King", 
            Pieces.QUEEN,  "Queen", 
            Pieces.ROOK,   "Rook",
            Pieces.KNIGHT, "Knight", 
            Pieces.BISHOP, "Bishop", 
            Pieces.PAWN,   "Pawn" 
    ));


    /**
     * Array containing unicode String for chess piece
     * (e.g. pieceToUnicode[Colors.WHITE][Pieces.KING])
     */
    public static final String[][] pieceToUnicode = 
    {   
        // King  ,  Queen  ,  Rook   ,  Knight ,  Bishop ,  Pawn
        {"\u265A", "\u265B", "\u265C", "\u265E", "\u265D", "\u265F"}, // White
        {"\u2654", "\u2655", "\u2656", "\u2658", "\u2657", "\u2659"}  // Black
    };
}
