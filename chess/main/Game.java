package main;

import java.awt.image.BufferStrategy;
import java.awt.Graphics;

import states.State;
import managers.GraphicsManager;
/*
import states.BoardPlacementState;
import states.GameState;
import states.ViewerState;
*/

public class Game {
    private int width, height;
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
    }

    // draw to screen using Buffer
    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3); // 3 buffers(hidden screens)
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

    // init game + update it accordingly, gets called when start is called
    public void run() {
        // 60 fps setup
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
                System.out.println("Clicked on coord: " + handler.getMouseManager().getMouseX() + "  " +
                    handler.getMouseManager().getMouseY());
                //update();
            }
            //necessary, game loop is too fast, reset mouse buttons
            handler.getMouseManager().setLeftClick(false);
            handler.getMouseManager().setRightClick(false);
            
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