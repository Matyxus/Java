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
    /**
     * Returns Map.Entry, where key is piece and value is color
     */
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
        Map.Entry<Integer, Integer> temp = unicodeToPiece.get(text[0]);
        int piece = temp.getKey();
        int color = temp.getValue();
        int from = algebraicToSquare(text[1]);
        int to = algebraicToSquare(text[3]);
        if (previous) {
            handler.getGameBoard().movePiece(to, from, color, piece);
            if (capturedPiece != null) {
                temp = unicodeToPiece.get(capturedPiece);
                piece = temp.getKey();
                color = temp.getValue();
                handler.getGameBoard().addPiece(piece, color, to);
            }
        } else {
            if (capturedPiece != null) {
                handler.getGameBoard().removePiece(unicodeToPiece.get(capturedPiece).getKey(), to);
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
        // Button to switch to show previous move, for testing purposes
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
                parser(true);
            }
        });

        // Button to switch to next move, for testing purposes
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
                parser(false);
            }
        });

        // Button to switch to PlacementState, for testing purposes
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
