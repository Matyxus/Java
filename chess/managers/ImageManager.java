package managers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import board.constants.Colors;
import board.constants.Pieces;

public class ImageManager {
    // Chess board square dimensions
    private final int PIECE_WIDTH = 80;
    private final int PIECE_HEIGHT = 60;
    private final String absPath = new File("").getAbsolutePath();

    private BufferedImage board;
    //private BufferedImage button_start, button_quit;
    //private BufferedImage back_button;
    //private BufferedImage marker;
    //private BufferedImage perft_button;
    private BufferedImage[][] pieces;
    //private BufferedImage[] arrows; //0-left, 1-right

    private BufferedImage sheet;
    


    public ImageManager(int width, int height) {
        init();
    }
    
    /**
     * Loads images.
     */
    private void init() {
        sheet = loadImage(absPath + "\\chess\\img\\sheet.png");
        pieces = new BufferedImage[6][2];
        int counter = 0;
        for (BufferedImage[] bufferedImage : pieces) {
            bufferedImage[Colors.WHITE.ordinal()] = crop(PIECE_WIDTH*counter, 0, PIECE_WIDTH, PIECE_HEIGHT);
            bufferedImage[Colors.BLACK.ordinal()] = crop(PIECE_WIDTH*counter, PIECE_HEIGHT, PIECE_WIDTH, PIECE_HEIGHT);
            counter++;
        }
        board = loadImage(absPath + "\\chess\\img\\chess_board_640_480.png");
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

    /**
     * @param path of image
     * @return BufferedImage if succesfull, null otherwise.
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

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @return subimage of sheet.
     */
    private BufferedImage crop(int x, int y, int width, int height) {
        return sheet.getSubimage(x, y, width, height);
    }

    // ------ Getters ------ 

    /**
     * @param piece
     * @param color of piece
     * @return BufferedImage of given piece.
     */
    public BufferedImage getPieceImg(Pieces piece, Colors color) {
        return pieces[piece.ordinal()][color.ordinal()];
    }

    /**
     * @return The chess board.
     */
    public BufferedImage getBoard() {
        return board;
    }
}



