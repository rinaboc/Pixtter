package utils.graphic;

import entities.Enemy;

import static utils.Constants.PlayerConstants.ATTACK1;

public class EnemySprite extends AnimatedSprite{
    private Enemy enemy;

    public EnemySprite(String path, int row, int col, int width, int height, Enemy enemy) {
        super(path, row, col, width, height);
        this.enemy = enemy;
    }

    @Override
    protected boolean checkAnimationIndexOverflow() {
        boolean overflow = super.checkAnimationIndexOverflow();

        if(overflow && animationAction == ATTACK1){
            enemy.setAttack(false);
        }

        return overflow;
    }
}
