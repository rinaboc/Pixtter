package entities;

import core.Application;
import utils.graphic.PlayerSprite;
import utils.graphic.StatusBar;
import utils.math.vec2d;

import java.awt.*;
import java.util.Vector;

import static utils.Constants.PlayerConstants.*;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.MovementDir.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Player extends MovingEntity{

    private final PlayerSprite sprite;
    private final StatusBar statusBar;

    private boolean moving = false, mirrorPlayer = false, turning = false, attack = false;

    private int jumpCooldown = JUMP_CD;
    private boolean canJump = false;

    public Player(float x, float y, Application app) {
        super(app, x, y, 100, 48);

        collider = new Rectangle((int)(x + width/3), (int)(y + height/3), 2*width/3, 2*height/3);
        movementSpeed = WALKING_SPEED;
        sprite = new PlayerSprite(this);
        statusBar = new StatusBar(new vec2d(position.x + width/4, position.y-40), 3*width/4, 10);
    }

    @Override
    public void update(){

        jumpUpdate();
        updatePosition();

    }



    @Override
    public void render(Graphics g, int xOffset){

        sprite.update();
        setAnimation();

        if(mirrorPlayer && !turning || (!mirrorPlayer && turning)){
            g.drawImage(sprite.getImage(), (int)position.x+width - xOffset, (int)position.y, -width, height, null);
        } else {
            g.drawImage(sprite.getImage(), (int)position.x - xOffset, (int)position.y, width, height, null);
        }

        statusBar.render(g, xOffset);

        // DEBUG DRAW
//        g.setColor(Color.PINK);
//        g.drawRect(collider.x - xOffset, collider.y, collider.width, collider.height);

        if(collidedObjectDebug != null){
            for(Rectangle rect : collidedObjectDebug){
                g.drawRect(rect.x - xOffset, rect.y, rect.width, rect.height);
            }
        }
//
//        g.setColor(Color.BLUE);
//        g.drawLine((int) bodyCollider.getCenterX(), (int) bodyCollider.getCenterY(), (int) (bodyCollider.getCenterX() + playerMovement.x * 50f), (int) (bodyCollider.getCenterY() + playerMovement.y * 50f));

    }

    private void jumpUpdate() {
        if(jumpCooldown != 0){
            jumpCooldown--;
        } else {
            canJump = true;
        }
    }

    protected void updatePosition() {

        if(attackedCD != 0){
            attackedCD--;
        }

        moving = false;
        boolean wasMirror = mirrorPlayer;

        // set the movement vector according to keys pressed
        if(movementDir[LEFT] && !movementDir[RIGHT] && attackedCD == 0){
            movementVec.x -= movementSpeed;
            mirrorPlayer = true;
        } else if(movementDir[RIGHT] && !movementDir[LEFT] && attackedCD == 0){
            movementVec.x += movementSpeed;
            mirrorPlayer = false;
        }

        if(movementDir[UP] && !movementDir[DOWN] && attackedCD == 0){
            if(canJump && movementVec.y == 0){ // jump
                movementVec.y -= JUMP_FORCE;
                jumpCooldown = JUMP_CD;
                canJump = false;
            }
        } else if(movementDir[DOWN] && !movementDir[UP] && attackedCD == 0){
            movementVec.y += movementSpeed * 0.01;
        }

        // apply physics
        movementVec.y += GRAVITY_PULL;

        collisionHandler();

        // allow jumping after hitting object
        if(movementVec.y != 0 && canJump){
            canJump = false;
        }

        // moving on the map
        if(!movementVec.nullVec()){
            // apply drags
            if(canJump && attackedCD == 0) movementVec.x *= GROUND_DRAG;
            else if(attackedCD != 0) movementVec.x *= 0.9f;
            else movementVec.x *= AIR_DRAG;
            // apply movement to character position
            position = position.addVec(movementVec);
            // scroll map -> horizontal movement
            app.getLevelManager().scrollLevel(position.addVec(new vec2d(width/2, 0)));

            moving = true;
        } else {
            moving = false;
        }

        // collider correction in case of opposite sprite direction movement
        if(mirrorPlayer){
            collider.setLocation((int)(position.x + width/8), (int)(position.y + height/3));
        } else {
            collider.setLocation((int)(position.x + width/4), (int)(position.y + height/3));
        }

        // check if player wants to go in opposite direction -> apply turning animation
        if(wasMirror != mirrorPlayer){
            turning = true;
            sprite.restartAnimation();
        }

        statusBar.setPosition(new vec2d(position.x + width/4, position.y - 40));

    }

    private void setAnimation(){

        if(turning){
            sprite.setAnimationAction(TURNING);
            return;
        }

        if(attack){
            sprite.setAnimationAction(ATTACK1);
            return;
        }


        if(moving && movementSpeed == WALKING_SPEED){
            sprite.setAnimationAction(WALKING);
        } else if(moving && movementSpeed == RUNNING_SPEED){
            sprite.setAnimationAction(RUNNING);
        }
        else {
            sprite.setAnimationAction(IDLE);
        }

        if(movementVec.y != 0){
            sprite.setAnimationAction(FALLING);
        }

    }

    public void runningAction(boolean status){
        if(status){
            movementSpeed = RUNNING_SPEED;
        } else {
            movementSpeed = WALKING_SPEED;
        }
    }

    public void attack(){
        if(attack){
            return;
        }

        attack = true;
        sprite.restartAnimation();

        Vector<Entity> enemies = app.getLevelManager().entityTrigger(getBounds());

        for(Entity entity: enemies){
            entity.attackHandler(position);
        }
    }

    public void setMovementDir(int dir, boolean status){
        movementDir[dir] = status;
    }

    public void resetMovement(){
        movementDir = new boolean[4];
    }

    public vec2d getPosition(){
        return position;
    }
    public vec2d getCenterPosition(){
        return new vec2d((float) collider.getCenterX(), (float) collider.getCenterY());
    }

    public void setTurning(boolean turning){
        this.turning = turning;
    }

    public void setAttack(boolean attack){
        this.attack = attack;
    }

    @Override
    public void attackHandler(vec2d attackerPosition) {
        super.attackHandler(attackerPosition);

        statusBar.changeHP(-0.1f);

        if(statusBar.isDead()){
            System.out.println("dead");
        }
    }
}
