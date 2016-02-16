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
	private Player player = GameScreen.getPlayer();
	private ArrayList<Enemy> enemyarray = GameScreen.getEnemyarray();

	private Rect r;
	
	private static String acceptedTileTypes = "t0";

	public static boolean isTileTypeSupported(char type) {
		String test = "";
		test += type;
		return acceptedTileTypes.contains(test);
	}
	
	public Tile(int x, int y, char typeInt) {
		centerX = (x * 50) + 25;
		centerY = (y * 50) + 25;
		type = typeInt;

		r = new Rect(getCenterX(),getCenterY(),getCenterX()+62,getCenterY()+63);
		
		if (type == 't') {
			tileImage = GameScreen.tileTree;
		} else {
			tileImage = GameScreen.tileGrass;
		}
	}

	public void checkHorizontalCollision(Player player) {
		if (Rect.intersects(player.rectX, r)) {
			if (player.getCenterX() <= this.getCenterX()) {
				player.setCenterX(player.getCenterX() - 2);
				player.setSpeedX(0);
			} else if (player.getCenterX() > this.getCenterX()) {
				player.setCenterX(player.getCenterX() + 2);
				player.setSpeedX(0);
			}
		}
	}

	public void checkVerticalCollision(Player player) {
		if (Rect.intersects(player.rectY, r)) {
			if (player.getCenterY() <= this.getCenterY()) {
				player.setCenterY(player.getCenterY() - 2);
				player.setSpeedY(0);
				//player.isColliding = true;
			} else if (player.getCenterY() > this.getCenterY()) {
				player.setCenterY(player.getCenterY() + 2);
				player.setSpeedY(0);
				//player.isColliding = true;
			}
		}
	}

	public void checkHorizontalCollision(Enemy enemy) {
		if (/*enemy.isAlive() == true && */Rect.intersects(enemy.rectX, r)) {
			if (enemy.getCenterX() <= this.getCenterX()) {
				enemy.setCenterX(enemy.getCenterX() - 2);
				enemy.setSpeedX(enemy.getSpeedX() * (-1));
			} else if (enemy.getCenterX() >= this.getCenterX()) {
				enemy.setCenterX(enemy.getCenterX() + 2);
				enemy.setSpeedX(enemy.getSpeedX() * (-1));
			}
		}
	}

	public void checkVerticalCollision(Enemy enemy) {
		if (/*enemy.isAlive() == true && */Rect.intersects(enemy.rectY, r)) {
			if (enemy.getCenterY() <= this.getCenterY()) {
				enemy.setCenterY(enemy.getCenterY() - 2);
				enemy.setSpeedY(enemy.getSpeedY() * (-1));
			} else if (enemy.getCenterY() >= this.getCenterY()) {
				enemy.setCenterY(enemy.getCenterY() + 2);
				enemy.setSpeedY(enemy.getSpeedY() * (-1));
			}
		}
	}

	public void checkPlayerEnemyCollision(Player player, Enemy enemy){
		if(Rect.intersects(player.rectX, enemy.rectX)){
			player.touched = true;
		} else if(Rect.intersects(player.rectX, enemy.rectY)){
			player.touched = true;
		} else if(Rect.intersects(player.rectY, enemy.rectX)){
			player.touched = true;
		} else if(Rect.intersects(player.rectY, enemy.rectY)){
			player.touched = true;
		} else {
			player.touched = false;
		}
	}
	
	public void checkCollisions() {
		checkHorizontalCollision(player);
		checkVerticalCollision(player);
		for (int i = 0; i < enemyarray.size(); i++) {
			Enemy e = enemyarray.get(i);
			checkHorizontalCollision(e);
			checkVerticalCollision(e);
            checkPlayerEnemyCollision(player, e);
		}
	}

	public void update() {
		r.set(getCenterX(), getCenterY(), getCenterX()+50, getCenterY()+50);
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

