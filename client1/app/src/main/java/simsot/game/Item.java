package simsot.game;

import android.graphics.Rect;

import java.util.ArrayList;

import simsot.framework.Image;

public class Item {

    private int centerX, centerY;
    private Rect r;
    public boolean touched = false;
    public Image sprite;
    private ArrayList<Player> playerarray = GameScreen.playerarray;
    private Player pacman = GameScreen.getPlayer();

    public Item(int x, int y){
        centerX = (x * 30) + 15;
        centerY = (y * 30) + 15;

        r = new Rect(getCenterX(),getCenterY(),getCenterX()+20,getCenterY()+20);

    }

    public void update() {
        r.set(getCenterX() - 13, getCenterY() - 13, getCenterX() + 13, getCenterY() + 13);
        checkCollision(pacman);
    }

    public void checkCollision(Player pacman){
        if (Rect.intersects(pacman.rect, r)) {
            touched = true;
        }
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

    public int getCenterX() {

        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

}
