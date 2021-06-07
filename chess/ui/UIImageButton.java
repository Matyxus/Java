package ui;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class UIImageButton extends UIObject {
    
    
    public UIImageButton(int x, int y, int width, int height, BufferedImage imageUP, BufferedImage imageDOWN) {
        super(x, y, width, height, imageUP, imageDOWN);
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(images[hovering ? 1:0], bounds.x, bounds.y, bounds.width, bounds.height, null);
    }

    @Override
    public void onClick() {};
}