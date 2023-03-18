package states;
import gui.Gui;
import java.awt.event.MouseEvent;

import gameboard.ChessEngine;

/**
 * Abstract class representing current state in game
 * 
 */
public abstract class State {
    /**
     * Pointer to chess game
     */
    protected final ChessEngine chessGame;
     /**
     * Current running state, null if none is running
     */ 
    private static State currentState = null;
    
    public State(Gui gui, ChessEngine chessGame) {
        this.chessGame = chessGame;
        gui.getUiManager().clear();
        addButtons(gui);
    }   

    /**
     * Renders objects in current state
     * @param gui of program
     * @return void
     */
    public abstract void render(Gui gui);


    /** 
     *  Takes care of updating objects in reponse to mouse event
     *  @param e current MouseEvent
     *  @param boardSquare on which mouse was pressed, "-1" if none
     *  @return void
     */
    public abstract void update(MouseEvent e, int boardSquare);

    /**
     * Serves mainly to communicate with state,
     * without user input (e.g. AI on another thread
     * finished finding move)
     * @return void
     */
    public abstract void tick();

    /**
     * Adds buttons specific to each state
     * @return void
     */
    protected abstract void addButtons(Gui gui);

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

    /**
     * @return ChessGame in current state
     */
    public ChessEngine getChessGame() {
        return chessGame;
    }

}
