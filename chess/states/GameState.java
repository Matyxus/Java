package states;

import java.awt.Graphics;
import java.util.ArrayList;

import assets.Pair;
import board.Move;
import board.Spot;
import board.constants.Files;
import board.constants.Img;
import board.constants.Ranks;
import main.Handler;
import managers.UIManager;
import ui.UIImageButton;

public class GameState extends State {

    private final UIManager uiManager;
    private final ArrayList<Move> move_list;
    private Spot previousSelected = null;

    public GameState(Handler handler) {
        super(handler);
        System.out.println("Initializing GameState");
        uiManager = new UIManager();
        move_list = new ArrayList<Move>();
        handler.getGameBoard().updatePieces(move_list);
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
        // If its new game
        if (handler.getGameBoard().getGameHistory().getRound() == 0) {
            String text = "Human vs Human\n";
            text += handler.getGameBoard().createFen() + "\n";
            handler.getGame().getDisplay().appendText(text);
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
            // Check if user clicked on square where
            // piece can move to
            for (Move move : previousSelected.getMoves()) {
                if (move.getToSquare() == squareIndex) {
                    // Move piece to location
                    int capture = handler.getGameBoard().applyMove(move);
                    String text = handler.getGameBoard().recordMove(move, capture);
                    // Write move to JTextArea
                    handler.getGame().getDisplay().appendText(text);
                    previousSelected = null;
                    // Generate new moves
                    move_list.clear();
                    handler.getGameBoard().updatePieces(move_list);
                    break;
                }
            }
        }
    }

    @Override
    protected void addButtons() {
        // Button to switch to PlacementState
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(),            // X
                handler.getAssets().getBoardHeight() - handler.getAssets().PIECE_HEIGHT, // Y
                2 * handler.getAssets().PIECE_WIDTH,            // Width
                handler.getAssets().PIECE_HEIGHT,               // Height
                handler.getAssets().getBackButton(Img.IMG_UP),  // Idle image
                handler.getAssets().getBackButton(Img.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                System.out.println("Switching to Placement state");
                State.setState(new PlacementState(handler));
            }
        });
    }    
}
