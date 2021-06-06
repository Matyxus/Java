package board;
import board.constants.Colors;

public class GameBoard {

    private final Player[] players;

    public GameBoard() {
        players = new Player[2];
        players[Colors.WHITE.ordinal()] = new Player(Colors.WHITE);
        players[Colors.BLACK.ordinal()] = new Player(Colors.BLACK);
        
    }

    public void resetVals() {
        players[Colors.WHITE.ordinal()].resetVals();
        players[Colors.BLACK.ordinal()].resetVals();
    }
    
    public void addPiece(int piece, int color, int square) {
        players[color].addPiece(piece, square);
    }

    public void removePiece(int color, int square) {
        players[color].removePiece(square);
    }
    
    public Square containsPiece(int square) {
        if (players[Colors.WHITE.ordinal()].containsPiece(square) != null) {
            return players[Colors.WHITE.ordinal()].containsPiece(square);
        }
        return players[Colors.BLACK.ordinal()].containsPiece(square);
    }

    public void placePiece(int piece, int color, int square) {
        if (players[Colors.WHITE.ordinal()].containsPiece(square) != null) {
            players[Colors.WHITE.ordinal()].removePiece(square);
        } else if (players[Colors.BLACK.ordinal()].containsPiece(square) != null) {
            players[Colors.BLACK.ordinal()].removePiece(square);
        }
        players[color].addPiece(piece, square);
    }

    public Player getPlayers(Colors color) {
        return players[color.ordinal()];
    }

    

}
