package entities;

import utils.math.vec2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected vec2d position;
    protected int width, height;

    public Entity(float x, float y, int width, int height){
        position = new vec2d(x, y);
        this.width = width;
        this.height = height;
    }

    public abstract void update();
    public abstract void render(Graphics g, int xOffset);

    public Rectangle getBounds(){
        return new Rectangle((int) position.x, (int) position.y, width, height);
    }

    public void setBounds(Rectangle rect){
        position.x = rect.x; position.y = rect.y;
        width = rect.width; height = rect.height;
    }
}
