package board;

import board.constants.Directions;
import board.constants.Files;
import board.constants.Ranks;
import board.constants.Size;

// https://www.chessprogramming.org/Magic_Bitboards -used Fancy
// http://pradu.us/old/Nov27_2008/Buzz/research/magic/Bitboards.pdf
// https://www.chessprogramming.org/index.php?title=Looking_for_Magics
// https://www.chessprogramming.org/Best_Magics_so_far
// magics found in array generated using Pradyumna Kannan (C++) code and (C)code from "Looking_for_Magics"

public class TrueMagic {
    private final Rays rays;
    
    private final long[] rookMagics = { // 4 * 16 = 64
        0xa180022080400230L, 0x40100040022000L,   0x80088020001002L,   0x80080280841000L,
        0x4200042010460008L, 0x4800a0003040080L,  0x400110082041008L,  0x8000a041000880L,
        0x10138001a080c010L, 0x804008200480L,     0x10011012000c0L,    0x22004128102200L,
        0x200081201200cL,    0x202a001048460004L, 0x81000100420004L,   0x4000800380004500L,
        0x208002904001L,     0x90004040026008L,   0x208808010002001L,  0x2002020020704940L,
        0x8048010008110005L, 0x6820808004002200L, 0xa80040008023011L,  0xb1460000811044L,
        0x4204400080008ea0L, 0xb002400180200184L, 0x2020200080100380L, 0x10080080100080L,
        0x2204080080800400L, 0xa40080360080L,     0x2040604002810b1L,  0x8c218600004104L,
        0x8180004000402000L, 0x488c402000401001L, 0x4018a00080801004L, 0x1230002105001008L,
        0x8904800800800400L, 0x42000c42003810L,   0x8408110400b012L,   0x18086182000401L,
        0x2240088020c28000L, 0x1001201040c004L,   0xa02008010420020L,  0x10003009010060L,
        0x4008008008014L,    0x80020004008080L,   0x282020001008080L,  0x50000181204a0004L,
        0x102042111804200L,  0x40002010004001c0L, 0x19220045508200L,   0x20030010060a900L,
        0x8018028040080L,    0x88240002008080L,   0x10301802830400L,   0x332a4081140200L,
        0x8080010a601241L,   0x1008010400021L,    0x4082001007241L,    0x211009001200509L,
        0x8015001002441801L, 0x801000804000603L,  0xc0900220024a401L,  0x1000200608243L
    };
    
    private final long[] bishopMagics = { // 4 * 16 = 64
        0x89a1121896040240L, 0x2004844802002010L, 0x2068080051921000L, 0x62880a0220200808L,
        0x4042004000000L,    0x100822020200011L,  0xc00444222012000aL, 0x28808801216001L,
        0x400492088408100L,  0x201c401040c0084L,  0x840800910a0010L,   0x82080240060L,
        0x2000840504006000L, 0x30010c4108405004L, 0x1008005410080802L, 0x8144042209100900L,
        0x208081020014400L,  0x4800201208ca00L,   0xf18140408012008L,  0x1004002802102001L,
        0x841000820080811L,  0x40200200a42008L,   0x800054042000L,     0x88010400410c9000L,
        0x520040470104290L,  0x1004040051500081L, 0x2002081833080021L, 0x400c00c010142L,
        0x941408200c002000L, 0x658810000806011L,  0x188071040440a00L,  0x4800404002011c00L,
        0x104442040404200L,  0x511080202091021L,  0x4022401120400L,    0x80c0040400080120L,
        0x8040010040820802L, 0x480810700020090L,  0x102008e00040242L,  0x809005202050100L,
        0x8002024220104080L, 0x431008804142000L,  0x19001802081400L,   0x200014208040080L,
        0x3308082008200100L, 0x41010500040c020L,  0x4012020c04210308L, 0x208220a202004080L,
        0x111040120082000L,  0x6803040141280a00L, 0x2101004202410000L, 0x8200000041108022L,
        0x21082088000L,      0x2410204010040L,    0x40100400809000L,   0x822088220820214L,
        0x40808090012004L,   0x910224040218c9L,   0x402814422015008L,  0x90014004842410L,
        0x1000042304105L,    0x10008830412a00L,   0x2520081090008908L, 0x40102000a0a60140L
    };
    
    // Structures holding legal moves, magic number, mask, bit-shift
    private final Struct[] rookStructs = new Struct[Size.BOARD_SIZE];
    private final Struct[] bishopStructs = new Struct[Size.BOARD_SIZE];
    
    // Long.Bitcount(rookMask[square])
    private final int[] rookBits = {
        12, 11, 11, 11, 11, 11, 11, 12,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        12, 11, 11, 11, 11, 11, 11, 12
    };
    // Long.Bitcount(bishopMask[square])
    private final int[] bishopBits = {
        6, 5, 5, 5, 5, 5, 5, 6,
        5, 5, 5, 5, 5, 5, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 5, 5, 5, 5, 5, 5,
        6, 5, 5, 5, 5, 5, 5, 6
    };

    public TrueMagic(Rays rays) {
        this.rays = rays;
        fillOptimalBishop();
        fillOptimalRook();
        init();
        long[][] bishopBoards = generateBoards(bishopStructs);
        long[][] rookBoards = generateBoards(rookStructs);
        generateAllBishopMoves(bishopBoards);
        generateAllRookMoves(rookBoards);
    }

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
    
    /**
     * Initializes Bishop/Rook structures with correct magic numbers, bit-shifts, masks
     */
    private void init() {
        for (int square = 0; square < Size.BOARD_SIZE; square++) {
			rookStructs[square] = new Struct(rookMagics[square], Long.SIZE - rookBits[square], rookMask(square));
            bishopStructs[square] = new Struct(bishopMagics[square], Long.SIZE  - bishopBits[square], bishopMask(square));
		}
    }

    /**
     * @param magics strucutres of either rook or bishop
     * @return all possible board variations
     */
    private long[][] generateBoards(Struct[] magics) {
        // Boards for all squares where piece could be
		long[][] occ = new long[Size.BOARD_SIZE][];
        // Iterate over all possible location of piece on board
		for (int square = 0; square < Size.BOARD_SIZE; square++) { 
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
		for (int square = 0; square < Size.BOARD_SIZE; square++) {
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
		for (int square = 0; square < Size.BOARD_SIZE; square++) {
			bishopStructs[square].magicMoves = new long[bishopBoards[square].length]; // Copy size
            // Calculate hash for all boards
			for (int variationIndex = 0; variationIndex < bishopBoards[square].length; variationIndex++) {
                // Hash
				int key = (int) ((bishopBoards[square][variationIndex] * bishopStructs[square].magic) >>> bishopStructs[square].shift);
				bishopStructs[square].magicMoves[key] = Bishop(bishopBoards[square][variationIndex], square);
			}
		}
    }

    /**
     * @param blockers pieces on board
     * @param square where rook is
     * @return moves for rook
     */
    private long Rook(long blockers, int square){
        long moves = Size.ZERO;
        moves |= rays.South(square, blockers); 
        moves |= rays.North(square, blockers);
        moves |= rays.West(square, blockers);
        moves |= rays.East(square, blockers);
        return moves;
    }

    //Bishop legal moves with "classical" approach
    private long Bishop(long blockers, int square) {
        long moves = Size.ZERO;
        moves |= rays.southEast(square, blockers);
        moves |= rays.southWest(square, blockers);
        moves |= rays.northEast(square, blockers);
        moves |= rays.northWest(square, blockers);
        return moves;
    }

    // Best magic numbers for rook and required bits for shift
    private final void fillOptimalRook(){
        rookMagics[48] = 0x48FFFE99FECFAA00L;
        rookBits[48] = 10; //48 = 0x48FFFE99FECFAA00 10
        rookMagics[49] = 0x48FFFE99FECFAA00L;
        rookBits[49] = 9; //49 = 0x48FFFE99FECFAA00	  9
        rookMagics[50] = 0x497FFFADFF9C2E00L;
        rookBits[50] = 9; //50 = 0x497FFFADFF9C2E00   9
        rookMagics[51] = 0x613FFFDDFFCE9200L;
        rookBits[51] = 9; //51 = 0x613FFFDDFFCE9200   9
        rookMagics[52] = 0xffffffe9ffe7ce00L;
        rookBits[52] = 9; //52 = 0xffffffe9ffe7ce00   9
        rookMagics[53] = 0xfffffff5fff3e600L;
        rookBits[53] = 9; //53 = 0xfffffff5fff3e600   9
        rookMagics[54] = 0x0003ff95e5e6a4c0L;
        rookBits[54] = 9; //54 = 0x0003ff95e5e6a4c0   9
        rookMagics[55] = 0x510FFFF5F63C96A0L;
        rookBits[55] = 10; //55 = 0x510FFFF5F63C96A0 10
        rookMagics[56] = 0xEBFFFFB9FF9FC526L;
        rookBits[56] = 11; //56 = 0xEBFFFFB9FF9FC526 11
        rookMagics[57] = 0x61FFFEDDFEEDAEAEL;
        rookBits[57] = 10; //57 = 0x61FFFEDDFEEDAEAE 10
        rookMagics[58] = 0x53BFFFEDFFDEB1A2L;
        rookBits[58] = 10; //58 = 0x53BFFFEDFFDEB1A2 10
        rookMagics[59] = 0x127FFFB9FFDFB5F6L;
        rookBits[59] = 10; //59 = 0x127FFFB9FFDFB5F6 10
        rookMagics[60] = 0x411FFFDDFFDBF4D6L;
        rookBits[60] = 10; //60 = 0x411FFFDDFFDBF4D6 10
        rookMagics[62] = 0x0003ffef27eebe74L;
        rookBits[62] = 10; //62 = 0x0003ffef27eebe74 10
        rookMagics[63] = 0x7645FFFECBFEA79EL;
        rookBits[63] = 11; //63 = 0x7645FFFECBFEA79E 11
    }
    // Best magic numbers for bishops and required bits for shift
    private final void fillOptimalBishop(){
        bishopMagics[0] = 0xffedf9fd7cfcffffL;
        bishopBits[0] = 5; //0 = 0xffedf9fd7cfcffff   5
        bishopMagics[1] = 0xfc0962854a77f576L;
        bishopBits[1] = 4; //1 = 0xfc0962854a77f576   4
        bishopMagics[6] = 0xfc0a66c64a7ef576L;
        bishopBits[6] = 4;//6 = 0xfc0a66c64a7ef576    4
        bishopMagics[7] = 0x7ffdfdfcbd79ffffL;
        bishopBits[7] = 5;//7 = 0x7ffdfdfcbd79ffff    5
        bishopMagics[8] = 0xfc0846a64a34fff6L;
        bishopBits[8] = 4;//8 = 0xfc0846a64a34fff6    4
        bishopMagics[9] = 0xfc087a874a3cf7f6L;
        bishopBits[9] = 4; //9 = 0xfc087a874a3cf7f6   4
        bishopMagics[14] = 0xfc0864ae59b4ff76L;
        bishopBits[14] = 4; //14 = 0xfc0864ae59b4ff76 4
        bishopMagics[15] = 0x3c0860af4b35ff76L;
        bishopBits[15] = 4; //15 = 0x3c0860af4b35ff76 4
        bishopMagics[16] = 0x73C01AF56CF4CFFBL;
        bishopBits[16] = 4; //16 = 0x73C01AF56CF4CFFB 4
        bishopMagics[17] = 0x41A01CFAD64AAFFCL;
        bishopBits[17] = 4; //17 = 0x41A01CFAD64AAFFC 4
        bishopMagics[22] = 0x7c0c028f5b34ff76L;
        bishopBits[22] = 4; //22 = 0x7c0c028f5b34ff76 4
        bishopMagics[23] = 0xfc0a028e5ab4df76L;
        bishopBits[23] = 4; //23 = 0xfc0a028e5ab4df76 4
        bishopMagics[40] = 0xDCEFD9B54BFCC09FL;
        bishopBits[40] = 4; //40 = 0xDCEFD9B54BFCC09F 4
        bishopMagics[41] = 0xF95FFA765AFD602BL;
        bishopBits[41] = 4; //41 = 0xF95FFA765AFD602B 4
        bishopMagics[46] = 0x43ff9a5cf4ca0c01L;
        bishopBits[46] = 4; //46 = 0x43ff9a5cf4ca0c01 4
        bishopMagics[47] = 0x4BFFCD8E7C587601L;
        bishopBits[47] = 4; //47 = 0x4BFFCD8E7C587601 4
        bishopMagics[48] = 0xfc0ff2865334f576L;
        bishopBits[48] = 4; //48 = 0xfc0ff2865334f576 4
        bishopMagics[49] = 0xfc0bf6ce5924f576L;
        bishopBits[49] = 4; //49 = 0xfc0bf6ce5924f576 4
        bishopMagics[54] = 0xc3ffb7dc36ca8c89L;
        bishopBits[54] = 4; //54 = 0xc3ffb7dc36ca8c89 4
        bishopMagics[55] = 0xc3ff8a54f4ca2c89L;
        bishopBits[55] = 4; //55 = 0xc3ff8a54f4ca2c89 4
        bishopMagics[56] = 0xfffffcfcfd79edffL;
        bishopBits[56] = 5; //56 = 0xfffffcfcfd79edff 5
        bishopMagics[57] = 0xfc0863fccb147576L;
        bishopBits[57] = 4; //57 = 0xfc0863fccb147576 4
        bishopMagics[62] = 0xfc087e8e4bb2f736L;
        bishopBits[62] = 4; //62 = 0xfc087e8e4bb2f736 4
        bishopMagics[63] = 0x43ff9e4ef4ca2c89L;
        bishopBits[63] = 5; //63 = 0x43ff9e4ef4ca2c89 5
    }
}
