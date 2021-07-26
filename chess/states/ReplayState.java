package states;

import java.awt.Graphics;
import java.util.ArrayList;

import assets.Pair;
import board.Perft;
import board.constants.Img;
import main.Handler;
import managers.UIManager;
import ui.PopUP;
import ui.UIImageButton;

public class ReplayState extends State {
    
    /**
     * Array holding previously removed fen positions
     */
    private final ArrayList<String> previousFen;
    /**
     * Array holding previously removed moves in text format
     */
    private final ArrayList<String> previousText;
    private final UIManager uiManager;

    private Thread perftRunner = null;

    public ReplayState(Handler handler) {
        super(handler);
        System.out.println("Initializing ReplayState");
        uiManager = new UIManager();
        previousFen = new ArrayList<String>();
        previousText = new ArrayList<String>();
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
    }

    private boolean perftIsRunning() {
        return (perftRunner != null && perftRunner.isAlive());
    }

    private void showMove(boolean previous) {
        String text = null;
        String fen = null;
        // Comparing size with one, since current position
        // is last one in array, so the one before is needed
        // and there is none before (when array size is 1)
        if (previous && handler.getHolder().getSize() != 1) {
            // Cannot be null
            text = handler.getHolder().removeLastText();
            fen = handler.getHolder().removeLastFen();
            // Append it, if user wants to go "forward" in game
            previousText.add(text);
            previousFen.add(fen);
            // Set display text
            handler.getGame().getDisplay().setText(
                handler.getHolder().getText()
            );
            // Load position
            handler.getGameBoard().loadFen(
                handler.getHolder().getLastFen()
            );
        } else if (!previous) {
            text = previousText.remove(previousText.size()-1);
            fen = previousFen.remove(previousFen.size()-1);
            // Append it back
            handler.getHolder().appendText(text);
            handler.getHolder().appendFen(fen);
            handler.getGame().getDisplay().appendText(text);
            // Load position
            handler.getGameBoard().loadFen(fen);
        }
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
                // If there is something to show
                if (handler.getHolder().getSize() != 0) {
                    showMove(true);
                }
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
                // If there is something to show
                if (previousFen.size() != 0) {
                    showMove(false);
                }
            }
        });

        // Button to start perft
        this.uiManager.addObject(new UIImageButton(
                handler.getAssets().getBoardWidth(),    // X
                handler.getAssets().PIECE_HEIGHT,       // Y
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

    @Override
    public void tick() {};

    @Override
    public boolean save(Holder holder) {
        return true;
    }

    @Override
    public boolean load(Holder holder) {
        previousFen.clear();
        previousText.clear();
        return true;
    }

    @Override
    public boolean canSave() {
        return !perftIsRunning();
    }

    @Override
    public boolean canLoad() {
        return !perftIsRunning();
    }
}
