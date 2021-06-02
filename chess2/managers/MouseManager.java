package chess2.managers;
import chess2.states.ui.UIManager;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class MouseManager implements MouseInputListener {

    private boolean leftClick;
    private boolean rightClick;
    private int mouseX, mouseY;
    private UIManager uiManager;


    public MouseManager() {};
    
    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    // Getter
    // 80 wide
    public int getCenteredMouseX() { 
        return ((mouseX+80)/81)-1;
    }
    // 60 height
    public int getCenteredMouseY() { 
        return ((mouseY+60)/61)-1;
    }

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

    // Implements
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
        if(e.getButton() == MouseEvent.BUTTON1){
            leftClick = true;
        }
        if(e.getButton() == MouseEvent.BUTTON3){
            rightClick = true;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1){
            leftClick = false;
        }

        if(e.getButton() == MouseEvent.BUTTON3){
            rightClick = false;
        }

        if(uiManager != null){
            uiManager.onMouseRelease(e);
        }
    }

    public void setLeftClick(boolean leftClick) {
        this.leftClick = leftClick;
    }

    public void setRightClick(boolean rightClick) {
        this.rightClick = rightClick;
    }

    @Override
    public void mouseClicked(MouseEvent e) {};

    @Override
    public void mouseEntered(MouseEvent e) {};

    @Override
    public void mouseExited(MouseEvent e) {};

    @Override
    public void mouseDragged(MouseEvent e) {};
    
}
