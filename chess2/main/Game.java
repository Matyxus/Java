package chess2.main;

import java.awt.image.BufferStrategy;
import java.awt.Graphics;

import chess2.managers.GraphicsManager;
import chess2.managers.MouseManager;
import chess2.states.BoardPlacementState;
import chess2.states.GameState;
import chess2.states.State;
import chess2.states.ViewerState;

public class Game implements Runnable {
    public final int width, height;
    public final String title;
    //thread
    private boolean running = false;
    private Thread thread;
    // screen
    private BufferStrategy bs;
    private Graphics g;
    private Display display;
    private GraphicsManager graphicsManager;
    // Input
    private final MouseManager mouseManager;
    // States
    public State gameState;
    public State BoardPlacementState;
    public State viewerState;

    private Handler handler;
    
    public Game(String title, int width, int height) {
        this.width = width;
        this.height = height;
        this.title = title;
        mouseManager = new MouseManager();
    }

    private void update() {
        if (State.getState() != null) {
            State.getState().tick();
        }
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
    @Override
    public void run() {
        // 60 fps setup
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long after;
        long now;
        long wait;
        init();
        while (running) {// other solutions takes too much CPU to get exact 61fps (using system.nano...)
            now = System.nanoTime();
            if (mouseManager.leftPressed() || mouseManager.rightPressed()) {// check for input
                update();
            }
            //necessary, game loop is too fast, reset mouse buttons
            mouseManager.setLeftClick(false);
            mouseManager.setRightClick(false);
            after = System.nanoTime() - now;
            wait = (OPTIMAL_TIME - after) / 1000000;
            wait = (wait >= 0 ) ? wait:-wait; // negative vaule in case of wait
            try {
                Thread.sleep(wait);
                render();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } 
        stop();
    }

    public MouseManager getMouseManager(){
        return mouseManager;
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

    // init thread, and the whole game
    public synchronized void start() {
        if(running){
            return;
        }
        running = true;
        thread = new Thread(this); // this game class
        thread.start();
    }

    public synchronized void stop() { // End thread
        if(!(running)){
            return;
        }
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //GETTERS && SETTERS
    public Display getDisplay() {
        return display;
    }

    public GraphicsManager getGraphicsManager() {
        return graphicsManager;
    }

    public void startViewerState(){
        this.viewerState = new ViewerState(handler);
    }
    
    public void startGameState() {
        this.gameState = new GameState(handler);
    }

    public void startBoardPlacementState(){
        this.BoardPlacementState = new BoardPlacementState(handler);
    }
}