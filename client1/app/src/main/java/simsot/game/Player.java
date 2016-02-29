package simsot.game;

import java.util.ArrayList;

import android.graphics.Rect;
import simsot.framework.Image;

public class Player {

	final int MOVESPEED = 4;

	private int centerX = 100;
	private int centerY = 100;
	private int speedX = 0;
	private int speedY = 0;
	private int scrollingSpeed = 0;
	private int health = 10;
	private boolean isMovingVer = false;
	private boolean isMovingHor = false;
	public boolean isColliding = false;
	public boolean touched = false;
	public Rect rectX = new Rect(0, 0, 0, 0);
	public Rect rectY = new Rect(0, 0, 0, 0);
	//private Firearm weapon;

	// 0 = not, 1 = left, 2 = top, 3 = right, 4 = bottom
	private int isShooting = 0;

	public Image characterLeft1, characterLeft2, characterRight1, characterRight2, characterUp1, characterUp2, characterDown1, characterDown2, characterClosed, currentSprite;
	
	/*
	private static Background bg1 = GameScreen.getBg1();
	private static Background bg2 = GameScreen.getBg2();*/

	private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

	public void update() {

		// Moves Character or Scrolls Background accordingly.
		/*
		if (speedY != 0) {
			centerY += speedY;
		}
		*/

		// Updates X Position
		centerX += speedX;
		centerY += speedY;
		
		/*
		if (speedY > 0 && centerY > 200) {
			bg1.setSpeedY(0);
			bg2.setSpeedY(0);
		} else if (speedY == 0) {
			bg1.setSpeedY(0);
			bg2.setSpeedY(0);
		} else if (speedY < 0 && centerY < 50) {
			bg1.setSpeedY(0);
			bg2.setSpeedY(0);
			setSpeedY(0);
			setCenterY(getCenterY() + 2);
		}*/

		// Prevents going beyond X coordinate of 0 or 800

		if (centerX + speedX <= 35) {
			centerX = 36;
		} else if (centerX + speedX >= 495) {
			centerX = 494;
		}

		// Prevents going beyond Y coordinate of 150 and 330
		if (centerY + speedY <= 35) {
			centerY = 36;
			//scrollingSpeed = 2*speedY;
		} else if (centerY + speedY >= 735) {
			centerY = 734;
			//scrollingSpeed = 2*speedY;
		}

		// Collision
		rectX.set(centerX - 15, centerY - 10, centerX + 15, centerY + 10);
		rectY.set(centerX - 10, centerY - 15, centerX + 10, centerY + 15);
		/*
		if (isShooting > 0) {
			if (weapon.isReady2Fire()) {
				switch (isShooting) {
				case 1:
					weapon.shootLeft(centerX, centerY);
					break;
				case 2:
					weapon.shootUp(centerX, centerY);
					break;
				case 3:
					weapon.shootRight(centerX, centerY);
					break;
				case 4:
					weapon.shootDown(centerX, centerY);
					break;
				}
			}
		}
		*/
		//weapon.increaseShootingCounter();
	}

	public int isShooting() {
		return isShooting;
	}

	public void setShooting(int isShooting) {
		this.isShooting = isShooting;
	}

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void moveRight() {
		if (isColliding == false) {
			speedX = MOVESPEED;
			setMovingHor(true);
		}
	}

	public void moveLeft() {
		if (isColliding == false) {
			speedX = -MOVESPEED;
			setMovingHor(true);
		}
	}

	public void moveUp() {
		if (isColliding == false) {
			this.setSpeedY(-MOVESPEED);
			setMovingVer(true);
		}
	}

	public void moveDown() {
		if (isColliding == false) {
			this.setSpeedY(MOVESPEED);
			setMovingVer(true);
		}
	}

	public void stopHor() {
		speedX = 0;
		setMovingHor(false);
	}

	public void stopVer() {
		speedY = 0;
		setMovingVer(false);
	}

	public boolean isMovingVer() {
		return isMovingVer;
	}

	public void setMovingVer(boolean isMovingVer) {
		this.isMovingVer = isMovingVer;
	}

	public boolean isMovingHor() {
		return isMovingHor;
	}

	public void setMovingHor(boolean isMovingHor) {
		this.isMovingHor = isMovingHor;
	}

	public int getSpeedX() {
		return speedX;
	}

	public int getSpeedY() {
		return speedY;
	}
	
	public int getScrollingSpeed() {
		return this.scrollingSpeed;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	/*
	public void setWeapon(Firearm weapon) {
		this.weapon = weapon;
	}
	
	public Firearm getWeapon() {
		return this.weapon;
	}
	*/
}
