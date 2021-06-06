package managers;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class MouseManager implements MouseInputListener {

    private boolean leftClick, rightClick;
    private int mouseX, mouseY;
    private UIManager uiManager;
    
    public MouseManager() {};

    // ------ Implements ------

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if(uiManager != null){
            uiManager.onMouseMove(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        leftClick = (e.getButton() == MouseEvent.BUTTON1);
        rightClick = (e.getButton() == MouseEvent.BUTTON3);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        leftClick = false;
        rightClick = false;
        if(uiManager != null) {
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

    // ------ Getters ------ 

    public boolean rightPressed() {
        return rightClick;
    }

    public boolean leftPressed() {
        return leftClick;
    }

    public int getMouseX() {
        return mouseX;
    }
    
    public int getMouseY() {
        return mouseY;
    }

    // ------ Setters ------ 

    public void setLeftClick(boolean leftClick) {
        this.leftClick = leftClick;
    }

    public void setRightClick(boolean rightClick) {
        this.rightClick = rightClick;
    }

    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }
    
    
}
