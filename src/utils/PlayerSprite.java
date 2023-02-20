package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static utils.Constants.PlayerConstants.GetSpriteAmount;

public class PlayerSprite {
    private BufferedImage[][] animations;
    private int aniTick, aniIndex;
    private final int aniSpeed = 25;
    private int playerAction;

    public PlayerSprite(){
        loadAnimations();
    }

    public void update(){
        updateAnimationTick();
    }

    public Image getImage(){
        return animations[playerAction][aniIndex];
    }

    public void setPlayerAction(int playerAction){
        this.playerAction = playerAction;
        checkAnimationIndexOverflow();
    }

    private void updateAnimationTick() {
        aniTick++;
        if(aniTick >= aniSpeed){
            aniTick = 0;
            aniIndex++;
            checkAnimationIndexOverflow();
        }
    }

    private void checkAnimationIndexOverflow(){
        if(aniIndex >= GetSpriteAmount(playerAction)){
            aniIndex = 0;
        }
    }

    private void loadAnimations() {
        InputStream is = getClass().getResourceAsStream("/player-spritesheet.png");
        try {
            BufferedImage img = ImageIO.read(is);

            animations = new BufferedImage[4][4];
            for (int j = 0; j < animations.length; j++) {
                for (int i = 0; i < animations[j].length; i++) {
                    animations[j][i] = img.getSubimage(i*50, j*24, 50,24);
                }
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
