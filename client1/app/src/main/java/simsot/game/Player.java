package simsot.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Rect;

import org.json.JSONException;
import org.json.JSONObject;

import simsot.framework.Image;
import simsot.framework.Input;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class Player {

    final int MOVESPEED = 4;

    private int centerX = 100;
    private int centerY = 100;
    private int rx, ry;
    private int speedX = 0;
    private int speedY = 0;
    private int scrollingSpeed = 0;
    private int health = 10;
    private boolean isMovingVer = false;
    private boolean isMovingHor = false;
    public boolean isColliding = false;
    public boolean touched = false;
    public Rect rectX = new Rect(0, 0, 0, 0);
    public Rect rectY = new Rect(0, 0, 0, 0);
    public int commandType;
    public boolean alive = true;
    private String mode;
    public int walkCounter = 1;
    public boolean colliding = false;
    public String collisionDirection;
    private double direction;
    //private Firearm weapon;

    // 0 = not, 1 = left, 2 = top, 3 = right, 4 = bottom
    private int isShooting = 0;

    public Image characterLeft1, characterLeft2, characterRight1, characterRight2, characterUp1, characterUp2, characterDown1, characterDown2, characterClosed, currentSprite;

	/*
	private static Background bg1 = GameScreen.getBg1();
	private static Background bg2 = GameScreen.getBg2();*/

    private ArrayList<Projectile> projectiles = new ArrayList<Projectile>();

    private String playerName;
    private String roomName;
    private MySocket mySocket;

    public Player(int x, int y, String mode, String playerName, String roomName, MySocket mySocket) {
        this.centerX = x;
        this.centerY = y;
        this.mode = mode;
        this.playerName = playerName;
        this.roomName = roomName;
        this.mySocket = mySocket;
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

        // Moves Character or Scrolls Background accordingly.
		/*
		if (speedY != 0) {
			centerY += speedY;
		}
		*/
        /*
        if(rx > 0 && rx < 15){
            centerX -= rx;
        } else if (rx >= 15){
            centerX += rx;
        }
        if(ry > 0 && ry < 15){
            centerY -= ry;
        } else if (ry >= 15){
            centerY += ry;
        }*/

        // Updates X Position
        //centerX += speedX;
        //centerY += speedY;
		
		/*
		if (speedY > 0 && centerY > 200) {
			bg1.setSpeedY(0);
			bg2.setSpeedY(0);
		} else if (speedY == 0) {
			bg1.setSpeedY(0);
			bg2.setSpeedY(0);
		} else if (speedY < 0 && centerY < 50) {
			bg1.setSpeedY(0);
			bg2.setSpeedY(0);
			setSpeedY(0);
			setCenterY(getCenterY() + 2);
		}*/

        // Prevents going beyond X coordinate of 0 or 800
/*
		if (centerX + speedX <= 35) {
			centerX = 36;
		} else if (centerX + speedX >= 495) {
			centerX = 494;
		}

		// Prevents going beyond Y coordinate of 150 and 330
		if (centerY + speedY <= 35) {
			centerY = 36;
			//scrollingSpeed = 2*speedY;
		} else if (centerY + speedY >= 735) {
			centerY = 734;
			//scrollingSpeed = 2*speedY;
		}
*/
        if (centerX > 510) {
            centerX = 0;
        } else if (centerX < 0) {
            centerX = 510;
        }

        // Collision
        rectX.set(centerX - 15, centerY - 10, centerX + 15, centerY + 10);
        rectY.set(centerX - 10, centerY - 15, centerX + 10, centerY + 15);

        //movement
        if (PacManConstants.LOCAL.equals(mode)) {
            movementControl(touchEvents);
            centerX += speedX;
            centerY += speedY;
            //System.out.println("sending : {\"player_name\":\""+playerName+"\",\"y\":"+centerY+",\"room_name\":\""+roomName+"\",\"x\":"+centerX+"}");
            mySocket.sendPositionUpdate(playerName, roomName, centerX, centerY);
        } else if (PacManConstants.REMOTE.equals(mode)) {
            Map<String, JSONObject> receivedCharacterPositionJSONMap = game.getReceivedCharacterPositionJSONMap();
            if (receivedCharacterPositionJSONMap.containsKey(playerName)) {
                try {
                    JSONObject json = receivedCharacterPositionJSONMap.get(playerName);
                    centerX = json.getInt(SocketConstants.X);
                    centerY = json.getInt(SocketConstants.Y);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (PacManConstants.AI.equals(mode)) {
            direction = Math.random();
            callAI();
            centerX += speedX;
            centerY += speedY;
            mySocket.sendPositionUpdate(playerName, roomName, centerX, centerY);
        }

        if (walkCounter > 1000) {
            walkCounter = 0;
        }
        walkCounter++;

    }

    public String getMode() {
        return mode;
    }

    public void callAI() {
        if (alive == true) {
            if (!colliding) {
                if (walkCounter % 50 == 1) {
                    if (direction < 0.25)
                        moveRight();
                    else if (direction < 0.55 && direction >= 0.25)
                        moveLeft();
                    else if (direction < 0.75 && direction >= 0.55)
                        moveUp();
                    else if (direction < 1.00 && direction >= 0.75)
                        moveDown();
                } /*else if (walkCounter % 200 == 51) {
                    moveDown();
                } else if (walkCounter % 200 == 101) {
                    moveLeft();
                } else if (walkCounter % 200 == 151) {
                    moveUp();
                }*/
            } else {
                if (direction < 0.25)
                    moveRight();
                else if (direction < 0.55 && direction >= 0.25)
                    moveLeft();
                else if (direction < 0.75 && direction >= 0.55)
                    moveUp();
                else if (direction < 1.00 && direction >= 0.75)
                    moveDown();
            }

        }
    }

    public void movementControl(List touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            Input.TouchEvent event = (Input.TouchEvent) touchEvents.get(i);
            if (event.type == Input.TouchEvent.TOUCH_DOWN) {
                if (inBounds(event, 215, 645, 50, 50)) {
                    moveUp();
                } else if (inBounds(event, 215, 715, 50, 50)) {
                    moveDown();
                }
                if (inBounds(event, 165, 675, 50, 50)) {
                    moveLeft();
                } else if (inBounds(event, 265, 675, 50, 50)) {
                    moveRight();
                }
            }

            if (event.type == Input.TouchEvent.TOUCH_UP) {
                if (inBounds(event, 215, 645, 50, 50)) {
                    stopVer();
                } else if (inBounds(event, 215, 715, 50, 50)) {
                    stopVer();
                }
                if (inBounds(event, 165, 675, 50, 50)) {
                    stopHor();
                } else if (inBounds(event, 265, 675, 50, 50)) {
                    stopHor();
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

    public int isShooting() {
        return isShooting;
    }

    public void setShooting(int isShooting) {
        this.isShooting = isShooting;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
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
            this.setSpeedY(-MOVESPEED);
            setMovingVer(true);
        }
    }

    public void moveDown() {
        if (!isColliding) {
            this.setSpeedY(MOVESPEED);
            setMovingVer(true);
        }
    }

    public void stopHor() {
        //Gridding
        rx = centerX % 30;
        if (rx < 15)
            centerX += rx - 15;
        else if (ry >= 15)
            centerX -= rx - 15;
        speedX = 0;
        setMovingHor(false);
    }

    public void stopVer() {
        //Gridding
        ry = centerY % 30;
        if (ry < 15)
            centerY += ry - 15;
        else if (ry >= 15)
            centerY -= ry - 15;
        speedY = 0;
        setMovingVer(false);
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
