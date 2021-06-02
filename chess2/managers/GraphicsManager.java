package chess2.managers;

import chess2.images.Assets;
import chess2.main.Handler;
import chess2.PopUps;
import chess2.board.Spot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

public class GraphicsManager {
    private final FileManager fileManager;
    private final Handler handler;

    private BufferedImage bimage = null;

    public GraphicsManager(Handler handler) {
        this.fileManager = new FileManager();
        this.handler = handler;
        Assets.init();
    }

    public void render(Graphics g){
        g.drawImage(Assets.board, 0, 0, null);
        renderBoard(g);
    }

    public void renderBoard(Graphics g){
        Iterator<Map.Entry<Integer, Spot>> it;
        it = handler.getGameBoard().getWhitePieces().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Spot> pair = (Map.Entry<Integer, Spot>)it.next();
            g.drawImage(Assets.pieces[pair.getValue().getPiece()][pair.getValue().getColor()],
            Assets.PIECE_WIDTH*(pair.getKey()%8), Assets.PIECE_HEIGHT*(pair.getKey()/8), null);
        }

        it = handler.getGameBoard().getBlackPieces().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Spot> pair = (Map.Entry<Integer, Spot>)it.next();
            g.drawImage(Assets.pieces[pair.getValue().getPiece()][pair.getValue().getColor()],
            Assets.PIECE_WIDTH*(pair.getKey()%8), Assets.PIECE_HEIGHT*(pair.getKey()/8), null);
        }
    }

    public boolean load(){
        if (fileManager.loadFile(handler.getGameBoard())){
            handler.getGame().getDisplay().cleanText();
            handler.getGame().getDisplay().appendText(fileManager.getText());
            fileManager.clean();
            return true;
        }
        return false;
    }

    public void save(){
        updateScreenShot();
        String fileName = PopUps.fileName;
        if (bimage != null && fileName != null){
            File outputfile = new File(fileManager.getAbsPath()+fileName+".png");
            try {
                ImageIO.write(bimage, "png", outputfile);
                String text = handler.getGame().getDisplay().getText();
                fileManager.safeFile(fileName, text, handler.getGameBoard());
            } catch (IOException e) {
                PopUps.errorPopUP(e);
            }
        }
    }

    private void updateScreenShot(){
        if (handler.getGameBoard().canPlay()){//making sure game is playable
            bimage = new BufferedImage(Assets.BOARD_WIDTH, Assets.BOARD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics bGr = bimage.createGraphics();
            bGr.drawImage(Assets.board, 0, 0, null);
            renderBoard(bGr);
            bGr.dispose();
            PopUps.savePopUp();
        } else {
            bimage = null;
            PopUps.plainMessagePopUP("Game is not playable!");
        }
    }
    
}
