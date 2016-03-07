package simsot.game;

import android.graphics.Rect;

import simsot.socket.MySocket;

public class Pinky extends Player {

    public Pinky(int centerX, int centerY, String mode, String playerName, String roomName, MySocket mySocket){
        super(centerX, centerY, mode, playerName, roomName, mySocket);
        characterLeft1 = Assets.pinkyLeft1;
        characterLeft2 = Assets.pinkyLeft2;
        characterRight1 = Assets.pinkyRight1;
        characterRight2 = Assets.pinkyRight2;
        //currentSprite = characterLeft1;
    }

}
