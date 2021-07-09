package assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import board.constants.Colors;
import board.constants.Pieces;

public class Assets {
    // square dimensions
    public final int PIECE_WIDTH = 80; // == SQUARE_WIDTH
    public final int PIECE_HEIGHT = 60; // == SQUARE_HEGIHT
    
    private BufferedImage board;
    //public BufferedImage button_start, button_quit;
    //public BufferedImage back_button;
    private BufferedImage marker;
    private BufferedImage perft_button;
    private HashMap<Integer, HashMap<Integer, BufferedImage>> piecesImg;
    private final BufferedImage[] leftArrows = new BufferedImage[2];
    private final BufferedImage[] rightArrows = new BufferedImage[2];

    public Assets(int width, int height) {
        init();
    }
    
    private void init() {
        String absPath = new File("").getAbsolutePath();
        
        // Load images of pieces.
        BufferedImage piecesSheet = loadImage(absPath + "\\chess\\images\\sheet.png");
        piecesImg = new HashMap<Integer, HashMap<Integer, BufferedImage>>();
        for (int color = Colors.WHITE; color < Colors.COLOR_COUNT; color++) {
            HashMap<Integer, BufferedImage> tmp = new HashMap<Integer, BufferedImage>();
            for (int piece = Pieces.KING; piece < Pieces.PIECE_COUNT; piece++) {
                BufferedImage tmpImg = piecesSheet.getSubimage(PIECE_WIDTH*piece,
                    (color == Colors.WHITE) ? 0:PIECE_HEIGHT, PIECE_WIDTH, PIECE_HEIGHT);
                tmp.put(piece, tmpImg);
            }
            piecesImg.put(color, tmp);
        }
    
        board = loadImage(absPath + "\\chess\\images\\chess_board_640_480.png");
        perft_button = loadImage(absPath + "\\chess\\images\\menuImages\\Perft_button.png");
        
        marker = loadImage(absPath + "\\chess\\images\\dot.png");
        leftArrows[0]  = loadImage(absPath + "\\chess\\images\\leftArrow.png");
        leftArrows[1]  = loadImage(absPath + "\\chess\\images\\leftArrow_hover.png");
        rightArrows[0] = loadImage(absPath + "\\chess\\images\\rightArrow.png");
        rightArrows[1] = loadImage(absPath + "\\chess\\images\\rightArrow_hover.png");
        /*
        //BUTTONS
        button_start = loadImage(absPath + "\\chess\\img\\menuImages\\startButton.png");
        button_quit = loadImage(absPath + "\\chess\\img\\menuImages\\quitButton.png");
        back_button = loadImage(absPath + "\\chess\\img\\menuImages\\back_button.png");
        arrows[0] =  loadImage(absPath + "\\chess\\img\\left_arrow.png");
        arrows[1] =  loadImage(absPath + "\\chess\\img\\right_arrow.png");
        BOARD_WIDTH = board.getWidth();
        BOARD_HEIGHT = board.getHeight();
        */
        System.out.println("Images loaded successfully");
    }

    /**
     * 
     * @param path to file
     * @return Buffered image or null if failed to load
     */
    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();// add pop-up -> fatal error
            System.exit(1);
        }
        return null;
    }

    // --------- Getters --------- 

    public BufferedImage getPerft_button() {
        return perft_button;
    }

    /**
     * 
     * @param piece chess piece
     * @param color color of chess piece
     * @return BufferedImage of given piece of given color
     */
    public BufferedImage getPieceImg(Integer piece, int color) {
        return piecesImg.get(color).get(piece);
    }
    
    /**
     * 
     * @return chess board
     */
    public BufferedImage getBoard() {
        return board;
    }

    /**
     * 
     * @return image of red dot
     */
    public BufferedImage getMarker() {
        return marker;
    }

    /**
     * @param hover type of image, or idle -> (0/1)
     * @return Image of arrow pointing left
     */
    public BufferedImage getLeftArrows(int hover) {
        return leftArrows[hover];
    }

    /**
     * @param hover type of image, or idle -> (0/1)
     * @return Image of arrow pointing right
     */
    public BufferedImage getRightArrows(int hover) {
        return rightArrows[hover];
    }

    /**
     * 
     * @return chess boards width
     */
    public int getBoardWidth() {
        return board.getWidth();
    }

    /**
     * 
     * @return chess boards height
     */
    public int getBoardHeight() {
        return board.getHeight();
    }

    /**
     * 
     * @param x coord of mouse
     * @return makes window into squares (PIECE_WIDTH x PIECE_WIDTH)
     * and returns x coord of such a square, where mouse is.
     */
    public int centerMouseX(int x) {
        return ((x+PIECE_WIDTH)/(PIECE_WIDTH+1))-1;
    }

    /**
     * 
     * @param y coord of mouse
     * @return makes window into squares (PIECE_HEIGHT x PIECE_HEIGHT)
     * and returns y coord of such a square, where mouse is.
     */
    public int centerMouseY(int y) {
        return ((y+PIECE_HEIGHT)/(PIECE_HEIGHT+1))-1;
    }

    /**
     * 
     * @param x transformed using centerMouseX
     * @param y transformed using centerMouseY
     * @return index of chess board square, -1 if out of range.
     */
    public int getBoardSquare(int x, int y) {
        if (x < 8 && y < 8) {
            return 8*y+x;
        }
        return -1;
    }
    
}

