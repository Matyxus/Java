package main;
// Swing.
import javax.swing.JFrame;
//import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
//import javax.swing.JPanel;
//import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;
//import javax.swing.text.BadLocationException;
// Awt.
import java.awt.Canvas;
import java.awt.Dimension;
//import java.awt.Font;
import java.awt.event.MouseEvent;

public class Display {

    private JFrame frame;
    private Canvas canvas;

    private final String title;
    private final int width, height;
    private final Handler handler;

    public Display(String title, int width, int height, Handler handler){
        this.title = title;
        this.width = width;
        this.height = height;
        this.handler = handler;
        crateDisplay();
    }

    private void crateDisplay() {
        // Frame
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        // Load and Save buttons.
        JMenuBar menuBar = createJMenuBar();
        frame.add(menuBar);
        frame.setJMenuBar(menuBar);
        // Canvas.
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        // Mouse.
        addMouseListener();
        // Finishing frame.
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // -------- Save button -------- 
        JMenu save = new JMenu("Save"); 
        save.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println("Clicked on save");
                    /*
                    if (handler.getGameBoard() != null) {
                        handler.getGameBoard().saveGame();
                    }
                    */
                }
            }
        });
        //  -------- Load button -------- 
        JMenu load = new JMenu("Load");
        load.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println("Clicked on load");
                    /*
                    if (handler.getGameBoard() != null) {
                        handler.getGameBoard().loadGame();
                    }
                    */
                }
            }
        });
        menuBar.add(save);
        menuBar.add(load);
        return menuBar;
    }

    /** 
        Adds mouse listener to frame and canvas.
    */
    private void addMouseListener() {
        frame.addMouseListener(handler.getMouseManager());
        frame.addMouseMotionListener(handler.getMouseManager());
        canvas.addMouseListener(handler.getMouseManager());
        canvas.addMouseMotionListener(handler.getMouseManager());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}
