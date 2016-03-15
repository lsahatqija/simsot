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

    public Room(String roomName, String host, int empty_slot){
        this(roomName, null, host, null, null, 100);
        this.empty_slot = empty_slot;
    }

    public Room(String roomName, String host, Double latitude, Double longitude, int distanceMin) {
        this(roomName, null, host, latitude, longitude, distanceMin);
    }

    public Room(String roomName, String roomPassword, String host, Double latitude, Double longitude, int distanceMin) {
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.host = host;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceMin = distanceMin;
    }

    public JSONObject ToJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        json.put(SocketConstants.ROOM_NAME, roomName);
        if (roomPassword  == null) {
            json.put("room_password", null);
        } else {
            json.put("room_password", roomPassword);
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
}
