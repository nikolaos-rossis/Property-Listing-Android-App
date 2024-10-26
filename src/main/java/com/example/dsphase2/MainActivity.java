package com.example.dsphase2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Socket;

import dsvp.ValuePasser;



public class MainActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;

    private static final String SERVER_IP = "192.168.1.3"; // Replace with your server's IP address
    private static final int SERVER_PORT = 4321;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Manager.class);
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Client.class);
                startActivity(intent);
            }
        });

        new ConnectTask().execute();
    }

    private class ConnectTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Connect to the server
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                SocketManager.getInstance().setSocket(socket);
                Log.e("Server Connection","Success");




            } catch (Exception e) {
                e.printStackTrace();
                publishProgress("Error: " + e.getMessage());
            }
            return null;
        }


    }
}