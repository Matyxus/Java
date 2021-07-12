package ui;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

public class PopUP {

    public static void errorPopUP(Exception e) {
        JOptionPane.showMessageDialog(null, e);
    }

    public static void messagePopUP(String text) { 
        JOptionPane.showMessageDialog(null, text, "Message", JOptionPane.PLAIN_MESSAGE);
    }

    public static void trial() {
        JCheckBox ai = new JCheckBox("Play against computer?");
        JCheckBox startingColor = new JCheckBox("White plays first?");
        JCheckBox playAsColor = new JCheckBox("Play as White?");
        Object[] params = {ai, startingColor, playAsColor};
        JOptionPane.showConfirmDialog(null, params, "Fen", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

}