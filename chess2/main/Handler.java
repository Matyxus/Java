package chess2.main;

import chess2.board.GameBoard;
import chess2.managers.MouseManager;

public class Handler {

    private final Game game;
    private final GameBoard gameBoard;

    public Handler(Game game){
        this.game = game;
        this.gameBoard = new GameBoard();
    }

    public MouseManager getMouseManager(){
        return this.game.getMouseManager();
    }

	public Game getGame() {
		return this.game;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }
}