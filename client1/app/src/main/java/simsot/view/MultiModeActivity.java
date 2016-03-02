package simsot.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simsot.game.R;
import simsot.model.Room;
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class MultiModeActivity extends Activity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";
    private static final String ACTUAL_LAYOUT = "actualLayout";
    private static final String LIST_ROOM = "list_room";
    private static final String CREATE_RESPONSE = "response_create";

    private static final boolean IS_HOST = true;
    private static final boolean IS_NOT_HOST = false;

    RelativeLayout joinCreateRoomChoiceLayout;
    LinearLayout createRoomLayout, joinRoomLayout;
    Button buttonJoinChoice, buttonCreateChoice, createRoomButton, searchRoomButton;

    RadioButton passwordOffRadio, passwordOnRadio;
    EditText roomNameLabel, passwordLabel, nPlayersLabel, nEnnemiesLabel, distanceLabel;

    String userLogin;

    private MySocket mySocket;

    ListView roomList;

    List<Room> foundRooms;

    private enum MultiModeActivityActualLayout {
        JOINCREATEROOMCHOICE,
        JOINROOM,
        CREATEROOM;
    }

    MultiModeActivityActualLayout actualLayout;

    private JSONArray jsonArrayReceived = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_mode);

        userLogin = getIntent().getStringExtra("userLogin");

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
        joinCreateRoomChoiceLayout = (RelativeLayout) findViewById(R.id.joinCreateRoomChoiceLayout);
        joinRoomLayout = (LinearLayout) findViewById(R.id.joinRoomLayout);
        createRoomLayout = (LinearLayout) findViewById(R.id.createRoomLayout);

        initJoinCreateRoomChoiceLayoutComponents();
        initJoinRoomLayoutComponents();
        initCreateRoomLayoutComponents();
    }

    protected void initJoinCreateRoomChoiceLayoutComponents() {
        buttonJoinChoice = (Button) findViewById(R.id.buttonJoinChoice);
        buttonCreateChoice = (Button) findViewById(R.id.buttonCreateChoice);
    }

    protected void initJoinRoomLayoutComponents() {
        roomList = (ListView) findViewById(R.id.roomList);

        searchRoomButton = (Button) findViewById(R.id.searchRoomButton);

        foundRooms = new ArrayList<Room>();

    }

    protected void initCreateRoomLayoutComponents() {
        createRoomButton = (Button) findViewById(R.id.createRoomButton);
        passwordOffRadio = (RadioButton) findViewById(R.id.passwordOffRadio);
        passwordOnRadio = (RadioButton) findViewById(R.id.passwordOnRadio);
        roomNameLabel = (EditText) findViewById(R.id.roomNameLabel);
        passwordLabel = (EditText) findViewById(R.id.passwordLabel);
        nPlayersLabel = (EditText) findViewById(R.id.nPlayersLabel);
        nEnnemiesLabel = (EditText) findViewById(R.id.nEnnemiesLabel);
        distanceLabel = (EditText) findViewById(R.id.distanceLabel);
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
            }
        });

        buttonCreateChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCreateRoomLayout();
            }
        });
    }

    protected void initJoinRoomLayoutComponentsEvents() {

        searchRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("null", null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mySocket.sendGetListRoomRequest(json);

                ProgressTask progressTask = new ProgressTask(SocketConstants.SocketRequestType.GET_LIST_ROOM);
                progressTask.execute();
            }
        });


        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room selectedRoom = foundRooms.get(position);
                String roomName = selectedRoom.getRoomName();

                showToast("connexion à " + roomName);
                JSONObject json = new JSONObject();
                try {
                    json.put("room_name", roomName);
                    json.put("player_name", userLogin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mySocket.sendJoinRoomRequest(json);

                Intent intent = new Intent(MultiModeActivity.this, RoomActivity.class);
                intent.putExtra("isHost", IS_NOT_HOST);
                intent.putExtra("roomName", roomName);
                intent.putExtra("host", selectedRoom.getHost());
                startActivity(intent);
            }
        });

    }

    protected void initCreateRoomLayoutComponentsEvents() {
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Room room = null;
                    if (passwordOffRadio.isChecked()) {
                        room = new Room(roomNameLabel.getText().toString(), userLogin, null, null,
                                Integer.valueOf(nPlayersLabel.getText().toString()), Integer.valueOf(nEnnemiesLabel.getText().toString()),
                                null, Integer.valueOf(distanceLabel.getText().toString()));
                    } else {
                        room = new Room(roomNameLabel.getText().toString(), passwordLabel.getText().toString(), userLogin, null, null,
                                Integer.valueOf(nPlayersLabel.getText().toString()), Integer.valueOf(nEnnemiesLabel.getText().toString()),
                                null, Integer.valueOf(distanceLabel.getText().toString()));
                    }
                    mySocket.sendNewRoomRequest(room.ToJSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        passwordOffRadio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                passwordLabel.setVisibility(v.INVISIBLE);
                passwordOnRadio.setChecked(false);
            }
        });

        passwordOnRadio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                passwordLabel.setVisibility(v.VISIBLE);
                passwordOffRadio.setChecked(false);
            }
        });
    }

    protected void initSocket() {
        try {
            mySocket = new MySocket(SERVER_URL);

            mySocket.on(LIST_ROOM, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (mySocket.isGetRoomListRequestSendingFlag()) {
                        mySocket.setGetRoomListRequestSendingFlag(false);

                        jsonArrayReceived = (JSONArray) args[0];
                        mySocket.setGetRoomListRequestResponseFlag(true);
                    }
                }
            });

            mySocket.on(CREATE_RESPONSE, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (mySocket.isRoomCreationRequestSendingFlag()) {
                        mySocket.setRoomCreationRequestSendingFlag(false);
                        if (args[0] instanceof String) {
                            String creationResponse = (String) args[0];
                            if("Create successful".equals(creationResponse)){
                                showToast("Creation succeeded");

                                // TODO to complete
                            } else{
                                showToast("Creation failed");
                            }
                        } else {
                            showToast("Creation error");
                        }

                        mySocket.setRoomCreationRequestResponseFlag(true);
                    }
                }
            });

            mySocket.connect();
        } catch (
                URISyntaxException e
                )

        {
            Toast.makeText(MultiModeActivity.this, "URISyntaxException", Toast.LENGTH_SHORT).show();
        }
    }

    protected void updateRoomsList() {
        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        for (Room room : foundRooms) {
            element = new HashMap<String, String>();
            element.put("roomName", room.getRoomName());
            element.put("host", room.getHost());
            liste.add(element);
        }

        ListAdapter adapter = new SimpleAdapter(MultiModeActivity.this,
                liste,
                android.R.layout.simple_list_item_2,
                new String[]{"roomName", "host"},
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
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
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
                    for (int i = 0; i < jsonArrayReceived.length(); i++) {
                        try {
                            JSONObject jsonObject = (JSONObject) jsonArrayReceived.get(i);

                            String roomName = jsonObject.getString("room_name");
                            String host = jsonObject.getString("host");

                            foundRooms.add(new Room(roomName, host));

                            updateRoomsList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    jsonArrayReceived = null;
                    break;
                case NEW_ROOM_REQUEST:
                    mySocket.setRoomCreationRequestResponseFlag(false);
                    break;
                default:
                    break;
            }
        }

    }
}
