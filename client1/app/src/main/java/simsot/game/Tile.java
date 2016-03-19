package simsot.game;

import android.graphics.Rect;

import java.util.ArrayList;

import simsot.framework.Image;

public class Tile {

	private final int NB_PIXELS_IN_CELL = PacManConstants.NB_PIXELS_IN_CELL;
	private final int HALF_NB_PIXELS_IN_CELL = PacManConstants.HALF_NB_PIXELS_IN_CELL;

	private int speedX = 0;
	private int speedY = 0;
	private int centerX;
	private int centerY;
	private char type;
	public Image tileImage;

	//private Background bg = StartingClass.getBg1();
	private ArrayList<Player> playerarray = GameScreen.playerarray;
	private ArrayList<Enemy> enemyarray = GameScreen.getEnemyarray();

	private Rect r;
	
	private static String acceptedTileTypes = "t0";

	public static boolean isTileTypeSupported(char type) {
		String test = "";
		test += type;
		return acceptedTileTypes.contains(test);
	}
	
	public Tile(int x, int y, char typeInt) {
		centerX = (x * NB_PIXELS_IN_CELL) + HALF_NB_PIXELS_IN_CELL;
		centerY = (y * NB_PIXELS_IN_CELL) + HALF_NB_PIXELS_IN_CELL;
		type = typeInt;

		r = new Rect(getCenterX() - HALF_NB_PIXELS_IN_CELL,
				getCenterY() - HALF_NB_PIXELS_IN_CELL,
				getCenterX() + HALF_NB_PIXELS_IN_CELL,
				getCenterY() + HALF_NB_PIXELS_IN_CELL);
		
		if (type == 't') {
			tileImage = GameScreen.tileTree;
		} else {
			tileImage = GameScreen.tileGrass;
		}
	}

	public void checkRightCollision(Player player) {
		if (r.contains(player.getCenterX() + player.getSpeedX() + HALF_NB_PIXELS_IN_CELL + 1,
				player.getCenterY())) {

			player.setCenterX(centerX - NB_PIXELS_IN_CELL);
			player.setSpeedX(0);
			player.colliding = true;
			player.collisionDirection = "left";
		} else {
			player.colliding = false;
			player.collisionDirection = "none";
		}
	}

	public void checkLeftCollision(Player player) {
		if (r.contains(player.getCenterX() + player.getSpeedX() - HALF_NB_PIXELS_IN_CELL,
				player.getCenterY())) {

			player.setCenterX(centerX + NB_PIXELS_IN_CELL);
			player.setSpeedX(0);
			player.colliding = true;
			player.collisionDirection = "left";
		} else {
			player.colliding = false;
			player.collisionDirection = "none";
		}
	}

	public void checkDownCollision(Player player) {
		if (r.contains(player.getCenterX(),
				player.getCenterY() + player.getSpeedY() + HALF_NB_PIXELS_IN_CELL + 1)) {

			player.setCenterY(centerY - NB_PIXELS_IN_CELL);
			player.setSpeedY(0);
			player.colliding = true;
			player.collisionDirection = "left";
		} else {
			player.colliding = false;
			player.collisionDirection = "none";
		}
	}

	public void checkUpCollision(Player player) {
		if (r.contains(player.getCenterX(),
				player.getCenterY() + player.getSpeedY() - HALF_NB_PIXELS_IN_CELL)) {

			player.setCenterY(centerY + NB_PIXELS_IN_CELL);
			player.setSpeedY(0);
			player.colliding = true;
			player.collisionDirection = "left";
		} else {
			player.colliding = false;
			player.collisionDirection = "none";
		}
	}

	public void update() {
		r.set(getCenterX() - 15, getCenterY() - 15, getCenterX() + 15, getCenterY() + 15);
	}

	public int getSpeedX() {
		return speedX;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public Rect getR() {
		return r;
	}

	public void setR(Rect r) {
		this.r = r;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public Image getTileImage() {
		return tileImage;
	}

	public void setTileImage(Image tileImage) {
		this.tileImage = tileImage;
	}
}

