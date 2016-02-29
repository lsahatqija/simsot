package simsot.view;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import simsot.game.R;
import simsot.model.Room;
import simsot.socket.MySocket;

public class MultiModeActivity extends Activity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";
    private static final String ACTUAL_LAYOUT = "actualLayout";
    private static final String LIST_ROOM = "list_room";

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
    };

    MultiModeActivityActualLayout actualLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_mode);

        userLogin = getIntent().getStringExtra("userLogin");

        initComponents();
        initComponentsEvents();
        initSocket();

        if(savedInstanceState != null){
            actualLayout = (MultiModeActivityActualLayout) savedInstanceState.getSerializable(ACTUAL_LAYOUT);
        }

        if (actualLayout == null) {
            displayJoinCreateRoomChoiceLayout();
        } else{
            switch(actualLayout){
                case JOINCREATEROOMCHOICE:
                    displayJoinCreateRoomChoiceLayout();
                    break;
                case JOINROOM :
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
        foundRooms.add(new Room("room1"));
        foundRooms.add(new Room("room2"));
        foundRooms.add(new Room("room3"));

        List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> element;
        for(Room room : foundRooms) {
            element = new HashMap<String, String>();
            element.put("roomName", room.getRoomName());
            element.put("nbPlayersMax", String.valueOf(room.getNbPlayersMax()));
            liste.add(element);
        }

        ListAdapter adapter = new SimpleAdapter(MultiModeActivity.this,
                liste,
                android.R.layout.simple_list_item_2,
                new String[] {"roomName", "nbPlayersMax"},
                new int[] {android.R.id.text1, android.R.id.text2 });
        roomList.setAdapter(adapter);
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
                mySocket.sendNewRoomRequest(json);
            }
        });


        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room selectedRoom = foundRooms.get(position);
                String roomName = selectedRoom.getRoomName();
                showToast(roomName);
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
                        room = new Room(roomNameLabel.getText().toString(), null, null, null,
                                Integer.valueOf(nPlayersLabel.getText().toString()), Integer.valueOf(nEnnemiesLabel.getText().toString()),
                                null, Integer.valueOf(distanceLabel.getText().toString()));
                    } else {
                        room = new Room(roomNameLabel.getText().toString(), passwordLabel.getText().toString(), null, null, null,
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
                    if (args[0] instanceof String) {
                        String connectionResponse = (String) args[0];

                        showToast(getString(R.string.connection_succeeded));

                    } else {
                        showToast("fail");
                    }
                }
            });

            mySocket.connect();
        } catch (URISyntaxException e) {
            Toast.makeText(MultiModeActivity.this, "URISyntaxException", Toast.LENGTH_SHORT).show();
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

}
