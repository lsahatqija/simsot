package simsot.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    public String userLogin = null;

    Button directGameButton, connectButton, registerButton, continueButton1, disconnectButton1;
    TextView welcomeText;
    LinearLayout layoutConnection, layoutConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        directGameButton = (Button) findViewById(R.id.directGameButton);
        connectButton = (Button) findViewById(R.id.connectButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        continueButton1 = (Button) findViewById(R.id.continueButton1);
        disconnectButton1 = (Button) findViewById(R.id.disconnectButton1);

        layoutConnection = (LinearLayout) findViewById(R.id.layoutConnection);
        layoutConnected = (LinearLayout) findViewById(R.id.layoutConnected);

        welcomeText = (TextView) findViewById(R.id.welcomeText);

        directGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, SampleGame.class);
                intent.putExtra("userLogin", "player_test");
                startActivity(intent);
            }
        });

        continueButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });

        disconnectButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("login");
                editor.commit();
                userLogin = null;

                displayConnectionLayout();
            }
        });

        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        String login = settings.getString("login", null);
        if (login == null) {
            displayConnectionLayout();

        } else {
            userLogin = login;
            displayConnectedLayout();
        }

        try {
            mSocket = IO.socket(SERVER_URL);

            // Messages de connexion
            mSocket.on(CONNECTION_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (args[0] instanceof String) {
                        String connectionResponse = (String) args[0];
                        if (CONNECTED.equals(connectionResponse)) {
                            SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("login", userLogin);
                            editor.commit();

                            displayConnectedLayout();
                        } else {
                            showToast(args[0].toString());
                        }
                    } else {
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
                userLogin = userPseudo;
            }
        });

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
        });
    }

    protected void displayConnectionLayout(){
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.VISIBLE);
                layoutConnected.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void displayConnectedLayout(){
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutConnected.setVisibility(View.VISIBLE);
                welcomeText.setText("Welcome " + userLogin);
            }
        });
    }

    protected void goToMenu() {
        Intent intent = new Intent(ConnectionActivity.this, MenuActivity.class);
        intent.putExtra("userLogin", userLogin);
        startActivity(intent);
    }

    protected void showToast(final String message) {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
