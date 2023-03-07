package gameboard.move_gen.magic;

import gameboard.constants.Board;
import gameboard.constants.bitboard.Directions;
import gameboard.constants.bitboard.Files;
import gameboard.constants.bitboard.Ranks;
import gameboard.constants.bitboard.Rays;

// https://www.chessprogramming.org/Magic_Bitboards - used Fancy
// http://pradu.us/old/Nov27_2008/Buzz/research/magic/Bitboards.pdf
// https://www.chessprogramming.org/index.php?title=Looking_for_Magics
// https://www.chessprogramming.org/Best_Magics_so_far
// Magics found in array generated using Pradyumna Kannan (C++) code and (C) code from "Looking_for_Magics",

/**
 * Class holding magic bitboards
 */
public class MagicBoard {
    private final Rays rays;
    
    public final long[] rookMagics = { // 4 * 16 = 64
        0xA180022080400230L, 0x40100040022000L,   0x80088020001002L,   0x80080280841000L,
        0x4200042010460008L, 0x4800A0003040080L,  0x400110082041008L,  0x8000A041000880L,
        0x10138001A080C010L, 0x804008200480L,     0x10011012000C0L,    0x22004128102200L,
        0x200081201200CL,    0x202A001048460004L, 0x81000100420004L,   0x4000800380004500L,
        0x208002904001L,     0x90004040026008L,   0x208808010002001L,  0x2002020020704940L,
        0x8048010008110005L, 0x6820808004002200L, 0xA80040008023011L,  0xB1460000811044L,
        0x4204400080008EA0L, 0xB002400180200184L, 0x2020200080100380L, 0x10080080100080L,
        0x2204080080800400L, 0xA40080360080L,     0x2040604002810B1L,  0x8C218600004104L,
        0x8180004000402000L, 0x488C402000401001L, 0x4018A00080801004L, 0x1230002105001008L,
        0x8904800800800400L, 0x42000C42003810L,   0x8408110400B012L,   0x18086182000401L,
        0x2240088020C28000L, 0x1001201040C004L,   0xA02008010420020L,  0x10003009010060L,
        0x4008008008014L,    0x80020004008080L,   0x282020001008080L,  0x50000181204A0004L,
        0x48FFFE99FECFAA00L, 0x48FFFE99FECFAA00L, 0x497FFFADFF9C2E00L, 0x613FFFDDFFCE9200L,
        0xFFFFFFE9FFE7CE00L, 0xFFFFFFF5FFF3E600L, 0x3FF95E5E6A4C0L,    0x510FFFF5F63C96A0L,
        0xEBFFFFB9FF9FC526L, 0x61FFFEDDFEEDAEAEL, 0x53BFFFEDFFDEB1A2L, 0x127FFFB9FFDFB5F6L,
        0x411FFFDDFFDBF4D6L, 0x801000804000603L,  0x3FFEF27EEBE74L,    0x7645FFFECBFEA79EL
    };
    
    public final long[] bishopMagics = { // 4 * 16 = 64
        0xFFEDF9FD7CFCFFFFL, 0xFC0962854A77F576L, 0x2068080051921000L, 0x62880A0220200808L,
        0x4042004000000L,    0x100822020200011L,  0xFC0A66C64A7EF576L, 0x7FFDFDFCBD79FFFFL,
        0xFC0846A64A34FFF6L, 0xFC087A874A3CF7F6L, 0x840800910A0010L,   0x82080240060L,
        0x2000840504006000L, 0x30010C4108405004L, 0xFC0864AE59B4FF76L, 0x3C0860AF4B35FF76L,
        0x73C01AF56CF4CFFBL, 0x41A01CFAD64AAFFCL, 0xF18140408012008L,  0x1004002802102001L,
        0x841000820080811L,  0x40200200A42008L,   0x7C0C028F5B34FF76L, 0xFC0A028E5AB4DF76L,
        0x520040470104290L,  0x1004040051500081L, 0x2002081833080021L, 0x400C00C010142L,
        0x941408200C002000L, 0x658810000806011L,  0x188071040440A00L,  0x4800404002011C00L,
        0x104442040404200L,  0x511080202091021L,  0x4022401120400L,    0x80C0040400080120L,
        0x8040010040820802L, 0x480810700020090L,  0x102008E00040242L,  0x809005202050100L,
        0xDCEFD9B54BFCC09FL, 0xF95FFA765AFD602BL, 0x19001802081400L,   0x200014208040080L,
        0x3308082008200100L, 0x41010500040C020L,  0x43FF9A5CF4CA0C01L, 0x4BFFCD8E7C587601L,
        0xFC0FF2865334F576L, 0xFC0BF6CE5924F576L, 0x2101004202410000L, 0x8200000041108022L,
        0x21082088000L,      0x2410204010040L,    0xC3FFB7DC36CA8C89L, 0xC3FF8A54F4CA2C89L,
        0xFFFFFCFCFD79EDFFL, 0xFC0863FCCB147576L, 0x402814422015008L,  0x90014004842410L,
        0x1000042304105L,    0x10008830412A00L,   0xFC087E8E4BB2F736L, 0x43FF9E4EF4CA2C89L
    };
        
    // Long.Bitcount(rookMask[square])
    public final int[] rookBits = { // 8x8 = 64
        12, 11, 11, 11, 11, 11, 11, 12, // 8
        11, 10, 10, 10, 10, 10, 10, 11, // 16
        11, 10, 10, 10, 10, 10, 10, 11, // 24
        11, 10, 10, 10, 10, 10, 10, 11, // 32
        11, 10, 10, 10, 10, 10, 10, 11, // 40
        11, 10, 10, 10, 10, 10, 10, 11, // 48
        10,  9,  9,  9,  9,  9,  9, 10, // 56
        11, 10, 10, 10, 10, 11, 10, 11  // 64
    };
    // Long.Bitcount(bishopMask[square])
    public final int[] bishopBits = { // 8x8 = 64
        5, 4, 5, 5, 5, 5, 4, 5, // 8
        4, 4, 5, 5, 5, 5, 4, 4, // 16
        4, 4, 7, 7, 7, 7, 4, 4, // 24
        5, 5, 7, 9, 9, 7, 5, 5, // 32
        5, 5, 7, 9, 9, 7, 5, 5, // 40 
        4, 4, 7, 7, 7, 7, 4, 4, // 48
        4, 4, 5, 5, 5, 5, 4, 4, // 56
        5, 4, 5, 5, 5, 5, 4, 5  // 64
    };

    // Structures holding legal moves, magic number, mask, bit-shift
    private final Struct[] rookStructs = new Struct[Board.BOARD_SIZE];
    private final Struct[] bishopStructs = new Struct[Board.BOARD_SIZE];

    public MagicBoard(Rays rays) {
        this.rays = rays;
        // Initializes Bishop/Rook structures with correct magic numbers, bit-shifts, masks
        for (int square = 0; square < Board.BOARD_SIZE; square++) {
			rookStructs[square] = new Struct(
                rookMagics[square], Long.SIZE - rookBits[square], rookMask(square)
            );
            bishopStructs[square] = new Struct(
                bishopMagics[square], Long.SIZE - bishopBits[square], bishopMask(square)
            );
		}
        long[][] bishopBoards = generateBoards(bishopStructs);
        long[][] rookBoards = generateBoards(rookStructs);
        generateAllBishopMoves(bishopBoards);
        generateAllRookMoves(rookBoards);
    }

    // ---------------------------- Magic Moves ---------------------------- 

    /**
     * @param square where rook is
     * @param allPieces on board
     * @return legal moves of rook (same colored pieces have to be removed)
     */
    public long getRookMoves(final int square, final long allPieces) {
		final Struct rook = rookStructs[square]; // pick the correct structure on given square
        // Unhash index
		return rook.magicMoves[(int) ((allPieces & rook.movementMask) * rook.magic >>> rook.shift)]; 
	}

    /**
     * @param square where bishop is
     * @param allPieces on board
     * @return legal moves of bishop (same colored pieces have to be removed)
     */
	public long getBishopMoves(final int square, final long allPieces) {
		final Struct bishop = bishopStructs[square]; // pick the correct structure on given square
        // Unhash index
		return bishop.magicMoves[(int) ((allPieces & bishop.movementMask) * bishop.magic >>> bishop.shift)]; 
    }

    // ---------------------------- Masks ---------------------------- 

    /**
     * @param square where rook is
     * @return legal moves on emptyboard (without edges)
     */
    private long rookMask(int square) {
        long south = rays.getRay(Directions.SOUTH, square);
        long north = rays.getRay(Directions.NORTH, square);
        long east = rays.getRay(Directions.EAST, square);
        long west = rays.getRay(Directions.WEST, square);
        // Remove edges
        south ^= (south & Ranks.RANK_8);
        north ^= (north & Ranks.RANK_1);
        east ^= (east & Files.FILE_H);
        west ^= (west & Files.FILE_A);
        return (south | north | east | west);    
	}

    /**
     * @param square where bishop is
     * @return legal moves on emptyboard (without edges)
     */
	private long bishopMask(int square) { 
        final long edgeSquares = (Files.FILE_A | Files.FILE_H | Ranks.RANK_1 | Ranks.RANK_8);
        return (
            (rays.getRay(Directions.NORTH_EAST, square) | rays.getRay(Directions.NORTH_WEST, square)  |
             rays.getRay(Directions.SOUTH_EAST, square) | rays.getRay(Directions.SOUTH_WEST, square)) & 
            ~edgeSquares
        );
	}
    
    // ---------------------------- Generators ---------------------------- 

    /**
     * @param magics strucutres of either rook or bishop
     * @return all possible board variations
     */
    private long[][] generateBoards(Struct[] magics) {
        // Boards for all squares where piece could be
		long[][] occ = new long[Board.BOARD_SIZE][];
        // Iterate over all possible location of piece on board
		for (int square = 0; square < Board.BOARD_SIZE; square++) { 
            // Number of all possible boards
			int allVariations = 1 << Long.bitCount(magics[square].movementMask); 
            // Allocate all boards
            occ[square] = new long[allVariations];
            // Iterate over the all possible variations
			for (int variationIndex = 1; variationIndex < allVariations; variationIndex++) {
                long currentMask = magics[square].movementMask; // Current movementMask
                // Remove moves from board to generate all variations
                for (int i = 0; i < (Integer.SIZE - Integer.numberOfLeadingZeros(variationIndex)); i++) {
					if (((1 << i) & variationIndex) != 0) { // until board is empty
						occ[square][variationIndex] |= Long.lowestOneBit(currentMask); // popped bit
					}
					currentMask &= currentMask - 1; // remove first bit of number (101)->(001)
				}
			}
		}
		return occ;
    }

    /**
     * @param rookBoards all possible moves for rook
     */
    private void generateAllRookMoves(long[][] rookBoards) {
        // For all squares on board
		for (int square = 0; square < Board.BOARD_SIZE; square++) {
			rookStructs[square].magicMoves = new long[rookBoards[square].length]; // Copy size
            // Calculate hash for all boards
            for (int variationIndex = 0; variationIndex < rookBoards[square].length; variationIndex++) {
                // Hash
				int key = (int) ((rookBoards[square][variationIndex] * rookStructs[square].magic) >>> rookStructs[square].shift);
				rookStructs[square].magicMoves[key] = Rook(rookBoards[square][variationIndex], square);
			}
		}
    }

    /**
     * @param bishopBoards all possible moves for bishop
     */
    private void generateAllBishopMoves(long[][] bishopBoards) {
        // For all squares on board
		for (int square = 0; square < Board.BOARD_SIZE; square++) {
			bishopStructs[square].magicMoves = new long[bishopBoards[square].length]; // Copy size
            // Calculate hash for all boards
			for (int variationIndex = 0; variationIndex < bishopBoards[square].length; variationIndex++) {
                // Hash
				int key = (int) ((bishopBoards[square][variationIndex] * bishopStructs[square].magic) >>> bishopStructs[square].shift);
				bishopStructs[square].magicMoves[key] = Bishop(bishopBoards[square][variationIndex], square);
			}
		}
    }

    // ---------------------------- Pieces ---------------------------- 

    /**
     * @param blockers pieces on board
     * @param square where rook is
     * @return moves for rook
     */
    private long Rook(long blockers, int square) {
        long moves = Board.ZERO;
        moves |= rays.South(square, blockers); 
        moves |= rays.North(square, blockers);
        moves |= rays.West(square, blockers);
        moves |= rays.East(square, blockers);
        return moves;
    }

    /**
     * @param blockers pieces on board
     * @param square where bishop is
     * @return moves for bishop
     */
    private long Bishop(long blockers, int square) {
        long moves = Board.ZERO;
        moves |= rays.southEast(square, blockers);
        moves |= rays.southWest(square, blockers);
        moves |= rays.northEast(square, blockers);
        moves |= rays.northWest(square, blockers);
        return moves;
    }
}
