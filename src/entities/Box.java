package entities;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.PhysicsComponent;
import utils.graphic.Renderable;
import utils.math.Vec2D;

public class Box implements Renderable {

    private final PhysicsComponent physicsComponent;
    private double xOffset, yOffset;

    public Box(double x, double y, double w, double h) {
        physicsComponent = new PhysicsComponent(x, y, w, h);
    }

    @Override
    public void render(GraphicsContext g) {
        Vec2D position = physicsComponent.getRenderPosition();
        g.setFill(Color.PINK);
        g.fillRect(position.x() + xOffset, position.y() + yOffset, physicsComponent.getDimensions().getWidth(), physicsComponent.getDimensions().getHeight());
    }

    @Override
    public void setOffset(double x, double y) {
        this.xOffset = x;
        this.yOffset = y;

    }

    public void add(Box b){
        Vec2D position = physicsComponent.getPosition();
        Dimension2D dim = physicsComponent.getDimensions();

        Vec2D positionB = b.physicsComponent.getPosition();
        Dimension2D dimB = b.physicsComponent.getDimensions();

        double leftBound = Math.min(position.x() - dim.getWidth()/2, positionB.x() - dimB.getWidth()/2);
        double rightBound = Math.max(position.x() + dim.getWidth()/2, positionB.x() + dimB.getWidth()/2);
        double bottomBound = Math.min(position.y() - dim.getHeight()/2, positionB.y() - dimB.getHeight()/2);
        double topBound = Math.max(position.y() + dim.getHeight()/2, positionB.y() + dimB.getHeight()/2);

        // new pivot
        double x = leftBound + (rightBound - leftBound) / 2;
        double y = topBound - (topBound - bottomBound) / 2;

        physicsComponent.setPosition(new Vec2D(x, y));

        double width = Math.abs(rightBound - leftBound);
        double height = Math.abs(topBound - bottomBound);

        physicsComponent.setDimension(new Dimension2D(width, height));
    }

}
