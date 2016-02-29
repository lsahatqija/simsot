package simsot.view;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import simsot.game.R;
import simsot.model.Room;
import simsot.socket.MySocket;

public class MultiModeActivity extends Activity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";
    private static final String ACTUAL_LAYOUT = "actualLayout";

    RelativeLayout joinCreateRoomChoiceLayout, joinRoomLayout;
    LinearLayout createRoomLayout;
    Button buttonJoinChoice, buttonCreateChoice, createRoomButton;

    RadioButton passwordOffRadio, passwordOnRadio;
    EditText roomNameLabel, passwordLabel, nPlayersLabel, nEnnemiesLabel, distanceLabel;

    String userLogin;

    private MySocket mySocket;

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
        joinRoomLayout = (RelativeLayout) findViewById(R.id.joinRoomLayout);
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
        // TODO to implement
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
        // TODO to implement
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


}
