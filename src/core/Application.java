package core;

import entities.Player;
import levels.LevelManager;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import static utils.Constants.GameDimensions.*;
import static utils.Constants.GamePalette.LoadPalette;

public class Application{

    private final GamePanel panel;

    private final int FPS_SET = 60;
    private final int UPS_SET = 120;
    private int framesCounter = 0, updatesCounter = 0;
    private final Timer[] gameTimers = new Timer[3];

    private Player player;
    private LevelManager levelManager;

    Application(){
        initClasses();

        panel = new GamePanel(this);
        new GameWindow("Pixtter", panel);
        panel.requestFocus();

        run();
    }

    private void initClasses() {
        LoadPalette();
        player = new Player((float)GAME_WIDTH/2 - 50, (float)GAME_HEIGHT/2, this);
        levelManager = new LevelManager(this);

        for (int i = 0; i < gameTimers.length; i++) {
            gameTimers[i] = new Timer();
        }
    }

    public void run(){

        double milliSecPerFrame = 1e3f / FPS_SET;
        double milliSecPerUpdate = 1e3f / UPS_SET;

        // frame update timer
        gameTimers[0].scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                panel.repaint();
                framesCounter++;
            }
        }, 0, (int)(milliSecPerFrame));

        // game logic update timer
        gameTimers[1].scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameUpdate();
                updatesCounter++;
            }
        }, 0, (int)(milliSecPerUpdate));

        // FPS & UPS counter timer
        gameTimers[2].scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("FPS: " + framesCounter + " | UPS: " + updatesCounter);
                framesCounter = updatesCounter = 0;
            }
        }, 0, 1000);
    }

    private void gameUpdate() {
        player.update();
        levelManager.update();
    }

    public void render(Graphics g){
        levelManager.render(g);
        player.render(g, levelManager.getOffset().x);
    }

    public void windowFocusLost(){
        player.resetMovement();
    }

    public Player getPlayer() {
        return player;
    }
    public LevelManager getLevelManager(){
        return levelManager;
    }
}