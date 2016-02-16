package simsot.game;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ConnectionActivity extends Activity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";
    private static final String CONNECTED = "Connecté";
    private static final String CONNECTION_RESPONSE = "response_connect";
    private static final String REGISTRATION_RESPONSE = "response_subscribe";

    private Socket mSocket;

    public static String pseudo = "error";

    Button directGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        directGameButton = (Button) findViewById(R.id.directGameButton);
        directGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, SampleGame.class);
                startActivity(intent);
            }
        });

        try {
            mSocket = IO.socket(SERVER_URL);

            // Messages de connexion
            mSocket.on(CONNECTION_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if(args[0] instanceof String){
                        String connectionResponse = (String) args[0];
                        // On lance MenuActivity si on se connecte, sinon on affiche le message serveur
                        if (CONNECTED.equals(connectionResponse)) {
                            Intent MenuActivity = new Intent(ConnectionActivity.this, MenuActivity.class);
                            MenuActivity.putExtra("pseudo", pseudo);
                            startActivity(MenuActivity);
                        } else {
                            showToast(args[0].toString());
                        }
                    } else{
                        showToast("Connection error");
                    }
                }
            });

            // Messages d'inscription
            mSocket.on(REGISTRATION_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (args[0] instanceof String) {
                        String registrationResponse = (String) args[0];
                        showToast(registrationResponse);
                    } else {
                        //TODO Remove before prod
                        showToast("error : args[0] isn't a String");
                    }
                }
            });
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Button connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPseudo = ((EditText) findViewById(R.id.userPseudo)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.userPassword)).getText().toString();

                JSONObject data = new JSONObject();

                try {
                    data.put("pseudo", userPseudo);
                    data.put("password", userPassword);
                } catch (JSONException e) {
                    return;
                }
                mSocket.emit("connect_user", data);

                // On note le pseudo pour le passer à la 2e activité
                pseudo = userPseudo;
            }

            ;
        });

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPseudo = ((EditText) findViewById(R.id.userPseudo)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.userPassword)).getText().toString();
                String userPassword2 = ((EditText) findViewById(R.id.userPassword2)).getText().toString();

                if (!userPassword.equals(userPassword2)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match.", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject data = new JSONObject();

                    try {
                        data.put("pseudo", userPseudo);
                        data.put("password", userPassword);
                    } catch (JSONException e) {
                        return;
                    }

                    mSocket.emit("subscribe", data);
                }
            }

            ;
        });
    }

    protected void showToast(final String message) {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
