package simsot.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import simsot.game.R;
import simsot.game.SampleGame;

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

    private String userLogin = null;

    private int logoCounter = 0;

    Button directGameButton, registrationChoiceButton, connectButton, registerButton, disconnectButton, backToConnectionButton;
    Button buttonSolo, buttonMulti, buttonSettings, buttonHow;
    TextView welcomeText;
    LinearLayout layoutConnection, layoutRegistration;
    RelativeLayout layoutMenu;
    ImageView menuLogo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        directGameButton = (Button) findViewById(R.id.directGameButton);

        registrationChoiceButton = (Button) findViewById(R.id.registrationChoiceButton);

        connectButton = (Button) findViewById(R.id.connectButton);

        registerButton = (Button) findViewById(R.id.registerButton);

        backToConnectionButton = (Button) findViewById(R.id.backToConnectionButton);

        disconnectButton = (Button) findViewById(R.id.disconnectButton);

        layoutConnection = (LinearLayout) findViewById(R.id.layoutConnection);
        layoutRegistration = (LinearLayout) findViewById(R.id.layoutRegistration);
        layoutMenu = (RelativeLayout) findViewById(R.id.layoutMenu);

        buttonSolo = (Button) findViewById(R.id.buttonSolo);
        buttonMulti = (Button) findViewById(R.id.buttonMulti);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);
        buttonHow = (Button) findViewById(R.id.buttonHow);

        welcomeText = (TextView) findViewById(R.id.welcomeText);

        menuLogo = (ImageView) findViewById(R.id.menuLogo);

        directGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, SampleGame.class);
                intent.putExtra("userLogin", "player_test");
                startActivity(intent);
            }
        });

        registrationChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRegistrationLayout();
            }
        });

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove(LOGIN_IN_PREFERENCES);
                editor.commit();
                userLogin = null;

                displayConnectionLayout();
            }
        });

        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        String login = settings.getString(LOGIN_IN_PREFERENCES, null);
        if (login == null) {
            displayConnectionLayout();
        } else {
            userLogin = login;
            displayMenuLayout();
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

                            displayMenuLayout();
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

        backToConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConnectionLayout();
            }
        });

        buttonSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, SampleGame.class);
                intent.putExtra("userLogin", userLogin);
                startActivity(intent);
            }
        });

        buttonMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, MultiModeActivity.class);
                intent.putExtra("userLogin", userLogin);
                startActivity(intent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConnectionActivity.this, "Not implemented yet !", Toast.LENGTH_SHORT).show();
            }
        });

        buttonHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ConnectionActivity.this, "Not implemented yet !", Toast.LENGTH_SHORT).show();
            }
        });

        menuLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoCounter++;
                if(logoCounter==42){
                    Toast.makeText(ConnectionActivity.this, "Nice, you find the answer", Toast.LENGTH_LONG).show();
                    logoCounter=0;
                }
            }
        });

    }

    protected void displayConnectionLayout() {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.VISIBLE);
                layoutMenu.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void displayMenuLayout() {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutMenu.setVisibility(View.VISIBLE);
                layoutRegistration.setVisibility(View.INVISIBLE);
                welcomeText.setText("Welcome " + userLogin);
            }
        });
    }

    protected void displayRegistrationLayout() {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutMenu.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.VISIBLE);
            }
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
