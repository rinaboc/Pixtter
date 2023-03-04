package entities;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Random;

import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.PlayerConstants.MovementConstants.*;
import static utils.Constants.PlayerConstants.MovementDir.*;
import static utils.Constants.PlayerConstants.PhysicsConstants.*;

public class Enemy extends MovingEntity{

    private Random rnd = new Random();

    private int movementCD = 100;
    protected float spawnRadius = 300f;
    private vec2d spawnPosition;
    protected boolean attack = false;

    protected vec2d debugVectoPlayer = new vec2d(0,0);

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

        if(attackedCD != 0){
            attackedCD--;
            movementVec.x *= 0.9f;
        }

        determineDirection();

        movementVec.y += GRAVITY_PULL;

        collisionHandler();

        position = position.addVec(movementVec);
        collider.setLocation((int) position.x, (int) position.y);
    }

    private void determineDirection(){
        if(movementCD != 0 || attackedCD != 0){
            return;
        }

        vec2d centerPoint = new vec2d((float) collider.getCenterX(), (float) collider.getCenterY());

        vec2d vecToPlayer = new vec2d(centerPoint, app.getPlayer().getCenterPosition());
        vec2d spawnVec = new vec2d(centerPoint, spawnPosition);

        // player is in range -> follow player
        if(vecToPlayer.getLength() < spawnRadius){
            debugVectoPlayer = vecToPlayer;

            movementSpeed = 0.6f;

            movementDir[LEFT] = vecToPlayer.x < 0 && vecToPlayer.getLength() > 30f;
            movementDir[RIGHT] = vecToPlayer.x > 0 && vecToPlayer.getLength() > 30f;

            if(movementDir[RIGHT]){
                movementVec.x = movementSpeed;
            } else if(movementDir[LEFT]) {
                movementVec.x = -movementSpeed;
            }

            if(vecToPlayer.getLength() <= spawnRadius/3) {
                // attack player
                //  TODO
                //

                attackInit(vecToPlayer);
                movementVec.x = 0;
                movementVec.y -= BUMP_FORCE;
                movementCD = 150;
            } else {
                movementCD = 50;
            }


            return;
        }

        // normal speed and movement reaction slower
        movementSpeed = 0.3f;
        movementCD = 400;

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

    @Override
    public void attackHandler(vec2d attackerPosition) {
        super.attackHandler(attackerPosition);

        System.out.println("attack handling");
    }

    protected void attackInit(vec2d atkVec){
        app.getPlayer().attackHandler(position);

//        if(atkVec.x > 0){
//            movementVec.x = BUMP_FORCE;
//        } else {
//            movementVec.x = -BUMP_FORCE;
//        }

    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }
}
