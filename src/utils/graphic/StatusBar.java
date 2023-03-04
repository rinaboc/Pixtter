package utils.graphic;

import utils.math.vec2d;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.awt.*;

import static utils.Constants.GamePalette.PALETTE;

public class StatusBar {
    private vec2d position;
    private int width, height;
    private float health = 1f;

    public StatusBar(vec2d position, int width, int height){
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public void render(Graphics g, int xOffset){

        g.setColor(PALETTE[0]);
        g.fillRect((int) (position.x - xOffset), (int) (position.y), width, height);

        g.setColor(PALETTE[11]);
        g.fillRect((int) (position.x - xOffset), (int) (position.y), (int) (width * health), height);
    }

    public void setPosition(vec2d position){
        this.position = position;
    }

    public void changeHP(float amount){
        health += amount;

        if(health < 0){
            health = 0;
            return;
        }

        if(health > 1f){
            health = 1f;
        }

    }

    public boolean isDead(){
        return health <= 0;
    }
}
