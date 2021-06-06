package main;

import assets.Assets;
import board.GameBoard;
import managers.MouseManager;

public class Handler {

    private final GameBoard gameBoard;
    private final MouseManager mouseManager;
    private final Assets assets;

    public Handler() {
        this.gameBoard = new GameBoard();
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

    public int centerMouseX() {
        return assets.centerMouseX(mouseManager.getMouseX());
    }

    public int centerMouseY() {
        return assets.centerMouseY(mouseManager.getMouseY());
    }
}