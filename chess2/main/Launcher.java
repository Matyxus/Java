package chess2.main;


public class Launcher {
    private static final int GAME_WIDTH = 800;
    private static final int GAME_HEIGHT = 480;
    
    public static void main(String[] args) {    
        Game game = new Game("Chess", GAME_WIDTH, GAME_HEIGHT);
        game.start();
    }
}
