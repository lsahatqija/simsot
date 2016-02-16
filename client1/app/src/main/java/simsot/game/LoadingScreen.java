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
		Assets.buttonUp = g.newImage("buttonUp.png", ImageFormat.RGB565);
		Assets.buttonDown = g.newImage("buttonDown.png", ImageFormat.RGB565);
		Assets.buttonLeft = g.newImage("buttonLeft.png", ImageFormat.RGB565);
		Assets.buttonRight = g.newImage("buttonRight.png", ImageFormat.RGB565);
		Assets.buttonPause = g.newImage("buttonPause.png", ImageFormat.RGB565);
        Assets.tileTree = g.newImage("tree.png", ImageFormat.RGB565);

        //This is how you would load a sound if you had one.
        //Assets.click = game.getAudio().createSound("explode.ogg");
     
        game.setScreen(new MainMenuScreen(game));

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
