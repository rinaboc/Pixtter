package input;

import core.Main;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import utils.Constants;

public class InputManager {

    public InputManager(Main main) {
        Scene scene = main.getScene();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                switch(event.getCode()) {
                    case UP: main.getPlayer().setDirection(Constants.DIR.UP, true); break;
                    case DOWN: main.getPlayer().setDirection(Constants.DIR.DOWN, true); break;
                    case RIGHT: main.getPlayer().setDirection(Constants.DIR.RIGHT, true); break;
                    case LEFT: main.getPlayer().setDirection(Constants.DIR.LEFT, true); break;
                    case F: main.getPlayer().attack(); break;
                    default: break;
                }

            }

        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                switch(event.getCode()) {
                    case UP: main.getPlayer().setDirection(Constants.DIR.UP, false); break;
                    case DOWN: main.getPlayer().setDirection(Constants.DIR.DOWN, false); break;
                    case RIGHT: main.getPlayer().setDirection(Constants.DIR.RIGHT, false); break;
                    case LEFT: main.getPlayer().setDirection(Constants.DIR.LEFT, false); break;
                    default: break;
                }
            }

        });
    }
}
