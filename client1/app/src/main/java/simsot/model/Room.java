package simsot.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Room {

    String roomName;
    String roomPassword;
    String host;
    List<String> playersList;
    List<String> ennemiesList;
    int nbPlayersMax;
    int nbEnnemiesMax;
    String GPS;
    int distanceMin;

    public Room(String roomName){
        this(roomName, null, null, null, null, 4, 3, null, 100);
    }

    public Room(String roomName, String host, List<String> playersList, List<String> ennemiesList, int nbPlayersMax, int nbEnnemiesMax, String GPS, int distanceMin) {
        this(roomName, null, host, playersList, ennemiesList, nbPlayersMax, nbEnnemiesMax, GPS, distanceMin);
    }

    public Room(String roomName, String roomPassword, String host, List<String> playersList, List<String> ennemiesList, int nbPlayersMax, int nbEnnemiesMax, String GPS, int distanceMin) {
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.host = host;
        this.playersList = playersList;
        this.ennemiesList = ennemiesList;
        this.nbPlayersMax = nbPlayersMax;
        this.nbEnnemiesMax = nbEnnemiesMax;
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
        json.put("list_ennemies", ennemiesList);
        json.put("number_players_max", nbPlayersMax);
        json.put("number_ennemies_max", nbEnnemiesMax);
        json.put("GPS", GPS);
        json.put("distance_min", distanceMin);

        return json;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getNbPlayersMax() {
        return nbPlayersMax;
    }
}
