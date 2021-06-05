package ui;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class UIObject {

    protected float x, y;
    protected int width, height;
    protected Rectangle bounds;
    protected boolean hovering  = false;
    protected boolean show = false;
    protected int piece = -1;
    protected BufferedImage image;

    public UIObject(float x, float y, int width, int height, BufferedImage image, int piece) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.piece = piece;
        bounds = new Rectangle((int) x, (int) y, width, height);
    }

    public abstract void render(Graphics g);

    public abstract void onClick();

    public void onMouseMove(MouseEvent e) {
        hovering = bounds.contains(e.getX(), e.getY());
    }

    public void onMouseRelease(MouseEvent e) {
        show = bounds.contains(e.getX(), e.getY());
        if (show && e.getButton() == MouseEvent.BUTTON1) {
            onClick();
        }
    }
    
    // -------- Getters -------- 
    
    public boolean getShow(){
        return show;
    }

    public boolean isOccupied(){
        return piece != -1;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getPiece() {
        return piece;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }

    // -------- Setters -------- 

    public void setHeight(int height) {
        this.height = height;
    }
    
    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }
}