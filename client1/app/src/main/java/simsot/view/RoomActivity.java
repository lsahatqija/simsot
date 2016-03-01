package simsot.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import simsot.game.R;

public class RoomActivity extends Activity {

    TextView roomNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        init();

        Intent intent = getIntent();
        String roomName = intent.getStringExtra("roomName");
        String host = intent.getStringExtra("host");

        roomNameText.setText(getResources().getString(R.string.roomNameText, roomName, host));

    }

    protected void init() {
        roomNameText = (TextView) findViewById(R.id.roomNameText);
    }
}
