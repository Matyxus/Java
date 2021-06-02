package chess2.board.text;
public enum TextHolder {
    KING("King", 0),
    QUEEN("Queen", 1),
    ROOK("Rook", 2),
    KNIGHT("Knight", 3),
    BISHOP("Bishop", 4),
    PAWN("Pawn", 5),
    A("a", 0),
    B("b", 1),
    C("c", 2),
    D("d", 3),
    E("e", 4),
    F("f", 5),
    G("g", 6),
    H("h", 7),
    WHITE("White", 0), //14
    BLACK("Black", 1); //15

    private final String name;
    private final int index;

    private TextHolder(String name, int index) {
        this.name = name;
        this.index = index;
    }
    
    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
    
}
