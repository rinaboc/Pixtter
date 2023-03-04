package utils.effects;

import core.Application;
import utils.math.vec2d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static utils.Constants.EffectConstants.GetEffectFrameCount;
import static utils.Constants.EffectConstants.GetFXAnimationSpeed;

public class Effect {
    protected BufferedImage[] frames;
    protected int aniTick, aniIndex;
    protected int effectType;

    protected vec2d position;
    protected int width, height;
    protected float scale;
    protected boolean active = true;

    protected Application app;

    public Effect(int effectType, vec2d position, Application app, float scale){
        this.width = this.height = 24;
        frames = new BufferedImage[GetEffectFrameCount(effectType)];
        loadAnimation(effectType, width, height);
        this.effectType = effectType;
        this.position = position;
        this.scale = scale;
        this.app = app;

        app.addEffect(this);
    }

    public void update(){
        if(!active){
            return;
        }

        updateAnimationTick();
    }

    public void render(Graphics g, int xOffset){
        if(!active){
            return;
        }

        g.drawImage(frames[aniIndex], (int) ( position.x - xOffset - width/2 * scale), (int) ( position.y - height/2 * scale), (int) (width * scale), (int) (height * scale), null);
    }

    private void updateAnimationTick() {
        aniTick++;
        if(aniTick >= GetFXAnimationSpeed(effectType)){
            aniTick = 0;
            aniIndex++;
        } else {
            return;
        }

        if(aniIndex >= GetEffectFrameCount(effectType)){
            active = false;
//            app.removeEffect(this);
        }
    }

    private void loadAnimation(int effectType, int width, int height) {
        InputStream is = getClass().getResourceAsStream("/effects-animation-sheet.png");
        try {
            BufferedImage img = ImageIO.read(is);

            for (int j = 0; j < frames.length; j++) {
                frames[j] = img.getSubimage(j * width, 0, width, height);
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

    public boolean isActive(){
        return active;
    }
}
