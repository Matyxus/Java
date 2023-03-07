package main;

/**
 * Class that launches the game
 */
public class Launcher {
    private static final int GAME_WIDTH = 1280;
    private static final int GAME_HEIGHT = 720;
    private static final String TITLE = "ChessMaster";
    
    public static void main(String[] args) { 
        Game game = new Game(TITLE, GAME_WIDTH, GAME_HEIGHT);
        game.run();
    }
}
