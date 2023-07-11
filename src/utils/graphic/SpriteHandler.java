package utils.graphic;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.FileInputStream;
import java.io.IOException;

public class SpriteHandler {

    private final Image[][] animationFrames;
    private int[][] animationSettings;
    private final boolean[] noLoop;
    private int aniTick, aniIndex;
    private int animationAction;
    private boolean stopped;

    public SpriteHandler(String path, int row, int col, int width, int height, int[][] aniSettings) {
        animationFrames = new Image[row][col];
        noLoop = new boolean[row];
        loadAnimations(path, width, height);

        if(aniSettings.length == row) {
            animationSettings = aniSettings;
        } else System.out.println("Wrong animation settings set.");
    }

    public void update(){
        updateAnimationTick();
    }

    public Image getImage(){
        return animationFrames[animationAction][aniIndex];
    }

    public void restartAnimation(){
        aniIndex = 0;
    }

    public boolean isEndOfAnimation(){
        return stopped;
    }

    public void setNonLoopAnimation(int index, boolean status){
        if(index >= noLoop.length || index < 0) {
            System.out.println("Index out of bounds. Cannot set non loop animation.");
            return;
        }
        noLoop[index] = status;
    }

    public void setAnimationAction(int animationAction){
        if(animationAction >= animationFrames.length || animationAction < 0) {
            System.out.println("Index out of bounds. Cannot set animation action.");
            return;
        }

        if(animationAction != this.animationAction){
            stopped = false;
        }
        this.animationAction = animationAction;
        checkAnimationIndexOverflow();
    }

    private void updateAnimationTick() {
        if(stopped) return;

        aniTick++;
        if(aniTick >= GetSpriteAnimationSpeed(animationAction)){
            aniTick = 0;
            aniIndex++;
            if(checkAnimationIndexOverflow() && noLoop[animationAction]){
                stopped = true;
            }
        }
    }

    private int GetSpriteAnimationSpeed(int animationAction) {
        if(animationAction >= animationSettings.length || animationAction < 0) {
            System.out.println("Index out of bounds. Cannot get animation settings.");
            return 0;
        }

        return animationSettings[animationAction][1];
    }

    protected boolean checkAnimationIndexOverflow(){
        if(aniIndex >= GetSpriteAmount(animationAction)){
            aniIndex = 0;
            return true;
        }

        return false;
    }

    private int GetSpriteAmount(int animationAction) {
        if(animationAction >= animationSettings.length || animationAction < 0) {
            System.out.println("Index out of bounds. Cannot get animation settings.");
            return 0;
        }

        return animationSettings[animationAction][0];
    }

    private void loadAnimations(String path, int width, int height) {
        try {
            Image image = new Image(new FileInputStream(path));
            PixelReader reader = image.getPixelReader();

            for (int j = 0; j < animationFrames.length; j++) {
                for (int i = 0; i < animationFrames[j].length; i++) {

                    WritableImage newImage = new WritableImage(reader, i*width, j*height, width, height);
                    animationFrames[j][i] = newImage;
                }
            }
        } catch (IOException e) {
            System.out.println("Cannot read spritesheet.");
            throw new RuntimeException(e);
        }
    }
}
