package main;

import java.awt.image.BufferStrategy;
import java.awt.Graphics;

import states.PlacementState;
import states.State;
import managers.GraphicsManager;

public class Game {
    private final int width, height;
    private boolean running = false;
    // Screen
    private BufferStrategy bs;
    private Graphics g;
    private final Display display;
    private final GraphicsManager graphicsManager;
    // Board and mouse.
    private final Handler handler;


    public Game(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.handler = new Handler();
        this.graphicsManager = new GraphicsManager(handler);
        this.display = new Display(title, width, height, handler);
        State.setState(new PlacementState(handler)); // Initial state is PlacementState.
    }

    /**
     * Renders the state and chess board with pieces.
     */
    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        // Add 3 buffers.
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        // Clear screen
        g.clearRect(0, 0, width, height);
        // Start drawing
        graphicsManager.render(g);
        if (State.getState() != null) {
            State.getState().render(g);
        }
        // End drawing
        bs.show(); // display it
        g.dispose(); // "dispose"
    }

    /**
     * Updates current state in response to mouse events.
     */
    private void update() {
        if (State.getState() != null) {
            State.getState().update();
        }
    }

    /**
     * Starts the game.
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
            // Check if input happened.
            if (handler.getMouseManager().leftPressed() || handler.getMouseManager().rightPressed()) {
                update();
            }
            // Necessary, game loop is too fast, reset mouse buttons
            handler.getMouseManager().setLeftClick(false);
            handler.getMouseManager().setRightClick(false);
            // Calculate if previous waiting was correct.
            after = System.nanoTime() - now;
            wait = Math.abs((OPTIMAL_TIME - after) / 1000000);
            try {
                render();
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }
}