package states;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static java.util.Map.entry;

import board.constants.Colors;
import board.constants.Img;
import board.constants.Pieces;
import board.constants.Size;
import main.Handler;
import managers.UIManager;
import ui.UIImageButton;



public class ReplayState extends State {
    
    private final HashMap<String, Integer> unicodeToPiece = 
        new HashMap<String, Integer>(Map.ofEntries(
            // White Pieces
            entry("\u2654",   Pieces.KING),   
            entry("\u2655",   Pieces.QUEEN),  
            entry("\u2656",   Pieces.ROOK),   
            entry("\u2658",   Pieces.KNIGHT), 
            entry("\u2657",   Pieces.BISHOP), 
            entry("\u2659",   Pieces.PAWN), 
            // Black Pieces
            entry("\u265A",   Pieces.KING),   
            entry("\u265B",   Pieces.QUEEN),  
            entry("\u265C",   Pieces.ROOK),   
            entry("\u265E",   Pieces.KNIGHT), 
            entry("\u265D",   Pieces.BISHOP), 
            entry("\u265F",   Pieces.PAWN) 
    ));

    private final HashMap<String, Integer> unicodeToColor = 
        new HashMap<String, Integer>(Map.ofEntries(
            // White Pieces
            entry("\u2654",   Colors.WHITE),   
            entry("\u2655",   Colors.WHITE),  
            entry("\u2656",   Colors.WHITE),   
            entry("\u2658",   Colors.WHITE), 
            entry("\u2657",   Colors.WHITE), 
            entry("\u2659",   Colors.WHITE), 
            // Black Pieces
            entry("\u265A",   Colors.BLACK),   
            entry("\u265B",   Colors.BLACK),  
            entry("\u265C",   Colors.BLACK),   
            entry("\u265E",   Colors.BLACK), 
            entry("\u265D",   Colors.BLACK), 
            entry("\u265F",   Colors.BLACK) 
    ));

    private final ArrayList<String> nextMoves;
    private final UIManager uiManager;

    private String capturedPiece = null;

    public ReplayState(Handler handler) {
        super(handler);
        System.out.println("Initializing GameState");
        uiManager = new UIManager();
        nextMoves = new ArrayList<String>();
        handler.getMouseManager().setUiManager(uiManager);
        addButtons();
    }

    // Text format: (* means optional)
    // Round: X
    // {Piece} (pos)  ->  (pos)
    // *[*[Enpassant (pos)] Capture: {Piece}]
    private void parser(boolean previous) {
        System.out.println("Parser:");
        String text = null;
        if (previous) {
            text = handler.getGame().getDisplay().removeLastLine();
            nextMoves.add(text);
        } else if (nextMoves.size() != 0) {
            text = nextMoves.remove(nextMoves.size()-1);
            handler.getGame().getDisplay().appendText(text);
        }
        if (text == null) {
            System.out.println("Nothing more to remove");
            return;
        }
        System.out.println("Text: " + text);
        // Move piece
        if (text.contains("->")) {
            System.out.println("Moving piece");
            text = text.replaceAll("[()]", "");
            pieceMovement(text.trim().split("\\s+"), previous);
            // Stop recrusion only when text contains "Round"
            parser(previous);
            // Add piece back / remove
        } else if (text.contains("CAPTURE")) {
            System.out.println("capture");
            // Enpassant has to be handled differently
            capturedPiece = text.trim().split("\\s+")[1];
            // Stop recrusion only when text contains "Round"
            parser(previous);
        } else { // Round
            System.out.println("round");
            handler.getGameBoard().getGameHistory().setRound(
                Integer.parseInt(text.trim().split("\\s+")[1])
            );
        }
    }

    private void pieceMovement(String[] text, boolean previous) {
        int piece = unicodeToPiece.get(text[0]);
        int color = unicodeToColor.get(text[0]);
        int from = algebraicToSquare(text[1]);
        int to = algebraicToSquare(text[3]);
        if (previous) {
            handler.getGameBoard().movePiece(to, from, color, piece);
            if (capturedPiece != null) {
                piece = unicodeToPiece.get(capturedPiece);
                color = unicodeToColor.get(capturedPiece);
                handler.getGameBoard().addPiece(piece, color, to);
            }
        } else {
            if (capturedPiece != null) {
                handler.getGameBoard().removePiece(unicodeToColor.get(capturedPiece), to);
            }
            handler.getGameBoard().movePiece(from, to, color, piece);
            
        }
        capturedPiece = null;
    }

    /**
     * @param square on board in String format (e.g. "a8")
     * @return square in integer format
     */
    private int algebraicToSquare(String square) {
        return (square.charAt(0)-'a')+Size.ROWS*(Size.ROWS-Character.getNumericValue(square.charAt(1)));
    }

    @Override
    public void render(Graphics g) {
        uiManager.render(g);
    }

    @Override
    public void update() {};

    @Override
    protected void addButtons() {
        // Button to switch to PlacementState, for testing purposes
        this.uiManager.addObject(new UIImageButton(0, handler.getAssets().getBoardHeight(), 
                160, 80, handler.getAssets().getPerft_button(), null) {
            @Override
            public void onClick() {
                System.out.println("Switching to Placement state");
                // Check if king is present -> leads to error
                State.setState(new PlacementState(handler));
            }
        });

        // Button to switch to show previous move, for testing purposes
        this.uiManager.addObject(new UIImageButton(
                160, handler.getAssets().getBoardHeight(), 160, 80, 
                handler.getAssets().getLeftArrows(Img.IMG_UP), handler.getAssets().getLeftArrows(Img.IMG_DOWN)
            ) {
            @Override
            public void onClick() {
                System.out.println("Showing previous move");
                parser(true);
            }
        });

        // Button to switch to next move, for testing purposes
        this.uiManager.addObject(new UIImageButton(
                320, handler.getAssets().getBoardHeight(), 160, 80, 
                handler.getAssets().getRightArrows(Img.IMG_UP), handler.getAssets().getRightArrows(Img.IMG_DOWN)
            ) {
            @Override
            public void onClick() {
                System.out.println("Showing next move");
                parser(false);
            }
        });
    }
}
