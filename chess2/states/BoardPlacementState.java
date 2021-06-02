package chess2.states;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import chess2.main.Handler;
import chess2.PopUps;
import chess2.board.Holder;
import chess2.images.Assets;
import chess2.states.ui.UIImageButton;
import chess2.states.ui.UIManager;

public class BoardPlacementState extends State {

    private final UIManager uiManager;
    private BufferedImage img;
    private long prevBoard;

    private boolean display = false;
    private boolean dragged = false;

    private int displayPiece;
    private int displayPieceColor;
    private int x, y;
    private int square;
    
    public BoardPlacementState(Handler handler) {
        super(handler);
        this.uiManager = new UIManager(handler);
        handler.getMouseManager().setUiManager(this.uiManager);
        addButtons();
        if (handler.getGameBoard().kingsArePresent()) {
            prevBoard = handler.getGameBoard().getZobrist().createHash();
        } else {
            prevBoard = 0;
        }
    }

    @Override
    public void tick() {
        this.x = handler.getMouseManager().getCenteredMouseX();
        this.y = handler.getMouseManager().getCenteredMouseY();
        this.square = 8 * y + x;
        if (handler.getMouseManager().leftPressed() && !(handler.getMouseManager().rightPressed())) {
            if (x < 8 && y < 8) {
                if (display || dragged) { // place down pieces
                    if (!(displayPiece == 5 && (y == 0 || y == 7))) { // pawn cant be placed on 0th or 7th row
                        handler.getGameBoard().placePiece(displayPiece, displayPieceColor, square, false);
                        if (dragged) { // put down picked piece from board, stop showing img of piece
                            dragged = false;
                        }
                    }
                } else if (handler.getGameBoard().clickedOnPiece(square)) {// pickup piece and place it
                    displayPiece = handler.getGameBoard().getClickedPiece();
                    displayPieceColor = handler.getGameBoard().getClickedPieceColor();
                    img = Assets.pieces[displayPiece][displayPieceColor];
                    handler.getGameBoard().removePiece(displayPieceColor, square);
                    dragged = true;
                }
            } else {
                display = dragged = false;
            }
        }

        if (handler.getMouseManager().rightPressed()) {// for removing pieces / stop displaying
            if (display || dragged) {
                display = dragged = false;
            } else if (handler.getGameBoard().clickedOnPiece(square)) {
                handler.getGameBoard().removePiece(handler.getGameBoard().getClickedPieceColor(), square);
            }
        }
    }

    public void render(Graphics g) {
        this.uiManager.render(g);
        if (display || dragged) {
            g.drawImage(img, handler.getMouseManager().getMouseX() - (Assets.PIECE_WIDTH/2), 
                        handler.getMouseManager().getMouseY() - (Assets.PIECE_HEIGHT/2), null);
        }
    }
    //check for modification, if there was some, remove text
    private void checkForModification() {
        if (handler.getGameBoard().kingsArePresent()) { // need to check if user didnt remove king/s
            if (prevBoard == handler.getGameBoard().getZobrist().createHash()) {
                return;
            }
        } 
        handler.getGameBoard().getZobrist().clear();
        handler.getGame().getDisplay().cleanText();
    }

    // should compare last image against current, to not save the same image again
    @Override
    public void saveGame() {
        handler.getGame().getGraphicsManager().save();
    }

    @Override
    public void loadGame() {
        if (handler.getGame().getGraphicsManager().load()) {
            prevBoard = handler.getGameBoard().getZobrist().createHash();
        }
        
    }

    protected void addButtons() {
        for (int i = 0; i < 6; i++) { // White pieces
            this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, Assets.PIECE_HEIGHT * i, 
                    Assets.PIECE_WIDTH, Assets.PIECE_HEIGHT, Assets.pieces[i][Holder.WHITE], i) {
                @Override
                public void onClick() {
                    if (show && isOccupied()) {
                        display = true;
                        img = getImage();
                        displayPiece = getPiece();
                        displayPieceColor = 0;
                    }
                }
            });
        }
        for (int i = 0; i < 6; i++) {// Black pieces
            this.uiManager.addObject(new UIImageButton(720, Assets.PIECE_HEIGHT * i,
                     Assets.PIECE_WIDTH, Assets.PIECE_HEIGHT, Assets.pieces[i][Holder.BLACK], i) {
                @Override
                public void onClick() {
                    if (show && isOccupied()) {
                        display = true;
                        img = getImage();
                        displayPiece = getPiece();
                        displayPieceColor = 1;
                    }
                }
            });
        }
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

    }
}
