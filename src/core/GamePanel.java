package core;

import input.KeyboardInputs;

import javax.swing.*;
import java.awt.*;

import static utils.Constants.GameDimensions.*;
import static utils.Constants.GamePalette.PALETTE;

public class GamePanel extends JPanel {

    private final Application app;

    public GamePanel(Application app){
        this.app = app;
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

        this.addKeyListener(new KeyboardInputs(this));
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        g.setColor(PALETTE[6]);
        g.fillRect(0,0, GAME_WIDTH, GAME_HEIGHT);

        app.render(g);

    }

    public Application getApp() {
        return app;
    }
}
