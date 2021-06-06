package managers;

import main.Handler;
//import PopUps;
//import board.Spot;

import java.awt.Graphics;
//import java.awt.image.BufferedImage;
/*
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
*/
import java.util.HashMap;

import board.Square;
import board.constants.Colors;
import board.constants.Pieces;


public class GraphicsManager {
    //private final FileManager fileManager;
    private final Handler handler;

    public GraphicsManager(Handler handler) {
        //this.fileManager = new FileManager();
        this.handler = handler;
    }

    /**
     * @param g Grapghics
     * @apiNote
     * Renders chess board and all pieces on it.
     */
    public void render(Graphics g) {
        g.drawImage(handler.getAssets().getBoard(), 0, 0, null);
        // Pieces.
        HashMap<Integer, Square> curr = handler.getGameBoard().getPlayers(Colors.WHITE).getPlacedPieces();
        // White pieces.
        curr.forEach((index, square) -> {
            g.drawImage(handler.getAssets().getPieceImg(Pieces.values()[square.getPiece()], Colors.WHITE),
            handler.getAssets().PIECE_WIDTH*(index%8), handler.getAssets().PIECE_HEIGHT*(index/8), null);
        });

        curr = handler.getGameBoard().getPlayers(Colors.BLACK).getPlacedPieces();
        // Black pieces.
        curr.forEach((index, square) -> {
            g.drawImage(handler.getAssets().getPieceImg(Pieces.values()[square.getPiece()], Colors.BLACK),
            handler.getAssets().PIECE_WIDTH*(index%8), handler.getAssets().PIECE_HEIGHT*(index/8), null);
        });
    }

    public boolean load() {
        /*
        if (fileManager.loadFile(handler.getGameBoard())){
            handler.getGame().getDisplay().cleanText();
            handler.getGame().getDisplay().appendText(fileManager.getText());
            fileManager.clean();
            return true;
        }
        */
        return false;
    }

    public void save() {
        /*
        BufferedImage bimage = getScreenShot();
        String fileName = "";//PopUps.fileName;
        if (bimage != null && fileName != null){
            File outputfile = new File(fileManager.getAbsPath()+fileName+".png");
            try {
                ImageIO.write(bimage, "png", outputfile);
                //String text = handler.getGame().getDisplay().getText();
                //fileManager.safeFile(fileName, text, handler.getGameBoard());
            } catch (IOException e) {
                //PopUps.errorPopUP(e);
                System.out.println(e);
            }
        }
        */
    }
    /*
    private BufferedImage getScreenShot() {
        BufferedImage bimage = null;
        
        if (handler.getGameBoard().canPlay()){ // making sure game is playable
            bimage = new BufferedImage(handler.getAssets().BOARD_WIDTH, 
                handler.getAssets().BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics bGr = bimage.createGraphics();
            bGr.drawImage(handler.getAssets().board, 0, 0, null);
            renderBoard(bGr);
            bGr.dispose();
            //PopUps.savePopUp();
        } else {
            //PopUps.plainMessagePopUP("Game is not playable!");
        }
        
        return bimage;
    }
    */
    
}
