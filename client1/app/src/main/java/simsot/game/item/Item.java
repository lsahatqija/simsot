package simsot.game.item;

import android.graphics.Rect;

import java.util.ArrayList;

import simsot.framework.Image;
import simsot.game.screen.GameScreen;
import simsot.game.player.Player;

public class Item {

    private int centerX, centerY;
    private Rect r;
    public boolean touched = false;
    public Image sprite;
    private ArrayList<Player> playerarray = GameScreen.playerarray;
    private Player pacman = GameScreen.getPlayer();
    private boolean isVisible;

    public Item(int x, int y){
        centerX = (x * 30) + 15;
        centerY = (y * 30) + 15;

        r = new Rect(getCenterX(), getCenterY(), getCenterX() + 10, getCenterY() + 10);

        isVisible = true;
    }

    public void update() {
        r.set(getCenterX() - 5, getCenterY() - 5, getCenterX() + 5, getCenterY() + 5);
        checkCollision(pacman);
    }

    public void checkCollision(Player pacman){
        if (Rect.intersects(pacman.rect, r)) {
            touched = true;
        }
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
