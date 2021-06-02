package chess2.computer;
import chess2.PopUps;
import chess2.board.GameBoard;
import chess2.board.Spot;
import java.util.HashMap;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import chess2.board.Holder;
// https://github.com/official-stockfish/Stockfish/blob/master/src/evaluate.cpp
// https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
// https://www.chessprogramming.org/Evaluation
// https://chess.stackexchange.com/questions/17957/how-to-write-a-chess-evaluation-function
public class MinMaxPlayer {
	// eval params
	private final int myColor;
	private final int enemyColor;
	private final Random RAND = new Random(System.currentTimeMillis());
	private long enemyAttacks = 0;
	private long myAttacks = 0;
	private long myPawnAttacks = 0;
	private long enemyPawnAttacks = 0;
	private int tempScore = 0;
	private long myWeaklyProtected = 0;
	private long myStronglyProtected = 0;
	private long enemyWeaklyProtected = 0;
	private long enemyStronglyProteted = 0;
	//
	private GameBoard gameBoard;
	private int chosenPiece;
	private int toSquare;
	private int fromSquare;
	private HashMap<Integer, SimpleEntry<Integer, Integer>> myMoves = new HashMap<Integer, SimpleEntry<Integer, Integer>>();
	private int best = Integer.MIN_VALUE;
	private final int MAX_DEPTH = 4;
	private boolean enpassant = false;
	
	public MinMaxPlayer(int color) {
		this.gameBoard = new GameBoard();
		this.gameBoard.enableEnpassant(PopUps.enpassant);
		this.gameBoard.enableCastling(false);
		enpassant = PopUps.enpassant;
		myColor = color;
		enemyColor = (myColor+1)%2;
	}

	public void findMove(GameBoard board) {
		this.gameBoard.loadGameBoard(board.getWhitePieces(), board.getBlackPieces(), myColor);
		if (enpassant){
			enpassant = gameBoard.possibleEnpassant(); // check if enpassant is possible, disable if not
		}
		best = Integer.MIN_VALUE;
		myMoves.clear();
		miniMax(MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		myMoves.forEach((score, entry)->{
			if (score > best){
				best = score;
			}
		});
		fromSquare  = myMoves.get(best).getKey();
		toSquare = myMoves.get(best).getValue();
		
		if (myColor == Holder.WHITE) {
			chosenPiece = board.getWhitePieces().get(fromSquare).getPiece();
		} else {
			chosenPiece = board.getBlackPieces().get(fromSquare).getPiece();
		}
	}

	public int getChosenPiece() {
		return chosenPiece;
	}

	public int getFromSquare() {
		return fromSquare;
	}

	public int getToSquare() {
		return toSquare;
	}

    public int evaluate() {
		if (gameBoard.getPiecesCount(myColor, Holder.KING) == 0){
			return Integer.MIN_VALUE;
		} else if (gameBoard.getPiecesCount(enemyColor, Holder.KING) == 0){
			return Integer.MAX_VALUE;
		}
		// SETUP
		myWeaklyProtected = 0;
		myStronglyProtected = 0;
		enemyWeaklyProtected = 0;
		enemyStronglyProteted = 0;
		int value = 0;
		tempScore = 0;
		HashMap<Integer, Spot> myPieces;
		HashMap<Integer, Spot> enemyPieces;
		if (myColor == Holder.WHITE) {
			gameBoard.updateWhite(); 
			myPieces = gameBoard.getWhitePieces();
			enemyPieces = gameBoard.getBlackPieces();
			gameBoard.updateBlack();
		} else {
			gameBoard.updateBlack(); 
			myPieces = gameBoard.getBlackPieces();
			enemyPieces = gameBoard.getWhitePieces();
			gameBoard.updateWhite();
		}
		// enemyAttacks
		enemyAttacks = gameBoard.getPieces().getAttacks(enemyColor); 
		// set myself as enemy
		gameBoard.getPieces().setEnemyPlayer(myColor); 
		// myAttacks, has to be generated again, since legal moves exclude my Pieces (super fast)
		myPieces.forEach((square, spot)->gameBoard.getPieces().generateAttacks(spot.getPiece(), square)); 
		myAttacks = gameBoard.getPieces().getAttacks(myColor); 
		// myPawnAttacks
		myPieces.forEach((square, spot)->{ 
			if (spot.getPiece() == Holder.PAWN){
				myPawnAttacks |= spot.getAttacks();
			}
		});
		// My pieces on bitboard
		long myPawns = gameBoard.getAllIndividualPieces(Holder.PAWN, myColor);
		long myBishops = gameBoard.getAllIndividualPieces(Holder.BISHOP, myColor);
		long myKnights = gameBoard.getAllIndividualPieces(Holder.KNIGHT, myColor);
		long myRooks = gameBoard.getAllIndividualPieces(Holder.ROOK, myColor);
		long myQueen = gameBoard.getAllIndividualPieces(Holder.QUEEN, myColor);
		long myAllPieces = gameBoard.getAllPieces(myColor);
		// Enemy pieces on bitboard
		long enemyPawns = gameBoard.getAllIndividualPieces(Holder.PAWN, enemyColor);
		//long enemyBishops = gameBoard.getAllIndividualPieces(Holder.BISHOP, enemyColor); #unused
		//long enemyKnights = gameBoard.getAllIndividualPieces(Holder.KNIGHT, enemyColor); #unused
		//long enemyRooks = gameBoard.getAllIndividualPieces(Holder.ROOK, enemyColor); #unused
		long enemyQueen = gameBoard.getAllIndividualPieces(Holder.QUEEN, enemyColor);
		long enemyAllPieces = gameBoard.getAllPieces(enemyColor);
		// END OF SETUP
		// START OF MOBILITY, PIECES VALUE
		value -= Long.bitCount(enemyAllPieces);
		value += Long.bitCount(myAllPieces);
		for (Spot spot : myPieces.values()) {
			value += Long.bitCount(spot.getMoves());
			value += 45*PiecesTables.getIndividValue(spot.getPiece());
		}
		for (Spot spot : enemyPieces.values()) {
			value -= Long.bitCount(spot.getMoves());
			value -= 45*PiecesTables.getIndividValue(spot.getPiece());
		}
		myPieces.forEach((square, spot) -> tempScore += PiecesTables.getPieceValue(spot.getPiece(), myColor, square));
		value += tempScore;
		tempScore = 0;
		enemyPieces.forEach((square, spot) -> tempScore -= PiecesTables.getPieceValue(spot.getPiece(), enemyColor, square));
		value += tempScore;
		// END OF MOBILITY, PIECES VALUE
		// START OF WEAKLY/STRONGLY PROTECTED PIECES
		myStronglyProtected |= (myPawnAttacks & myAllPieces);
		myPieces.forEach((square, spot) -> { // includes myPieces
			long attacksMoves = spot.getAttacks();
			if (spot.getPiece() == Holder.KING){
				attacksMoves = gameBoard.getRays().getKing(square);
			} else if (spot.getPiece() == Holder.QUEEN){
				attacksMoves = ((gameBoard.getPieces().getTrueMagic().getBishopMoves(square, gameBoard.blockers) |
								gameBoard.getPieces().getTrueMagic().getRookMoves(square, gameBoard.blockers)));
			}
			if ((myWeaklyProtected & (attacksMoves & myAllPieces)) != 0) {
				myStronglyProtected |= (myWeaklyProtected & (attacksMoves & myAllPieces));
			}
			myWeaklyProtected |= (attacksMoves & myAllPieces);
		});
		value += Long.bitCount(myWeaklyProtected); // bonus for weaklyProtected pieces
		value += 2*Long.bitCount(myStronglyProtected); // bonus for stronglyProtected pieces
		value -= Long.bitCount((myAllPieces & myWeaklyProtected)); // minus for notProtected pieces
		enemyStronglyProteted |= (enemyPawnAttacks & enemyAllPieces);
		enemyPieces.forEach((square, spot) -> { // includes enemyPieces
			long attacksMoves = spot.getAttacks();
			if (spot.getPiece() == Holder.KING) {
				attacksMoves = gameBoard.getRays().getKing(square);
			} else if (spot.getPiece() == Holder.QUEEN) {
				attacksMoves = ((gameBoard.getPieces().getTrueMagic().getBishopMoves(square, gameBoard.blockers) |
								gameBoard.getPieces().getTrueMagic().getRookMoves(square, gameBoard.blockers)));
			}
			if ((enemyWeaklyProtected & (attacksMoves & enemyAllPieces)) != 0) {
				enemyStronglyProteted |= (myWeaklyProtected & (attacksMoves & enemyAllPieces));
			}
			enemyWeaklyProtected |= (attacksMoves & enemyAllPieces);
		});
		value -= Long.bitCount(myAttacks & enemyWeaklyProtected); // minus for enemy Weakly Protected
		value -= 2*Long.bitCount(myAttacks & enemyStronglyProteted);// minus for enemy Strongly Protected
		// END OF WEAKLY/STRONGLY PROTECTED PIECES
		// START OF PAWNS (doubled pawns, backward pawn, isolated)
		for (long file : Holder.FILES) {
			if (Long.bitCount(myPawns & file) != 1) {
				value -= 2;
			}
			if (Long.bitCount(enemyPawns & file) != 1) {
				value += 2;
			}
			// bonus for rook on same file as queen
			if ((file & myQueen) != 0 && (file & myRooks) != 0) {
				value += 3;
				break;
			}
		}
		long backWardPawn = 63-Long.numberOfLeadingZeros(myPawns);
		if ((backWardPawn & enemyAttacks) != 0){
			value -= 2;
		}
		backWardPawn = Long.numberOfTrailingZeros(enemyPawns);
		if ((backWardPawn & myAttacks) != 0){
			value += 2;
		}
		long safeSquares = (~enemyAttacks | myAttacks);
		if (myColor == Holder.WHITE) {
			value -= Long.bitCount(gameBoard.blockers & (myPawns >>> 8));
			value += Long.bitCount(gameBoard.blockers & (enemyPawns << 8));
			// bonus for White pawn advances on safeSquares
			if (((myPawns >>> 8 ) & safeSquares) != 0) {
				value += Long.bitCount((myPawns >>> 8 ) & safeSquares);
			}
			if ((myPawns & Holder.RANK_2) != 0){
				if ((((myPawns & Holder.RANK_2) >>> 16) & safeSquares) != 0) {
					value += Long.bitCount((((myPawns & Holder.RANK_2) >>> 16) & safeSquares));
				}
			}
		} else {
			value -= Long.bitCount(gameBoard.blockers & (myPawns << 8));
			value += Long.bitCount(gameBoard.blockers & (enemyPawns >>> 8));
			// bonus for Black pawn advances on safeSquares
			if (((myPawns << 8 ) & safeSquares) != 0) {
				value += Long.bitCount((myPawns >>> 8 ) & safeSquares);
			}
			if ((myPawns & Holder.RANK_7) != 0) {
				if ((((myPawns & Holder.RANK_7) << 16) & safeSquares) != 0){
					value += Long.bitCount((((myPawns & Holder.RANK_7) << 16) & safeSquares));
				}
			}
		}
		long temp = (myAttacks & enemyAllPieces);
		while (temp != 0) {
			int enemyPieceSquare = Long.numberOfTrailingZeros(temp);
			value += PiecesTables.getIndividValue(enemyPieces.get(enemyPieceSquare).getPiece());
			temp ^= (0b1L << enemyPieceSquare);
		}
		temp = (enemyAttacks & myAllPieces);
		while (temp != 0) {
			int myPieceSquare = Long.numberOfTrailingZeros(temp);
			value -= PiecesTables.getIndividValue(myPieces.get(myPieceSquare).getPiece());
			temp ^= (0b1L << myPieceSquare);
		}
		value += 3*Long.bitCount((myAttacks & enemyAllPieces) & safeSquares); // bonus for attack on safe squares
		value -= 3*Long.bitCount((enemyAttacks & myAllPieces) & ~safeSquares); // minus for enemyAttacks on notSafeSquares
		// END OF PAWNS
		// START OF OUTPOST/S ( https://en.wikipedia.org/wiki/Outpost_(chess) )
		enemyPieces.forEach((square, spot)->{ // enemyPawn attacks
			if (spot.getPiece() == Holder.PAWN){
				enemyPawnAttacks |= spot.getAttacks();
			}
		});
		if (myColor == Holder.WHITE){
			for (int i = 3; i < 6; i++) { // 4th rank to 7th (from white perspective)
				long outpostRank = myPawnAttacks & Holder.RANKS[i];
				if (outpostRank != 0) {
					long safeFromEnemyPawns = outpostRank ^ (outpostRank & enemyPawnAttacks);
					if (safeFromEnemyPawns != 0){
						if ((safeFromEnemyPawns & myKnights) != 0) {
							value += 2*Long.bitCount((safeFromEnemyPawns & myKnights));
						} else {
							value -= 2;
						}
						if ((safeFromEnemyPawns & myBishops) != 0) {
							value += 2*Long.bitCount((safeFromEnemyPawns & myBishops));
						} else {
							value -= 2;
						}
					}
				}
			}
		} else {
			for (int i = 1; i < 4; i++) { // 2th rank to 4th (from black perspective)
				long outpostRank = myPawnAttacks & Holder.RANKS[i];
				if (outpostRank != 0) {
					long safeFromEnemyPawns = outpostRank ^ (outpostRank & enemyPawnAttacks);
					if (safeFromEnemyPawns != 0) {
						if ((safeFromEnemyPawns & myKnights) != 0) {
							value += 2*Long.bitCount((safeFromEnemyPawns & myKnights));
						} else {
							value -= 2;
						}
						if ((safeFromEnemyPawns & myBishops) != 0) {
							value += 2*Long.bitCount((safeFromEnemyPawns & myBishops));
						} else {
							value -= 2;
						}
					}
				}
			}

		}
		// END OF OUTPOST/S
		// START OF QUEEN ( bonus for threats on the next moves against enemy queen)
		if ((myQueen & enemyAttacks) == 0 && myQueen != 0) {
			value += 15;
		} else if (myQueen == 0) {
			value -= 50;
		}
		// pressure enemy Queen
		if ((enemyQueen & myAttacks) != 0) {
			value += 15;
		} else if (enemyQueen == 0) {
			value += 50;
		}
		// START OF ROOKS (rook defending pawn/s)
		while (myRooks != 0) { 
			int rookPos = Long.numberOfTrailingZeros(myRooks);
			myRooks ^= (0b1L << rookPos);
			long rookAttacks  = myPieces.get(rookPos).getAttacks();
			long defending = ( rookAttacks & myPawns);
			if (defending != 0) {
				value += 2*Long.bitCount(defending);
			} else {
				value -= 2;
			}
			// defending other pieces
			long otherPiecesDefens = (rookAttacks & (myAllPieces ^ myPawns));
			if (otherPiecesDefens != 0) {
				value += 3;
			} else {
				value -= 3;
			}
			if ((rookAttacks & enemyAllPieces) != 0){
				value -= 2;
			}
		}
		// END OF ROOKS
		// START OF BISHOPS (bonus for bishop/s shielded by pawn/s, two bishops advantage)
		while (myBishops != 0) {
			int bishopSquare = Long.numberOfTrailingZeros(myBishops);
			if ((myPawns & gameBoard.getRays().getPawn(bishopSquare, enemyColor)) != 0 ){
				value += 2;
			} else {
				value -= 2;
			}
			myBishops ^= (0b1L << bishopSquare);
		}
		if (gameBoard.getPiecesCount(myColor, Holder.BISHOP) >= 2) {
			value += 5;
			if (gameBoard.getPiecesCount(enemyColor, Holder.BISHOP) < 2) {
				value += 5;
			} 
		} else if (gameBoard.getPiecesCount(enemyColor, Holder.BISHOP) >= 2) {
			value -= 10;
		}
		// END OF BISHOPS
		// START OF KNIGHTS (bonus for knight/s shielded by pawn/s)
		while (myKnights != 0) {
			int knightSquare = Long.numberOfTrailingZeros(myKnights);
			if ((myPawns & gameBoard.getRays().getPawn(knightSquare, enemyColor)) != 0 ){
				value += 2;
			} else {
				value -= 1;
			}
			myKnights ^= (0b1L << knightSquare);
		}
		// END OF KNIGHTS
		// START OF KING (Penalty when our king is on a pawnless flank)
		int kingSquare = gameBoard.getKingsPos(myColor);
		int kingX = kingSquare%8;
		long kingFile = Holder.FILES[kingX];
		if (kingX > 0) { // king protected on left file
			if ((Holder.FILES[kingX-1] & myAllPieces) != 0){
				value += 2;
			} else {
				value -= 2;
			}
		}
		if (kingX < 7) { // king protected on right file
			if ((Holder.FILES[kingX+1] & myAllPieces) != 0){
				value += 2;
			} else {
				value -= 2;
			}
		}
		for (int dir : Holder.DIAGONAL) {
			if ((gameBoard.getRays().getRay(dir, kingSquare) & myAllPieces) != 0){
				value += 1;
			} else {
				value -= 1;
			}
		}
		long pawnKingFile  = (kingFile & myPawns);
		if (pawnKingFile != 0){
			value += 2; // minor boost to score, its not necessary for king to have many pawns around
		} else {
			value -= 1;
		}
		// Penalty if king flank is under attack, potentially moving toward the king
		if ((enemyAttacks & kingFile) != 0) {
			value -= 8;
		}
		// king mobility bonus
		value += 2*Long.bitCount(myPieces.get(kingSquare).getMoves()); 
		value += 3*Long.bitCount((myPieces.get(kingSquare).getMoves() & enemyAllPieces));
		return value;
	}
	
	
	private void modUndoMove(int toSquare, int fromSquare, int piece, int color, boolean promotion){
		gameBoard.removePiece(color, fromSquare);
		if (promotion) {
			piece = Holder.PAWN;
		}
		gameBoard.addPiece(piece, color, toSquare, false);
	}

	private int miniMax(int depth, Integer alpha, Integer beta, boolean maximizingPlayer) {
		if (depth == 0) {
			return evaluate();//color
		}
		HashMap<Integer, Spot> moveList = new HashMap<Integer, Spot>();
		if (maximizingPlayer) {
			if (myColor == Holder.WHITE){
				gameBoard.updateWhite();
				gameBoard.getWhitePieces().forEach((square, spot)->moveList.put(square, new Spot(spot.getPiece(), Holder.WHITE, false)));
				gameBoard.getWhitePieces().forEach((square, spot)->moveList.get(square).setMoves(spot.getMoves()));
			} else {
				gameBoard.updateBlack();
				gameBoard.getBlackPieces().forEach((square, spot)->moveList.put(square, new Spot(spot.getPiece(), Holder.BLACK, false)));
				gameBoard.getBlackPieces().forEach((square, spot)->moveList.get(square).setMoves(spot.getMoves()));
			}
		} else {
			if (enemyColor == Holder.WHITE){
				gameBoard.updateWhite();
				gameBoard.getWhitePieces().forEach((square, spot)->moveList.put(square, new Spot(spot.getPiece(), Holder.WHITE, false)));
				gameBoard.getWhitePieces().forEach((square, spot)->moveList.get(square).setMoves(spot.getMoves()));
			} else {
				gameBoard.updateBlack();
				gameBoard.getBlackPieces().forEach((square, spot)->moveList.put(square, new Spot(spot.getPiece(), Holder.BLACK, false)));
				gameBoard.getBlackPieces().forEach((square, spot)->moveList.get(square).setMoves(spot.getMoves()));
			}
		}
		//# maximizing player
		boolean promotion = false;
		if (maximizingPlayer) {
			Integer maxEval = Integer.MIN_VALUE; 
			for (Integer square : moveList.keySet()) {
				Spot spot = moveList.get(square);
				int piece = spot.getPiece();
				long move = spot.getMoves();
				int result;
				while (move != 0) {
					result = Long.numberOfTrailingZeros(move);
					move ^= (0b1L << result);
					if (enpassant){
						gameBoard.clearEnpassant(myColor);
					}
					if (piece == Holder.PAWN && (result < 8 || result > 55)) {
						piece = Holder.QUEEN;
						promotion = true;
					}
					int capture = gameBoard.makeMove(piece, result, square, myColor);
					int capturedSquare = gameBoard.getRemovedPieceSquare();
					int score = miniMax(depth-1, alpha, beta, false);
					if (depth == MAX_DEPTH) {
						if (myMoves.containsKey(score)) {
							// if its same score as previous move, if this is capture, replace, else random chance
							if (((0b1L << result) & gameBoard.getAllPieces(enemyColor)) != 0){
								myMoves.put(score, new SimpleEntry<>(square, result));
							} else if (RAND.nextBoolean()) {
								myMoves.put(score, new SimpleEntry<>(square, result));
							}
						} else {
							myMoves.put(score, new SimpleEntry<>(square, result));
						}
					}
					modUndoMove(square, result, piece, myColor, promotion);
					promotion = false;
					if (capture != -1) {
						gameBoard.addPiece(capture, enemyColor, capturedSquare, false);
					}
					if (score > maxEval){
						maxEval = score;
					}
					if (score > alpha) {
						alpha = score;
					}	
					if (beta <= alpha){
						break;
					}
				}
			}
			return maxEval;
		//# minimzing player
		}else {
			Integer minEval = Integer.MAX_VALUE; 
			for (Integer square : moveList.keySet()) {
				Spot spot = moveList.get(square);
				int piece = spot.getPiece();
				long move = spot.getMoves();
				int result;
				while (move != 0) {
					result = Long.numberOfTrailingZeros(move);
					move ^= (0b1L << result);
					if (enpassant){
						gameBoard.clearEnpassant(enemyColor);
					}
					if (piece == Holder.PAWN && (result < 8 || result > 55)) {
						piece = Holder.QUEEN;
						promotion = true;
					}
					int capture = gameBoard.makeMove(piece, result, square, enemyColor);
					int capturedSquare = gameBoard.getRemovedPieceSquare();
					int score = miniMax(depth-1, alpha, beta, true);
					modUndoMove(square, result, piece, enemyColor, promotion);
					promotion = false;
					if (capture != -1) {
						gameBoard.addPiece(capture, myColor, capturedSquare, false);
					}
					if (score < minEval){
						minEval = score;
					}
					if (score < beta){
						beta = score;
					}
					if (beta <= alpha) {
						break;
					}
					
				}
			}
			return minEval;
		}
	}
}
