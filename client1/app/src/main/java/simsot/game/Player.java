package simsot.game;

import android.graphics.Rect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import simsot.framework.Image;
import simsot.framework.Input;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class Player {

    final int MOVESPEED = 4;
    private final int NB_PIXELS_IN_CELL = PacManConstants.NB_PIXELS_IN_CELL;
    private final int HALF_NB_PIXELS_IN_CELL = PacManConstants.HALF_NB_PIXELS_IN_CELL;

    private final int WALK_COUNTER_LOOP = NB_PIXELS_IN_CELL / MOVESPEED;
    private final int HALF_WALK_COUNTER_LOOP = WALK_COUNTER_LOOP / 2;

    private int centerX = 100;
    private int centerY = 100;
    private int speedX = 0;
    private int speedY = 0;
    private int scrollingSpeed = 0;
    private int health = 10;
    private String forceMove = "no";
    private String lastButtonPressed = "left";
    private boolean isMovingVer = false;
    private boolean isMovingHor = false;
    public boolean isColliding = false;
    public boolean touched = false;
    public Rect rect = new Rect(0, 0, 0, 0);
    public int commandType;
    public boolean alive = true;
    private String mode;
    public int walkCounter = 1;
    public boolean colliding = false;
    public String collisionDirection;
    private double direction;

    public boolean vulnerable=false;

    // 0 = not, 1 = left, 2 = top, 3 = right, 4 = bottom
    private int isShooting = 0;

    public Image characterLeft1, characterLeft2, characterRight1, characterRight2, characterUp1,
            characterUp2, characterDown1, characterDown2, characterClosed, currentSprite, vulnerableMode;

    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private String playerName;
    private String roomName;
    private MySocket mySocket;
    protected int lives;

    public Player(int x, int y, String mode, String playerName, String roomName, MySocket mySocket) {
        this.centerX = x;
        this.centerY = y;
        this.mode = mode;
        this.playerName = playerName;
        this.roomName = roomName;
        this.mySocket = mySocket;
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

    public void update(List touchEvents, SampleGame game) {
        ArrayList<Tile> tilearray = GameScreen.tilearray;

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

        if(GameScreen.isPowerMode){
            this.vulnerable = true;
        } else {
            this.vulnerable = false;
        }

        // Collision
        rect.set(centerX - HALF_NB_PIXELS_IN_CELL,
                centerY - HALF_NB_PIXELS_IN_CELL,
                centerX + HALF_NB_PIXELS_IN_CELL,
                centerY + HALF_NB_PIXELS_IN_CELL);

        //movement
        if (PacManConstants.LOCAL.equals(mode)) {
            if ("no".equals(forceMove)) {
                movementControl(touchEvents);
                checkTileCollisions(tilearray);
            } else if ("up".equals(forceMove)) {
                stopUp();
            } else if ("down".equals(forceMove)) {
                stopDown();
            } else if ("left".equals(forceMove)) {
                stopLeft();
            } else if ("right".equals(forceMove)) {
                stopRight();
            }
            animate();
            mySocket.sendPositionUpdate(playerName, roomName, centerX, centerY);
        } else if (PacManConstants.REMOTE.equals(mode)) {
            Map<String, JSONObject> receivedCharacterPositionJSONMap = game.getReceivedCharacterPositionJSONMap();
            if (receivedCharacterPositionJSONMap.containsKey(playerName)) {
                try {
                    JSONObject json = receivedCharacterPositionJSONMap.get(playerName);
                    centerX = json.getInt(SocketConstants.X);
                    centerY = json.getInt(SocketConstants.Y);
                    animate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (PacManConstants.AI.equals(mode)) {
            if ("no".equals(forceMove)) {
                direction = Math.random();
                callAI();
                checkTileCollisions(tilearray);
            } else if ("up".equals(forceMove)) {
                stopUp();
            } else if ("down".equals(forceMove)) {
                stopDown();
            } else if ("left".equals(forceMove)) {
                stopLeft();
            } else if ("right".equals(forceMove)) {
                stopRight();
            }
            animate();
            mySocket.sendPositionUpdate(playerName, roomName, centerX, centerY);
        }

        if (walkCounter > 1000) {
            walkCounter = 0;
        }
        walkCounter++;
    }

    public void movementControl(List touchEvents) {
        int len = touchEvents.size();
        if ("no".equals(forceMove)) {
            for (int i = 0; i < len; i++) {
                Input.TouchEvent event = (Input.TouchEvent) touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                    if (inBounds(event, 215, 645, 50, 50)) {
                        lastButtonPressed = "up";
                        moveUp();
                    } else if (inBounds(event, 215, 715, 50, 50)) {
                        lastButtonPressed = "down";
                        moveDown();
                    } else if (inBounds(event, 165, 675, 50, 50)) {
                        lastButtonPressed = "left";
                        moveLeft();
                    } else if (inBounds(event, 265, 675, 50, 50)) {
                        lastButtonPressed = "right";
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
        } else {
            for (int i = 0; i < len; i++) {
                Input.TouchEvent event = (Input.TouchEvent) touchEvents.get(i);
                if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                    if (inBounds(event, 215, 645, 50, 50)) {
                        lastButtonPressed = "up";
                    } else if (inBounds(event, 215, 715, 50, 50)) {
                        lastButtonPressed = "down";
                    } else if (inBounds(event, 165, 675, 50, 50)) {
                        lastButtonPressed = "left";
                    } else if (inBounds(event, 265, 675, 50, 50)) {
                        lastButtonPressed = "right";
                    }
                }
            }
        }
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
            setCenterX(getCenterX() + getSpeedX());
            setCenterY(getCenterY() + getSpeedY());
        }
    }

    private boolean inBounds(Input.TouchEvent event, int x, int y, int width, int height) {
        if (event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1)
            return true;
        else
            return false;
    }

    public void callAI() {
        if (alive) {
            if (!colliding) {
                if (walkCounter % 50 == 1) {
                    if (direction < 0.25)
                        moveRight();
                    else if (direction < 0.50)
                        moveLeft();
                    else if (direction < 0.75)
                        moveUp();
                    else if (direction < 1.00)
                        moveDown();
                }
            } else {
                if (direction < 0.25)
                    moveRight();
                else if (direction < 0.50)
                    moveLeft();
                else if (direction < 0.75)
                    moveUp();
                else if (direction < 1.00)
                    moveDown();
            }
        }
    }

    public void moveRight() {
        if (!isColliding) {
            speedX = MOVESPEED;
            setMovingHor(true);
        }
    }

    public void moveLeft() {
        if (!isColliding) {
            speedX = -MOVESPEED;
            setMovingHor(true);
        }
    }

    public void moveUp() {
        if (!isColliding) {
            speedY = -MOVESPEED;
            setMovingVer(true);
        }
    }

    public void moveDown() {
        if (!isColliding) {
            speedY = MOVESPEED;
            setMovingVer(true);
        }
    }

    public void stopLeft() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (centerX + HALF_NB_PIXELS_IN_CELL) % NB_PIXELS_IN_CELL;
        if (distanceToCenter <= MOVESPEED) {
            centerX -= distanceToCenter;
            setMovingHor(false);
            forceMove = "no";
        } else {
            centerX -= MOVESPEED;
            forceMove = "left";
        }
        speedX = 0;
    }

    public void stopRight() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (900 + HALF_NB_PIXELS_IN_CELL - centerX) % NB_PIXELS_IN_CELL;
        if (distanceToCenter <= MOVESPEED) {
            centerX += distanceToCenter;
            setMovingHor(false);
            forceMove = "no";
        } else {
            centerX += MOVESPEED;
            forceMove = "right";
        }
        speedX = 0;
    }

    public void stopUp() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (centerY - HALF_NB_PIXELS_IN_CELL) % NB_PIXELS_IN_CELL;
        if (distanceToCenter <= MOVESPEED) {
            centerY -= distanceToCenter;
            setMovingVer(false);
            forceMove = "no";
        } else {
            centerY -= MOVESPEED;
            forceMove = "up";
        }
        speedY = 0;
    }

    public void stopDown() {
        // Keep going until center of next cell is reached
        int distanceToCenter = (900 + HALF_NB_PIXELS_IN_CELL - centerY) % NB_PIXELS_IN_CELL;
        if (distanceToCenter <= MOVESPEED) {
            centerY += distanceToCenter;
            setMovingVer(false);
            forceMove = "no";
        } else {
            centerY += MOVESPEED;
            forceMove = "down";
        }
        speedY = 0;
    }

    public void animate() {
        if (Pacman.class.isInstance(this)) {
            if ("left".equals(lastButtonPressed)) {
                if (getSpeedX() == 0) {
                    currentSprite = characterLeft1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterLeft1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterLeft2;
                }
            } else if ("right".equals(lastButtonPressed)) {
                if (getSpeedX() == 0) {
                    currentSprite = characterRight1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterRight1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterRight2;
                }
            } else if ("up".equals(lastButtonPressed)) {
                if (getSpeedY() == 0) {
                    currentSprite = characterUp1;
                    walkCounter = 0;
                } else if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterUp1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterUp2;
                }
            } else if ("down".equals(lastButtonPressed)) {
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
            if ((isMovingVer() || isMovingHor()) && getSpeedX() < 0) {
                if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterLeft1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterLeft2;
                }
            } else if ((isMovingVer() || isMovingHor()) && getSpeedX() > 0) {
                if (walkCounter % WALK_COUNTER_LOOP == 0) {
                    currentSprite = characterRight1;
                } else if (walkCounter % WALK_COUNTER_LOOP == HALF_WALK_COUNTER_LOOP) {
                    currentSprite = characterRight2;
                }
            } else // in case ghost isn't moving or is colliding
            {
                currentSprite = characterLeft1;
            }
        } else {
            currentSprite = vulnerableMode;
        }
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

    public int getSpeedY() {
        return speedY;
    }

    public int getScrollingSpeed() {
        return this.scrollingSpeed;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
	
	/*
	public void setWeapon(Firearm weapon) {
		this.weapon = weapon;
	}
	
	public Firearm getWeapon() {
		return this.weapon;
	}
	*/
}
