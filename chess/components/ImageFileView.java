package components;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;

import board.constants.Img;


public class ImageFileView extends FileView {
    // Icon for chess files
    private final ImageIcon pngIcon = Utils.createImageIcon(
        Img.IMAGE_PATH + "chess_icon.png"
    );

    /**
     * @param f file
     * @return Icon for file if its ".png", null otherwise
     */
    public Icon getIcon(File f) {
        String extension = Utils.getExtension(f);
        Icon icon = null;
        if (extension != null) {
            if (extension.equals(Utils.png)) {
                icon = pngIcon;
            }
        }
        return icon;
    }
}
