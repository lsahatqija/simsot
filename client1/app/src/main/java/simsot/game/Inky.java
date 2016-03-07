package simsot.game;

import android.graphics.Rect;

import simsot.socket.MySocket;

public class Inky extends Player {

    public Inky(int centerX, int centerY, String mode, String playerName, String roomName, MySocket mySocket){
        super(centerX, centerY, mode, playerName, roomName, mySocket);
        characterLeft1 = Assets.inkyLeft1;
        characterLeft2 = Assets.inkyLeft2;
        characterRight1 = Assets.inkyRight1;
        characterRight2 = Assets.inkyRight2;
        //currentSprite = characterLeft1;
    }

}
