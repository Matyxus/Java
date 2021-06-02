package chess2.board;
import java.util.HashMap;

public final class Pieces {
    private final Rays ray;
    private final GameBoard board;
    private final HashMap<Integer, Long> pins;
    private final TrueMagic trueMagic;
    private int kingSquare; 

	private boolean enpassant = false;
	private boolean castling = false;
    
    private long pathToBlock = Holder.ZERO;
    private int currPlayerInCheck = 0;
    private long[] attacks;
    
    private boolean[] playersCastling;
    private int[] rookCastleSquares;
    private int enemyPlayer;
    private int currPlayer;
    private int enpassantSquare = 0;
    private long currAttacks = 0;
    
	public Pieces(GameBoard board, Rays rays) {
        this.attacks = new long[2];//bit-map of all attacks of white, black player
		this.ray = rays;
        this.board = board;
        this.pins = new HashMap<Integer, Long>();
        this.trueMagic = new TrueMagic(rays);
        this.attacks[Holder.WHITE] = 0;
        this.attacks[Holder.BLACK] = 0;
    }

    private void createCastling(){
        rookCastleSquares[0] = -1;
        rookCastleSquares[1] = -1;
    }
    
    public void updatePiecesMoves(HashMap<Integer, Spot> curr, HashMap<Integer, Spot> enemy, int currPlayer) {
        resetVals();//reset
        this.currPlayer = currPlayer;
        enemyPlayer = (currPlayer+1)%2;
        // first generate enemy
        enemy.forEach((square, spot)->generateAttacks(spot.getPiece(), square));
        kingSquare = board.getKingsPos(currPlayer);
        checkForCheck(); // check if king is in check
        if (currPlayerInCheck == 2){
            generateKing(curr);
            return; // only king can play
        }
        curr.forEach((square, spot)->{
            spot.setMoves(generateMoves(spot.getPiece(), square));
            spot.setAttacks(currAttacks); // moves including my own pieces, needed in evaluation
        });
        //determine pinned pieces and their moves
        if (curr.containsKey(kingSquare)){
            checkForPinned();
        }
        
        if (currPlayerInCheck == 1){
            curr.forEach((square, spot)->{
                if (!(pins.containsKey(square))){ // if there is check, pins moves are already 0
                    spot.modifyMoves(pathToBlock);
                }
            });
            if (enpassantSquare != 0) { // pawn can take enemy pawn giving check using enpassant
                if (curr.containsKey(enpassantSquare+1) && 
                        curr.get(enpassantSquare+1).getPiece() == Holder.PAWN) {
                            
                    curr.get(enpassantSquare+1).setMoves(board.getEnPassantMoves(enemyPlayer));
                }
                if (curr.containsKey(enpassantSquare-1) && 
                        curr.get(enpassantSquare-1).getPiece() == Holder.PAWN) {

                    curr.get(enpassantSquare-1).setMoves(board.getEnPassantMoves(enemyPlayer));
                }
            }
        }
        //Update pinned pieces moves
        pins.forEach((square, move)->curr.get(square).modifyMoves(move));
        generateKing(curr);
    }

    private void checkForPinned() {
        long searchingFor = (board.getAllIndividualPieces(Holder.QUEEN, enemyPlayer) | 
            board.getAllIndividualPieces(Holder.ROOK, enemyPlayer));
        long blokingPieces = board.blockers;
        long myPieces = board.getAllPieces(currPlayer);
        for (int dir : Holder.ORTHOGONAL) {
            findPinned(dir, myPieces, blokingPieces, searchingFor);
        }
        searchingFor = (board.getAllIndividualPieces(Holder.QUEEN, enemyPlayer) | 
            board.getAllIndividualPieces(Holder.BISHOP, enemyPlayer));
        for (int dir : Holder.DIAGONAL) {
            findPinned(dir, myPieces, blokingPieces, searchingFor);
        }
    }
    private void findPinned(int dir, long myPieces, long blokingPieces, long searchingFor) {
        long currPinnedMoves = (findDirection(dir, kingSquare, blokingPieces) & myPieces);
        if (currPinnedMoves != 0){ // found my piece, which could potentionally be pinned
            int pinnedPieceSquare = Long.numberOfTrailingZeros(currPinnedMoves); // find its position
            long findEnemySliding = (findDirection(dir, pinnedPieceSquare, blokingPieces) & searchingFor); // check for enemy sliding
            if (findEnemySliding != 0){ // found him !
                if (currPlayerInCheck == 1){ // if curr player is in check, pinned cant move
                    pins.put(pinnedPieceSquare, Holder.ZERO);
                    return;
                }
                long resultingMoves = ray.getRay(dir, kingSquare);
                pins.put(pinnedPieceSquare, resultingMoves);
            }
        }
        return;
    }
    
    private long findDirection(int dir, int square, long blockers) {
        switch (dir) {
            case Holder.SOUTH:
                return ray.South(square, blockers);
            case Holder.NORTH:
                return ray.North(square, blockers);
            case Holder.EAST:
                return ray.East(square, blockers);
            case Holder.WEST:
                return ray.West(square, blockers);
            case Holder.SOUTH_WEST:
                return ray.southWest(square, blockers);
            case Holder.SOUTH_EAST:
                return ray.southEast(square, blockers);
            case Holder.NORTH_WEST:
                return ray.northWest(square, blockers);
            case Holder.NORTH_EAST:
                return ray.northEast(square, blockers);       
            default:
                return Holder.ZERO;
        }
    }

    private void resetVals() {
        this.pins.clear();
        this.attacks[enemyPlayer] = Holder.ZERO;
        this.pathToBlock = Holder.ZERO;
        this.currPlayerInCheck = 0;
        this.enpassantSquare = 0;
        currAttacks = 0;
    }

    private void generateKing(HashMap<Integer, Spot> curr) {
        if (curr.containsKey(kingSquare)) { //<- only here becasue perft can capture king
            long result = King(board.blockers, board.getAllPieces(currPlayer), kingSquare);
            curr.get(kingSquare).setMoves(result);
            if (castling){
                if (playersCastling[currPlayer]){
                    createCastling();
                    determineCastling(curr, kingSquare);
                }
            }
        }
    }

    private void checkForCheck() {
        long UnderAttack = board.getAllIndividualPieces(Holder.KING, currPlayer) & attacks[enemyPlayer];
        if (UnderAttack == 0){ // king square cant be reached by enemy
            return;
        }
        findPath();// king is under attack/s
    }

    private void determineCastling(HashMap<Integer, Spot> curr, int kingPos){
        if (!(correctRookPos())){// rooks has to be on RANK 1 / 8
            this.playersCastling[currPlayer] = false;
            return;
        }
        if (currPlayerInCheck == 0){// player isnt in check
            // check only if king is on rank 1 or 7, player could have manually placed him on 5th rank etc.
            if (!(curr.get(kingPos).getHasMoved()) && correctKingPos(currPlayer)){// king didnt move and is in correct pos
                //will stop at the first piece it meets and includes it
                long pathToRook = ray.West(kingPos, board.blockers); 
                findCastle(pathToRook, curr, kingPos, Holder.WEST);//"queen castling"->left
                pathToRook = ray.East(kingPos,  board.blockers);
                findCastle(pathToRook, curr, kingPos, Holder.EAST);//right castling
            } else {
                this.playersCastling[currPlayer] = false;
            }
        }
    }

    private boolean correctRookPos(){
        if (currPlayer == Holder.WHITE){
            return((board.getAllIndividualPieces(Holder.ROOK, currPlayer) & Holder.RANK_8 )!= 0);
        } else {
            return((board.getAllIndividualPieces(Holder.ROOK, currPlayer) & Holder.RANK_1 )!= 0);
        }
    }
    
    private void findCastle(long pathToRook, HashMap<Integer, Spot> curr, int kingPos, int dir){
        long rooksPos = board.getAllIndividualPieces(Holder.ROOK, currPlayer);
        long result = pathToRook & rooksPos;
        int rookSquare = Long.numberOfTrailingZeros(result);
        if (result != 0 && !(pins.containsKey(rookSquare))) {// there is rook and isnt pinned
            int squaresToRook = Long.bitCount(pathToRook);//check num of squares to rook
            long withOutRook = pathToRook ^ result; // remove rook from calculation now, he can be under attack
            long underAttack = withOutRook ^ (withOutRook & attacks[enemyPlayer]);// path isnt under attack
            if (withOutRook == underAttack && (squaresToRook == 4 || squaresToRook == 3)) {//dir has to have 4/3 squares to rook->including rook
                if(!(curr.get(rookSquare).getHasMoved())) {// rook didnt move -> can castle
                    long currKingMoves = curr.get(kingPos).getMoves();
                    int shift = (dir == Holder.WEST) ? kingPos-2:kingPos+2;
                    curr.get(kingPos).setMoves(currKingMoves | (Holder.ONE << (shift))); // add castling move
                    if (dir == Holder.WEST){ //rook pos, to which king can castle
                        rookCastleSquares[0] = rookSquare;
                    } else {
                        rookCastleSquares[1] = rookSquare;
                    }
                }
            }
        }
    }

    private boolean correctKingPos(int currPlayer) {
        long kingPos = board.getAllIndividualPieces(Holder.KING, currPlayer);
        if ((kingPos & Holder.RANK_1)!=0){
            return true;
        } else if ((kingPos & Holder.RANK_8)!=0){
            return true;
        }
        return false;
    }

    private void findPath() {
        //find pawns around king using King_Moves
        long tempBlockers = board.blockers;
        int tempBitCount = 0;
        //Pretend that king is same colored pawn and looks if it can attack other pawns
        long givingCheck = ray.getPawn(kingSquare, currPlayer) & board.getAllIndividualPieces(Holder.PAWN, enemyPlayer);
        //Here i can check if king is under check from enpassant pawn
        currPlayerInCheck = Long.bitCount(givingCheck);
        // enpassant check
        if (enpassant && currPlayerInCheck != 0) { // check for pawn + enpassant
            long pos1 = givingCheck << Holder.ROWS; // givingCheck is location of enemy pawnSquare
            if ((pos1 & board.getEnPassantMoves(enemyPlayer))!=0) {
                enpassantSquare = Long.numberOfTrailingZeros(givingCheck);
            } else if (((pos1 >>> (2*Holder.ROWS)) & board.getEnPassantMoves(enemyPlayer))!=0) {
                enpassantSquare = Long.numberOfTrailingZeros(givingCheck);
            }
        }

        pathToBlock |= givingCheck;
        if (currPlayerInCheck >= 2) {
            return;
        }
        //Pretend that king is knight to find knights
        givingCheck = ray.getKnight(kingSquare) & board.getAllIndividualPieces(Holder.KNIGHT, enemyPlayer);
        currPlayerInCheck += Long.bitCount(givingCheck);
        pathToBlock |= givingCheck;
        if (currPlayerInCheck >= 2) {
            return;
        }
        //Pretend that king is bishop
        givingCheck = Bishop(tempBlockers, board.getAllPieces(currPlayer), kingSquare);
        // look for enemy bishops or queens, can be treated as a same, if their moves land on king
        long enemyGivinCheck = givingCheck & (board.getAllIndividualPieces(Holder.BISHOP, enemyPlayer) | 
            board.getAllIndividualPieces(Holder.QUEEN, enemyPlayer));
        tempBitCount = Long.bitCount(enemyGivinCheck);
        if (tempBitCount != 0){// found some ?
            currPlayerInCheck += tempBitCount;
            if (currPlayerInCheck >= 2) {
                return;
            } // if i didnt found more than 2 by this point, update blockers
            int enemyPos = Long.numberOfTrailingZeros(enemyGivinCheck); // find enemy pos
            //overlap the rays over themselfs, to get path from piece to king, + add position of piece for capture
            long rayOverlapp = givingCheck & Bishop(tempBlockers, 
                board.getAllPieces(enemyPlayer), enemyPos) | (Holder.ONE << enemyPos); 
            pathToBlock |= rayOverlapp; 
        }
        // pretend that i am rook instead of king
        givingCheck = Rook(tempBlockers, board.getAllPieces(currPlayer), kingSquare);
        // look for enemy rooks or queens, can be treated as a same, if their moves land on king
        enemyGivinCheck = givingCheck & (board.getAllIndividualPieces(Holder.ROOK, enemyPlayer) |
            board.getAllIndividualPieces(Holder.QUEEN, enemyPlayer));
        tempBitCount = Long.bitCount(enemyGivinCheck);
        if (tempBitCount != 0){
            currPlayerInCheck += tempBitCount;
            if (currPlayerInCheck >= 2){
                return;
            }
            int enemyPos = Long.numberOfTrailingZeros(enemyGivinCheck);
            long rayOverlapp = givingCheck & Rook(tempBlockers, 
                board.getAllPieces(enemyPlayer), enemyPos) | (Holder.ONE << enemyPos); 
            pathToBlock |= rayOverlapp; 
        }
    }
    
    private long generateMoves(int piece, int square) {
        switch (piece) {
            case Holder.KING:
                return 0; 
            case Holder.QUEEN:
                return Queen(board.blockers, board.getAllPieces(currPlayer), square);
            case Holder.ROOK:
                return Rook(board.blockers, board.getAllPieces(currPlayer), square);
            case Holder.KNIGHT:
                return Knight(board.getAllPieces(currPlayer), square);
            case Holder.BISHOP:
                return Bishop(board.blockers, board.getAllPieces(currPlayer), square);
            case Holder.PAWN:
                return Pawn(board.blockers, ~board.blockers, square, currPlayer);
            default:
                return Holder.ZERO;
        }
    }
    //need to make currPlayer king invisible when calucalting attacks for enemy sliding pieces
    public void generateAttacks(int piece, int square) {
        switch (piece) {
            case Holder.KING:
                attacks[enemyPlayer] |= ray.getKing(square);
                return;
            case Holder.QUEEN: 
                attacks[enemyPlayer] |= (trueMagic.getRookMoves(square, board.getBlockersWithouKing(currPlayer)) | 
                    trueMagic.getBishopMoves(square, board.getBlockersWithouKing(currPlayer)));
                return;
            case Holder.ROOK:
                attacks[enemyPlayer] |= trueMagic.getRookMoves(square, board.getBlockersWithouKing(currPlayer));
                return;
            case Holder.KNIGHT: 
                attacks[enemyPlayer] |= ray.getKnight(square);
                return;
            case Holder.BISHOP:
                attacks[enemyPlayer] |= trueMagic.getBishopMoves(square, board.getBlockersWithouKing(currPlayer));
                return;
            case Holder.PAWN:
                attacks[enemyPlayer] |= ray.getPawn(square, enemyPlayer);
                return;
            default:
                return;
        }
    }

    private long King(long blockers, long myPieces, int square){
        long moves = ray.getKing(square);
        long movesWithoutMine = (moves  ^ (moves & myPieces)); // first remove my pieces
        return movesWithoutMine ^ (movesWithoutMine & attacks[enemyPlayer]); // than check if squares are under attack
    }

    private long Bishop(long blockers, long myPieces, int square) {
        currAttacks = trueMagic.getBishopMoves(square, blockers);
        return currAttacks ^ (currAttacks & myPieces); // remove my pieces
    }
    
    private long Queen(long blockers, long myPieces,  int square) {
        return Rook(blockers, myPieces, square) | Bishop(blockers, myPieces, square);
    }

    private long Rook(long blockers, long myPieces, int square) {
        currAttacks = trueMagic.getRookMoves(square, blockers);
        return currAttacks ^ (currAttacks & myPieces); // remove my pieces
    }
    
    private long Knight(long myPieces, int square) {
        currAttacks = ray.getKnight(square);
        return currAttacks ^ (currAttacks & myPieces); // remove my pieces
    }
    // check if Enpassant capture wont put king into check (special situation)
    private boolean checkForFileSafety(int square) {
        int enemyPawnSquare;
        if (enemyPlayer == Holder.WHITE) { // white enpassant
            enemyPawnSquare = Long.numberOfTrailingZeros(board.getEnPassantMoves(enemyPlayer))-Holder.ROWS;
        } else { // black enpassant
            enemyPawnSquare = Long.numberOfTrailingZeros(board.getEnPassantMoves(enemyPlayer))+Holder.ROWS;
        }
        long kingPos = board.getAllIndividualPieces(Holder.KING, currPlayer);
        long searchingFor = (board.getAllIndividualPieces(Holder.ROOK, enemyPlayer) | 
            board.getAllIndividualPieces(Holder.QUEEN, enemyPlayer));
        long tempBlockers = (board.blockers ^ ((Holder.ONE << enemyPawnSquare) |
            (Holder.ONE << square))); // blockers without these 2 pawns
        long moves = ray.West(enemyPawnSquare, tempBlockers);
        moves |= ray.East(enemyPawnSquare, tempBlockers);
        if ((moves & kingPos) != 0) { // found king on file
            if ((moves & searchingFor) != 0) { // found queen or rook on file
                return false;
            }
        }
        return true;
    }

    private long Pawn(long blockers, long notOccupied, int square, int color) {
        long moved = Holder.ONE << square;
        currAttacks = ray.getPawn(square, color);
        long modMoves = currAttacks & board.getAllPieces(enemyPlayer); // attacks of pawn
        if (enpassant && ((currAttacks & board.getEnPassantMoves(enemyPlayer))!= 0) && currPlayerInCheck == 0) { // enpassant is possible
            if (checkForFileSafety(square)){ // enpassant wont put king into check
                modMoves |= board.getEnPassantMoves(enemyPlayer); // add enemy enpassant
            }
        }
        if (color == Holder.WHITE){ // check forward moves
            long shift = ((moved >>> Holder.ROWS) & notOccupied);
            modMoves |= shift;
            if (((moved & Holder.RANK_7)!=0) && shift != 0){ // 2 step can only be done on rank 2 or 7
                modMoves |= ((moved >>> (2*Holder.ROWS)) & notOccupied);
            }
        } else {
            long shift = ((moved << Holder.ROWS) & notOccupied);
            modMoves |= shift;
            if (((moved & Holder.RANK_2)!=0) && shift != 0) {
                modMoves |= ((moved << (2*Holder.ROWS)) & notOccupied);
            }
        }
        return modMoves;
    }
    //GETTERS && SETTERS
    public void setEnemyPlayer(int enemyPlayer) {
        attacks[enemyPlayer] = 0;
        this.enemyPlayer = enemyPlayer;
    }
    
    public int getCurrPlayerInCheck() {
        return currPlayerInCheck;
    }

    public int getRookCastleSquares(int dir) {
        return rookCastleSquares[dir];
    }

    public void setCastling(boolean castling) {
        this.castling = castling;
        if (castling){
            this.playersCastling = new boolean[2];
            this.rookCastleSquares = new int[2];
            playersCastling[Holder.WHITE] = true;
            playersCastling[Holder.BLACK] = true;
        }
    }

    public void setEnpassant(boolean enpassant) {
        this.enpassant = enpassant;
    }

    public boolean canCastle(int player){
        return playersCastling[player];
    }

    public long getAttacks(int player) {
        return attacks[player];
    }

    public TrueMagic getTrueMagic() {
        return trueMagic;
    }
}