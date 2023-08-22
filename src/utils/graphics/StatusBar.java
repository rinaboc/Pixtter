package utils.graphics;

import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.exceptions.EntryNotFound;
import utils.math.Vec2D;
import utils.simulation.PhysicsComponent;
import utils.simulation.Status;

public class StatusBar implements UIComponent{

    private final Status status;
    private final PhysicsComponent physicsComponent;
    private final Vec2D offset;
    private final Dimension2D dimensions;

    private double xOffset;
    private double yOffset;

    public StatusBar(Status status, PhysicsComponent physicsComponent, Vec2D offset, Dimension2D dimensions){
        this.status = status;
        this.physicsComponent = physicsComponent;
        this.offset = offset;
        this.dimensions = dimensions;
    }

    @Override
    public void render(GraphicsContext g) {
        Vec2D position = physicsComponent.getRenderPosition();

        g.setFill(Color.ALICEBLUE);
        g.fillRect((int) position.x() + offset.x() + xOffset, position.y() + offset.y() + yOffset, dimensions.getWidth(), dimensions.getHeight());

        g.setFill(Color.YELLOWGREEN);
        try {
            g.fillRect((int) (int) position.x() + offset.x() + xOffset, position.y() + offset.y() + yOffset, (int) (dimensions.getWidth() * status.getEntry("health").getValue()), dimensions.getHeight());
        } catch (EntryNotFound e) {
            System.out.println("Couldn't draw statusbar.");
        }
    }

    @Override
    public void setOffset(double x, double y) {
        this.xOffset = x;
        this.yOffset = y;
    }

    @Override
    public void update() {

    }
}
