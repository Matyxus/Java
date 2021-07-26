package states;
import java.awt.Graphics;

import assets.Data;
import main.Handler;

public abstract class State implements Data {
    /**
     * Current running state, null if none is running
     */ 
    private static State currentState = null;
    protected Handler handler;
    
    public State(Handler handler) {
        this.handler = handler;
    }

    /**
     * Takes care of rendering objects in given state
     * @param g graphics rendered
     */
    public abstract void render(Graphics g);

    /** 
     *  Takes care of updating objects in given state 
     *  in response to mouse events
     */
    public abstract void update();

    /**
     * Serves mainly to communicate with state,
     * without user input (e.g. AI on another thread
     * finished finding move)
     */
    public abstract void tick();

    /**
     * Adds buttons specific to each state
     */
    protected abstract void addButtons();

    /**
     * @param state to be set
     */
    public static void setState(State state){
        currentState = state;
    }

    /**
     * @return current State, null if there is none
     */
    public static State getState(){
        return currentState;
    }
}
