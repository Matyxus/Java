package managers;
import board.GameBoard;
import board.Square;
//import components.FileChooser;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.PrintWriter;
import java.awt.image.BufferedImage;

public class FileManager {
    private final String absPath = new File("").getAbsolutePath() + "\\saves\\";
    private HashMap<Integer, Square> blackPiecesMap;
    private HashMap<Integer, Square> whitePiecesMap;
    private PrintWriter fstream;
    //private FileChooser fc = null;

    public FileManager(){};
    
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
        fstream.close();
    }

    public void clean() {
        this.whitePiecesMap.clear();
        this.blackPiecesMap.clear();
    }
    
    public String getAbsPath() {
        return absPath;
    }
}