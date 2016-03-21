package simsot.socket;


import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public final class MySocket {

    private static volatile MySocket instance = null;

    private Socket mSocket;

    public class SocketFlags{
        private volatile boolean sendingFlag;
        private volatile boolean responseFlag;

        public SocketFlags(){
            this.sendingFlag = false;
            this.responseFlag = false;
        }

        public boolean waitResponse(){
            for (long i = 0; i < SocketConstants.REQUEST_TIMEOUT; i += SocketConstants.RESPONSE_CHECK_TIME) {
                try {
                    Thread.sleep(SocketConstants.RESPONSE_CHECK_TIME);
                } catch (InterruptedException e) {
                    Log.e("InterruptedException", e.getMessage(), e);
                }
                if (isResponseFlag()) {
                    break;
                }
            }
            if (isResponseFlag()) {
                setResponseFlag(false);
                return true;
            } else {
                setSendingFlag(false);
                return false;
            }
        }

        public boolean isSendingFlag() {
            return sendingFlag;
        }

        public void setSendingFlag(boolean sendingFlag) {
            this.sendingFlag = sendingFlag;
        }

        public boolean isResponseFlag() {
            return responseFlag;
        }

        public void setResponseFlag(boolean responseFlag) {
            this.responseFlag = responseFlag;
        }
    }

    private volatile SocketFlags connectionRequestFlags;
    private volatile SocketFlags registerRequestFlags;
    private volatile SocketFlags getRoomListRequestFlags;
    private volatile SocketFlags roomCreationRequestFlags;
    private volatile SocketFlags soloRoomCreationRequestFlags;
    private volatile SocketFlags joinRoomRequestFlags;


    private MySocket(String urlServer) {
        try {
            this.mSocket = IO.socket(urlServer);

            this.connectionRequestFlags = new SocketFlags();
            this.registerRequestFlags = new SocketFlags();
            this.getRoomListRequestFlags = new SocketFlags();
            this.roomCreationRequestFlags = new SocketFlags();
            this.soloRoomCreationRequestFlags = new SocketFlags();
            this.joinRoomRequestFlags = new SocketFlags();

            connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public final static MySocket getInstance() {
        if (MySocket.instance == null) {
            synchronized (MySocket.class) {
                if (MySocket.instance == null) {
                    MySocket.instance = new MySocket(SocketConstants.SERVER_URL);
                }
            }
        }
        return MySocket.instance;
    }

    public void on(String event, Emitter.Listener fn) {
        mSocket.on(event, fn);
    }

    public void sendConnectionRequest(JSONObject data) {
        mSocket.emit(SocketConstants.CONNECTION_REQUEST, data);
        getConnectionRequestFlags().setSendingFlag(true);
    }

    public void sendRegistrationRequest(JSONObject data) {
        mSocket.emit(SocketConstants.REGISTER_REQUEST, data);
        getRegisterRequestFlags().setSendingFlag(true);
    }

    public void sendNewRoomRequest(JSONObject data) {
        mSocket.emit(SocketConstants.NEW_ROOM_REQUEST, data);
        getRoomCreationRequestFlags().setSendingFlag(true);
    }

    public void sendSoloRoomCreation(JSONObject data){
        mSocket.emit(SocketConstants.CREATE_SOLO_ROOM_REQUEST, data);
        getSoloRoomCreationRequestFlags().setSendingFlag(true);
    }

    public void sendPositionUpdate(String playerName, String character, String roomName, int x, int y, JSONObject gameState) {
        try {
            JSONObject json = new JSONObject();
            json.put(SocketConstants.PLAYER_NAME, playerName);
            json.put(SocketConstants.CHARACTER, character);
            json.put(SocketConstants.ROOM_NAME, roomName);
            json.put(SocketConstants.X, x);
            json.put(SocketConstants.Y, y);

            if(gameState != null){
                json.put(SocketConstants.GAME_STATE, gameState);
            }

            mSocket.emit(SocketConstants.CHARACTER_POSITION, json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public void sendGetListRoomRequest() {
        mSocket.emit(SocketConstants.GET_LIST_ROOM, new JSONObject());
        getGetRoomListRequestFlags().setSendingFlag(true);
    }

    public void sendJoinRoomRequest(JSONObject data) {
        mSocket.emit(SocketConstants.JOIN_ROOM, data);
        getJoinRoomRequestFlags().setSendingFlag(true);
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
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public void sendGameStart(String roomName) {
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            mSocket.emit(SocketConstants.GAME_START, json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public void sendCharacterTimeoutEnded(String roomName) {
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            mSocket.emit(SocketConstants.CHARACTER_TIMEOUT_ENDED, json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public void sendLeaveSoloRoom(String roomName){
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            mSocket.emit(SocketConstants.LEAVE_SOLO_ROOM, json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public void sendLeaveMultiRoom(String roomName, String playerName){
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            json.put(SocketConstants.PLAYER_NAME, playerName);
            mSocket.emit(SocketConstants.LEAVE_MULTI_ROOM, json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    public void sendPelletTaken(String roomName, int index){
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, roomName);
            json.put(SocketConstants.PELLET_INDEX, index);
            mSocket.emit(SocketConstants.PELLET_TAKEN, json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    /***
     * CONNECT
     ***/
    private void connect() {
        mSocket.connect();
    }

    /***
     * FLAGS
     ***/

    public SocketFlags getConnectionRequestFlags() {
        return connectionRequestFlags;
    }

    public SocketFlags getRegisterRequestFlags() {
        return registerRequestFlags;
    }

    public SocketFlags getGetRoomListRequestFlags() {
        return getRoomListRequestFlags;
    }

    public SocketFlags getRoomCreationRequestFlags() {
        return roomCreationRequestFlags;
    }

    public SocketFlags getSoloRoomCreationRequestFlags() {
        return soloRoomCreationRequestFlags;
    }

    public SocketFlags getJoinRoomRequestFlags() {
        return joinRoomRequestFlags;
    }
}
