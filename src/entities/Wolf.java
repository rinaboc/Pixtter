package entities;

import core.Application;
import utils.AnimatedSprite;

import java.awt.*;

import static utils.Constants.PlayerConstants.MovementDir.LEFT;
import static utils.Constants.PlayerConstants.MovementDir.RIGHT;
import static utils.Constants.PlayerConstants.*;

public class Wolf extends Enemy{

    private boolean mirrorImage = false;
    private AnimatedSprite sprite;

    public Wolf(Application app, float x, float y) {
        super(app, x, y, 160, 128);
        sprite = new AnimatedSprite("/wolf-spritesheet.png", 2, 4, 80, 64);
        collider = new Rectangle((int) (x + width/9), (int) (y), (int) (4.5*width/6), height);
    }

    @Override
    public void update() {
        super.update();


    }

    @Override
    protected void updatePosition() {
        super.updatePosition();

        if(mirrorImage){
            collider.setLocation((int) (position.x + width/9), (int) position.y);
        } else {
            collider.setLocation((int)(position.x + width/6), (int) position.y);
        }
    }

    private void setAnimation() {
        if(movementVec.getLength() > 0.1f && attackedCD == 0){
            sprite.setAnimationAction(WALKING);
        } else {
            sprite.setAnimationAction(IDLE);
        }
    }

    @Override
    public void render(Graphics g, int xOffset) {

        mirrorImage = movementDir[RIGHT];
        sprite.update();
        setAnimation();

        if(mirrorImage){
            g.drawImage(sprite.getImage(), (int) (position.x + width - xOffset), (int) position.y, -width, height, null);
        } else {
            g.drawImage(sprite.getImage(), (int) (position.x - xOffset), (int) position.y, width, height, null);
        }

//        g.setColor(Color.PINK);
//        g.drawRect(collider.x - xOffset, collider.y, collider.width, collider.height);
    }

}
