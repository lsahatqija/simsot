package simsot.game.player;

import android.graphics.Rect;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import simsot.framework.Image;
import simsot.framework.Input;
import simsot.game.Assets;
import simsot.game.PacManConstants;
import simsot.game.SampleGame;
import simsot.game.Tile;
import simsot.game.screen.GameScreen;
import simsot.socket.SocketConstants;

public class Player {

    final int PACMAN_MOVESPEED = 4;

    private final int WALK_COUNTER_LOOP = PacManConstants.NB_PIXELS_IN_CELL / PACMAN_MOVESPEED;
    private final int HALF_WALK_COUNTER_LOOP = WALK_COUNTER_LOOP / 2;

    protected final static String BUTTON_UP = "up";
    protected final static String BUTTON_DOWN = "down";
    protected final static String BUTTON_LEFT = "left";
    protected final static String BUTTON_RIGHT = "right";
    protected final static String FORCE_MOVE_NO = "no";

    private int centerX = 100;
    private int centerY = 100;
    private int speedX = 0;
    private int speedY = 0;
    private int movespeed = 4;
    private String forceMove = FORCE_MOVE_NO;
    private String lastButtonPressed = BUTTON_LEFT;
    private boolean isMovingVer = false;
    private boolean isMovingHor = false;
    public boolean isColliding = false;
    public boolean touched = false;
    public Rect rect = new Rect(0, 0, 0, 0);
    public boolean alive = true;
    private String mode;
    public int walkCounter = 1;
    public boolean colliding = false;
    public String collisionDirection;
    private double direction;

    public boolean vulnerable = false;

    public Image characterLeft1, characterLeft2, characterRight1, characterRight2, characterUp1,
            characterUp2, characterDown1, characterDown2, characterClosed, currentSprite, vulnerableMode;

    private String playerName;
    protected int lives;

    public Player(int x, int y, String mode, String playerName) {
        this(x, y, mode, playerName, PacManConstants.MOVESPEED_DEFAULT);
    }

    public Player(int x, int y, String mode, String playerName, int movespeed) {
        this.centerX = x;
        this.centerY = y;
        this.mode = mode;
        this.playerName = playerName;
        this.movespeed = movespeed;
        lives = 1;
        characterLeft1 = Assets.characterLeft1;
        characterLeft2 = Assets.characterLeft2;
        characterRight1 = Assets.characterRight1;
        characterRight2 = Assets.characterRight2;
        characterDown1 = Assets.characterDown1;
        characterDown2 = Assets.characterDown2;
        characterUp1 = Assets.characterUp1;
        characterUp2 = Assets.characterUp2;
        characterClosed = Assets.characterClosed;

        currentSprite = characterLeft1;
    }

    public void update(List touchEvents, SampleGame game, GameScreen gameScreen) {
        ArrayList<Tile> tilearray = GameScreen.tilearray;

        this.vulnerable = GameScreen.isPowerMode;

        if (centerX > 510) {
            centerX = 0;
        } else if (centerX < 0) {
            centerX = 510;
        }

        if (centerY > 630) {
            centerY = 0;
        } else if (centerY < 0) {
            centerY = 630;
        }

        // Collision
        rect.set(centerX - PacManConstants.HALF_NB_PIXELS_IN_CELL,
                centerY - PacManConstants.HALF_NB_PIXELS_IN_CELL,
                centerX + PacManConstants.HALF_NB_PIXELS_IN_CELL,
                centerY + PacManConstants.HALF_NB_PIXELS_IN_CELL);

        //movement
        if (isLocal()) {
            if (FORCE_MOVE_NO.equals(forceMove)) {
                movementControl(touchEvents);
                checkTileCollisions(tilearray);
            } else if (BUTTON_UP.equals(forceMove)) {
                stopUp();
            } else if (BUTTON_DOWN.equals(forceMove)) {
                stopDown();
            } else if (BUTTON_LEFT.equals(forceMove)) {
                stopLeft();
            } else if (BUTTON_RIGHT.equals(forceMove)) {
                stopRight();
            }
        } else if (isAI()) {
            if (FORCE_MOVE_NO.equals(forceMove)) {
                direction = Math.random();
                callAI();
                checkTileCollisions(tilearray);
            } else if (BUTTON_UP.equals(forceMove)) {
                stopUp();
            } else if (BUTTON_DOWN.equals(forceMove)) {
                stopDown();
            } else if (BUTTON_LEFT.equals(forceMove)) {
                stopLeft();
            } else if (BUTTON_RIGHT.equals(forceMove)) {
                stopRight();
            }
        } else if (isRemote()) {
            Map<String, JSONObject> receivedCharacterPositionJSONMap = game.getReceivedCharacterPositionJSONMap();
            if (receivedCharacterPositionJSONMap.containsKey(getCharacter())) {
                int oldCenterX = centerX;
                int oldCenterY = centerY;

                try {
                    JSONObject json = receivedCharacterPositionJSONMap.get(getCharacter());
                    centerX = json.getInt(SocketConstants.X);
                    centerY = json.getInt(SocketConstants.Y);
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage(), e);
                }

                calculateRemoteSpeed(oldCenterX, oldCenterY);
            }
        }

        animate();

        if (walkCounter > 1000) {
            walkCounter = 0;
        }
        walkCounter++;
    }

    public void movementControl(List touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = (Input.TouchEvent) touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                if (inBounds(event, 215, 645, 50, 50)) {
                    lastButtonPressed = BUTTON_UP;
                    moveUp();
                } else if (inBounds(event, 215, 715, 50, 50)) {
                    lastButtonPressed = BUTTON_DOWN;
                    moveDown();
                } else if (inBounds(event, 165, 675, 50, 50)) {
                    lastButtonPressed = BUTTON_LEFT;
                    moveLeft();
                } else if (inBounds(event, 265, 675, 50, 50)) {
                    lastButtonPressed = BUTTON_RIGHT;
                    moveRight();
                }
            }

            if (event.type == Input.TouchEvent.TOUCH_UP) {
                if (inBounds(event, 215, 645, 50, 50)) {
                    stopUp();
                } else if (inBounds(event, 215, 715, 50, 50)) {
                    stopDown();
                } else if (inBounds(event, 165, 675, 50, 50)) {
                    stopLeft();
                } else if (inBounds(event, 265, 675, 50, 50)) {
                    stopRight();
                }
            }
        }
    }

    private boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    private void checkTileCollisions(ArrayList<Tile> tilearray) {
        for (int i = 0; i < tilearray.size(); i++) {
            Tile t = tilearray.get(i);
            if (t.getType() != '0') {
                if (speedX > 0) {
                    t.checkRightCollision(this);
                } else if (speedX < 0) {
                    t.checkLeftCollision(this);
                } else if (speedY > 0) {
                    t.checkDownCollision(this);
                } else if (speedY < 0) {
                    t.checkUpCollision(this);
                }
            }
        }
        if (!colliding) {
            if (speedX != 0) {
                setCenterX(getCenterX() + getSpeedX());
            } else {
                setCenterY(getCenterY() + getSpeedY());
            }
        }
    }

    public void callAI() {
        if (alive) {
            if (colliding || walkCounter % 50 == 1) {
                if (direction < 0.25) {
                    lastButtonPressed = BUTTON_RIGHT;
                    moveRight();
                } else if (direction < 0.50) {
                    lastButtonPressed = BUTTON_LEFT;
                    moveLeft();
                } else if (direction < 0.75) {
                    lastButtonPressed = BUTTON_UP;
                    moveUp();
                } else if (direction < 1.00) {
                    lastButtonPressed = BUTTON_DOWN;
                    moveDown();
                }
            }
        }
    }

    public void moveRight() {
        if (!isColliding) {
            speedX = movespeed;
            setMovingHor(true);
        }
    }

    public void moveLeft() {
        if (!isColliding) {
            speedX = -movespeed;
            setMovingHor(true);
        }
    }

    public void moveUp() {
        if (!isColliding) {
            speedY = -movespeed;
            setMovingVer(true);
        }
    }

    public void moveDown() {
        if (!isColliding) {
            speedY = movespeed;
            setMovingVer(true);
        }
    }

    public void stopLeft() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (centerX + PacManConstants.HALF_NB_PIXELS_IN_CELL) % PacManConstants.NB_PIXELS_IN_CELL;
        if (distanceToCenter <= movespeed) {
            centerX -= distanceToCenter;
            setMovingHor(false);
            forceMove = FORCE_MOVE_NO;
        } else {
            centerX -= movespeed;
            forceMove = BUTTON_LEFT;
        }
        speedX = 0;
    }

    public void stopRight() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (900 + PacManConstants.HALF_NB_PIXELS_IN_CELL - centerX) % PacManConstants.NB_PIXELS_IN_CELL;
        if (distanceToCenter <= movespeed) {
            centerX += distanceToCenter;
            setMovingHor(false);
            forceMove = FORCE_MOVE_NO;
        } else {
            centerX += movespeed;
            forceMove = BUTTON_RIGHT;
        }
        speedX = 0;
    }

    public void stopUp() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (centerY - PacManConstants.HALF_NB_PIXELS_IN_CELL) % PacManConstants.NB_PIXELS_IN_CELL;
        if (distanceToCenter <= movespeed) {
            centerY -= distanceToCenter;
            setMovingVer(false);
            forceMove = FORCE_MOVE_NO;
        } else {
            centerY -= movespeed;
            forceMove = BUTTON_UP;
        }
        speedY = 0;
    }

    public void stopDown() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (900 + PacManConstants.HALF_NB_PIXELS_IN_CELL - centerY) % PacManConstants.NB_PIXELS_IN_CELL;
        if (distanceToCenter <= movespeed) {
            centerY += distanceToCenter;
            setMovingVer(false);
            forceMove = FORCE_MOVE_NO;
        } else {
            centerY += movespeed;
            forceMove = BUTTON_DOWN;
        }
        speedY = 0;
    }

    public void animate() {
        if (Pacman.class.isInstance(this)) {
            if (BUTTON_LEFT.equals(lastButtonPressed)) {
                if (getSpeedX() == 0) {
                    currentSprite = characterLeft1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterLeft1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterLeft2;
                }
            } else if (BUTTON_RIGHT.equals(lastButtonPressed)) {
                if (getSpeedX() == 0) {
                    currentSprite = characterRight1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterRight1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterRight2;
                }
            } else if (BUTTON_UP.equals(lastButtonPressed)) {
                if (getSpeedY() == 0) {
                    currentSprite = characterUp1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterUp1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterUp2;
                }
            } else if (BUTTON_DOWN.equals(lastButtonPressed)) {
                if (getSpeedY() == 0) {
                    currentSprite = characterDown1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterDown1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterDown2;
                }
            }
        } else if (!vulnerable) {
            if (BUTTON_LEFT.equals(lastButtonPressed)) {
                if (getSpeedX() == 0) {
                    currentSprite = characterLeft1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterLeft1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterLeft2;
                }
            } else if (BUTTON_RIGHT.equals(lastButtonPressed)) {
                if (getSpeedX() == 0) {
                    currentSprite = characterRight1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterRight1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterRight2;
                }
            } else if (currentSprite == vulnerableMode) {
                currentSprite = characterLeft1;
            }
        } else {
            currentSprite = vulnerableMode;
        }
    }

    public void calculateRemoteSpeed(int oldCenterX, int oldCenterY) {
        speedX = oldCenterX - centerX;
        speedY = oldCenterY - centerY;
        if (speedX < 0) {
            lastButtonPressed = BUTTON_RIGHT;
        } else if (speedX > 0) {
            lastButtonPressed = BUTTON_LEFT;
        } else if (speedY < 0) {
            lastButtonPressed = BUTTON_DOWN;
        } else if (speedY > 0) {
            lastButtonPressed = BUTTON_UP;
        }
    }

    public int getLives() {
        return lives;
    }

    public void decrementLives(){
        lives--;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCharacter() {
        if (Pacman.class.isInstance(this)) {
            return PacManConstants.PACMAN;
        }
        else if (Blinky.class.isInstance(this)) {
            return PacManConstants.BLINKY;
        }
        else if (Clyde.class.isInstance(this)) {
            return PacManConstants.CLYDE;
        }
        else if (Inky.class.isInstance(this)) {
            return PacManConstants.INKY;
        }
        else if (Pinky.class.isInstance(this)) {
            return PacManConstants.PINKY;
        }

        return null;
    }

    public boolean isLocal(){
        return PacManConstants.LOCAL.equals(mode);
    }

    public boolean isAI(){
        return PacManConstants.AI.equals(mode);
    }

    public boolean isRemote() {
        return PacManConstants.REMOTE.equals(mode);
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

    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable;
    }
}
