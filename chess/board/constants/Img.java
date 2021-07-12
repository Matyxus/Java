package board.constants;

import java.io.File;

public class Img {
    public static final String IMAGE_PATH = new File("").getAbsolutePath() + "\\chess\\images\\";
    public static final String SAVE_PATH  = new File("").getAbsolutePath() + "\\chess\\saves\\";
    /**
     * Idle image
     */ 
    public static final int IMG_UP    = 0;
    /**
     * Mouse hovers over image
     */
    public static final int IMG_DOWN  = 1;
    /**
     * Types of images
     */
    public static final int IMG_TYPES = 2;
    
}
