package simsot.socket;


import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MySocket {

    private static final String CONNECTION_REQUEST = "connect_user";
    private static final String REGISTER_REQUEST = "subscribe";

    private static final String NAME = "name";
    private static final String X = "x";
    private static final String Y = "y";
    private Socket mSocket;

    public MySocket(String urlServer) throws URISyntaxException {
        this.mSocket = IO.socket(urlServer);
    }

    public Emitter on(String event, Emitter.Listener fn) {
       return mSocket.on(event, fn);
    }

    public void sendConnectionRequest(JSONObject data){
        mSocket.emit(CONNECTION_REQUEST, data);
    }

    public void sendRegistrationRequest(JSONObject data){
        mSocket.emit(REGISTER_REQUEST, data);
    }

    public void sendPositionUpdate(String playerName, int x, int y) {
        try {
            JSONObject json = new JSONObject();

            json.put(NAME, playerName);
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
