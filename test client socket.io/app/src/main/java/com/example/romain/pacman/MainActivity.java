package com.example.romain.pacman;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends ActionBarActivity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";
    private int nbTest;

    Button testButton;
    TextView responseTexteView;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nbTest = 1;
        try {
            mSocket = IO.socket(SERVER_URL);

            mSocket.on("testresponse", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            String test;
                            String dataString;
                            try {
                                test = data.getString("test");
                                dataString = data.getString("data");

                            } catch (JSONException e) {
                                return;
                            }

                            // add the message to view
                            addMessage(test, dataString);
                        }
                    });
                }
            });
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Button connectButton = (Button) findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userPseudo = (EditText) findViewById(R.id.userPseudo);
                EditText userPassword = (EditText) findViewById(R.id.userPassword);

                Toast.makeText(getApplicationContext(), "Sending connect_user to server...", Toast.LENGTH_SHORT).show();
                JSONObject data = new JSONObject();

                try {
                    data.put("pseudo", userPseudo.getText().toString());
                    data.put("password", userPassword.getText().toString());
                } catch (JSONException e) {
                    return;
                }

                mSocket.emit("connect_user", data);
            };
        });

        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userPseudo = (EditText) findViewById(R.id.userPseudo);
                EditText userPassword = (EditText) findViewById(R.id.userPassword);
                EditText userPassword2 = (EditText) findViewById(R.id.userPassword2);

                if (!userPassword.getText().toString().equals(userPassword2.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(), "Passwords don't match.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Sending subscribe to server...", Toast.LENGTH_SHORT).show();
                    JSONObject data = new JSONObject();

                    try {
                        data.put("pseudo", userPseudo.getText().toString());
                        data.put("password", userPassword.getText().toString());
                    } catch (JSONException e) {
                        return;
                    }

                    mSocket.emit("subscribe", data);
                }
            };
        });

        //responseTexteView = (TextView) findViewById(R.id.textView2);
    }

    private void addMessage(String message, String data) {
        responseTexteView.setText(responseTexteView.getText() + "\n" + data + " - " + message);
    }

}
