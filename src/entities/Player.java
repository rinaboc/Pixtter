package entities;

import core.Application;
import utils.PlayerSprite;
import utils.math.vec2d;

import java.awt.*;
import java.util.Vector;

import static utils.Constants.GameDimensions.GAME_WIDTH;
import static utils.Constants.LevelConstants.TILE_SIZE;
import static utils.Constants.PlayerConstants.*;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.MovementDir.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Player extends Entity{

    private final Application app;
    private vec2d worldPosition;

    private final PlayerSprite sprite;

    private float playerSpeed = WALKING_SPEED;
    private boolean[] movementDir = new boolean[4];
    private final vec2d playerMovement = new vec2d(0, 0);

    private boolean moving = false, mirrorPlayer = false;

    private int jumpCooldown = JUMP_CD;
    private boolean canJump = false;

    private final Rectangle bodyCollider;
    private Vector<Rectangle> collidedObjectDebug = new Vector<>();

    public Player(float x, float y, Application app) {
        super(x, y, 100, 48);
        this.app = app;

        worldPosition = new vec2d(x, y);

        bodyCollider = new Rectangle((int)(x + width/3), (int)(y + height/3), 2*width/3, 2*height/3);
        sprite = new PlayerSprite();
    }

    @Override
    public void update(){

        if(jumpCooldown != 0){
            jumpCooldown--;
        } else {
            canJump = true;
        }

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
        g.setColor(Color.PINK);
        g.drawRect(bodyCollider.x - xOffset, bodyCollider.y, bodyCollider.width, bodyCollider.height);

        if(collidedObjectDebug != null){
            for(Rectangle rect : collidedObjectDebug){
                g.drawRect(rect.x - xOffset, rect.y, rect.width, rect.height);
            }
        }
//
//        g.setColor(Color.BLUE);
//        g.drawLine((int) bodyCollider.getCenterX(), (int) bodyCollider.getCenterY(), (int) (bodyCollider.getCenterX() + playerMovement.x * 50f), (int) (bodyCollider.getCenterY() + playerMovement.y * 50f));

    }

    private void updatePosition() {

        moving = false;

        if(movementDir[LEFT] && !movementDir[RIGHT]){
            playerMovement.x -= playerSpeed;
            mirrorPlayer = true;
        } else if(movementDir[RIGHT] && !movementDir[LEFT]){
            playerMovement.x += playerSpeed;
            mirrorPlayer = false;
        }

        if(movementDir[UP] && !movementDir[DOWN]){
            if(canJump && playerMovement.y == 0){ // jump
                playerMovement.y -= JUMP_FORCE;
                jumpCooldown = JUMP_CD;
                canJump = false;
            }
        } else if(movementDir[DOWN] && !movementDir[UP]){
            playerMovement.y += playerSpeed * 0.01;
        }

        playerMovement.y += GRAVITY_PULL;

        collisionHandler();

        // moving on the map
        if(!playerMovement.nullVec()){
            // apply drags
            if(canJump) playerMovement.x *= GROUND_DRAG;
            else playerMovement.x *= AIR_DRAG;
            // apply movement to character position
            position = position.addVec(playerMovement);
            // scroll map -> horizontal movement
            app.getLevelManager().scrollLevel(position.addVec(new vec2d(width/2, 0)));


            System.out.println(position.x + " " + position.y);

            moving = true;
        } else {
            moving = false;
        }

        // collider correction in case of opposite sprite direction movement
        if(mirrorPlayer){
            bodyCollider.setLocation((int)(position.x + width/8), (int)(position.y + height/3));
        } else {
            bodyCollider.setLocation((int)(position.x + width/4), (int)(position.y + height/3));
        }
    }

    private void collisionHandler(){
        Vector<Rectangle> collidedObject = app.getLevelManager().hiddenCollisionTrigger(bodyCollider);
        collidedObjectDebug = collidedObject;

        if(collidedObject.size() > 0){

            for(Rectangle rect : collidedObject){

                Rectangle innerCollider = new Rectangle(bodyCollider);
                innerCollider.grow(-2, -2);

                // left direction
                innerCollider.setLocation(bodyCollider.x - TILE_SIZE/2, bodyCollider.y + 1);
                if(playerMovement.x < 0 && rect.intersects(innerCollider)){
                    playerMovement.x = 0;
                }

                // up direction
                innerCollider.setLocation(bodyCollider.x + 1, bodyCollider.y - TILE_SIZE/2);
                if(playerMovement.y < 0 && rect.intersects(innerCollider)){
                    playerMovement.y = 0;
                }

                // right direction
                innerCollider.setLocation(bodyCollider.x + TILE_SIZE/2, bodyCollider.y + 1);
                if(playerMovement.x > 0 && rect.intersects(innerCollider)){
                    playerMovement.x = 0;
                }

                // down direction
                innerCollider.setLocation(bodyCollider.x + 1, bodyCollider.y + TILE_SIZE/2);
                if(playerMovement.y > 0 && rect.intersects(innerCollider)){
                    playerMovement.y = 0;
                    jumpCooldown = 0;
                }

                // stairs auto bump
                if(bodyCollider.y + bodyCollider.height - rect.y > 1 && bodyCollider.y + bodyCollider.height - rect.y <= TILE_SIZE + 1 && playerMovement.x == 0){
                    playerMovement.y -= BUMP_FORCE;
                }

            }
        }
    }

    private void setAnimation(){
        if(moving && playerSpeed == WALKING_SPEED){
            sprite.setPlayerAction(WALKING);
        } else if(moving && playerSpeed == RUNNING_SPEED){
            sprite.setPlayerAction(RUNNING);
        }
        else {
            sprite.setPlayerAction(IDLE);
        }

        if(playerMovement.y != 0){
            sprite.setPlayerAction(FALLING);
        }
    }

    public void runningAction(boolean status){
        if(status){
            playerSpeed = RUNNING_SPEED;
        } else {
            playerSpeed = WALKING_SPEED;
        }
    }

    public void setMovementDir(int dir, boolean status){
        movementDir[dir] = status;
    }

    public void resetMovement(){
        movementDir = new boolean[4];
    }
}
