package entities;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Constants;
import utils.graphic.Renderable;

public class Line implements Renderable {

    private double x, y, x2, y2;
    private double xOffset, yOffset;

    public Line(double x, double y, double x2, double y2) {
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

    public Point2D getPosition() {
        return new Point2D(x, y);
    }
}
