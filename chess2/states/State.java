package chess2.states;
import java.awt.Graphics;
import chess2.main.Handler;

public abstract class State {

    private static State currentState = null;

    public static void setState(State state){
        currentState = state;
    }

    public static State getState(){
        return currentState;
    }

    protected Handler handler;
    
    public State(Handler handler){
        this.handler = handler;
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public abstract void saveGame();

    public abstract void loadGame();
}
