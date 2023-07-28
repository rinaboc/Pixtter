package entities;

import core.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import utils.graphic.Renderable;
import utils.math.Vec2D;

import static utils.CameraComponent.getRenderPosition;

public class Box implements Renderable {

    private final Main main;
    private double x, y;
    private double width, height;
    private double xOffset, yOffset;

    private boolean rendering;
    private boolean collider;

    public Box(double x, double y, double w, double h, Main main) {
        this.main = main;
        this.x = x; this.y = y;
        this.width = w; this.height = h;
    }

    public Box(Box box){
        this.main = box.main;
        this.x = box.x; this.y = box.y;
        this.width = box.width; this.height = box.height;
        this.xOffset = box.xOffset; this.yOffset = box.yOffset;

        this.rendering = box.rendering;
        this.collider = box.collider;
    }

    public void setRender(boolean status){
        if(status && !rendering){
            main.addRenderComponent(this);
            rendering = true;
        } else {
            main.removeRenderComponent(this);
            rendering = false;
        }
    }

    public void setCollider(boolean status){
        collider = status;
        if(status) main.addColliderComponent(this);
        else main.removeColliderComponent(this);
    }

    public boolean isCollider(){return collider;}

    @Override
    public void render(GraphicsContext g) {

        Vec2D position = getRenderPosition(x, y, width, height);

        g.setFill(Color.BROWN);
        g.fillRect(position.x() + xOffset, position.y() + yOffset, width, height);

        if(collider){
            g.setStroke(Color.YELLOWGREEN);
            g.strokeRect(position.x() + xOffset, position.y() + yOffset, width, height);
        }
    }

    @Override
    public void setOffset(double x, double y) {
        this.xOffset = x;
        this.yOffset = y;

    }

    public void add(Box b){

        double leftBound = Math.min(x - width/2, b.x - b.width/2);
        double rightBound = Math.max(x + width/2, b.x + b.width/2);
        double bottomBound = Math.min(y - height/2, b.y - b.height/2);
        double topBound = Math.max(y + height/2, b.y + b.height/2);

        // new pivot
        this.x = leftBound + (rightBound - leftBound) / 2;
        this.y = topBound - (topBound - bottomBound) / 2;

        this.width = Math.abs(rightBound - leftBound);
        this.height = Math.abs(topBound - bottomBound);
    }

    public void moveBox(Vec2D vec){
        this.x += vec.x();
        this.y += vec.y();
    }

    public boolean intersects(Box b){
        Rectangle rec = new Rectangle(x - width/2, y - height/2, width, height);
        return rec.intersects(b.x - b.width/2, b.y - b.height/2, b.width, b.height);

    }

    public Vec2D getPosition(){
        return new Vec2D(x, y);
    }

}
