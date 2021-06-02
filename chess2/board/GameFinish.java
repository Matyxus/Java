package chess2.board;
import java.util.HashMap;
//https://en.wikipedia.org/wiki/Rules_of_chess
public class GameFinish {
    private HashMap<Long, Integer> prevBoards;
    private boolean draw;
    private int counter = 0;
    private boolean win = true;
    private boolean stopChecking = false;

    public GameFinish() {
        prevBoards = new HashMap<Long, Integer>();
    }

    private void clear() {
        this.prevBoards.clear();
        counter = 0;
    }

    public void load(GameBoard gameBoard) {
        clear();
        if (gameBoard.getZobrist().getPointer() != null) {
            gameBoard.getZobrist().getPointer().forEach((hash, count) -> prevBoards.put(hash, count));
            this.counter = gameBoard.getZobrist().getCounter();
            gameBoard.getZobrist().clear();
        }
    }

    // If a player's king is placed in check and there is no legal move that player
    // can make
    // to escape check, then the king is said to be checkmated, the game ends.
    public boolean checkForWin(GameBoard gameBoard) {
        gameBoard.start();
        win = true;
        if (gameBoard.getCheck() == 1 || gameBoard.getCheck() == 2) {
            gameBoard.getCurrPlayerPieces().forEach((square, spot) -> {
                if (spot.getMoves() != 0) {
                    win = false;
                    return;
                }
            });
        } else {
            win = false;
        }
        return win;// acts as defeat here
    }

    public int checkForDraw(GameBoard gameBoard, int piece, int capturedPiece) {
        long hash = gameBoard.getZobrist().createHash();
        prevBoards.merge(hash, 1, Integer::sum);

        if (insufficientMaterial(gameBoard)) {
            return 1;
        }

        if (staleMate(gameBoard)) {
            return 2;
        }

        boolean pawnMoved = (piece == Holder.PAWN) ? true:false;
        boolean capture = (capturedPiece != -1) ? true:false;
        if (gameBoard.getCheck() != 2 && fiftyMoveDraw(pawnMoved, capture)) {
            return 3;
        }

        if (threeFoldRepetition() && !(stopChecking)) {
            return 4;
        }

        if (fiveFoldRepetition()) {
            return 5;
        }
        return 0;// none draw rule applies
    }
    // The game is automatically a draw if the player to move is not in check 
    // and has no legal move. This situation is called a stalemate.
    private boolean staleMate(GameBoard gameBoard) {
        draw = false;
        if (gameBoard.getCheck() == 0){
            draw = true;
            gameBoard.getCurrPlayerPieces().forEach((square, spot)->{
                if (spot.getMoves() != 0){
                    draw = false;
                    return;
                }
            });
        }
        return draw;
    }
    //The game is immediately drawn when there is no possibility of checkmate for either side
    //with any series of legal moves. This draw is often due to insufficient material
    private boolean insufficientMaterial(GameBoard gameBoard){
        switch (Long.bitCount(gameBoard.blockers)) {
            case 2://king vs king;
                return true;
            case 3:// king vs king+(bishop or knight)
               if ( gameBoard.getPiecesCount(Holder.WHITE, Holder.KNIGHT) == 1 ||
                    gameBoard.getPiecesCount(Holder.BLACK, Holder.KNIGHT) == 1 ||
                    gameBoard.getPiecesCount(Holder.WHITE, Holder.BISHOP) == 1 ||
                    gameBoard.getPiecesCount(Holder.BLACK, Holder.BISHOP) == 1) {
                        return true;
                    }
            case 4:// king + bishop vs king + bishop, king vs king+2*Knight
                if (gameBoard.getPiecesCount(Holder.WHITE, Holder.BISHOP) == 1 &&
                    gameBoard.getPiecesCount(Holder.BLACK, Holder.BISHOP) == 1) {
                        return true;
                    }
                if (gameBoard.getPiecesCount(Holder.WHITE, Holder.KNIGHT) == 2 ||
                    gameBoard.getPiecesCount(Holder.BLACK, Holder.KNIGHT) == 2){
                        return true;
                    }
            default:
                break;
        }
        return false;
    }
    //Threefold repetition: The same board position has occurred three times with
    //the same player to move and all pieces having the same rights to move, 
    //including the right to castle or capture en passant.
    private boolean threeFoldRepetition() {
        draw = false;
        prevBoards.forEach((hash, value)->{
            if (value == 3) {
                draw = true;
                return;
            }
        });
        return draw;
    }
    //Fivefold repetition of position: Similar to the threefold-repetition rule, 
    //but in this case neither player needs to claim the draw.
    private boolean fiveFoldRepetition() {
        draw = false;
        prevBoards.forEach((hash, value)->{
            if (value == 5) {
                draw = true;
                return;
            }
        });
        return draw;
    }
    //Fifty-move rule: There has been no capture or pawn move in the last fifty moves
    //by each player, if the last move was not a checkmate.
    private boolean fiftyMoveDraw(boolean pawnMoved, boolean capture) {
        counter = (pawnMoved == false) ? counter++:0;
        if (capture) {
            counter = 0;
        }
        return (counter == 50);
    }

    public HashMap<Long, Integer> getPrevBoards() {
        return prevBoards;
    }

    public int getCounter() {
        return counter;
    }

    public void setStopChecking(boolean stopChecking) {
        this.stopChecking = stopChecking;
    }
}
