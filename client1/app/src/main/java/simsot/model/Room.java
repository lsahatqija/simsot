package simsot.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Room {

    String roomName;
    String roomPassword;
    String host;
    int empty_slot = 0;
    List<String> playersList;
    List<String> enemiesList;
    int nbPlayersMax;
    String GPS;
    int distanceMin;

    public Room(String roomName, String host, int empty_slot){
        this(roomName, null, host, null, null, 5, null, 100);
        this.empty_slot = empty_slot;
    }

    public Room(String roomName, String host, List<String> playersList, List<String> enemiesList, int nbPlayersMax, String GPS, int distanceMin) {
        this(roomName, null, host, playersList, enemiesList, nbPlayersMax, GPS, distanceMin);
    }

    public Room(String roomName, String roomPassword, String host, List<String> playersList, List<String> enemiesList, int nbPlayersMax, String GPS, int distanceMin) {
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.host = host;
        this.playersList = playersList;
        this.enemiesList = enemiesList;
        this.nbPlayersMax = nbPlayersMax;
        this.GPS = GPS;
        this.distanceMin = distanceMin;
    }

    public JSONObject ToJSONObject() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("room_name", roomName);
        if (roomPassword  == null) {
            json.put("room_password", null);
        } else {
            json.put("room_password", roomPassword);
        }

        json.put("host", host);
        json.put("list_players", playersList);
        json.put("list_enemies", enemiesList);
        json.put("number_players_max", nbPlayersMax);
        json.put("GPS", GPS);
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
}
