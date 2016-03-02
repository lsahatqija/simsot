package simsot.game;

import android.graphics.Rect;

public class Inky extends Player {

    public Inky(int centerX, int centerY, String mode){
        super(centerX, centerY, mode);
        characterLeft1 = Assets.inkyLeft1;
        characterLeft2 = Assets.inkyLeft2;
        characterRight1 = Assets.inkyRight1;
        characterRight2 = Assets.inkyRight2;
        currentSprite = characterLeft1;
    }

}
