package levels;

import core.Application;
import utils.math.vec2d;

import java.awt.*;
import java.util.Vector;

import static utils.Constants.GameDimensions.*;

public class LevelManager {
    private Application app;
    private Level level1;

    public LevelManager(Application app){
        this.app = app;
        level1 = new Level("/level1.png");
    }

    public void render(Graphics g){
        level1.render(g);
    }

    public void update(){

    }

    public void scrollLevel(vec2d v2){
        level1.setDrawingSpace(v2);
    }

    public Vector<Rectangle> collisionTrigger(Rectangle bounds){
        return level1.collisionTrigger(bounds);
    }

    public boolean canScroll(){
        return !level1.borderOfLevel();
    }
}
