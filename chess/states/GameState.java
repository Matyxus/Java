package states;

import java.awt.Graphics;
import java.util.ArrayList;

import assets.Pair;
import board.Move;
import board.Spot;
import board.constants.Files;
import board.constants.Img;
import board.constants.Pieces;
import board.constants.Ranks;
import main.Handler;
import managers.UIManager;
import ui.PopUP;
import ui.UIImageButton;

public class GameState extends State {

    private final UIManager uiManager;
    private final ArrayList<Move> move_list;
    private Spot previousSelected = null;
    private boolean promotion = false;
    private Move promotionMove = null;

    public GameState(Handler handler) {
        super(handler);
        System.out.println("Initializing GameState");
        uiManager = new UIManager();
        move_list = new ArrayList<Move>();
        handler.getGameBoard().updatePieces(move_list);
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
        // If its new game
        if (handler.getGameBoard().getRound() == 0) {
            System.out.println("New game");
            String text = "";
            // Check for AI
            if (handler.getHolder().getAI()) {
                text = "Computer vs Human\n";
            } else {
                text = "Human vs Human\n";
            }
            handler.getGame().getDisplay().appendText(text);
            handler.getHolder().appendText(text);
            // Initial fen position, unless
            // game was loaded from Fen
            if (handler.getHolder().getSize() == 0) {
                handler.getHolder().appendFen(
                    handler.getGameBoard().createFen()
                );
            }
            
            System.out.println(handler.getHolder());
        } else {
            System.out.println("Already existing game");
        }
    }

    @Override
    public void render(Graphics g) {
        uiManager.render(g);
        // Render where piece can move, if user selected any
        if (previousSelected != null) {
            previousSelected.getMoves().forEach(move -> {
                g.drawImage(
                    handler.getAssets().getMarker(), // Image
                    Ranks.getRow(move.getToSquare()) * handler.getAssets().PIECE_WIDTH,     // X
                    Files.getColumn(move.getToSquare()) * handler.getAssets().PIECE_HEIGHT, // Y
                    null // Observer
                );
            });
        }
    }

    @Override
    public void update() {
        // User has to choose piece which pawn will be promoted to
        if (promotion) {
            return;
        }
        // Right click stops displaying path
        if (handler.getMouseManager().rightPressed()) {
            previousSelected = null;
            return;
        }
        int squareIndex = handler.getAssets().getBoardSquare(
            handler.centerMouseX(), 
            handler.centerMouseY()
        );
        Pair<Integer, Integer> target = handler.getGameBoard().containsPiece(squareIndex);
        // User clicked on his piece
        if (target != null && target.getValue() == handler.getGameBoard().getCurrentPlayer()) {
            previousSelected = new Spot(target.getKey(), target.getValue(), squareIndex);
            previousSelected.setMoves(move_list);
        } else if (previousSelected != null) {
            // Check if user clicked on square where piece can move to
            for (Move move : previousSelected.getMoves()) {
                if (move.getToSquare() == squareIndex) {
                    // Give user choice to choose piece
                    if (move.isPromotion()) {
                        promotion = true;
                        addPromotionButtons();
                        return;
                    }
                    playMove(move);
                    break;
                }
            }
        }
    }

    @Override
    public void tick() {
        // Play promotionMove, 
        // stop rendering promotion buttons
        if (promotionMove != null) {
            promotion = false;
            playMove(promotionMove);
            promotionMove = null;   
            removePromotionButtons();
        }
    }    

    private void playMove(Move move) {
        // Move piece to location
        int capture = handler.getGameBoard().applyMove(move);
        String text = handler.getGameBoard().recordMove(move, capture);
        // Write move to JTextArea
        handler.getGame().getDisplay().appendText(text);
        handler.getHolder().appendText(text);
        handler.getHolder().appendFen(handler.getGameBoard().createFen());
        System.out.println(handler.getHolder());
        previousSelected = null;
        // Generate new moves
        move_list.clear();
        handler.getGameBoard().updatePieces(move_list);
    }

    private void removePromotionButtons() {
        uiManager.clear();
        addButtons();
    }

    private void addPromotionButtons() {
        final int color = handler.getGameBoard().getCurrentPlayer();
        int x = handler.getAssets().getBoardWidth();
        int y = 0;
        for (int piece : Pieces.PROMOTION_PIECES) {
            uiManager.addObject(new UIImageButton(
                x,                                              // X
                y,                                              // Y
                handler.getAssets().PIECE_WIDTH,                // Width
                handler.getAssets().PIECE_HEIGHT,               // Height
                handler.getAssets().getPieceImg(piece, color),  // Idle image
                null                                            // Hover image
                ) {
                @Override
                public void onClick() {
                    System.out.println("Promoting to piece: " + piece);
                    for (Move move : previousSelected.getMoves()) {
                        if (move.isPromotion() && move.getPromotionPiece() == piece) {
                            promotionMove = move;
                            break;
                        }
                    }
                }
            });
            // Have images of pieces next to each other
            if (x == handler.getAssets().getBoardWidth()) {
                x += handler.getAssets().PIECE_WIDTH;
            } else {
                x = handler.getAssets().getBoardWidth();
                y += handler.getAssets().PIECE_HEIGHT;
            }
        }
    }

    @Override
    protected void addButtons() {
        // Button to switch to PlacementState
        uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(),            // X
                handler.getAssets().getBoardHeight() - handler.getAssets().PIECE_HEIGHT, // Y
                2 * handler.getAssets().PIECE_WIDTH,            // Width
                handler.getAssets().PIECE_HEIGHT,               // Height
                handler.getAssets().getBackButton(Img.IMG_UP),  // Idle image
                handler.getAssets().getBackButton(Img.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                if (!promotion) {
                    System.out.println("Switching to Placement state");
                    State.setState(new PlacementState(handler));
                } else {
                    PopUP.messagePopUP("Finish promoting piece");
                }
            }
        });
    }

    @Override
    public boolean save(Holder holder) {
        return true;
    }

    @Override
    public boolean load(Holder holder) {
        // Load AI if present
        if (holder.getAI()) {
            System.out.println("Computer player is set to true");
        }
        return true;
    }

    @Override
    public boolean canSave() {
        return !promotion;
    }

    @Override
    public boolean canLoad() {
        return true; // AI is not playing now
    }

    
}
