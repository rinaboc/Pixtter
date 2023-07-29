package utils;

public class Constants {
    public static class SCR {
        public static double SCALE = 1d;
        public static int WIDTH = (int) (640 * SCALE);
        public static int HEIGHT = (int) (360 * SCALE);
        public static int FPS = 60;
        public static int UPS = 120;
    }

    public enum DIR {
        NONE(-1), UP(0), DOWN(2), LEFT(3), RIGHT(1);

        private final int index;

        DIR(int index){
            this.index = index;
        }

        public int getIndex(){
            return index;
        }
    }

    public static class PHYS {
        public static double GRAVITY = 0.1d;
    }
}
