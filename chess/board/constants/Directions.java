package board.constants;

public enum Directions {
    // Orthogonal.
    SOUTH,
    NORTH,
    EAST,
    WEST,
    // Diagonal.
    SOUTH_WEST,
    SOUTH_EAST,
    NORTH_WEST,
    NORTH_EAST;
    
    public int[] getOrthogonal() {
        return new int[]{SOUTH.ordinal(), NORTH.ordinal(), EAST.ordinal(), WEST.ordinal()};
    }

    public int[] getDiagonal() {
        return new int[]{SOUTH_WEST.ordinal(), SOUTH_EAST.ordinal(), NORTH_WEST.ordinal(), NORTH_EAST.ordinal()};
    }

}