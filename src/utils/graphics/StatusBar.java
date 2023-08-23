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
    private PhysicsComponent physicsComponent = null;
    private final Vec2D offset;
    private final Dimension2D dimensions;

    private final double lineWidth = 1.5d;

    private double xOffset;
    private double yOffset;

    public StatusBar(Status status, Vec2D offset, Dimension2D dimensions){
        this.status = status;
        this.offset = offset;
        this.dimensions = dimensions;
    }

    public void attachPhysicsComponent(PhysicsComponent physicsComponent){
        this.physicsComponent = physicsComponent;
    }

    @Override
    public void render(GraphicsContext g) {
        Vec2D position = new Vec2D(0, 0);

        // change position if the bar is attached to a component
        if(physicsComponent != null){
            position = physicsComponent.getRenderPosition();
            position.add(new Vec2D(xOffset, yOffset));
        }

        g.setFill(Color.YELLOWGREEN);
        g.fillRect((int) position.x() + offset.x() - lineWidth, position.y() + offset.y() - lineWidth, dimensions.getWidth() + lineWidth*2, dimensions.getHeight() + lineWidth*2);

        g.setFill(Color.ALICEBLUE);
        g.fillRect((int) position.x() + offset.x(), position.y() + offset.y(), dimensions.getWidth(), dimensions.getHeight());

        g.setFill(Color.YELLOWGREEN);
        try {
            g.fillRect((int) (int) position.x() + offset.x(), position.y() + offset.y(), (int) (dimensions.getWidth() * status.getEntry("health").getValue()), dimensions.getHeight());
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
