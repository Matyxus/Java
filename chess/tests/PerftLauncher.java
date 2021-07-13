package tests;
import board.Perft;
import board.constants.Colors;

public class PerftLauncher {

    public static void main(String[] args) {
        Perft perft = new Perft();
        perft.init(6, Colors.WHITE, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    }
    
}
