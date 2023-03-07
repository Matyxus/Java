package main;

import gui.Gui;
import gameboard.ChessGame;
// States
import states.State;
import states.PlacementState;

public class Game {
    private boolean running = false;
    private final Gui gui;
    private final ChessGame chessGame;

    public Game(String title, int width, int height) {
        this.gui = new Gui(title, width, height);
        this.chessGame = new ChessGame();
        State.setState(new PlacementState(gui, chessGame));
    }

    /**
     * Starts the main game loop
     * @return void
     */
    public void run() {
        // 60 fps setup.
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long after;
        long now;
        long wait;
        running = true;
        while (running) {
            now = System.nanoTime();
            // Check if input happened, update state
            if (gui.getMouseManager().leftPressed() || gui.getMouseManager().rightPressed()) {
                State.getState().update(gui.getMouseManager().getCurrMouseEvent(), gui.getBoardSquare());
            }
            // Necessary, game loop is too fast, reset mouse buttons
            gui.getMouseManager().setLeftClick(false);
            gui.getMouseManager().setRightClick(false);
            // Render gameboard and current state
            gui.render(State.getState());
            State.getState().tick();
            // Calculate waiting time
            after = System.nanoTime() - now;
            wait = Math.abs((OPTIMAL_TIME - after) / 1000000);
            try { // Wait the rest of the time to achieve 60 FPS
                Thread.sleep(wait);
            } catch (InterruptedException e) { // Error
                e.printStackTrace();
                running = false;
            }
        }
    }
}
