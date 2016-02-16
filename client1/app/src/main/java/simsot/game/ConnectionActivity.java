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
    private static final String CONNECTED = "Connected";
    private static final String REGISTERED = "Registered";
    private static final String CONNECTION_REQUEST = "connect_user";
    private static final String REGISTER_REQUEST = "subscribe";
    private static final String CONNECTION_RESPONSE = "response_connect";
    private static final String REGISTRATION_RESPONSE = "response_subscribe";
    private static final String USERNAME = "pseudo";
    private static final String PASSWORD = "password";

    private static final String LOGIN_IN_PREFERENCES = "login";

    private Socket mSocket;

    public String userLogin = null;

    Button directGameButton,connectionChoiceButton, registrationChoiceButton, connectButton, registerButton, continueButton1, disconnectButton1;
    TextView welcomeText;
    LinearLayout layoutRegistrationConnection, layoutConnection, layoutConnected, layoutRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        directGameButton = (Button) findViewById(R.id.directGameButton);

        connectionChoiceButton = (Button) findViewById(R.id.connectionChoiceButton);
        registrationChoiceButton = (Button) findViewById(R.id.registrationChoiceButton);

        connectButton = (Button) findViewById(R.id.connectButton);

        registerButton = (Button) findViewById(R.id.registerButton);

        continueButton1 = (Button) findViewById(R.id.continueButton1);
        disconnectButton1 = (Button) findViewById(R.id.disconnectButton1);

        layoutRegistrationConnection = (LinearLayout) findViewById(R.id.layoutRegistrationConnection);
        layoutConnection = (LinearLayout) findViewById(R.id.layoutConnection);
        layoutRegistration = (LinearLayout) findViewById(R.id.layoutRegistration);
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

        connectionChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConnectionLayout();
            }
        });


        registrationChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRegistrationLayout();
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
                editor.remove(LOGIN_IN_PREFERENCES);
                editor.commit();
                userLogin = null;

                displayRegistrationConnectionLayout();
            }
        });

        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        String login = settings.getString(LOGIN_IN_PREFERENCES, null);
        if (login == null) {
            displayRegistrationConnectionLayout();
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
                            editor.putString(LOGIN_IN_PREFERENCES, userLogin);
                            editor.commit();

                            showToast(getString(R.string.connection_succeeded));

                            displayConnectedLayout();
                        } else {
                            showToast(getString(R.string.connection_failed));
                        }
                    } else {
                        showToast(getString(R.string.connection_error));
                    }
                }
            });

            // Messages d'inscription
            mSocket.on(REGISTRATION_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (args[0] instanceof String) {
                        String registrationResponse = (String) args[0];
                        if (REGISTERED.equals(registrationResponse)) {
                            showToast(getString(R.string.registration_succeeded));

                            displayConnectionLayout();
                        } else {
                            showToast(getString(R.string.registration_failed));
                        }
                    } else {
                        showToast(getString(R.string.registration_error));
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
                String userPseudo = ((EditText) findViewById(R.id.userPseudoConnection)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.userPasswordConnection)).getText().toString();

                JSONObject data = new JSONObject();

                try {
                    data.put(USERNAME, userPseudo);
                    data.put(PASSWORD, userPassword);
                } catch (JSONException e) {
                    return;
                }
                mSocket.emit(CONNECTION_REQUEST, data);

                // On note le pseudo pour le passer à la 2e activité
                userLogin = userPseudo;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPseudo = ((EditText) findViewById(R.id.userPseudoRegistration)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.userPasswordRegistration)).getText().toString();
                String userPassword2 = ((EditText) findViewById(R.id.userPassword2Registration)).getText().toString();

                if (!userPassword.equals(userPassword2)) {
                    Toast.makeText(getApplicationContext(), R.string.password_not_match, Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject data = new JSONObject();

                    try {
                        data.put(USERNAME, userPseudo);
                        data.put(PASSWORD, userPassword);
                    } catch (JSONException e) {
                        return;
                    }

                    mSocket.emit(REGISTER_REQUEST, data);
                }
            }
        });

    }

    protected void displayRegistrationConnectionLayout(){
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutRegistrationConnection.setVisibility(View.VISIBLE);
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutConnected.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void displayConnectionLayout(){
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutRegistrationConnection.setVisibility(View.INVISIBLE);
                layoutConnection.setVisibility(View.VISIBLE);
                layoutConnected.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void displayConnectedLayout(){
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutRegistrationConnection.setVisibility(View.INVISIBLE);
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutConnected.setVisibility(View.VISIBLE);
                layoutRegistration.setVisibility(View.INVISIBLE);
                welcomeText.setText("Welcome " + userLogin);
            }
        });
    }

    protected void displayRegistrationLayout(){
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutRegistrationConnection.setVisibility(View.INVISIBLE);
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutConnected.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.VISIBLE);
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
