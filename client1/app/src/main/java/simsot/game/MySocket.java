package simsot.game;


import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MySocket {
    private static final String NAME = "nom";
    private static final String X = "x";
    private static final String Y = "y";
    private Socket mSocket;
    private String userLogin;

    public MySocket(String urlServer, String userLogin) throws URISyntaxException {
        this.mSocket = IO.socket(urlServer);
        this.userLogin = userLogin;
    }

    public void sendPositionUpdate(int x, int y) {
        try {
            JSONObject json = new JSONObject();

            json.put(NAME, userLogin);
            json.put(X, x);
            json.put(Y, y);
            mSocket.emit("client_data", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void connect(){
        mSocket.connect();
    }
}
