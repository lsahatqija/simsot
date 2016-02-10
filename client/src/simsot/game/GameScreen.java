package simsot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.graphics.Color;
import android.graphics.Paint;

import simsot.framework.Game;
import simsot.framework.Graphics;
import simsot.framework.Image;
import simsot.framework.Input.TouchEvent;
import simsot.framework.Screen;
import simsot.framework.Graphics.ImageFormat;

public class GameScreen extends Screen {
	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready;

	// Variable Setup

	private int walkCounter = 1;
	private static Background bg1, bg2;
	private static Player player;
	// public static Heliboy hb, hb2;

	private Image image, character1, character2, characterMove1, characterMove2, currentSprite, background;
	public static Image tileTree, tileGrass;
	private Animation anim, hanim;

	private ArrayList<Tile> tilearray = new ArrayList<Tile>();
	public static ArrayList<Enemy> enemyarray = new ArrayList<Enemy>();

	int livesLeft = 1;
	Paint paint, paint2;

	public GameScreen(Game game) {
		super(game);

		// Initialize game objects here

		bg1 = new Background(0, 0);
		bg2 = new Background(2160, 0);
		player = new Player();
		// hb = new Heliboy(340, 360);
		// hb2 = new Heliboy(700, 360);

		// Image Setups
		player.character1 = Assets.character1;
		player.character2 = Assets.character2;
		player.characterMove1 = Assets.characterwalk1;
		player.characterMove2 = Assets.characterwalk2;
		background = Assets.background;
		tileTree = Assets.tileTree;
		tileGrass = Assets.tileGrass;

		anim = new Animation();
		anim.addFrame(player.character1, 1250);
		anim.addFrame(player.character2, 50);
		for (int i = 0; i < getEnemyarray().size(); i++) {
			Enemy e = getEnemyarray().get(i);
			e.characterStay = Assets.enemy1;
			e.characterMove1 = Assets.enemy2;
			e.characterMove2 = Assets.enemy3;
			e.anim.addFrame(e.characterStay, 100);
			e.currentSprite = e.anim.getImage();
		}

		// currentSprite = anim.getImage();
		player.currentSprite = player.character1;

		loadMap();

		// Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);

		paint2 = new Paint();
		paint2.setTextSize(100);
		paint2.setTextAlign(Paint.Align.CENTER);
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);

	}

	private void loadMap() {
		ArrayList lines = new ArrayList();
		int width = 0;
		int height = 0;

		Scanner scanner = new Scanner(SampleGame.map);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

			// no more lines to read
			if (line == null) {
				break;
			}

			if (!line.startsWith("!")) {
				lines.add(line);
				width = Math.max(width, line.length());

			}
		}
		height = lines.size();

		for (int j = 0; j < 12; j++) {
			String line = (String) lines.get(j);
			for (int i = 0; i < width; i++) {

				if (i < line.length()) {
					char ch = line.charAt(i);
					if (ch == 't') {
						Tile t = new Tile(i, j, ch);
						tilearray.add(t);
					}
				}

			}
		}

	}

	@Override
	public void update(float deltaTime) {
		List touchEvents = game.getInput().getTouchEvents();

		// We have four separate update methods in this example.
		// Depending on the state of the game, we call different update methods.
		// Refer to Unit 3's code. We did a similar thing without separating the
		// update methods.

		if (state == GameState.Ready)
			updateReady(touchEvents);
		if (state == GameState.Running)
			updateRunning(touchEvents, deltaTime);
		if (state == GameState.Paused)
			updatePaused(touchEvents);
		if (state == GameState.GameOver)
			updateGameOver(touchEvents);
	}

	private void updateReady(List touchEvents) {

		// This example starts with a "Ready" screen.
		// When the user touches the screen, the game begins.
		// state now becomes GameState.Running.
		// Now the updateRunning() method will be called!

		if (touchEvents.size() > 0)
			state = GameState.Running;
	}

	private void updateRunning(List touchEvents, float deltaTime) {

		// This is identical to the update() method from our Unit 2/3 game.

		// 1. All touch input is handled here:
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_DOWN) {

				if (inBounds(event, 50, 325, 50, 50)) {
					player.moveUp();
					currentSprite = anim.getImage();
				}

				else if (inBounds(event, 0, 350, 65, 65)) {
					/*
					 * if (player.isDucked() == false && player.isJumped() ==
					 * false && player.isReadyToFire()) { player.shootDown(); }
					 */
				}

				else if (inBounds(event,50, 395, 50, 50)) {
					player.moveDown();

				}

				if (inBounds(event, 0, 355, 50, 50)) {
					player.moveLeft();

				}
				
				else if (inBounds(event, 100, 355, 50, 50)) {
					player.moveRight();
				}

			}

			if (event.type == TouchEvent.TOUCH_UP) {

				if (inBounds(event, 0, 285, 65, 65)) {
					currentSprite = anim.getImage();
					player.stopVer();

				} else if (inBounds(event, 0, 415, 65, 65)){
					player.stopVer();
				}
				
				if (inBounds(event, 0, 355, 50, 50)) {
					player.stopHor();

				}
				
				else if (inBounds(event, 100, 355, 50, 50)) {
					player.stopHor();
				}

				if (inBounds(event, 0, 0, 35, 35)) {
					pause();

				}

				if (event.x > 400) {
					// Move right.
					player.stopHor();
				}
			}

		}

		// Animation
		if (player.isMovingHor() == true || player.isMovingVer() == true) {
			if (walkCounter % 30 == 0) {
				player.currentSprite = player.characterMove2;
			} else if (walkCounter % 15 == 0) {
				player.currentSprite = player.characterMove1;
			}
		} else if (player.isMovingVer() == false && player.isMovingHor() == false) {
			// currentSprite = anim.getImage();
			player.currentSprite = player.character1;
		}

		for (int j = 0; j < getEnemyarray().size(); j++) {
			Enemy e = getEnemyarray().get(j);

			if (e.alive == true) {
				if (e.isMoving == true) {
					if (walkCounter % 30 == 0) {
						e.currentSprite = Assets.enemy2;
					} else if (walkCounter % 15 == 0) {
						e.currentSprite = Assets.enemy3;
					}
				} else if (e.isMoving == false) {
					e.currentSprite = Assets.enemy1;
				}
				if (e.walkCounter > 1000) {
					e.walkCounter = 0;
				}
			}
		}

		player.update();

		callEnemiesAIs();
		checkTileCollisions();
		checkEnemiesCollision();
		updateEnemies();

		bg1.update();
		bg2.update();
		animate();
		updateTiles();
		// repaint(); // this calls paint
		if (walkCounter > 1000) {
			walkCounter = 0;
		}
		walkCounter++;
	}

	private void updateEnemies() {
		for (int j = 0; j < getEnemyarray().size(); j++) {
			Enemy e = getEnemyarray().get(j);
			e.update();
		}
	}

	private void callEnemiesAIs() {
		for (Enemy e : getEnemyarray()) {
			e.callAI();
		}
	}

	private void checkEnemiesCollision() {
		for (Enemy e : getEnemyarray()) {
			e.checkEnemyCollisions();
		}
	}

	private void checkTileCollisions() {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = tilearray.get(i);
			t.checkCollisions();
		}
	}

	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		if (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1)
			return true;
		else
			return false;
	}

	private void updatePaused(List touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 0, 0, 800, 240)) {

					if (!inBounds(event, 0, 0, 35, 35)) {
						resume();
					}
				}

				if (inBounds(event, 0, 240, 800, 240)) {
					nullify();
					goToMenu();
				}
			}
		}
	}

	private void updateGameOver(List touchEvents) {
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 0, 0, 800, 480)) {
					nullify();
					game.setScreen(new MainMenuScreen(game));
					return;
				}
			}
		}

	}

	private void updateTiles() {

		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			t.update();
		}

	}

	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();
		Assets.background = g.newImage("background.png", ImageFormat.RGB565);
		Assets.character1 = g.newImage("character1.png", ImageFormat.RGB565);
		Assets.character2 = g.newImage("character2.png", ImageFormat.RGB565);
		Assets.characterMove1 = g.newImage("characterwalk1.png", ImageFormat.RGB565);
		Assets.characterMove2 = g.newImage("characterwalk2.png", ImageFormat.RGB565);
		Assets.button = g.newImage("button.png", ImageFormat.RGB565);
		g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
		g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());
		paintTiles(g);
		g.drawRect(50, 325, 50, 50, Color.WHITE);	//up
		g.drawRect(50, 395, 50, 50, Color.BLUE);		//down
		g.drawRect(0, 355, 50, 50, Color.YELLOW);	//left
		g.drawRect(100, 355, 50, 50, Color.RED);		//right

		ArrayList projectiles = player.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
			g.drawRect(p.getCenterX(), p.getCenterY(), 10, 5, Color.YELLOW);
		}
		// First draw the game elements.

		g.drawImage(Assets.character1, player.getCenterX() - 61, player.getCenterY() - 63);

		for (int i = 0; i < getEnemyarray().size(); i++) {
			Enemy e = getEnemyarray().get(i);
			Assets.enemy1 = g.newImage("enemy1.png", ImageFormat.RGB565);
			Assets.enemy2 = g.newImage("enemy2.png", ImageFormat.RGB565);
			Assets.enemy3 = g.newImage("enemy3.png", ImageFormat.RGB565);
			g.drawImage(e.currentSprite, e.getCenterX() - 61, e.getCenterY() - 63);
		}

		// Example:
		// g.drawImage(Assets.background, 0, 0);
		// g.drawImage(Assets.character, characterX, characterY);

		// Secondly, draw the UI above the game elements.
		if (state == GameState.Ready)
			drawReadyUI();
		if (state == GameState.Running)
			drawRunningUI();
		if (state == GameState.Paused)
			drawPausedUI();
		if (state == GameState.GameOver)
			drawGameOverUI();

	}

	private void paintTiles(Graphics g) {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			tileGrass = g.newImage("grass.png", ImageFormat.RGB565);
			tileTree = g.newImage("tree.png", ImageFormat.RGB565);
			if (t.getType() != 0) {
				t.setTileImage(tileTree);
				g.drawImage(tileTree, t.getCenterX() - 31, t.getCenterY() - 31);
			} else {
				t.setTileImage(tileGrass);
				g.drawImage(tileGrass, t.getCenterX() - 31, t.getCenterY() - 31);
			}
		}
	}

	public void animate() {
		anim.update(10);
		// hanim.update(50);
	}

	private void nullify() {

		// Set all variables to null. You will be recreating them in the
		// constructor.
		paint = null;
		bg1 = null;
		bg2 = null;
		player = null;
		// hb = null;
		// hb2 = null;
		currentSprite = null;
		character1 = null;
		character2 = null;
		anim = null;

		// Call garbage collector to clean up memory.
		System.gc();

	}

	private void drawReadyUI() {
		Graphics g = game.getGraphics();

		g.drawARGB(155, 0, 0, 0);
		g.drawString("Tap to Start.", 400, 240, paint);

	}

	private void drawRunningUI() {
		Graphics g = game.getGraphics();
		g.drawImage(Assets.button, 0, 285, 0, 0, 65, 65);
		g.drawImage(Assets.button, 0, 350, 0, 65, 65, 65);
		g.drawImage(Assets.button, 0, 415, 0, 130, 65, 65);
		g.drawImage(Assets.button, 0, 0, 0, 195, 35, 35);

	}

	private void drawPausedUI() {
		Graphics g = game.getGraphics();
		// Darken the entire screen so you can display the Paused screen.
		g.drawARGB(155, 0, 0, 0);
		g.drawString("Resume", 400, 165, paint2);
		g.drawString("Menu", 400, 360, paint2);

	}

	private void drawGameOverUI() {
		Graphics g = game.getGraphics();
		g.drawRect(0, 0, 1281, 801, Color.BLACK);
		g.drawString("GAME OVER.", 400, 240, paint2);
		g.drawString("Tap to return.", 400, 290, paint);

	}

	@Override
	public void pause() {
		if (state == GameState.Running)
			state = GameState.Paused;

	}

	@Override
	public void resume() {
		if (state == GameState.Paused)
			state = GameState.Running;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void backButton() {
		pause();
	}

	private void goToMenu() {
		// TODO Auto-generated method stub
		game.setScreen(new MainMenuScreen(game));

	}

	public static Background getBg1() {
		// TODO Auto-generated method stub
		return bg1;
	}

	public static Background getBg2() {
		// TODO Auto-generated method stub
		return bg2;
	}

	public static Player getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}

	public ArrayList<Tile> getTilearray() {
		return tilearray;
	}

	public void setTilearray(ArrayList<Tile> tilearray) {
		this.tilearray = tilearray;
	}

	public static ArrayList<Enemy> getEnemyarray() {
		return enemyarray;
	}

	public static void setEnemyarray(ArrayList<Enemy> enemyarray) {
		GameScreen.enemyarray = enemyarray;
	}

}