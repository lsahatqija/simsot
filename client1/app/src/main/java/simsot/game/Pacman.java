package simsot.game;

import simsot.socket.MySocket;

public class Pacman extends Player {

    public Pacman(int centerX, int centerY, String mode, String playerName, String roomName, MySocket mySocket){
        super(centerX, centerY, mode, playerName, roomName, mySocket);
        characterLeft1 = Assets.characterLeft1;
        characterLeft2 = Assets.characterLeft2;
        characterRight1 = Assets.characterRight1;
        characterRight2 = Assets.characterRight2;
        //currentSprite = characterLeft1;
    }
}
