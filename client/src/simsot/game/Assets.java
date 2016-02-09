package simsot.game;

import simsot.framework.Image;
import simsot.framework.Music;
import simsot.framework.Sound;

public class Assets {
    
    public static Image menu, splash, background, character1, character2, characterMove1, characterMove2, 
    	currentSprite, characterwalk1, characterwalk2, enemy1, enemy2, enemy3;
    public static Image tiledirt, tilegrassTop, tilegrassBot, tilegrassLeft, tilegrassRight, characterJump, characterDown;
	public static Image tileTree, tileGrass;
    public static Image button;
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