package states;

import gui.Gui;
import ui.ImageButton;
import gameboard.ChessGame;
import gameboard.constants.Colors;
import gameboard.constants.Pieces;
import utils.ImageConst;
import utils.Pair;

import java.awt.event.MouseEvent;

/**
 * Initial state, handles placement of pieces on board,
 * provides buttos to switch to GameState or ReplayState
 */
public class PlacementState extends State {

    private int displayPiece = -1;
    private int displayColor = -1;
    private boolean dragged = false;

    public PlacementState(Gui gui, ChessGame chessGame) {
        super(gui, chessGame);
        System.out.println("Initialized PlacementState");
    }

    @Override
    public void render(Gui gui) {
        // Render piece, which user clicked, on cursor
        if (displayPiece != -1) {
            gui.getGraphics().drawImage(
                gui.getAssets().getPieceImg(displayPiece, displayColor), 
                gui.getMouseManager().getCurrMouseEvent().getX() - (gui.getAssets().PIECE_WIDTH / 2),  // X
                gui.getMouseManager().getCurrMouseEvent().getY() - (gui.getAssets().PIECE_HEIGHT / 2), // Y
                null
            ); 
        }
    }

    @Override
    public void update(MouseEvent e, int boardSquare) {
        // ----------------- Left click  -----------------  
        if (e.getButton() == MouseEvent.BUTTON1) {
            // Clicked on chess board
            if (boardSquare != -1) {
                // Place down displayed piece
                if (displayPiece != -1) { 
                    chessGame.getGameBoard().addPiece(displayPiece, displayColor, boardSquare);
                    // Stop dragging piece
                    if (dragged) {
                        set_piece(-1, -1);
                    }
                // Pickup piece from board -> dragging
                } else if (chessGame.getGameBoard().containsPiece(boardSquare) != null) {
                    Pair<Integer, Integer> target = chessGame.getGameBoard().containsPiece(boardSquare);
                    set_piece(target.getKey(), target.getValue());
                    chessGame.getGameBoard().removePiece(displayColor, boardSquare);
                    dragged = true;
                }
            } else {
                // Stop showing img
                set_piece(-1, -1);
            }
        }
        // ----------------- Right click  -----------------  
        if (e.getButton() == MouseEvent.BUTTON3) {
            // Stop rendering piece image on mouse
            if (displayPiece != -1) {
                set_piece(-1, -1);
            // Remove piece on board
            } else if (chessGame.getGameBoard().containsPiece(boardSquare) != null) {
                chessGame.getGameBoard().removePiece(chessGame.getGameBoard().containsPiece(boardSquare).getValue(), boardSquare);
            }
        }
    }

    @Override
    public void tick() {};

    @Override
    protected void addButtons(Gui gui) {
        // Add buttons showing pieces of both colors, for user to configure their own board
        int y = 0;
        int[] x = {gui.getAssets().getBoardWidth(), gui.getAssets().getBoardWidth() + gui.getAssets().PIECE_WIDTH};
        for (int color: Colors.COLORS) {
            for (int piece : Pieces.ALL_PIECES) {
                y = gui.getAssets().PIECE_HEIGHT * piece;
                // White pieces.
                gui.getUiManager().addObject(new ImageButton(
                    x[color], y, 
                    gui.getAssets().PIECE_WIDTH, gui.getAssets().PIECE_HEIGHT, 
                    gui.getAssets().getPieceImg(piece, color), null
                    ) {
                    @Override
                    public void onClick() {
                        set_piece(piece, color);
                    }
                });
            }
        }
        y += gui.getAssets().PIECE_HEIGHT;
        // Button to switch to GameState
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth(), y,          // X, Y
                2 * gui.getAssets().PIECE_WIDTH, gui.getAssets().PIECE_HEIGHT, // Width, Height
                gui.getAssets().getStartButton(ImageConst.IMG_UP),  // Idle image
                gui.getAssets().getStartButton(ImageConst.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                // Check if game can be played
                if (chessGame != null) {
                    System.out.println("Switching to GameState!");
                    // State.setState(new GameState(handler));
                } else { // PopUP
                    System.out.println("Invalid chess position!");
                }
            }
        });
        y += gui.getAssets().PIECE_HEIGHT;
        // Button to switch to ReplayState
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth(), y,           // X, Y
                2 * gui.getAssets().PIECE_WIDTH,              // Width
                gui.getAssets().PIECE_HEIGHT,                 // Height
                gui.getAssets().getReplayButton(ImageConst.IMG_UP),  // Idle image
                gui.getAssets().getReplayButton(ImageConst.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                System.out.println("Switching to Replay state");
                // State.setState(new ReplayState(handler));
            }
        });
    }

    /**
     * Sets values of piece button on which user clicked (will be used to display on cursor)
     * @param piece on which user clicked
     * @param color of piece
     */
    private void set_piece(int piece, int color) {
        displayPiece = piece;
        displayColor = color;
        // Deselecting piece
        if (piece == -1) {
            dragged = false;
        }
    }    
}
