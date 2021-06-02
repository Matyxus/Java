package chess2.states.ui;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class UIImageButton extends UIObject {
    
    private final int[] hRGB = {136, 182, 196}; // light blue color
    private final Color hoverColor = new Color(hRGB[0], hRGB[1], hRGB[2]);
    private final int[] cRGB = {50, 108, 125}; // dark blue color
    private final Color clickColor = new Color(cRGB[0], cRGB[1], cRGB[2]);
    
    public UIImageButton(float x, float y, int width, int height, BufferedImage image, int piece) {
        super(x, y, width, height, image, piece);
    }

    @Override
    public void tick() {};

    @Override
    public void render(Graphics g) {
        if(hovering && !(show)){
            g.setColor(hoverColor);
            g.fillRect((int) x, (int) y, width, height);
        }
        if (show){
            g.setColor(clickColor);
            g.fillRect((int) x, (int) y, width, height);
        }
        g.drawImage(image, (int) x, (int) y, width, height, null);
    }

    @Override
    public void onClick() {};
}