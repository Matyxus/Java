package chess2.board;
import java.util.HashMap;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
//https://www.chessprogramming.org/Zobrist_Hashing
//https://en.wikipedia.org/wiki/Zobrist_hashing
//castling_rights, en_passant_files, sideToMove are random numbers
public class Zobrist {
    private final long[] CASTLING_RIGHTS = {8901601852338749779L, 7073377999468250156L, 6556969973348860604L, 8224048741023841477L};
    private final int SEED = 4;
    private final Random generator = new Random(SEED);
    public static final int PIECE_OFFSET = 6;
    private final GameBoard board;
    public static final long blackToMove = 2703255076737973876L;
    public static final long[] EN_PASSANT_FILES = {
        7338921424909686254L,
        7427175436213957123L,
        2898066206797371386L,
        1468308461554057123L,
        7503219113706599870L,
        8096443611919276782L,
        6554333725051357809L,
        9208530740313182802L,
    };
    public static long[][] piecesHash;
    public static long zobrist = 0;
    private long key = 0;
    private int offset = 0;
    private static HashMap<Long, SimpleEntry<Integer, Long>> cache;
    //pointers to save
    private HashMap<Long, Integer> pointer = null;
    private int counter = 0;
    public static long prevEnpassant = 0;
    public static long currEnpassant = 0;

    public Zobrist(GameBoard gameboard){
        board = gameboard;
        cache = new HashMap<Long, SimpleEntry<Integer, Long>>();
        fillPiecesHash();
    }

    //[Hash for White Rook on a1] xor [Hash for White Knight on b1] xor [Hash for White Bishop on c1] xor ... ( all pieces )
    //... xor [Hash for White king castling] xor [Hash for White queeb castling] xor ... ( all castling rights ) xor [Hash for existing enpassant[file]]
    // hashInit is for Perft/AI, doesnt use Castling
    public void hashInit() {   
        board.getWhitePieces().forEach((square, spot)-> zobrist ^= piecesHash[spot.getPiece()][square]);
        if (board.isEnpassant()&& board.getEnPassantMoves(Holder.WHITE) != 0) {
            zobrist ^= (EN_PASSANT_FILES[board.getEnpassantSquare()%8]);
        }
        board.getBlackPieces().forEach((square, spot)-> zobrist ^= piecesHash[spot.getPiece()+PIECE_OFFSET][square]);
        if (board.isEnpassant() && board.getEnPassantMoves(Holder.BLACK) != 0) {
            zobrist ^= (EN_PASSANT_FILES[board.getEnpassantSquare()%8]);
        }
    }

    private void fillPiecesHash() {
        piecesHash = new long[12][Holder.BOARD_SIZE];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < Holder.BOARD_SIZE; j++) {
                piecesHash[i][j] = generator.nextLong();
            }
        }
    }

    public static void destroyMap(){
        cache = null;
    }

    //[Original Hash of position] xor [Hash for White Knight on b1] ... ( removing the knight from b1 )
    //... xor [Hash for Black Bishop on c3] ( removing the captured bishop from c3 )
    //... xor [Hash for White Knight on c3] ( placing the knight on the new square )
    //... xor [Hash for Black to move] ( change sides)
    public static void save(long hash, int depth, long nodes) { // nodes can be used as score for AI
        cache.put(hash, new SimpleEntry<>(depth, nodes));
    }

    public static long isValid(long hash, int depth) {
        if (cache.containsKey(hash) && cache.get(hash).getKey() == depth) {
            return cache.get(hash).getValue();
        }
        return 0;// doesnt contain or depth isnt same
    }

    private void castlingMoves(int player) {
        int kingSquare = board.getKingsPos(player);
        long normalKingMoves = board.getRays().getKing(kingSquare);
        long currKingMoves = board.getPlayerPieces(player).get(kingSquare).getMoves();
        long castleMoves = currKingMoves ^ (currKingMoves & normalKingMoves);
        int totalCastles = Long.bitCount(castleMoves);
        if (board.isCastling() && totalCastles != 0){ 
            offset = 2*player; // offset for castling hashes
            if (totalCastles == 2) {// can castle to both sides
                key ^= CASTLING_RIGHTS[0+offset];
                key ^= CASTLING_RIGHTS[1+offset];
            } else if (totalCastles == 1) {// can castle to only 1 side
                int side = kingSquare - Long.numberOfTrailingZeros(castleMoves);
                int index = (side > 0) ? 0+offset:1+offset;
                key ^= CASTLING_RIGHTS[index];
            }
        }
    }
    // hash for legal game, includes Castling, Enpassant
    public long createHash() {
        board.getWhitePieces().forEach((square, spot)-> key ^= piecesHash[spot.getPiece()][square]);
        if (board.isEnpassant() && board.getEnPassantMoves(Holder.WHITE) != 0){
            key ^= (EN_PASSANT_FILES[board.getEnpassantSquare()%8]);
        }
        castlingMoves(Holder.WHITE);
        board.getBlackPieces().forEach((square, spot)-> key ^= piecesHash[spot.getPiece()+PIECE_OFFSET][square]);
        if (board.isEnpassant() && board.getEnPassantMoves(Holder.BLACK) != 0){
            key ^= (EN_PASSANT_FILES[board.getEnpassantSquare()%8]);
        }
        castlingMoves(Holder.BLACK);
        key ^= blackToMove;
        long toReturn = key;
        key = 0; // reset key
        return toReturn;
    }

    public void setPointer(HashMap<Long, Integer> temp, int counter) {
        if (temp == null || temp.size() == 0){
            pointer = null;
        } else {
            pointer = new HashMap<Long, Integer>();
            temp.forEach((key, value)->pointer.put(key, value));
        }
        this.counter = counter;
    }

    public static void clearCache() {
        cache.clear();
    }

    public void clear(){
        if (pointer != null) {
            pointer = null;
            counter = 0;
        }
    }

    public int getCounter() {
        return counter;
    }

    public HashMap<Long, Integer> getPointer() {
        return pointer;
    }
}
