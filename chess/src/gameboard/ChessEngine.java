package gameboard;

import gameboard.history.GameHistory;
import gameboard.history.Fen;
import gameboard.move_gen.Move;
import gameboard.move_gen.MoveGen;
import utils.Pair;

import java.util.ArrayList;


public class ChessEngine {
    // load & save
    private final GameBoard gameBoard;
    private final MoveGen moveGen;
    private final GameHistory gameHistory;
    private final Fen fen;
    private final ArrayList<Move> move_list; // All legal moves of current player
    // private final Settings setttings;

    public ChessEngine() {
        gameBoard = new GameBoard();
        moveGen = new MoveGen(gameBoard.getPlayers());
        gameHistory = new GameHistory();
        fen = new Fen();
        move_list = new ArrayList<Move>();
    }

    public static void main(String[] args) {
        ChessEngine engine = new ChessEngine();
        boolean success = engine.loadGame("test");
        System.out.println("Sucessfully loaded game: " + success);
    }

    /**
     * Starts the game with the given setting
     */
    public void startGame() {
        gameHistory.recordBoard(gameBoard.getCurrentPositon(), generateFEN());
        // Update pieces for current player
        move_list.clear();
        moveGen.updatePiecesMoves(gameBoard.getCurrentPositon(), move_list);
    }

    /**
     * Executes move and records it in GameHistory class
     * @param move to be played
     */
    public void playMove(Move move) {
        if (move == null || !move_list.contains(move)) {
            System.err.println("Invalid move: " + move);
            return;
        }
        Pair<Integer, Integer> piece = gameBoard.containsPiece(move.getFromSquare());
        if (piece == null) {
            System.err.println("Invalid move, does not move piece: " + move);
            return;
        }
        // Apply and record move
        int capture = gameBoard.applyMove(move);
        gameHistory.recordBoard(gameBoard.getCurrentPositon(), generateFEN());
        gameHistory.recordMove(piece.getKey(), piece.getValue(), capture, move);
        // Update pieces for opposite player
        moveGen.updatePiecesMoves(gameBoard.getCurrentPositon(), move_list);
        return;
   }

   // ------------------------ Switching states ------------------------ 

    /**
     * Returns game to previous state
     * @return true on success, false otherwise
     */
    public boolean previousState() {
        // There is no history of previous round
        if (!gameHistory.previousRound()) {
            return false;
        }
        Pair<Move, Integer> previousMove = gameHistory.getCurrentMove();
        Position previousPosition = gameHistory.getCurrentPosition();
        // Only when we return to initial board state (since there is no Move recorded)
        if (previousMove == null || previousPosition == null) {
            return false;
        }
        gameBoard.undoMove(previousMove.getKey(), previousMove.getValue(), previousPosition);
        return true;
    }

    /**
     * Changes game to future state
     * @return true on success, false otherwise
     */
    public boolean nextState() {
        // There is no history of previous round
        if (!gameHistory.nextRound()) {
            return false;
        }
        Pair<Move, Integer> nextMove = gameHistory.getCurrentMove();
        assert (nextMove != null);
        gameBoard.applyMove(nextMove.getKey());
        return true;
    }

    // ------------------------ Load & save ------------------------ 

    /**
     * @param fileName from which game will be loaded (ROOT/data/saves) or can be FEN
     * @return true on success, false otherwise
     */
    public boolean loadGame(String fileName) {
        

        return true;
    }

    /**
     * @param fileName to which the game will be saved (ROOT/data/saves)
     * @return true on success, false otherwise
     */
    public boolean saveGame(String fileName) {
        return true;
    }

    // ------------------------ Getters ------------------------ 

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public GameHistory getGameHistory() {
        return gameHistory;
    }

    public ArrayList<Move> getMove_list() {
        return move_list;
    }

    // ------------------------ Utils ------------------------ 

    /**
     * @return FEN string of current chess board
     */
    public String generateFEN() {
        return fen.createFen(gameBoard.getPieces(), gameBoard.getCurrentPositon());
    }
}
