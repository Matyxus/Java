package main;

import assets.Assets;
import assets.Pair;
import board.GameBoard;
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
        Pair<String, String> tmp = null;
        try {
            tmp = fileManager.loadFile();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (!tmp.getKey().isEmpty()) {
            if (gameBoard.loadFen(tmp.getKey())) {
                System.out.println("Loading Success");
                // Load text history
                game.getDisplay().setText(tmp.getValue());
            }
        }
    }

    /**
     * Saves game into two files .txt and .png
     * @param textHistory history of current game
     */
    public void saveGame(String textHistory) {
        fileManager.safeFile(textHistory, game.getGraphicsManager().getScreenShot(), gameBoard.createFen());
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