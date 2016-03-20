package simsot.game.player;

import simsot.game.Assets;
import simsot.game.PacManConstants;

public class Pinky extends Player {

    public Pinky(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
        characterLeft1 = Assets.pinkyLeft1;
        characterLeft2 = Assets.pinkyLeft2;
        characterRight1 = Assets.pinkyRight1;
        characterRight2 = Assets.pinkyRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
        //currentSprite = characterLeft1;
    }

    @Override
    public String getCharacter() {
        return PacManConstants.PINKY;
    }

}
