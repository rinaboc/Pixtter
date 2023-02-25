package entities;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Random;

import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.PlayerConstants.MovementDir.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Enemy extends MovingEntity{

    private Random rnd = new Random();

    private int movementCD = 100;
    private final float spawnRadius = 300f;
    private vec2d spawnPosition;

    public Enemy(Application app, float x, float y, int width, int height) {
        super(app, x, y, width, height);
        movementSpeed = 0.4f;
        spawnPosition = new vec2d(x, y);
    }

    @Override
    public void render(Graphics g, int xOffset){
        g.setColor(PALETTE[11]);
        g.fillRect((int) position.x - xOffset, (int) position.y, width, height);

        for(Rectangle rect: collidedObjectDebug){
            g.drawRect(rect.x - xOffset , rect.y, rect.width, rect.height);
        }

        g.setColor(PALETTE[3]);
        g.drawRect(collider.x - xOffset, collider.y, collider.width, collider.height);
    }

    @Override
    public void update(){
        updatePosition();
    }

    protected void updatePosition() {

        if(movementCD != 0){
            movementCD--;
        }

        determineDirection();

        movementVec.y += GRAVITY_PULL;

        collisionHandler();

        position = position.addVec(movementVec);
        collider.setLocation((int) position.x, (int) position.y);
    }

    private void determineDirection(){
        if(movementCD != 0){
            return;
        }

        vec2d vecToPlayer = new vec2d(position, app.getPlayer().getPosition());
        vec2d spawnVec = new vec2d(position, spawnPosition);

        // player is in range -> follow player
        if(vecToPlayer.getLength() < spawnRadius){
            System.out.println("player in radius");

            movementSpeed = 0.6f;

            movementDir[LEFT] = vecToPlayer.x < 0;
            movementDir[RIGHT] = vecToPlayer.x > 0;

            if(movementDir[RIGHT]){
                movementVec.x = movementSpeed;
            } else {
                movementVec.x = -movementSpeed;
            }

            movementCD = 100;

            return;
        }

        // normal speed and movement reaction slower
        movementSpeed = 0.4f;
        movementCD = 500;

        // player not in range and enemy outside of spawn radius -> go back
        if(spawnVec.getLength() > spawnRadius){

            movementDir[LEFT] = spawnVec.x < 0;
            movementDir[RIGHT] = spawnVec.x > 0;

            if(movementDir[RIGHT]){
                movementVec.x = movementSpeed;
            } else {
                movementVec.x = -movementSpeed;
            }

            return;
        }

        int rndNum = rnd.nextInt(20);

        movementDir[LEFT] = rndNum % 3 == 0;
        movementDir[RIGHT] = rndNum % 3 == 1;

        if(rndNum % 3 == 0) {
            movementVec.x = -movementSpeed;
        } else if(rndNum % 3 == 1) {
            movementVec.x = movementSpeed;
        } else {
            movementVec.x = 0;
        }
    }

}
