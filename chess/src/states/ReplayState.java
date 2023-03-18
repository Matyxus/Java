package states;

import gui.Gui;
import ui.ImageButton;
import gameboard.ChessEngine;
import gameboard.constants.Colors;
import gameboard.constants.Pieces;
import utils.ImageConst;
import utils.Pair;
import utils.Perft;

import java.awt.event.MouseEvent;

/**
 * State handling replays of game, provides button to
 * switch to past/future board states. Also provides
 * "Perft" (search how many moves are in given position up to given depth).
 */
public class ReplayState extends State {

    /**
     * Thread running perft
     */
    private Thread perfThread = null;

    public ReplayState(Gui gui, ChessEngine chessGame) {
        super(gui, chessGame);
        System.out.println("Initialized PlacementState");
    }

    @Override
    public void render(Gui gui) {};

    @Override
    public void update(MouseEvent e, int boardSquare) {};

    @Override
    public void tick() {};

    @Override
    protected void addButtons(Gui gui) {
        // Button to switch to show previous move
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth(), 0,         // X, Y
                gui.getAssets().PIECE_WIDTH,                // Width
                gui.getAssets().PIECE_HEIGHT,               // Height
                gui.getAssets().getLeftArrows(ImageConst.IMG_UP),  // Idle image
                gui.getAssets().getLeftArrows(ImageConst.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                chessGame.previousState();
            }
        });

        // Button to switch to next move
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth() + gui.getAssets().PIECE_WIDTH, 0, // X, Y
                gui.getAssets().PIECE_WIDTH,                 // Width
                gui.getAssets().PIECE_HEIGHT,                // Height
                gui.getAssets().getRightArrows(ImageConst.IMG_UP),  // Idle image
                gui.getAssets().getRightArrows(ImageConst.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                chessGame.nextState();
            }
        });

        // Button to start perft
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth(),    // X
                gui.getAssets().PIECE_HEIGHT,       // Y
                2 * gui.getAssets().PIECE_WIDTH,    // Width
                gui.getAssets().PIECE_HEIGHT,       // Height
                gui.getAssets().getPerft_button(),  // Idle image
                gui.getAssets().getPerft_button()   // Hover image
            ) {
            @Override
            public void onClick() {
                final Pair<Integer, Boolean> result = null; // PopUP.perftSetupMessage();
                if (result != null && !perftIsRunning()) {
                    // TODO FEN
                    final String currentFen = chessGame.generateFEN();
                    Runnable myrunnable = new Runnable() {
                        @Override
                        public void run() {
                            while (!Thread.currentThread().isInterrupted()) {
                                try {
                                    // Run perft
                                    final Perft perft = new Perft();
                                    Pair<Long, Long> temp = perft.run_perft(
                                        result.getKey(), // Depth
                                        currentFen, // Fen
                                        false // info
                                    );
                                    /* 
                                    PopUP.messagePopUP(
                                        "Found: " + temp.getKey() + 
                                        " moves in " + temp.getValue() + " sec."
                                    );
                                    */
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    };
                    // Spawn new thread on which perft will run
                    perfThread = new Thread(myrunnable);
                    perfThread.start();
                }
            }
        });

        // Button to switch to PlacementState
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth(), // X
                gui.getAssets().getBoardHeight() - gui.getAssets().PIECE_HEIGHT, // Y
                2 * gui.getAssets().PIECE_WIDTH,  // Width
                gui.getAssets().PIECE_HEIGHT,    // Height
                gui.getAssets().getBackButton(ImageConst.IMG_UP),  // Idle image
                gui.getAssets().getBackButton(ImageConst.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                if (perftIsRunning()) {
                    // PopUP.messagePopUP("Killing thread running Perft");
                    perfThread.interrupt();
                    assert (!perftIsRunning());
                }
                System.out.println("Switching to Placement state");
                State.setState(new PlacementState(gui, chessGame));
            }
        });
    }

    /**
     * @return true if perft is running (another thread)
     */
    private boolean perftIsRunning() {
        return (perfThread != null && perfThread.isAlive() && !perfThread.isInterrupted());
    }
    
}
