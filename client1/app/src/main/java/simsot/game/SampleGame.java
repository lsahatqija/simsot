package simsot.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simsot.framework.Screen;
import simsot.framework.implementation.AndroidGame;
import simsot.game.screen.SplashLoadingScreen;
import simsot.model.IntentParameters;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;
import simsot.view.MusicManager;

public class SampleGame extends AndroidGame {

    public static String map;
    boolean firstTimeCreate = true;

    private String playerName;
    private boolean isHost;
    private String roomName;
    private boolean isMultiMode;
    private int ghostMovespeed;

    private volatile List<JSONObject> receivedCharacterChoiceJSONList;

    private volatile Map<String, JSONObject> receivedCharacterPositionJSONMap;

    private volatile List<Integer> pelletTakenList;

    private volatile boolean gameCanStart;

    private MySocket mySocket;

    private volatile String customMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        playerName = intent.getStringExtra(IntentParameters.USER_LOGIN);
        isHost = intent.getBooleanExtra(IntentParameters.IS_HOST, false);
        roomName = intent.getStringExtra(IntentParameters.ROOM_NAME);
        isMultiMode = intent.getBooleanExtra(IntentParameters.IS_MULTI_MODE, false);
        customMap = intent.getStringExtra(IntentParameters.MAP);;
        ghostMovespeed = intent.getIntExtra(IntentParameters.GHOST_MOVESPEED, 4);

        receivedCharacterChoiceJSONList = new ArrayList<JSONObject>();

        receivedCharacterPositionJSONMap = new HashMap<String, JSONObject>();

        pelletTakenList = new ArrayList<Integer>();

        gameCanStart = false;

        mySocket = MySocket.getInstance();

        mySocket.on(SocketConstants.CHARACTER_CHOICE_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    receivedCharacterChoiceJSONList.add((JSONObject) args[0]);
                } else {
                    Log.e("SocketError", SocketConstants.CHARACTER_CHOICE_RESPONSE + " args[0]  not instanceof JSONObject");
                }
            }
        });

        mySocket.on(SocketConstants.CHARACTER_POSITION_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    try {
                        String name = ((JSONObject) args[0]).getString(SocketConstants.PLAYER_NAME);

                        if (!playerName.equals(name)) {
                            receivedCharacterPositionJSONMap.put(name, (JSONObject) args[0]);
                        }
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage(), e);
                    }
                } else {
                    Log.e("SocketError", SocketConstants.CHARACTER_POSITION_RESPONSE + " args[0]  not instanceof JSONObject");
                }
            }
        });

        mySocket.on(SocketConstants.CHARACTER_TIMEOUT_ENDED_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    try {
                        int errorCode = ((JSONObject) args[0]).getInt(SocketConstants.ERROR_CODE);

                        if (errorCode == 0) {
                            gameCanStart = true;
                        } else {
                            Log.e("SocketError", SocketConstants.CHARACTER_TIMEOUT_ENDED_RESPONSE + "errorCode isn't 0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("SocketError", SocketConstants.CHARACTER_TIMEOUT_ENDED_RESPONSE + " args[0]  not instanceof JSONObject");
                }
            }
        });

        mySocket.on(SocketConstants.PELLET_TAKEN_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    try {
                        int pelletIndex = ((JSONObject) args[0]).getInt(SocketConstants.PELLET_INDEX);

                        pelletTakenList.add(pelletIndex);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("SocketError", SocketConstants.PELLET_TAKEN_RESPONSE + " args[0]  not instanceof JSONObject");
                }
            }
        });

    }

    @Override
    public Screen getInitScreen() {

        if (firstTimeCreate) {
            Assets.load(this);
            firstTimeCreate = false;
        }

        InputStream is = getResources().openRawResource(R.raw.map1);
        map = convertStreamToString(is);

        return new SplashLoadingScreen(this);

    }

    @Override
    public void onBackPressed() {
        getCurrentScreen().backButton();
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            Log.w("LOG", e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.w("LOG", e.getMessage());
            }
        }
        return sb.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        MusicManager.getInstance().start(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        MusicManager.getInstance().pause();
    }

    public MySocket getMySocket() {
        return mySocket;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getGhostMovespeed() {
        return ghostMovespeed;
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean isMultiMode() {
        return isMultiMode;
    }

    public List<JSONObject> getReceivedCharacterChoiceJSONList() {
        return receivedCharacterChoiceJSONList;
    }

    public Map<String, JSONObject> getReceivedCharacterPositionJSONMap() {
        return receivedCharacterPositionJSONMap;
    }

    public List<Integer> getPelletTakenList() {
        return pelletTakenList;
    }

    public boolean isGameCanStart() {
        return gameCanStart;
    }

    public void setGameCanStart(boolean gameCanStart) {
        this.gameCanStart = gameCanStart;
    }

    public String getMap() {
        if (customMap != null && !IntentParameters.NO_MAP.equals(customMap)) {
            return customMap;
        } else {
            return map;
        }
    }

    public void leaveGame() {
        if (isMultiMode()) {
            mySocket.sendLeaveMultiRoom(roomName, playerName);

        } else {
            mySocket.sendLeaveSoloRoom(roomName);
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.e("InterruptedException", e.getMessage(), e);
        }

        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
