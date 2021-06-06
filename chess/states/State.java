package states;
import java.awt.Graphics;
import main.Handler;

public abstract class State {
    // Current running state.
    private static State currentState = null;
    // Handler to manage mouse and carry gameboard.
    protected Handler handler;
    
    public State(Handler handler) {
        this.handler = handler;
    }

    /** 
       Takes care of rendering objects in given state.
    */
    public abstract void render(Graphics g);

    /** 
        Takes care of updating objects in given state,
        in response to mouse events.
    */
    public abstract void update();

    public static void setState(State state){
        currentState = state;
    }

    public static State getState(){
        return currentState;
    }
}
