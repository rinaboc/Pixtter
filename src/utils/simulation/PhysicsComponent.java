package utils.simulation;

import core.Main;
import entities.Box;
import javafx.geometry.Dimension2D;
import utils.graphics.CameraComponent;
import utils.math.Vec2D;

import java.util.Vector;

import static levels.Level.TILE_SIZE;
import static utils.Constants.PHYS.GRAVITY;

public class PhysicsComponent implements Updateable {

    private final Main main;

    private Vec2D position;
    private Dimension2D dimension;
    private Vec2D acceleration;

    // simulation variables
    private boolean useGravity = false;
    private double bump = 0d;
    private Vec2D drag = new Vec2D(1d, 1d);
    private final Vector<Box> colliders = new Vector<>();

    public PhysicsComponent(double x, double y, double width, double height, Main main){
        this.main = main;
        this.position = new Vec2D(x, y);
        this.dimension = new Dimension2D(width, height);
        this.acceleration = new Vec2D(0, 0);

        main.addUpdateComponent(this); // add to update loop
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
    public Vec2D getAcceleration() {
        return acceleration;
    }

    /**
     * Shifts component's position by the given vector.
     * @param vec
     */
    public void moveBody(Vec2D vec){
        if(vec == null) return;
        position.add(vec);

        for(Box collider : colliders){
            collider.moveBox(vec);
        }
    }

    /**
     * Create force in the direction of the given vector.
     * @param vec
     */
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

    /**
     * Creates a collider box for the physics component with the given parameters.
     * @param locX local x position (added to component's position)
     * @param locY local y position (added to component's position)
     * @param width
     * @param height
     */
    public void addCollider(double locX, double locY, double width, double height){
        Box collider = new Box(position.x() + locX, position.y() + locY, width, height, main);
        collider.setCollider(true);
        colliders.add(collider);
    }

    public void setUseGravity(boolean status){
        useGravity = status;
    }
    public void setBumpStrength(double strength){
        this.bump = strength;
    }
    public void setDrag(double x, double y){
        drag = new Vec2D(x, y);
    }

    /**
     * Add or remove collider objects from render loop.
     * @param status
     */
    public void setColliderDebugDraw(boolean status){
        for(Box collider : colliders){
            collider.setRender(status);
        }
    }

    @Override
    public void update(){
        acceleration = acceleration.multiply(drag); // apply drag
        if(Math.abs(acceleration.x()) < 0.0001d)
            acceleration = acceleration.multiply(new Vec2D(0, 1d)); // rounding

        if(useGravity){
            acceleration.add(new Vec2D(0.0, -GRAVITY));
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

                            double topDifference = (collider.getPosition().y() + collider.getDimension().getHeight()/2) -
                                    (thisCollider.getPosition().y() - thisCollider.getDimension().getHeight()/2);
                            if(bump != 0 && Math.abs(limitedAcceleration.y()) < 1d && Math.abs(topDifference) <= TILE_SIZE){
                                limitedAcceleration.add(new Vec2D(0, bump));
                            }
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

        moveBody(acceleration);
    }
}
