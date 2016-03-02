package simsot.view;

import android.app.Activity;
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

    protected void initComponents() {
        roomNameText = (TextView) findViewById(R.id.roomNameText);
        startMultiGame = (Button) findViewById(R.id.startMultiGame);

        if(isHost){
            startMultiGame.setVisibility(View.VISIBLE);
        } else{
            startMultiGame.setVisibility(View.INVISIBLE);
        }

    }

    protected void initComponentsEvents(){
        startMultiGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO to complete
            }
        });
    }
}
