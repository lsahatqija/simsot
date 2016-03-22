package simsot.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import simsot.game.R;
import simsot.game.SampleGame;
import simsot.model.IntentParameters;
import simsot.model.User;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class ConnectionActivity extends Activity {

    private static final String ACTUAL_LAYOUT = "actualLayout";

    private static final String LOGIN_IN_PREFERENCES = "login";
    private static final String DIFFICULTY_IN_PREFERENCES = "difficulty";

    private MySocket mySocket;

    private int logoCounter = 0;
    private int difficulty = 0;

    private Button registrationChoiceButton, connectButton, registerButton, disconnectButton, backToConnectionButton;
    private Button buttonSolo, buttonMulti, buttonSettings;
    private EditText userPseudoConnection, userPasswordConnection, userPseudoRegistration, userPasswordRegistration, userPassword2Registration;
    private TextView welcomeText;
    private LinearLayout layoutConnection, layoutRegistration, layoutMenu;
    private ImageView menuLogo;

    private enum ConnectionActivityActualLayout {
        LAYOUTCONNECTION,
        LAYOUTREGISTRATION,
        LAYOUTMENU
    }

    private ConnectionActivityActualLayout actualLayout;

    private Location locationGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        initComponents();
        initComponentsEvents();
        initSocket();

        if (savedInstanceState != null) {
            actualLayout = (ConnectionActivityActualLayout) savedInstanceState.getSerializable(ACTUAL_LAYOUT);
        }

        if (actualLayout == null) {
            displayMenuLayoutIfItIsPossible();
        } else {
            switch (actualLayout) {
                case LAYOUTCONNECTION:
                    displayConnectionLayout();
                    break;
                case LAYOUTREGISTRATION:
                    displayRegistrationLayout();
                    break;
                case LAYOUTMENU:
                    displayMenuLayoutIfItIsPossible();
                    break;
                default:
                    break;
            }
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MusicManager.getInstance().release();
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
        layoutConnection = (LinearLayout) findViewById(R.id.layoutConnection);
        layoutRegistration = (LinearLayout) findViewById(R.id.layoutRegistration);
        layoutMenu = (LinearLayout) findViewById(R.id.layoutMenu);

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
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
    }

    protected void initComponentsEvents() {
        initConnectionLayoutComponentsEvents();
        initRegistrationLayoutComponentsEvents();
        initMenuLayoutComponentsEvents();
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

                User newUser = new User(userPseudo, userPassword);

                try {
                    mySocket.sendConnectionRequest(newUser.ToJSONObject());

                    ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.CONNECTION_REQUEST);
                    progressTask.execute();

                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage(), e);
                }
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

                if (userPseudo.isEmpty()) {
                    showToast("Username must not be empty.");
                } else if (userPassword.isEmpty()) {
                    showToast("Password must not be empty.");
                } else if (!userPassword.equals(userPassword2)) {
                    showToast(getString(R.string.password_not_match));
                } else {
                    User user = new User(userPseudo, userPassword);

                    try {
                        mySocket.sendRegistrationRequest(user.ToJSONObject());

                        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.REGISTER_REQUEST);
                        progressTask.execute();
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage(), e);
                    }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionActivity.this);
                builder.setTitle("Custom Map");
                builder.setMessage("Want a custom map ?");
                builder.setPositiveButton(R.string.yes_response, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCustomMapForSoloMode();
                    }
                });
                builder.setNegativeButton(R.string.no_response, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            JSONObject json = new JSONObject();
                            json.put(SocketConstants.ERROR_CODE, 0);
                            json.put(SocketConstants.PLAYER_NAME, getSharedPreferencesUserLogin());
                            sendSoloRoomCreation(json);
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage(), e);
                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        buttonMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, MultiModeActivity.class);
                startActivity(intent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        menuLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoCounter++;
                if (logoCounter == 42) {
                    showToast("Nice, you find the answer");
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

                displayConnectionLayout();
            }
        });
    }

    protected void initSocket() {
        mySocket = MySocket.getInstance();

        mySocket.on(SocketConstants.CONNECTION_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.getConnectionRequestFlags().isSendingFlag()) {
                    mySocket.getConnectionRequestFlags().setSendingFlag(false);
                    mySocket.getConnectionRequestFlags().setResponseFlag(true);

                    onConnectionRequestResponse(args[0]);
                } else {
                    Log.e("SocketError", SocketConstants.CONNECTION_RESPONSE + " received and not asked");
                }
            }
        });

        mySocket.on(SocketConstants.REGISTRATION_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.getRegisterRequestFlags().isSendingFlag()) {
                    mySocket.getRegisterRequestFlags().setSendingFlag(false);
                    mySocket.getRegisterRequestFlags().setResponseFlag(true);

                    onRegistrationRequestResponse(args[0]);
                } else {
                    Log.e("SocketError", SocketConstants.REGISTRATION_RESPONSE + " received and not asked");
                }
            }
        });

        mySocket.on(SocketConstants.CREATE_SOLO_ROOM_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (mySocket.getSoloRoomCreationRequestFlags().isSendingFlag()) {
                    mySocket.getSoloRoomCreationRequestFlags().setSendingFlag(false);
                    mySocket.getSoloRoomCreationRequestFlags().setResponseFlag(true);

                    if (args[0] instanceof JSONObject) {
                        JSONObject soloRoomCreationResponse = (JSONObject) args[0];
                        try {
                            int errorCode = soloRoomCreationResponse.getInt(SocketConstants.ERROR_CODE);
                            if (errorCode == 0) {
                                String roomCreated = soloRoomCreationResponse.getString(SocketConstants.ROOM_NAME);

                                String map = IntentParameters.NO_MAP;
                                if (soloRoomCreationResponse.has(SocketConstants.MAP)) {
                                    map = soloRoomCreationResponse.getString(SocketConstants.MAP);
                                    if (map == null) {
                                        map = IntentParameters.NO_MAP;
                                    }
                                }

                                Intent intent = new Intent(ConnectionActivity.this, SampleGame.class);
                                intent.putExtra(IntentParameters.USER_LOGIN, getSharedPreferencesUserLogin());
                                intent.putExtra(IntentParameters.IS_HOST, true);
                                intent.putExtra(IntentParameters.ROOM_NAME, roomCreated);
                                intent.putExtra(IntentParameters.IS_MULTI_MODE, false);
                                intent.putExtra(IntentParameters.MAP, map);
                                intent.putExtra(IntentParameters.GHOST_MOVESPEED, getSharedPreferencesDifficulty());
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage(), e);
                        }
                    } else {
                        Log.e("SocketError", "CreateSoloRoomResponse not a JSONObject");
                    }
                }
            }
        });
    }

    protected void sendSoloRoomCreation(JSONObject json) {
        mySocket.sendSoloRoomCreation(json);

        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.CREATE_SOLO_ROOM);
        progressTask.execute();
    }

    protected void onConnectionRequestResponse(Object response) {
        if (response instanceof JSONObject) {
            JSONObject connectionResponse = (JSONObject) response;
            try {
                int errorCode = connectionResponse.getInt(SocketConstants.ERROR_CODE);

                if (errorCode == 0) {
                    String newUserName = connectionResponse.getString(SocketConstants.PLAYER_NAME);
                    SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(LOGIN_IN_PREFERENCES, newUserName);
                    editor.commit();

                    showToast(getString(R.string.connection_succeeded));

                    displayMenuLayout();
                } else if (errorCode == 1) {
                    showToast(getString(R.string.username_or_password_incorrect));
                } else {
                    showToast(getString(R.string.connection_failed));
                }
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage(), e);
            }
        } else {
            showToast(getString(R.string.connection_error));
            Log.e("SocketError", "ConnectionRequestResponse not a JSONObject");
        }
    }

    protected void onRegistrationRequestResponse(Object response) {
        if (response instanceof JSONObject) {
            JSONObject registrationResponse = (JSONObject) response;
            try {
                int errorCode = registrationResponse.getInt(SocketConstants.ERROR_CODE);
                if (errorCode == 0) {
                    showToast(getString(R.string.registration_succeeded));

                    displayConnectionLayout();
                } else if (errorCode == 1) {
                    showToast(getString(R.string.user_taken));
                } else if (errorCode == 2) {
                    showToast(getString(R.string.username_or_password_empty));
                } else {
                    showToast(getString(R.string.registration_failed));
                }
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage(), e);
            }

        } else {
            showToast(getString(R.string.registration_error));
            Log.e("SocketError", "RegistrationRequestResponse not a JSONObject");
        }
    }

    protected void displayMenuLayoutIfItIsPossible() {
        if (getSharedPreferencesUserLogin() == null) {
            displayConnectionLayout();
        } else {
            displayMenuLayout();
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
                welcomeText.setText(getResources().getString(R.string.welcome_user, getSharedPreferencesUserLogin()));
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

    protected String getSharedPreferencesUserLogin() {
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        return settings.getString(LOGIN_IN_PREFERENCES, null);
    }

    protected int getSharedPreferencesDifficulty() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return Integer.parseInt(preferences.getString(DIFFICULTY_IN_PREFERENCES, "4"));
    }

    protected class ProgressTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        private SocketConstants.SocketRequestType socketRequestType;

        public ProgressTask(SocketConstants.SocketRequestType socketRequestType) {
            this.socketRequestType = socketRequestType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConnectionActivity.this);
            progressDialog.setMessage(getString(R.string.loading_message));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            boolean responseReceived;
            switch (socketRequestType) {
                case CONNECTION_REQUEST:
                    responseReceived = mySocket.getConnectionRequestFlags().waitResponse();
                    if (!responseReceived) {
                        showToast("No server answer not received");
                    }
                    break;
                case REGISTER_REQUEST:
                    responseReceived = mySocket.getRegisterRequestFlags().waitResponse();
                    if (!responseReceived) {
                        showToast("No server answer not received");
                    }
                    break;
                case CREATE_SOLO_ROOM:
                    responseReceived = mySocket.getSoloRoomCreationRequestFlags().waitResponse();
                    if (!responseReceived) {
                        showToast("No server answer not received");
                    }
                    break;
                default:
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
        }

    }


    protected void getCustomMapForSoloMode() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            GPSAsyncTask gpsAsyncTask = new GPSAsyncTask();
            gpsAsyncTask.execute(locationManager);
        } else {
            buildAlertMessageNoGps();
        }
    }

    private void sendGetMapSoloMode(Location location) {
        try {
            JSONObject json = new JSONObject();
            json.put(SocketConstants.ERROR_CODE, 0);
            json.put(SocketConstants.PLAYER_NAME, getSharedPreferencesUserLogin());
            json.put(SocketConstants.LATITUDE, location.getLatitude());
            json.put(SocketConstants.LONGITUDE, location.getLongitude());
            sendSoloRoomCreation(json);
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage(), e);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS is disabled, you must enable it")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected class GPSAsyncTask extends AsyncTask<LocationManager, Void, Void> implements LocationListener {

        private ProgressDialog progressDialog;
        private Location location = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConnectionActivity.this);
            progressDialog.setMessage(getString(R.string.search_location));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(LocationManager... params) {
            LocationManager locationManager = params[0];

            Looper.prepare();
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            Looper.loop();

            return null;
        }


        @Override
        public void onLocationChanged(Location location) {
            this.location = location;
            Looper.myLooper().quit();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.hide();
            progressDialog.dismiss();
            sendGetMapSoloMode(location);
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
