package states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import assets.Pair;
import main.Handler;
import board.constants.Colors;
import board.constants.Img;
import board.constants.Pieces;
import board.constants.Size;
import managers.UIManager;
import ui.UIImageButton;

public class PlacementState extends State {

    private final UIManager uiManager;
    private BufferedImage pieceImage;

    private boolean display = false; // Clicked on button containing piece
    private boolean dragged = false; // Clicked on piece on board

    private int displayPiece;
    private int displayPieceColor;
    
    public PlacementState(Handler handler) {
        super(handler);
        System.out.println("Initializing PlacementState");
        uiManager = new UIManager();
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
    }

    @Override
    public void update() {
        int x = handler.centerMouseX();
        int y = handler.centerMouseY();
        int squareIndex = handler.getAssets().getBoardSquare(x, y);
        final String currentFen = handler.getGameBoard().createFen();
        if (handler.getMouseManager().leftPressed()) {
            // Clicked on chess board
            if (squareIndex != -1) {
                // Place down pieces
                if (display || dragged) { 
                    // Pawn cant be placed on 0th or 7th row
                    if (!(displayPiece == Pieces.PAWN && (y == 0 || y == 7))) { 
                        // Put down picked piece from board, stop showing its img
                        handler.getGameBoard().addPiece(
                            displayPiece, 
                            displayPieceColor, 
                            squareIndex
                        );
                        dragged = false;
                    }
                // Pickup piece and place it
                } else if (handler.getGameBoard().containsPiece(squareIndex) != null) {
                    Pair<Integer, Integer> target = handler.getGameBoard().containsPiece(squareIndex);
                    displayPiece = target.getKey();
                    displayPieceColor = target.getValue();
                    pieceImage = handler.getAssets().getPieceImg(displayPiece, displayPieceColor);
                    handler.getGameBoard().removePiece(displayPieceColor, squareIndex);
                    dragged = true;
                }
            } else {
                // Stop showing img
                display = dragged = false;
            }
        }
        // Right click as removal of pieces on board or to stop dragging/displaying piece
        if (handler.getMouseManager().rightPressed()) {
            if (display || dragged) {
                display = dragged = false;
            } else if (handler.getGameBoard().containsPiece(squareIndex) != null) {
                Pair<Integer, Integer> target = handler.getGameBoard().containsPiece(squareIndex);
                handler.getGameBoard().removePiece(target.getValue(), squareIndex);
            }
        }
        // Player modified board (by moving/adding/deleting piece)
        // clear game history
        if (!currentFen.equals(handler.getGameBoard().createFen())) {
            System.out.println("Modification occured, clearing game history");
            handler.getGame().getDisplay().setText("");
            handler.getHolder().clear();
        }
    }

    @Override
    public void render(Graphics g) {
        uiManager.render(g);
        // Render the selected piece
        if (display || dragged) {
            g.drawImage(
                pieceImage, 
                handler.getMouseManager().getMouseX() - (pieceImage.getWidth()/2),  // X
                handler.getMouseManager().getMouseY() - (pieceImage.getHeight()/2), // Y
                null
            ); 
        }
    }

    @Override
    protected void addButtons() {
        // Add buttons showing pieces of both
        // colors, for user to configure their own board
        int y = 0;
        for (int piece : Pieces.getPieces()) {
            int x = handler.getAssets().getBoardWidth();
            y = handler.getAssets().PIECE_HEIGHT * piece;
            int width = handler.getAssets().PIECE_WIDTH;
            int height = handler.getAssets().PIECE_HEIGHT;
            BufferedImage pieceImg = handler.getAssets().getPieceImg(piece, Colors.WHITE);
            // White pieces.
            UIImageButton button = new UIImageButton(x, y, width, height, pieceImg, null) {
                @Override
                public void onClick() {
                    if (object != null) {
                        display = true;
                        pieceImage = getImage(Img.IMG_UP);
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
                        pieceImage = getImage(Img.IMG_UP);
                        displayPiece = piece;
                        displayPieceColor = Colors.BLACK;
                    }
                }
            };
            button.setObject(piece);
            this.uiManager.addObject(button);
        }
        y += handler.getAssets().PIECE_HEIGHT;
        // Button to switch to GameState
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(), y,          // X, Y
                2 * handler.getAssets().PIECE_WIDTH, handler.getAssets().PIECE_HEIGHT, // Width, Height
                handler.getAssets().getStartButton(Img.IMG_UP),  // Idle image
                handler.getAssets().getStartButton(Img.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                int whiteKingSquare = handler.getGameBoard().getPlayer(Colors.WHITE).getKingSquare();
                int blackKingSquare = handler.getGameBoard().getPlayer(Colors.BLACK).getKingSquare();
                // If kings are present, switch to GameState
                if (whiteKingSquare != Size.BOARD_SIZE && blackKingSquare != Size.BOARD_SIZE) {
                    System.out.println("Switching to Game state");
                    State.setState(new GameState(handler));
                } else {
                    System.out.println("Cannot play without king/s");
                }
            }
        });
        y += handler.getAssets().PIECE_HEIGHT;
        // Button to switch to ReplayState
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(), y,           // X, Y
                2 * handler.getAssets().PIECE_WIDTH,              // Width
                handler.getAssets().PIECE_HEIGHT,                 // Height
                handler.getAssets().getReplayButton(Img.IMG_UP),  // Idle image
                handler.getAssets().getReplayButton(Img.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                System.out.println("Switching to Replay state");
                State.setState(new ReplayState(handler));
            }
        });
    }

    @Override
    public void tick() {}


    @Override
    public boolean save(Holder holder) {
        return true;
    }

    @Override
    public boolean load(Holder holder) {
        display = dragged = false; // Reset selected piece
        return true;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public boolean canLoad() {
        return true;
    };
}
