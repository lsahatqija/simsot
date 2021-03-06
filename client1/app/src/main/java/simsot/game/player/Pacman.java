package simsot.game.player;

import simsot.game.Assets;
import simsot.game.PacManConstants;

public class Pacman extends Player {

    public Pacman(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
        characterLeft1 = Assets.characterLeft1;
        characterLeft2 = Assets.characterLeft2;
        characterRight1 = Assets.characterRight1;
        characterRight2 = Assets.characterRight2;
        vulnerableMode = Assets.powerModeGhost;
        vulnerable = false;
        lives = 3;
        //currentSprite = characterLeft1;
    }

    @Override
    public String getCharacter() {
        return PacManConstants.PACMAN;
    }
}
