package utils;

import javafx.geometry.Dimension2D;
import utils.math.Vec2D;

public class PhysicsComponent {
    private Vec2D position;
    private Vec2D acceleration;
    private Dimension2D dimension;

    public PhysicsComponent(double x, double y, double width, double height){
        this.position = new Vec2D(x, y);
        this.dimension = new Dimension2D(width, height);
        this.acceleration = new Vec2D(0, 0);
    }

    public Vec2D getRenderPosition(){
        return new Vec2D((double) Constants.SCR.WIDTH /2 + position.x() - dimension.getWidth()/2,
                (double) Constants.SCR.HEIGHT /2 - position.y() - dimension.getHeight()/2);
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
    }

    public void pushBody(Vec2D vec){
        if(vec == null) return;
        acceleration = vec;
    }

    public void setPosition(Vec2D vec){
        position = vec;
    }

    public void setDimension(Dimension2D dim){
        dimension = dim;
    }

    public void update(){
        moveBody(acceleration);
        acceleration = acceleration.multiply(new Vec2D(0.9, 0.7));
    }
}
