package main;

import java.awt.image.BufferStrategy;
import java.awt.Graphics;
/*
import managers.GraphicsManager;
import managers.MouseManager;
import states.BoardPlacementState;
import states.GameState;
import states.State;
import states.ViewerState;
*/


public class Game {
    public final int width, height;
    public final String title;
    
    private boolean running = false;
    // screen
    private BufferStrategy bs;
    private Graphics g;
    public Game(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
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
        init();
        while (running) {
            now = System.nanoTime();
            System.out.println("In game loop");
            /*
            if (mouseManager.leftPressed() || mouseManager.rightPressed()) {// check for input
                update();
            }
            //necessary, game loop is too fast, reset mouse buttons
            mouseManager.setLeftClick(false);
            mouseManager.setRightClick(false);
            */
            after = System.nanoTime() - now;
            wait =  Math.abs((OPTIMAL_TIME - after) / 1000000);
            try {
                Thread.sleep(wait);
                render();
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    // init display
    private void init() {
        display = new Display(title, width, height);
        display.getFrame().addMouseListener(mouseManager);
        display.getFrame().addMouseMotionListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().addMouseMotionListener(mouseManager);
        //Either Frame or Canvas is active, so they will get the listener
        handler = new Handler(this);
        graphicsManager = new GraphicsManager(handler);
        BoardPlacementState = new BoardPlacementState(handler);
        State.setState(BoardPlacementState);
    }
}