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
import android.util.Log;
import android.view.LayoutInflater;
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

    private RadioButton roomPasswordOffRadio, roomPasswordOnRadio, roomCustomMapNo, roomCustomMapYes;
    private EditText roomNameCreation, roomPasswordCreation;

    private MySocket mySocket;

    private ListView roomList;
    private TextView noRoomFoundText;

    private List<Room> foundRooms;

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

        noRoomFoundText = (TextView) findViewById(R.id.noRoomFoundText);

        foundRooms = new ArrayList<Room>();
    }

    protected void initCreateRoomLayoutComponents() {
        roomPasswordOffRadio = (RadioButton) findViewById(R.id.roomPasswordOffRadio);
        roomPasswordOnRadio = (RadioButton) findViewById(R.id.roomPasswordOnRadio);
        roomNameCreation = (EditText) findViewById(R.id.roomNameCreation);
        roomPasswordCreation = (EditText) findViewById(R.id.roomPasswordCreation);
        roomCustomMapNo = (RadioButton) findViewById(R.id.roomCustomMapNo);
        roomCustomMapYes = (RadioButton) findViewById(R.id.roomCustomMapYes);
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

                if (selectedRoom.isPassword()) {
                    buildPasswordRequest(selectedRoom);
                } else {
                    connectToRoom(selectedRoom);
                }
            }
        });

        joinRoomBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayJoinCreateRoomChoiceLayout();
            }
        });

    }

    protected void connectToRoom(Room selectedRoom) {
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, selectedRoom.getRoomName());
            json.put(SocketConstants.PLAYER_NAME, getSharedPreferencesUserLogin());
            json.put(SocketConstants.ROOM_PASSWORD, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mySocket.sendJoinRoomRequest(json);

        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.JOIN_ROOM_REQUEST);
        progressTask.execute();
    }

    protected void connectToRoom(Room selectedRoom, String passwordInput) {
        JSONObject json = new JSONObject();
        try {
            json.put(SocketConstants.ROOM_NAME, selectedRoom.getRoomName());
            json.put(SocketConstants.PLAYER_NAME, getSharedPreferencesUserLogin());
            json.put(SocketConstants.ROOM_PASSWORD, passwordInput);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mySocket.sendJoinRoomRequest(json);

        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.JOIN_ROOM_REQUEST);
        progressTask.execute();
    }

    protected void initCreateRoomLayoutComponentsEvents() {
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((roomNameCreation.getText().toString()).isEmpty()) {
                    showToast("Error: room name is empty.");
                } else {
                    Room room;
                    if (roomPasswordOffRadio.isChecked()) {
                        room = new Room(roomNameCreation.getText().toString(), getSharedPreferencesUserLogin(), null, null);
                    } else {
                        room = new Room(roomNameCreation.getText().toString(), roomPasswordCreation.getText().toString(), getSharedPreferencesUserLogin(), null, null, true);
                    }

                    if (roomCustomMapYes.isChecked()) {
                        getCustomMapForMultiMode(room);
                    } else {
                        try {
                            sendMultiModeRoomCreation(room.ToJSONObject());
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage(), e);
                        }
                    }
                }
            }
        });

        roomPasswordOffRadio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                roomPasswordCreation.setVisibility(View.INVISIBLE);
                roomPasswordOnRadio.setChecked(false);
            }
        });

        roomPasswordOnRadio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                roomPasswordCreation.setVisibility(View.VISIBLE);
                roomPasswordOffRadio.setChecked(false);
            }
        });

        roomCustomMapNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomCustomMapYes.setChecked(false);
            }
        });

        roomCustomMapYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomCustomMapNo.setChecked(false);
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
                            int errorCode = ((JSONObject) args[0]).getInt(SocketConstants.ERROR_CODE);
                            if (errorCode == 0) {
                                jsonArrayReceived = ((JSONObject) args[0]).getJSONArray(SocketConstants.ROOMS);
                            } else {
                                showToast(getString(R.string.get_room_list_error));
                            }

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
                    String map = selectedRoom.getMap();
                    if (map == null) {
                        map = IntentParameters.NO_MAP;
                    }
                    intent.putExtra(IntentParameters.MAP, map);
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

                    String map = IntentParameters.NO_MAP;
                    if (creationResponse.has(SocketConstants.MAP)) {
                        map = creationResponse.getString(SocketConstants.MAP);
                        if (map == null) {
                            map = IntentParameters.NO_MAP;
                        }
                    }

                    showToast(getString(R.string.room_created));

                    Intent intent = new Intent(MultiModeActivity.this, RoomActivity.class);
                    intent.putExtra(IntentParameters.ROOM_NAME, roomName);
                    intent.putExtra(IntentParameters.IS_HOST, IS_HOST);
                    intent.putExtra(IntentParameters.HOST, host);
                    intent.putExtra(IntentParameters.MAP, map);

                    startActivity(intent);
                } else if (errorCode == 1) {
                    showToast(getString(R.string.room_name_already_used));
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

    protected void sendGetListRoomRequest() {
        mySocket.sendGetListRoomRequest();

        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.GET_LIST_ROOM);
        progressTask.execute();
    }

    protected void sendMultiModeRoomCreation(JSONObject json) {
        mySocket.sendNewRoomRequest(json);

        ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.NEW_ROOM_REQUEST);
        progressTask.execute();
    }

    protected void updateRoomsList() {
        if (foundRooms.isEmpty()) {
            noRoomFoundText.setVisibility(View.VISIBLE);
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
            noRoomFoundText.setVisibility(View.INVISIBLE);
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

        private boolean responseReceived;

        public ProgressTask(SocketConstants.SocketRequestType socketRequestType) {
            this.socketRequestType = socketRequestType;
            this.responseReceived = false;
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
                    responseReceived = mySocket.getGetRoomListRequestFlags().waitResponse();
                    if (responseReceived) {
                        responseReceived = true;
                    } else {
                        responseReceived = false;
                        showToast("No server answer not received");
                    }
                    break;
                case NEW_ROOM_REQUEST:
                    responseReceived = mySocket.getRoomCreationRequestFlags().waitResponse();
                    if (!responseReceived) {
                        showToast("No server answer not received");
                    }
                    break;
                case JOIN_ROOM_REQUEST:
                    responseReceived = mySocket.getJoinRoomRequestFlags().waitResponse();
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

            if (SocketConstants.SocketRequestType.GET_LIST_ROOM.equals(socketRequestType) && responseReceived) {
                foundRooms.clear();
                if (jsonArrayReceived != null) {
                    for (int i = 0; i < jsonArrayReceived.length(); i++) {
                        try {
                            JSONObject jsonObject = (JSONObject) jsonArrayReceived.get(i);

                            String roomName = jsonObject.getString(SocketConstants.ROOM_NAME);
                            String host = jsonObject.getString(SocketConstants.HOST);
                            int slot_empty = jsonObject.getInt(SocketConstants.SLOT_EMPTY);
                            boolean isPassword = jsonObject.getBoolean(SocketConstants.IS_PASSWORD);

                            String map = IntentParameters.NO_MAP;
                            if (jsonObject.has(SocketConstants.MAP)) {
                                map = jsonObject.getString(SocketConstants.MAP);
                            }

                            foundRooms.add(new Room(roomName, host, slot_empty, isPassword, map));

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

    protected void getCustomMapForMultiMode(final Room room) {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            GPSAsyncTask gpsAsyncTask = new GPSAsyncTask(room);
            gpsAsyncTask.execute(locationManager);
        } else {
            buildAlertMessageNoGps();
        }
    }

    public void buildPasswordRequest(final Room selectedRoom) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View w = inflater.inflate(R.layout.password_dialog, null);
        builder.setView(w)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText passwordInput = (EditText) w.findViewById(R.id.passwordInput);
                        connectToRoom(selectedRoom, passwordInput.getText().toString());
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
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
        private Room room;

        public GPSAsyncTask(final Room room) {
            this.room = room;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MultiModeActivity.this);
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

            try {
                room.setLatitude(location.getLatitude());
                room.setLongitude(location.getLongitude());
                sendMultiModeRoomCreation(room.ToJSONObject());
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage(), e);
            }
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


