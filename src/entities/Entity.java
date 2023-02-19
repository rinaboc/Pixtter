package entities;

import utils.math.vec2d;

public abstract class Entity {
    protected vec2d position;
    protected int width, height;
    public Entity(float x, float y, int width, int height){
        position = new vec2d(x, y);
        this.width = width;
        this.height = height;
    }
}
