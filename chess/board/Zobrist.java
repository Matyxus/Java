package board;
import java.util.Random;

import board.constants.Colors;
import board.constants.Pieces;
import board.constants.Size;
// https://www.chessprogramming.org/Zobrist_Hashing

public class Zobrist {
    
    /**
     * Hashes for castling rights
     */
    private final long[][] CASTLING_RIGHTS = {
        {8901601852338749779L, 7073377999468250156L}, 
        {6556969973348860604L, 8224048741023841477L}
    };

    /**
     * Hashes for pieces and squares
     */
    private final long[][] piecesHash;

    /**
     * Hash for black player to move
     */
    private final long blackToMove = 2703255076737973876L;

    /**
     * Hashes for differen enpassant files
     */
    private final long[] EN_PASSANT_FILES = {
        7338921424909686254L, // A
        7427175436213957123L, // B
        2898066206797371386L, // C
        1468308461554057123L, // D
        7503219113706599870L, // E
        8096443611919276782L, // F
        6554333725051357809L, // G
        9208530740313182802L, // H
    };

    public Zobrist() {
        piecesHash = fillPiecesHash(new Random(158479));
    }

    /**
     * @return long[12][64] containing unique hash number for
     * each piece (both colors) on each square on board
     */
    private long[][] fillPiecesHash(Random generator) {
        long[][] tmp = new long[Pieces.PIECE_COUNT*2][Size.BOARD_SIZE];
        for (int i = 0; i < Pieces.PIECE_COUNT*2; i++) {
            for (int j = 0; j < Size.BOARD_SIZE; j++) {
                tmp[i][j] = generator.nextLong();
            }
        }
        return tmp;
    }

    /**
     * @param board
     * @return hash for current gameboard
     */
    public long createHash(GameBoard board) {
        long key = 0;
        int[] whitePieces = board.getPieces(Colors.WHITE);
        int[] blackPieces = board.getPieces(Colors.BLACK);
        // Create hash for pieces
        for (int square = 0; square < Size.BOARD_SIZE; square++) {
            if (whitePieces[square] != -1) {
                key ^= piecesHash[whitePieces[square]][square];
            } else if (blackPieces[square] != -1) {
                key ^= piecesHash[blackPieces[square]+6][square];
            }
        }
        // Create hash for enpassant
        if (board.getCurrentPositon().getEnpassant() != 0) {
            key ^= (EN_PASSANT_FILES[board.getCurrentPositon().getEnpassant() & 7]);
        }
        // Create hash for Castling rights
        boolean[][] castlingRights = board.getCurrentPositon().getCastlingRights();
        for (int color = Colors.WHITE; color < Colors.COLOR_COUNT; color++) {
            for (int side = Pieces.KING; side < 2; side++) {
                if (castlingRights[color][side]) {
                    key ^= CASTLING_RIGHTS[color][side];
                }
            }
        }
        // Change hash if black player is moving
        if (board.getCurrentPositon().getSideToMove() == Colors.BLACK) {
            key ^= blackToMove;
        }
        return key;
    }

    /**
     * @param offset shifted number based on color and piece (e.g. color*6+piece)
     * @param from of castling
     * @return Hash for piece on square
     */
    public long getPieceHash(int offset, int square) {
        return piecesHash[offset][square];
    }

    /**
     * @param color of player who is playin now
     * @param side of castling
     * @return Hash for castling based on color and side
     */
    public long getCastlingRightsHash(int color, int side) {
        return CASTLING_RIGHTS[color][side];
    }

    /**
     * @param square where enpassant is
     * @return Hash for enpassant file based on square
     */
    public long getEnpassantHash(int square) {
        return EN_PASSANT_FILES[square & 7];
    }

    /**
     * @return Hash used for changing players
     */
    public long getBlackToMove() {
        return blackToMove;
    }

}
