package chess2.images;

import java.awt.image.BufferedImage;
import java.io.File;

public class Assets {
    // square dimensions
    public static final int PIECE_WIDTH = 80; // == SQUARE_WIDTH
    public static final int PIECE_HEIGHT = 60; // == SQUARE_HEGIHT
    public static final int BOARD_SQUARES = 64;
    public static int BOARD_WIDTH;
    public static int BOARD_HEIGHT;

    public static BufferedImage board;
    public static BufferedImage button_start, button_quit;
    public static BufferedImage menu_backround;
    public static BufferedImage back_button;
    public static BufferedImage marker;
    public static BufferedImage perft_button;
    public static BufferedImage[] pawn, rook, king, queen, tower, knight, bishop;
    public static BufferedImage[][] pieces;
    public static BufferedImage[] arrows; //0-left, 1-right
    
    public static void init() {
        String absPath = new File("").getAbsolutePath();
        SpriteSheet sheet = new SpriteSheet(ImageLoader.loadImage(absPath + "\\chess2\\img\\sheet.png"));
        arrows = new BufferedImage[2];
        pawn = new BufferedImage[2];
        rook = new BufferedImage[2];
        king = new BufferedImage[2];
        queen = new BufferedImage[2];
        tower = new BufferedImage[2];
        knight = new BufferedImage[2];
        bishop = new BufferedImage[2];
        pieces = new BufferedImage[6][0];
        
        pieces[0] = king;
        pieces[1] = queen;
        pieces[2] = rook;
        pieces[3] = knight;
        pieces[4] = bishop;  
        pieces[5] = pawn;
        int counter = 0;
        for (BufferedImage[] bufferedImage : pieces) {
            bufferedImage[0] = sheet.crop(PIECE_WIDTH*counter, 0, PIECE_WIDTH, PIECE_HEIGHT);
            bufferedImage[1] = sheet.crop(PIECE_WIDTH*counter, PIECE_HEIGHT, PIECE_WIDTH, PIECE_HEIGHT);
            counter++;
        }
        board = ImageLoader.loadImage(absPath + "\\chess2\\img\\chess_board_640_480.png");
        marker = ImageLoader.loadImage(absPath + "\\chess2\\img\\dot.png");
        //BUTTONS
        perft_button = ImageLoader.loadImage(absPath + "\\chess2\\img\\menuImages\\Perft_button.png");
        button_start = ImageLoader.loadImage(absPath + "\\chess2\\img\\menuImages\\startButton.png");
        button_quit = ImageLoader.loadImage(absPath + "\\chess2\\img\\menuImages\\quitButton.png");
        back_button = ImageLoader.loadImage(absPath + "\\chess2\\img\\menuImages\\back_button.png");
        arrows[0] =  ImageLoader.loadImage(absPath + "\\chess2\\img\\left_arrow.png");
        arrows[1] =  ImageLoader.loadImage(absPath + "\\chess2\\img\\right_arrow.png");
        BOARD_WIDTH = board.getWidth();
        BOARD_HEIGHT = board.getHeight();
    }
    
}

