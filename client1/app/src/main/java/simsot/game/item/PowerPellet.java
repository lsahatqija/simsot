package simsot.game.item;

import org.json.JSONException;
import org.json.JSONObject;

import simsot.game.PacManConstants;
import simsot.socket.SocketConstants;

public class PowerPellet extends Pellet {

    public PowerPellet(int centerX, int centerY){
        super(centerX, centerY);
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(SocketConstants.PELLET_CENTER_X, getCenterX());
        json.put(SocketConstants.PELLET_CENTER_Y, getCenterY());
        json.put(SocketConstants.PELLET_TYPE, PacManConstants.PELLET_TYPE_POWER);
        return json;
    }
}