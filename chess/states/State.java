package states;
import java.awt.Graphics;
import main.Handler;

public abstract class State {
    // Current running state
    private static State currentState = null;
    // Handler to manage mouse and carry gameboard
    protected Handler handler;
    
    public State(Handler handler) {
        this.handler = handler;
    }

    /** 
       Takes care of rendering objects in given state
    */
    public abstract void render(Graphics g);

    /** 
        Takes care of updating objects in given state,
        response to mouse events
    */
    public abstract void update();

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
