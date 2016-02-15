package simsot.game;

import android.graphics.Rect;

public abstract class Projectile {

	private int speedX = 0;
	private int speedY = 0;
	private int centerX;
	private int centerY;
	private boolean visible;
	private Rect rectP;
	private int initX;
	private int initY;
	protected int range;
	protected int width;
	protected int height;
	public int damage = 1;

	public Projectile(int startX, int startY, float vectorX, float vectorY, int speed) {
		speedX = (int)(vectorX * speed);
		speedY = (int)(vectorY * speed);
		initX = startX;
		initY = startY;
		visible = true;
		rectP = new Rect(0, 0, 0, 0);
	}

	public void update() {
		rectP.set(getCenterX() - 2, getCenterY() - 2, width, height);
		if (Math.abs(this.getCenterX() - initX) > range) {
			visible = false;
		}
		if (Math.abs(this.getCenterY() - initY) > range) {
			visible = false;
		}
	}

	boolean checkCollision(Enemy e) {
		if(Rect.intersects(rectP, e.R) /*|| rectP.intersects(e.rectY)*/){
			visible = false;
			return true;
		}
		return false;
	}
	
	boolean checkCollision(Tile t) {
		if (t.getType() != '0') {
			if(Rect.intersects(rectP, t.getR())){
				visible = false;
				return true;
			}
			return false;
		}
		return false;
	}
	
	boolean checkCollision(Player p) {
		if(Rect.intersects(rectP, p.rectX) || Rect.intersects(rectP, p.rectY)){
			visible = false;
			return true;
		}
		return false;
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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Rect getR() {
		return rectP;
	}
}
