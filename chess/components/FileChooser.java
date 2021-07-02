package components;
import java.io.File;
import javax.swing.JFileChooser;


public class FileChooser {
    private JFileChooser fc;
    private String fileName = null;

    public FileChooser(String path) {
        // Set up the file chooser
        fc = new JFileChooser();
        // Add a custom file filter and disable the default
        fc.setCurrentDirectory(new File(path));
        fc.addChoosableFileFilter(new ImageFilter());
        fc.setAcceptAllFileFilterUsed(false);
        // Add custom icons for file types
        fc.setFileView(new ImageFileView());
        // Add the preview pane
        fc.setAccessory(new ImagePreview(fc));
        // Show it
        int returnVal = fc.showDialog(null,"Load");
        // Process the results
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fileName = file.getName();
        } else if (returnVal == JFileChooser.CANCEL_OPTION) {
            System.out.println("Canceled FileChooser");
        }
        // Reset the file chooser for the next time it's shown
        fc.setSelectedFile(null);
        fc = null;
    }

    public String getFileName() {
        return fileName;
    }
}
