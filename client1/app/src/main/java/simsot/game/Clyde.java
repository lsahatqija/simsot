package simsot.game;

import simsot.socket.MySocket;

public class Clyde extends Player {

    public Clyde(int centerX, int centerY, String mode, String playerName, String roomName, MySocket mySocket){
        super(centerX, centerY, mode, playerName, roomName, mySocket);
        characterLeft1 = Assets.clydeLeft1;
        characterLeft2 = Assets.clydeLeft2;
        characterRight1 = Assets.clydeRight1;
        characterRight2 = Assets.clydeRight2;
        //currentSprite = characterLeft1;
    }

}