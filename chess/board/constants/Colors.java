package board.constants;
/**
 * Class holding values of colors assigned to players
 */
public class Colors {
    public final static int WHITE       = 0;
    public final static int BLACK       = 1;
    public final static int COLOR_COUNT = 2;

    public final static String[] COLOR_TO_STRING = {
        "White", "Black"
    };

    public final static int opposite_color(int color) {
        return (color+1) & 1;
    }
}
