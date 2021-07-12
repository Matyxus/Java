package managers;
import board.constants.Img;
import assets.Pair;
import components.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileManager {
    private FileChooser fc = null;

    public FileManager(){};

    /**
     * @return Pair class, where key is fen, second is history of game
     * in text format, both can be empty
     * @throws IOException
     */
    public Pair<String, String> loadFile() throws IOException {
        String history = "";
        String fen = "";
        // Changes FileName in JFileChooser to "FileName/FEN:"
        UIManager.put("FileChooser.fileNameLabelText", "FileName/FEN:");
        fc = new FileChooser(Img.SAVE_PATH, false);
        String result = fc.getFileName();
        // Possibly fen
        if (result != null) {
            if (!result.contains(".")) {
                System.out.println("Fen: " + result);
                fen = result;
            } else if (result.contains(".png")) { // Process file
                result = result.replace(".png", ".txt");
                // Open streams
                FileInputStream fstream = new FileInputStream(Img.SAVE_PATH + result);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine = null;
                // Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    System.out.println("Line: " + strLine);
                    // Text history of game
                    if (!strLine.contains("/")) {
                        history += (strLine + "\n");
                    } else { // Fen
                        fen = strLine;
                    }
                }
                // Close streams
                br.close();
                fstream.close();
            }
        }
        fc = null;
        return new Pair<String, String>(fen, history);
    }
    
    /**
     * @param textHistory of game
     * @param img of current board with pieces on it
     * @param fen of current position
     */
    public void safeFile(String textHistory, BufferedImage img, String fen) {
        // Changes FileName in JFileChooser to "FileName:"
        UIManager.put("FileChooser.fileNameLabelText", "FileName:");
        // Ask user for file name
        fc = new FileChooser(Img.SAVE_PATH, true);
        String fileName = fc.getFileName();
        System.out.println("Filename to save: " +fileName);
        fc = null;
        // User didnt enter any name or canceled fileChooser
        if (fileName == null) {
            return;
        }
        final String path = Img.SAVE_PATH+fileName;
        PrintWriter fstream = null;
        File file = new File(path+".txt");
        File imgFile = new File(path+".png");
        try { 
            file.createNewFile();
            imgFile.createNewFile();
            ImageIO.write(img, "png", imgFile); // Save png
            fstream = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            // Save game
            fstream.print(textHistory);
            fstream.print(fen);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (fstream != null) {
            fstream.close();
        }
        System.out.println("Saved succesfully");
    }
}