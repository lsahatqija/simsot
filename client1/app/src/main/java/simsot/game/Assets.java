package simsot.game;

import simsot.framework.Image;
import simsot.framework.Music;
import simsot.framework.Sound;

public class Assets {
    
    public static Image menu, splash, background;

    public static Image characterLeft1, characterLeft2, characterRight1, characterRight2, characterUp1, characterUp2, characterDown1, characterDown2, characterClosed;
    public static Image enemyLeft1, enemyLeft2, enemyRight1, enemyRight2;
    public static Image inkyLeft1, inkyLeft2, inkyRight1, inkyRight2;
    public static Image pinkyLeft1, pinkyLeft2, pinkyRight1, pinkyRight2;
    public static Image blinkyLeft1, blinkyLeft2, blinkyRight1, blinkyRight2;
    public static Image clydeLeft1, clydeLeft2, clydeRight1, clydeRight2;
    public static Image playerSelectionLocal, playerSelectionRemote;
    public static Image powerModeGhost;
    public static Image joypad, joystick;

    public static Image pacmanSelection, inkySelection, pinkySelection, blinkySelection, clydeSelection;

    public static Image pelletSprite;
    public static Image powerPelletSprite;

	public static Image tileTree, tileGrass;

    public static Image buttonUp, buttonDown, buttonRight, buttonLeft, buttonPause;
    public static Sound click;
    public static Music theme;
    
    public static void load(SampleGame sampleGame) {
        /*theme = sampleGame.getAudio().createMusic("menutheme.mp3");
        theme.setLooping(true);
        theme.setVolume(0.85f);
        theme.play();*/
    }
}