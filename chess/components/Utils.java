package components;

import java.io.File;
import javax.swing.ImageIcon;


public class Utils {
    protected final static String png = "png";

    /**
     * @param f name of file
     * @return extension of file if it exists, null otherwise
     */
    protected static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    /**
     * @param path to file
     * @return ImageIcon from file if exists, null otherwise
     */
    protected static ImageIcon createImageIcon(String path) {
        ImageIcon tmp = null;
        try {
           tmp = new ImageIcon(path);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return tmp;
    }
}
