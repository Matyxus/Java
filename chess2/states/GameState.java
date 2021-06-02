package chess2.states;

import chess2.board.text.TextBoard;
import chess2.images.Assets;
import chess2.states.ui.UIImageButton;
import chess2.states.ui.UIManager;
import chess2.computer.MinMaxPlayer;
import chess2.main.Handler;
import chess2.PopUps;
import chess2.board.GameFinish;
import chess2.board.Holder;
import java.awt.Graphics;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;

public class GameState extends State {
    private int round = 0;
    private boolean canPlay = true;
    private boolean run;

    private boolean promotion = false;
    private boolean chosen = false;

    private boolean highlighte = false;
    private long visibleMoves = -1;
    private int[] movesToHighlit;

    private int highlightedPiece = -1;
    private int highlightedPieceColor = -1;

    private int x, y;
    private int currentSquare = -1;
    private int selectedSquare = -1;

    private final TextBoard textBoard;// Text representation of moves
    private final UIManager uiManager; // pawn promotion buttons+back button
    private final GameFinish gameFinish;// check for win/draw
    private MinMaxPlayer minMaxPlayer = null; // not truly random, but not really good
    private boolean aiTurn = false;

    private long timePerMove;
    private final long promotionTime = 10;// 10sec
    private long sec;

    public GameState(Handler handler) {
        super(handler);
        this.uiManager = new UIManager(handler);
        this.gameFinish = new GameFinish();
        this.textBoard = new TextBoard(handler);
        addBackButton();
        handler.getMouseManager().setUiManager(this.uiManager);
        handler.getGameBoard().enableCastling(PopUps.castling);
        handler.getGameBoard().enableEnpassant(PopUps.enpassant);
        handler.getGame().getDisplay().setTextVisibility(true);
        if (PopUps.startingPlayerColor != handler.getGameBoard().getCurrentPlayer()) {
            handler.getGameBoard().updateGame();
        } else {
            handler.getGameBoard().start();
        }
        timePerMove = PopUps.time;
        sec = timePerMove;

        if (timePerMove != 0) {
            startClock();
        }
        // check if game wasnt loaded in BoardPlacementState / ViewerState
        if (handler.getGame().getDisplay().getText() == null || handler.getGame().getDisplay().getText().length() < 2) {
            textBoard.initGame(PopUps.aiCol, PopUps.AI);
        } else {
            round = PopUps.round;
            handler.getGame().getDisplay().appendText("\n");
        }
        if (PopUps.AI) {
            initAI();
        }
    }

    @Override
    public void tick() {
        this.x = handler.getMouseManager().getCenteredMouseX();
        this.y = handler.getMouseManager().getCenteredMouseY();
        if (handler.getMouseManager().leftPressed() && !(promotion) && canPlay) {
            if (x < 8 && y < 8) {
                currentSquare = 8 * y + x;
                // user clicked on the same piece -> stop showing moves
                if ((highlighte && (currentSquare == selectedSquare))) {
                    this.highlighte = false;
                    return;
                }
                // check where user clicked
                if (handler.getGameBoard().clickedValidPiece(currentSquare)) {
                    highlighte = true;
                    highlightedPiece = handler.getGameBoard().getClickedPiece();
                    highlightedPieceColor = handler.getGameBoard().getClickedPieceColor();
                    visibleMoves = -1;
                    selectedSquare = currentSquare;
                    return;
                }
                // move piece to valid location
                if (highlighte) {
                    if (((0b1L << currentSquare) & visibleMoves) != 0) {
                        movePiece();
                        highlighte = false;
                        visibleMoves = -1;
                        return;
                    }
                }
            }
        }

        if (handler.getMouseManager().rightPressed()) {
            highlighte = false;
        }
    }

    @Override
    public void render(Graphics g) {
        uiManager.tick();
        uiManager.render(g);
        // show possible moves
        if (highlighte) {
            showMoves(g);
        }
        // piece to promotion was chosen
        if (chosen) {
            textBoard.writePromotion(highlightedPiece);
            handler.getGameBoard().promote(highlightedPiece, highlightedPieceColor, currentSquare);
            handler.getGameBoard().updateGame();
            handleDraw(Holder.PAWN, -1);
            promotion = false;
            chosen = false;
            if (timePerMove == 0) {
                run = false;
                handler.getGame().getDisplay().appendTime("");
            } else {
                sec = timePerMove;
            }
            addBackButton();
            if (minMaxPlayer != null) {
                aiTurn = true;
                aiMove();
            }
        }
    }

    private void handleDraw(int piece, int capture) {
        int result = gameFinish.checkForDraw(handler.getGameBoard(), piece, capture);
        if (result != 0) {
            run = false;
            if (!(PopUps.drawPopUp(result))) {
                textBoard.writeDraw(result);
                canPlay = false;
                handler.getGame().getDisplay().appendTime("");
            } else {
                if (result == 4) {
                    gameFinish.setStopChecking(true);
                }
                resumeClock();
            }
        } else if (gameFinish.checkForWin(handler.getGameBoard())) {
            textBoard.writeWin(handler.getGameBoard().getCurrentPlayer(), PopUps.AI);
            run = false;
            canPlay = false;
        }
    }

    private void startClock() {
        handler.getGame().getDisplay().setTimeVisibility(true);
        clock();
    }

    private void resumeClock() {
        if (timePerMove != 0) {
            clock();
        }
    }

    private void movePiece() {
        round++;
        textBoard.writeMove(highlightedPieceColor, highlightedPiece, selectedSquare, currentSquare, round);
        // Update bitboard
        int removedPiece = handler.getGameBoard().movePiece(selectedSquare, currentSquare, highlightedPiece);
        if (removedPiece != -1) {
            int removedSquare = handler.getGameBoard().getRemovedPieceSquare();
            textBoard.writeCapture(highlightedPieceColor, removedPiece, removedSquare);
        }
        int rookPos = handler.getGameBoard().getRookCastlePos();
        if (rookPos != -1) {
            boolean dir = (rookPos > currentSquare) ? true : false;
            textBoard.writeCastle(rookPos, dir);
        }

        if (highlightedPiece == Holder.PAWN && (currentSquare < 8 || currentSquare > 55)) {
            if (aiTurn) {
                textBoard.writePromotion(highlightedPiece);
                handler.getGameBoard().promote(highlightedPiece, highlightedPieceColor, currentSquare);
                handler.getGameBoard().updateGame();
                sec = timePerMove;
                return;
            }
            sec = promotionTime;
            promotion = true;
            promoteButtons(highlightedPieceColor);
            if (timePerMove == 0) {
                startClock();
            }
        } else {
            handler.getGameBoard().updateGame();
            handleDraw(highlightedPiece, removedPiece);
            sec = timePerMove;
            if (aiTurn) {
                aiTurn = false;
            } else if (minMaxPlayer != null) {
                aiTurn = true;
                aiMove();
            }
        }

    }

    private void showMoves(Graphics g) {
        if (visibleMoves == -1) {
            visibleMoves = handler.getGameBoard().getClickedPieceMoves();
            movesToHighlit = convertLongToSquares(visibleMoves);
        }
        if (movesToHighlit.length > 0) {
            for (int square : movesToHighlit) {
                g.drawImage(Assets.marker, (square % 8) * Assets.PIECE_WIDTH, (square / 8) * Assets.PIECE_HEIGHT, null);
            }
        }
    }

    private int[] convertLongToSquares(long moves) {
        int[] toReturn = new int[Long.bitCount(moves)];
        int counter = 0;
        while (moves != 0) {
            int result = Long.numberOfTrailingZeros(moves);
            moves ^= (0b1L << result);
            toReturn[counter] = result;
            counter++;
        }
        return toReturn;
    }

    @Override
    public void saveGame() {
        if (!(promotion)) {// cant save game during promotion
            PopUps.currPlayerTime = sec;
            PopUps.gameEnd = canPlay;
            PopUps.round = round;
            handler.getGameBoard().getZobrist().setPointer(gameFinish.getPrevBoards(), gameFinish.getCounter());
            run = false;// stop clock during save
            handler.getGame().getGraphicsManager().save();
            if (canPlay) {
                resumeClock();
            }
        }
    }

    @Override
    public void loadGame() {
        if (!(promotion)) { // cant load game during promotion
            run = false;
            if (handler.getGame().getGraphicsManager().load()) {
                gameFinish.load(handler.getGameBoard());
                canPlay = PopUps.gameEnd;
                if (!(canPlay)) {
                    PopUps.plainMessagePopUP("Game cannot be played!");
                    handler.getGame().getDisplay().appendTime("");
                    minMaxPlayer = null;
                    return;
                }
                gameFinish.setStopChecking(false);
                round = PopUps.round;
                handler.getGame().getDisplay().appendText("\n");
                if (PopUps.AI && minMaxPlayer == null) {
                    minMaxPlayer = new MinMaxPlayer((handler.getGameBoard().getCurrentPlayer()+1)%2);
                } else {
                    minMaxPlayer = null;
                }
                timePerMove = PopUps.time;
                if (timePerMove != 0) {
                    sec = PopUps.currPlayerTime;
                }
            }
            resumeClock();
        }
    }

    private void clock() {
        run = true;
        Thread clock = new Thread() {
            public void run() {
                try {
                    while (run) {
                        String strTime = LocalTime.MIN.plusSeconds(sec).toString();
                        if (sec % 60 == 0)
                            strTime += ":00";// LocalTime is omitting sec if they are 0
                        handler.getGame().getDisplay().appendTime(strTime);
                        sec--;
                        Thread.sleep(1000);
                        if (sec == 0) {
                            handler.getGame().getDisplay().appendTime("");
                            if (promotion) {
                                highlightedPiece = 1;
                                chosen = true;
                                Thread.sleep(50);
                            } else {// popup
                                if (timePerMove != 0) {
                                    PopUps.plainMessagePopUP("End of time for Move");
                                    textBoard.writeWin(handler.getGameBoard().getCurrentPlayer(), PopUps.AI);
                                    canPlay = false;
                                    run = false;
                                }
                            }
                        }
                    }
                    return;
                } catch (Exception e) {
                    PopUps.errorPopUP(e);
                    run = false;
                }
            }
        };
        if (run) {
            clock.start();
        }
    }

    private void addBackButton() {
        this.uiManager.clean();
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 360, 2*Assets.PIECE_WIDTH,
            Assets.PIECE_HEIGHT, Assets.back_button, -1) {
            @Override
            public void onClick() {
                if (show && !(promotion)) {
                    run = false;// stop thread
                    handler.getGame().getDisplay().setTextVisibility(false);
                    handler.getGame().getDisplay().setTimeVisibility(false);
                    handler.getGame().getDisplay().appendTime("");// clean time
                    PopUps.round = round;
                    System.gc();
                    handler.getGame().startBoardPlacementState();
                    State.setState(handler.getGame().BoardPlacementState);
                }
            }
        });
    }

    private void promoteButtons(int currPlayer) {
        this.uiManager.clean();
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 300, Assets.PIECE_WIDTH, Assets.PIECE_HEIGHT,
                Assets.pieces[Holder.QUEEN][currPlayer], Holder.QUEEN) {
            @Override
            public void onClick() {
                if (show) {
                    highlightedPiece = getPiece();
                    chosen = true;
                }
            }
        });
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH + Assets.PIECE_WIDTH, 300, Assets.PIECE_WIDTH,
                Assets.PIECE_HEIGHT, Assets.pieces[Holder.ROOK][currPlayer], Holder.ROOK) {
            @Override
            public void onClick() {
                if (show) {
                    highlightedPiece = getPiece();
                    chosen = true;
                }
            }
        });
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH, 360, Assets.PIECE_WIDTH, Assets.PIECE_HEIGHT,
                Assets.pieces[Holder.KNIGHT][currPlayer], Holder.KNIGHT) {
            @Override
            public void onClick() {
                if (show) {
                    highlightedPiece = getPiece();
                    chosen = true;
                }
            }
        });
        this.uiManager.addObject(new UIImageButton(Assets.BOARD_WIDTH + Assets.PIECE_WIDTH, 360, Assets.PIECE_WIDTH,
                Assets.PIECE_HEIGHT, Assets.pieces[Holder.BISHOP][currPlayer], Holder.BISHOP) {
            @Override
            public void onClick() {
                if (show) {
                    highlightedPiece = getPiece();
                    chosen = true;
                }
            }
        });
    }

    private void initAI() {
        minMaxPlayer = new MinMaxPlayer(PopUps.aiCol);
        if (handler.getGameBoard().getCurrentPlayer() == PopUps.aiCol) {
            aiTurn = true;
            aiMove();
        }
    }
    
    private void aiMove() {
        final CountDownLatch latch = new CountDownLatch(1);
        Thread aiThread = new Thread("AIHandler") {
            @Override
            public void run() {
                minMaxPlayer.findMove(handler.getGameBoard());
                latch.countDown(); // Release await() in the test thread.
            }
        };
        aiThread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            PopUps.errorPopUP(e);
            return;
        }
        currentSquare = minMaxPlayer.getToSquare();
        selectedSquare = minMaxPlayer.getFromSquare();
        highlightedPiece = minMaxPlayer.getChosenPiece();
        highlightedPieceColor = PopUps.aiCol;
        movePiece();
    }
}