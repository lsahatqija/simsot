package com.example.romain.pacman;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends ActionBarActivity {

    private static final String SERVER_URL = "https://simsot-server.herokuapp.com";

    private Socket mSocket;

    public static String pseudo = "error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mSocket = IO.socket(SERVER_URL);

            // Messages de connexion
            mSocket.on("response_connect", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    // On lance MenuActivity si on se connecte, sinon on affiche le message serveur
                    if ("Connecté".equals(args[0].toString())) {
                        // Le premier paramètre est le nom de l'activité actuelle
                        // Le second est le nom de l'activité de destination
                        Intent MenuActivity = new Intent(MainActivity.this, MenuActivity.class);

                        // On rajoute un extra (on passe un paramètre)
                        MenuActivity.putExtra("pseudo", pseudo);

                        // Puis on lance l'intent !
                        startActivity(MenuActivity);
                    } else {
                        showToast(args[0].toString());
                    }
                }
            });

            // Messages d'inscription
            mSocket.on("response_subscribe", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    // On affiche le message serveur
                    if (args[0].toString() != null) {
                        showToast(args[0].toString());
                    }
                    else {
                        showToast("error : args[0].toString() is null");
                    }
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
                String userPseudo = ((EditText) findViewById(R.id.userPseudo)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.userPassword)).getText().toString();

                JSONObject data = new JSONObject();

                try {
                    data.put("pseudo", userPseudo);
                    data.put("password", userPassword);
                } catch (JSONException e) {
                    return;
                }
                mSocket.emit("connect_user", data);

                // On note le pseudo pour le passer à la 2e activité
                pseudo = userPseudo;
            }

            ;
        });

        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userPseudo = ((EditText) findViewById(R.id.userPseudo)).getText().toString();
                String userPassword = ((EditText) findViewById(R.id.userPassword)).getText().toString();
                String userPassword2 = ((EditText) findViewById(R.id.userPassword2)).getText().toString();

                if (!userPassword.equals(userPassword2)) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match.", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject data = new JSONObject();

                    try {
                        data.put("pseudo", userPseudo);
                        data.put("password", userPassword);
                    } catch (JSONException e) {
                        return;
                    }

                    mSocket.emit("subscribe", data);
                }
            }

            ;
        });
    }

    protected void showToast(final String message) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
