package tests;

import java.util.ArrayList;

import board.Fen;
import board.GameBoard;
import board.Spot;

public class FenLauncher {

    
    private final static String[] testCases = {
        "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1",
        "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8",
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
        "r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R",
        "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R",
        "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1",
        "1b6/8/8/3Pp3/8/8/4k2K/8"
    };

    public static void main(String[] args) {
        Fen tmp = new Fen();
        GameBoard gameBoard = new GameBoard(null);
        for (String fen : testCases) {
            ArrayList<Spot> pieces = tmp.interpret(fen);
            pieces.forEach((spot) -> gameBoard.addPiece(spot.getPiece(), spot.getColor(), spot.getSquare()));
            String result = tmp.createFen(gameBoard);
            assert (result.equals(fen));
            gameBoard.reset();
        }
    }
}
