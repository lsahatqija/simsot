package simsot.game;

import android.graphics.Rect;
import simsot.framework.Image;

public class Item {

    private int centerX, centerY;
    private Rect r;
    public boolean touched = false;
    public Image sprite;
    private Player player = GameScreen.getPlayer();

    public Item(int x, int y){
        centerX = (x * 30) + 15;
        centerY = (y * 30) + 15;

        r = new Rect(getCenterX(),getCenterY(),getCenterX()+20,getCenterY()+20);

    }

    public void update() {
        r.set(getCenterX()-13, getCenterY()-13, getCenterX()+13, getCenterY()+13);
        checkCollision(player);
    }

    public void checkCollision(Player player){
        if (Rect.intersects(player.rectX, r)) {
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
