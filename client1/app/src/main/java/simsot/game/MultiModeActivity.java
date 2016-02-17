package simsot.game;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class MultiModeActivity extends Activity {

    RelativeLayout joinCreateRoomChoiceLayout, joinRoomLayout;
    LinearLayout createRoomLayout;
    Button buttonJoinChoice, buttonCreateChoice, createRoomButton;

    RadioButton passwordOffRadio,passwordOnRadio;
    EditText roomNameLabel,passwordLabel,nPlayersLabel,nEnnemiesLabel,distanceLabel;

    String userLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_mode);

        userLogin = getIntent().getStringExtra("userLogin");

        joinCreateRoomChoiceLayout = (RelativeLayout) findViewById(R.id.joinCreateRoomChoiceLayout);
        joinRoomLayout  = (RelativeLayout) findViewById(R.id.joinRoomLayout);
        createRoomLayout  = (LinearLayout) findViewById(R.id.createRoomLayout);

        buttonJoinChoice = (Button) findViewById(R.id.buttonJoinChoice);
        buttonCreateChoice = (Button) findViewById(R.id.buttonCreateChoice);

        createRoomButton = (Button) findViewById(R.id.createRoomButton);
        passwordOffRadio = (RadioButton) findViewById(R.id.passwordOffRadio);
        passwordOnRadio = (RadioButton) findViewById(R.id.passwordOnRadio);
        roomNameLabel = (EditText) findViewById(R.id.roomNameLabel);
        passwordLabel = (EditText) findViewById(R.id.passwordLabel);
        nPlayersLabel = (EditText) findViewById(R.id.nPlayersLabel);
        nEnnemiesLabel = (EditText) findViewById(R.id.nEnnemiesLabel);
        distanceLabel = (EditText) findViewById(R.id.distanceLabel);

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

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check parameters
                //send on socket
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


    protected void displayJoinCreateRoomChoiceLayout(){
        joinCreateRoomChoiceLayout.setVisibility(View.VISIBLE);
        joinRoomLayout.setVisibility(View.INVISIBLE);
        createRoomLayout.setVisibility(View.INVISIBLE);
    }

    protected void displayJoinRoomLayout(){
        joinCreateRoomChoiceLayout.setVisibility(View.INVISIBLE);
        joinRoomLayout.setVisibility(View.VISIBLE);
        createRoomLayout.setVisibility(View.INVISIBLE);
    }

    protected void displayCreateRoomLayout(){
        joinCreateRoomChoiceLayout.setVisibility(View.INVISIBLE);
        joinRoomLayout.setVisibility(View.INVISIBLE);
        createRoomLayout.setVisibility(View.VISIBLE);
    }




}
