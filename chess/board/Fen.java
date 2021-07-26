package board;

import java.util.ArrayList;

import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;

// https://www.chessprogramming.org/Forsyth-Edwards_Notation

public class Fen {
    
    public Fen() {};

    /**
     * @param fen string
     * @return Array of Spots (containing pieces, colors etc..) 
     * if fen string is correct, null otherwise
     */
    public ArrayList<Spot> interpret(String fen, Position position) {
        if (fen == null || fen.isEmpty()) {
            return null;
        }
        String[] splitted = fen.split("/");
        String[] info = splitted[splitted.length-1].split(" ");
        splitted[splitted.length-1] = info[0];
        assert (splitted.length == 8);
        assert (info.length >= 4);
        // ----------------- Board ----------------- 
        ArrayList<Spot> pieces = new ArrayList<Spot>();
        int square = 0;
        for (int i = 0; i < splitted.length; i++) {
            for (Character ch : splitted[i].toCharArray()) {
                if (isPiece(ch)) {
                    int color = (Character.isLowerCase(ch)) ? Colors.BLACK : Colors.WHITE;
                    pieces.add(new Spot(Pieces.charToPiece.get(Character.toLowerCase(ch)), color, square));
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
        // ----------------- Info ----------------- 
        if (!(info[1].equals("w") || info[1].equals("b"))) {
            return null;
        }
        position.setSideToMove(
            (info[1].equals("w")) ? Colors.WHITE : Colors.BLACK
        );
        // Castle rights
        position.clearCastleRights(Colors.WHITE);
        position.clearCastleRights(Colors.BLACK);
        if (!info[2].equals("-")) {
            for (Character ch : info[2].toCharArray()) {
                int color = (Character.isUpperCase(ch)) ? Colors.WHITE : Colors.BLACK;
                assert (Pieces.charToPiece.get(Character.toLowerCase(ch)) != null);
                int side = Pieces.charToPiece.get(Character.toLowerCase(ch));
                position.setCastlingRight(color, side, true);
            }
        } 
        // Enpassant square
        position.setEnpassant(
            (info[3].equals("-")) ? 0 : (Size.algebraicToSquare(info[3]))
        );
        return pieces;
    }

    /**
     * @param gameBoard
     * @return FEN notation in String
     */
    public String createFen(GameBoard gameBoard) {
        final int[] whitePieces = gameBoard.getPieces(Colors.WHITE);
        final int[] blackPieces = gameBoard.getPieces(Colors.BLACK);
        // ----------------- Board ----------------- 
        String result = "";
        int empty = 0;
        for (int square = 0; square < Size.BOARD_SIZE; square++) {
            // Check if board on this square is empty
            if (whitePieces[square] != -1 || blackPieces[square] != -1) {
                // Write empty squares
                if (empty != 0) {
                    result += String.valueOf(empty);
                    empty = 0;
                }
                // Write piece
                result += (
                    (whitePieces[square] != -1) ? 
                    Character.toUpperCase(Pieces.pieceToChar.get(whitePieces[square]))
                    :
                    Pieces.pieceToChar.get(blackPieces[square])
                );
            } else {
                empty++;
            }
            // After every 8 squares add "/", apart from first and last
            if (square != 0 && ((square+1) % Size.ROWS) == 0) {
                // Write empty squares
                if (empty != 0) {
                    result += String.valueOf(empty);
                    empty = 0;
                }
                // Dont at at the end
                if (square != (Size.BOARD_SIZE-1)) {
                    result += "/";
                }
            }
        }
        // ----------------- Info ----------------- 
        // Side to move
        result += " " + (
            (gameBoard.getCurrentPositon().getSideToMove() == Colors.WHITE) ?
            "w " : "b "
        );
        // Castling rights
        empty = 0;
        for (int color = Colors.WHITE; color < Colors.COLOR_COUNT; color++) {
            for (int side = Pieces.KING; side < 2; side++) {
                if (gameBoard.getCurrentPositon().getCastlingRights()[color][side]) {
                    result += (
                        (color == Colors.WHITE) ? 
                        Character.toUpperCase(Pieces.pieceToChar.get(side))
                        :
                        Pieces.pieceToChar.get(side)
                    );
                } else {
                    empty++;
                }
            }
        }
        // No castling rights
        if (empty == 4) {
            result += "- ";
        } else {
            result += " ";
        }
        // Enpassant square
        if (gameBoard.getCurrentPositon().getEnpassant() != 0) {
            result += Size.SQUARE_TO_ALGEBRAIC[gameBoard.getCurrentPositon().getEnpassant()];
        } else {
            result += "-";
        }
        // Pawn counters
        result += " 0 1";
        return result;
    }

    /**
     * @param ch char from fen
     * @return true if char is piece, false otherwise
     */
    private boolean isPiece(char ch) {
        return (Pieces.charToPiece.get(ch) != null || Pieces.charToPiece.get(Character.toLowerCase(ch)) != null);
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
