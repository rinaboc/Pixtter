package entities;

import core.Application;
import utils.math.vec2d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static utils.Constants.GameDimensions.GAME_WIDTH;
import static utils.Constants.LevelConstants.TILE_SIZE;
import static utils.Constants.PlayerConstants.*;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.MovementDir.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Player extends Entity{

    private Application app;

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int playerAction = IDLE;

    private float playerSpeed = WALKING_SPEED;
    private boolean[] movementDir = new boolean[4];
    private boolean moving = false, mirrorPlayer = false;

    private vec2d playerMovement = new vec2d(0, 0);
    private int jumpCooldown = JUMP_CD;
    private boolean canJump = false;

    private Rectangle bodyCollider;
    private Vector<Rectangle> collidedObjectDebug = new Vector<>();

    public Player(float x, float y, Application app) {
        super(x, y, 100, 48);
        this.app = app;
        bodyCollider = new Rectangle((int)(x + width/3), (int)(y + height/3), 2*width/3, 2*height/3);
        loadAnimations();
    }

    public void update(){
        if(jumpCooldown != 0){
            jumpCooldown--;
        } else {
            canJump = true;
        }
        updatePosition();
        setAnimation();
        updateAnimationTick();
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
            if(canJump){ // jump
                playerMovement.y -= JUMP_FORCE;
                jumpCooldown = JUMP_CD;
                canJump = false;
                System.out.println("jump");
            }
        } else if(movementDir[DOWN] && !movementDir[UP]){
            playerMovement.y += playerSpeed * 0.01;
        }

        playerMovement.y += GRAVITY_PULL;

        collisionHandlerThree();

        // moving on the map
        if(!playerMovement.nullVec()){
            // scroll map -> horizontal movement
            if((!app.getLevelManager().canScroll()) || (Math.abs(position.x + (float)width/2 - (float)GAME_WIDTH/2) >= 1f)){
                position.x += playerMovement.x;
            }
            app.getLevelManager().scrollLevel(playerMovement);

            // apply drags
            if(canJump) playerMovement.x *= GROUND_DRAG;
            else playerMovement.x *= AIR_DRAG;

            // vertical movement
            position.y += playerMovement.y;

            moving = true;
        } else {
            moving = false;
        }

        // collider correction in case of mirror movement
        if(mirrorPlayer){
            bodyCollider.setLocation((int)(position.x + width/8), (int)(position.y + height/3));
        } else {
            bodyCollider.setLocation((int)(position.x + width/4), (int)(position.y + height/3));
        }
    }

    private void collisionHandlerThree(){
        Vector<Rectangle> collidedObject = app.getLevelManager().collisionTrigger(bodyCollider);
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

                // stairs
                if(bodyCollider.y + bodyCollider.height - rect.y > 1 && bodyCollider.y + bodyCollider.height - rect.y <= TILE_SIZE + 1 && playerMovement.x == 0){
                    playerMovement.y -= BUMP_FORCE;
                }

            }
        }
    }

    private void collisionHandlerOne(){
        Vector<Rectangle> collidedObject = app.getLevelManager().collisionTrigger(bodyCollider);
        collidedObjectDebug = collidedObject;

        if(collidedObject.size() > 0){ // collision detected

            boolean horizontalCollision = false;

            for(Rectangle rect : collidedObject){
                if(Math.abs(rect.getCenterX() - bodyCollider.getCenterX()) < ((float)bodyCollider.width/2 + (float)rect.width/2) && Math.abs(rect.y - bodyCollider.y) <= TILE_SIZE){
                    horizontalCollision = true;
                    playerMovement.x = 0;
                }

                // apply stairs bump
                if(bodyCollider.y + bodyCollider.height - rect.y > TILE_SIZE && !horizontalCollision){
                    if(bumpCooldown != 0){
                        bumpCooldown--;
                    } else {
                        playerMovement.y -= BUMP_FORCE;
                        bumpCooldown = BUMP_CD;
                    }
                }
                else {
                    if(playerMovement.y > 0){
                        playerMovement.y = 0;
                        canJump = true;
                    }
                }
            }
        } else { // gravity
            playerMovement.y += GRAVITY_PULL;
        }
    }

    private void collisionHandlerTwo(){
        Vector<Rectangle> collidedObject = app.getLevelManager().collisionTrigger(bodyCollider);
        collidedObjectDebug = collidedObject;

        if(collidedObject.size() > 0){ // collision detected

            vec2d playerVec = new vec2d(playerMovement);
            playerVec.normalize();

            vec2d yDir = new vec2d(0, 1f);
            vec2d xDir = new vec2d(1f, 0);
            jointCollision = collidedObject.elementAt(0);

            for(Rectangle rect : collidedObject){
                jointCollision.add(rect);

                vec2d collisionVec = new vec2d((float)bodyCollider.getCenterX(), (float)bodyCollider.getCenterY(), (float)rect.getCenterX(), (float)rect.getCenterY());
                collisionVec.normalize();

                System.out.println(" dx: " + playerVec.x + " dy: " + playerVec.y + " col x: " + collisionVec.x + " col y: " + collisionVec.y);

                if(playerVec.dotProduct(collisionVec) > 0.9f){
                    float yDot = yDir.dotProduct(collisionVec);
                    System.out.println(yDot);
                    float xDot = xDir.dotProduct(collisionVec);

                    if((yDot > 0.9f && playerMovement.y > 0) || (yDot < -0.9f && playerMovement.y < 0)){
                        playerMovement.y = 0;
                    }

                    if((xDot > 0.9f && playerMovement.x > 0) || (xDot < -0.9f && playerMovement.x < 0)){
                        playerMovement.x = 0;
                    }

                }
            }

            vec2d collisionVec = new vec2d((float)bodyCollider.getCenterX(), (float)bodyCollider.getCenterY(), (float)jointCollision.getCenterX(), (float)jointCollision.getCenterY());
            collisionVec.normalize();

//            System.out.println(" dx: " + playerVec.x + " dy: " + playerVec.y + " col x: " + collisionVec.x + " col y: " + collisionVec.y);

            if(playerVec.dotProduct(collisionVec) > 0.7f){
                float yDot = yDir.dotProduct(collisionVec);
                float xDot = xDir.dotProduct(collisionVec);
                System.out.println(yDot + " " + xDot);

                if((yDot > 0.7f && playerMovement.y > 0) || (yDot < -0.7f && playerMovement.y < 0)){
                    playerMovement.y = 0;
                }

                if((xDot > 0.7f && playerMovement.x > 0) || (xDot < -0.7f && playerMovement.x < 0)){
                    playerMovement.x = 0;
                }
            }
        } else { // gravity
            playerMovement.y += GRAVITY_PULL;
        }
    }

    public void render(Graphics g){
        if(mirrorPlayer){
            g.drawImage(animations[playerAction][aniIndex], (int)position.x+width, (int)position.y, -width, height, null);
        } else {
            g.drawImage(animations[playerAction][aniIndex], (int)position.x, (int)position.y, width, height, null);
        }

        // DEBUG DRAW
        g.setColor(Color.PINK);
        g.drawRect(bodyCollider.x, bodyCollider.y, bodyCollider.width, bodyCollider.height);

        if(collidedObjectDebug != null){
            for(Rectangle rect : collidedObjectDebug){
                g.drawRect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        g.setColor(Color.BLUE);
        g.drawLine((int) bodyCollider.getCenterX(), (int) bodyCollider.getCenterY(), (int) (bodyCollider.getCenterX() + playerMovement.x * 50f), (int) (bodyCollider.getCenterY() + playerMovement.y * 50f));

        g.setColor(Color.GREEN);
        g.drawRect(jointCollision.x, jointCollision.y, jointCollision.width, jointCollision.height);
    }

    private void setAnimation(){
        if(moving && playerSpeed == WALKING_SPEED){
            playerAction = WALKING;
        } else if(moving && playerSpeed == RUNNING_SPEED){
            playerAction = RUNNING;
        }
        else {
            playerAction = IDLE;
        }
        checkAnimationIndexOverflow();
    }

    public void runningAction(boolean status){
        if(status){
            playerSpeed = RUNNING_SPEED;
        } else {
            playerSpeed = WALKING_SPEED;
        }
    }

    private void loadAnimations() {
        InputStream is = getClass().getResourceAsStream("/player-spritesheet.png");
        try {
            BufferedImage img = ImageIO.read(is);

            animations = new BufferedImage[3][4];
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

    public void setMovementDir(int dir, boolean status){
        movementDir[dir] = status;
    }

    public void resetMovement(){
        movementDir = new boolean[4];
    }

    public vec2d getPosition(){
        return position;
    }

    public vec2d getMovementDir(){
        return playerMovement.multiplyVec(playerMovement);
    }
}
