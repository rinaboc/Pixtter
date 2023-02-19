package levels;

import utils.math.vec2d;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static utils.Constants.GameDimensions.*;
import static utils.Constants.GamePalette.PALETTE;
import static utils.Constants.LevelConstants.*;

public class Level {
    private Color[][] elements;
    private Vector<Rectangle> collidableObjects;
    private Rectangle[] collArray;
    private Rectangle drawingSpace;
    private int mapEnd;

    Level(String map){
        collidableObjects = new Vector<>();
        collArray = new Rectangle[255];
        drawingSpace = new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
        loadMap(map);
    }

    public void render(Graphics g){

        g.setColor(PALETTE[7]);
        for(Rectangle rect : collidableObjects){
            if(drawingSpace.intersects(rect)){
                g.fillRect(rect.x - drawingSpace.x, rect.y, rect.width, rect.height);
            }
        }

        g.drawRect(0,GAME_HEIGHT - TILE_SIZE,TILE_SIZE, TILE_SIZE);

//        for (int j = 0; j < elements.length; j++) {
//            for (int i = 0; i < elements[j].length; i++) {
//                if(elements[j][i].getGreen() != 255){
//                    g.setColor(PALETTE[7]);
//                    g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
//
//                }
//                if(elements[j][i].getGreen() == 2){
//                    g.setColor(PALETTE[8]);
//                    g.fillOval(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
//                }
//            }
//        }
    }

    private void loadMap(String path){
        InputStream is = getClass().getResourceAsStream(path);
        try {
            BufferedImage img = ImageIO.read(is);
            System.out.println(img.getWidth() + " " + img.getHeight());
            mapEnd = img.getWidth() * TILE_SIZE;

            elements = new Color[img.getWidth()][img.getHeight()];
            System.out.println(elements.length + " " + elements[0].length);

            for (int j = 0; j < elements.length; j++) {
                for (int i = 0; i < elements[j].length; i++) {
                    elements[j][i] = Color.decode(Integer.toString(img.getRGB(j, i)));
                    int green = elements[j][i].getGreen();
                    if(green != 255){
                        collidableObjects.add(new Rectangle((j)*TILE_SIZE, (i-2)*TILE_SIZE, TILE_SIZE, TILE_SIZE));
                        if(collArray[green-1] == null){
                            collArray[green-1] = new Rectangle((j)*TILE_SIZE, (i-2)*TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                        collArray[green-1].add(new Rectangle((j)*TILE_SIZE, (i-2)*TILE_SIZE, TILE_SIZE, TILE_SIZE));
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
            Rectangle translatedRect = new Rectangle(rect);
            translatedRect.setLocation(translatedRect.x - drawingSpace.x, translatedRect.y);

            if(translatedRect.intersects(bounds)){
                ret.add(translatedRect);
            }
        }
        return ret;
    }

    public void setDrawingSpace(vec2d v2){
        drawingSpace.translate((int)(v2.x * SCROLL_SPEED), 0);

        if(drawingSpace.x < 0){
            drawingSpace.x = 0;
        } else if(drawingSpace.x + drawingSpace.width >= mapEnd){
            drawingSpace.x = mapEnd - drawingSpace.width;
        }
    }

    public boolean borderOfLevel(){

        if(drawingSpace.x <= 0 || drawingSpace.x + drawingSpace.width >= mapEnd){
            return true;
        }

        return false;
    }
}
