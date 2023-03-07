package gameboard;

import gameboard.history.GameHistory;
import gameboard.history.Fen;
import gameboard.move_gen.Move;
import gameboard.move_gen.MoveGen;
import java.util.ArrayList;

public class ChessGame {
    // load & save
    private final GameBoard gameBoard;
    private final MoveGen moveGen;
    private final GameHistory GameHistory;
    private final Fen fen;
    private final ArrayList<Move> move_list; // All legal moves of current player
    // private final Settings setttings;

    public ChessGame() {
        gameBoard = new GameBoard();
        moveGen = new MoveGen(gameBoard.getPlayers());
        GameHistory = new GameHistory();
        fen = new Fen();
        move_list = new ArrayList<Move>();
    }

    /**
     * Executes move and records its
     * @param move to be played
     * @return representation of move (String with unicode characters for pieces),
     * null if invalid Move is being played
     */
    public String playMove(Move move) {
        if (move == null ||!move_list.contains(move)) {
            System.err.println("Invalid move: " + move);
            return null;
        }
        int capture = gameBoard.applyMove(move);
        // Record move
        // Update pieces for opposite player
        moveGen.updatePiecesMoves(gameBoard.getCurrentPositon(), move_list);
        return "";
   }

    /**
     * Returns game to previous state
     */
    public void previousState() {

    }

    /**
     * Changes game to future state
     */
    public void nextState() {

    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public MoveGen getMoveGen() {
        return moveGen;
    }

    public GameHistory getGameHistory() {
        return GameHistory;
    }

    public Fen getFen() {
        return fen;
    }  

    public ArrayList<Move> getMove_list() {
        return move_list;
    }
}
