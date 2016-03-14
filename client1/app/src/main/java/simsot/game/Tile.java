package simsot.game;

import android.graphics.Rect;

import java.util.ArrayList;

import simsot.framework.Image;

public class Tile {

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
		centerX = (x * 30) + 15;
		centerY = (y * 30) + 15;
		type = typeInt;

		r = new Rect(getCenterX(),getCenterY(),getCenterX()+20,getCenterY()+20);
		
		if (type == 't') {
			tileImage = GameScreen.tileTree;
		} else {
			tileImage = GameScreen.tileGrass;
		}
	}

	public void checkHorizontalCollision(Player player) {
		if (Rect.intersects(player.rect, r)) {
			if (player.getCenterX() < this.getCenterX()) {
				player.setCenterX(player.getCenterX() - 2);
                player.setSpeedX(0);
                player.colliding = true;
                player.collisionDirection = "left";
			} else if (player.getCenterX() > this.getCenterX()) {
                player.setCenterX(player.getCenterX() + 2);
                player.setSpeedX(0);
                player.colliding = true;
                player.collisionDirection = "right";
			}
		} else {
            player.colliding = false;
            player.collisionDirection = "none";
        }
	}

	public void checkVerticalCollision(Player player) {
		if (Rect.intersects(player.rect, r)) {
			if (player.getCenterY() < this.getCenterY()) {
                player.setCenterY(player.getCenterY() - 2);
                player.setSpeedY(0);
                player.colliding = true;
                player.collisionDirection = "up";
			} else if (player.getCenterY() > this.getCenterY()) {
                player.setCenterY(player.getCenterY() + 2);
                player.setSpeedY(0);
                player.colliding = true;
                player.collisionDirection = "down";
			}
		} else {
            player.colliding = false;
            player.collisionDirection = "none";
        }
	}
	
	public void checkCollisions() {
        for(int j = 0; j < playerarray.size(); j++){
            Player player = playerarray.get(j);
            checkHorizontalCollision(player);
            checkVerticalCollision(player);
        }
	}

	public void update() {
		r.set(getCenterX()-13, getCenterY()-13, getCenterX()+13, getCenterY()+13);
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

