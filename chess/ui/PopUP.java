package ui;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import assets.Pair;

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

    /**
     * @return Pair<Depth, whiteStarts>, where depth is
     * how many moves forward perft explores, whiteStarts if true means, white
     * player starts, pair can be null if user canceled window
     */
    public static Pair<Integer, Boolean> perftSetupMessage() {
        // Let user choose depth (5 is optimal, since 6 could take too long)
        Integer[] depth = {1, 2, 3, 4, 5};
        JComboBox<Integer> jComboBox = new JComboBox<Integer>(depth);
        jComboBox.setEditable(false);
        // CheckBox asking is white should start
        JCheckBox playAsWhite = new JCheckBox("White starts?");
        Object[] params = {jComboBox, playAsWhite};
        int selected = JOptionPane.showConfirmDialog(
            null, params, "Select parameters", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        Pair<Integer, Boolean> tmp = null;
        // User can cancell window
        if (selected == JOptionPane.OK_OPTION) {
            tmp = new Pair<Integer, Boolean>((Integer)jComboBox.getSelectedItem(), playAsWhite.isSelected());
        }
        return tmp;
    }

}