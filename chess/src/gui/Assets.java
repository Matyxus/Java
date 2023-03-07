package gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import gameboard.constants.Colors;
import gameboard.constants.Pieces;
import utils.ImageConst;
import file_system.FilePath;

public class Assets {
    // Same as dimension of square on chess board
    public final int PIECE_WIDTH  = 80; // == SQUARE_WIDTH
    public final int PIECE_HEIGHT = 60; // == SQUARE_HEGIHT
    
    private final BufferedImage board;
    private final BufferedImage marker;
    private final BufferedImage perft_button;
    private final HashMap<Integer, HashMap<Integer, BufferedImage>> piecesImg;
    private final BufferedImage[] startButtons  = new BufferedImage[ImageConst.IMG_STATES];
    private final BufferedImage[] replayButtons = new BufferedImage[ImageConst.IMG_STATES];
    private final BufferedImage[] backButtons   = new BufferedImage[ImageConst.IMG_STATES];
    private final BufferedImage[] leftArrows    = new BufferedImage[ImageConst.IMG_STATES];
    private final BufferedImage[] rightArrows   = new BufferedImage[ImageConst.IMG_STATES];
    

    public Assets() {
        // Load images of pieces.
        BufferedImage piecesSheet = loadImage(FilePath.ASSETS + "\\board\\sheet.png");
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
    
        board = loadImage(FilePath.ASSETS + "\\board\\chess_board_640_480.png");
        perft_button = loadImage(FilePath.ASSETS+ "\\buttons\\Perft_button.png");
        
        marker = loadImage(FilePath.ASSETS + "\\board\\dot.png");
        // Left arrow
        leftArrows[ImageConst.IMG_UP]  = loadImage(FilePath.ASSETS + "\\arrows\\leftArrow.png");
        leftArrows[ImageConst.IMG_DOWN]  = loadImage(FilePath.ASSETS + "\\arrows\\leftArrow_hover.png");
        // Right arrow
        rightArrows[ImageConst.IMG_UP] = loadImage(FilePath.ASSETS + "\\arrows\\rightArrow.png");
        rightArrows[ImageConst.IMG_DOWN] = loadImage(FilePath.ASSETS + "\\arrows\\rightArrow_hover.png");
        // Start buttons
        startButtons[ImageConst.IMG_UP] = loadImage(FilePath.ASSETS + "\\buttons\\startButton.png");
        startButtons[ImageConst.IMG_DOWN] = loadImage(FilePath.ASSETS + "\\buttons\\startButton_hover.png");
        // Replay Buttons
        replayButtons[ImageConst.IMG_UP] = loadImage(FilePath.ASSETS + "\\buttons\\replayButton.png");
        replayButtons[ImageConst.IMG_DOWN] = loadImage(FilePath.ASSETS + "\\buttons\\replayButton_hover.png");
        // Back Buttons
        backButtons[ImageConst.IMG_UP] = loadImage(FilePath.ASSETS + "\\buttons\\backButton.png");
        backButtons[ImageConst.IMG_DOWN] = loadImage(FilePath.ASSETS + "\\buttons\\backButton_hover.png");
        /*
        //BUTTONS
        button_start = loadImage(absPath + "\\chess\\img\\menuImages\\startButton.png");
        button_quit = loadImage(absPath + "\\chess\\img\\menuImages\\quitButton.png");
        back_button = loadImage(absPath + "\\chess\\img\\menuImages\\back_button.png");
        BOARD_WIDTH = board.getWidth();
        BOARD_HEIGHT = board.getHeight();
        */
        System.out.println("Images loaded successfully");
    }
    
    /**
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
     * @param piece chess piece
     * @param color color of chess piece
     * @return BufferedImage of given piece of given color
     */
    public BufferedImage getPieceImg(int piece, int color) {
        return piecesImg.get(color).get(piece);
    }
    
    /**
     * @return chess board
     */
    public BufferedImage getBoard() {
        return board;
    }

    /**
     * @return image of red dot
     */
    public BufferedImage getMarker() {
        return marker;
    }

    /**
     * @param hover type of image (Up/Down)
     * @return Image of back button
     */
    public BufferedImage getBackButton(int hover) {
        return backButtons[hover];
    }


    /**
     * @param hover type of image (Up/Down)
     * @return Image of start button
     */
    public BufferedImage getStartButton(int hover) {
        return startButtons[hover];
    }

    /**
     * @param hover type of image (Up/Down)
     * @return Image of replay button
     */
    public BufferedImage getReplayButton(int hover) {
        return replayButtons[hover];
    }

    /**
     * @param hover type of image (Up/Down)
     * @return Image of arrow pointing left
     */
    public BufferedImage getLeftArrows(int hover) {
        return leftArrows[hover];
    }

    /**
     * @param hover type of image (Up/Down)
     * @return Image of arrow pointing right
     */
    public BufferedImage getRightArrows(int hover) {
        return rightArrows[hover];
    }

    /**
     * @return chess boards width
     */
    public int getBoardWidth() {
        return board.getWidth();
    }

    /**
     * @return chess boards height
     */
    public int getBoardHeight() {
        return board.getHeight();
    }

    /**
     * @param x coord of mouse
     * @return makes window into squares (PIECE_WIDTH x PIECE_WIDTH)
     * and returns x coord of such a square, where mouse is.
     */
    public int centerMouseX(int x) {
        return ((x+PIECE_WIDTH)/(PIECE_WIDTH+1))-1;
    }

    /**
     * @param y coord of mouse
     * @return makes window into squares (PIECE_HEIGHT x PIECE_HEIGHT)
     * and returns y coord of such a square, where mouse is.
     */
    public int centerMouseY(int y) {
        return ((y+PIECE_HEIGHT)/(PIECE_HEIGHT+1))-1;
    }

    /**
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

