package managers;

import main.Handler;
import board.constants.Colors;
import board.constants.Files;
import board.constants.Ranks;
import board.constants.Size;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

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
        int[] whitePieces = handler.getGameBoard().getPieces(Colors.WHITE);
        int[] blackPieces = handler.getGameBoard().getPieces(Colors.BLACK);
        for (int square = 0; square < Size.BOARD_SIZE; square++) {
            int piece = whitePieces[square];
            int color = Colors.WHITE;
            if (blackPieces[square] != -1) {
                piece = blackPieces[square];
                color = Colors.BLACK;
            }
            if (piece != -1) {
                g.drawImage(
                    handler.getAssets().getPieceImg(piece, color), // Image
                    handler.getAssets().PIECE_WIDTH*Ranks.getRow(square),  // X
                    handler.getAssets().PIECE_HEIGHT*Files.getColumn(square), // Y
                    null // Observer
                );
            }
        }
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
