package components;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import java.io.File;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.geom.AffineTransform;

public class ImagePreview extends JComponent implements PropertyChangeListener {
    private static final long serialVersionUID = 1L;
    ImageIcon thumbnail = null;
    File file = null;

    /**
     * @param fc current active FileChooser
     */
    public ImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(320, 240));
        fc.addPropertyChangeListener(this);
    }

    /**
     * Loads image to be displayed from chess board image
     */
    public void loadImage() {
        if (file == null) {
            thumbnail = null;
            return;
        }

        ImageIcon tmpIcon = new ImageIcon(file.getPath());
        if (tmpIcon != null) {
            if (tmpIcon.getIconWidth() > 320) {
                Image img = tmpIcon.getImage();
                BufferedImage buffer = toBufferedImage(img);
                thumbnail = new ImageIcon(scale(buffer, BufferedImage.TYPE_INT_ARGB, 320, 240, 0.5, 0.5));
            } else { // no need to miniaturize
                thumbnail = tmpIcon;
            }
        }
    }

    /**
     * @param img image of type "Image"
     * @return BufferedImage from given img
     */
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    /**
     * @param img original image
     * @param imageType type of img
     * @param width of image
     * @param height of image
     * @param sx scale width
     * @param sy scale height
     * @return new scaled BufferedImage (using AffineTransofrm, recommended
     * scaling to be integers ex. 2x original)
     */
    private BufferedImage scale(BufferedImage img, int imageType, int width, int height, double sx,
            double sy) {
        BufferedImage scaled = null;
        if (img != null) {
            scaled = new BufferedImage(width, height, imageType);
            Graphics2D g = scaled.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(sx, sy);
            g.drawRenderedImage(img, at);
        }
        return scaled;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        boolean update = false;
        String prop = e.getPropertyName();

        // If the directory changed, don't show an image
        if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
            file = null;
            update = true;

            // If a file became selected, find out which one
        } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            update = true;
        }
        // Update the preview accordingly.
        if (update) {
            thumbnail = null;
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
            int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;

            if (y < 0) {
                y = 0;
            }

            if (x < 5) {
                x = 5;
            }
            thumbnail.paintIcon(this, g, x, y);
        }
    }
}
