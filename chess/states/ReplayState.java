package states;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import assets.Pair;

import static java.util.Map.entry;

import board.Perft;
import board.constants.Colors;
import board.constants.Img;
import board.constants.Pieces;
import main.Handler;
import managers.UIManager;
import ui.PopUP;
import ui.UIImageButton;

public class ReplayState extends State {
    /**
     * Returns Map.Entry, where key is piece and value is color
     */
    @SuppressWarnings("unused")
    private final HashMap<String, Map.Entry<Integer, Integer>> unicodeToPiece = 
        new HashMap<String, Map.Entry<Integer, Integer>>(Map.ofEntries(
            // White Pieces
            entry("\u2654",   entry(Pieces.KING, Colors.WHITE)),   
            entry("\u2655",   entry(Pieces.QUEEN, Colors.WHITE)),  
            entry("\u2656",   entry(Pieces.ROOK, Colors.WHITE)),   
            entry("\u2658",   entry(Pieces.KNIGHT, Colors.WHITE)), 
            entry("\u2657",   entry(Pieces.BISHOP, Colors.WHITE)), 
            entry("\u2659",   entry(Pieces.PAWN, Colors.WHITE)), 
            // Black Pieces
            entry("\u265A",   entry(Pieces.KING, Colors.BLACK)),   
            entry("\u265B",   entry(Pieces.QUEEN, Colors.BLACK)),  
            entry("\u265C",   entry(Pieces.ROOK, Colors.BLACK)),   
            entry("\u265E",   entry(Pieces.KNIGHT, Colors.BLACK)), 
            entry("\u265D",   entry(Pieces.BISHOP, Colors.BLACK)), 
            entry("\u265F",   entry(Pieces.PAWN, Colors.BLACK)) 
    ));

    // TO-DO: clear nextMoves when loading new game
    /**
     * Array holding previously removed moves
     */
    @SuppressWarnings("unused")
    private final ArrayList<String> nextMoves;
    private final UIManager uiManager;

    private Thread perftRunner = null;

    public ReplayState(Handler handler) {
        super(handler);
        System.out.println("Initializing ReplayState");
        uiManager = new UIManager();
        nextMoves = new ArrayList<String>();
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
    }

    private boolean perftIsRunning() {
        return (perftRunner != null && perftRunner.isAlive());
    }


    @Override
    public void render(Graphics g) {
        uiManager.render(g);
    }

    @Override
    public void update() {};

    @Override
    protected void addButtons() {
        // Button to switch to show previous move
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(), 0,         // X, Y
                handler.getAssets().PIECE_WIDTH,                // Width
                handler.getAssets().PIECE_HEIGHT,               // Height
                handler.getAssets().getLeftArrows(Img.IMG_UP),  // Idle image
                handler.getAssets().getLeftArrows(Img.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                System.out.println("Showing previous move");
            }
        });

        // Button to switch to next move
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth()+handler.getAssets().PIECE_WIDTH, 0, // X, Y
                handler.getAssets().PIECE_WIDTH,                 // Width
                handler.getAssets().PIECE_HEIGHT,                // Height
                handler.getAssets().getRightArrows(Img.IMG_UP),  // Idle image
                handler.getAssets().getRightArrows(Img.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                System.out.println("Showing next move");
            }
        });

        // Button to start perft
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(), handler.getAssets().PIECE_HEIGHT, // X, Y
                2 * handler.getAssets().PIECE_WIDTH,    // Width
                handler.getAssets().PIECE_HEIGHT,       // Height
                handler.getAssets().getPerft_button(),  // Idle image
                handler.getAssets().getPerft_button()   // Hover image
            ) {
            @Override
            public void onClick() {
                final Pair<Integer, Boolean> result = PopUP.perftSetupMessage();
                if (result != null && !perftIsRunning()) {
                    final String currentFen = handler.getGameBoard().createFen();
                    // Create new hashmaps of pieces
                    // to pass into function, since modification
                    // to current can occur in different thread
                    Runnable myrunnable = new Runnable() {
                        public void run() {
                            // Run perft
                            final Perft perft = new Perft();
                            Pair<Long, Long> temp = perft.init(
                                result.getKey(), // Depth
                                currentFen // Fen
                            );
                            PopUP.messagePopUP(
                                "Found: " + temp.getKey() + " moves in " + temp.getValue() + " sec."
                            );
                        }
                    };
                    // Spawn new thread on which perft will run
                    perftRunner = new Thread(myrunnable);
                    perftRunner.start();
                }
            }
        });

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
                if (perftIsRunning()) {
                    PopUP.messagePopUP("Wait untill perft finishes running");
                } else {
                    System.out.println("Switching to Placement state");
                    State.setState(new PlacementState(handler));
                }
            }
        });
    }
}
