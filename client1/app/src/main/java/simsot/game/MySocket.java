package simsot.game;


import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MySocket {
    private final String NAME = "nom";
    private Socket mSocket;

    public MySocket(String urlServer) throws URISyntaxException {
        mSocket = IO.socket(urlServer);

    }

    public void sendPositionUpdate(String playerName, int x, int y) {
        try {
            JSONObject json = new JSONObject();

            json.put(NAME, playerName);
            json.put("letter", x + " - " + y);
            mSocket.emit("client_data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void connect(){
        mSocket.connect();
    }
}
