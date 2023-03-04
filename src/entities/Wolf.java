package entities;

import core.Application;
import utils.effects.Effect;
import utils.graphic.EnemySprite;
import utils.graphic.StatusBar;
import utils.math.vec2d;

import java.awt.*;

import static utils.Constants.EffectConstants.FX_SPARK;
import static utils.Constants.PlayerConstants.MovementDir.RIGHT;
import static utils.Constants.PlayerConstants.*;

public class Wolf extends Enemy{

    private boolean mirrorImage = false;
    private EnemySprite sprite;
    private StatusBar statusBar;

    public Wolf(Application app, float x, float y) {
        super(app, x, y, 160, 128);
        sprite = new EnemySprite("/wolf-spritesheet.png", 6, 4, 80, 64, this);
        collider = new Rectangle((int) (x + width/9), (int) (y), (int) (4.5*width/6), height);
        spawnRadius = 300f;

        statusBar = new StatusBar(position.addVec(new vec2d(width/4, -40)), 3*width/4, 10);
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

        statusBar.setPosition(new vec2d(position.x + width/4, position.y - 40));
    }

    private void setAnimation() {
        if(attack){
            sprite.setAnimationAction(ATTACK1);
            return;
        }

        if(movementVec.getLength() > 0.1f && attackedCD == 0){
            sprite.setAnimationAction(WALKING);
        } else {
            sprite.setAnimationAction(IDLE);
        }

        if(movementVec.y != 0){
            sprite.setAnimationAction(FALLING);
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

//        g.setColor(PALETTE[0]);
//        g.fillRect((int) (position.x - xOffset + width/4), (int) (position.y - 40), width - width/4, 10);
//
//        g.setColor(PALETTE[11]);
//        g.fillRect((int) (position.x - xOffset + width/4), (int) (position.y - 40), (int) (3*width/4 * health ), 10);
        statusBar.render(g, xOffset);

        g.setColor(Color.PINK);
        g.drawLine((int) collider.getCenterX() - xOffset, (int) collider.getCenterY(), (int) (collider.getCenterX() + debugVectoPlayer.x) -xOffset, (int) (collider.getCenterY() + debugVectoPlayer.y));
//        g.drawRect(collider.x - xOffset, collider.y, collider.width, collider.height);
    }

    @Override
    public void attackHandler(vec2d attackerPosition) {
        super.attackHandler(attackerPosition);

        statusBar.changeHP(-0.3f);
//        health -= 0.3f;

        if(statusBar.isDead()){
            new Effect(FX_SPARK, new vec2d((float) collider.getCenterX(), (float) collider.getCenterY()), app, 4);
            app.getLevelManager().deleteEntity(this);
        }
    }

    @Override
    protected void attackInit(vec2d atkVec) {
        super.attackInit(atkVec);

        attack = true;
        sprite.setAnimationAction(ATTACK1);
        sprite.restartAnimation();
    }
}
