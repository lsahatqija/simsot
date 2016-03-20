package simsot.game;

import simsot.socket.MySocket;

public class Clyde extends Player {

    public Clyde(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
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