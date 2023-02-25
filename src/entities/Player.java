package entities;

import core.Application;
import utils.PlayerSprite;
import utils.math.vec2d;

import java.awt.*;

import static utils.Constants.PlayerConstants.*;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.MovementDir.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Player extends MovingEntity{

    private final PlayerSprite sprite;

    private boolean moving = false, mirrorPlayer = false;

    private int jumpCooldown = JUMP_CD;
    private boolean canJump = false;

    public Player(float x, float y, Application app) {
        super(app, x, y, 100, 48);

        collider = new Rectangle((int)(x + width/3), (int)(y + height/3), 2*width/3, 2*height/3);
        movementSpeed = WALKING_SPEED;
        sprite = new PlayerSprite();
    }

    @Override
    public void update(){

        jumpUpdate();

        updatePosition();
        setAnimation();
        sprite.update();
    }



    @Override
    public void render(Graphics g, int xOffset){
        if(mirrorPlayer){
            g.drawImage(sprite.getImage(), (int)position.x+width - xOffset, (int)position.y, -width, height, null);
        } else {
            g.drawImage(sprite.getImage(), (int)position.x - xOffset, (int)position.y, width, height, null);
        }

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

        moving = false;

        if(movementDir[LEFT] && !movementDir[RIGHT]){
            movementVec.x -= movementSpeed;
            mirrorPlayer = true;
        } else if(movementDir[RIGHT] && !movementDir[LEFT]){
            movementVec.x += movementSpeed;
            mirrorPlayer = false;
        }

        if(movementDir[UP] && !movementDir[DOWN]){
            if(canJump && movementVec.y == 0){ // jump
                movementVec.y -= JUMP_FORCE;
                jumpCooldown = JUMP_CD;
                canJump = false;
            }
        } else if(movementDir[DOWN] && !movementDir[UP]){
            movementVec.y += movementSpeed * 0.01;
        }

        movementVec.y += GRAVITY_PULL;

        collisionHandler();

        if(movementVec.y != 0 && canJump){
            canJump = false;
        }

        // moving on the map
        if(!movementVec.nullVec()){
            // apply drags
            if(canJump) movementVec.x *= GROUND_DRAG;
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
    }

    private void setAnimation(){
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

    public void setMovementDir(int dir, boolean status){
        movementDir[dir] = status;
    }

    public void resetMovement(){
        movementDir = new boolean[4];
    }

    public vec2d getPosition(){
        return position;
    }
}
