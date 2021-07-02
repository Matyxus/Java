package ui;

import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class PopUP {

    public static void errorPopUP(Exception e) {
        JOptionPane.showMessageDialog(null, e);
    }

    public static void messagePopUP(String text) { 
        JOptionPane.showMessageDialog(null, text, "Message", JOptionPane.PLAIN_MESSAGE);
    }

    public static String Trial() {
        JTextField fenField = new JTextField();
        Object[] params = {fenField};
        JOptionPane.showConfirmDialog(null, params, "Fen", JOptionPane.OK_CANCEL_OPTION);
        System.out.println(fenField.getText());
        return fenField.getText();
    }

}