package managers;

import main.Handler;
import board.Spot;
import board.constants.Colors;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GraphicsManager {
    private final Handler handler;

    public GraphicsManager(Handler handler) {
        this.handler = handler;
    }

    /**
     * Renders chess board and all pieces on it
     * @param g Graphics
     */
    public void render(Graphics g) {
        g.drawImage(handler.getAssets().getBoard(), 0, 0, null);
        renderPieces(g);
    }

    /**
     * Renders pieces on board
     * @param g Graphics
     */
    private void renderPieces(Graphics g) {
        // White pieces.
        HashMap<Integer, Spot> curr = handler.getGameBoard().getPieces(Colors.WHITE);
        curr.forEach((index, square) -> {
            g.drawImage(
                handler.getAssets().getPieceImg(square.getPiece(), Colors.WHITE), // Image
                handler.getAssets().PIECE_WIDTH*(index%8),  // X
                handler.getAssets().PIECE_HEIGHT*(index/8), // Y
                null // Observer
            );
        });
        // Black pieces.
        curr = handler.getGameBoard().getPieces(Colors.BLACK);
        curr.forEach((index, square) -> {
            g.drawImage(
                handler.getAssets().getPieceImg(square.getPiece(), Colors.BLACK), // Image
                handler.getAssets().PIECE_WIDTH*(index%8),  // X 
                handler.getAssets().PIECE_HEIGHT*(index/8), // Y
                null // Observer
            );
        });
    }

    /**
     * @return BufferedImage with board and pieces on it
     */
    public BufferedImage getScreenShot() {
        BufferedImage bimage = new BufferedImage 
        (
            handler.getAssets().getBoardWidth(),   // Width
            handler.getAssets().getBoardHeight(),  // Height
            BufferedImage.TYPE_INT_ARGB            // Image type
        );
        Graphics bGr = bimage.createGraphics();
        render(bGr);
        bGr.dispose();
        return bimage;
    }
}
