package components;

import java.io.File;
import javax.swing.filechooser.FileFilter;


public class ImageFilter extends FileFilter {
    
    /**
     * @return true if file is directory or
     * if file extension is .png, false otherwise
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            return extension.equals(Utils.png);
        }
        return false;
    }

    /**
     * @return "ChessBoard Images"
     */
    public String getDescription() {
        return "ChessBoard Images";
    }
}
