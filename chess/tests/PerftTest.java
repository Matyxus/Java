package tests;

import assets.Pair;
import board.Perft;

// https://www.chessprogramming.org/Perft_Result

public class PerftTest {

    private final Perft perft;
    private long totalTime = 0;
    private long totalNodes = 0;

    public PerftTest() {
        perft = new Perft();
        fullPerftRun();
        System.out.println("**************************** Success ****************************");
        System.out.println("Total moves found: " + totalNodes);
        System.out.println("Total time taken(sec.): " + totalTime);
    }

    private void fullPerftRun() {
        perftScenarion1();
        perftScenarion2();
        perftScenarion3();
        perftScenarion4();
        perftScenarion5();
        perftScenarion6();
        perftScenarioLong();
        perftScenarioAdditional();
    }


    public static void main(String[] args) {
        new PerftTest();
    }


    /**
     * https://www.chessprogramming.org/Perft_Results#Initial_Position
     */
    private void perftScenarion1() {
        System.out.println("**************************** Test 1 ****************************");
        final long[] results = {1, 20, 400, 8902, 197281, 4865609, 119060324};
        for (int i = 1; i < results.length; i++) {
            Pair<Long, Long> tmp = perft.init(i, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[i]);
        }
    }

    /**
     * https://www.chessprogramming.org/Perft_Results#Position_2
     */
    private void perftScenarion2() {
        System.out.println("**************************** Test 2 ****************************");
        final long[] results = {1, 48, 2039, 97862, 4085603, 193690690};
        for (int i = 1; i < results.length; i++) {
            Pair<Long, Long> tmp = perft.init(i, "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[i]);
        }
    }

    /**
     * https://www.chessprogramming.org/Perft_Results#Position_3
     */
    private void perftScenarion3() {
        System.out.println("**************************** Test 3 ****************************");
        final long[] results = {1, 14, 191, 2812, 43238, 674624, 11030083};
        for (int i = 1; i < results.length; i++) {
            Pair<Long, Long> tmp = perft.init(i, "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[i]);
        }
    }

    /**
     * https://www.chessprogramming.org/Perft_Results#Position_4
     */
    private void perftScenarion4() {
        System.out.println("**************************** Test 4 ****************************");
        final long[] results = {1, 6, 264, 9467, 422333, 15833292};
        for (int i = 1; i < results.length; i++) {
            Pair<Long, Long> tmp = perft.init(i, "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[i]);
        }
    }

    /**
     * https://www.chessprogramming.org/Perft_Results#Position_5
     */
    private void perftScenarion5() {
        System.out.println("**************************** Test 5 ****************************");
        final long[] results = {1, 44, 1486, 62379, 2103487, 89941194};
        for (int i = 1; i < results.length; i++) {
            Pair<Long, Long> tmp = perft.init(i, "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[i]);
        }
    }

    /**
     * https://www.chessprogramming.org/Perft_Results#Position_6
     */
    private void perftScenarion6() {
        System.out.println("**************************** Test 6 ****************************");
        final long[] results = {1, 46, 2079, 89890, 3894594, 164075551};
        for (int i = 1; i < results.length; i++) {
            Pair<Long, Long> tmp = perft.init(i, "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 ");
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[i]);
        }
    }

    /*
        Martin Sedlak's test positions:
        http://www.talkchess.com/forum/viewtopic.php?t=47318
    */

    /**
     * More positions
     */
    private void perftScenarioAdditional() {
        System.out.println("**************************** Additional Tests ****************************");
        String[] scenarios = new String[] {
            // Discovered check: 
            "8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1",
            "5K2/8/1Q6/2N5/8/1p2k3/8/8 w - - 0 1",
            // Promote to give check: 
            "4k3/1P6/8/8/8/8/K7/8 w - - 0 1",   
            "8/k7/8/8/8/8/1p6/4K3 b - - 0 1", 
            // Underpromote to check: 
            "8/P1k5/K7/8/8/8/8/8 w - - 0 1",
            "8/8/8/8/8/k7/p1K5/8 b - - 0 1",
            // Self stalemate: 
            "K1k5/8/P7/8/8/8/8/8 w - - 0 1", 
            "8/8/8/8/8/p7/8/k1K5 b - - 0 1", 
            // Stalemate/checkmate: 
            "8/k1P5/8/1K6/8/8/8/8 w - - 0 1",
            "8/8/8/8/1k6/8/K1p5/8 b - - 0 1",
            // Double check: 
            "8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1",
            "8/5k2/8/5N2/5Q2/2K5/8/8 w - - 0 1",
            // Promote out of check
            "2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1",
            "3K4/8/8/8/8/8/4p3/2k2R2 b - - 0 1",
            // Castling prevented 
            "r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1",
            "r3k2r/8/5Q2/8/8/3q4/8/R3K2R w KQkq - 0 1",
            // Lost of castling rights due to rook capture
            "r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1", 
            "r3k2r/7b/8/8/8/8/1B4BQ/R3K2R b KQkq - 0 1",
            // Queen side castling gives check
            "3k4/8/8/8/8/8/8/R3K3 w Q - 0 1", 
            "r3k3/8/8/8/8/8/8/3K4 b q - 0 1",
            // King side castling gives check
            "5k2/8/8/8/8/8/8/4K2R w K - 0 1", 
            "5k2/8/8/8/8/8/8/4K2R w K - 0 1",
            // Enpassant capture checks opponent
            "8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1", 
            "8/5k2/8/2Pp4/2B5/1K6/8/8 w - d6 0 1",
            // Avoid illegel Enpassant
            "3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1",
            "8/8/8/8/k1p4R/8/3P4/3K4 w - - 0 1",
            "8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1",
            "8/b2p2k1/8/2P5/8/4K3/8/8 b - - 0 1"

        };
        int[][] results = new int[][] {
            {
                5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 4, 4,
                6, 6, 4, 4, 4, 4, 6, 6, 6, 6, 6, 6,
                6, 6, 6, 6
            },
            {
                1004658, 1004658, 217342, 217342, 92683, 92683,
                2217, 2217, 567584, 567584, 23527, 23527, 3821001,
                3821001, 1720476, 1720476, 1274206, 1274206, 803711,
                803711, 661072, 661072, 1440467, 1440467, 1134888, 1134888,
                1015133, 1015133
            }
        };
        for (int i = 0; i < results[0].length; i++) {
            Pair<Long, Long> tmp = perft.init(results[0][i], scenarios[i]);
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[1][i]);
        }
    }

    /**
     * John Merlino's test positions:
     * Can take longer
     */
    private void perftScenarioLong() {
        System.out.println("**************************** Longer Tests ****************************");
        String[] scenarios = new String[] {
            "r3k2r/8/8/8/3pPp2/8/8/R3K1RR b KQkq e3 0 1",
            "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
            "8/7p/p5pb/4k3/P1pPn3/8/P5PP/1rB2RK1 b - d3 0 28",
            "8/3K4/2p5/p2b2r1/5k2/8/8/1q6 b - - 1 67",
            "rnbqkb1r/ppppp1pp/7n/4Pp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3",
            "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -",
            "8/p7/8/1P6/K1k3p1/6P1/7P/8 w - -",
            "n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - -",
            "r3k2r/p6p/8/B7/1pp1p3/3b4/P6P/R3K2R w KQkq -",
            "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -",
            "8/5p2/8/2k3P1/p3K3/8/1P6/8 b - -",
            "r3k2r/pb3p2/5npp/n2p4/1p1PPB2/6P1/P2N1PBP/R3K2R w KQkq -",
        };
        int[][] results = new int[][] {
            {6, 6, 6, 7, 6, 5, 8, 6, 6, 7, 8, 5},
            {
                485647607, 706045033, 38633283, 493407574,
                244063299, 193690690, 8103790, 71179139,
                77054993, 178633661, 64451405, 29179893
            }
        };
        for (int i = 0; i < results[0].length; i++) {
            Pair<Long, Long> tmp = perft.init(results[0][i], scenarios[i]);
            totalNodes += tmp.getKey();
            totalTime += tmp.getValue();
            assert (perft.getNodes() == results[1][i]);
        }
    } 
}
