package gui;
// Swing
import javax.swing.JFrame;
import managers.MouseManager;
// Awt
import java.awt.Canvas;
import java.awt.Dimension;

/**
 * Class handling creation of window
 */
public class Display {

    private JFrame frame;
    private Canvas canvas;
    private final String title;
    public final int width, height;

    public Display(String title, int width, int height, MouseManager mouseManager) {
        this.title = title;
        this.width = width;
        this.height = height;
        crateDisplay(mouseManager);
    }

    private void crateDisplay(MouseManager mouseManager) {
        // -------- Frame --------
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        // -------- Canvas --------
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        // -------- Mouse --------
        frame.addMouseListener(mouseManager);
        frame.addMouseMotionListener(mouseManager);
        canvas.addMouseListener(mouseManager);
        canvas.addMouseMotionListener(mouseManager);
        // -------- Finishing frame --------
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    // ------------------------------------ Utils ------------------------------------

    // ------------------------------------ Getters ------------------------------------

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}
