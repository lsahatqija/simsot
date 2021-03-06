package simsot.game.player;

import simsot.game.Assets;
import simsot.game.PacManConstants;

public class Clyde extends Player {

    public Clyde(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
        characterLeft1 = Assets.clydeLeft1;
        characterLeft2 = Assets.clydeLeft2;
        characterRight1 = Assets.clydeRight1;
        characterRight2 = Assets.clydeRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
    }

    public Clyde(int centerX, int centerY, String mode, String playerName, int movespeed){
        super(centerX, centerY, mode, playerName, movespeed);
        characterLeft1 = Assets.clydeLeft1;
        characterLeft2 = Assets.clydeLeft2;
        characterRight1 = Assets.clydeRight1;
        characterRight2 = Assets.clydeRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
        //currentSprite = characterLeft1;
    }

    @Override
    public String getCharacter() {
        return PacManConstants.CLYDE;
    }

}