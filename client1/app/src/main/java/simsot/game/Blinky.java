package simsot.game;

import simsot.socket.MySocket;

public class Blinky extends Player {


    public Blinky(int centerX, int centerY, String mode, String playerName, String roomName, MySocket mySocket){
        super(centerX, centerY, mode, playerName, roomName, mySocket);
        characterLeft1 = Assets.blinkyLeft1;
        characterLeft2 = Assets.blinkyLeft2;
        characterRight1 = Assets.blinkyRight1;
        characterRight2 = Assets.blinkyRight2;
        vulnerable = false;
        vulnerableMode = Assets.powerModeGhost;
        //currentSprite = characterLeft1;
    }
}