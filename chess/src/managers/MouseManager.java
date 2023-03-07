package managers;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class MouseManager implements MouseInputListener {

    /**
     * Pointer to buttons, since MouseManager runs on its own thread,
     * its independent of game FPS
     */
    private final UiManager uiManager;
    private boolean leftClick, rightClick;
    private MouseEvent currMouseEvent;
    
    public MouseManager(UiManager uiManager) {
        this.uiManager = uiManager;
    }

    // -------------------------------- Mouse --------------------------------

    @Override
    public void mouseMoved(MouseEvent e) {
        currMouseEvent = e;
        if (uiManager != null) {
            uiManager.onMouseMove(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        leftClick = (e.getButton() == MouseEvent.BUTTON1);
        rightClick = (e.getButton() == MouseEvent.BUTTON3);
        currMouseEvent = e;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        leftClick = false;
        rightClick = false;
        currMouseEvent = e;
        if (uiManager != null) {
            uiManager.onMouseRelease(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {};

    @Override
    public void mouseEntered(MouseEvent e) {};

    @Override
    public void mouseExited(MouseEvent e) {};

    @Override
    public void mouseDragged(MouseEvent e) {};

    // -------------------------------- Getters --------------------------------

    /**
     * @return true if right mouse button was pressed, false otherwise
     */
    public boolean rightPressed() {
        return rightClick;
    }

    /**
     * @return true if left mouse button was pressed, false otherwise
     */
    public boolean leftPressed() {
        return leftClick;
    }

    /**
     * @return current mouse event
     */
    public MouseEvent getCurrMouseEvent() {
        return currMouseEvent;
    }

    // -------------------------------- Setters --------------------------------

    /**
     * Since gameloop is too fast, we need to reset mouse click
     * @param leftClick true or false value
     */
    public void setLeftClick(boolean leftClick) {
        this.leftClick = leftClick;
    }

    /**
     * Since gameloop is too fast, we need to reset mouse click
     * @param rightClick true or false value
     */
    public void setRightClick(boolean rightClick) {
        this.rightClick = rightClick;
    }
}
