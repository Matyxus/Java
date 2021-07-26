package main;

import assets.Assets;
import assets.Data.Holder;
import board.GameBoard;
import managers.FileManager;
import managers.MouseManager;
import states.State;

public class Handler {
    private final Game game;
    private final GameBoard gameBoard;
    private final MouseManager mouseManager;
    private final Assets assets;
    private final FileManager fileManager;
    private Holder holder;

    public Handler(Game game) {
        this.game = game;
        this.gameBoard = new GameBoard();
        this.mouseManager = new MouseManager();
        this.assets = new Assets();
        this.fileManager = new FileManager();
        this.holder = new Holder();
    }

    /**
     * Loads game either from FEN or from file
     */
    public void loadGame() {
        if (State.getState() != null && State.getState().canLoad()) {
            Holder temp = new Holder();
            if (fileManager.load(temp)) {
                holder = temp;
                // Load board -> Fen
                gameBoard.loadFen(holder.getLastFen());
                gameBoard.setRound(holder.getSize()-1);
                // Set text
                game.getDisplay().setText(holder.getText());
                State.getState().load(holder);
            }
        }
    }

    /**
     * Saves game into two files .txt and .png if
     * successful
     */
    public void saveGame() {
        if (State.getState() != null && State.getState().canSave()) {
            fileManager.setScreenShot(game.getGraphicsManager().getScreenShot());
            if (fileManager.save(holder)) {
                State.getState().save(holder);
            }
        } 
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
     * @return Holder
     */
    public Holder getHolder() {
        return holder;
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