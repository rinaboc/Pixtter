package levels;

import core.Application;
import entities.Enemy;
import entities.Entity;
import utils.math.vec2d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static utils.Constants.GameDimensions.*;
import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.LevelConstants.*;

public class Level {
    private final Rectangle[] collArray;
    private final Rectangle drawingSpace;
    private int mapEnd;

    private Entity[] entities;

    Level(String map, Application app){
        collArray = new Rectangle[255];
        entities = new Entity[255];
        drawingSpace = new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
        loadMap(map, app);
    }

    public void update(){
        for (Entity entity: entities){
            if(entity == null) break;
            entity.update();
        }
    }

    public void render(Graphics g){

        g.setColor(PALETTE[7]);
        for(Rectangle rect : collArray){
            if(rect == null) break;

            if(drawingSpace.intersects(rect)){
                g.fillRect(rect.x - drawingSpace.x, rect.y, rect.width, rect.height);
            }
        }

        g.setColor(PALETTE[10]);
        for(Entity entity: entities){
            if(entity == null) break;
            entity.render(g, drawingSpace.x);
//            Rectangle bounds = entity.getBounds();
//            g.fillRect(bounds.x - drawingSpace.x, bounds.y, bounds.width, bounds.height);
        }

        g.drawRect(0,GAME_HEIGHT - TILE_SIZE,TILE_SIZE, TILE_SIZE);
    }

    private void loadMap(String path, Application app){
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage img = ImageIO.read(is);

            mapEnd = img.getWidth() * TILE_SIZE;

            // store each non transparent pixel of the source map according to their separate RGB values
            // useful for indexing and map generation
            for (int j = 0; j < img.getWidth(); j++) {
                for (int i = 0; i < img.getHeight(); i++) {

                    // green value represents objects with collision
                    // rectangles get stored in the collision array at the corresponding index
                    int greenValue = Color.decode(Integer.toString(img.getRGB(j, i))).getGreen();
                    if(greenValue != 255 && greenValue != 0){

                        if(collArray[greenValue-1] == null){
                            collArray[greenValue-1] = new Rectangle((j)*TILE_SIZE, (i)*TILE_SIZE, TILE_SIZE, TILE_SIZE);
                            continue;
                        }

                        collArray[greenValue-1].add(new Rectangle((j)*TILE_SIZE, (i)*TILE_SIZE, TILE_SIZE, TILE_SIZE));
                    }

                    int redValue = Color.decode(Integer.toString(img.getRGB(j, i))).getRed();
                    if(redValue != 255 && redValue != 0){
                        if(entities[redValue-1] == null){
                            entities[redValue-1] = new Enemy(app, (j)*TILE_SIZE, (i)*TILE_SIZE, TILE_SIZE, TILE_SIZE);
                            continue;
                        }

                        Rectangle newBound = new Rectangle(entities[redValue-1].getBounds());
                        newBound.add(new Rectangle((j)*TILE_SIZE, (i)*TILE_SIZE, TILE_SIZE, TILE_SIZE));
                        entities[redValue-1].setBounds(newBound);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public Vector<Rectangle> collisionTrigger(Rectangle bounds){
        Vector<Rectangle> ret = new Vector<>();

        for(Rectangle rect : collArray){
            if(rect == null) return ret;

            // position rectangle into view
            Rectangle translatedRect = new Rectangle(rect);
            translatedRect.setLocation(translatedRect.x - drawingSpace.x, translatedRect.y);

            if(translatedRect.intersects(bounds)){
                ret.add(translatedRect);
            }
        }
        return ret;
    }
    public Vector<Rectangle> hiddenCollisionTrigger(Rectangle bounds){
        Vector<Rectangle> ret = new Vector<>();

        for(Rectangle rect : collArray){
            if(rect == null) return ret;

            if(rect.intersects(bounds)){
                ret.add(rect);
            }
        }
        return ret;
    }

    // used for scrolling on the map
    public void setDrawingSpace(vec2d v2){
        drawingSpace.translate((int)(v2.x * SCROLL_SPEED), 0);

        // stop from scrolling out of map
        if(drawingSpace.x < 0){
            drawingSpace.x = 0;
        } else if(drawingSpace.x + drawingSpace.width >= mapEnd){
            drawingSpace.x = mapEnd - drawingSpace.width;
        }
    }

    // is the player camera at either end of the map
    public boolean borderOfLevel(){
        return drawingSpace.x <= 0 || drawingSpace.x + drawingSpace.width >= mapEnd;
    }
}
