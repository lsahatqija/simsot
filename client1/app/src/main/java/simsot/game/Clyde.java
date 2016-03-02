package simsot.game;

import android.graphics.Rect;

public class Clyde extends Player {

    public Clyde(int centerX, int centerY, String mode){
        super(centerX, centerY, mode);
        characterLeft1 = Assets.clydeLeft1;
        characterLeft2 = Assets.clydeLeft2;
        characterRight1 = Assets.clydeRight1;
        characterRight2 = Assets.clydeRight2;
        //currentSprite = characterLeft1;
    }

}