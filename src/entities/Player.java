package entities;

import core.Main;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import utils.*;
import utils.graphic.Renderable;
import utils.graphic.SpriteHandler;
import utils.math.Vec2D;

import java.util.Timer;
import java.util.TimerTask;

public final class Player implements Renderable, Updateable {

    public static class Actions {
        public static int IDLE = 0, WALK = 1, RUN = 2, FALL = 3; // looped animations
        public static int TURN = 4, ATTACK = 5; // non looped animations
    }

    private final double movementSpeed = 0.8d;
    private final double jumpPower = 5.2d;
    private final boolean[] direction = new boolean[4];
    private double xOffset, yOffset;

    private boolean moving = false, mirrorPlayer = false, turning = false, attack = false, canJump = true;

    private final SpriteHandler spriteHandler;
    private final PhysicsComponent physicsComponent;

    public Player(double x, double y, double w, double h, Main main) {
        physicsComponent = new PhysicsComponent(x, y, w, h, main);
        physicsComponent.setUseGravity(true);
        physicsComponent.addCollider(0, 0, 40, 24);
        physicsComponent.setColliderDebugDraw(false);
        main.addUpdateComponent(this);

        this.spriteHandler = new SpriteHandler("res/player-spritesheet.png", 6, 4, 50, 24,
                new int[][] {{3, 12}, {4, 12}, {3, 12}, {3, 12}, {3, 4}, {4, 8}});
        this.spriteHandler.setNonLoopAnimation(4, true);
        this.spriteHandler.setNonLoopAnimation(5, true);
    }

    @Override
    public void update() {
        boolean wasMirror = mirrorPlayer;
        if(isMoving()){
            double xAxis = 0; double yAxis = 0;

            if(direction[Constants.DIR.RIGHT.getIndex()] && !direction[Constants.DIR.LEFT.getIndex()]) {
                xAxis = 1d;
                mirrorPlayer = false;
                moving = true;
            }
            else if(direction[Constants.DIR.RIGHT.getIndex()] != direction[Constants.DIR.LEFT.getIndex()]) {
                xAxis = -1d;
                mirrorPlayer = true;
                moving = true;
            }

            if(direction[Constants.DIR.UP.getIndex()] && !direction[Constants.DIR.DOWN.getIndex()] && canJump) {
                yAxis = 1d;
                canJump = false;
            }

            else if(!direction[Constants.DIR.UP.getIndex()] && direction[Constants.DIR.DOWN.getIndex()] && physicsComponent.getAcceleration().y() != 0d)
                yAxis = -0.05d;

            physicsComponent.pushBody(new Vec2D(xAxis * movementSpeed, yAxis * jumpPower));

            // check if player wants to go in opposite direction -> apply turning animation
            if(wasMirror != mirrorPlayer){
                turning = true;
                spriteHandler.restartAnimation();
            }

        } else moving = false;

        if(!canJump && Math.abs(physicsComponent.getAcceleration().y()) == 0d){
            canJump = true;
        }
    }

    private void setAnimation(){
        if(turning){
            spriteHandler.setAnimationAction(Actions.TURN);
            if(spriteHandler.isEndOfAnimation()) turning = false;
            else return;
        }

        if(attack){
            spriteHandler.setAnimationAction(Actions.ATTACK);
            if(spriteHandler.isEndOfAnimation()) attack = false;
            else return;
        }

        if(moving){
            spriteHandler.setAnimationAction(Actions.WALK);
        } else
            spriteHandler.setAnimationAction(Actions.IDLE);

        if(physicsComponent.getAcceleration().y() != 0){
            spriteHandler.setAnimationAction(Actions.FALL);
        }

    }

    @Override
    public void render(GraphicsContext g) {

        this.spriteHandler.update();
        setAnimation();

        Vec2D position = physicsComponent.getRenderPosition();

        if(mirrorPlayer && !turning || (!mirrorPlayer && turning)){
            g.drawImage(spriteHandler.getImage(), position.x() + physicsComponent.getDimensions().getWidth() + xOffset, position.y() + yOffset, -physicsComponent.getDimensions().getWidth(), physicsComponent.getDimensions().getHeight());
        } else {
            g.drawImage(spriteHandler.getImage(), position.x() + xOffset, position.y() + yOffset, physicsComponent.getDimensions().getWidth(), physicsComponent.getDimensions().getHeight());
        }

    }

    private boolean isMoving(){
        for (boolean b : direction) {
            if(b) return true;
        }
        return false;
    }

    @Override
    public void setOffset(double x, double y) {
        this.xOffset = x;
        this.yOffset = y;
    }

    public void setDirection(Constants.DIR dir, boolean status){
        if(!dir.equals(Constants.DIR.NONE)){
            direction[dir.getIndex()] = status;
        }
    }

    public void attack(){
        attack = true;
        spriteHandler.restartAnimation();
    }

    public PhysicsComponent getPhysicsComponent(){
        return physicsComponent;
    }

}
