package states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Handler;
import board.Square;
import board.constants.Colors;
import board.constants.Pieces;
import managers.UIManager;
import ui.UIImageButton;

public class PlacementState extends State {

    private final UIManager uiManager;
    private BufferedImage img;

    private boolean display = false; // Clicked on button containing piece.
    private boolean dragged = false; // Clicked on piece on board.

    private Pieces displayPiece;
    private Colors displayPieceColor;
    
    public PlacementState(Handler handler) {
        super(handler);
        uiManager = new UIManager();
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
    }

    @Override
    public void update() {
        int x = handler.centerMouseX();
        int y = handler.centerMouseY();
        int squareIndex = handler.getAssets().getBoardSquare(x, y);
        System.out.println("Clicked on square: " + squareIndex);
        if (handler.getMouseManager().leftPressed()) {
            if (x < 8 && y < 8) {
                // Place down pieces.
                if (display || dragged) { 
                    // Pawn cant be placed on 0th or 7th row.
                    if (!(displayPiece == Pieces.PAWN && (y == 0 || y == 7))) { 
                        // Put down picked piece from board, stop showing its img.
                        handler.getGameBoard().placePiece(displayPiece.ordinal(), 
                            displayPieceColor.ordinal(), squareIndex);
                        dragged = false;
                    }
                // Pickup piece and place it.
                } else if (handler.getGameBoard().containsPiece(squareIndex) != null) {
                    Square target = handler.getGameBoard().containsPiece(squareIndex);
                    displayPiece = Pieces.values()[target.getPiece()];
                    displayPieceColor = Colors.values()[target.getColor()];
                    img = handler.getAssets().getPieceImg(displayPiece, displayPieceColor);
                    handler.getGameBoard().removePiece(displayPieceColor.ordinal(), squareIndex);
                    dragged = true;
                }
            } else {
                // Cancle showing img.
                display = dragged = false;
            }
        }
        // Right click as removal of pieces on bord or to stop draggin piece.
        if (handler.getMouseManager().rightPressed()) {
            if (display || dragged) {
                display = dragged = false;
            } else if (handler.getGameBoard().containsPiece(squareIndex) != null) {
                Square target = handler.getGameBoard().containsPiece(squareIndex);
                handler.getGameBoard().removePiece(target.getColor(), squareIndex);
            }
        }
    }

    public void render(Graphics g) {
        this.uiManager.render(g);
        // Render the selected piece.
        if (display || dragged) {
            g.drawImage(img, 
                handler.getMouseManager().getMouseX() - (handler.getAssets().PIECE_WIDTH/2),  // x
                handler.getMouseManager().getMouseY() - (handler.getAssets().PIECE_HEIGHT/2), // y
                null); 
        }
        
    }

    protected void addButtons() {
        
        for (Pieces piece : Pieces.values()) {
            // White pieces.
            UIImageButton button = new UIImageButton(handler.getAssets().getBoardWidth(),
                handler.getAssets().PIECE_HEIGHT * piece.ordinal(), handler.getAssets().PIECE_WIDTH, 
                handler.getAssets().PIECE_HEIGHT, handler.getAssets().getPieceImg(piece, Colors.WHITE), null) {

                @Override
                public void onClick() {
                    if (object != null) {
                        display = true;
                        img = getImage(0);
                        displayPiece = ((Pieces) object);
                        displayPieceColor = Colors.WHITE;
                    }
                    
                }
            };
            button.setObject(piece);
            this.uiManager.addObject(button);
            // Black pieces.
            button = new UIImageButton(handler.getAssets().getBoardWidth()+handler.getAssets().PIECE_WIDTH,
                handler.getAssets().PIECE_HEIGHT * piece.ordinal(), handler.getAssets().PIECE_WIDTH, 
                handler.getAssets().PIECE_HEIGHT, handler.getAssets().getPieceImg(piece, Colors.BLACK), null) {

                @Override
                public void onClick() {
                    if (object != null) {
                        display = true;
                        img = getImage(0);
                        displayPiece = ((Pieces) object);
                        displayPieceColor = Colors.BLACK;
                    }
                }
            };
            button.setObject(piece);
            this.uiManager.addObject(button);
        }

        /*
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 360, 
                2*Assets.PIECE_WIDTH, Assets.PIECE_HEIGHT, Assets.button_start, -1) {
            @Override
            public void onClick() { // "play button"
                if (handler.getGameBoard().canPlay()) {
                    if (PopUps.gameSettingsPopUp()) {
                        checkForModification();
                        System.gc();
                        handler.getGame().startGameState();
                        State.setState(handler.getGame().gameState); 
                    }  
                }
            }
        });
        // this will be button to set to viewerState
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 420, 
                2*Assets.PIECE_WIDTH,  Assets.PIECE_HEIGHT, Assets.button_quit, -1) {
            @Override
            public void onClick() {
                checkForModification();
                System.gc();
                handler.getGame().startViewerState();
                State.setState(handler.getGame().viewerState); 
            }
        });
        */
    }
}
