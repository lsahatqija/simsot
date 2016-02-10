package com.example.romain.pacman;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends ActionBarActivity {

    private static final String SERVER_URL = "http://10.0.2.2:3000";
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

        testButton = (Button) findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("test", "test" + nbTest);
                nbTest++;
            }
        });

        responseTexteView = (TextView) findViewById(R.id.textView2);
    }

    private void addMessage(String message, String data) {
        responseTexteView.setText(responseTexteView.getText() + "\n" + data + " - " + message);
    }

}
