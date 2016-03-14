package simsot.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simsot.game.R;
import simsot.model.IntentParameters;
import simsot.model.Room;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class MultiModeActivity extends Activity {

    private static final String ACTUAL_LAYOUT = "actualLayout";

    private static final String LOGIN_IN_PREFERENCES = "login";

    private static final boolean IS_HOST = true;
    private static final boolean IS_NOT_HOST = false;

    private static final int NB_PLAYERS = 5;

    private LinearLayout joinCreateRoomChoiceLayout, createRoomLayout, joinRoomLayout;
    private Button buttonJoinChoice, buttonCreateChoice, createRoomButton, refreshRoomsButton, joinCreateRoomChoiceBackButton, createRoomBackButton, joinRoomBackButton;

    private RadioButton roomPasswordOffRadio, roomPasswordOnRadio;
    private EditText roomNameCreation, roomPasswordCreation, roomDistanceMaxCreation;

    private MySocket mySocket;

    private ListView roomList;
    private TextView notRoomFoundText;

    private List<Room> foundRooms;

    private Location location_GPS;
    private Criteria criteria;
    private LocationManager locationManager;
    private String location_string;
    private LocationListener locationListener;

    private enum MultiModeActivityActualLayout {
        JOINCREATEROOMCHOICE,
        JOINROOM,
        CREATEROOM
    }

    private MultiModeActivityActualLayout actualLayout;

    private JSONArray jsonArrayReceived = null;

    private Room selectedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_mode);

        initComponents();
        initComponentsEvents();
        initSocket();
        initGPSVariables();
        initGPSLocation();

        if (savedInstanceState != null) {
            actualLayout = (MultiModeActivityActualLayout) savedInstanceState.getSerializable(ACTUAL_LAYOUT);
        }

        if (actualLayout == null) {
            displayJoinCreateRoomChoiceLayout();
        } else {
            switch (actualLayout) {
                case JOINCREATEROOMCHOICE:
                    displayJoinCreateRoomChoiceLayout();
                    break;
                case JOINROOM:
                    displayJoinRoomLayout();

                    sendGetListRoomRequest();
                    break;
                case CREATEROOM:
                    displayCreateRoomLayout();
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
        actualLayout = (MultiModeActivityActualLayout) savedInstanceState.getSerializable(ACTUAL_LAYOUT);
    }

    protected void initComponents() {
        joinCreateRoomChoiceLayout = (LinearLayout) findViewById(R.id.joinCreateRoomChoiceLayout);
        joinRoomLayout = (LinearLayout) findViewById(R.id.joinRoomLayout);
        createRoomLayout = (LinearLayout) findViewById(R.id.createRoomLayout);

        initJoinCreateRoomChoiceLayoutComponents();
        initJoinRoomLayoutComponents();
        initCreateRoomLayoutComponents();
    }

    protected void initJoinCreateRoomChoiceLayoutComponents() {
        buttonJoinChoice = (Button) findViewById(R.id.buttonJoinChoice);
        buttonCreateChoice = (Button) findViewById(R.id.buttonCreateChoice);
        joinCreateRoomChoiceBackButton = (Button) findViewById(R.id.joinCreateRoomChoiceBackButton);
    }

    protected void initJoinRoomLayoutComponents() {
        roomList = (ListView) findViewById(R.id.roomList);

        refreshRoomsButton = (Button) findViewById(R.id.refreshRoomsButton);
        joinRoomBackButton = (Button) findViewById(R.id.joinRoomBackButton);

        notRoomFoundText = (TextView) findViewById(R.id.notRoomFoundText);

        foundRooms = new ArrayList<Room>();
    }

    protected void initCreateRoomLayoutComponents() {
        roomPasswordOffRadio = (RadioButton) findViewById(R.id.roomPasswordOffRadio);
        roomPasswordOnRadio = (RadioButton) findViewById(R.id.roomPasswordOnRadio);
        roomNameCreation = (EditText) findViewById(R.id.roomNameCreation);
        roomPasswordCreation = (EditText) findViewById(R.id.roomPasswordCreation);
        roomDistanceMaxCreation = (EditText) findViewById(R.id.roomDistanceMaxCreation);
        createRoomButton = (Button) findViewById(R.id.createRoomButton);
        createRoomBackButton = (Button) findViewById(R.id.createRoomBackButton);
    }

    protected void initComponentsEvents() {
        initComponentsJoinCreateRoomChoiceLayoutEvents();
        initJoinRoomLayoutComponentsEvents();
        initCreateRoomLayoutComponentsEvents();
    }

    protected void initComponentsJoinCreateRoomChoiceLayoutEvents() {
        buttonJoinChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayJoinRoomLayout();
                sendGetListRoomRequest();
            }
        });

        buttonCreateChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateRoomLayout();
            }
        });

        joinCreateRoomChoiceBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    protected void initJoinRoomLayoutComponentsEvents() {

        refreshRoomsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGetListRoomRequest();
            }
        });


        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRoom = foundRooms.get(position);
                String roomName = selectedRoom.getRoomName();

                JSONObject json = new JSONObject();
                try {
                    json.put(SocketConstants.ROOM_NAME, roomName);
                    json.put(SocketConstants.PLAYER_NAME, getSharedPreferencesUserLogin());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mySocket.sendJoinRoomRequest(json);

                ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.JOIN_ROOM_REQUEST);
                progressTask.execute();
            }
        });

        joinRoomBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayJoinCreateRoomChoiceLayout();
            }
        });

    }

    protected void initCreateRoomLayoutComponentsEvents() {
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Room room;
                    if (roomPasswordOffRadio.isChecked()) {
                        room = new Room(roomNameCreation.getText().toString(), getSharedPreferencesUserLogin(), null,
                                Integer.valueOf(roomDistanceMaxCreation.getText().toString()));
                    } else {
                        room = new Room(roomNameCreation.getText().toString(), roomPasswordCreation.getText().toString(), getSharedPreferencesUserLogin(), null,
                                Integer.valueOf(roomDistanceMaxCreation.getText().toString()));
                    }
                    mySocket.sendNewRoomRequest(room.ToJSONObject());

                    ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.NEW_ROOM_REQUEST);
                    progressTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        roomPasswordOffRadio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                roomPasswordCreation.setVisibility(v.INVISIBLE);
                roomPasswordOnRadio.setChecked(false);
            }
        });

        roomPasswordOnRadio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                roomPasswordCreation.setVisibility(v.VISIBLE);
                roomPasswordOffRadio.setChecked(false);
            }
        });

        createRoomBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayJoinCreateRoomChoiceLayout();
            }
        });
    }

    protected void initSocket() {
        mySocket = MySocket.getInstance();

        mySocket.on(SocketConstants.LIST_ROOM, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.getGetRoomListRequestFlags().isSendingFlag()) {
                    mySocket.getGetRoomListRequestFlags().setSendingFlag(false);

                    if (args[0] instanceof JSONObject) {
                        try {
                            jsonArrayReceived = ((JSONObject) args[0]).getJSONArray(SocketConstants.ROOMS);
                        } catch (JSONException e) {
                            Log.e("SocketError", "error getJSONArray(SocketConstants.ROOMS)");
                            jsonArrayReceived = null;
                        }
                    } else {
                        Log.e("SocketError", "ListRoomResponse not a JSONObject");
                        jsonArrayReceived = null;
                    }

                    mySocket.getGetRoomListRequestFlags().setResponseFlag(true);
                }
            }
        });

        mySocket.on(SocketConstants.JOIN_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.getJoinRoomRequestFlags().isSendingFlag()) {
                    mySocket.getJoinRoomRequestFlags().setSendingFlag(false);
                    mySocket.getJoinRoomRequestFlags().setResponseFlag(true);

                    onJoinResponse(args[0]);
                }
            }
        });

        mySocket.on(SocketConstants.CREATE_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.getRoomCreationRequestFlags().isSendingFlag()) {
                    mySocket.getRoomCreationRequestFlags().setSendingFlag(false);
                    mySocket.getRoomCreationRequestFlags().setResponseFlag(true);

                    onCreateResponse(args[0]);
                }
            }
        });
    }

    protected void onJoinResponse(Object response) {
        if (response instanceof JSONObject) {
            JSONObject joinResponse = (JSONObject) response;
            try {
                int errorCode = joinResponse.getInt(SocketConstants.ERROR_CODE);
                if (errorCode == 0) {
                    Intent intent = new Intent(MultiModeActivity.this, RoomActivity.class);
                    intent.putExtra(IntentParameters.IS_HOST, IS_NOT_HOST);
                    intent.putExtra(IntentParameters.ROOM_NAME, selectedRoom.getRoomName());
                    intent.putExtra(IntentParameters.HOST, selectedRoom.getHost());
                    startActivity(intent);
                } else if (errorCode == 1) {
                    showToast(getString(R.string.room_not_found));
                } else if (errorCode == 2) {
                    showToast(getString(R.string.room_full));
                } else if (errorCode == 3) {
                    showToast(getString(R.string.player_already_in_room));
                } else {
                    showToast(getString(R.string.join_failed));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            showToast(getString(R.string.join_error));
        }
    }

    protected void onCreateResponse(Object response) {
        if (response instanceof JSONObject) {
            JSONObject creationResponse = (JSONObject) response;

            try {
                int errorCode = creationResponse.getInt(SocketConstants.ERROR_CODE);
                if (errorCode == 0) {
                    String roomName = creationResponse.getString(SocketConstants.ROOM_NAME);
                    String host = creationResponse.getString(SocketConstants.HOST);

                    showToast(getString(R.string.room_created));

                    Intent intent = new Intent(MultiModeActivity.this, RoomActivity.class);
                    intent.putExtra(IntentParameters.IS_HOST, IS_HOST);
                    intent.putExtra(IntentParameters.ROOM_NAME, roomName);
                    intent.putExtra(IntentParameters.HOST, host);

                    startActivity(intent);
                } else if (errorCode == 1) {
                    // TODO message a preciser
                    showToast("Creation fail");
                } else {
                    showToast("Creation failed");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            showToast("Creation error");
        }
    }

    protected void sendGetListRoomRequest(){
        mySocket.sendGetListRoomRequest();

        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.GET_LIST_ROOM);
        progressTask.execute();
    }

    protected void updateRoomsList() {
        if(foundRooms.isEmpty()){
            notRoomFoundText.setVisibility(View.VISIBLE);
            roomList.setVisibility(View.INVISIBLE);
        } else {
            List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> element;
            for (Room room : foundRooms) {
                element = new HashMap<String, String>();

                element.put("title", "Room " + room.getRoomName() + " by " + room.getHost());
                element.put("subtitle", "Number of players: " + (NB_PLAYERS - room.getSlotEmpty()) + "/" + NB_PLAYERS);
                liste.add(element);
            }

            ListAdapter adapter = new SimpleAdapter(MultiModeActivity.this,
                    liste,
                    android.R.layout.simple_list_item_2,
                    new String[]{"title", "subtitle"},
                    new int[]{android.R.id.text1, android.R.id.text2});
            roomList.setAdapter(adapter);
            notRoomFoundText.setVisibility(View.INVISIBLE);
            roomList.setVisibility(View.VISIBLE);
        }
    }

    protected void displayJoinCreateRoomChoiceLayout() {
        MultiModeActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                joinCreateRoomChoiceLayout.setVisibility(View.VISIBLE);
                joinRoomLayout.setVisibility(View.INVISIBLE);
                createRoomLayout.setVisibility(View.INVISIBLE);
            }
        });
        actualLayout = MultiModeActivityActualLayout.JOINCREATEROOMCHOICE;
    }

    protected void displayJoinRoomLayout() {
        MultiModeActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                joinCreateRoomChoiceLayout.setVisibility(View.INVISIBLE);
                joinRoomLayout.setVisibility(View.VISIBLE);
                createRoomLayout.setVisibility(View.INVISIBLE);
            }
        });
        actualLayout = MultiModeActivityActualLayout.JOINROOM;
    }

    protected void displayCreateRoomLayout() {
        MultiModeActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                joinCreateRoomChoiceLayout.setVisibility(View.INVISIBLE);
                joinRoomLayout.setVisibility(View.INVISIBLE);
                createRoomLayout.setVisibility(View.VISIBLE);
            }
        });
        actualLayout = MultiModeActivityActualLayout.CREATEROOM;
    }

    protected void showToast(final String message) {
        MultiModeActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected String getSharedPreferencesUserLogin() {
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        return settings.getString(LOGIN_IN_PREFERENCES, null);
    }

    protected class ProgressTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        private SocketConstants.SocketRequestType socketRequestType;

        private boolean getListRoomReceived;

        public ProgressTask(SocketConstants.SocketRequestType socketRequestType) {
            this.socketRequestType = socketRequestType;
            this.getListRoomReceived = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MultiModeActivity.this);
            progressDialog.setMessage(getString(R.string.loading_message));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            switch (socketRequestType) {
                case GET_LIST_ROOM:
                    for (long i = 0; i < SocketConstants.REQUEST_TIMEOUT; i += SocketConstants.RESPONSE_CHECK_TIME) {
                        try {
                            Thread.sleep(SocketConstants.RESPONSE_CHECK_TIME);
                        } catch (InterruptedException e) {
                            Log.e("InterruptedException", e.getMessage(), e);
                        }
                        if (mySocket.getGetRoomListRequestFlags().isResponseFlag()) {
                            break;
                        }
                    }
                    if (mySocket.getGetRoomListRequestFlags().isResponseFlag()) {
                        mySocket.getGetRoomListRequestFlags().setResponseFlag(false);
                        getListRoomReceived = true;
                    } else {
                        mySocket.getGetRoomListRequestFlags().setSendingFlag(false);
                        showToast("No server answer not received");
                    }
                    break;
                case NEW_ROOM_REQUEST:
                    for (long i = 0; i < SocketConstants.REQUEST_TIMEOUT; i += SocketConstants.RESPONSE_CHECK_TIME) {
                        try {
                            Thread.sleep(SocketConstants.RESPONSE_CHECK_TIME);
                        } catch (InterruptedException e) {
                            Log.e("InterruptedException", e.getMessage(), e);
                        }
                        if (mySocket.getRoomCreationRequestFlags().isResponseFlag()) {
                            break;
                        }
                    }
                    if (mySocket.getRoomCreationRequestFlags().isResponseFlag()) {
                        mySocket.getRoomCreationRequestFlags().setResponseFlag(false);
                    } else {
                        mySocket.getRoomCreationRequestFlags().setSendingFlag(false);
                        showToast("No server answer not received");
                    }
                    break;
                case JOIN_ROOM_REQUEST:
                    for (long i = 0; i < SocketConstants.REQUEST_TIMEOUT; i += SocketConstants.RESPONSE_CHECK_TIME) {
                        try {
                            Thread.sleep(SocketConstants.RESPONSE_CHECK_TIME);
                        } catch (InterruptedException e) {
                            Log.e("InterruptedException", e.getMessage(), e);
                        }
                        if (mySocket.getJoinRoomRequestFlags().isResponseFlag()) {
                            break;
                        }
                    }
                    if (mySocket.getJoinRoomRequestFlags().isResponseFlag()) {
                        mySocket.getJoinRoomRequestFlags().setResponseFlag(false);
                    } else {
                        mySocket.getJoinRoomRequestFlags().setSendingFlag(false);
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

            if(SocketConstants.SocketRequestType.GET_LIST_ROOM.equals(socketRequestType) && getListRoomReceived) {
                foundRooms.clear();
                if (jsonArrayReceived != null) {
                    for (int i = 0; i < jsonArrayReceived.length(); i++) {
                        try {
                            JSONObject jsonObject = (JSONObject) jsonArrayReceived.get(i);

                            String roomName = jsonObject.getString(SocketConstants.ROOM_NAME);
                            String host = jsonObject.getString(SocketConstants.HOST);
                            int slot_empty = jsonObject.getInt(SocketConstants.SLOT_EMPTY);

                            foundRooms.add(new Room(roomName, host, slot_empty));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                updateRoomsList();
                jsonArrayReceived = null;
            }
        }

    }


    protected void initGPSVariables(){
        criteria = new Criteria();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location_string = locationManager.getBestProvider(criteria, true);
        location_GPS = locationManager.getLastKnownLocation(location_string);
        //double latitude = (double) (location_GPS.getLatitude());
        //double longitude = (double) (location_GPS.getLongitude());
        //showToast("Last position : " + latitude +" and " + longitude);
    }

    protected void initGPSLocation() {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showToast("GPS is disabled!");
            buildAlertMessageNoGps();
        }
        // corriger si GPS pas activé et en cours d'activation

        locationListener = new LocationListener() {
            Criteria criteria = new Criteria();

            public void onLocationChanged(Location location) {
                set_location(location);
                locationManager.removeUpdates(this);
                showToast("Location detected");
                double latitude = (double) (location.getLatitude());
                double longitude = (double) (location.getLongitude());
                showToast("Last position : " + latitude +" and " + longitude);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
                showToast("Location enabled");
            }

            public void onProviderDisabled(String provider) {
                showToast("Location disabled");
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 1000, (float) 0, locationListener);
    }

    private void set_location(Location location){
        location_GPS=location;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}


