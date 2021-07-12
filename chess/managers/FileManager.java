package managers;
import board.Fen;
import board.GameBoard;
import board.Spot;
import board.constants.Img;
import components.FileChooser;

import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.swing.UIManager;

public class FileManager {
    private final Fen fen = new Fen();
    private String textHistory = "";
    private FileChooser fc = null;

    public FileManager(){};

    /**
     * @return
     * @throws IOException
     */
    public ArrayList<Spot> loadFile() throws IOException {
        textHistory = "";
        // Changes FileName in JFileChooser to "FileName/FEN:"
        UIManager.put("FileChooser.fileNameLabelText", "FileName/FEN:");
        fc = new FileChooser(Img.SAVE_PATH, false);
        String result = fc.getFileName();
        ArrayList<Spot> loadedPieces = null;
        // Possibly fen
        if (result != null) {
            if (!result.contains(".")) {
                System.out.println("Fen: " + result);
                return fen.interpret(result);
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
                        textHistory += (strLine + "\n");
                    } else { // Fen
                        loadedPieces = fen.interpret(strLine);
                    }
                }
                // Close streams
                br.close();
                fstream.close();
            }
        }
        fc = null;
        return loadedPieces;
    }
    
    /**
     * @param textHistory
     * @param img
     * @param gameBoard
     */
    public void safeFile(String textHistory, BufferedImage img, GameBoard gameBoard) {
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
            String currentFen = fen.createFen(gameBoard);
            // Save game
            fstream.print(textHistory);
            fstream.print(currentFen);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (fstream != null) {
            fstream.close();
        }
        System.out.println("Saved succesfully");
    }

    /**
     * @return history of loaded game in String format
     */
    public String getTextHistory() {
        return textHistory;
    }
}