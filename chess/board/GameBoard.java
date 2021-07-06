package board;
import java.util.ArrayList;
import java.util.HashMap;

import board.constants.Colors;
import managers.FileManager;

public class GameBoard {
    private final MoveGen moveGen;
    private final FileManager fileManager;
    private int currentPlayer = Colors.WHITE;
    

    private final Player[] players = {new Player(Colors.WHITE), new Player(Colors.BLACK)};

    public GameBoard() {
        moveGen = new MoveGen(new Rays(), players);
        fileManager = new FileManager();
    }

    public void reset() {
        System.out.println("Deleting pieces");
        for (Player player : players) {
            player.resetVals();
        }
    }

    public void loadGame() {
        ArrayList<Spot> pieces = fileManager.loadFile();
        if (pieces != null && !pieces.isEmpty()) {
            System.out.println("Loading Success");
            reset();
            pieces.forEach((spot) -> addPiece(
                spot.getPiece(), spot.getColor(), spot.getSquare())
            );
        }
    }

    public void saveGame() {
        System.out.println("Saving not implemented");
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
     * Places piece on given square, removes previous piece on this square if possible.
     * @param piece to be placed
     * @param color of piece
     * @param square of piece
     * @return Structure of piece that was removed, null if none were removed.
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

    public HashMap<Integer, Spot> getPieces(int color) {
        return players[color].getPlacedPieces();
    }
    
    public Player getPlayer(int color) {
        return players[color];
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
}
