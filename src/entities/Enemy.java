package entities;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Random;
import java.util.Vector;

import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.LevelConstants.TILE_SIZE;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Enemy extends Entity{
    private Application app;
    private Random rnd = new Random();
    private vec2d movementVec = new vec2d(0,0);
    private float movementSpeed = 0.4f;
    private Rectangle collider;
    private int movementCD = 100;
    private boolean[] movementDir = new boolean[2];

    private Vector<Rectangle> collidedObjectDebug = new Vector<>();

    public Enemy(Application app, float x, float y, int width, int height) {
        super(x, y, width, height);
        this.app = app;
        collider = new Rectangle((int) x, (int) y, width, height);
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

        if(movementCD != 0){
            movementCD--;
        }

        updatePosition();
    }

    private void updatePosition() {

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

    private void collisionHandler(){
        Vector<Rectangle> collidedObject = app.getLevelManager().hiddenCollisionTrigger(collider);
        collidedObjectDebug = collidedObject;

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
                    if(movementDir[0]){
                        movementVec.x = -movementSpeed;
                    } else {
                        movementVec.x = movementSpeed;
                    }
                }

            }
        }
    }

    @Override
    public void setBounds(Rectangle rect) {
        super.setBounds(rect);
        collider.setBounds(rect);
    }
}
