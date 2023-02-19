package input;

import core.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static utils.Constants.PlayerConstants.MovementDir.*;

public class KeyboardInputs implements KeyListener {
    private final GamePanel gamePanel;
    public KeyboardInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_W -> gamePanel.getApp().getPlayer().setMovementDir(UP, true);
            case KeyEvent.VK_D -> gamePanel.getApp().getPlayer().setMovementDir(RIGHT, true);
            case KeyEvent.VK_S -> gamePanel.getApp().getPlayer().setMovementDir(DOWN, true);
            case KeyEvent.VK_A -> gamePanel.getApp().getPlayer().setMovementDir(LEFT, true);
            case KeyEvent.VK_SHIFT -> gamePanel.getApp().getPlayer().runningAction(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_W -> gamePanel.getApp().getPlayer().setMovementDir(UP, false);
            case KeyEvent.VK_D -> gamePanel.getApp().getPlayer().setMovementDir(RIGHT, false);
            case KeyEvent.VK_S -> gamePanel.getApp().getPlayer().setMovementDir(DOWN, false);
            case KeyEvent.VK_A -> gamePanel.getApp().getPlayer().setMovementDir(LEFT, false);
            case KeyEvent.VK_SHIFT -> gamePanel.getApp().getPlayer().runningAction(false);
        }
    }
}
