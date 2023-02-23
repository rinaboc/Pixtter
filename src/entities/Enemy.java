package entities;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Random;

import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Enemy extends MovingEntity{

    private Random rnd = new Random();

    private int movementCD = 100;
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

        createRandomDirection();

        movementVec.y += GRAVITY_PULL;

        collisionHandler();

        position = position.addVec(movementVec);
        collider.setLocation((int) position.x, (int) position.y);
    }

    private void createRandomDirection() {
        if(movementCD != 0){
            return;
        }

        vec2d spawnVec = new vec2d(position, spawnPosition);

        if(spawnVec.getLength() > 100f){
            if(spawnVec.x > 0){
                movementVec.x = movementSpeed;
                movementDir[0] = false;
                movementDir[1] = true;
            } else {
                movementVec.x = -movementSpeed;
                movementDir[0] = true;
                movementDir[1] = false;
            }

            return;
        }

        movementCD = 500;
        int rndNum = rnd.nextInt(20);

        if(rndNum % 3 == 0) {
            movementVec.x = -movementSpeed;
            movementDir[0] = true;
            movementDir[1] = false;
        } else if(rndNum % 3 == 1) {
            movementVec.x = movementSpeed;
            movementDir[0] = false;
            movementDir[1] = true;
        } else {
            movementVec.x = 0;
            movementDir[0] = false;
            movementDir[1] = false;
        }
    }

}
