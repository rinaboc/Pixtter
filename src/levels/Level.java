package levels;

import core.Main;
import entities.Box;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import utils.Constants;

import java.io.FileInputStream;
import java.io.IOException;

public class Level {
    private final Main main;
    private final Box[] collArray = new Box[255];
    private static int TILE_SIZE = (int) (8 * Constants.SCR.SCALE);

    public Level(Main main, String path){
        this.main = main;
        loadMap(path);
//        Box box = new Box(64, -16, 32, 32);
//        box.add(new Box(16, -16, 64, 32));
//        main.addRenderComponent(box);
    }

    private void loadMap(String path){
        try {
            Image img = new Image(new FileInputStream(path));
            PixelReader reader = img.getPixelReader();

            // store each non transparent pixel of the source map according to their separate RGB values
            // useful for indexing and map generation
            for (int j = 0; j < img.getWidth(); j++) {
                for (int i = 0; i < img.getHeight(); i++) {

                    // green value represents objects with collision
                    // rectangles get stored in the collision array at the corresponding index
                    int greenValue = (int)(reader.getColor(j, i).getGreen()*255);
                    if(greenValue != 255 && greenValue != 0){

                        double x = j - (double) Constants.SCR.WIDTH/2/TILE_SIZE;
                        double y = (double) Constants.SCR.HEIGHT/2/TILE_SIZE - i;

                        if(collArray[greenValue-1] == null){
                            Box tile = new Box(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE, main);
                            tile.setCollider(true);
                            tile.setRender(true);
                            collArray[greenValue-1] = tile;
                            continue;
                        }

                        collArray[greenValue-1].add(new Box(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE, main));
                    }

                    int redValue = (int)(reader.getColor(j, i).getRed()*255);
                    if(redValue != 255 && redValue != 0){
                        // TODO add enemy to scene

//                        if(entities[redValue-1] == null){
//                            entities[redValue-1] = new Wolf(app, (j)*TILE_SIZE, (i)*TILE_SIZE - 200);
//                            continue;
//                        }

//                        Rectangle newBound = new Rectangle(entities[redValue-1].getBounds());
//                        newBound.add(new Rectangle((j)*TILE_SIZE, (i)*TILE_SIZE, TILE_SIZE, TILE_SIZE));
//                        entities[redValue-1].setBounds(newBound);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
