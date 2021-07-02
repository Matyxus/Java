package states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Handler;
import board.Fen;
import board.Spot;
import board.constants.Colors;
import board.constants.Pieces;
import managers.UIManager;
import ui.PopUP;
import ui.UIImageButton;

public class PlacementState extends State {

    private final UIManager uiManager;
    private BufferedImage img;

    private boolean display = false; // Clicked on button containing piece.
    private boolean dragged = false; // Clicked on piece on board.

    private int displayPiece;
    private int displayPieceColor;
    
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
                        handler.getGameBoard().placePiece(displayPiece, 
                            displayPieceColor, squareIndex);
                        dragged = false;
                    }
                // Pickup piece and place it.
                } else if (handler.getGameBoard().containsPiece(squareIndex) != null) {
                    Spot target = handler.getGameBoard().containsPiece(squareIndex);
                    displayPiece = target.getPiece();
                    displayPieceColor = target.getColor();
                    img = handler.getAssets().getPieceImg(displayPiece, displayPieceColor);
                    handler.getGameBoard().removePiece(displayPieceColor, squareIndex);
                    dragged = true;
                }
            } else {
                // Stop showing img.
                display = dragged = false;
            }
        }
        // Right click as removal of pieces on bord or to stop draggin piece.
        if (handler.getMouseManager().rightPressed()) {
            if (display || dragged) {
                display = dragged = false;
            } else if (handler.getGameBoard().containsPiece(squareIndex) != null) {
                Spot target = handler.getGameBoard().containsPiece(squareIndex);
                handler.getGameBoard().removePiece(target.getColor(), squareIndex);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        this.uiManager.render(g);
        // Render the selected piece.
        if (display || dragged) {
            g.drawImage(
                img, 
                handler.getMouseManager().getMouseX() - (handler.getAssets().PIECE_WIDTH/2),  // x
                handler.getMouseManager().getMouseY() - (handler.getAssets().PIECE_HEIGHT/2), // y
                null
            ); 
        }
    }

    protected void addButtons() {
        
        for (int piece : Pieces.getPieces()) {
            int x = handler.getAssets().getBoardWidth();
            int y = handler.getAssets().PIECE_HEIGHT * piece;
            int width = handler.getAssets().PIECE_WIDTH;
            int height = handler.getAssets().PIECE_HEIGHT;
            BufferedImage pieceImg = handler.getAssets().getPieceImg(piece, Colors.WHITE);
            // White pieces.
            UIImageButton button = new UIImageButton(x, y, width, height, pieceImg, null) {
                @Override
                public void onClick() {
                    if (object != null) {
                        display = true;
                        img = getImage(0);
                        displayPiece = piece;
                        displayPieceColor = Colors.WHITE;
                    }
                }
            };
            button.setObject(piece);
            this.uiManager.addObject(button);
            // Black pieces.
            x += width; // Move black pieces next to white pieces
            pieceImg = handler.getAssets().getPieceImg(piece, Colors.BLACK);
            button = new UIImageButton(x, y, width, height, pieceImg, null) {
                @Override
                public void onClick() {
                    if (object != null) {
                        display = true;
                        img = getImage(0);
                        displayPiece = piece;
                        displayPieceColor = Colors.BLACK;
                    }
                }
            };
            button.setObject(piece);
            this.uiManager.addObject(button);
        }

        // Fen button for testing purposes
        this.uiManager.addObject(new UIImageButton(0, handler.getAssets().getBoardHeight(), 
                160, 80, handler.getAssets().getPerft_button(), null) {
            @Override
            public void onClick() { // "play button"
                String tmp = PopUP.Trial();
                if (tmp != null && !tmp.isEmpty()) {
                    Fen fen = new Fen();
                    ArrayList<Spot> result = fen.interpret(tmp);
                    if (result != null && !result.isEmpty()) {
                        handler.getGameBoard().reset();
                        result.forEach((spot) -> 
                            handler.getGameBoard().addPiece(spot.getPiece(), spot.getColor(), spot.getSquare())
                        );
                    }
                }
            }
        });
        /*
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
