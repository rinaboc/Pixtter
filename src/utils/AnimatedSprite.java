package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static utils.Constants.PlayerConstants.GetSpriteAmount;

public class AnimatedSprite {
    protected BufferedImage[][] animations;
    protected int aniTick, aniIndex;
    protected final int aniSpeed = 25;
    protected int animationAction;

    public AnimatedSprite(String path, int row, int col, int width, int height){
        animations = new BufferedImage[row][col];
        loadAnimations(path, width, height);
    }

    public void update(){
        updateAnimationTick();
    }

    public Image getImage(){
        return animations[animationAction][aniIndex];
    }


    public void setAnimationAction(int animationAction){
        this.animationAction = animationAction;
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
        if(aniIndex >= GetSpriteAmount(animationAction)){
            aniIndex = 0;
        }
    }

    private void loadAnimations(String path, int width, int height) {
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage img = ImageIO.read(is);

            for (int j = 0; j < animations.length; j++) {
                for (int i = 0; i < animations[j].length; i++) {
                    animations[j][i] = img.getSubimage(i*width, j*height, width,height);
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
