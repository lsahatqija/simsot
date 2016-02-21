package simsot.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import simsot.framework.Screen;
import simsot.framework.implementation.AndroidGame;
import simsot.socket.MySocket;

public class SampleGame extends AndroidGame {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com/";
    private static final String USER_LOGIN = "userLogin";
	
	public static String map;
    boolean firstTimeCreate = true;

    private String playerName;

    private MySocket mySocket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO manage when userLogin is null
        playerName = getIntent().getStringExtra(USER_LOGIN);

        try {
            mySocket = new MySocket(SERVER_URL, playerName);
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


}
