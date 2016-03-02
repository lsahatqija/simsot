package simsot.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import simsot.game.R;

public class RoomActivity extends Activity {

    TextView roomNameText;
    Button startMultiGame;

    private boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        initComponents();
        initComponentsEvents();

        Intent intent = getIntent();
        String roomName = intent.getStringExtra("roomName");
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
                //TODO to complete

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
}
