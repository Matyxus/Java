package utils;

public class testingGrounds {

    private enum Colors {
        WHITE(0, "w"),
        BLACK(1, "b");

        private int value;
        private String fenValue;

        Colors(int value, String fenValue) {
            this.value= value;
            this.fenValue = fenValue;
        }
    }


    public static void main(String[] args) {
        Colors color = Colors.WHITE;
        Colors color2 = Colors.BLACK;
        System.out.println(color);
        System.out.println(color == Colors.WHITE);
        
    }
}
