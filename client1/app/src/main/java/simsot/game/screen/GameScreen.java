package simsot.game.screen;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import simsot.framework.Game;
import simsot.framework.Graphics;
import simsot.framework.Image;
import simsot.framework.Input.TouchEvent;
import simsot.framework.Screen;
import simsot.game.Assets;
import simsot.game.Background;
import simsot.game.SampleGame;
import simsot.game.Tile;
import simsot.game.item.Item;
import simsot.game.item.Pellet;
import simsot.game.item.PowerPellet;
import simsot.game.player.Pacman;
import simsot.game.player.Player;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class GameScreen extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver, Win, Round
    }

    GameState state = GameState.Ready;

    // Variable Setup
    private int walkCounter = 1;
    private int countDown = 180;
    private int roundCountDown = 180;
    private int score = 0;
    private int maxScore;
    private static Background bg1;
    private static Player pacman, pinky, inky, blinky, clyde;
    public static boolean isPowerMode = false;
    private int PowerModeTimer = 0;

    public static Image tileTree, tileGrass, background;

    public static ArrayList<Tile> tilearray = new ArrayList<Tile>();
    public static ArrayList<Pellet> pelletarray = new ArrayList<Pellet>();
    public static ArrayList<Player> playerarray = new ArrayList<Player>();

    public int pelletCounter = 0;

    int livesLeft = 1;
    Paint paint, paint2;

    MySocket mySocket;
    String playerName;
    String roomName;

    private long clock = System.currentTimeMillis();

    public GameScreen(Game game) {
        super(game);

        mySocket = ((SampleGame) game).getMySocket();
        playerName = ((SampleGame) game).getPlayerName();
        roomName = ((SampleGame) game).getRoomName();

        score = 0;
        // Initialize game objects here
        bg1 = new Background(0, 0);
        pacman = CharacterSelectionScreen.getPacman();
        pinky = CharacterSelectionScreen.getPinky();
        inky = CharacterSelectionScreen.getInky();
        blinky = CharacterSelectionScreen.getBlinky();
        clyde = CharacterSelectionScreen.getClyde();
        playerarray.add(pinky);
        playerarray.add(inky);
        playerarray.add(clyde);
        playerarray.add(pacman);
        playerarray.add(blinky);

        inky.characterLeft1 = Assets.inkyLeft1;
        inky.characterLeft2 = Assets.inkyLeft2;
        inky.characterRight1 = Assets.inkyRight1;
        inky.characterRight2 = Assets.inkyRight2;
        inky.currentSprite = inky.characterLeft1;

        pinky.characterLeft1 = Assets.pinkyLeft1;
        pinky.characterLeft2 = Assets.pinkyLeft2;
        pinky.characterRight1 = Assets.pinkyRight1;
        pinky.characterRight2 = Assets.pinkyRight2;
        pinky.currentSprite = pinky.characterLeft1;

        blinky.characterLeft1 = Assets.blinkyLeft1;
        blinky.characterLeft2 = Assets.blinkyLeft2;
        blinky.characterRight1 = Assets.blinkyRight1;
        blinky.characterRight2 = Assets.blinkyRight2;
        blinky.currentSprite = blinky.characterLeft1;

        clyde.characterLeft1 = Assets.clydeLeft1;
        clyde.characterLeft2 = Assets.clydeLeft2;
        clyde.characterRight1 = Assets.clydeRight1;
        clyde.characterRight2 = Assets.clydeRight2;
        clyde.currentSprite = clyde.characterLeft1;

        background = Assets.background;
        tileTree = Assets.tileTree;
        tileGrass = Assets.tileGrass;

        loadMap();
        maxScore = pelletarray.size();
        repositionCharacters();

        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        paint2 = new Paint();
        paint2.setTextSize(100);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.WHITE);

        Graphics g = game.getGraphics();
        g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
        paintTiles(g);
    }

    private void loadMap() {
        ArrayList lines = new ArrayList();
        int width = 0;

        Scanner scanner = new Scanner(((SampleGame) game).getMap());
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            // no more lines to read
            if (line == null) {
                break;
            }

            if (!line.startsWith("!")) {
                lines.add(line);
                width = Math.max(width, line.length());

            }
        }
        int height = lines.size();

        for (int j = 0; j < height; j++) {
            String line = (String) lines.get(j);
            for (int i = 0; i < width; i++) {

                if (i < line.length()) {
                    char ch = line.charAt(i);
                    if (ch == 't') {
                        Tile t = new Tile(i, j, ch);
                        tilearray.add(t);
                    } else if (ch == 'p') {
                        if (pelletCounter % 22 == 6) {
                            PowerPellet p = new PowerPellet(i, j);
                            p.sprite = Assets.powerPelletSprite;
                            pelletarray.add(p);
                        } else {
                            Pellet p = new Pellet(i, j);
                            p.sprite = Assets.pelletSprite;
                            pelletarray.add(p);
                        }
                        pelletCounter++;
                    }
                }
            }
        }
    }

    private void repositionCharacters() {
        for (int i = 1; i < 6; i++) {
            float j = pelletarray.size() * i / 5;
            Player e = playerarray.get(i - 1);
            e.setCenterX(pelletarray.get((int) j - 1).getCenterX());
            e.setCenterY(pelletarray.get((int) j - 1).getCenterY());
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    @Override
    public void update(float deltaTime) {
        List touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady();
        if (state == GameState.Running)
            updateRunning(touchEvents);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
        if (state == GameState.Win)
            updateWin(touchEvents);
        if (state == GameState.Round)
            updateRound();
    }

    private void updateReady() {
        if (countDown > 0) {
            countDown--;
        } else {
            state = GameState.Running;
        }
    }

    private void updateRunning(List touchEvents) {
        // Sleep
        try {
            Thread.sleep(Math.abs(17 - System.currentTimeMillis() + clock));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clock = System.currentTimeMillis();

        // 1. All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = (TouchEvent) touchEvents.get(i);
            if (inBounds(event, 0, 0, 35, 35)) {
                pause();
            }
        }

        for (int i = 0; i < playerarray.size(); i++) {
            Player play = playerarray.get(i);
            play.update(touchEvents, (SampleGame) game, this);
        }

        checkPlayerCollision();
        bg1.update();
        updateTiles();
        updateItems();
        if (walkCounter > 1000) {
            walkCounter = 0;
        }
        walkCounter++;

        if(((SampleGame) game).isMultiMode()){
            for (int i = 0; i < playerarray.size(); i++) {
                Player play = playerarray.get(i);
                if(play.isLocal() || play.isAI()){
                    mySocket.sendPositionUpdate(roomName, play.getCharacter(), play.getCenterX(), play.getCenterY());
                }
            }
        }
    }

    private void updatePaused(List touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = (TouchEvent) touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, 0, 0, 480, 400)) {
                    if (!inBounds(event, 0, 0, 35, 35)) {
                        resume();
                    }
                }
                if (inBounds(event, 0, 400, 480, 800)) {
                    nullify();
                    leaveGame();
                }
            }
        }
    }

    private void updateGameOver(List touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = (TouchEvent) touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_DOWN) {
                if (inBounds(event, 0, 0, 480, 800)) {
                    nullify();
                    leaveGame();
                    return;
                }
            }
        }
    }

    private void updateWin(List touchEvents) {
        drawPacmanWinUI();
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = (TouchEvent) touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_DOWN) {
                if (inBounds(event, 0, 0, 480, 800)) {
                    nullify();
                    leaveGame();
                    return;
                }
            }
        }
    }

    private void updateRound() {
        roundCountDown--;
        repositionCharacters();
        if (roundCountDown == 0) {
            state = GameState.Running;
            roundCountDown = 180;
        }
    }

    private void checkPlayerCollision() {
        for (int i = 0; i < playerarray.size(); i++) {
            Player p = playerarray.get(i);
            if (!Pacman.class.isInstance(p)) {
                if (Rect.intersects(p.rect, pacman.rect)) {
                    if (!isPowerMode)
                        pacmanDeath();
                    else
                        ghostDeath(p);
                }
            }
        }
    }

    private void updateTiles() {
        for (int i = 0; i < tilearray.size(); i++) {
            Tile t = tilearray.get(i);
            t.update();
        }
    }

    private void updateItems() {
        for (int i = 0; i < pelletarray.size(); i++) {
            Item p = pelletarray.get(i);
            p.update();
            if (p.touched && p.isVisible()) {
                if (PowerPellet.class.isInstance(p)) {
                    isPowerMode = true;
                    PowerModeTimer = 300;
                }
                p.setIsVisible(false);
                score++;

                if(((SampleGame) game).isMultiMode() && (pacman.isLocal() || pacman.isAI())){
                    mySocket.sendPelletTaken(roomName, i);
                }
            }
        }

        if(pacman.isRemote() && !((SampleGame) game).getPelletTakenList().isEmpty()){
            int pelletIndex = ((SampleGame) game).getPelletTakenList().get(0);
            Item p = pelletarray.get(pelletIndex);
            if(p.isVisible()){
                if (PowerPellet.class.isInstance(p)) {
                    isPowerMode = true;
                    PowerModeTimer = 300;
                }
                p.setIsVisible(false);
                score++;
            }

            ((SampleGame) game).getPelletTakenList().remove(0);
        }
        if (PowerModeTimer > 0) {
            PowerModeTimer--;
        }
        if (PowerModeTimer == 0) {
            isPowerMode = false;
        }

        if (score == maxScore) {
            state = GameState.Win;
        }
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
        paintTiles(g);
        paintItems(g);

        // First draw the game elements.

        for (int i = 0; i < playerarray.size(); i++) {
            Player play = playerarray.get(i);
            g.drawImage(play.currentSprite, play.getCenterX() - 30, play.getCenterY() - 30);
            if (play.touched) {
                g.drawString("Hit!", play.getCenterX(), play.getCenterY(), paint);
                play.touched = false;
            }
        }

        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();
        if (state == GameState.Win)
            drawPacmanWinUI();
        if (state == GameState.Round)
            drawRestartingUI();

    }

    private void paintTiles(Graphics g) {
        for (int i = 0; i < tilearray.size(); i++) {
            Tile t = tilearray.get(i);
            if (t.getType() != '0') {
                t.setTileImage(tileTree);
                //g.drawImage(tileTree, t.getCenterX() - 27, t.getCenterY() - 27);
                g.drawRect(t.getCenterX() - 30, t.getCenterY() - 30, 30, 30, Color.BLUE);
            }
        }
    }

    private void paintItems(Graphics g) {
        for (int i = 0; i < pelletarray.size(); i++) {
            Item t = pelletarray.get(i);
            if(t.isVisible()){
                g.drawImage(t.sprite, t.getCenterX() - 27, t.getCenterY() - 27);
            }
        }
    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;
        bg1 = null;

        while (playerarray.size() > 0) {
            int i = playerarray.size() - 1;
            playerarray.remove(i);
        }
        while (pelletarray.size() > 0) {
            int i = pelletarray.size() - 1;
            pelletarray.remove(i);
        }
        while (tilearray.size() > 0) {
            int i = tilearray.size() - 1;
            tilearray.remove(i);
        }

        // Call garbage collector to clean up memory.
        System.gc();

    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();

        g.drawARGB(155, 0, 0, 0);
        g.drawString("Start in...", 240, 400, paint);
        g.drawString("" + (countDown / 60 + 1), 240, 550, paint);
    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();
        g.drawImage(Assets.buttonUp, 215, 645);        //up
        g.drawImage(Assets.buttonDown, 215, 715);    //down
        g.drawImage(Assets.buttonLeft, 165, 675);        //left
        g.drawImage(Assets.buttonRight, 265, 675);    //right
        g.drawImage(Assets.buttonPause, 0, 0);    //pause

        g.drawString("" + score, 50, 700, paint);
        if (pacman.getLives() > 0)
            g.drawImage(Assets.characterRight1, 50, 750);
        if (pacman.getLives() > 1)
            g.drawImage(Assets.characterRight1, 85, 750);
        if (pacman.getLives() > 2)
            g.drawImage(Assets.characterRight1, 120, 750);
    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Resume", 240, 365, paint2);
        g.drawString("Menu", 240, 560, paint2);

    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 300, 480, 200, Color.BLACK);
        g.drawString("Ghosts Win", 200, 400, paint);
    }

    private void drawRestartingUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 300, 480, 200, Color.BLACK);
        g.drawString("Next round in " + (roundCountDown / 60), 220, 400, paint);
    }

    private void drawPacmanWinUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 300, 480, 200, Color.BLACK);
        g.drawString("Pacman Wins", 220, 400, paint);
    }

    public void pacmanDeath() {
        pacman.decrementLives();
        if (pacman.getLives() == 0) {
            state = GameState.GameOver;
        } else {
            state = GameState.Round;
        }
    }

    public void ghostDeath(Player p) {
        int q = (int) (Math.random() * 5);
        System.out.println((pelletarray.size() * q / 5));
        p.setCenterX(pelletarray.get((pelletarray.size() * q / 5)).getCenterX());
        p.setCenterY(pelletarray.get((pelletarray.size() * q / 5)).getCenterY());
        p.vulnerable = false;
        p.alive = true;
    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume() {
        if (state == GameState.Paused)
            state = GameState.Running;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        pause();
    }

    private void leaveGame() {
        ((SampleGame) game).leaveGame();
    }

    /*private void setPelletarray(JSONArray pelletJSONArray) throws JSONException {
        pelletarray.clear();

        for(int i = 0; i < pelletJSONArray.length(); ++i){
            JSONObject pelletJSONObject = pelletJSONArray.getJSONObject(i);
            String pelletType = pelletJSONObject.getString(SocketConstants.PELLET_TYPE);

            if(PacManConstants.PELLET_TYPE_NORMAL.equals(pelletType)){
                Pellet pellet =    new Pellet(pelletJSONObject.getInt(SocketConstants.PELLET_CENTER_X), pelletJSONObject.getInt(SocketConstants.PELLET_CENTER_Y));
                pellet.sprite = Assets.pelletSprite;
                pelletarray.add(pellet);
            } else if(PacManConstants.PELLET_TYPE_POWER.equals(pelletType)){
                PowerPellet powerPellet =   new PowerPellet(pelletJSONObject.getInt(SocketConstants.PELLET_CENTER_X), pelletJSONObject.getInt(SocketConstants.PELLET_CENTER_Y));
                powerPellet.sprite = Assets.powerPelletSprite;
                pelletarray.add(powerPellet);
            }
        }
    }

    private JSONArray convertPelletArrayToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < pelletarray.size(); ++i){
            jsonArray.put(pelletarray.get(i).toJSONObject());
        }

        return jsonArray;
    }*/

    public void synchroniseGame(JSONObject gameState){
        try {
            /*JSONArray pelletJSONArray = gameState.getJSONArray(SocketConstants.PELLET_ARRAY);
            setPelletarray(pelletJSONArray);*/

            state = GameState.valueOf(gameState.getString(SocketConstants.STATE));

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public static Background getBg1() {
        return bg1;
    }

    public static Player getPlayer() {
        return pacman;
    }

    public ArrayList<Tile> getTilearray() {
        return tilearray;
    }

    public void setTilearray(ArrayList<Tile> tilearray) {
        this.tilearray = tilearray;
    }
}