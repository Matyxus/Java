package assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import board.constants.Colors;
import board.constants.Pieces;

public class Assets {
    // square dimensions
    public final int PIECE_WIDTH = 80; // == SQUARE_WIDTH
    public final int PIECE_HEIGHT = 60; // == SQUARE_HEGIHT
    public final int BOARD_SQUARES = 64;
    private final String absPath = new File("").getAbsolutePath();
  

    private BufferedImage board;
    //public BufferedImage button_start, button_quit;
    //public BufferedImage back_button;
    //public BufferedImage marker;
    //public BufferedImage perft_button;
    //public BufferedImage[] pawn, rook, king, queen, tower, knight, bishop;
    private BufferedImage[][] pieces;
    //public BufferedImage[] arrows; //0-left, 1-right

    private BufferedImage sheet;
    

    public Assets(int width, int height) {
        System.out.println("Abs path: " + absPath);
        init();
    }
    
    private void init() {
        sheet = loadImage(absPath + "\\chess\\images\\sheet.png");
        pieces = new BufferedImage[6][2];
        int counter = 0;
        for (BufferedImage[] bufferedImage : pieces) {
            bufferedImage[Colors.WHITE.ordinal()] = crop(PIECE_WIDTH*counter, 0, PIECE_WIDTH, PIECE_HEIGHT);
            bufferedImage[Colors.BLACK.ordinal()] = crop(PIECE_WIDTH*counter, PIECE_HEIGHT, PIECE_WIDTH, PIECE_HEIGHT);
            counter++;
        }
        board = loadImage(absPath + "\\chess\\images\\chess_board_640_480.png");
        /*
        marker = loadImage(absPath + "\\chess\\img\\dot.png");
        //BUTTONS
        perft_button = loadImage(absPath + "\\chess\\img\\menuImages\\Perft_button.png");
        button_start = loadImage(absPath + "\\chess\\img\\menuImages\\startButton.png");
        button_quit = loadImage(absPath + "\\chess\\img\\menuImages\\quitButton.png");
        back_button = loadImage(absPath + "\\chess\\img\\menuImages\\back_button.png");
        arrows[0] =  loadImage(absPath + "\\chess\\img\\left_arrow.png");
        arrows[1] =  loadImage(absPath + "\\chess\\img\\right_arrow.png");
        BOARD_WIDTH = board.getWidth();
        BOARD_HEIGHT = board.getHeight();
        */
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();// add pop-up -> fatal error
            System.exit(1);
        }
        return null;
    }

    private BufferedImage crop(int x, int y, int width, int height) {
        return sheet.getSubimage(x, y, width, height);
    }

    // --------- Getters --------- 

    public BufferedImage getPieceImg(Pieces piece, Colors color) {
        return pieces[piece.ordinal()][color.ordinal()];
    }

    public BufferedImage getBoard() {
        return board;
    }
    
}

