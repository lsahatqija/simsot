package simsot.socket;


import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public final class MySocket {

    private static volatile MySocket instance = null;

    private Socket mSocket;

    private SocketConstants.GameMode gameMode;

    private boolean connectionRequestSendingFlag;
    private boolean registerRequestSendingFlag;
    private boolean getRoomListRequestSendingFlag;
    private boolean roomCreationRequestSendingFlag;
    private boolean joinRoomRequestSendingFlag;

    private boolean connectionRequestResponseFlag;
    private boolean registerRequestResponseFlag;
    private boolean getRoomListRequestResponseFlag;
    private boolean roomCreationRequestResponseFlag;
    private boolean joinRoomRequestResponseFlag;

    private MySocket(String urlServer) {
        try {
            this.mSocket = IO.socket(urlServer);

            this.connectionRequestSendingFlag = false;
            this.connectionRequestResponseFlag = false;

            this.registerRequestSendingFlag = false;
            this.registerRequestResponseFlag = false;

            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public final static MySocket getInstance() {
        if (MySocket.instance == null) {
            synchronized(MySocket.class) {
                if (MySocket.instance == null) {
                    MySocket.instance = new MySocket(SocketConstants.SERVER_URL);
                }
            }
        }
        return MySocket.instance;
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

    public void sendPositionUpdate(String playerName, String roomName,  int x, int y) {
        try {
            JSONObject json = new JSONObject();
            json.put(SocketConstants.PLAYER_NAME, playerName);
            json.put(SocketConstants.X, x);
            json.put(SocketConstants.Y, y);
            json.put(SocketConstants.ROOM_NAME, roomName);
            mSocket.emit(SocketConstants.CHARACTER_POSITION, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendGetListRoomRequest(){
        mSocket.emit(SocketConstants.GET_LIST_ROOM, new JSONObject());
        getRoomListRequestSendingFlag = true;
    }

    public void sendJoinRoomRequest(JSONObject data) {
        mSocket.emit(SocketConstants.JOIN_ROOM, data);
        joinRoomRequestSendingFlag = true;
    }

    public void sendLeaveRoomRequest(JSONObject data) {
        mSocket.emit(SocketConstants.LEAVE_ROOM, data);
    }

    public void sendCharacterChoice(String character, String playerName, String roomName) {
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.PLAYER_NAME, playerName);
            json.put(SocketConstants.CHARACTER, character);
            json.put(SocketConstants.ROOM_NAME, roomName);

            mSocket.emit(SocketConstants.CHARACTER_CHOICE, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendGameStart(String roomName){
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            mSocket.emit(SocketConstants.GAME_START, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendCharacterTimeoutEnded(String roomName){
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            mSocket.emit(SocketConstants.CHARACTER_TIMEOUT_ENDED, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*** CONNECT ***/
    private void connect(){
        mSocket.connect();
    }

    public SocketConstants.GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(SocketConstants.GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /*** FLAGS ***/
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

    public boolean isJoinRoomRequestSendingFlag() {
        return joinRoomRequestSendingFlag;
    }

    public void setJoinRoomRequestSendingFlag(boolean joinRoomRequestSendingFlag) {
        this.joinRoomRequestSendingFlag = joinRoomRequestSendingFlag;
    }

    public boolean isJoinRoomRequestResponseFlag() {
        return joinRoomRequestResponseFlag;
    }

    public void setJoinRoomRequestResponseFlag(boolean joinRoomRequestResponseFlag) {
        this.joinRoomRequestResponseFlag = joinRoomRequestResponseFlag;
    }

}
