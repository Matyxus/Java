package chess2;
//Pop-Up windows
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
//Time format
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PopUps {
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
    public static boolean castling = false;
    public static boolean enpassant = false;
    public static boolean AI = false;
    public static long time;
    public static int startingPlayerColor = 0;// base is White player
    public static int aiCol = 1;
    public static long currPlayerTime;
    public static String fileName;
    public static boolean gameEnd;
    public static int perftDepth = 5;
    public static boolean perftHashing = false;
    public static int round = 0;

    public static void savePopUp(){
        Date currTime = new java.util.Date();
        fileName = (String) JOptionPane.showInputDialog(null, "Enter file name:", "Save",
                JOptionPane.PLAIN_MESSAGE, null, null, currTime);
        // Check if user entered something valid
        if (fileName == null || fileName.length() == 0) {
            fileName = null;
            return;
        }
        String tmp = "" + currTime;
        if (tmp.equals(fileName)) {// if filname is Date, replace with format
            fileName = TIME_FORMAT.format(currTime);
        } else { // If a string was returned, replace all special chars
            fileName = fileName.replaceAll("[^a-zA-Z0-9]+", "");
        }
    }

    private static String drawMessage(int result){
        switch (result) {
            case 1:
                return "Draw due to insufficient material";
            case 2:
                return "Draw due to stalemate";
            case 3:
                return "Draw due to fifty move rule";
            case 4:
                return "Draw due to threefold repetition";
            case 5:
                return "Draw due to fivefold repetition";
            default:
                return null;
        }
    }

    // draw due to (insufficient material, stalemate, fivefold repetition) wont allow the game to continue
    public static boolean drawPopUp(int result){
        String message = drawMessage(result);
        if (result != 1 && result != 2 && result != 5){
            int returendVal = JOptionPane.showConfirmDialog(null, message+", do you wish to continue ?", "Draw", JOptionPane.YES_NO_OPTION);
            return (returendVal == JOptionPane.YES_OPTION) ? true:false;
        }else {
            JOptionPane.showMessageDialog(null, message, "Draw", JOptionPane.PLAIN_MESSAGE);  
        }
        return false;
    }

    public static void errorPopUP(Exception e){
        JOptionPane.showMessageDialog(null, e);
    }
    // also for win
    public static void plainMessagePopUP(String text){ 
        JOptionPane.showMessageDialog(null, text, "Message", JOptionPane.PLAIN_MESSAGE);
    }
    // Perft setup
    public static boolean perftSettingsPopUP(){
        JCheckBox hashing = new JCheckBox("Hashing?");
        JTextField depthField = new JTextField();
        Object[] params =  {hashing, "Depth:(Max 5 on non-standard board, else 7 (castling, promotion not included))",depthField};
        int result = JOptionPane.showConfirmDialog(null, params, "Setup Perft", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION){
            return false;
        }
        perftHashing = hashing.isSelected();
        if (perftHashing){
            long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
            long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
            // real 4Gbytes = 4,294,965,096, but even perft 7 with 3*(10**9) moves wont take this much, and since
            // depth 5 on non-standard board is limit, its almost impossible to have more than 100-million moves
            if (presumableFreeMemory < 4000000000L){ 
                PopUps.plainMessagePopUP("4Gigabytes of free memory is needed for safe hashing, your free memory is "+presumableFreeMemory);
                perftHashing = false;
            }
        }
        String temp = depthField.getText();
        temp = temp.replaceAll("\\D+","");
        if (temp != null && temp.length() > 0){
            perftDepth = Integer.parseInt(temp);
            if (perftDepth > 7){
                perftDepth = 7;
            } else if (perftDepth < 1){
                perftDepth = 1;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean gameSettingsPopUp() {
        JCheckBox castleBox = new JCheckBox("Castling?");
        JCheckBox enpassantBox = new JCheckBox("Enpassant?");
        JCheckBox aiBox = new JCheckBox("AI opponent?");
        JCheckBox aiColor = new JCheckBox("AI color(white)?");
        JCheckBox currPlayer = new JCheckBox("Starting Player(black)?"); 
        JTextField timeField = new JTextField();
        timeField.setText("0sec(unlimited)");
        Object[] params = {castleBox, enpassantBox, aiBox, currPlayer, aiColor, "Time:", timeField};
        int result = JOptionPane.showConfirmDialog(null, params, "Setup", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.CANCEL_OPTION){
            return false;
        }
        castling = castleBox.isSelected();
        enpassant = enpassantBox.isSelected();
        AI = aiBox.isSelected();
        if (aiColor.isSelected()){
            aiCol = 0;
        }
        if (currPlayer.isSelected()){
            startingPlayerColor = 1;
        } else {
            startingPlayerColor = 0;
        }
        String temp = timeField.getText();
        temp = temp.replaceAll("\\D+",""); // get numbers from string
        if (temp != null && temp.length() > 0){
            time = Long.parseLong(temp);
            if (time < 10 && time != 0){
                time = 10; // min 10sec
            } else if (time > 3600){
                time = 3600; // max one hour
            } 
        }else {
            time = 0;
        }
        return true;
    }
}