package utils;

import core.Main;
import entities.Box;
import javafx.geometry.Dimension2D;
import utils.math.Vec2D;

import java.util.Vector;

public class PhysicsComponent implements Updateable {

    private final Main main;

    private Vec2D position;
    private Vec2D acceleration;
    private Dimension2D dimension;
    private boolean useGravity;

    private final Vector<Box> colliders;

    public PhysicsComponent(double x, double y, double width, double height, Main main){
        this.main = main;
        this.position = new Vec2D(x, y);
        this.dimension = new Dimension2D(width, height);
        this.acceleration = new Vec2D(0, 0);
        this.colliders = new Vector<>();

        main.addUpdateComponent(this);
    }


    public void removeFromUpdateLoop(){
        main.removeUpdateComponent(this);
    }

    public Vec2D getRenderPosition(){
        return CameraComponent.getRenderPosition(position.x(), position.y(), dimension.getWidth(), dimension.getHeight());
    }

    public Vec2D getPosition(){
        return position;
    }

    public Dimension2D getDimensions(){
        return dimension;
    }

    public void moveBody(Vec2D vec){
        if(vec == null) return;
        position.add(vec);

        for(Box collider : colliders){
            collider.moveBox(vec);
        }
    }

    public void pushBody(Vec2D vec){
        if(vec == null) return;
        acceleration.add(vec);
    }

    public void setPosition(Vec2D vec){
        position = vec;
    }

    public void setDimension(Dimension2D dim){
        dimension = dim;
    }

    public Vec2D getAcceleration() {
        return acceleration;
    }

    public void addCollider(double locX, double locY, double width, double height){
        Box collider = new Box(position.x() + locX, position.y() + locY, width, height, main);
        collider.setCollider(true);
        colliders.add(collider);
    }

    public void setUseGravity(boolean status){
        useGravity = status;
    }

    public void setColliderDebugDraw(boolean status){
        for(Box collider : colliders){
            collider.setRender(status);
        }
    }

    @Override
    public void update(){
        acceleration = acceleration.multiply(new Vec2D(0.7, 0.98)); // apply drag
        if(Math.abs(acceleration.x()) < 0.0001d)
            acceleration = acceleration.multiply(new Vec2D(0, 1d)); // rounding

        if(useGravity){
            acceleration.add(new Vec2D(0.0, -0.1));
        }

        // collision check
        if(!colliders.isEmpty()){
            for(Box collider: main.getColliderComponents()){

                for(Box thisCollider : colliders){
                    if(collider.equals(thisCollider)) continue;

                    Box nextBoxPosition = new Box(thisCollider);
                    nextBoxPosition.moveBox(acceleration);

                    if(nextBoxPosition.intersects(collider)){
                        Vec2D limitedAcceleration = new Vec2D(acceleration);

                        // xAxis collision check
                        nextBoxPosition = new Box(thisCollider);
                        nextBoxPosition.moveBox(new Vec2D(acceleration.x(), 0));
                        if(nextBoxPosition.intersects(collider)){
                            limitedAcceleration.subtract(new Vec2D(acceleration.x(), 0));
                        }

                        // yAxis collision check
                        nextBoxPosition = new Box(thisCollider);
                        nextBoxPosition.moveBox(new Vec2D(0, acceleration.y()));
                        if(nextBoxPosition.intersects(collider)){
                            limitedAcceleration.subtract(new Vec2D(0, acceleration.y()));
                        }

                        acceleration = new Vec2D(limitedAcceleration);
                        break;
                    }
                }
            }
        }

//        System.out.println(acceleration);
        moveBody(acceleration);
    }
}
