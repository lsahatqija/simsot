package simsot.game;

import simsot.framework.Game;
import simsot.framework.Graphics;
import simsot.framework.Screen;
import simsot.framework.Graphics.ImageFormat;


public class LoadingScreen extends Screen {
	
	public LoadingScreen(Game game) {
        
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        
		Assets.background = g.newImage("background.png", ImageFormat.RGB565);
		Assets.characterClosed = g.newImage("characterclosed.png", ImageFormat.RGB565);
		Assets.characterLeft1 = g.newImage("characterleft1.png", ImageFormat.RGB565);
		Assets.characterLeft2 = g.newImage("characterleft2.png", ImageFormat.RGB565);
		Assets.characterRight1 = g.newImage("characterright1.png", ImageFormat.RGB565);
		Assets.characterRight2 = g.newImage("characterright2.png", ImageFormat.RGB565);
        Assets.characterUp1 = g.newImage("characterup1.png", ImageFormat.RGB565);
        Assets.characterUp2 = g.newImage("characterup2.png", ImageFormat.RGB565);
        Assets.characterDown1 = g.newImage("characterdown1.png", ImageFormat.RGB565);
        Assets.characterDown2 = g.newImage("characterdown2.png", ImageFormat.RGB565);

		Assets.enemyLeft1 = g.newImage("enemyLeft1.png", ImageFormat.RGB565);
		Assets.enemyLeft2 = g.newImage("enemyLeft2.png", ImageFormat.RGB565);
		Assets.enemyRight1 = g.newImage("enemyRight1.png", ImageFormat.RGB565);
		Assets.enemyRight2 = g.newImage("enemyRight2.png", ImageFormat.RGB565);
        Assets.inkyLeft1 = g.newImage("inkyLeft1.png", ImageFormat.RGB565);
        Assets.inkyLeft2 = g.newImage("inkyLeft2.png", ImageFormat.RGB565);
        Assets.inkyRight1 = g.newImage("inkyRight1.png", ImageFormat.RGB565);
        Assets.inkyRight2 = g.newImage("inkyRight2.png", ImageFormat.RGB565);
        Assets.pinkyLeft1 = g.newImage("pinkyLeft1.png", ImageFormat.RGB565);
        Assets.pinkyLeft2 = g.newImage("pinkyLeft2.png", ImageFormat.RGB565);
        Assets.pinkyRight1 = g.newImage("pinkyRight1.png", ImageFormat.RGB565);
        Assets.pinkyRight2 = g.newImage("pinkyRight2.png", ImageFormat.RGB565);
        Assets.blinkyLeft1 = g.newImage("blinkyLeft1.png", ImageFormat.RGB565);
        Assets.blinkyLeft2 = g.newImage("blinkyLeft2.png", ImageFormat.RGB565);
        Assets.blinkyRight1 = g.newImage("blinkyRight1.png", ImageFormat.RGB565);
        Assets.blinkyRight2 = g.newImage("blinkyRight2.png", ImageFormat.RGB565);
        Assets.clydeLeft1 = g.newImage("clydeLeft1.png", ImageFormat.RGB565);
        Assets.clydeLeft2 = g.newImage("clydeLeft2.png", ImageFormat.RGB565);
        Assets.clydeRight1 = g.newImage("clydeRight1.png", ImageFormat.RGB565);
        Assets.clydeRight2 = g.newImage("clydeRight2.png", ImageFormat.RGB565);
        Assets.powerModeGhost = g.newImage("powermodeghost.png", ImageFormat.RGB565);

        Assets.pacmanSelection = g.newImage("pacmanSelection.png", ImageFormat.RGB565);
        Assets.inkySelection = g.newImage("inkySelection.png", ImageFormat.RGB565);
        Assets.pinkySelection = g.newImage("pinkySelection.png", ImageFormat.RGB565);
        Assets.blinkySelection = g.newImage("blinkySelection.png", ImageFormat.RGB565);
        Assets.clydeSelection = g.newImage("clydeSelection.png", ImageFormat.RGB565);
        Assets.playerSelectionLocal = g.newImage("playerSelectionlocal.png", ImageFormat.RGB565);
        Assets.playerSelectionRemote = g.newImage("playerSelectionremote.png", ImageFormat.RGB565);


		Assets.buttonUp = g.newImage("buttonUp.png", ImageFormat.RGB565);
		Assets.buttonDown = g.newImage("buttonDown.png", ImageFormat.RGB565);
		Assets.buttonLeft = g.newImage("buttonLeft.png", ImageFormat.RGB565);
		Assets.buttonRight = g.newImage("buttonRight.png", ImageFormat.RGB565);
		Assets.buttonPause = g.newImage("buttonPause.png", ImageFormat.RGB565);

        Assets.tileTree = g.newImage("tree.png", ImageFormat.RGB565);
        Assets.pelletSprite = g.newImage("pellet.png", ImageFormat.RGB565);
        Assets.powerPelletSprite = g.newImage("powerpellet.png", ImageFormat.RGB565);

        //This is how you would load a sound if you had one.
        //Assets.click = game.getAudio().createSound("explode.ogg");
     
        game.setScreen(new CharacterSelectionScreen(game));

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawImage(Assets.splash, 0, 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {

    }
}
