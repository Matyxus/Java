package chess2.states;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import chess2.main.Handler;
import chess2.board.Holder;
import chess2.board.text.TextBoard;
import chess2.computer.Perft;
import chess2.images.Assets;
import chess2.states.ui.UIImageButton;
import chess2.states.ui.UIManager;
import chess2.PopUps;

public class ViewerState extends State {
    private final TextBoard textBoard;
    private final ArrayList<String> gameAsText;
    private final UIManager uiManager;
    private final String REGEX = "[^a-zA-Z0-9]+";

    private int lastIndex = -1;
    private boolean castling = false;
    private int castlingFrom;
    private int fromSquare;
    private int toSquare;
    private int color;
    private int piece;
    private int captureSquare = -1;
    private int round = 0;
    private boolean res = false;

    public ViewerState(Handler handler) {
        super(handler);
        handler.getGame().getDisplay().setTextVisibility(true);
        this.gameAsText = new ArrayList<>();
        this.textBoard = new TextBoard(handler);
        this.uiManager = new UIManager(handler);
        handler.getMouseManager().setUiManager(this.uiManager);
        addButtons();
        round = PopUps.round;
        res = false;
    }

    private void process(boolean left) {
        if (left) { // add piece if capture occured, move piece <-
            String removed = handler.getGame().getDisplay().removeLast();
            if (removed != null) {
                gameAsText.add(removed);
                lastIndex++;
                if (!(removed.contains("#"))) {
                    String temp = removed.replace("\n", "").replace("\r", "");
                    if (temp.length() > 1) {
                        String[] tmp = temp.split(REGEX);
                        decode(tmp, left);
                    }
                    process(left);
                } else {
                    round--;
                }
            }
        } else { // move piece ->, remove if capture occured
            if (lastIndex >= 0) {
                String append = gameAsText.get(lastIndex);
                if (append != null && append.length() > 0) {
                    round++;
                    removeText(append);
                    removeTillNext(left);
                }
            }
        }
    }

    private void removeTillNext(boolean left) {
        if (lastIndex >= 0) {
            String append = gameAsText.get(lastIndex);
            if (append != null && append.length() > 0 && !(append.contains("#"))) {
                String temp = append.replace("\n", "").replace("\r", "");
                String[] tmp = temp.split(REGEX);
                if (tmp.length > 1) {// split will cause empty array to have lenght 1
                    decode(tmp, left);
                }
                removeText(append);
                removeTillNext(left);
            }
            return;
        }
    }

    private void movePiece(boolean left) {
        if (left) {
            if (castling) {
                castle(left);
            }
            if (captureSquare != toSquare){
                handler.getGameBoard().removePiece(color, toSquare);
            }
            handler.getGameBoard().addPiece(piece, color, fromSquare, false);
        } else {
            handler.getGameBoard().removePiece(color, fromSquare);
            if (handler.getGameBoard().getWhitePieces().containsKey(toSquare)) {
                handler.getGameBoard().removePiece(Holder.BLACK, toSquare);
            } else if (handler.getGameBoard().getBlackPieces().containsKey(toSquare)) {
                handler.getGameBoard().removePiece(Holder.WHITE, toSquare);
            }
            handler.getGameBoard().addPiece(piece, color, toSquare, false);
        }
    }

    private void castle(boolean left) {
        if (left) {
            int dest = (fromSquare > castlingFrom) ? fromSquare - 1 : fromSquare + 1;
            handler.getGameBoard().removePiece(color, dest);
            handler.getGameBoard().addPiece(2, color, castlingFrom, false);
        } else {
            int dest = (toSquare > castlingFrom) ? toSquare + 1 : toSquare - 1;
            handler.getGameBoard().removePiece(color, castlingFrom);
            handler.getGameBoard().addPiece(2, color, dest, false);
        }
        castling = false;
    }

    private void capture(boolean left) {
        if (left) {
            if (handler.getGameBoard().getWhitePieces().containsKey(captureSquare)) {
                handler.getGameBoard().removePiece(0, captureSquare);
            } else if (handler.getGameBoard().getBlackPieces().containsKey(captureSquare)) {
                handler.getGameBoard().removePiece(1, captureSquare);
            }
            handler.getGameBoard().addPiece(piece, color, captureSquare, false);
        } else {
            if (captureSquare != toSquare) {
                handler.getGameBoard().removePiece(color, captureSquare);
            }
        }
    }

    private void promote(int square, int targetPiece, int color) {
        handler.getGameBoard().removePiece(color, square);
        handler.getGameBoard().addPiece(piece, color, square, false);
    }

    private void decode(String[] array, boolean left) {
        switch (array[0]) {
            case "Promotion":
                if (!(left)) {
                    promote(toSquare, textBoard.getPieceIndex(array[1]), color);
                }
                return;
            case "Castling":
                castlingFrom = textBoard.getSquare(array[2]);
                castling = true;
                if (!(left)) { // castling to right has to be done here
                    castle(left);
                }
                return;
            case "Capture":
                color = textBoard.getReverseColor(array[1]);
                piece = textBoard.getPieceIndex(array[2]);
                captureSquare = textBoard.getSquare(array[3]);
                capture(left);
                return;
            case "Black":
                color = textBoard.getReverseColor(array[0]);
                piece = textBoard.getPieceIndex(array[1]);
                fromSquare = textBoard.getSquare(array[2]);
                toSquare = textBoard.getSquare(array[3]);
                movePiece(left);
                captureSquare = -1;
                return;
            case "White":
                color = textBoard.getReverseColor(array[0]);
                piece = textBoard.getPieceIndex(array[1]);
                fromSquare = textBoard.getSquare(array[2]);
                toSquare = textBoard.getSquare(array[3]);
                movePiece(left);
                captureSquare = -1;
                return;
            default:
                return;
        }
    }

    private void removeText(String append) {
        handler.getGame().getDisplay().appendText(append);
        gameAsText.remove(lastIndex);
        lastIndex--;
    }

    @Override
    public void tick() {};

    @Override
    public void render(Graphics g) {
        this.uiManager.render(g);
    }

    @Override
    public void saveGame() {
        handler.getGame().getGraphicsManager().save();
    }

    @Override
    public void loadGame() {
        handler.getGame().getGraphicsManager().load();
        round = PopUps.round;
    }

    private void addButtons() {
        // left
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 360, Assets.PIECE_WIDTH, Assets.PIECE_HEIGHT,
                Assets.arrows[0], -1) {
            @Override
            public void onClick() {
                if (show) {
                    process(true);
                    show = false;
                }
            }
        });
        // right
        this.uiManager
                .addObject(new UIImageButton(720, 360, Assets.PIECE_WIDTH, 
                        Assets.PIECE_HEIGHT, Assets.arrows[1], -1) {
                    @Override
                    public void onClick() {
                        if (show) {
                            process(false);
                            show = false;
                        }
                    }
                });
        // back button
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 420, 2*Assets.PIECE_WIDTH, 
                Assets.PIECE_HEIGHT, Assets.back_button, -1) {
            @Override
            public void onClick() {
                if (show) {
                    PopUps.round = round;
                    System.gc();
                    handler.getGame().startBoardPlacementState();
                    handler.getGame().getDisplay().setTextVisibility(false);
                    State.setState(handler.getGame().BoardPlacementState);
                }
            }
        });
        // perft button
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 300, 2*Assets.PIECE_WIDTH,
                Assets.PIECE_HEIGHT, Assets.perft_button, -1) {
            @Override
            public void onClick() {
                if (show) {
                    if (PopUps.perftSettingsPopUP()){
                        startPerft();
                    }
                }
            }
        });

    }

    private void startPerft() {
        final CountDownLatch latch = new CountDownLatch(1);
        Thread uiThread = new Thread("PerftHandler") {
            @Override
            public void run() {
                Perft perft = new Perft();
                res = perft.init(PopUps.perftDepth, handler.getGameBoard(), PopUps.perftHashing);
                perft.free(); // free mem
                perft = null;
                latch.countDown(); // Release await() in the test thread.
            }
        };
        long before = System.nanoTime();
        uiThread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            PopUps.errorPopUP(e);
            return;
        }
        if (!(res)){
            PopUps.plainMessagePopUP("Board has to have at least 3 pieces and both kings, which cant be in check");
        } else {
            long after = System.nanoTime() - before;
            PopUps.plainMessagePopUP("Time taken = "+(after/1000000000)+" sec + nodes searched = "+Perft.nodes);
        }
    }
}
