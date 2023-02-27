package levels;

import core.Application;
import entities.Entity;
import utils.math.vec2d;

import java.awt.*;
import java.util.Vector;


public class LevelManager {
    private Application app;
    private Level level1;

    public LevelManager(Application app){
        this.app = app;
        level1 = new Level("/level1.png", app);
    }

    public void render(Graphics g){
        level1.render(g);
    }

    public void update(){
        level1.update();
    }

    public void scrollLevel(vec2d v2){
        level1.setDrawingSpace(v2);
    }

    public Vector<Rectangle> collisionTrigger(Rectangle bounds){
        return level1.collisionTrigger(bounds);
    }

    public Vector<Entity> entityTrigger(Rectangle bounds){
        return level1.entityTrigger(bounds);
    }

    public boolean atBorderOfLevel(){
        return !level1.borderOfLevel();
    }

    public Rectangle getOffset(){
        return level1.getOffset();
    }
}
