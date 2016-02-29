package simsot.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;

import java.net.URISyntaxException;

import simsot.game.R;
import simsot.game.SampleGame;
import simsot.model.User;
import simsot.socket.MySocket;

public class ConnectionActivity extends Activity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";
    private static final String CONNECTED = "Connected";
    private static final String REGISTERED = "Registered";
    private static final String ACTUAL_LAYOUT = "actualLayout";

    private static final String CONNECTION_RESPONSE = "response_connect";
    private static final String REGISTRATION_RESPONSE = "response_subscribe";

    private static final String LOGIN_IN_PREFERENCES = "login";

    private MySocket mSocket;

    private String userLogin = null;

    private int logoCounter = 0;

    Button directGameButton, registrationChoiceButton, connectButton, registerButton, disconnectButton, backToConnectionButton;
    Button buttonSolo, buttonMulti, buttonSettings, buttonHow;
    EditText userPseudoConnection, userPasswordConnection, userPseudoRegistration, userPasswordRegistration, userPassword2Registration;
    TextView welcomeText;
    LinearLayout layoutConnection, layoutRegistration;
    RelativeLayout layoutMenu;
    ImageView menuLogo;

    private enum ConnectionActivityActualLayout {
        LAYOUTCONNECTION,
        LAYOUTREGISTRATION,
        LAYOUTMENU;
    }

    ;

    ConnectionActivityActualLayout actualLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        initComponents();
        initComponentsEvents();
        initSocket();

        if(savedInstanceState != null){
            actualLayout = (ConnectionActivityActualLayout) savedInstanceState.getSerializable(ACTUAL_LAYOUT);
        }

        if (actualLayout == null) {
            SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
            String login = settings.getString(LOGIN_IN_PREFERENCES, null);
            if (login == null) {
                displayConnectionLayout();
            } else {
                userLogin = login;
                displayMenuLayout();
            }
        } else {
            switch (actualLayout) {
                case LAYOUTCONNECTION:
                    displayConnectionLayout();
                    break;
                case LAYOUTREGISTRATION:
                    displayRegistrationLayout();
                    break;
                case LAYOUTMENU:
                    displayMenuLayout();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(ACTUAL_LAYOUT, actualLayout);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        actualLayout = (ConnectionActivityActualLayout) savedInstanceState.getSerializable(ACTUAL_LAYOUT);
    }


    protected void initComponents() {
        directGameButton = (Button) findViewById(R.id.directGameButton);

        layoutConnection = (LinearLayout) findViewById(R.id.layoutConnection);
        layoutRegistration = (LinearLayout) findViewById(R.id.layoutRegistration);
        layoutMenu = (RelativeLayout) findViewById(R.id.layoutMenu);

        initConnectionLayoutComponents();
        initRegistrationLayoutComponents();
        initMenuLayoutComponents();
    }

    protected void initConnectionLayoutComponents() {
        userPseudoConnection = (EditText) findViewById(R.id.userPseudoConnection);
        userPasswordConnection = (EditText) findViewById(R.id.userPasswordConnection);
        registrationChoiceButton = (Button) findViewById(R.id.registrationChoiceButton);
        connectButton = (Button) findViewById(R.id.connectButton);
    }

    protected void initRegistrationLayoutComponents() {
        userPseudoRegistration = (EditText) findViewById(R.id.userPseudoRegistration);
        userPasswordRegistration = (EditText) findViewById(R.id.userPasswordRegistration);
        userPassword2Registration = (EditText) findViewById(R.id.userPassword2Registration);
        registerButton = (Button) findViewById(R.id.registerButton);
        backToConnectionButton = (Button) findViewById(R.id.backToConnectionButton);
    }

    protected void initMenuLayoutComponents() {
        menuLogo = (ImageView) findViewById(R.id.menuLogo);
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        buttonSolo = (Button) findViewById(R.id.buttonSolo);
        buttonMulti = (Button) findViewById(R.id.buttonMulti);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);
        buttonHow = (Button) findViewById(R.id.buttonHow);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
    }

    protected void initComponentsEvents() {
        initConnectionLayoutComponentsEvents();
        initRegistrationLayoutComponentsEvents();
        initMenuLayoutComponentsEvents();

        directGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, SampleGame.class);
                intent.putExtra("userLogin", "player_test");
                startActivity(intent);
            }
        });
    }

    protected void initConnectionLayoutComponentsEvents() {
        registrationChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRegistrationLayout();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPseudo = userPseudoConnection.getText().toString();
                String userPassword = userPasswordConnection.getText().toString();

                User user = new User(userPseudo, userPassword);

                try {
                    mSocket.sendConnectionRequest(user.ToJSONObject());
                } catch (JSONException e) {
                    // TODO manage exception
                    e.printStackTrace();
                }

                // On note le pseudo pour le passer à la 2e activité
                userLogin = user.getUserLogin();
            }
        });
    }

    protected void initRegistrationLayoutComponentsEvents() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPseudo = userPseudoRegistration.getText().toString();
                String userPassword = userPasswordRegistration.getText().toString();
                String userPassword2 = userPassword2Registration.getText().toString();

                if (userPassword.equals(userPassword2)) {
                    User user = new User(userPseudo, userPassword);

                    try {
                        mSocket.sendRegistrationRequest(user.ToJSONObject());
                    } catch (JSONException e) {
                        // TODO manage exception
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.password_not_match, Toast.LENGTH_SHORT).show();
                }
            }
        });

        backToConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConnectionLayout();
            }
        });
    }

    protected void initMenuLayoutComponentsEvents() {
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
                if (logoCounter == 42) {
                    Toast.makeText(ConnectionActivity.this, "Nice, you find the answer", Toast.LENGTH_LONG).show();
                    logoCounter = 0;
                }
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
    }

    protected void initSocket() {
        try {
            mSocket = new MySocket(SERVER_URL);

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
    }

    protected void displayConnectionLayout() {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.VISIBLE);
                layoutMenu.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.INVISIBLE);
            }
        });
        actualLayout = ConnectionActivityActualLayout.LAYOUTCONNECTION;
    }

    protected void displayRegistrationLayout() {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                layoutConnection.setVisibility(View.INVISIBLE);
                layoutMenu.setVisibility(View.INVISIBLE);
                layoutRegistration.setVisibility(View.VISIBLE);
            }
        });
        actualLayout = ConnectionActivityActualLayout.LAYOUTREGISTRATION;
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
        actualLayout = ConnectionActivityActualLayout.LAYOUTMENU;
    }

    protected void showToast(final String message) {
        ConnectionActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
