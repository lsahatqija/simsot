package simsot.game;

import org.json.JSONException;
import org.json.JSONObject;

import simsot.socket.SocketConstants;

public class PowerPellet extends Pellet {

    public PowerPellet(int centerX, int centerY){
        super(centerX, centerY);
    }

    public JSONObject ToJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(SocketConstants.PELLET_CENTER_X, getCenterX());
        json.put(SocketConstants.PELLET_CENTER_Y, getCenterY());
        json.put(SocketConstants.PELLET_TYPE, PacManConstants.PELLET_TYPE_POWER);
        return json;
    }
}