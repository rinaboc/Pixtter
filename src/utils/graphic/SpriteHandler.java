package utils.graphic;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Sprite handling object. Does all the things a sprite needs to do.
 */
public class SpriteHandler {

    private final Image[][] animationFrames; // source frames
    private int[][] animationSettings; // animation action -> {sprite amount, animation speed}
    private final boolean[] noLoop; // not looping animation flags
    private int aniTick, aniIndex;
    private int animationAction; // currently playing animation index
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

    /**
     * Get currently played animation frame.
     * @return
     */
    public Image getImage(){
        return animationFrames[animationAction][aniIndex];
    }

    /**
     * Start animation from the beginning.
     */
    public void restartAnimation(){
        aniIndex = 0;
    }

    public boolean isEndOfAnimation(){
        return stopped;
    }

    /**
     * Sets flag for non-looped animation.
     * @param index
     * @param status
     */
    public void setNonLoopAnimation(int index, boolean status){
        if(index >= noLoop.length || index < 0) {
            System.out.println("Index out of bounds. Cannot set non loop animation.");
            return;
        }
        noLoop[index] = status;
    }

    /**
     * Set currently played animation.
     * @param animationAction animation's index
     */
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

    /**
     * Trigger new animation tick and set frames according to the animation speed of current animation action.
     */
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

    /**
     * Reads image from source path and cuts it up according to given parameters at sprite constructor.
     * @param path source
     * @param width single frame width
     * @param height single frame height
     */
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
