package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverText, portServerText, portServer, clientText, prefixClient, textClient, invisible;
    Button getClient, startServer;

    ServerThread serverThread;
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverText = findViewById(R.id.serverText);
        portServerText = findViewById(R.id.portServerText);
        portServer = findViewById(R.id.portServer);
        clientText = findViewById(R.id.clientText);
        prefixClient = findViewById(R.id.prefixClient);
        textClient = findViewById(R.id.textClient);

        getClient = findViewById(R.id.getClient);
        startServer = findViewById(R.id.startServer);
        invisible = findViewById(R.id.invizibil);

        getClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = textClient.getText().toString();

                String clPort = portServer.getText().toString();
                String clAddress = "127.0.0.1";

                clientThread = new ClientThread(clAddress, Integer.parseInt(clPort), query, invisible);
                clientThread.start();

            }
        });

        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String svPort = portServer.getText().toString();
                if (svPort != null && !svPort.isEmpty()) {
                    // Verificare ca e corect
                    serverThread = new ServerThread(Integer.parseInt(svPort));
                    //verificare iara
                    serverThread.start();
                }
                else
                {
                    Toast.makeText(PracticalTest02MainActivity.this, "port cannot be null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        invisible.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(PracticalTest02MainActivity.this, invisible.getText().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    @Override
    protected void onDestroy() {

        if (serverThread != null)
            serverThread.stopThread();
        super.onDestroy();


    }
}
