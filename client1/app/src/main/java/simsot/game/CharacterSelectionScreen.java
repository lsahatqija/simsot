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
import simsot.socket.SocketConstants;

public class CharacterSelectionScreen extends Screen {

    Paint paint = new Paint();
    int timeout = 900;
    private long clock = System.currentTimeMillis();

    public static Player pacman;
    public static Player pinky;
    public static Player inky;
    public static Player blinky;
    public static Player clyde;

    private MySocket mySocket;
    private String playerName;
    private String roomName;
    private boolean isHost;

    private boolean pacmanTaken;
    private boolean pinkyTaken;
    private boolean inkyTaken;
    private boolean blinkyTaken;
    private boolean clydeTaken;

    private boolean pacmanTakenLocal;
    private boolean pinkyTakenLocal;
    private boolean inkyTakenLocal;
    private boolean blinkyTakenLocal;
    private boolean clydeTakenLocal;

    private boolean playerSelect = false;

    private static String pacmanName;
    private static String pinkyName;
    private static String inkyName;
    private static String blinkyName;
    private static String clydeName;

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
        roomName = ((SampleGame) game).getRoomName();
        isHost = ((SampleGame) game).isHost();
    }



    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        paint(deltaTime);
        List<Input.TouchEvent> touchEvents = game.getInput().getTouchEvents();

        //while(timeout > 0) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++) {
                Input.TouchEvent event = touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    List<JSONObject> receivedCharacterChoiceJSONList = ((SampleGame) game).getReceivedCharacterChoiceJSONList();
                    if (!receivedCharacterChoiceJSONList.isEmpty()) {
                        try {
                            JSONObject characterChoiceJSONReceived = receivedCharacterChoiceJSONList.get(0);
                            receivedCharacterChoiceJSONList.remove(0);
                            String character = characterChoiceJSONReceived.getString(SocketConstants.CHARACTER);
                            String playerNameReceived = characterChoiceJSONReceived.getString(SocketConstants.PLAYER_NAME);

                            switch (character) {
                                case PACMAN:
                                    pacman = new Pacman(100, 200, REMOTE, playerNameReceived, roomName, mySocket);
                                    pacmanName = playerNameReceived;
                                    pacmanTaken = true;
                                    break;
                                case INKY:
                                    inky = new Inky(100, 500, REMOTE, playerNameReceived, roomName, mySocket);
                                    inkyName = playerNameReceived;
                                    inkyTaken = true;
                                    break;
                                case PINKY:
                                    pinky = new Pinky(300, 100, REMOTE, playerNameReceived, roomName, mySocket);
                                    pinkyName = playerNameReceived;
                                    pinkyTaken = true;
                                    break;
                                case BLINKY:
                                    blinky = new Blinky(300, 500, REMOTE, playerNameReceived, roomName, mySocket);
                                    blinkyName = playerNameReceived;
                                    blinkyTaken = true;
                                    break;
                                case CLYDE:
                                    clyde = new Clyde(100, 100, REMOTE, playerNameReceived, roomName, mySocket);
                                    clydeName = playerNameReceived;
                                    clydeTaken = true;
                                    break;
                                default:
                                    break;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inBounds(event, 203, 185, 75, 75)) {
                        if (!pacmanTaken && !playerSelect) {
                            pacman = new Pacman(100, 200, LOCAL, playerName, roomName, mySocket);
                            mySocket.sendCharacterChoice(PACMAN, playerName, roomName);
                            pacmanTaken = true;
                            pacmanTakenLocal = true;
                            playerSelect = true;
                        }
                    }
                    if (inBounds(event, 96, 335, 75, 75)) {
                        if (!inkyTaken && !playerSelect) {
                            inky = new Inky(100, 500, LOCAL, playerName, roomName, mySocket);
                            mySocket.sendCharacterChoice(INKY, playerName, roomName);
                            inkyTaken = true;
                            inkyTakenLocal = true;
                            playerSelect = true;
                        }
                    }
                    if (inBounds(event, 171, 335, 75, 75)) {
                        if (!pinkyTaken && !playerSelect) {
                            pinky = new Pinky(300, 100, LOCAL, playerName, roomName, mySocket);
                            mySocket.sendCharacterChoice(PINKY, playerName, roomName);
                            pinkyTaken = true;
                            pinkyTakenLocal = true;
                            playerSelect = true;
                        }
                    }
                    if (inBounds(event, 247, 335, 75, 75)) {
                        if (!blinkyTaken && !playerSelect) {
                            blinky = new Blinky(300, 500, LOCAL, playerName, roomName, mySocket);
                            mySocket.sendCharacterChoice(BLINKY, playerName, roomName);
                            blinkyTaken = true;
                            blinkyTakenLocal = true;
                            playerSelect = true;
                        }
                    }
                    if (inBounds(event, 322, 335, 75, 75)) {
                        if (!clydeTaken && !playerSelect) {
                            clyde = new Clyde(100, 100, LOCAL, playerName, roomName, mySocket);
                            mySocket.sendCharacterChoice(CLYDE, playerName, roomName);
                            clydeTaken = true;
                            clydeTakenLocal = true;
                            playerSelect = true;
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
        //}
        if(timeout == 0){
            String characterMode;
            if(isHost)
            {
                characterMode = AI;
            } else {
                characterMode = REMOTE;
            }
            
            if(!pacmanTaken){
                pacman = new Pacman(100, 200, characterMode, PACMAN, roomName, mySocket);
                mySocket.sendCharacterChoice(PACMAN, PACMAN, roomName);
            }
            if(!inkyTaken){
                inky = new Inky(100, 500, characterMode, INKY, roomName, mySocket);
                mySocket.sendCharacterChoice(INKY, INKY, roomName);
            }
            if(!pinkyTaken){
                pinky = new Pinky(300, 100, characterMode, PINKY, roomName, mySocket);
                mySocket.sendCharacterChoice(PINKY, PINKY, roomName);
            }
            if(!blinkyTaken){
                blinky = new Blinky(300, 500, characterMode, BLINKY, roomName, mySocket);
                mySocket.sendCharacterChoice(BLINKY, BLINKY, roomName);
            }
            if(!clydeTaken){
                clyde = new Clyde(100, 100, characterMode, CLYDE, roomName, mySocket);
                mySocket.sendCharacterChoice(CLYDE, CLYDE, roomName);
            }

            game.setScreen(new GameScreen(game));
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
        g.drawString("Timeout in: "+timeout/60, 240, 600, paint);

        g.drawImage(Assets.pacmanSelection, 203, 185);
        if(pacmanTaken){
            if(pacmanTakenLocal){
                g.drawImage(Assets.playerSelectionLocal, 203, 185);
                //g.drawString(playerName, 203, 200, paint);
            } else {
                g.drawImage(Assets.playerSelectionRemote, 203, 185);
                //g.drawString(pacmanName, 203, 200, paint);
            }

        }
        g.drawImage(Assets.inkySelection, 96, 335);
        if(inkyTaken){
            if(inkyTakenLocal){
                g.drawImage(Assets.playerSelectionLocal, 96, 335);
            } else {
                g.drawImage(Assets.playerSelectionRemote, 96, 335);
            }

        }
        g.drawImage(Assets.pinkySelection, 171, 335);
        if(pinkyTaken){
            if(pinkyTakenLocal){
                g.drawImage(Assets.playerSelectionLocal, 171, 335);
            } else {
                g.drawImage(Assets.playerSelectionRemote, 171, 335);
            }

        }
        g.drawImage(Assets.blinkySelection, 247, 335);
        if(blinkyTaken){
            if(blinkyTakenLocal){
                g.drawImage(Assets.playerSelectionLocal, 247, 335);
            } else {
                g.drawImage(Assets.playerSelectionRemote, 247, 335);
            }

        }
        g.drawImage(Assets.clydeSelection, 322, 335);
        if(clydeTaken){
            if(clydeTakenLocal){
                g.drawImage(Assets.playerSelectionLocal, 322, 335);
            } else {
                g.drawImage(Assets.playerSelectionRemote, 322, 335);
            }

        }
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
