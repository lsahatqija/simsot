package simsot.game;

import android.graphics.Rect;
import simsot.framework.Image;

public class Enemy {

    final int MOVESPEED = 4;

	private int speedX = 0;
	private int speedY = 0;
	private int centerX;
	private int centerY;
	private int maxHealth, currentHealth, power;
	protected int health;
	protected boolean alive = true;
	//protected URL base;
	protected boolean isMoving = false;
	protected int walkCounter = 1;

	private Background bg = GameScreen.getBg1();
	public Rect rectX;
	public Rect rectY;
	public Rect R;
	protected int movementTime = ((int) Math.random() * 100) + 50;

	protected Player player = GameScreen.getPlayer();
	//private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	
	protected Animation anim;

	//protected Firearm weapon;

	public Image characterLeft1, characterLeft2, characterRight1, characterRight2, currentSprite;
	public String characterStayPath, characterMove1Path, characterMove2Path, characterDiePath, currentSpritePath;

	public Enemy(int centerX, int centerY) {
		//weapon.setHolderProjectiles(projectiles);
		//this.weapon = weapon;
		//weapon.setFireRate(weapon.getFireRate() * (5 - difficultylevel));
		this.health = 1;
		this.centerX = centerX;
		this.centerY = centerY;
		rectX = new Rect(centerX - 15, centerY - 10, centerX + 15, centerY + 10);
        rectY = new Rect(centerX - 10, centerY - 15, centerX + 10, centerY + 15);
	}


	public void checkCollision(Enemy e) {
		/*
		if (Rect.intersects(rectX, e.R)) {
			if (e.getCenterX() - getCenterX() >= 0 && getSpeedX() > 0) {
				setSpeedX(0);
			}
			if (e.getCenterX() - getCenterX() <= 0 && getSpeedX() < 0) {
				setSpeedX(0);
			}
		}
		if (Rect.intersects(rectY, e.R)) {
			if (e.getCenterY() - getCenterY() >= 0 && getSpeedY() > 0) {
				setSpeedY(0);
			}
			if (e.getCenterY() - getCenterY() <= 0 && getSpeedY() < 0) {
				setSpeedY(0);
			}
		}*/
	}

	
	public void checkEnemyCollisions() {
		for (Enemy e : GameScreen.getEnemyarray()) {
			if (!e.equals(this))
				checkCollision(e);
		}
	}

	public boolean isAlive() {
		return alive;
	}

	// Behavioral Methods
	public void update() {
		
		if (alive == true) {
			
			centerX += speedX;
			centerY += speedY;
			
			// Prevents going beyond X coordinate of 0 or 800
			if (centerX + speedX <= 30) {
				centerX = 31;
				setSpeedX(MOVESPEED);
			} else if (centerX + speedX >= 480) {
				centerX = 479;
				setSpeedX(-MOVESPEED);
			}

			if (centerY + speedY <= 30) {
				centerY = 31;
				setSpeedY(MOVESPEED);
			} else if (centerY + speedY >= 800) {
				centerY = 799;
				setSpeedY(-MOVESPEED);
			}
			
			if (getSpeedX() != 0 || getSpeedY() !=0){
				isMoving = true;
			} else {
				isMoving = false;
			}
			
			if(walkCounter < 100){
				walkCounter++;
			} else {
				walkCounter = 0;
			}

			// Collision
			rectX.set(centerX - 15, centerY - 10, centerX + 15, centerY + 10);
			rectY.set(centerX - 10, centerY - 15, centerX + 10, centerY + 15);

			// AI
		}
	}

	public void callAI() {
		if (alive == true){
			if (walkCounter % 100 == 51){
				setSpeedX(MOVESPEED);
			} else if (walkCounter % 100 == 1) {
				setSpeedX(-MOVESPEED);
			}
		}
	}
	
	public void die() {
		alive = false;
		setSpeedX(0);
		setSpeedY(0);
	}

	public void attack() {
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getCurrentHealth() {
		return currentHealth;
	}

	public void setCurrentHealth(int currentHealth) {
		this.currentHealth = currentHealth;
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

	public Rect getRectX() {
		return rectX;
	}

	public void setRectX(Rect rectX) {
		this.rectX = rectX;
	}

	public Rect getRectY() {
		return rectY;
	}

	public void setRectY(Rect rectY) {
		this.rectY = rectY;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	/*
	 * public int getMovementParam() { return movementParam; }
	 * 
	 * public void setMovementParam(int movementParam) { this.movementParam =
	 * movementParam; }
	 */

	public Background getBg() {
		return bg;
	}

	public void setBg(Background bg) {
		this.bg = bg;
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	/*
	public void shootUp() {
		weapon.shootUp(centerX, centerY);
	}

	public void shootDown() {
		weapon.shootDown(centerX, centerY);
	}

	public void shootLeft() {
		weapon.shootLeft(centerX, centerY);
	}

	public void shootRight() {
		weapon.shootRight(centerX, centerY);
	}

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
	*/

}

