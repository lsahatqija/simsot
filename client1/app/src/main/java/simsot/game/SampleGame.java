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
import simsot.model.IntentParameters;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class SampleGame extends AndroidGame {

    public static String map;
    boolean firstTimeCreate = true;

    private String playerName;
    private boolean isHost;
    private String roomName;
    private boolean isMultiMode;

    private volatile List<JSONObject> receivedCharacterChoiceJSONList;

    private volatile Map<String, JSONObject> receivedCharacterPositionJSONMap;

    private volatile boolean gameCanStart;

    private MySocket mySocket;

    private volatile String customMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // TODO manage when there are null
        playerName = intent.getStringExtra(IntentParameters.USER_LOGIN);
        isHost = intent.getBooleanExtra(IntentParameters.IS_HOST, false);
        roomName = intent.getStringExtra(IntentParameters.ROOM_NAME);
        isMultiMode = intent.getBooleanExtra(IntentParameters.IS_MULTI_MODE, false);
        customMap = intent.getStringExtra(IntentParameters.MAP);

        receivedCharacterChoiceJSONList = new ArrayList<JSONObject>();

        receivedCharacterPositionJSONMap = new HashMap<String, JSONObject>();

        gameCanStart = false;

        mySocket = MySocket.getInstance();

        mySocket.on(SocketConstants.CHARACTER_CHOICE_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    //System.out.println("receiving  : " + ((JSONObject) args[0]).toString());
                    receivedCharacterChoiceJSONList.add((JSONObject) args[0]);
                } else {
                    //TODO manage this error
                    System.out.println(SocketConstants.CHARACTER_CHOICE_RESPONSE + " args[0]  not instanceof JSONObject");
                }
            }
        });

        mySocket.on(SocketConstants.CHARACTER_POSITION_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    try {
                       // System.out.println("receiving  : "+((JSONObject) args[0]).toString());
                        String name = ((JSONObject) args[0]).getString(SocketConstants.PLAYER_NAME);

                        if(!playerName.equals(name)){
                            receivedCharacterPositionJSONMap.put(name,(JSONObject)args[0]);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //TODO manage this error
                    System.out.println(SocketConstants.CHARACTER_POSITION_RESPONSE + " args[0]  not instanceof JSONObject");
                }
            }
        });

        mySocket.on(SocketConstants.CHARACTER_TIMEOUT_ENDED_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args[0] instanceof JSONObject) {
                    try {
                        int errorCode = ((JSONObject) args[0]).getInt(SocketConstants.ERROR_CODE);

                        if(errorCode == 0){
                            gameCanStart = true;
                        } else{
                            // TODO manage else
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //TODO manage this error
                    System.out.println(SocketConstants.CHARACTER_TIMEOUT_ENDED_RESPONSE + " args[0]  not instanceof JSONObject");
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

        Assets.theme.play();

    }

    @Override
    public void onPause() {
        super.onPause();
        Assets.theme.pause();

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

    public boolean isHost() {
        return isHost;
    }

    public boolean isMultiMode() {
        return isMultiMode;
    }

    public List<JSONObject> getReceivedCharacterChoiceJSONList() {
        return receivedCharacterChoiceJSONList;
    }

    public Map<String,JSONObject> getReceivedCharacterPositionJSONMap() {
        return receivedCharacterPositionJSONMap;
    }

    public boolean isGameCanStart() {
        return gameCanStart;
    }

    public void setGameCanStart(boolean gameCanStart) {
        this.gameCanStart = gameCanStart;
    }
}
