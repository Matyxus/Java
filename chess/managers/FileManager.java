package managers;
import board.constants.Img;
import assets.Data;
import components.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileManager implements Data {
    private FileChooser fc = null;
    private BufferedImage screenShot = null;

    public FileManager(){};

    @Override
    public boolean save(Holder holder) {
        // Changes FileName in JFileChooser to "FileName:"
        UIManager.put("FileChooser.fileNameLabelText", "FileName:");
        // Ask user for file name
        fc = new FileChooser(Img.SAVE_PATH, true);
        String fileName = fc.getFileName();
        System.out.println("Filename to save: " +fileName);
        fc = null;
        // User didnt enter any name or canceled fileChooser,
        // or screenshot of game was not set
        if (fileName == null || screenShot == null) {
            return false;
        }
        final String path = Img.SAVE_PATH+fileName;
        PrintWriter fstream = null;
        File file = new File(path+".txt");
        File imgFile = new File(path+".png");
        try { 
            file.createNewFile();
            imgFile.createNewFile();
            ImageIO.write(screenShot, "png", imgFile); // Save png
            fstream = new PrintWriter(file.getAbsolutePath(), "UTF-8");
            // Save game
            fstream.print(holder);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        if (fstream != null) {
            fstream.close();
        }
        System.out.println("Saved succesfully");
        screenShot = null;
        return true;
    }

    @Override
    public boolean load(Holder holder) {
        // Changes FileName in JFileChooser to "FileName/FEN:"
        UIManager.put("FileChooser.fileNameLabelText", "FileName/FEN:");
        fc = new FileChooser(Img.SAVE_PATH, false);
        String result = fc.getFileName();
        fc = null;
        if (result == null) {
            return false;
        }
        holder.clear();
        // Fen
        if (!result.contains(".")) {
            System.out.println("Fen: " + result);
            holder.appendFen(result);
        // File
        } else if (result.contains(".png")) {
            try {
                // Get the actual file contaning data
                result = result.replace(".png", ".txt");
                // Open streams
                FileInputStream fstream = new FileInputStream(Img.SAVE_PATH + result);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String temp = "";
                String strLine = null;
                // Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    System.out.println("Line: " + strLine);
                    // Add new line at end
                    strLine += "\n";
                    // Fen history of game
                    if (strLine.contains("/")) {
                        holder.appendFen(strLine);
                    } else if (strLine.contains("Round")){ // Text history
                        if (temp.contains("Round")) {
                            holder.appendText(temp);
                            temp = "";
                        }
                        temp += strLine;
                    } else {
                        temp += strLine;
                    }
                }
                // Check if there is computer player
                if (holder.getSize() != 0) {
                    holder.setAI(
                        holder.getTextHistory().get(0).contains("Computer")
                    );
                }
                // Close streams
                br.close();
                fstream.close();
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }   
        }
        return true;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    public void setScreenShot(BufferedImage screenShot) {
        this.screenShot = screenShot;
    }
}