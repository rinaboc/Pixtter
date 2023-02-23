package entities;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Vector;

import static utils.Constants.LevelConstants.TILE_SIZE;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.MovementDir.*;

public abstract class MovingEntity extends Entity{

    protected Application app;

    protected Rectangle collider;
    protected boolean[] movementDir = new boolean[4];
    protected vec2d movementVec = new vec2d(0,0);
    protected float movementSpeed;

    protected Vector<Rectangle> collidedObjectDebug = new Vector<>();

    public MovingEntity(Application app, float x, float y, int width, int height) {
        super(x, y, width, height);
        this.app = app;
        collider = new Rectangle((int) x, (int) y, width, height);
    }

    protected abstract void updatePosition();

    protected void collisionHandler(){
        Vector<Rectangle> collidedObject = app.getLevelManager().collisionTrigger(collider);
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

                // up direction
                innerCollider.setLocation(collider.x + 1, collider.y - TILE_SIZE/2);
                if(movementVec.y < 0 && rect.intersects(innerCollider)){
                    movementVec.y = 0;
                }

                // stairs auto bump
                if(collider.y + collider.height - rect.y > 1 && collider.y + collider.height - rect.y <= TILE_SIZE + 1 && movementVec.x == 0){
                    movementVec.y -= BUMP_FORCE;
                    if(movementDir[LEFT]){
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
