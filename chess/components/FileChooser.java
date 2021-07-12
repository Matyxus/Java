package components;
import java.io.File;
import javax.swing.JFileChooser;

public class FileChooser {
    private JFileChooser fc;
    private String fileName;

    public FileChooser(String path, boolean save) {
        fileName = null;
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
        int returnVal;
        // Save or load
        if (save) {
            returnVal = fc.showDialog(null, "Save");
        } else {
            returnVal = fc.showDialog(null, "Load");
        }
        // Process the results
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            fileName = file.getName();
            // User entered fen when loading
            if (!fileName.contains(".") && !save) {
                // Get fen from absolute path of file
                String tmp = file.getAbsolutePath();
                String[] arr = tmp.split("\\\\");
                // Fen has 8 parts
                if (arr.length >= 8) {
                    fileName = "";
                    for (int i = arr.length-8; i < arr.length-1; i++) {
                        fileName += arr[i] + "/";
                    }
                    // Dont add the last "/"
                    fileName += arr[arr.length-1];
                }
            }
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
