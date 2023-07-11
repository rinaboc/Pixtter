package utils.graphic;

import javafx.scene.canvas.GraphicsContext;

public interface Renderable {
    public void render(GraphicsContext g);
    public void setOffset(double x, double y);
}
