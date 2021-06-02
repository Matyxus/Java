package chess2.main;
import chess2.states.State;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.BorderLayout;

public class Display {

    private JFrame frame;
    private Canvas canvas;

    private final String title;
    private final int width, height;

    private JTextArea textArea;
    private JPanel textPanel;
    private JScrollPane scrollPane;
    private JLabel clocks;

    public Display(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        crateDisplay();
    }

    private void crateDisplay() {
        frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu save = new JMenu("Save"); // save button
        save.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (State.getState() != null){
                        State.getState().saveGame();
                    }
                }
            }
        });
        menuBar.add(save);
        JMenu load = new JMenu("Load"); // load button
        load.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (State.getState() != null) {
                        State.getState().loadGame();
                    }
                }
            }
        });

        menuBar.add(load);
        frame.setJMenuBar(menuBar);
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        // textfield (width = frame.width+1 to avoid border of textArea)
        textPanel = new JPanel();
        textPanel.setBounds(640, 0, 161, 300);
        textArea = new JTextArea();
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        textArea.setEditable(false);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane = new JScrollPane(textArea);
        textPanel.setLayout(new BorderLayout());
        textPanel.add(scrollPane);
        frame.add(textPanel);
        setTextVisibility(false); // initial setting false -> going to board Placement first
        clockAdder();
        frame.add(canvas);
        frame.pack();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setTextVisibility(boolean textVisibility) {
        this.textPanel.setVisible(textVisibility);
    }

    public void setTimeVisibility(boolean timeVisibility) {
        this.clocks.setVisible(timeVisibility);
    }

    public void appendText(String text) {
        this.textArea.append(text);
    }

    public String removeLast() {
        String content;
        String removed = null;
        try {
            content = textArea.getDocument().getText(0, textArea.getDocument().getLength());
            int lastLineBreak = content.lastIndexOf('\n');
            removed =  textArea.getDocument().getText(lastLineBreak, textArea.getDocument().getLength() - lastLineBreak);
            textArea.getDocument().remove(lastLineBreak, textArea.getDocument().getLength() - lastLineBreak);
        } catch (BadLocationException e) {
            //bad locationException doesnt need to be handled -> it means, there is nothing to remove
        }
        return removed;
    }

    public String getText(){
        try {
            return this.textArea.getText();
        } catch (Exception e) {
            return null;
        }
    }

    public void cleanText(){
        this.textArea.setText("");
    }
    
    private void clockAdder(){
        clocks = new JLabel();
        clocks.setBounds(640, 420, 160, 60);
        clocks.setFont(new Font("Serif", Font.PLAIN, 42));
        clocks.setVisible(false);
        frame.getContentPane().add(clocks);
    }

    public void appendTime(String time){
        this.clocks.setText(time);
    }
}
