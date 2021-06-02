package chess2.board.text;
import chess2.PopUps;
import chess2.main.Handler;

public class TextBoard {
    private final Handler handler;
    private final int COLOR_OFFSET = 14;
    private final int WORD_OFFSET = 6;
    private final int COL = 8; // == row

    public TextBoard(Handler handler){
        this.handler = handler;
    }
    
    public void writeMove(int color, int piece, int fromSquare, int toSquare, int round){
        handler.getGame().getDisplay().appendText("#Move "+round+"\n");
        handler.getGame().getDisplay().appendText(getColor(color)+
            ":"+getPieceName(piece)+"("+getLetter(x(fromSquare))+
            ""+(COL-y(fromSquare))+")" + " → " + "("+getLetter(x(toSquare))
            +""+(COL-y(toSquare))+")\n");
    }

    public void writeCapture(int color, int piece, int square){
        handler.getGame().getDisplay().appendText("Capture:"+getColor(opposite(color))+" "+
            getPieceName(piece)+" ("+getLetter(x(square))+""+(COL-y(square))+")\n");
    }

    public void writeCastle(int square, boolean right){
        if (right){
            handler.getGame().getDisplay().appendText("Castling → right("+
                getLetter(x(square))+""+(COL-y(square))+")\n");
        } else {
            handler.getGame().getDisplay().appendText("Castling → left("+
                getLetter(x(square))+""+(COL-y(square))+")\n");
        }
    }

    public void writePromotion(int piece){
        handler.getGame().getDisplay().appendText("Promotion → "+getPieceName(piece)+"\n");
    }

    public void writeWin(int color, boolean ai){
        if (ai && PopUps.aiCol == opposite(color)){
            handler.getGame().getDisplay().appendText("AI won");
            PopUps.plainMessagePopUP("AI won");
        } else {
            handler.getGame().getDisplay().appendText("Player "+getColor(opposite(color))+" won");
            PopUps.plainMessagePopUP("Player "+getColor(opposite(color))+" won");
        }
    }

    public void writeDraw(int result){
        switch (result) {
            case 1:
                handler.getGame().getDisplay().appendText("Draw-insufficient material");
                break;
            case 2:
                handler.getGame().getDisplay().appendText("Draw-stalemate");
                break;
            case 3:
                handler.getGame().getDisplay().appendText("Draw-Fifty-move rule");
                break;
            case 4:
                handler.getGame().getDisplay().appendText("Draw-Threefold repetition");
                break;
            case 5:
                handler.getGame().getDisplay().appendText("Draw-Fivefold repetition");
                break;
            default:
                break;
        }
    }

    public void initGame(int color, boolean ai){
        String txt = "";
        if (ai){
            txt += "Human("+getColor(opposite(color))+") vs "+"AI("+getColor(color)+")\n";
        } else {
            txt += "Human vs Human\n";
        }
        handler.getGame().getDisplay().appendText(txt);
    }

    public String getColor(int col) {
        return TextHolder.values()[col+COLOR_OFFSET].getName();
    }

    private String getPieceName(Integer piece) {
        return TextHolder.values()[piece].getName();
    }

    public int getPieceIndex(String pieceName){
        return TextHolder.valueOf(pieceName.toUpperCase()).getIndex();
    }

    public int getReverseColor(String color) {
        return TextHolder.valueOf(color.toUpperCase()).getIndex();
    }

    private String getLetter(int squareX){
        return TextHolder.values()[squareX+WORD_OFFSET].getName();
    }

    public int getSquare(String location){
        String xPos = location.substring(0, 1);
        String yPos = location.substring(1, 2);
        int xSquare = TextHolder.valueOf(xPos.toUpperCase()).getIndex();
        return xSquare + COL*(COL-Integer.parseInt(yPos));
    }

    private final int x(int square){
        return square%COL;
    }

    private final int y(int square){
        return square/COL;
    }

    private final int opposite(int color){
        return (color+1)%2;
    }
}
