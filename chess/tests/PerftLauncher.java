package tests;
import board.Perft;

public class PerftLauncher {

    public static void main(String[] args) {
        Perft perft = new Perft();
        perft.init(6, false);
    }
    
}
