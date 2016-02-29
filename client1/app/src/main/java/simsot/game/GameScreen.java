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
import simsot.socket.MySocket;

public class GameScreen extends Screen {
	enum GameState {
		Ready, Running, Paused, GameOver
	}

	GameState state = GameState.Ready;

	// Variable Setup

	private int walkCounter = 1;
	private static Background bg1, bg2;
	private static Player player;
	public Enemy e1, e2;

	public static Image tileTree, tileGrass, background;
	private Animation anim;

	private ArrayList<Tile> tilearray = new ArrayList<Tile>();
	public static ArrayList<Enemy> enemyarray = new ArrayList<Enemy>();

	int livesLeft = 1;
	Paint paint, paint2;

	MySocket mySocket;
	String playerName;

    private long clock = System.currentTimeMillis();

	public GameScreen(Game game) {
		super(game);

		mySocket = ((SampleGame) game).getMySocket();
		playerName = ((SampleGame) game).getPlayerName();

		// Initialize game objects here

		bg1 = new Background(0, 0);
		bg2 = new Background(2160, 0);
		player = new Player();
		e1 = new Enemy(400, 100);
		e2 = new Enemy(100, 700);
		enemyarray.add(e1);
		enemyarray.add(e2);

		// Image Setups
		player.characterLeft1 = Assets.characterLeft1;
		player.characterLeft2 = Assets.characterLeft2;
		player.characterRight1 = Assets.characterRight1;
		player.characterRight2 = Assets.characterRight2;
        player.characterDown1 = Assets.characterDown1;
        player.characterDown2 = Assets.characterDown2;
        player.characterUp1 = Assets.characterUp1;
        player.characterUp2 = Assets.characterUp2;
		player.characterClosed = Assets.characterClosed;

        player.currentSprite = player.characterLeft1;

        for(int i = 0; i < enemyarray.size(); i++){
            Enemy e = enemyarray.get(i);
            e.characterLeft1 = Assets.enemyLeft1;
            e.characterLeft2 = Assets.enemyLeft2;
            e.characterRight1 = Assets.enemyRight1;
            e.characterRight2 = Assets.enemyRight2;
            e.currentSprite = e.characterLeft1;
        }
		background = Assets.background;
		tileTree = Assets.tileTree;
		tileGrass = Assets.tileGrass;

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

        Graphics g = game.getGraphics();
        g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
        g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());
        paintTiles(g);

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

		for (int j = 0; j < height; j++) {
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

		// Sleep
        try {
            Thread.sleep(Math.abs(17 - System.currentTimeMillis() + clock));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clock = System.currentTimeMillis();

		// 1. All touch input is handled here:
		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = (TouchEvent) touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_DOWN) {
				if (inBounds(event, 215, 645, 50, 50)) {
					player.moveUp();
				}
				else if (inBounds(event, 215, 715, 50, 50)) {
					player.moveDown();
				}
				if (inBounds(event, 165, 675, 50, 50)) {
					player.moveLeft();
				}
				else if (inBounds(event, 265, 675, 50, 50)) {
					player.moveRight();
				}
			}

			if (event.type == TouchEvent.TOUCH_UP) {
				if (inBounds(event, 215, 645, 50, 50)) {
					player.stopVer();
				} else if (inBounds(event, 215, 715, 50, 50)){
					player.stopVer();
				}			
				if (inBounds(event, 165, 675, 50, 50)) {
					player.stopHor();
				}			
				else if (inBounds(event, 265, 675, 50, 50)) {
					player.stopHor();
				}
				if (inBounds(event, 0, 0, 35, 35)) {
					pause();
				}
			}
		}

		// Animation
		if ((player.isMovingHor() == true || player.isMovingVer() == true) && player.getSpeedX() < 0 && player.getSpeedY() == 0){
			if (walkCounter % 16 == 0) {
				player.currentSprite = player.characterLeft2;
			} else if (walkCounter % 16 == 4) {
				player.currentSprite = player.characterLeft1;
			} else if (walkCounter % 16 == 8) {
				player.currentSprite = player.characterLeft2;
			} else if (walkCounter % 16 == 12) {
				player.currentSprite = player.characterClosed;
			}
		} else if ((player.isMovingHor() == true || player.isMovingVer() == true) && player.getSpeedX() > 0  && player.getSpeedY() == 0){
			if (walkCounter % 16 == 0) {
				player.currentSprite = player.characterRight2;
			} else if (walkCounter % 16 == 4) {
				player.currentSprite = player.characterRight1;
			} else if (walkCounter % 16 == 8) {
				player.currentSprite = player.characterRight2;
			} else if (walkCounter % 16 == 12) {
				player.currentSprite = player.characterClosed;
			}
		} else if ((player.isMovingHor() == true || player.isMovingVer() == true) && player.getSpeedY() > 0  && player.getSpeedX() == 0){
            if (walkCounter % 16 == 0) {
                player.currentSprite = player.characterDown2;
            } else if (walkCounter % 16 == 4) {
                player.currentSprite = player.characterDown1;
            } else if (walkCounter % 16 == 8) {
                player.currentSprite = player.characterDown2;
            } else if (walkCounter % 16 == 12) {
                player.currentSprite = player.characterClosed;
            }
        } else if ((player.isMovingHor() == true || player.isMovingVer() == true) && player.getSpeedY() < 0  && player.getSpeedX() == 0){
            if (walkCounter % 16 == 0) {
                player.currentSprite = player.characterUp2;
            } else if (walkCounter % 16 == 4) {
                player.currentSprite = player.characterUp1;
            } else if (walkCounter % 16 == 8) {
                player.currentSprite = player.characterUp2;
            } else if (walkCounter % 16 == 12) {
                player.currentSprite = player.characterClosed;
            }
        }

        for (int j = 0; j < getEnemyarray().size(); j++) {
			Enemy e = getEnemyarray().get(j);

			if (e.alive == true) {
				if (e.isMoving == true && e.getSpeedX() <= 0) {
					if (walkCounter % 16 == 0) {
						e.currentSprite = Assets.enemyLeft1;
					} else if (walkCounter % 16 == 8) {
						e.currentSprite = Assets.enemyLeft2;
					}
				} else if (e.isMoving == true && e.getSpeedX() > 0) {
					if (walkCounter % 16 == 0) {
						e.currentSprite = Assets.enemyRight1;
					} else if (walkCounter % 16 == 8) {
						e.currentSprite = Assets.enemyRight2;
					}
				} else if (e.isMoving == false) {
					e.currentSprite = Assets.enemyLeft1;
				}
				if (e.walkCounter > 1000) {
					e.walkCounter = 0;
				}
			}
		}

		player.update();
		mySocket.sendPositionUpdate(playerName, player.getCenterX(), player.getCenterY());

		callEnemiesAIs();
		checkTileCollisions();
		checkEnemiesCollision();
		updateEnemies();

		bg1.update();
		bg2.update();
		//animate();
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
			if(t.getType() != '0'){
				t.checkCollisions();
			}
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
				if (inBounds(event, 0, 0, 480, 400)) {

					if (!inBounds(event, 0, 0, 35, 35)) {
						resume();
					}
				}

				if (inBounds(event, 0, 400, 480, 800)) {
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
				if (inBounds(event, 0, 0, 480, 800)) {
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
		g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
		g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());
		paintTiles(g);

		ArrayList projectiles = player.getProjectiles();
		for (int i = 0; i < projectiles.size(); i++) {
			Projectile p = (Projectile) projectiles.get(i);
			g.drawRect(p.getCenterX(), p.getCenterY(), 10, 5, Color.YELLOW);
		}
		// First draw the game elements.

		g.drawImage(player.currentSprite, player.getCenterX() - 61, player.getCenterY() - 63);

		for (int i = 0; i < getEnemyarray().size(); i++) {
			Enemy e = getEnemyarray().get(i);
			g.drawImage(e.currentSprite, e.getCenterX() - 61, e.getCenterY() - 63);
		}

		if(player.touched == true){
			g.drawString("Hit!", player.getCenterX(), player.getCenterY(), paint);
            player.touched = false;
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
			//tileGrass = g.newImage("grass.png", ImageFormat.RGB565);
			//tileTree = g.newImage("tree.png", ImageFormat.RGB565);
			if (t.getType() != 0) {
				t.setTileImage(tileTree);
				g.drawImage(tileTree, t.getCenterX() - 31, t.getCenterY() - 31);
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
        /*
        player.characterLeft1 = null;
        player.characterLeft2 = null;
        player.characterRight1 = null;
        player.characterRight2 = null;
        player.characterUp1 = null;
        player.characterUp2 = null;
        player.characterDown1 = null;
        player.characterDown2 = null;
        player.characterClosed = null;
        player.currentSprite = null;
        */

        /*
        for(int i = enemyarray.size()+1; i <= 0; i--){
            Enemy e = enemyarray.get(i);
            e = null;
            enemyarray.remove(i);

        }*/

        while(enemyarray.size() > 0){
            int i = enemyarray.size()-1;
            enemyarray.remove(i);
        }

		// Call garbage collector to clean up memory.
		System.gc();

	}

	private void drawReadyUI() {
		Graphics g = game.getGraphics();

		g.drawARGB(155, 0, 0, 0);
		g.drawString("Tap to Start.", 240, 400, paint);

	}

	private void drawRunningUI() {
		Graphics g = game.getGraphics();
		g.drawImage(Assets.buttonUp, 215, 645);		//up
		g.drawImage(Assets.buttonDown, 215, 715);	//down
		g.drawImage(Assets.buttonLeft, 165, 675);		//left
		g.drawImage(Assets.buttonRight, 265, 675);	//right
		g.drawImage(Assets.buttonPause, 0, 0);	//pause
	}

	private void drawPausedUI() {
		Graphics g = game.getGraphics();
		// Darken the entire screen so you can display the Paused screen.
		g.drawARGB(155, 0, 0, 0);
		g.drawString("Resume", 240, 365, paint2);
		g.drawString("Menu", 240, 560, paint2);

	}

	private void drawGameOverUI() {
		Graphics g = game.getGraphics();
		g.drawRect(0, 0, 1281, 801, Color.BLACK);
		g.drawString("GAME OVER.", 240, 400, paint2);
		g.drawString("Tap to return.", 240, 450, paint);

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