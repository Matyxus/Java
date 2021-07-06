package managers;
import board.Fen;
import board.GameBoard;
import board.Spot;
import components.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.swing.UIManager;

public class FileManager {
    private final String absPath = new File("").getAbsolutePath() + "\\chess\\saves\\";
    private final Fen fen = new Fen();
    private PrintWriter fstream;
    private FileChooser fc = null;

    public FileManager(){
        // Changes FileName in JFileChooser to FileName/FEN
        UIManager.put("FileChooser.fileNameLabelText", "FileName/FEN:");
    }

    public ArrayList<Spot> loadFile() {
        fc = new FileChooser(absPath);
        String result = fc.getFileName();
        // Possibly fen
        if (result != null) {
            if (!result.contains(".")) {
                System.out.println("Fen: " + result);
                return fen.interpret(result);
            } else {
                // Process file
            }
        }
        fc = null;
        return null;
    }
    
    public void safeFile(String fileName, BufferedImage img, GameBoard board) {
        final String path = absPath+fileName;
        File file = new File(path+".txt");
        File imgFile = new File(path+".png");
        try { 
            file.createNewFile();
            imgFile.createNewFile();
            ImageIO.write(img, "png", imgFile); // Save png
            fstream = new PrintWriter(path, "UTF-8");
            // Save game

        } catch (Exception e) {
            System.out.println(e);
        }
        if (fstream != null) {
            fstream.close();
        }
    }

    public String getAbsPath() {
        return absPath;
    }
}