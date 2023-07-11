package levels;

import core.Main;

public class LevelManager {
    private Main main;
    private Level level1;

    public LevelManager(Main main){
        this.main = main;
        level1 = new Level(main, "resource/level1.png");
    }

    public void update(){

    }
}
