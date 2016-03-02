package simsot.game;

import android.graphics.Rect;

public class Blinky extends Player {


    public Blinky(int centerX, int centerY, String mode, String playerName){
        super(centerX, centerY, mode, playerName);
        characterLeft1 = Assets.blinkyLeft1;
        characterLeft2 = Assets.blinkyLeft2;
        characterRight1 = Assets.blinkyRight1;
        characterRight2 = Assets.blinkyRight2;
        //currentSprite = characterLeft1;
    }



    /*
    @Override
    public void update(){
        setCenterX(getCenterX() + getSpeedX());
        setCenterY(getCenterY() + getSpeedY());

        // Prevents going beyond X coordinate of 0 or 480
        if (getCenterX() + getSpeedX() <= 30) {
            setCenterX(31);
            setSpeedX(MOVESPEED);
        } else if (getCenterX() + getSpeedX() >= 480) {
            setCenterX(479);
            setSpeedX(-MOVESPEED);
        }

        if (getCenterY() + getSpeedY() <= 30) {
            setCenterY(31);
            setSpeedY(MOVESPEED);
        } else if (getCenterY() + getSpeedY() >= 800) {
            setCenterY(799);
            setSpeedY(-MOVESPEED);
        }

        if (getSpeedX() != 0 || getSpeedY() !=0){
            isMoving = true;
        } else {
            isMoving = false;
        }

        if(walkCounter < 100){
            walkCounter++;
        } else {
            walkCounter = 0;
        }

        // Collision
        rectX.set(getCenterX() - 15, getCenterY() - 10, getCenterX() + 15, getCenterY() + 10);
        rectY.set(getCenterX() - 10, getCenterY() - 15, getCenterX() + 10, getCenterY() + 15);

        // AI
        if(player.getCenterX() < this.getCenterX()){
            this.setSpeedX(-MOVESPEED);
        } else if(player.getCenterX() >= this.getCenterX()){
            this.setSpeedX(MOVESPEED);
        }
        if(player.getCenterY() < this.getCenterY()){
            this.setSpeedY(-MOVESPEED);
        } else if(player.getCenterY() >= this.getCenterY()){
            this.setSpeedY(MOVESPEED);
        }

    }*/


}