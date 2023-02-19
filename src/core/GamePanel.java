package core;

import input.KeyboardInputs;
import org.w3c.dom.css.RGBColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static utils.Constants.GameDimensions.*;
import static utils.Constants.GamePalette.PALETTE;

public class GamePanel extends JPanel {

    private Application app;

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
