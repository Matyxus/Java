package chess2.board;
//https://www.chessprogramming.org/Magic_Bitboards -used Fancy
//http://pradu.us/old/Nov27_2008/Buzz/research/magic/Bitboards.pdf
//https://www.chessprogramming.org/index.php?title=Looking_for_Magics
//https://www.chessprogramming.org/Best_Magics_so_far
//magics found in array generated using Pradyumna Kannan (C++) code and (C)code from "Looking_for_Magics" (more in readMe).
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
    
    //Structures holding legal moves, magic number, mask, bit-shift
    private final Struct[] rookStructs = new Struct[Holder.BOARD_SIZE];
    private final Struct[] bishopStructs = new Struct[Holder.BOARD_SIZE];
    
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

    public TrueMagic(Rays rays){
        this.rays = rays;
        fillOptimalBishop();
        fillOptimalRook();
        init();
        bishopMask();
        rookMask();
        long[][] bishopBoards = generateBoards(bishopStructs);
        long[][] rookBoards = generateBoards(rookStructs);
        generateAllBishopMoves(bishopBoards);
        generateAllRookMoves(rookBoards);
    }

    private void rookMask() { // moves on emptyboard (without edge squares)
        for (int square = 0; square < Holder.BOARD_SIZE; square++) {
            long south = rays.getRay(Holder.SOUTH, square);
            long north = rays.getRay(Holder.NORTH, square);
            long east = rays.getRay(Holder.EAST, square);
            long west = rays.getRay(Holder.WEST, square);
            //remove edges
            south ^= (south & Holder.RANK_8);
            north ^= (north & Holder.RANK_1);
            east ^= (east & Holder.FILE_H);
            west ^= (west & Holder.FILE_A);
            //save the moves
            rookStructs[square].movementMask = (south | north | east | west);
        }
	}

	private void bishopMask() { // moves on emptyboard (without edge squares)
        long edgeSquares = (Holder.FILE_A | Holder.FILE_H | Holder.RANK_1 | Holder.RANK_8);
        for (int square = 0; square < Holder.BOARD_SIZE; square++) {
            bishopStructs[square].movementMask = (rays.getRay(Holder.NORTH_EAST, square) | rays.getRay(Holder.NORTH_WEST, square) |
            rays.getRay(Holder.SOUTH_EAST, square) | rays.getRay(Holder.SOUTH_WEST, square)) & ~(edgeSquares);
        }
	}
    //get legal moves
    public long getRookMoves(final int fromSquare, final long allPieces) {
		final Struct rook = rookStructs[fromSquare]; // pick the correct structure on given square
		return rook.magicMoves[(int) ((allPieces & rook.movementMask) * rook.magic >>> rook.shift)]; // unHash index
	}
    //get legal moves
	public long getBishopMoves(final int fromSquare, final long allPieces) {
		final Struct bishop = bishopStructs[fromSquare]; // pick the correct structure on given square
		return bishop.magicMoves[(int) ((allPieces & bishop.movementMask) * bishop.magic >>> bishop.shift)]; // unHash index
    }
    //create Structures with correct magicNumber, bit-shift
    private void init() {
        for (int i = 0; i < Holder.BOARD_SIZE; i++) {
			rookStructs[i] = new Struct(rookMagics[i]);
            bishopStructs[i] = new Struct(bishopMagics[i]);
            rookStructs[i].shift = 64 - rookBits[i]; // 64 = max bits in long
			bishopStructs[i].shift = 64 - bishopBits[i];
		}
    }
    // create all possible boards with all possible pieces location (without hashing = +/- 147.57 exabytes))
    // removing bits from (bishop/rook)Masks until empty -> all possible boards on given square
    private long[][] generateBoards(Struct[] magics) {
		long[][] occ = new long[Holder.BOARD_SIZE][];//init 64 squares
		for (int square = 0; square < Holder.BOARD_SIZE; square++) { // iter possible location of piece (64 = whole board)
			int allVariations = 1 << Long.bitCount(magics[square].movementMask);//num of all possible boards
            occ[square] = new long[allVariations];//almost minimal number of indexes needed to store all moves
            // iter over the all possible variations
			for (int variationIndex = 1; variationIndex < allVariations; variationIndex++) {
                long currentMask = magics[square].movementMask; //current movementMask
                //another possibility is int x = Long.numberOfTrailingZeros(currentMask), long loc = 0b1L<<x (currentMask XOR= loc))
                // if (loc & variationIndex ) != 0, occ[square][variationIndex] |= loc 
                for (int i = 0; i < 32 - Integer.numberOfLeadingZeros(variationIndex); i++) { // 32 = max number of bits in int
					if (((1 << i) & variationIndex) != 0) { // until board is empty
						occ[square][variationIndex] |= Long.lowestOneBit(currentMask);// popped bit
					}
					currentMask &= currentMask - 1; // remove first bit of number (101)->(001)
				}
			}
		}
		return occ;
    }
    // create legal moves on artificially created boards for rook, including edgeSquares
    private void generateAllRookMoves(long[][] rookBoards) {
		for (int square = 0; square < Holder.BOARD_SIZE; square++) { // for all possible squares on board
			rookStructs[square].magicMoves = new long[rookBoards[square].length];//copy size
            for (int variationIndex = 0; variationIndex < rookBoards[square].length; variationIndex++) {//for all boards
                // hash
				int key = (int) ((rookBoards[square][variationIndex] * rookStructs[square].magic) >>> rookStructs[square].shift);
				rookStructs[square].magicMoves[key] = Rook(rookBoards[square][variationIndex], square); // store the moves
			}
		}
    }
    // create legal moves on artificially created boards for bishop, including edgeSquares
    private void generateAllBishopMoves(long[][] bishopBoards) {
		for (int square = 0; square < Holder.BOARD_SIZE; square++) {// for all possible squares on board
			bishopStructs[square].magicMoves = new long[bishopBoards[square].length];//copy size
			for (int variationIndex = 0; variationIndex < bishopBoards[square].length; variationIndex++) {//for all boards
				int key = (int) ((bishopBoards[square][variationIndex] * bishopStructs[square].magic) >>> bishopStructs[square].shift);
				bishopStructs[square].magicMoves[key] = Bishop(bishopBoards[square][variationIndex], square);
			}
		}
    }
    // Rook legal moves with "classical" approach
    private long Rook(long blockers, int square){
        long moves = Holder.ZERO;
        moves |= rays.South(square, blockers); 
        moves |= rays.North(square, blockers);
        moves |= rays.West(square, blockers);
        moves |= rays.East(square, blockers);
        return moves;
    }
    //Bishop legal moves with "classical" approach
    private long Bishop(long blockers, int square) {
        long moves = Holder.ZERO;
        moves |= rays.southEast(square, blockers);
        moves |= rays.southWest(square, blockers);
        moves |= rays.northEast(square, blockers);
        moves |= rays.northWest(square, blockers);
        return moves;
    }
    // best magicNumbers for rook and required bits for shift
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
    // best magicNumbers for bishops and required bits for shift
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
