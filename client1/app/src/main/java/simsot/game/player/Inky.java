package simsot.game.player;

import simsot.game.Assets;
import simsot.game.PacManConstants;

public class Inky extends Player {

    public Inky(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
        characterLeft1 = Assets.inkyLeft1;
        characterLeft2 = Assets.inkyLeft2;
        characterRight1 = Assets.inkyRight1;
        characterRight2 = Assets.inkyRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
    }

    public Inky(int centerX, int centerY, String mode, String playerName, int movespeed){
        super(centerX, centerY, mode, playerName, movespeed);
        characterLeft1 = Assets.inkyLeft1;
        characterLeft2 = Assets.inkyLeft2;
        characterRight1 = Assets.inkyRight1;
        characterRight2 = Assets.inkyRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
        //currentSprite = characterLeft1;
    }

    @Override
    public String getCharacter() {
        return PacManConstants.INKY;
    }

}
