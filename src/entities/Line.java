package entities;

import core.Main;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Constants;
import utils.graphics.Renderable;

/**
 * Class primitive, mostly made for debugging.
 */
public class Line implements Renderable {

    private final Main main;

    // endpoints
    private double x, y;
    private double x2, y2;

    private double xOffset, yOffset;
    private boolean rendering;

    public Line(double x, double y, double x2, double y2, Main main) {
        this.main = main;
        this.x = x; this.y = -y;
        this.x2 = x2; this.y2 = -y2;
    }

    @Override
    public void render(GraphicsContext g) {
        g.setStroke(Color.LIGHTGREY);
        g.strokeLine((double) Constants.SCR.WIDTH /2 + x + xOffset, (double) Constants.SCR.HEIGHT /2 - y + yOffset,
                (double) Constants.SCR.WIDTH /2 + x2 + xOffset, (double) Constants.SCR.HEIGHT /2 - y2 + yOffset);
    }

    @Override
    public void setOffset(double x, double y) {
        this.xOffset = x;
        this.yOffset = y;
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

    public Point2D getPosition() {
        return new Point2D(x, y);
    }
}
