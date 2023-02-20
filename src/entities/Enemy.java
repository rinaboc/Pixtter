package entities;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Random;
import java.util.Vector;

import static utils.Constants.LevelConstants.TILE_SIZE;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Enemy extends Entity{
    private Application app;
    private Random rnd = new Random();
    private vec2d movementVec = new vec2d(0,0);
    private float movementSpeed = 10f;
    private Rectangle collider;
    private int movementCD = 100;

    public Enemy(Application app, float x, float y, int width, int height) {
        super(x, y, width, height);
        this.app = app;
        collider = new Rectangle((int) x, (int) y, width, height);
    }

    @Override
    public void render(Graphics g){
        g.drawRect((int) position.x, (int) position.y, width, height);
    }

    @Override
    public void update(){

        if(movementCD != 0){
            movementCD--;
        }

        updatePosition();
    }

    private void updatePosition() {

        int rndNum = rnd.nextInt(20);

        if(movementCD == 0) {
            System.out.println("left");
            movementVec.x -= movementSpeed;
            movementCD = 500;
        }

        movementVec.y += GRAVITY_PULL;

        collisionHandler();

        // moving on the map
        if(!movementVec.nullVec()){
            // apply drags
            movementVec.x *= GROUND_DRAG;

        }

        position.x += movementVec.x;
    }

    private void collisionHandler(){
        Vector<Rectangle> collidedObject = app.getLevelManager().collisionTrigger(collider);

        if(collidedObject.size() > 0){

            for(Rectangle rect : collidedObject){

                Rectangle innerCollider = new Rectangle(collider);
                innerCollider.grow(-2, -2);

                // left direction
                innerCollider.setLocation(collider.x - TILE_SIZE/2, collider.y + 1);
                if(movementVec.x < 0 && rect.intersects(innerCollider)){
                    movementVec.x = 0;
                }

                // right direction
                innerCollider.setLocation(collider.x + TILE_SIZE/2, collider.y + 1);
                if(movementVec.x > 0 && rect.intersects(innerCollider)){
                    movementVec.x = 0;
                }

                // down direction
                innerCollider.setLocation(collider.x + 1, collider.y + TILE_SIZE/2);
                if(movementVec.y > 0 && rect.intersects(innerCollider)){
                    movementVec.y = 0;
                }

                // stairs auto bump
                if(collider.y + collider.height - rect.y > 1 && collider.y + collider.height - rect.y <= TILE_SIZE + 1 && movementVec.x == 0){
                    movementVec.y -= BUMP_FORCE;
                }

            }
        }
    }

}
