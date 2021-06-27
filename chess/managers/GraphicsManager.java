package managers;

import main.Handler;
//import PopUps;
//import board.Spot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
/*
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.imageio.ImageIO;
*/
import java.util.HashMap;

import board.Spot;
import board.constants.Colors;


public class GraphicsManager {
    private final FileManager fileManager;
    private final Handler handler;

    public GraphicsManager(Handler handler) {
        this.fileManager = new FileManager();
        this.handler = handler;
    }

    /**
     * @param g Grapghics
     * @apiNote
     * Renders chess board and all pieces on it.
     */
    public void render(Graphics g) {
        g.drawImage(handler.getAssets().getBoard(), 0, 0, null);
        renderPieces(g);
    }

    /**
     * @param g Grapghics
     * Renders pieces on board.
     */
    private void renderPieces(Graphics g) {
        // White pieces.
        HashMap<Integer, Spot> curr = handler.getGameBoard().getWhitePieces();
        curr.forEach((index, square) -> {
            g.drawImage(handler.getAssets().getPieceImg(square.getPiece(), Colors.WHITE),
            handler.getAssets().PIECE_WIDTH*(index%8), handler.getAssets().PIECE_HEIGHT*(index/8), null);
        });
        // Black pieces.
        curr = handler.getGameBoard().getBlackPieces();
        curr.forEach((index, square) -> {
            g.drawImage(handler.getAssets().getPieceImg(square.getPiece(), Colors.BLACK),
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
        BufferedImage bimage = getScreenShot();
        String fileName = "";//PopUps.fileName;
        if (bimage != null && fileName != null) {
            fileManager.safeFile(fileName, bimage, handler.getGameBoard());
        }
    }
    
    /**
     * @return BufferedImage with board and pieces on it
     */
    private BufferedImage getScreenShot() {
        BufferedImage bimage = new BufferedImage 
        (
            handler.getAssets().getBoardWidth(),   // width
            handler.getAssets().getBoardHeight(),  // height
            BufferedImage.TYPE_INT_ARGB            // image type
        );
        Graphics bGr = bimage.createGraphics();
        bGr.drawImage(handler.getAssets().getBoard(), 0, 0, null); // Render board
        renderPieces(bGr); // Render pieces
        bGr.dispose();
        return bimage;
    }
    
    
}
