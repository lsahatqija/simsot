package simsot.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

import simsot.framework.Screen;
import simsot.framework.implementation.AndroidGame;
import simsot.model.IntentParameters;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class SampleGame extends AndroidGame {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com/";

	
	public static String map;
    boolean firstTimeCreate = true;

    private String playerName;
    private boolean isHost;
    private String roomName;

    private boolean characterChoiceReceived = false;
    private JSONObject characterChoiceJSONReceived = null;

    private boolean characterPositionReceived = false;
    private JSONObject characterPositionJSONReceived = null;

    private MySocket mySocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // TODO manage when there are null
        playerName = intent.getStringExtra(IntentParameters.USER_LOGIN);
        isHost = intent.getBooleanExtra(IntentParameters.IS_HOST, false);
        roomName = intent.getStringExtra(IntentParameters.ROOM_NAME);

        try {
            mySocket = new MySocket(SERVER_URL);

            mySocket.on(SocketConstants.CHARACTER_CHOICE_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    characterChoiceJSONReceived = (JSONObject) args[0];
                    characterChoiceReceived = true;
                }
            });

            mySocket.on(SocketConstants.CHARACTER_POSITION_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    characterPositionJSONReceived = (JSONObject) args[0];
                    characterPositionReceived = true;
                }
            });

            mySocket.connect();
        } catch (URISyntaxException e) {
            Toast.makeText(SampleGame.this,"URISyntaxException", Toast.LENGTH_SHORT).show();
        }
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
                sb.append((line + "\n"));
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

    public boolean isCharacterChoiceReceived() {
        return characterChoiceReceived;
    }

    public JSONObject getCharacterChoiceJSONReceived() {
        return characterChoiceJSONReceived;
    }

    public void setCharacterChoiceReceived(boolean characterChoiceReceived) {
        this.characterChoiceReceived = characterChoiceReceived;
    }

    public boolean isCharacterPositionReceived() {
        return characterPositionReceived;
    }

    public JSONObject getCharacterPositionJSONReceived() {
        return characterPositionJSONReceived;
    }

    public void setCharacterPositionReceived(boolean characterPositionReceived) {
        this.characterPositionReceived = characterPositionReceived;
    }
}
