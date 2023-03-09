package gameboard.history;

import java.util.Arrays;

import gameboard.Player;
import gameboard.Position;
import gameboard.constants.Pieces;
import gameboard.constants.Colors;
import gameboard.constants.Board;
import gameboard.constants.Castle;
import utils.Pair;

// https://www.chessprogramming.org/Forsyth-Edwards_Notation

public class Fen {
    
    public Fen() {};

    /**
     * @param fen of chess position
     * @return true if fen is valid, false otherwise
     */
    public boolean isLegal(String fen) {
        return true;
    }

    public static void main(String[] args) {
        Fen fenGenerator = new Fen();
        String fen = "8/5k2/3p4/1p1Pp2p/pP2Pp1P/P4P1K/8/8 b - - 99 50";    
        fenGenerator.interpretFen(fen);
    }

    /**
     * @param fen string representation of FEN position
     * @param position chess board position
     * @return pair containing arrays of pieces and chess Position, null if fen is invalid
     */
    public Pair<int[][], Position> interpretFen(String fen) {
        // Check for null or empty
        if (fen == null || fen.isEmpty()) {
            System.out.println("Invalid fen string, either null or empty!");
            return null;
        } 
        String[] splitted = fen.split(" ");
        if (splitted.length < 2) {
            System.out.println("Unable to split fen string by empty space, make sure to put empty space after each expression!");
            return null;
        // Check for 7 slashes
        } else if (splitted[0].chars().filter(slash -> slash == '/').count() != 7) {
            System.out.println("Invalid fen string, coudnt find '/' 7 times, got: " + splitted[0]);
            return null;
        }
        splitted[0] = splitted[0].replaceAll("/", "");
        // ----------------- Board ----------------- 
        int[][] pieces = new int[Colors.COLOR_COUNT][Board.BOARD_SIZE];
        Arrays.fill(pieces[Colors.WHITE], Pieces.INVALID_PIECE);
        Arrays.fill(pieces[Colors.BLACK], Pieces.INVALID_PIECE);
        int square = 0;
        for (Character ch : splitted[0].toCharArray()) {
            if (isPiece(ch)) { // Add piece
                int color = (Character.isLowerCase(ch)) ? Colors.BLACK : Colors.WHITE;
                pieces[color][square] = Pieces.charToPiece.get(Character.toLowerCase(ch));
                square++; // Each piece is equal to one square
            } else if (ch >= '1' && ch <= '8') { // Add number of empty squares
                square += (ch - '0');
            } else { // Invalid char
                System.out.println("Error, incorrect char in fen!: " + ch);
                return null;
            }
        }
        // Check that sum of pieces and empty squares is equal to 64
        if (square != Board.BOARD_SIZE) {
            System.out.println("Error, incorrect fen, sum of pieces and empty square is not 64, got: " + square);
            return null;
        }
        System.out.println("White pieces: " + Arrays.toString(pieces[Colors.WHITE]));
        System.out.println("Black pieces: " + Arrays.toString(pieces[Colors.BLACK]));
        System.out.println("Splitted lenght: " + splitted.length);
       
        // ----------------- Info ----------------- 
        // Side to move
        if (!(splitted[1].equals("w") || splitted[1].equals("b"))) {
            System.out.println("Incorrect side to move in FEN, expected 'w' or 'b', got: " + splitted[1]);
            return null;
        }
        Position position = new Position(
            ((splitted[1].equals("w")) ? Colors.WHITE : Colors.BLACK),
            0, 0, Castle.getFullCastleRights()
        );
        // Castle rights
        if (splitted.length > 2) {
            position.clearCastleRights(Colors.WHITE);
            position.clearCastleRights(Colors.BLACK);
            if (!splitted[2].equals("-")) {
                // Invalid string of castling rights
                if (splitted[2].length() == 0 || splitted[2].length() > 4) {
                    System.out.println("Invalid castling rights, expected at least 1 char, up to 4 chars, got: " + splitted[2]);
                    return null;
                }
                for (Character ch : splitted[2].toCharArray()) {
                    int color = (Character.isUpperCase(ch)) ? Colors.WHITE : Colors.BLACK;
                    if (!isPiece(ch)) {
                        System.out.println("Invalid piece in castling rights: " + ch + " expected one of '[K, Q, k, q]'");
                        return null;
                    }
                    int side = Pieces.charToPiece.get(Character.toLowerCase(ch));
                    if (side != Pieces.KING || side != Pieces.QUEEN) {
                        System.out.println("Invalid piece in castling rights: " + ch + " expected one of '[K, Q, k, q]'");
                        return null;
                    }
                    position.setCastlingRight(color, side, true);
                }
            } 
        }
        // Enpassant square
        if (splitted.length > 3) {
            if (!splitted[3].equals("-") && Board.algebraicToSquare(splitted[3]) == Board.INVALID_SQUARE) {
                System.out.println("Invalind enpassant square, expected '-' or some square (e.g. 'a8'), got: " + splitted[3]);
                return null;
            }
            position.setEnpassant((splitted[3].equals("-")) ? 0 : (Board.algebraicToSquare(splitted[3])));
        }
        // Half move clock
        if (splitted.length > 4) {
            position.half_move_clock = Integer.valueOf(splitted[4]);
        }
        // Full move number
        if (splitted.length > 5) {
            position.full_move_number = Integer.valueOf(splitted[5]);
        }
        return new Pair<int[][], Position>(pieces, position);
    }

    /**
     * @param gameBoard
     * @return FEN notation in String
     */
    public String createFen(Player[] players, Position position) {
        final int[] whitePieces = players[Colors.WHITE].getPlacedPieces();
        final int[] blackPieces = players[Colors.BLACK].getPlacedPieces();
        // ----------------- Board ----------------- 
        String result = "";
        int empty = 0;
        for (int square = 0; square < Board.BOARD_SIZE; square++) {
            // Check if board on this square is empty
            if (whitePieces[square] != Pieces.INVALID_PIECE || blackPieces[square] != Pieces.INVALID_PIECE) {
                // Write empty squares
                if (empty != 0) {
                    result += String.valueOf(empty);
                    empty = 0;
                }
                // Write piece
                result += ((whitePieces[square] != Pieces.INVALID_PIECE) ? 
                    Character.toUpperCase(Pieces.pieceToChar.get(whitePieces[square])) :
                    Pieces.pieceToChar.get(blackPieces[square])
                );
            } else {
                empty++;
            }
            // After every 8 squares add "/", apart from first and last
            if (square != 0 && ((square + 1) % Board.ROWS) == 0) {
                // Write empty squares
                if (empty != 0) {
                    result += String.valueOf(empty);
                    empty = 0;
                }
                // Dont add "/" at the end
                if (square != (Board.BOARD_SIZE-1)) {
                    result += "/";
                }
            }
        }
        // ----------------- Info ----------------- 
        // Side to move
        result += " " + ((position.getSideToMove() == Colors.WHITE) ? "w " : "b ");
        // Castling rights
        empty = 0;
        for (int color = Colors.WHITE; color < Colors.COLOR_COUNT; color++) {
            for (int side = Pieces.KING; side < 2; side++) {
                if (position.getCastlingRights()[color][side]) {
                    result += (
                        (color == Colors.WHITE) ? 
                        Character.toUpperCase(Pieces.pieceToChar.get(side)) :
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
        if (position.getEnpassant() != 0) {
            result += Board.SQUARE_TO_ALGEBRAIC[position.getEnpassant()];
        } else {
            result += "-";
        }
        // Pawn counters
        result += " " + position.half_move_clock + " " + position.full_move_number;
        return result;
    }

    /**
     * @param ch char from fen
     * @return true if char is piece, false otherwise
     */
    private boolean isPiece(char ch) {
        return (Pieces.charToPiece.get(ch) != null || Pieces.charToPiece.get(Character.toLowerCase(ch)) != null);
    }
}
