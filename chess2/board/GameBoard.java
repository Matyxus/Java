package chess2.board;
import java.util.HashMap;

public class GameBoard {
    private final Rays rays;
    private final Zobrist zobrist;
    private final Pieces pieces;
    // clickedSpot is not safe, and shoud not be manipulated, its reference gets changed all the time in game
    private Spot clickedSpot = null; 
    private long[] allPieces;
    private long[][] allIndividualPieces;
    public long blockers;
    private int[] kingsPos;
    private HashMap<Integer, Spot> whitePieces; 
    private HashMap<Integer, Spot> blackPieces;
    private int currentPlayer = 0;
    private boolean canStart = false;
    private int[][] piecesCounter;
    private long[] enPassantMoves = null;
    private int removedPiece;
    private int removedPieceSquare;
    private int whiteEnpassantSquare;
    private int blackEnapssantSquare;
    private int enpassantSquare = 0;
    private int rookPos = -1;
    private boolean castling;

    public GameBoard() {
        rays = new Rays();
        zobrist = new Zobrist(this);
        init();
        this.pieces = new Pieces(this, rays);
    }

    private void init() {
        this.piecesCounter = new int[2][6];
        this.whitePieces = new HashMap<Integer, Spot>();
        this.blackPieces = new HashMap<Integer, Spot>();
        this.kingsPos = new int[2];
        this.allIndividualPieces = new long[2][6];
        this.allPieces = new long[2];
        resetVals();
    }

    private void resetVals() {
        whitePieces.clear();
        blackPieces.clear();
        kingsPos[Holder.WHITE] = 0;
        kingsPos[Holder.BLACK] = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                allIndividualPieces[i][j] = Holder.ZERO;
                piecesCounter[i][j] = 0;
            }
        }
        allPieces[Holder.WHITE] = Holder.ZERO;
        allPieces[Holder.BLACK] = Holder.ZERO;
        blockers = Holder.ZERO;
    }
    
    public void loadGameBoard(HashMap<Integer, Spot> white, HashMap<Integer, Spot> black, int player) {
        resetVals();
        white.forEach((square, spot)->addPiece(spot.getPiece(), Holder.WHITE, square, spot.getHasMoved()));
        black.forEach((square, spot)->addPiece(spot.getPiece(), Holder.BLACK, square, spot.getHasMoved()));
        kingsPos[Holder.WHITE] = Long.numberOfTrailingZeros(allIndividualPieces[Holder.WHITE][Holder.KING]);
        kingsPos[Holder.BLACK] = Long.numberOfTrailingZeros(allIndividualPieces[Holder.BLACK][Holder.KING]);
        this.currentPlayer = player;
        start();
    }

    public void addPiece(int piece, int color, int square, boolean hasMoved) {
        long targetSquare = Holder.ONE << square;
        piecesCounter[color][piece]++;
        //All
        blockers |= targetSquare;
        //All*color*
        allPieces[color] |= targetSquare;
        //Individ
        allIndividualPieces[color][piece] |= targetSquare;
        //Hash
        if (color == Holder.WHITE) {
            whitePieces.put(square, new Spot(piece, color, hasMoved));
        } else {
            blackPieces.put(square, new Spot(piece, color, hasMoved));
        }
    }

    public void removePiece(int color, int square) {
        //Hash
        if (color == Holder.WHITE) {
            removedPiece = whitePieces.get(square).getPiece();
            whitePieces.remove(square);
        } else {
            removedPiece = blackPieces.get(square).getPiece();
            blackPieces.remove(square);
        }
        piecesCounter[color][removedPiece]--;
        long targetSquare = Holder.ONE << square;
        //All*color*
        allPieces[color] ^= targetSquare;
        //All
        blockers ^= targetSquare;
        //Individ
        allIndividualPieces[color][removedPiece] ^= targetSquare;
    }
    // CHECK GAME PLAYABILITY
    public boolean canPlay() {
        boolean start = false;
        int piecesCount = whitePieces.size()+blackPieces.size();
        if (piecesCount > 2 && kingsArePresent()) {
            updateWhite();
            start = checkMoves(pieces.getCurrPlayerInCheck(), whitePieces, Holder.WHITE);
            canStart = false;
            if (start) {
                updateBlack();
                start = checkMoves(pieces.getCurrPlayerInCheck(), blackPieces, Holder.BLACK);
            }
        }
        return start;
    }

    private boolean checkMoves(int check, HashMap<Integer, Spot> curr, int player) {
        if (check == 0){
            return true;
        }
        if (check == 1){ // atlease 1 piece can block/capture enemy
            curr.forEach((square, spot) -> {
                if (spot.getMoves() != 0) {
                    canStart = true;
                    return;
                }
            });
            return canStart;
        }
        if (check == 2){ // king can move
            int kingSquare = getKingsPos(player);
            if (curr.get(kingSquare).getMoves() != 0) {
                return true;
            }   
        }
        return false;        
    }

    public boolean kingsArePresent() {//Need to know if there is exactly one king
        int white = piecesCounter[Holder.WHITE][Holder.KING];
        int black = piecesCounter[Holder.BLACK][Holder.KING];
        return (white == black && white == 1);
    }
    // END OF CHECKING
    //
    public void updateGame() {
        this.currentPlayer = (currentPlayer+1)%2;
        if (enPassantMoves != null){
            enPassantMoves[currentPlayer] = 0;
        }
        start();
    }

    public void start() {
        if (currentPlayer == 0){
            updateWhite();
        } else {
            updateBlack();
        }
    }

    public void updateWhite() {
        pieces.updatePiecesMoves(whitePieces, blackPieces, Holder.WHITE);
    }

    public void updateBlack() {
        pieces.updatePiecesMoves(blackPieces, whitePieces, Holder.BLACK);
    }
    
    public int movePiece(int fromSquare, int toSquare, int piece) {
        removePiece(currentPlayer, fromSquare);
        removedPiece = -1;
        if (whitePieces.containsKey(toSquare)) {
            removePiece(Holder.WHITE, toSquare);
        } else if (blackPieces.containsKey(toSquare)) {
            removePiece(Holder.BLACK, toSquare);
        }
        addPiece(piece, currentPlayer, toSquare, true);
        removedPieceSquare = toSquare;
        // castling
        if (piece == Holder.KING) {
            kingsPos[currentPlayer] = toSquare;
            int distance = toSquare-fromSquare;
            int shift = -1;
            if (distance == 2){ // castling if +/-2
                rookPos = pieces.getRookCastleSquares(1);// right side
                removePiece(currentPlayer, rookPos);
                removedPiece = -1;
                addPiece(Holder.ROOK, currentPlayer, toSquare+shift, true);
            } else if (distance == -2){
                rookPos = pieces.getRookCastleSquares(0);// left side "Queen castling"
                removePiece(currentPlayer, rookPos);
                removedPiece = -1;
                addPiece(Holder.ROOK, currentPlayer, toSquare-shift, true);
            }
        }
        // enpassant
        if (enPassantMoves != null && piece == Holder.PAWN){ // přidat funkci co se po 20 +- tazích koukne jestli je enpassant ještě možnej
            int distance = toSquare-fromSquare;
            if (removedPiece == -1 && ((blockers & enPassantMoves[(currentPlayer+1)%2])!=0)) {
                removedPiece = Holder.PAWN; // enpassant capture
                removedPieceSquare = enpassantSquare; // !!
                removePiece((currentPlayer+1)%2, removedPieceSquare);
            } else if (distance == 16){ // player used 2 square move
                enPassantMoves[currentPlayer] |= (Holder.ONE << (toSquare-Holder.ROWS));//black
            } else if (distance == -16){
                enPassantMoves[currentPlayer] |= (Holder.ONE << (toSquare+Holder.ROWS));//white
            }
            enpassantSquare = toSquare;
        }
        return removedPiece;
    }

    public void clearEnpassant(int player) {
        enPassantMoves[player] = Holder.ZERO;
    }

    public void promote(int piece, int color, int square) {
        removePiece(color, square);
        addPiece(piece, color, square, true);
    }

    public boolean clickedOnPiece(int square) {
        if (whitePieces.containsKey(square)){
            clickedSpot = whitePieces.get(square);
            return true;
        }
        if (blackPieces.containsKey(square)){
            clickedSpot = blackPieces.get(square);
            return true;
        }
        return false;
    }

    public boolean clickedValidPiece(int square) {
        if (clickedOnPiece(square) && getCurrentPlayer() == clickedSpot.getColor()) {
            if (getCheck() == 2 && clickedSpot.getPiece() != Holder.KING){
                return false;
            }
            return true;
        }
        return false;
    }

    public int makeMove(int piece, int result, int square, int tempColor){
        if (tempColor == Holder.WHITE){
            return moveWhitePiece(square, result, piece);
        } else {
            return moveBlackPiece(square, result, piece);
        }
    }

    public void placePiece(int piece, int color, int square, boolean hasMoved) {
        if (whitePieces.containsKey(square)) {
            removePiece(Holder.WHITE, square);
        } else if (blackPieces.containsKey(square)) {
            removePiece(Holder.BLACK, square);
        }
        addPiece(piece, color, square, hasMoved);
    }

    public boolean possibleEnpassant(){
        if ((allIndividualPieces[Holder.WHITE][Holder.PAWN] & Holder.RANK_7) == 0 && 
            (allIndividualPieces[Holder.BLACK][Holder.PAWN] & Holder.RANK_2) == 0 ) {
            enableEnpassant(false);
            return false;
        }
        return true;
    }

    //GETTERS && SETTERS
    public int getRookCastlePos() {
        int temp = rookPos;
        rookPos = -1;
        return temp;
    }

    public int getEnpassantSquare() {
        return enpassantSquare;
    }

    public long[][] getAll(){
        return allIndividualPieces;
    }

    public int getRemovedPieceSquare() {
        return removedPieceSquare;
    }

    public void enableCastling(boolean castling) {
        pieces.setCastling(castling);
        this.castling = castling;
    }

    public void enableEnpassant(boolean enpassant) {
        if (enpassant){
            enPassantMoves = new long[2];
            enPassantMoves[Holder.WHITE] = Holder.ZERO;
            enPassantMoves[Holder.BLACK] = Holder.ZERO;
        } else {
            enPassantMoves = null;
        }
        pieces.setEnpassant(enpassant);
    }

    public Rays getRays() {
        return this.rays;
    }

    public Zobrist getZobrist() {
        return this.zobrist;
    }

    public Pieces getPieces() {
        return this.pieces;
    }

    public boolean isEnpassant() {
        return (enPassantMoves != null);
    }

    public boolean isCastling() {
        return castling;
    }

    public long getAllPieces(int color) {
        return allPieces[color];
    }

    public long getAllIndividualPieces(int piece, int color) {
        return allIndividualPieces[color][piece];
    }

    public int getKingsPos(int color) {
        return Long.numberOfTrailingZeros(allIndividualPieces[color][Holder.KING]);
    }

    public long getEnPassantMoves(int player) {
        return enPassantMoves[player];
    }

    public long getBlockersWithouKing(int color) {
        return (blockers ^ allIndividualPieces[color][Holder.KING]);
    }
    
    public HashMap<Integer, Spot> getWhitePieces() {
        return whitePieces;
    }

    public int getPiecesCount(int color, int piece) {
        return piecesCounter[color][piece];
    }

    public HashMap<Integer, Spot> getBlackPieces() {
        return blackPieces;
    }

    public HashMap<Integer, Spot> getCurrPlayerPieces() {
        return (currentPlayer == 0) ? whitePieces:blackPieces;
    }

    public HashMap<Integer, Spot> getPlayerPieces(int player){
        if (player == Holder.WHITE){
            return whitePieces;
        } else {
            return blackPieces;
        }
    }
    
    public int getBlackEnapssantSquare() {
        return blackEnapssantSquare;
    }

    public int getWhiteEnpassantSquare() {
        return whiteEnpassantSquare;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCheck(){
        return pieces.getCurrPlayerInCheck();
    }

    public void setEnPassantMoves(int player, long bitMoves) {
        if (bitMoves != 0){
            enPassantMoves[player] = bitMoves;
            enpassantSquare = Long.numberOfTrailingZeros(bitMoves);
            if ((bitMoves & Holder.RANK_3)!=0) {
                enpassantSquare += Holder.ROWS; // Black
            } else {
                enpassantSquare -= Holder.ROWS; // White
            }
        }
    }

    public int getClickedPiece() {
        return clickedSpot.getPiece();
    }

    public int getClickedPieceColor() {
        return clickedSpot.getColor();
    }

    public long getClickedPieceMoves() {
        return clickedSpot.getMoves();
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    
    public int moveWhitePiece(int fromSquare, int toSquare, int piece) {
        removePiece(Holder.WHITE, fromSquare);
        removedPiece = -1;
        if (blackPieces.containsKey(toSquare)) {
            removePiece(Holder.BLACK, toSquare);
        }
        addPiece(piece, Holder.WHITE, toSquare, true);
        removedPieceSquare = toSquare;
        // enpassant
        if (enPassantMoves != null && piece == Holder.PAWN) {
            int result = toSquare-fromSquare;
            if (removedPiece == -1 && ((blockers & enPassantMoves[Holder.BLACK]) != 0)) {
                removedPiece = Holder.PAWN;
                removedPieceSquare = toSquare+Holder.ROWS;
                removePiece(Holder.BLACK, removedPieceSquare);
            }  else if (result == -16) { // pawn used 2 square move
                enPassantMoves[Holder.WHITE] |= (Holder.ONE << (toSquare+Holder.ROWS));//white
            }
        }
        return removedPiece;
    }

    public int moveBlackPiece(int fromSquare, int toSquare, int piece) {
        removePiece(Holder.BLACK, fromSquare);
        removedPiece = -1;
        if (whitePieces.containsKey(toSquare)) { 
            removePiece(Holder.WHITE, toSquare);
        }
        addPiece(piece, Holder.BLACK, toSquare, true);
        removedPieceSquare = toSquare;
        // enpassant
        if (enPassantMoves != null && piece == Holder.PAWN) {
            int result = toSquare-fromSquare;
            if (removedPiece == -1 && ((blockers & enPassantMoves[Holder.WHITE])!=0)) {
                removedPiece = Holder.PAWN;
                removedPieceSquare = toSquare-Holder.ROWS;
                removePiece(Holder.WHITE, removedPieceSquare);
            } else if (result == 16) { // pawn used 2 square move
                enPassantMoves[Holder.BLACK] |= (Holder.ONE << (toSquare-Holder.ROWS));//black
            }
        }
        return removedPiece;
    }



    public int moveHashWhitePiece(int fromSquare, int toSquare, int piece) {
        enPassantMoves[Holder.WHITE] = 0;
        Zobrist.currEnpassant = 0; // always zero, unless not
        removePiece(Holder.WHITE, fromSquare);
        removedPiece = -1;
        if (blackPieces.containsKey(toSquare)) {
            removePiece(Holder.BLACK, toSquare); 
            Zobrist.zobrist ^= Zobrist.piecesHash[removedPiece+Zobrist.PIECE_OFFSET][toSquare]; // removePiece hash
        }
        addPiece(piece, Holder.WHITE, toSquare, true);
        removedPieceSquare = toSquare;
        // enpassant
        if (enPassantMoves != null && piece == Holder.PAWN) {
            int result = toSquare-fromSquare;
            if (removedPiece == -1 && ((blockers & enPassantMoves[Holder.BLACK])!=0)) {
                removedPiece = Holder.PAWN;
                removedPieceSquare = toSquare+Holder.ROWS;
                removePiece(Holder.BLACK, removedPieceSquare);
                Zobrist.zobrist ^= Zobrist.piecesHash[Holder.PAWN+Zobrist.PIECE_OFFSET][removedPieceSquare]; // removeEnpassantPiece Hash
            }  else if (result == -16) {
                enPassantMoves[Holder.WHITE] |= (Holder.ONE << (toSquare+Holder.ROWS));//white
                // if enpassant capture is possible
                if (((rays.getPawn(toSquare+Holder.ROWS, Holder.WHITE)) & allIndividualPieces[Holder.BLACK][Holder.PAWN]) != 0){
                    long enpHash = Zobrist.EN_PASSANT_FILES[(toSquare)%8];// +8 is unnecessary
                    Zobrist.currEnpassant = enpHash; // record curr enpassant
                    Zobrist.zobrist ^= enpHash; // add possible hash for enpassant
                }
            }
        }
        if (Zobrist.prevEnpassant != Zobrist.currEnpassant){
            if (Zobrist.prevEnpassant != 0){
                Zobrist.zobrist ^= Zobrist.prevEnpassant; // remove prev enpassant
                if (Zobrist.currEnpassant == 0){
                    Zobrist.prevEnpassant = 0; // no previous enpassant recorded
                }
            }
            if (Zobrist.currEnpassant != 0){ // already added
                Zobrist.prevEnpassant = Zobrist.currEnpassant; // prev = current
            }
        }
        return removedPiece;
    }

    public int moveHashBlackPiece(int fromSquare, int toSquare, int piece) {
        enPassantMoves[Holder.BLACK] = 0;
        Zobrist.currEnpassant = 0; // always zero, unless not
        removePiece(Holder.BLACK, fromSquare);
        removedPiece = -1;
        if (whitePieces.containsKey(toSquare)) { 
            removePiece(Holder.WHITE, toSquare);
            Zobrist.zobrist ^= Zobrist.piecesHash[removedPiece][toSquare]; // removePiece hash
        }
        addPiece(piece, Holder.BLACK, toSquare, true);
        removedPieceSquare = toSquare;
        // enpassant
        if (enPassantMoves != null && piece == Holder.PAWN) {
            int result = toSquare-fromSquare;
            if (removedPiece == -1 && ((blockers & enPassantMoves[Holder.WHITE])!=0)) {
                removedPiece = Holder.PAWN;
                removedPieceSquare = toSquare-Holder.ROWS;
                removePiece(Holder.WHITE, removedPieceSquare);
                Zobrist.zobrist ^= Zobrist.piecesHash[Holder.PAWN][removedPieceSquare]; // removeEnpassantPiece
            } else if (result == 16) { // player used 2 square move
                enPassantMoves[Holder.BLACK] |= (Holder.ONE << (toSquare-Holder.ROWS));//black
                if (((rays.getPawn(toSquare-Holder.ROWS, Holder.BLACK)) & allIndividualPieces[Holder.WHITE][Holder.PAWN]) != 0){
                    long enpHash = Zobrist.EN_PASSANT_FILES[(toSquare)%8]; // -8 is unnecessary
                    Zobrist.currEnpassant = enpHash; // record curr enpassant
                    Zobrist.zobrist ^= enpHash; // add possible hash for enpassant
                }
            }
        }
        if (Zobrist.prevEnpassant != Zobrist.currEnpassant){ // if enpassant == null, prev == curr == 0
            if (Zobrist.prevEnpassant != 0){
                Zobrist.zobrist ^= Zobrist.prevEnpassant; // remove prev enpassant
                if (Zobrist.currEnpassant == 0){
                    Zobrist.prevEnpassant = 0; // no previous enpassant recorded
                }
            }
            if (Zobrist.currEnpassant != 0){ // already added
                Zobrist.prevEnpassant = Zobrist.currEnpassant; // prev = current
            }
        }
        return removedPiece;
    }
}
