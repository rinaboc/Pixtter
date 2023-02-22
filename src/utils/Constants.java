package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Constants {
    public static class GameDimensions{
        public static int GAME_WIDTH = 640;
        public static int GAME_HEIGHT = 360;
    }

    public static class GamePalette{
        public static Color[] PALETTE = new Color[12];

        public static void LoadPalette() {
            InputStream is = GamePalette.class.getResourceAsStream("/palette.png");
            try {
                BufferedImage img = ImageIO.read(is);

                for (int j = 0; j < 12; j++) {
                    PALETTE[j] = Color.decode(Integer.toString(img.getRGB(j, 0)));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static class LevelConstants{
        public static final int TILE_SIZE = 8;
        public static final float SCROLL_SPEED = 1.8f;
    }

    public static class PlayerConstants{
        public static final int IDLE = 0;
        public static final int WALKING = 1;
        public static final int RUNNING = 2;
        public static final int FALLING = 3;

        public static int GetSpriteAmount(int player_action){
            switch (player_action){
                case IDLE:
                case RUNNING:
                case FALLING:
                    return 3;
                case WALKING:
                    return 4;
                default:
                    return 1;
            }
        }

        public static class MovementDir{
            public static final int UP = 0;
            public static final int RIGHT = 1;
            public static final int DOWN = 2;
            public static final int LEFT = 3;
        }

        public static class MovementConstants{
            public static final int JUMP_CD = 120;
            public static final float JUMP_FORCE = 2.5f;

            public static final float BUMP_FORCE = 0.14f;

            public static final float WALKING_SPEED = 0.8f;
            public static final float RUNNING_SPEED = 0.81f;
        }

        public static class PhysicsConstants{
            public static final float GRAVITY_PULL = 0.05f;
            public static final float GROUND_DRAG = 0.5f;
            public static final float AIR_DRAG = 0.5f;
        }
    }
}
