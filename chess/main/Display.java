package main;
// Swing
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.DefaultCaret;
import javax.swing.text.BadLocationException;
// Awt
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

public class Display {

    private JFrame frame;
    private Canvas canvas;
    // textArea for printing piece moves
    private JTextArea textArea;
    private final String title;
    private final int width, height;
    private final Handler handler;

    public Display(String title, int width, int height, Handler handler) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.handler = handler;
        crateDisplay();
    }

    private void crateDisplay() {
        // -------- Frame --------
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        // -------- Load and Save buttons --------
        JMenuBar menuBar = createJMenuBar();
        frame.add(menuBar);
        frame.setJMenuBar(menuBar);
        // -------- Text area --------
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(textArea.getFont().deriveFont(30.0f));
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(
            handler.getAssets().getBoardWidth()+160,
            0,
            width - (handler.getAssets().getBoardWidth()+160),
            handler.getAssets().getBoardHeight()
        );
        frame.add(scrollPane);
        // -------- Canvas --------
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        // -------- Mouse --------
        addMouse();
        // -------- Finishing frame --------
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @return JMenuBar with Save and Load buttons
     */
    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        // -------- Save button -------- 
        JMenu save = new JMenu("Save"); 
        save.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println("Clicked on save");
                    if (handler.getGameBoard() != null) {
                        handler.saveGame(textArea.getText());
                    }
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
                    handler.loadGame();
                }
            }
        });
        menuBar.add(save);
        menuBar.add(load);
        return menuBar;
    }

    /**
     *  Adds mouse listener to frame, canvas and textArea
     */
    private void addMouse() {
        textArea.addMouseListener(handler.getMouseManager());
        textArea.addMouseMotionListener(handler.getMouseManager());
        frame.addMouseListener(handler.getMouseManager());
        frame.addMouseMotionListener(handler.getMouseManager());
        canvas.addMouseListener(handler.getMouseManager());
        canvas.addMouseMotionListener(handler.getMouseManager());
    }

    /**
     * @param text to be appended
     */
    public void appendText(String text) {
        textArea.append(text);
    }

    /**
     * @param text to be set, empty or null deletes current text
     */
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * @return removed last line from textArea, null if there is none
     */
    public String removeLastLine() {
        String removed = null;
        // Check if textArea is not empty
        if (textArea.getDocument().getLength() > 0) {
            try {
                String content = textArea.getDocument().getText(0, textArea.getDocument().getLength());
                int lastLineBreak = content.lastIndexOf('\n', textArea.getDocument().getLength()-2);
                // Removing last line
                if (lastLineBreak == -1) {
                    lastLineBreak = 0;
                }
                removed = textArea.getDocument().getText(lastLineBreak, textArea.getDocument().getLength() - lastLineBreak);
                textArea.getDocument().remove(lastLineBreak, textArea.getDocument().getLength() - lastLineBreak);
            } catch (BadLocationException e) {
                System.out.println(e); 
            }
        }
        return removed;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }
}
