package utils.graphics;

import core.Main;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import utils.Constants;
import utils.simulation.Updateable;
import utils.Constants.SCR;
import utils.math.Vec2D;
import utils.simulation.PhysicsComponent;

import java.util.Timer;
import java.util.TimerTask;

public class CameraComponent extends Group implements Updateable {

    private final Main main;
    private final Canvas canvas; // game gets rendered on this
    private final GraphicsContext graphics;

    private Vec2D position = new Vec2D(0, 0);
    private Vec2D offset = new Vec2D(0, 0);
    private PhysicsComponent followedObject = null;
    private UIHandler uiHandler = null;

    public CameraComponent(Main main) {
        this.main = main;
        this.canvas = new Canvas(SCR.WIDTH, SCR.HEIGHT);
        this.canvas.getGraphicsContext2D().setImageSmoothing(false); // for pixel art

        this.getChildren().add(canvas);
        this.graphics = this.canvas.getGraphicsContext2D();
    }

    /**
     * Attach camera to a physics component. It will follow the attached object.
     * @param o
     */
    public void attachObject(PhysicsComponent o){
        followedObject = o;
        main.addUpdateComponent(this);
    }

    /**
     * Set renderable object's draw offset according to the camera's position.
     * @param object
     */
    private void calculateOffset(Renderable object) {
        object.setOffset(-position.x(), position.y());
    }

    /**
     * Renders the game scene.
     */
    public void renderScreen() {
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Renderable element : main.getRenderComponents()) {
            calculateOffset(element);
            element.render(graphics);
        }

        calculateOffset(uiHandler);
        uiHandler.render(graphics);
    }

    public void setPosition(Vec2D vec) {
        this.position = new Vec2D(vec);
    }

    /**
     * Set the camera's position compared to the followed object.
     * @param vec
     */
    public void setLocalOffset(Vec2D vec){
        if(followedObject == null) {
            System.out.println("No object attached. Cannot set offset.");
            return;
        }

        offset = vec;
    }

    @Override
    public void update() {
        if(followedObject == null) return;

        Vec2D distVec = new Vec2D(followedObject.getPosition());
        distVec.subtract(position);

        if(distVec.length() > (double) SCR.HEIGHT / 3 ){
            distVec.add(offset);
            Timer smoothCameraTimer = new Timer();
            smoothCameraTimer.scheduleAtFixedRate(new TimerTask() {

                private final int smoothness = 64;
                private Vec2D step = distVec.multiply(new Vec2D((double) 1 /smoothness, (double) 1 /smoothness));
                private Vec2D movementVec = distVec;

                @Override
                public void run() {
                    if(movementVec.length() < 1) this.cancel();
                    position.add(step);

                    movementVec = new Vec2D(followedObject.getPosition());
                    movementVec.subtract(position);
                    movementVec.add(offset);
                    step = movementVec.multiply(new Vec2D((double) 1 /smoothness, (double) 1 /smoothness));
//                    System.out.println(followedObject.getPosition().y() + " " + position.y());
                }
            }, 0, 5);
        }
    }

    public void enableUI(UIHandler uiHandler){
        this.uiHandler = uiHandler;
        this.getChildren().add(uiHandler);

        main.addUpdateComponent(uiHandler);
    }

    public void disableUI(){
        if(uiHandler == null){
            System.out.println("No UIHandler found. Cannot disable.");
            return;
        }

        main.removeUpdateComponent(uiHandler);
        this.getChildren().remove(uiHandler);
    }

    public UIHandler getUiHandler(){
        return uiHandler;
    }

    public static Vec2D getRenderPosition(double x, double y, double w, double h){
        return new Vec2D((double) Constants.SCR.WIDTH /2 + x - w/2,
                (double) Constants.SCR.HEIGHT /2 - y - h/2);
    }
}
