package chess2.managers;
import chess2.PopUps;
import chess2.board.GameBoard;
import chess2.board.Spot;
import chess2.components.FileChooser;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FileManager {
    private final String REGEX = "\\s"; //split by space
    private final String absPath = new File("").getAbsolutePath() + "\\saves\\";
    private HashMap<Integer, Spot> blackPiecesMap;
    private HashMap<Integer, Spot> whitePiecesMap;
    private HashMap<Long, Integer> zobrisHash;
    private PrintWriter fstream;
    private FileChooser fc = null;
    private String fName = null;
    private int currPlayer;
    private String txt = "";
    private boolean retBool = false;
    private boolean enpassant = false;
    private boolean castling = false;
    private long enpassantBit = 0b0L;
    private int counter = 0;

    public FileManager(){};

    public boolean loadFile(GameBoard gameBoard){
        if (fc == null){
            fc = new FileChooser(absPath);
        }
        if (fc != null){
            fName = fc.getFileName();
            if (fName != null && fName.length() > 0){
                fName = fName.replaceFirst("\\.png$", ".txt");
                try {
                    readFile();
                    gameBoard.loadGameBoard(whitePiecesMap, blackPiecesMap, currPlayer);
                    gameBoard.enableCastling(castling);
                    gameBoard.enableEnpassant(enpassant);
                    gameBoard.setEnPassantMoves((currPlayer+1)%2, enpassantBit);
                    gameBoard.getZobrist().setPointer(zobrisHash, counter);
                    retBool = true;
                } catch (IOException e) {
                    PopUps.errorPopUP(e);
                }
            }
        }
        fc = null;
        fName = null;   
        return retBool;
    }

    private void parseHash(String[] input){
        if (input.length == 1){ //"null"
            zobrisHash = null;
            return;
        }
        long key;
        int count;
        for(int i=0; i<input.length-1; i+=2) {
            key = Long.parseLong(input[i]);
            count = Integer.parseInt(input[i+1]);
            zobrisHash.put(key, count);
        }
    }

    private void readFile() throws IOException {
        this.blackPiecesMap = new HashMap<Integer, Spot>();
        this.whitePiecesMap = new HashMap<Integer, Spot>();
        this.zobrisHash = new HashMap<Long, Integer>();
        BufferedReader bi = new BufferedReader(new FileReader(absPath+fName));
        String[] strNums;
        String temp;
        // game info
        strNums = bi.readLine().split(REGEX);
        if (strNums[0].equals("WHITE")){
            currPlayer = 0;
        } else {
            currPlayer = 1;
        }
        enpassant = Boolean.parseBoolean(strNums[1]);
        castling = Boolean.parseBoolean(strNums[2]);
        PopUps.gameEnd = Boolean.parseBoolean(strNums[3]);
        PopUps.AI = Boolean.parseBoolean(strNums[4]);
        counter = Integer.parseInt(strNums[5]);
        //time + round
        strNums = bi.readLine().split(REGEX);
        PopUps.time = Long.parseLong(strNums[0]);
        if (PopUps.time != 0){
            PopUps.currPlayerTime = Long.parseLong(strNums[1]);
        }
        PopUps.round = Integer.parseInt(strNums[2]);
        //board
        strNums = bi.readLine().split(REGEX);
        parsePieces(strNums, 0);
        strNums = bi.readLine().split(REGEX);
        parsePieces(strNums, 1);
        //hashes
        strNums = bi.readLine().split(REGEX);
        parseHash(strNums);
        //get enpassant
        if (enpassant){
            enpassantBit = Long.parseLong(bi.readLine());
        }
        //text
        while ((temp = bi.readLine()) != null){
            txt += temp+"\n";
        }
        if (txt != null && txt.length() > 1){
            txt = txt.substring(0, txt.length()-1); // remove last "\n"
        }
        bi.close();
    }
    private void parsePieces(String[] input, int color){
        int square;
        int piece;
        boolean moved;
        for(int i=0; i < input.length-2; i+=3) {
            square = Integer.parseInt(input[i]);
            piece = Integer.parseInt(input[i+1]);
            moved = Boolean.parseBoolean(input[i+2]);
            Spot temp = new Spot(piece, color, moved);
            if (color == 0){
                this.whitePiecesMap.put(square, temp);
            }else {
                this.blackPiecesMap.put(square, temp);
            }
        }
    }

    public void safeFile(String fileName, String text, GameBoard board){
        String path = absPath+fileName+".txt";
        File f = new File(path);
        try { 
            f.createNewFile();
         } catch (IOException e){ 
            PopUps.errorPopUP(e);
            return; 
        }
        try { 
            fstream = new PrintWriter(path, "UTF-8");
            // player, game  info
            if (board.getCurrentPlayer() == 0){
                fstream.write("WHITE");
            } else {
                fstream.write("BLACK");
            }
            fstream.write(" "+PopUps.enpassant+" "+PopUps.castling+" "+PopUps.gameEnd+" "+PopUps.AI+" "+board.getZobrist().getCounter());
            fstream.write("\n");
            //time + round
            fstream.write(""+PopUps.time + " "+PopUps.currPlayerTime+ " "+PopUps.round);
            fstream.write("\n");
            //board
            board.getWhitePieces().forEach((square,spot)->fstream.write(square+" "+
                spot.getPiece()+" "+spot.getHasMoved()+" "));
            fstream.write("\n");
            board.getBlackPieces().forEach((square,spot)->fstream.write(square+" "+
                spot.getPiece()+" "+spot.getHasMoved()+" "));
            fstream.write("\n");
            //hashed positions
            if (board.getZobrist().getPointer() != null){
                board.getZobrist().getPointer().forEach((hash, count)->fstream.write(hash+" "+count+" "));
            } else {
                fstream.write("null");
            }
            fstream.write("\n");
            // enpassan board
            if (board.isEnpassant()){
                fstream.write(""+board.getEnPassantMoves((board.getCurrentPlayer()+1)%2));
                fstream.write("\n");
            }
            fstream.write(text); //text representation of game
        } catch (Exception e) {
            PopUps.errorPopUP(e);
        }
        fstream.close();
    }

    public void clean(){
        this.whitePiecesMap.clear();
        this.blackPiecesMap.clear();
        this.zobrisHash = null;
        txt = "";
        enpassantBit = 0b0L;
        retBool = false;
        counter = 0;
    }
    // GETTERS AND SETTERS
	public String getText() {
		return txt;
    }

    public String getAbsPath() {
        return absPath;
    }
}