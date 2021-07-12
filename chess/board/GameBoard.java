package board;
import java.util.HashMap;

import board.constants.Colors;

public class GameBoard {
    private final GameHistory gameHistory;
    private final MoveGen moveGen;
    
    private int currentPlayer = Colors.WHITE;
    

    private final Player[] players = {new Player(Colors.WHITE), new Player(Colors.BLACK)};

    public GameBoard() {
        gameHistory = new GameHistory();
        moveGen = new MoveGen(new Rays(), players);
    }

    /**
     * Deletes pieces on board and resets
     * associated values with them (e.g. bitboards)
     */
    public void reset() {
        System.out.println("Deleting pieces");
        for (Player player : players) {
            player.resetVals();
        }
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

    public void updatePieces(int color) {
        moveGen.updatePiecesMoves(color);
    }
     
    /**
     * @param square of piece
     * @return Spot if square contains a piece,
     * null otherwise.
     */
    public Spot containsPiece(int square) {
        if (players[Colors.WHITE].containsPiece(square) != null) {
            return players[Colors.WHITE].containsPiece(square);
        }
        return players[Colors.BLACK].containsPiece(square);
    }

    /**
     * Places piece on given square, removes previous piece on this square if possible
     * @param piece to be placed
     * @param color of piece
     * @param square of piece
     * @return Structure of piece that was removed, null if none were removed
     */
    public Spot placePiece(int piece, int color, int square) {
        Spot removed = players[Colors.WHITE].getPlacedPieces().get(square);
        if (removed != null) {
            players[Colors.WHITE].removePiece(square);
        } else if (players[Colors.BLACK].containsPiece(square) != null) {
            removed = players[Colors.BLACK].getPlacedPieces().get(square);
            players[Colors.BLACK].removePiece(square);
        }
        players[color].addPiece(piece, square);
        return removed;
    }

    /**
     * @param fromSquare original position of piece
     * @param toSquare destination of piece
     * @param color of piece
     * @param piece type
     * @return Spot class if capture occured, null otherwise
     */
    public Spot movePiece(int fromSquare, int toSquare, int color,  int piece) {
        removePiece(color, fromSquare);
        // Check if capture occurred
        Spot removedSpot = players[((color+1) & 1)].containsPiece(toSquare);
        if (removedSpot != null) { 
            removePiece(removedSpot.getColor(), toSquare);
        }
        addPiece(piece, color, toSquare);
        return removedSpot;
    }

    /**
     * Calls movePiece function and also records move played,
     * switches player to opponent and generates moves for him
     * @param fromSquare original position of piece
     * @param toSquare destination of piece
     * @param color of piece
     * @param piece type
     * @return String containing text representation of move
     */
    public String playMove(int fromSquare, int toSquare, int color,  int piece) {
        Spot from = containsPiece(fromSquare);
        Spot capture = movePiece(fromSquare, toSquare, color, piece);
        swapPlayer();
        updatePieces(currentPlayer);
        return gameHistory.recordMove(from, capture, toSquare);
    }

    public HashMap<Integer, Spot> getPieces(int color) {
        return players[color].getPlacedPieces();
    }
    
    public Player getPlayer(int color) {
        return players[color];
    }

    private void swapPlayer() {
        currentPlayer = (currentPlayer + 1) & 1;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public GameHistory getGameHistory() {
        return gameHistory;
    }
    
}
