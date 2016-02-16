package simsot.game;

import simsot.framework.Image;
import simsot.framework.Music;
import simsot.framework.Sound;
import simsot.framework.Graphics.ImageFormat;

public class Assets {
    
    public static Image menu, splash, background, character1, characterLeft1, characterLeft2, characterRight1, 
    							characterRight2, characterUp1, characterUp2, characterDown1, characterDown2, characterClosed, enemyLeft1, enemyLeft2, enemyRight1, enemyRight2;
    //public Image currentSprite;
    public static Image tiledirt, tilegrassTop, tilegrassBot, tilegrassLeft, tilegrassRight, characterJump, characterDown;
	public static Image tileTree, tileGrass;
    public static Image buttonUp, buttonDown, buttonRight, buttonLeft, buttonPause;
    public static Sound click;
    public static Music theme;
    
    public static void load(SampleGame sampleGame) {
        // TODO Auto-generated method stub
        theme = sampleGame.getAudio().createMusic("menutheme.mp3");
        theme.setLooping(true);
        theme.setVolume(0.85f);
        theme.play();
    }
}