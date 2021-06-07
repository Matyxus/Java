package ui;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class UIObject {

    protected final Rectangle bounds;
    protected final BufferedImage[] images;
    protected Object object;
    protected boolean hovering  = false;
    
    public UIObject(int x, int y, int width, int height, BufferedImage imageUP, BufferedImage imageDOWN) {
        this.images = new BufferedImage[2];
        this.images[0] = imageUP;
        this.images[1] = imageDOWN;
        this.bounds = new Rectangle((int) x, (int) y, width, height);
    }

    public abstract void render(Graphics g);

    public abstract void onClick();

    /**
     * @param e mouse event
     * checks if mouse is hovering over UIObject
     */
    public void onMouseMove(MouseEvent e) {
        hovering = bounds.contains(e.getX(), e.getY());
    }

    /**
     * @param e mouse event
     * Checks if mouse was released on object while hovering, acts as mouse click.
     */
    public void onMouseRelease(MouseEvent e) {
        if (hovering && e.getButton() == MouseEvent.BUTTON1) {
            onClick();
        }
    }

    /**
     * @param object
     * Sets some additional object that may be needed on mouse click etc.
     */
    public void setObject(Object object) {
        this.object = object;
    }
    
    // -------- Getters -------- 
    
    /**
     * @param up 0 if image when mouse is not hovering, 1 otherwise.
     * @return the selected image.
     */
    public BufferedImage getImage(int up) {
        return images[up];
    }

    /**
     * @return bounds of UIObject
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
}