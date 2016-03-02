package simsot.game;

import android.graphics.Rect;

public class Pinky extends Player {

    public Pinky(int centerX, int centerY, String mode){
        super(centerX, centerY, mode);
        characterLeft1 = Assets.pinkyLeft1;
        characterLeft2 = Assets.pinkyLeft2;
        characterRight1 = Assets.pinkyRight1;
        characterRight2 = Assets.pinkyRight2;
        currentSprite = characterLeft1;
    }

}
