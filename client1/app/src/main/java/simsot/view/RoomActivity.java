package simsot.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import simsot.socket.MySocket;
import simsot.socket.SocketConstants;

public class RoomActivity extends Activity {

    private static final String LOGIN_IN_PREFERENCES = "login";

    TextView roomNameText;
    Button startMultiGame;
    ListView playerList;

    private boolean isHost;

    private MySocket mySocket;

    String roomName;

    JSONArray jsonArrayReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        initComponents();
        initComponentsEvents();
        initSocket();

        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        String host = intent.getStringExtra("host");
        isHost = intent.getBooleanExtra("isHost", false);

        roomNameText.setText(getResources().getString(R.string.roomNameText, roomName, host));

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RoomActivity.this);
        builder.setTitle("Exit room");
        builder.setMessage("Would you leave the room ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject json = new JSONObject();
                try {
                    json.put("room_name", roomName);
                    json.put("player_name", getSharedPreferencesUserLogin());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mySocket.sendLeaveRoomRequest(json);

                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    protected void initComponents() {
        roomNameText = (TextView) findViewById(R.id.roomNameText);
        startMultiGame = (Button) findViewById(R.id.startMultiGame);
        playerList = (ListView) findViewById(R.id.playerList);

        if (isHost) {
            startMultiGame.setVisibility(View.VISIBLE);
        } else {
            startMultiGame.setVisibility(View.INVISIBLE);
        }

    }


    protected void initComponentsEvents() {
        startMultiGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO to complete
            }
        });
    }

    protected void initSocket() {
        try {
            mySocket = new MySocket(SocketConstants.SERVER_URL);

            mySocket.on(SocketConstants.LIST_PLAYER, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    List<String> playersList = new ArrayList<String>();
                    jsonArrayReceived = (JSONArray) args[0];
                    for (int i = 0; i < jsonArrayReceived.length(); i++) {
                        try {
                            playersList.add((String) jsonArrayReceived.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    updatePlayersList(playersList);
                }
            });

            mySocket.connect();
        } catch (URISyntaxException e) {
            Toast.makeText(RoomActivity.this, "URISyntaxException", Toast.LENGTH_SHORT).show();
        }
    }

    protected void updatePlayersList(final List<String> playersList) {
        RoomActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RoomActivity.this, android.R.layout.simple_list_item_1, playersList);
                playerList.setAdapter(adapter);
            }
        });
    }

    protected void showToast(final String message) {
        RoomActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected String getSharedPreferencesUserLogin() {
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        return settings.getString(LOGIN_IN_PREFERENCES, null);
    }
}
