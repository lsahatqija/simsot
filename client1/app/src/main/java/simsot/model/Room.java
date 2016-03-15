package simsot.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import simsot.socket.SocketConstants;

public class Room implements Serializable {

    String roomName;
    String roomPassword;
    String host;
    int empty_slot = 0;
    Double latitude;
    Double longitude;
    int distanceMin;
    boolean isPassword;
    String map;

    public Room(String roomName, String host, int empty_slot, boolean isPassword, String map) {
        this(roomName, "", host, null, null, 100, isPassword);
        this.empty_slot = empty_slot;
        this.isPassword = isPassword;
        this.map = map;
    }

    public Room(String roomName, String host, Double latitude, Double longitude, int distanceMin) {
        this(roomName, "", host, latitude, longitude, distanceMin, false);
    }

    public Room(String roomName, String roomPassword, String host, Double latitude, Double longitude, int distanceMin, boolean isPassword) {
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.host = host;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMin = distanceMin;
        this.isPassword = isPassword;
    }

    public JSONObject ToJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(SocketConstants.ROOM_NAME, roomName);
        if (isPassword == false) {
            json.put("room_password", null);
            json.put("is_password", false);
        } else {
            json.put("room_password", roomPassword);
            json.put("is_password", true);
        }

        json.put(SocketConstants.HOST, host);
        json.put(SocketConstants.LATITUDE, latitude);
        json.put(SocketConstants.LONGITUDE, longitude);
        json.put("distance_min", distanceMin);

        return json;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getHost() {
        return host;
    }

    public int getSlotEmpty() {
        return empty_slot;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isPassword() {
        return isPassword;
    }

    public String getMap() {
        return map;
    }
}
