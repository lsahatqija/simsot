package simsot.game.player;

import simsot.game.Assets;
import simsot.game.PacManConstants;

public class Blinky extends Player {

    public Blinky(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
        characterLeft1 = Assets.blinkyLeft1;
        characterLeft2 = Assets.blinkyLeft2;
        characterRight1 = Assets.blinkyRight1;
        characterRight2 = Assets.blinkyRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
    }

    public Blinky(int centerX, int centerY, String mode, String playerName, int movespeed){
        super(centerX, centerY, mode, playerName, movespeed);
        characterLeft1 = Assets.blinkyLeft1;
        characterLeft2 = Assets.blinkyLeft2;
        characterRight1 = Assets.blinkyRight1;
        characterRight2 = Assets.blinkyRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
        //currentSprite = characterLeft1;
    }

    @Override
    public String getCharacter() {
        return PacManConstants.BLINKY;
    }
}