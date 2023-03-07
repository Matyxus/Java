package gameboard.constants;
/**
 * Class holding values of colors assigned to players/pieces
 */
public class Colors {
    public final static int WHITE       = 0;
    public final static int BLACK       = 1;
    public final static int COLOR_COUNT = 2;

    public final static String[] COLOR_TO_STRING = {"White", "Black"};
    public final static int[] COLORS = {WHITE, BLACK};

    /**
     * @param color of player / piece
     * @return opposite color of given one
     */
    public final static int oppositeColor(int color) {
        return (color + 1) & 1;
    }
}
