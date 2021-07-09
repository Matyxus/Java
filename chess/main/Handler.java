package main;

import assets.Assets;
import board.GameBoard;
import managers.MouseManager;

public class Handler {
    private final Game game;
    private final GameBoard gameBoard;
    private final MouseManager mouseManager;
    private final Assets assets;

    public Handler(Game game) {
        this.game = game;
        this.gameBoard = new GameBoard(this);
        this.mouseManager = new MouseManager();
        this.assets = new Assets(0, 0);
    }

    public MouseManager getMouseManager() {
        return mouseManager;
    }
    
    public GameBoard getGameBoard() {
        return gameBoard;
    }
    
    public Assets getAssets() {
        return assets;
    }

    public Game getGame() {
        return game;
    }

    public int centerMouseX() {
        return assets.centerMouseX(mouseManager.getMouseX());
    }

    public int centerMouseY() {
        return assets.centerMouseY(mouseManager.getMouseY());
    }
}