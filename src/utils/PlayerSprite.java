package utils;


import entities.Player;

import static utils.Constants.PlayerConstants.ATTACK1;
import static utils.Constants.PlayerConstants.TURNING;

public class PlayerSprite extends AnimatedSprite {

    private final Player player;

    public PlayerSprite(Player player){
        super("/player-spritesheet.png", 6, 4, 50, 24);
        this.player = player;
    }

    @Override
    protected boolean checkAnimationIndexOverflow() {
        boolean overflow = super.checkAnimationIndexOverflow();
        if(overflow && animationAction == TURNING){
            player.setTurning(false);
        }

        if(overflow && animationAction == ATTACK1){
            player.setAttack(false);
        }

        return overflow;
    }
}
