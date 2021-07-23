package board;
import java.util.ArrayList;
import java.util.HashMap;

import board.constants.Colors;
import board.constants.Directions;
import board.constants.Files;
import board.constants.Flags;
import board.constants.Pieces;
import board.constants.Ranks;
import board.constants.Size;

public final class MoveGen {

    private final TrueMagic trueMagic;
    private final Rays ray;
    private final Castle castle;
    // HashMap of pinned pieces
    private final HashMap<Integer, Long> pins;
    
    private final Player[] players;
    private long allPieces = 0;
    // Bit-board pieces must play on when king is in check
    private long pathToBlock = Size.ZERO;
    // Bit-board of pinned pieces
    private long pinned = Size.ZERO;
    // Players colors
    private int enemyPlayer;
    private int currPlayer;
    // Current player king's square
    private int kingSquare;
    // Current player move list
    private ArrayList<Move> curr_move_list = null;

	public MoveGen(Rays rays, Player[] players, Castle castle) {
        this.players = players;
		this.ray = rays;
        this.pins = new HashMap<Integer, Long>();
        this.trueMagic = new TrueMagic(rays);
        this.castle = castle;
    }

    /**
     * @param currPlayer color of player that is currently playing
     * Generates legal moves for current player
     */
    public void updatePiecesMoves(Position currentPos, ArrayList<Move> move_list) {
        curr_move_list = move_list;
        resetVals();
        currPlayer = currentPos.getSideToMove();
        enemyPlayer = (currPlayer+1) & 1;
        allPieces = (players[Colors.WHITE].getAllPieces() | players[Colors.BLACK].getAllPieces());
        kingSquare = players[currPlayer].getKingSquare();
 
        // First generate enemy attacks bit board
        final long enemyAttacks = generateEnemyAttacks();
        // Number of times current player's is in check
        final int checkCount = checkForCheck(enemyAttacks); 
        if (checkCount == 2) {
            generateKing(checkCount, null, enemyAttacks);
            return; // Only king can play
        }
        // Determine pinned pieces and their moves
        checkForPinned();
        // Bit board of squares, that are empty
        long moves = ~allPieces;
        // Bit board of pieces than can be captured
        long captures = players[enemyPlayer].getAllPieces();
        if (checkCount == 1) {
            // During check, we can only capture the piece giving check
            captures &= pathToBlock;
            // Moves during check can either block piece (Queen/Rook/Bishop)
            if ((captures & players[enemyPlayer].getSliders()) != 0) {
                moves = (pathToBlock ^ captures);
            } else { // Cant block pawn/knight
                moves = Size.ZERO;
            }
        } else {
            pathToBlock = Long.MAX_VALUE;
            // Pinned pawns can only move, if there is no check
            generatePinnedPawns();
        }
        generateKing(checkCount, currentPos, enemyAttacks);
        generateKnights(moves, captures);
        generatePawns(moves, captures);
        generateEnpassant(currentPos.getEnpassant(), pathToBlock);
        generateSliders(moves, captures);
    }

    // ----------------------------- Pinned Pieces ----------------------------- 
    
    /**
     * Checks in orthogonal, diagonal directions for pinned pieces
     */
    private void checkForPinned() {
        long searchingFor = (
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN) | 
            players[enemyPlayer].getIndividualPieces(Pieces.ROOK)
        );
        final long myPieces = players[currPlayer].getAllPieces();
        // In orthogonal search for Queens an Rooks
        for (int dir : Directions.ORTHOGONAL) {
            findPinned(dir, myPieces, searchingFor);
        }
        searchingFor = (
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN)  | 
            players[enemyPlayer].getIndividualPieces(Pieces.BISHOP) 
        );
        // In diagonal search for Queens and Bishops
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
        // Found my piece, which could potentionally be pinned
        if (currPinnedMoves != 0) { 
            // Get its position
            final int pinnedPieceSquare = Long.numberOfTrailingZeros(currPinnedMoves); 
            // Check for enemy sliding pieces Queen/Rook/Bishop
            if ((ray.getDirection(dir, pinnedPieceSquare, allPieces) & searchingFor) != 0) {
                pinned |= (Size.ONE << pinnedPieceSquare);
                pins.put(pinnedPieceSquare, ray.getRay(dir, kingSquare));
            }
        }
    }

    /**
     * Clears pinned pieces, resets variables to zero
     */
    private void resetVals() {
        pins.clear();
        pathToBlock = Size.ZERO;
        pinned = Size.ZERO;
    }

    // ----------------------------- King Moves ----------------------------- 

    /**
     * Generates legal moves for king
     */
    private void generateKing(int checkCount, Position currentPos, long enemyAttacks) {
        long moves = King((players[currPlayer].getAllPieces() | enemyAttacks), kingSquare);
        generateAttacks(moves & players[enemyPlayer].getAllPieces(), kingSquare);
        generateMoves(moves & (~allPieces), kingSquare);
        // ------------------------ Castling ------------------------ 
        // Cant castle in check
        if (checkCount != 0) {
            return;
        }
        // ----------------- King side castling -----------------
        // Check if player can castle, and path is not under attack or blocked
        long mask = castle.getCastleSquareMask(currPlayer, Pieces.KING);
        if (currentPos.getCastlingRights()[currPlayer][Pieces.KING] &&
            (mask & (enemyAttacks | allPieces)) == 0) {
            // Castling is possible
            curr_move_list.add(new Move(kingSquare, kingSquare+2, Flags.KING_CASTLE));
        }
        mask = castle.getCastleSquareMask(currPlayer, Pieces.QUEEN);
        // ----------------- Queen side castling -----------------
        if ((currentPos.getCastlingRights()[currPlayer][Pieces.QUEEN] &&
            (((mask & (enemyAttacks | allPieces)) | ((mask >>> 1) & allPieces))) == 0)) {
            // Castling is possible
            curr_move_list.add(new Move(kingSquare, kingSquare-2, Flags.QUEEN_CASTLE));
        }
    }

    // ----------------------------- Check ----------------------------- 

    /**
     * @return number of times current player is in check
     */
    private int checkForCheck(long enemyAttacks) {
        // King square cant be reached by enemy
        if ((players[currPlayer].getIndividualPieces(Pieces.KING) & enemyAttacks) == 0) { 
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

    // ----------------------------- Knights ----------------------------- 

    /**
     * @param moves on empty squares that knight can make
     * @param captures that knights can make
     */
    private void generateKnights(long moves, long captures) {
        // Pinned knight/s cannot move
        long position = (players[currPlayer].getIndividualPieces(Pieces.KNIGHT) & (~pinned));
        while (position != 0) {
            int from = Long.numberOfTrailingZeros(position);
            position ^= (Size.ONE <<  from);
            final long knightMoves = ray.getKnight(from);
            // Normal moves
            generateMoves((knightMoves & moves), from);
            // Captures
            generateAttacks((knightMoves & captures), from);
        }
    }

    // ----------------------------- Pawns ----------------------------- 

    /**
     * @param moves on empty squares that pawns can make
     * @param captures that pawns can make
     */
    private void generatePawns(long moves, long captures) {
        // Pinned pawns cannot move
        final long pawns = (players[currPlayer].getIndividualPieces(Pieces.PAWN) & (~pinned));
        if (pawns == 0) {
            return;
        }
        // ---------------------------------- Moves ---------------------------------- 
        // Previous position of piece, if it moved by one rank (in terms of squares)
        int originalPos = (currPlayer == Colors.WHITE) ? 8 : -8;
        // Push pawns by one rank
        final long singleMoves = singlePushTargets(pawns, currPlayer); 
        // Legal moves of pawn by one rank
        pawnMoves(originalPos, (singleMoves & (~allPieces) & moves));
        // Double move, can only occur on when double moves do exist on 
        // rank 5 / 4 depending on players color,
        final long file = (currPlayer == Colors.WHITE) ? Ranks.RANK_5 : Ranks.RANK_4;
        long doubleMoves = (
            singlePushTargets(singleMoves & (~allPieces), currPlayer) & (~allPieces) & file & moves
        ); 
        // Enpassant moves
        originalPos <<= 1; // Jumping two ranks now
        while (doubleMoves != 0) {
            int to = Long.numberOfTrailingZeros(doubleMoves);
            doubleMoves ^= (Size.ONE << to);
            curr_move_list.add(new Move(to+originalPos, to, Flags.EN_PASSANT));
        }
        // ---------------------------------- Captures ---------------------------------- 
        // Previous position of piece, if it moved by one rank and 1 to right (in terms of squares)
        originalPos = (currPlayer == Colors.WHITE) ? 7 : -9;
        // Right captures
        pawnCaptures(originalPos, ((singleMoves & (~Files.FILE_H)) << 1) & captures);
        // Previous position of piece, if it moved by one rank and 1 to left (in terms of squares)
        originalPos = (currPlayer == Colors.WHITE) ? 9 : -7;
        // Left captures
        pawnCaptures(originalPos, ((singleMoves & (~Files.FILE_A)) >>> 1) & captures);
    }

    /**
     * @param shift original square of pan (value to substract from toSquare)
     * @param captures of pawn
     */
    private void pawnCaptures(final int shift, long captures) {
        pawnPromotions(shift, captures & Ranks.PROMOTION_RANKS[currPlayer], Flags.CAPTURE);
        // Remove promotion moves from captures
        captures &= ~Ranks.PROMOTION_RANKS[currPlayer];
        while (captures != 0) {
            int to = Long.numberOfTrailingZeros(captures);
            captures ^= (Size.ONE << to);
            curr_move_list.add(new Move(to+shift, to, Flags.CAPTURE));
        }
    }

    /**
     * @param shift original square of pan (value to substract from toSquare)
     * @param moves of pawn
     */
    private void pawnMoves(final int shift, long moves) {
        pawnPromotions(shift, moves & Ranks.PROMOTION_RANKS[currPlayer], Flags.MOVE);
        // Remove promotion moves from moves
        moves &= ~Ranks.PROMOTION_RANKS[currPlayer];
        while (moves != 0) {
            int to = Long.numberOfTrailingZeros(moves);
            moves ^= (Size.ONE << to);
            curr_move_list.add(new Move(to+shift, to, Flags.MOVE));
        }
    }

    /**
     * @param shift original square of pan (value to substract from toSquare)
     * @param moves of pawn
     * @param flag either Capture or Basic move
     */
    private void pawnPromotions(final int shift, long moves, final int flag) {
        while (moves != 0) {
            int to = Long.numberOfTrailingZeros(moves);
            moves ^= (Size.ONE << to);
            for (int piece : Pieces.PROMOTION_PIECES) {
                curr_move_list.add(new Move(to+shift, to, Flags.PROMOTION | flag | piece));
            }
        }
    }

    /**
     * @param pawns bitboard of pawns
     * @param color of current player
     * @return bitboard of pawns, that moved by one square forward/backwards
     * depending on color
     */
    private long singlePushTargets(long pawns, int color) {
        return ((pawns >>> 8) << (color << 4));
    }

    /**
     * Generates moves for pinned pawns
     */
    private void generatePinnedPawns() {
        // Only pinned pawns
        long pawns = (players[currPlayer].getIndividualPieces(Pieces.PAWN) & pinned);
        // Remove pawns on the same rank as King, they cannot move, if they are pinned
        pawns ^= (pawns & Ranks.ALL_RANKS[(kingSquare >> 3)]);
        if (pawns == 0) {
            return;
        }
        // Moving forward for pinned pawns, is only possible on the same file as king
        final long forwardPawns = (pawns & Files.ALL_FILES[(kingSquare & 7)]);
        // -------------------------- Moves -------------------------- 
        if (forwardPawns != 0) {
            int shift = (currPlayer == Colors.WHITE) ? 8 : -8;
            pawns ^= forwardPawns;
            // Push pawns by one rank
            final long singleMoves = singlePushTargets(forwardPawns, currPlayer); 
            // Legal moves of pawn by one rank
            long moves = (singleMoves & (~allPieces));
            while (moves != 0) {
                int to = Long.numberOfTrailingZeros(moves);
                moves ^= (Size.ONE << to);
                curr_move_list.add(new Move(to+shift, to, Flags.MOVE));
            }
            // Double move, can only occur on when double moves do exist on 
            // rank 5 / 4 depending on players color,
            final long file = (currPlayer == Colors.WHITE) ? Ranks.RANK_5 : Ranks.RANK_4;
            long doubleMoves = (
                singlePushTargets(singleMoves & (~allPieces), currPlayer) & (~allPieces) & file
            ); 
            // Enpassant moves
            shift <<= 1; // Jumping two ranks now
            while (doubleMoves != 0) {
                int to = Long.numberOfTrailingZeros(doubleMoves);
                doubleMoves ^= (Size.ONE << to);
                curr_move_list.add(new Move(to+shift, to, Flags.EN_PASSANT));
            }
        }
        // Moving on diagonals for pinned pawns, is only possible if they can capture pinning
        // Queen / Bishop, can still be promotion move
        while (pawns != 0) {
            int from = Long.numberOfTrailingZeros(pawns);
            pawns ^= (Size.ONE << from);
            long captures = (pins.get(from) & ray.getPawn(from, currPlayer) & players[enemyPlayer].getAllPieces());
            if (captures != 0) {
                int to = Long.numberOfTrailingZeros(captures);
                // Capture
                if ((captures & (~Ranks.PROMOTION_RANKS[currPlayer])) != 0) {
                    curr_move_list.add(new Move(from, to, Flags.CAPTURE));
                } else { // Promotion Capture
                    for (int piece : Pieces.PROMOTION_PIECES) {
                        curr_move_list.add(new Move(from, to, Flags.PROMOTION | Flags.CAPTURE | piece));
                    }
                }
            }
        }
    }

    /**
     * @param enpassant bitboard containing enpassant square
     * @param captures bitboard containing pieces, that can be captured
     */
    private void generateEnpassant(int enpassant, long captures) {
        final int originalPos = ((enemyPlayer == Colors.WHITE) ? -8:+8) + enpassant;
        // There is enpassant and can be captured
        if (enpassant != 0 && ((Size.ONE << originalPos) & captures) != 0) {
            // Only non pinned pawns, pawns that can attack enpasant square,
            // and only if enpassant square can be captured
            long pawns = (
                players[currPlayer].getIndividualPieces(Pieces.PAWN) & (~pinned) &
                ray.getPawn(enpassant, enemyPlayer)
            );
            // There can be up to two pawns making such a capture
            while (pawns != 0) {
                int from = Long.numberOfTrailingZeros(pawns);
                pawns ^= (Size.ONE << from);
                // Discovered check
                if (fileSafety(from, originalPos)) {
                    curr_move_list.add(
                        new Move(from, enpassant, Flags.CAPTURE | Flags.EN_PASSANT)
                    );
                }
            }
        }
    }

    /**
     * @param from square 
     * @param to square
     * @return if making enpassant capture put king into danger
     */
    private boolean fileSafety(int from, int to) {
        // Remove my pawn, and pawn to be captured from board
        final long pieces = (allPieces ^ ((Size.ONE << from) | (Size.ONE << to)));
        final long rooksAndQueens = (
            players[enemyPlayer].getIndividualPieces(Pieces.QUEEN) |
            players[enemyPlayer].getIndividualPieces(Pieces.ROOK)
        );
        // Ray stopped by first piece it encountered
        final long fileRay = ray.East(from, pieces) | ray.West(from, pieces);
        // Check if ray encountered king, and Rook/Queen
        return ((fileRay & (Size.ONE << kingSquare)) == 0 || (fileRay & rooksAndQueens) == 0);
    }

    // ----------------------------- Sliders ----------------------------- 

    /**
     * @param moves bitboard to be added to list
     * @param from square
     */
    private void generateAttacks(long moves, final int from) {
        while (moves != 0) {
            int to = Long.numberOfTrailingZeros(moves);
            moves ^= (Size.ONE << to);
            curr_move_list.add(new Move(from, to, Flags.CAPTURE));
        }
    }
    
    /**
     * @param moves bitboard to be added to list
     * @param from square
     */
    private void generateMoves(long moves, final int from) {
        while (moves != 0) {
            int to = Long.numberOfTrailingZeros(moves);
            moves ^= (Size.ONE << to);
            curr_move_list.add(new Move(from, to, Flags.MOVE));
        }
    }

    /**
     * @param moves where is possible to move
     * @param captures that can be made
     */
    private void generateSliders(long moves, long captures) {
        final long myPieces = players[currPlayer].getAllPieces();
        // Add Queen/s to bishop and rooks, since Queen
        // is both of them at the same time
        long rooks = (
            players[currPlayer].getIndividualPieces(Pieces.ROOK) |
            players[currPlayer].getIndividualPieces(Pieces.QUEEN)
        );
        long bishops = (
            players[currPlayer].getIndividualPieces(Pieces.BISHOP) |
            players[currPlayer].getIndividualPieces(Pieces.QUEEN)
        );
        // ------------------------- Rook/s ------------------------- 
        while (rooks != 0) {
            int from = Long.numberOfTrailingZeros(rooks);
            rooks ^= (Size.ONE << from);
            long pieceMoves = Rook(allPieces, myPieces, from);
            // Check for pinned
            if (pins.containsKey(from)) {
                pieceMoves &= pins.get(from);
            }
            generateMoves(pieceMoves & moves, from);
            generateAttacks(pieceMoves & captures, from);
        }
        // ------------------------- Bishop/s ------------------------- 
        while (bishops != 0) {
            int from = Long.numberOfTrailingZeros(bishops);
            bishops ^= (Size.ONE << from);
            long pieceMoves = Bishop(allPieces, myPieces, from);
            // Check for pinned
            if (pins.containsKey(from)) {
                pieceMoves &= pins.get(from);
            }
            generateMoves(pieceMoves & moves, from);
            generateAttacks(pieceMoves & captures, from);
        }
    }

    // ----------------------------- Enemy ----------------------------- 

    /**
     * Generates bitboard contaning possible attacks of all enemy pieces
     */
    private long generateEnemyAttacks() {
        final long blockersWithoutKing = (allPieces ^ (Size.ONE << kingSquare));
        final Player enemy = players[enemyPlayer];
        // King
        long enemyAttacks = ray.getKing(enemy.getKingSquare());    
        // --------------------------  Rook/s -------------------------- 
        // Add Queen to both Rooks and Bishops, 
        // same trick as in generateSliders()
        long position = (
            enemy.getIndividualPieces(Pieces.ROOK) |
            enemy.getIndividualPieces(Pieces.QUEEN)
        );
        while (position != 0) {
            int from = Long.numberOfTrailingZeros(position);
            position ^= (Size.ONE << from);
            enemyAttacks |= (
                trueMagic.getRookMoves(from, blockersWithoutKing) 
            );
        }
        // --------------------------  Bishop/s -------------------------- 
        position = (
            enemy.getIndividualPieces(Pieces.BISHOP) |
            enemy.getIndividualPieces(Pieces.QUEEN)
        );
        while (position != 0) {
            int from = Long.numberOfTrailingZeros(position);
            position ^= (Size.ONE << from);
            enemyAttacks |= (
                trueMagic.getBishopMoves(from, blockersWithoutKing)
            );
        }
        // --------------------------  Knight/s -------------------------- 
        position = enemy.getIndividualPieces(Pieces.KNIGHT);
        while (position != 0) {
            int from = Long.numberOfTrailingZeros(position);
            position ^= (Size.ONE << from);
            enemyAttacks |= ray.getKnight(from);
        }
        // -------------------------- Pawns -------------------------- 
        position = singlePushTargets(enemy.getIndividualPieces(Pieces.PAWN), enemyPlayer);
        enemyAttacks |= ((position & (~Files.FILE_A)) >>> 1);
        enemyAttacks |= ((position & (~Files.FILE_H)) << 1);
        return enemyAttacks;
    }

    // ----------------------------- Piece Functions ----------------------------- 

    /**
     * @param blockers same colored pieces, with enemy attack bitboard
     * @param square on which king is
     * @return legal moves of king
     */
    private long King(long blockers, int square){
        long moves = ray.getKing(square);
        // From moves remove my pieces, 
        // and squares that are under attack by enemy player
        return (moves  ^ (moves & blockers));
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    private long Pawn(long blockers, long notOccupied, int square, int color) {
        final long position = Size.ONE << square;
        // Attacks of pawn
        long modMoves = (ray.getPawn(square, color) & players[enemyPlayer].getAllPieces());
        // Check forward moves
        if (color == Colors.WHITE) {
            long shift = ((position >>> Size.ROWS) & notOccupied);
            modMoves |= shift;
            // Two step move can be only on rank 7 for white
            if (((position & Ranks.RANK_7) != 0) && shift != 0) { 
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