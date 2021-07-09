package states;

import java.awt.Graphics;

import board.Spot;
import board.constants.Size;
import main.Handler;
import managers.UIManager;
import ui.UIImageButton;

public class GameState extends State {

    private final UIManager uiManager;
    private Spot previousSelected = null;

    public GameState(Handler handler) {
        super(handler);
        System.out.println("Initializing GameState");
        uiManager = new UIManager();
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
    }

    @Override
    public void render(Graphics g) {
        uiManager.render(g);
        // Render where piece can move, if user selected any
        if (previousSelected != null) {
            long moves = previousSelected.getMoves();
            while (moves != 0) {
                int square = Long.numberOfTrailingZeros(moves);
                moves ^= (Size.ONE << square);
                g.drawImage(
                    handler.getAssets().getMarker(), // image
                    (square % 8) * handler.getAssets().PIECE_WIDTH,  // x
                    (square / 8) * handler.getAssets().PIECE_HEIGHT, // y
                    null // observer
                );
            }
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
        Spot target = handler.getGameBoard().containsPiece(squareIndex);
        // User clicked on his piece
        if (target != null && target.getColor() == handler.getGameBoard().getCurrentPlayer()) {
            previousSelected = target;
        } else if (previousSelected != null) {
            // Check if user clicked on path, to move piece,
            if ((previousSelected.getMoves() & (Size.ONE << squareIndex)) != 0) {
                // Move piece to location
                handler.getGameBoard().playMove(
                    previousSelected.getSquare(), // From
                    squareIndex,                  // To
                    previousSelected.getColor(),  // Color
                    previousSelected.getPiece()   // Piece
                );
                previousSelected = null;
            }
        }
    }

    @Override
    protected void addButtons() {
        // Button to switch to PlacementState, for testing purposes
        this.uiManager.addObject(new UIImageButton(0, handler.getAssets().getBoardHeight(), 
                160, 80, handler.getAssets().getPerft_button(), null) {
            @Override
            public void onClick() {
                System.out.println("Switching to Placement state");
                State.setState(new PlacementState(handler));
            }
        });
    }    
}
