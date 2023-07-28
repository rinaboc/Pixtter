package utils;

import core.Main;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import utils.graphic.Renderable;
import utils.Constants.SCR;
import utils.math.Vec2D;

import java.util.Timer;
import java.util.TimerTask;

public class CameraComponent extends Group implements Updateable {

    private final Main main;
    private final Canvas canvas;
    private final GraphicsContext graphics;
    private Vec2D position;
    private PhysicsComponent followedObject;

    public CameraComponent(Main main) {
        this.main = main;
        this.canvas = new Canvas(SCR.WIDTH, SCR.HEIGHT);
        this.canvas.getGraphicsContext2D().setImageSmoothing(false);
        this.position = new Vec2D(0, 0);

        this.getChildren().add(canvas);
        this.graphics = this.canvas.getGraphicsContext2D();
        followedObject = null;
    }

    public void attachObject(PhysicsComponent o){
        followedObject = o;
        main.addUpdateComponent(this);
    }

    private void calculateOffset(Renderable object) {
        object.setOffset(-position.x(), position.y());
    }

    public void renderDebug() {
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Renderable element : main.getRenderComponents()) {
            calculateOffset(element);
            element.render(graphics);
        }
    }

    public void moveCamera(Vec2D vec) {
        this.position = new Vec2D(vec);
    }

    @Override
    public void update() {
        if(followedObject == null) return;

        Vec2D distVec = new Vec2D(followedObject.getPosition());
        distVec.subtract(position);

        if(distVec.length() > (double) SCR.HEIGHT / 3 ){
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
                    step = movementVec.multiply(new Vec2D((double) 1 /smoothness, (double) 1 /smoothness));
                }
            }, 0, 5);
        }
    }

    public static Vec2D getRenderPosition(double x, double y, double w, double h){
        return new Vec2D((double) Constants.SCR.WIDTH /2 + x - w/2,
                (double) Constants.SCR.HEIGHT /2 - y - h/2);
    }
}
