package simsot.game;

import android.graphics.Color;
import android.graphics.Paint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import simsot.framework.Game;
import simsot.framework.Graphics;
import simsot.framework.Input;
import simsot.framework.Screen;
import simsot.socket.MySocket;

public class CharacterSelectionScreen extends Screen {

    Paint paint = new Paint();
    int timeout = 600;
    private long clock = System.currentTimeMillis();

    public static Player pacman;
    public static Player pinky;
    public static Player inky;
    public static Player blinky;
    public static Player clyde;

    MySocket mySocket;
    String playerName;

    private boolean pacmanTaken;
    private boolean pinkyTaken;
    private boolean inkyTaken;
    private boolean blinkyTaken;
    private boolean clydeTaken;

    private static final String PACMAN = "Pacman";
    private static final String PINKY = "Pinky";
    private static final String INKY = "Inky";
    private static final String BLINKY = "Blinky";
    private static final String CLYDE = "Clyde";

    private static final String AI = "AI";
    private static final String LOCAL = "local";
    private static final String REMOTE = "remote";

    public CharacterSelectionScreen (Game game){
        super(game);
        mySocket = ((SampleGame) game).getMySocket();
        playerName = ((SampleGame) game).getPlayerName();
    }



    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();

        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();

        while(timeout > 0) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++) {
                Input.TouchEvent event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    if (((SampleGame) game).isCharacterChoiceReceived()) {
                        try {
                            JSONObject characterChoiceJSONReceived = ((SampleGame) game).getCharacterChoiceJSONReceived();
                            String character = characterChoiceJSONReceived.getString("character");
                            String playerNameReceived = characterChoiceJSONReceived.getString("playerName");

                            switch (character) {
                                case PACMAN:
                                    pacman = new Pacman(100, 200, REMOTE, playerNameReceived);
                                    break;
                                case INKY:
                                    inky = new Inky(100, 500, REMOTE, playerNameReceived);
                                    break;
                                case PINKY:
                                    pinky = new Pinky(300, 100, REMOTE, playerNameReceived);
                                    break;
                                case BLINKY:
                                    blinky = new Blinky(300, 500, REMOTE, playerNameReceived);
                                    break;
                                case CLYDE:
                                    clyde = new Clyde(100, 100, REMOTE, playerNameReceived);
                                    break;
                                default:
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inBounds(event, 203, 185, 75, 75)) {
                        if (!pacmanTaken) {
                            pacman = new Pacman(100, 200, LOCAL, playerName);
                            mySocket.sendCharacterChoice(PACMAN, playerName);
                            pacmanTaken = true;
                        }
                    }
                    if (inBounds(event, 96, 335, 75, 75)) {
                        if (!inkyTaken) {
                            inky = new Inky(100, 500, LOCAL, playerName);
                            mySocket.sendCharacterChoice(INKY, playerName);
                            inkyTaken = true;
                        }
                    }
                    if (inBounds(event, 171, 335, 75, 75)) {
                        if (!pinkyTaken) {
                            pinky = new Pinky(300, 100, LOCAL, playerName);
                            mySocket.sendCharacterChoice(PINKY, playerName);
                            pinkyTaken = true;
                        }
                    }
                    if (inBounds(event, 247, 335, 75, 75)) {
                        if (!blinkyTaken) {
                            blinky = new Blinky(300, 500, LOCAL, playerName);
                            mySocket.sendCharacterChoice(BLINKY, playerName);
                            blinkyTaken = true;
                        }
                    }
                    if (inBounds(event, 322, 335, 75, 75)) {
                        if (!clydeTaken) {
                            clyde = new Clyde(100, 100, LOCAL, playerName);
                            mySocket.sendCharacterChoice(CLYDE, playerName);
                            clydeTaken = true;
                        }
                    }

                }
            }
            timeout--;
            // Sleep
            try {
                Thread.sleep(Math.abs(17 - System.currentTimeMillis() + clock));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clock = System.currentTimeMillis();
        }
        if(!pacmanTaken){
            pacman = new Pacman(100, 200, AI, PACMAN);
        }
        if(!inkyTaken){
            inky = new Inky(100, 500, AI, INKY);
        }
        if(!pinkyTaken){
            pinky = new Pinky(300, 100, AI, PINKY);
        }
        if(!blinkyTaken){
            blinky = new Blinky(300, 500, AI, BLINKY);
        }
        if(!clydeTaken){
            clyde = new Clyde(100, 100, AI, CLYDE);
        }
        game.setScreen(new GameScreen(game));
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
