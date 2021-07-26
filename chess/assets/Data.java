package assets;

import java.util.ArrayList;

public interface Data {

    /**
     * Class holding text history visible on screen,
     * move history (FEN positions),
     * and if AI is present in game
     */
    public class Holder {
        /**
         * Ai present in game
         */
        private boolean AI = false;
        /**
         * 0th String is starting position of game,
         * last String is current position of game
         */
        private final ArrayList<String> fenHistory;
        /**
         * Same as fenHistory
         */
        private final ArrayList<String> textHistory;

        public Holder(boolean AI, ArrayList<String> fenHistory, ArrayList<String> textHistory) {
                this.AI = AI;
                this.fenHistory = fenHistory;
                this.textHistory = textHistory;
        }

        public Holder() {
            this(false, new ArrayList<>(), new ArrayList<>());
        }

        /**
         * Resets to default values
         */
        public void clear() {
            AI = false;
            fenHistory.clear();
            textHistory.clear();
        }


        public void appendFen(String fen) {
            fenHistory.add(fen);
        }

        public void appendText(String text) {
            textHistory.add(text);
        }

        public String removeLastText() {
            if (textHistory.size() == 0) {
                return null;
            }
            return textHistory.remove(textHistory.size()-1);
        }

        public String removeLastFen() {
            if (fenHistory.size() == 0) {
                return null;
            }
            return fenHistory.remove(fenHistory.size()-1);
        }

        public String getLastText() {
            if (textHistory.size() == 0) {
                return null;
            }
            return textHistory.get(textHistory.size()-1);
        }

        public String getLastFen() {
            if (fenHistory.size() == 0) {
                return null;
            }
            return fenHistory.get(fenHistory.size()-1);
        }

        public int getSize() {
            return fenHistory.size();
        }

        public String getText() {
            String result = "";
            for (String string : textHistory) {
                result += string;
            }
            return result;
        }

        public ArrayList<String> getFenHistory() {
            return fenHistory;
        }

        public ArrayList<String> getTextHistory() {
            return textHistory;
        }

        /**
         * @param AI if computer player should be play
         */
        public void setAI(boolean AI) {
            this.AI = AI;
        }

        /**
         * @return true if computer player is in game, 
         * false otherwise
         */
        public boolean getAI() {
            return AI;
        }

        @Override
        public String toString() {
            String result = "";
            for (String text : textHistory) {
                result += text;//(text + "\n");
            }
            for (String fen : fenHistory) {
                result += (fen + "\n");
            }
            return result;
        }
    }

    /**
     * @param holder to which implementing class
     * should give its state to be saved
     * @return true if saving was successful,
     * false otherwise
     */
    public boolean save(Holder holder);
    
    /**
     * @param holder from which implementing class
     * should take data
     * @return true if loading was successful,
     * false otherwise
     */
    public boolean load(Holder holder);

    /**
     * @return true if implementing class is able to save
     * its state right now, false otherwise
     */
    public boolean canSave();

    /**
     * @return true if implementing class is able to load
     * its state right now, false otherwise
     */
    public boolean canLoad();
}
