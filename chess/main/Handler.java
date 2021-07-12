package main;

import java.util.ArrayList;

import assets.Assets;
import board.GameBoard;
import board.Spot;
import managers.FileManager;
import managers.MouseManager;

public class Handler {
    private final Game game;
    private final GameBoard gameBoard;
    private final MouseManager mouseManager;
    private final Assets assets;
    private final FileManager fileManager;

    public Handler(Game game) {
        this.game = game;
        this.gameBoard = new GameBoard();
        this.mouseManager = new MouseManager();
        this.assets = new Assets();
        this.fileManager = new FileManager();
    }

    /**
     * Loads game either from FEN or from file
     */
    public void loadGame() {
        ArrayList<Spot> pieces = null;
        try {
            pieces = fileManager.loadFile();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (pieces != null && !pieces.isEmpty()) {
            System.out.println("Loading Success");
            gameBoard.reset();
            // Load pieces
            pieces.forEach((spot) -> gameBoard.addPiece(
                spot.getPiece(), spot.getColor(), spot.getSquare())
            );
            // Load text history
            game.getDisplay().setText(fileManager.getTextHistory());
        }
    }

    /**
     * Saves game into two files .txt and .png
     * @param img image of current board with pieces
     */
    public void saveGame(String textHistory) {
        fileManager.safeFile(textHistory, game.getGraphicsManager().getScreenShot(), gameBoard);
    }

    /**
     * @return MouseManager
     */
    public MouseManager getMouseManager() {
        return mouseManager;
    }
    
    /**
     * @return GameBard
     */
    public GameBoard getGameBoard() {
        return gameBoard;
    }
    
    /**
     * @return Assets
     */
    public Assets getAssets() {
        return assets;
    }

    /**
     * @return Game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @return Current mouse X position centered into grid (chess board)
     */
    public int centerMouseX() {
        return assets.centerMouseX(mouseManager.getMouseX());
    }

    /**
     * @return Current mouse Y position centered into grid (chess board)
     */
    public int centerMouseY() {
        return assets.centerMouseY(mouseManager.getMouseY());
    }
}