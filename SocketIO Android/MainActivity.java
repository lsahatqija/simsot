package fr.insa_lyon.testsocketio;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter.Listener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class MainActivity extends ActionBarActivity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com/";

    Button testButton;
    Button connectionButton;
    TextView responseTexteView;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {}
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String test ="niStringniJSON";
                    if (args[0] instanceof String) {
                        test = (String) args[0];
                    } else if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            test = data.getString("test");

                        } catch (JSONException e) {
                            return;
                        }
                    }

                    // add the message to view
                    addMessage(test);
                }
            });
        }
    };

    private Emitter.Listener onProut = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String test ="niStringniJSON";
                    if (args[0] instanceof String) {
                        test = (String) args[0];
                    } else if (args[0] instanceof JSONObject) {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            test = data.getString("prout");

                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this,"JSONException", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSocket.on("player_data", onNewMessage);
        mSocket.on("prout", onProut);


        connectionButton = (Button) findViewById(R.id.button2);
        testButton = (Button) findViewById(R.id.button);
        testButton.setEnabled(false);

        connectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.connect();
                testButton.setEnabled(true);
                connectionButton.setEnabled(false);
            }
        });


        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("nom", "test");
                    json.put("letter", "a");

                    mSocket.emit("client_data", json);
                    Toast.makeText(MainActivity.this,"Envoyé", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,"JSONException", Toast.LENGTH_SHORT).show();
                }

            }
        });

        responseTexteView = (TextView) findViewById(R.id.textView2);
    }

    private void addMessage(String message) {
        responseTexteView.setText(responseTexteView.getText()+"\n" + message);
    }

}
