package simsot.game;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

import simsot.framework.Game;
import simsot.framework.Graphics;
import simsot.framework.Input;
import simsot.framework.Screen;

public class CharacterSelectionScreen extends Screen {

    Paint paint = new Paint();

    public static Player pacman;
    public static Player pinky;
    public static Player inky;
    public static Player blinky;
    public static Player clyde;

    public CharacterSelectionScreen (Game game){
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();

        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_UP) {

                if (inBounds(event, 203, 185, 75, 75)) {
                    pacman = new Pacman(100, 200,"local");
                    pinky = new Pinky(300, 100, "AI");
                    inky = new Inky(100, 500, "AI");
                    blinky = new Blinky(300, 500, "AI");
                    clyde = new Clyde(100, 100, "AI");
                    game.setScreen(new GameScreen(game));
                }
                if (inBounds(event, 96, 335, 75, 75)) {
                    pacman = new Pacman(100, 200,"AI");
                    pinky = new Pinky(300, 100, "AI");
                    inky = new Inky(100, 500, "local");
                    blinky = new Blinky(300, 500, "AI");
                    clyde = new Clyde(100, 100, "AI");
                    game.setScreen(new GameScreen(game));
                }
                if (inBounds(event, 171, 335, 75, 75)) {
                    pacman = new Pacman(100, 200,"AI");
                    pinky = new Pinky(300, 100, "local");
                    inky = new Inky(100, 500, "AI");
                    blinky = new Blinky(300, 500, "AI");
                    clyde = new Clyde(100, 100, "AI");
                    game.setScreen(new GameScreen(game));
                }
                if (inBounds(event, 247, 335, 75, 75)) {
                    pacman = new Pacman(100, 200,"AI");
                    pinky = new Pinky(300, 100, "AI");
                    inky = new Inky(100, 500, "AI");
                    blinky = new Blinky(300, 500, "local");
                    clyde = new Clyde(100, 100, "AI");
                    game.setScreen(new GameScreen(game));
                }
                if (inBounds(event, 322, 335, 75, 75)) {
                    pacman = new Pacman(100, 200,"AI");
                    pinky = new Pinky(300, 100, "AI");
                    inky = new Inky(100, 500, "AI");
                    blinky = new Blinky(300, 500, "AI");
                    clyde = new Clyde(100, 100, "local");
                    game.setScreen(new GameScreen(game));
                }

            }
        }
    }

    private boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height) {
        if (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1)
            return true;
        else
            return false;
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        //g.drawImage(Assets.menu, 0, 0);
        g.drawRect(0, 0, 530, 850, Color.BLACK);

        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        g.drawString("Select your character!", 240, 100, paint);

        g.drawImage(Assets.pacmanSelection, 203, 185);
        g.drawImage(Assets.inkySelection, 96, 335);
        g.drawImage(Assets.pinkySelection, 171, 335);
        g.drawImage(Assets.blinkySelection, 247, 335);
        g.drawImage(Assets.clydeSelection, 322, 335);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        android.os.Process.killProcess(android.os.Process.myPid());

    }

    public static Player getPacman() {
        return pacman;
    }

    public static Player getPinky() {
        return pinky;
    }

    public static Player getInky() {
        return inky;
    }

    public static Player getBlinky() {
        return blinky;
    }

    public static Player getClyde() {
        return clyde;
    }
}
