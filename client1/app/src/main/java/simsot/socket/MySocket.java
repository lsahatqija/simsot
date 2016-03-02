package simsot.socket;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MySocket {

    private static final String NAME = "name";
    private static final String X = "x";
    private static final String Y = "y";
    private Socket mSocket;

    private boolean connectionRequestSendingFlag;
    private boolean registerRequestSendingFlag;
    private boolean getRoomListRequestSendingFlag;
    private boolean roomCreationRequestSendingFlag;


    private boolean connectionRequestResponseFlag;
    private boolean registerRequestResponseFlag;
    private boolean getRoomListRequestResponseFlag;
    private boolean roomCreationRequestResponseFlag;

    public MySocket(String urlServer) throws URISyntaxException {
        this.mSocket = IO.socket(urlServer);
        this.connectionRequestSendingFlag = false;
        this.connectionRequestResponseFlag = false;

        this.registerRequestSendingFlag = false;
        this.registerRequestResponseFlag = false;
    }

    public Emitter on(String event, Emitter.Listener fn) {
       return mSocket.on(event, fn);
    }

    public void sendConnectionRequest(JSONObject data){
        mSocket.emit(SocketConstants.CONNECTION_REQUEST, data);
        connectionRequestSendingFlag = true;
    }

    public void sendRegistrationRequest(JSONObject data){
        mSocket.emit(SocketConstants.REGISTER_REQUEST, data);
        registerRequestSendingFlag = true;
    }

    public void sendNewRoomRequest(JSONObject data){
        mSocket.emit(SocketConstants.NEW_ROOM_REQUEST, data);
        roomCreationRequestSendingFlag = true;
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

    public void sendGetListRoomRequest(JSONObject data){
        mSocket.emit(SocketConstants.GET_LIST_ROOM, data);
        getRoomListRequestSendingFlag = true;
    }

    public void connect(){
        mSocket.connect();
    }

    public void sendJoinRoomRequest(JSONObject data) {
        mSocket.emit(SocketConstants.JOIN_ROOM, data);
    }

    public boolean isConnectionRequestSendingFlag() {
        return connectionRequestSendingFlag;
    }

    public void setConnectionRequestSendingFlag(boolean connectionRequestSendingFlag) {
        this.connectionRequestSendingFlag = connectionRequestSendingFlag;
    }

    public boolean isRegisterRequestSendingFlag() {
        return registerRequestSendingFlag;
    }

    public void setRegisterRequestSendingFlag(boolean registerRequestSendingFlag) {
        this.registerRequestSendingFlag = registerRequestSendingFlag;
    }

    public boolean isConnectionRequestResponseFlag() {
        return connectionRequestResponseFlag;
    }

    public void setConnectionRequestResponseFlag(boolean connectionRequestResponseFlag) {
        this.connectionRequestResponseFlag = connectionRequestResponseFlag;
    }

    public boolean isRegisterRequestResponseFlag() {
        return registerRequestResponseFlag;
    }

    public void setRegisterRequestResponseFlag(boolean registerRequestResponseFlag) {
        this.registerRequestResponseFlag = registerRequestResponseFlag;
    }

    public boolean isGetRoomListRequestSendingFlag() {
        return getRoomListRequestSendingFlag;
    }

    public void setGetRoomListRequestSendingFlag(boolean getRoomListRequestSendingFlag) {
        this.getRoomListRequestSendingFlag = getRoomListRequestSendingFlag;
    }

    public boolean isGetRoomListRequestResponseFlag() {
        return getRoomListRequestResponseFlag;
    }

    public void setGetRoomListRequestResponseFlag(boolean getRoomListRequestResponseFlag) {
        this.getRoomListRequestResponseFlag = getRoomListRequestResponseFlag;
    }

    public boolean isRoomCreationRequestSendingFlag() {
        return roomCreationRequestSendingFlag;
    }

    public void setRoomCreationRequestSendingFlag(boolean roomCreationRequestSendingFlag) {
        this.roomCreationRequestSendingFlag = roomCreationRequestSendingFlag;
    }

    public boolean isRoomCreationRequestResponseFlag() {
        return roomCreationRequestResponseFlag;
    }

    public void setRoomCreationRequestResponseFlag(boolean roomCreationRequestResponseFlag) {
        this.roomCreationRequestResponseFlag = roomCreationRequestResponseFlag;
    }

    public void sendCharacterChoice(String character, String playerName) {
        JSONObject json = new JSONObject();
        try {
            json.put("playerName", playerName);
            json.put("character", character);
            mSocket.emit(SocketConstants.CHARACTER_CHOICE, character);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
