package simsot.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
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
    private EditText roomNameCreation, roomPasswordCreation, roomNbPlayersCreation, roomDistanceMaxCreation;

    private MySocket mySocket;

    private ListView roomList;

    private List<Room> foundRooms;

    private enum MultiModeActivityActualLayout {
        JOINCREATEROOMCHOICE,
        JOINROOM,
        CREATEROOM
    }

    private MultiModeActivityActualLayout actualLayout;

    private JSONArray jsonArrayReceived = null;

    private Room roomWaitingConfirmation = null;
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

                    mySocket.sendGetListRoomRequest();

                    ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.GET_LIST_ROOM);
                    progressTask.execute();
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

        foundRooms = new ArrayList<Room>();

    }

    protected void initCreateRoomLayoutComponents() {
        roomPasswordOffRadio = (RadioButton) findViewById(R.id.roomPasswordOffRadio);
        roomPasswordOnRadio = (RadioButton) findViewById(R.id.roomPasswordOnRadio);
        roomNameCreation = (EditText) findViewById(R.id.roomNameCreation);
        roomPasswordCreation = (EditText) findViewById(R.id.roomPasswordCreation);
        roomNbPlayersCreation = (EditText) findViewById(R.id.roomNbPlayersCreation);
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
                mySocket.sendGetListRoomRequest();

                ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.GET_LIST_ROOM);
                progressTask.execute();
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
                mySocket.sendGetListRoomRequest();

                ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.GET_LIST_ROOM);
                progressTask.execute();
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
                                Integer.valueOf(roomNbPlayersCreation.getText().toString()),
                                null, Integer.valueOf(roomDistanceMaxCreation.getText().toString()));
                    } else {
                        room = new Room(roomNameCreation.getText().toString(), roomPasswordCreation.getText().toString(), getSharedPreferencesUserLogin(), null,
                                Integer.valueOf(roomNbPlayersCreation.getText().toString()),
                                null, Integer.valueOf(roomDistanceMaxCreation.getText().toString()));
                    }
                    mySocket.sendNewRoomRequest(room.ToJSONObject());
                    roomWaitingConfirmation = room;

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
                if (mySocket.isGetRoomListRequestSendingFlag()) {
                    mySocket.setGetRoomListRequestSendingFlag(false);

                    jsonArrayReceived = (JSONArray) args[0];
                    mySocket.setGetRoomListRequestResponseFlag(true);
                }
            }
        });

        mySocket.on(SocketConstants.JOIN_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.isJoinRoomRequestSendingFlag()) {
                    mySocket.setJoinRoomRequestSendingFlag(false);

                    if (args[0] instanceof JSONObject) {
                        JSONObject joinResponse = (JSONObject) args[0];
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
                    mySocket.setJoinRoomRequestResponseFlag(true);
                }
            }
        });

        mySocket.on(SocketConstants.CREATE_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (mySocket.isRoomCreationRequestSendingFlag()) {
                    mySocket.setRoomCreationRequestSendingFlag(false);
                    if (args[0] instanceof JSONObject) {
                        JSONObject creationResponse = (JSONObject) args[0];

                        try {
                            int errorCode = creationResponse.getInt(SocketConstants.ERROR_CODE);
                            if (errorCode == 0) {
                                showToast(getString(R.string.room_created));

                                // TODO to complete
                                Intent intent = new Intent(MultiModeActivity.this, RoomActivity.class);
                                intent.putExtra(IntentParameters.IS_HOST, IS_HOST);
                                intent.putExtra(IntentParameters.ROOM_NAME, roomWaitingConfirmation.getRoomName());
                                intent.putExtra(IntentParameters.HOST, roomWaitingConfirmation.getHost());

                                roomWaitingConfirmation = null;
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

                    mySocket.setRoomCreationRequestResponseFlag(true);
                }
            }
        });
    }

    protected void updateRoomsList() {
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

        public ProgressTask(SocketConstants.SocketRequestType socketRequestType) {
            this.socketRequestType = socketRequestType;
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
                    while (!mySocket.isGetRoomListRequestResponseFlag()) {
                        // TODO add timeout counter
                    }
                    break;
                case NEW_ROOM_REQUEST:
                    while (!mySocket.isRoomCreationRequestResponseFlag()) {
                        // TODO add timeout counter
                    }
                    break;
                case JOIN_ROOM_REQUEST:
                    while (!mySocket.isJoinRoomRequestResponseFlag()) {
                        // TODO add timeout counter
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

            switch (socketRequestType) {
                case GET_LIST_ROOM:
                    mySocket.setGetRoomListRequestResponseFlag(false);
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
                    break;
                case NEW_ROOM_REQUEST:
                    mySocket.setRoomCreationRequestResponseFlag(false);
                    break;
                case JOIN_ROOM_REQUEST:
                    mySocket.setJoinRoomRequestResponseFlag(false);
                    break;
                default:
                    break;
            }
        }

    }
}
