package gui;

import managers.MouseManager;
import managers.UiManager;
import states.State;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.Rectangle;

import gameboard.constants.Colors;
import gameboard.constants.Board;
import gameboard.constants.bitboard.Ranks;
import gameboard.constants.bitboard.Files;

/**
 * Class handling all graphics & input related objects
 */
public class Gui {
    // Graphics
    private final Display display;
    private final MouseManager mouseManager;
    private final UiManager uiManager;
    private final Assets assets;
    private final Rectangle boardPosition;
    private Graphics g;

    public Gui(String title, int width, int height) {
        this.assets = new Assets();
        this.boardPosition = new Rectangle(0, 0, assets.getBoardWidth(), assets.getBoardHeight());
        this.uiManager = new UiManager();
        this.mouseManager = new MouseManager(this.uiManager);
        this.display = new Display(title, width, height, this.mouseManager);
    }

    /**
     * Handles rendering of objects in state and chess board
     * @param state current state
     * @return void
     */
    public void render(State state) {
        BufferStrategy bs = display.getCanvas().getBufferStrategy();
        // Add 3 buffers.
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        // Clear screen
        g.clearRect(0, 0, display.width, display.height);
        // Start drawing
        this.uiManager.render(g);
        if (state != null) {
            renderBoard(g, state);
            state.render(this);
        }
        // End drawing
        bs.show(); // display it
        g.dispose(); // "dispose"
        g = null;
    }

    /**
     * Renders board and pieces on it if possible
     * @param g Graphics object
     * @param state current state
     * @return void
     */
    public void renderBoard(Graphics g, State state) {
        // ChessGame is invalid
        if (state.getChessGame() == null) {
            return;
        }
        g.drawImage(assets.getBoard(), boardPosition.x, boardPosition.y, null);
        // Render pieces
        int[] whitePieces = state.getChessGame().getGameBoard().getPlayer(Colors.WHITE).getPlacedPieces();
        int[] blackPieces = state.getChessGame().getGameBoard().getPlayer(Colors.BLACK).getPlacedPieces();
        for (int square = 0; square < Board.BOARD_SIZE; square++) {
            int piece = whitePieces[square];
            int color = Colors.WHITE;
            if (blackPieces[square] != -1) {
                piece = blackPieces[square];
                color = Colors.BLACK;
            }
            if (piece != -1) {
                g.drawImage(
                    assets.getPieceImg(piece, color), // Image
                    assets.PIECE_WIDTH * Ranks.getRow(square),  // X
                    assets.PIECE_HEIGHT * Files.getColumn(square), // Y
                    null // Observer
                );
            }
        }

    }

    // ----------------------------- Getters -----------------------------

    public MouseManager getMouseManager() {
        return mouseManager;
    }

    public Display getDisplay() {
        return display;
    }

    public UiManager getUiManager() {
        return uiManager;
    }

    public Assets getAssets() {
        return assets;
    }

    public Graphics getGraphics() {
        return g;
    }

    public Rectangle getBoardPosition() {
        return boardPosition;
    }

    /**
     * @return square on board on which mouse pressed, "-1" if none.
     */
    public int getBoardSquare() {
        if (mouseManager.getCurrMouseEvent() == null) {
            return Board.INVALID_SQUARE;
        }
        int x = mouseManager.getCurrMouseEvent().getX();
        int y = mouseManager.getCurrMouseEvent().getY();
        if (boardPosition.contains(x, y)) {
            // Shift board to left corner (0, 0)
            x -= boardPosition.x;
            y -= boardPosition.y;
            // Center coordinates
            x = ((x + assets.PIECE_WIDTH) / (assets.PIECE_WIDTH+1))-1;
            y = ((y + assets.PIECE_HEIGHT) / (assets.PIECE_HEIGHT+1))-1;
            // Return square on board
            return Board.COLS * y + x;
        }
        return Board.INVALID_SQUARE;
    }
}
