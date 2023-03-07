package gui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

/**
 * Interface for classes that render some 
 * objects to screen and need updating
 */
public interface Renderer {
    
    /**
     * Draws images to current graphics object
     * @param g current Graphics object
     * @return void
     */
    public void render(Graphics g);

    /** 
     *  Takes care of updating objects in reponse to mouse event
     *  @param e current MouseEvent
     *  @return void
     */
    public void update(MouseEvent e);

}
