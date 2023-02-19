package core;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class GameWindow extends JFrame {

    public GameWindow(String title, GamePanel panel){
        super(title);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

        this.add(panel);
        this.pack();

        this.setLocationRelativeTo(null);
        this.setVisible(true);

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                panel.getApp().windowFocusLost();
            }
        });
    }
}
