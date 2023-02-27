package entities;

import core.Application;
import utils.AnimatedSprite;
import utils.math.vec2d;

import java.awt.*;

import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.PlayerConstants.MovementDir.LEFT;
import static utils.Constants.PlayerConstants.MovementDir.RIGHT;
import static utils.Constants.PlayerConstants.*;

public class Wolf extends Enemy{

    private boolean mirrorImage = false;
    private AnimatedSprite sprite;

    private float health = 1f;

    public Wolf(Application app, float x, float y) {
        super(app, x, y, 160, 128);
        sprite = new AnimatedSprite("/wolf-spritesheet.png", 2, 4, 80, 64);
        collider = new Rectangle((int) (x + width/9), (int) (y), (int) (4.5*width/6), height);
        spawnRadius = 300f;
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

        g.setColor(PALETTE[0]);
        g.fillRect((int) (position.x - xOffset + width/4), (int) (position.y - 40), width - width/4, 10);

        g.setColor(PALETTE[11]);
        g.fillRect((int) (position.x - xOffset + width/4), (int) (position.y - 40), (int) (3*width/4 * health ), 10);

        g.setColor(Color.PINK);
        g.drawLine((int) collider.getCenterX() - xOffset, (int) collider.getCenterY(), (int) (collider.getCenterX() + debugVectoPlayer.x) -xOffset, (int) (collider.getCenterY() + debugVectoPlayer.y));
//        g.drawRect(collider.x - xOffset, collider.y, collider.width, collider.height);
    }

    @Override
    public void attackHandler(vec2d attackerPosition) {
        super.attackHandler(attackerPosition);

        health -= 0.1f;

        if(health <= 0){
            app.getLevelManager().deleteEntity(this);
        }
    }
}
