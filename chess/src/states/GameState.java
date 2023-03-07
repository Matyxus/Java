package states;
import gui.Gui;
import gameboard.ChessGame;
import gameboard.constants.Pieces;
import gameboard.constants.bitboard.Ranks;
import gameboard.constants.bitboard.Files;
import gameboard.move_gen.Move;
import utils.Pair;
import ui.ImageButton;
import utils.ImageConst;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class GameState extends State {

    /*
     * Moves of selected piece
     */
    private ArrayList<Move> move_list = null;
    private Move promotionMove = null;

    public GameState(Gui gui, ChessGame chessGame) {
        super(gui, chessGame);
        move_list = new ArrayList<Move>();
        System.out.println("Initialized PlacementState");
    }

    @Override
    public void render(Gui gui) {
        if (promotionMove != null) {
            // TODO create pop-up with piece images for promotion
            return;
        }

        if (move_list != null) {
            move_list.forEach(move -> {
                gui.getGraphics().drawImage(
                    gui.getAssets().getMarker(), // Image
                    Ranks.getRow(move.getToSquare()) * gui.getAssets().PIECE_WIDTH + gui.getBoardPosition().x,     // X
                    Files.getColumn(move.getToSquare()) * gui.getAssets().PIECE_HEIGHT +  gui.getBoardPosition().y, // Y
                    null // Observer
                );
            });
        }
    }

    @Override
    public void update(MouseEvent e, int boardSquare) {
        // User has to choose piece which pawn will be promoted to
        if (promotionMove != null) {
            return;
        }
        // Right click stops displaying path
        if (e.getButton() == MouseEvent.BUTTON3) {
            move_list = null;
            return;
        }
        // User clicked on his piece
        Pair<Integer, Integer> target = chessGame.getGameBoard().containsPiece(boardSquare);
        if (target != null && target.getValue() == chessGame.getGameBoard().getCurrentPositon().getSideToMove()) {
            move_list = new ArrayList<Move>();
            chessGame.getMove_list().forEach(move -> {
                if (move.getFromSquare() == boardSquare) {
                    move_list.add(move);
                }
            });
        } else if (move_list != null) {
            // Check if user clicked on square where piece can move to
            for (Move move : move_list) {
                if (move.getToSquare() == boardSquare) {
                    // Give user choice to choose piece
                    if (move.isPromotion()) {
                        promotionMove = move;
                    } else {
                        chessGame.playMove(move);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void tick() {
        // Communicate with AI thread
    }

    @Override
    protected void addButtons(Gui gui) {
        // Button to switch to PlacementState
        gui.getUiManager().addObject(new ImageButton(
                gui.getAssets().getBoardWidth(),            // X
                gui.getAssets().getBoardHeight() - gui.getAssets().PIECE_HEIGHT, // Y
                2 * gui.getAssets().PIECE_WIDTH,            // Width
                gui.getAssets().PIECE_HEIGHT,               // Height
                gui.getAssets().getBackButton(ImageConst.IMG_UP),  // Idle image
                gui.getAssets().getBackButton(ImageConst.IMG_DOWN) // Hover image
            ) {
            @Override
            public void onClick() {
                // TODO ask user if he wants to switch (abandon promotion)
                if (promotionMove == null) {
                    System.out.println("Switching to Placement state");
                    State.setState(new PlacementState(gui, chessGame));
                } else {
                    // PopUP.messagePopUP("Finish promoting piece");
                }
            }
        });
    }

    /**
     * Adds buttons for piece promotion
     * @param gui of game
     */
    private void addPromotionButtons(Gui gui) {
        final int color = chessGame.getGameBoard().getCurrentPositon().getSideToMove();
        int x = gui.getAssets().getBoardWidth();
        int y = 0;
        for (int piece : Pieces.PROMOTION_PIECES) {
            gui.getUiManager().addObject(new ImageButton(
                x, y, // Coordinates
                gui.getAssets().PIECE_WIDTH, gui.getAssets().PIECE_HEIGHT, // Height
                gui.getAssets().getPieceImg(piece, color),  null // Idel, Hover image
                ) {
                @Override
                public void onClick() {
                    // System.out.println("Promoting to piece: " + piece);
                    if (move_list == null || move_list.isEmpty()) {
                        return;
                    }
                    for (Move move : move_list) {
                        assert (move.isPromotion());
                        if (move.getPromotionPiece() == piece) {
                            chessGame.playMove(move);
                            break;
                        }
                    }
                }
            });
            // Have images of pieces next to each other
            if (x == gui.getAssets().getBoardWidth()) {
                x += gui.getAssets().PIECE_WIDTH;
            } else {
                x = gui.getAssets().getBoardWidth();
                y += gui.getAssets().PIECE_HEIGHT;
            }
        }
    }
}
