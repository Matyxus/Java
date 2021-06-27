package board;
import java.util.HashMap;

import board.constants.Colors;
import board.constants.Directions;
import board.constants.Files;
import board.constants.Pieces;
import board.constants.Ranks;
import board.constants.Size;

public final class MoveGen {
    private final Rays ray;
    // HashMap of pinned pieces
    private final HashMap<Integer, Long> pins;
    private final TrueMagic trueMagic;
    private final Player[] players;

    
    private long enemyAttacks = 0;
    private long allPieces = 0;
    // Bit-board pieces must play on when king is in check
    private long pathToBlock = Size.ZERO;
    // Players colors
    private int enemyPlayer;
    private int currPlayer;
    // Current player king's square
    private int kingSquare; 
	public MoveGen(Rays rays, Player[] players) {
        this.players = players;
		this.ray = rays;
        this.pins = new HashMap<Integer, Long>();
        this.trueMagic = new TrueMagic(rays);
    }

    /**
     * @param currPlayer color of player that is currently playing
     * Generates legal moves for current player
     */
    public void updatePiecesMoves(int currPlayer) {
        resetVals();//reset
        this.currPlayer = currPlayer;
        enemyPlayer = (currPlayer+1) & 1;
        allPieces = (players[Colors.WHITE].getAllPieces() | players[Colors.BLACK].getAllPieces());
        kingSquare = players[currPlayer].getKingSquare();
        
        
        // first generate enemy
        final long blockersWithouKing = (allPieces ^ (Size.ONE << kingSquare));
        players[enemyPlayer].getPlacedPieces().forEach((square, spot)->
            generateAttacks(spot.getPiece(), square, blockersWithouKing)
        );
        enemyAttacks |= PawnAttacks(players[enemyPlayer].getIndividualPieces(Pieces.PAWN), enemyPlayer);
        
        // Number of times current player's is in check
        final int checkCount = checkForCheck(); 
        if (checkCount == 2) {
            generateKing();
            return; // only king can play
        }
        final HashMap<Integer, Spot> curr = players[currPlayer].getPlacedPieces();
        curr.forEach((square, spot)->spot.setMoves(generateMoves(spot.getPiece(), square)));
        //determine pinned pieces and their moves
        if (curr.containsKey(kingSquare)){
            checkForPinned();
        }
        
        if (checkCount == 1){
            curr.forEach((square, spot)->{
                // if there is check, pins moves are already 0
                if (!(pins.containsKey(square))) { 
                    spot.modifyMoves(pathToBlock);
                }
            });
            // Pinned pieces during check cant move
            pins.forEach((square, move)->curr.get(square).setMoves(Size.ZERO));
        } else {
            //Update pinned pieces moves
            pins.forEach((square, move)->curr.get(square).modifyMoves(move));
        }
        generateKing();
    }

    /**
     * Checks in orthogonal, diagonal directions for pinned pieces
     */
    private void checkForPinned() {
        long searchingFor = (
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN) | 
            players[enemyPlayer].getIndividualPieces(Pieces.ROOK)
        );
        final long myPieces = players[currPlayer].getAllPieces();
        for (int dir : Directions.ORTHOGONAL) {
            findPinned(dir, myPieces, searchingFor);
        }
        searchingFor = (
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN)  | 
            players[enemyPlayer].getIndividualPieces(Pieces.BISHOP) 
        );
        for (int dir : Directions.DIAGONAL) {
            findPinned(dir, myPieces, searchingFor);
        }
    }
    /**
     * @param dir direction
     * @param myPieces pieces of current player
     * @param searchingFor pieces that can check king, if current pieces move
     */
    private void findPinned(int dir, long myPieces, long searchingFor) {
        final long currPinnedMoves = (ray.getDirection(dir, kingSquare, allPieces) & myPieces);
        // found my piece, which could potentionally be pinned
        if (currPinnedMoves != 0){ 
            final int pinnedPieceSquare = Long.numberOfTrailingZeros(currPinnedMoves); // find its position
            // check for enemy sliding pieces Queen/Rook/Bishop
            if ((ray.getDirection(dir, pinnedPieceSquare, allPieces) & searchingFor) != 0) {
                pins.put(pinnedPieceSquare, ray.getRay(dir, kingSquare));
            }
        }
    }
    
    /**
     * Clears pinned pieces, resets variables to zero
     */
    private void resetVals() {
        pins.clear();
        enemyAttacks = Size.ZERO;
        pathToBlock = Size.ZERO;
    }

    /**
     * Generates legal moves for king
     */
    private void generateKing() {
        long result = King(allPieces, players[currPlayer].getAllPieces(), kingSquare);
        players[currPlayer].getPlacedPieces().get(kingSquare).setMoves(result);
    }

    /**
     * @return number of times current player is in check
     */
    private int checkForCheck() {
        // King square cant be reached by enemy
        if ((players[currPlayer].getAllPieces() & enemyAttacks) == 0) { 
            return 0;
        }
        // King is under attack/s
        return findPath();
    }

    /**
     * @return number of times current player is in check
     */
    private int findPath() {
        // Prented that king is Pawn, Knight, (apart from king) ... 
        // to find same type of enemy piece reacheable
        // by the same piece -> puts king in check, creates "pathToBlock"
        // which leads from king to piece giving check
        
        // ------------------ Pawn Check ------------------
        long givingCheck = (
            ray.getPawn(kingSquare, currPlayer) & 
            players[enemyPlayer].getIndividualPieces(Pieces.PAWN)
        );
        int checkCount = Long.bitCount(givingCheck);
        pathToBlock |= givingCheck;
        if (checkCount >= 2) {
            return 2;
        }
        // ------------------ Knight Check ------------------
        givingCheck = (
            ray.getKnight(kingSquare) & 
            players[enemyPlayer].getIndividualPieces(Pieces.KNIGHT)
        );
        checkCount += Long.bitCount(givingCheck);
        pathToBlock |= givingCheck;
        if (checkCount >= 2) {
            return 2;
        }
        // ------------------ Bishop Check ------------------
        givingCheck = Bishop(allPieces, players[currPlayer].getAllPieces(), kingSquare);
        // Look for enemy bishops or queens, (can find either)
        long enemyGivinCheck = (givingCheck & (
            players[enemyPlayer].getIndividualPieces(Pieces.BISHOP) | 
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN))
        );
        if (enemyGivinCheck != 0) {// Found either bishop or queen
            checkCount += Long.bitCount(enemyGivinCheck);
            if (checkCount >= 2) {
                return 2;
            }
            int enemyPos = Long.numberOfTrailingZeros(enemyGivinCheck);
            // Find path from piece giving check and add it to "pathToBlock"
            long rayOverlapp = (
                (givingCheck & 
                Bishop(allPieces, players[enemyPlayer].getAllPieces(), enemyPos)) | 
                (Size.ONE << enemyPos)
            ); 
            pathToBlock |= rayOverlapp; 
        }
        // ------------------ Rook Check ------------------
        givingCheck = Rook(allPieces, players[currPlayer].getAllPieces(), kingSquare);
        // Look for enemy rooks or queens, (can find either)
        enemyGivinCheck = (givingCheck & (
            players[enemyPlayer].getIndividualPieces(Pieces.ROOK) |
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN))
        );
        if (enemyGivinCheck != 0) {// Found either rook or queen
            checkCount += Long.bitCount(enemyGivinCheck);
            if (checkCount >= 2) {
                return 2;
            }
            int enemyPos = Long.numberOfTrailingZeros(enemyGivinCheck);
            // Find path from piece giving check and add it to "pathToBlock"
            long rayOverlapp = (
                (givingCheck & Rook(allPieces, players[enemyPlayer].getAllPieces(), enemyPos)) | 
                (Size.ONE << enemyPos)
            ); 
            pathToBlock |= rayOverlapp; 
        }
        return checkCount;
    }
    
    /**
     * @param piece which moves are to be generated
     * @param square on which piece is
     * @return bit board legal moves of piece
     */
    private long generateMoves(int piece, int square) {
        switch (piece) {
            case Pieces.KING:
                return Size.ZERO;
            case Pieces.QUEEN:
                return Queen(allPieces, players[currPlayer].getAllPieces(), square);
            case Pieces.ROOK:
                return Rook(allPieces, players[currPlayer].getAllPieces(), square);
            case Pieces.KNIGHT:
                return Knight(players[currPlayer].getAllPieces(), square);
            case Pieces.BISHOP:
                return Bishop(allPieces, players[currPlayer].getAllPieces(), square);
            case Pieces.PAWN:
                return Pawn(allPieces, ~allPieces, square, currPlayer);
            default:
                return Size.ZERO;
        }
    }
    /**
     * @param piece which attacks are to be generated
     * @param square on which piece is
     * @param blockersWithouKing all pieces without current players king
     */
    private void generateAttacks(int piece, int square, long blockersWithouKing) {
        switch (piece) {
            case Pieces.KING:
                enemyAttacks |= ray.getKing(square);
                return;
            case Pieces.QUEEN: 
                enemyAttacks |= (
                    trueMagic.getRookMoves(square, blockersWithouKing) | 
                    trueMagic.getBishopMoves(square, blockersWithouKing)
                );
                return;
            case Pieces.ROOK:
                enemyAttacks |= trueMagic.getRookMoves(square, blockersWithouKing);
                return;
            case Pieces.KNIGHT: 
                enemyAttacks |= ray.getKnight(square);
                return;
            case Pieces.BISHOP:
                enemyAttacks |= trueMagic.getBishopMoves(square, blockersWithouKing);
                return;
            case Pieces.PAWN:
                return;
            default:
                return;
        }
    }

    /**
     * @param pawns on board
     * @param color of pawns
     * @return attacks of pawns
     */
    private long PawnAttacks(long pawns, int color) {
        long attacks = Size.ZERO;
        long moved = Size.ZERO;
        if (color == Colors.WHITE) {
            moved = (pawns >>> Size.ROWS);
        } else {
            moved = (pawns << Size.ROWS);
        }
        attacks |= ((moved & (~Files.FILE_A)) >>> 1);
        attacks |= ((moved & (~Files.FILE_H)) << 1);
        return attacks;
    }

    /**
     * @param blockers all pieces on board
     * @param myPieces same colored pieces as king
     * @param square on which king is
     * @return legal moves of king
     */
    private long King(long blockers, long myPieces, int square){
        long moves = ray.getKing(square);
        // From moves remove my pieces, 
        // and squares that are under attack by enemy player
        return (moves  ^ (moves & (myPieces | enemyAttacks)));
    }

    /**
     * @param blockers all pieces on board
     * @param myPieces same colored pieces as bishop
     * @param square on which bishop is
     * @return legal moves of bishop
     */
    private long Bishop(long blockers, long myPieces, int square) {
        long currAttacks = trueMagic.getBishopMoves(square, blockers);
        return currAttacks ^ (currAttacks & myPieces); // remove my pieces
    }
    
    /**
     * @param blockers all pieces on board
     * @param myPieces same colored pieces as queen
     * @param square on which queen is
     * @return legal moves of queen
     */
    private long Queen(long blockers, long myPieces,  int square) {
        return Rook(blockers, myPieces, square) | Bishop(blockers, myPieces, square);
    }

    /**
     * @param blockers all pieces on board
     * @param myPieces same colored pieces as rook
     * @param square on which rook is
     * @return legal moves of rook
     */
    private long Rook(long blockers, long myPieces, int square) {
        long currAttacks = trueMagic.getRookMoves(square, blockers);
        return currAttacks ^ (currAttacks & myPieces); // remove my pieces
    }
    
    /**
     * @param blockers all pieces on board
     * @param myPieces same colored pieces as knight
     * @param square on which knight is
     * @return legal moves of knight
     */
    private long Knight(long myPieces, int square) {
        long currAttacks = ray.getKnight(square);
        return currAttacks ^ (currAttacks & myPieces); // remove my pieces
    }
    
    /**
     * @param blockers all pieces on board
     * @param notOccupied all empty squares on board
     * @param square on which pawn is
     * @param color color of pawn
     * @return legal moves of pawn
     */
    private long Pawn(long blockers, long notOccupied, int square, int color) {
        final long position = Size.ONE << square;
        // Attacks of pawn
        long modMoves = (ray.getPawn(square, color) & players[enemyPlayer].getAllPieces());
        if (color == Colors.WHITE){ // check forward moves
            long shift = ((position >>> Size.ROWS) & notOccupied);
            modMoves |= shift;
            // Two step move can be only on rank 7 for white
            if (((position & Ranks.RANK_7) != 0) && shift != 0){ 
                modMoves |= ((position >>> (2*Size.ROWS)) & notOccupied);
            }
        } else {
            long shift = ((position << Size.ROWS) & notOccupied);
            modMoves |= shift;
            // Two step move can be only on rank 2 for black
            if (((position & Ranks.RANK_2) != 0) && shift != 0) {
                modMoves |= ((position << (2*Size.ROWS)) & notOccupied);
            }
        }
        return modMoves;
    }
}