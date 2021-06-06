package board;
import board.constants.Colors;

public class GameBoard {

    private final Player[] players;

    public GameBoard() {
        players = new Player[2];
        players[Colors.WHITE.ordinal()] = new Player(Colors.WHITE);
        players[Colors.BLACK.ordinal()] = new Player(Colors.BLACK);
        
    }

    /**
     * Resets values of players.
     */
    public void resetVals() {
        players[Colors.WHITE.ordinal()].resetVals();
        players[Colors.BLACK.ordinal()].resetVals();
    }
    
    /**
     * @param piece to be added
     * @param color of piece
     * @param square of piece
     */
    public void addPiece(int piece, int color, int square) {
        players[color].addPiece(piece, square);
    }

    /**
     * @param color of piece
     * @param square of piece
     */
    public void removePiece(int color, int square) {
        players[color].removePiece(square);
    }
    
    /**
     * @param square of piece
     * @return Class Square if square with such a index exists,
     * null otherwise.
     */
    public Square containsPiece(int square) {
        if (players[Colors.WHITE.ordinal()].containsPiece(square) != null) {
            return players[Colors.WHITE.ordinal()].containsPiece(square);
        }
        return players[Colors.BLACK.ordinal()].containsPiece(square);
    }

    /**
     * Places piece on given square, removes previous piece on this square if possible.
     * @param piece to be placed
     * @param color of piece
     * @param square of piece
     */
    public void placePiece(int piece, int color, int square) {
        if (players[Colors.WHITE.ordinal()].containsPiece(square) != null) {
            players[Colors.WHITE.ordinal()].removePiece(square);
        } else if (players[Colors.BLACK.ordinal()].containsPiece(square) != null) {
            players[Colors.BLACK.ordinal()].removePiece(square);
        }
        players[color].addPiece(piece, square);
    }

    /**
     * @param color of player
     * @return Player of given color.
     */
    public Player getPlayers(Colors color) {
        return players[color.ordinal()];
    }

    

}
