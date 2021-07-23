package board.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Class contstant associated with pieces
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

    /**
     * @return array of all pieces
     */
    public static final int[] getPieces() {
        return new int[] {KING, QUEEN, ROOK, KNIGHT, BISHOP, PAWN};
    }

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
     * Map that maps pieces (e.g. KING, QUEEN ..) to char (e.g. 'k', 'q' ..)
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
