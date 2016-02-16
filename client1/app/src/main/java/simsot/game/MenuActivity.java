package simsot.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends Activity {

    Button buttonSolo;
    Button buttonMulti;
    Button buttonSettings;
    Button buttonHow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buttonSolo = (Button) findViewById(R.id.buttonSolo);
        buttonMulti = (Button) findViewById(R.id.buttonMulti);
        buttonSettings = (Button) findViewById(R.id.buttonSettings);
        buttonHow = (Button) findViewById(R.id.buttonHow);

        buttonSolo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SampleGame.class);

                startActivity(intent);
            }
        });

        buttonMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "yolo !", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "Not implemented yet !", Toast.LENGTH_SHORT).show();
            }
        });

        buttonHow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "Not implemented yet !", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
