package utils;


import entities.Player;

import static utils.Constants.PlayerConstants.TURNING;

public class PlayerSprite extends AnimatedSprite {

    private final Player player;

    public PlayerSprite(Player player){
        super("/player-spritesheet.png", 5, 4, 50, 24);
        this.player = player;
    }

    @Override
    protected boolean checkAnimationIndexOverflow() {
        boolean overflow = super.checkAnimationIndexOverflow();
        if(overflow && animationAction == TURNING){
            player.setTurning(false);
        }

        return overflow;
    }
}
