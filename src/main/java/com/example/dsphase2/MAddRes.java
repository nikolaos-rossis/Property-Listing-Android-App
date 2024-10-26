package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import dsvp.ValuePasser;

public class MAddRes extends AppCompatActivity {


    ArrayList<String[]> Katalymata = new ArrayList<String[]>();


    String[] jsonPaths = {
            "basicroom.json",
            "Katalyma_1.json"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madd_res);

        for (String i : jsonPaths) {
            jsonIterator(i);
        }

        DataManager.getInstance().SetKatalymata(Katalymata);
        ArrayList<String> items = new ArrayList<>();
        for (String[] a : DataManager.getInstance().getKatalymata()){
            items.add(a[0]);
        }
        CustomAdapter adapter = new CustomAdapter(this, items);

        ListView listView = findViewById(R.id.bookings_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);
                for( String[] a : Katalymata){
                    if (a[0]==selectedItem){

                        String drawableName = a[6]; // e.g., "example_image"
                        drawableName=drawableName.replace("\\","");
                        drawableName=drawableName.substring(0,drawableName.indexOf("."));
                        System.out.println(drawableName);

                        int resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());


                        byte[] fileData = ResourceUtils.readDrawableResource(getApplicationContext(), resourceId);

                        Object[] list = {a, fileData};

                        ValuePasser roomDetails = new ValuePasser(list, 1,0);
                        new SendVP().execute(roomDetails);
                        DataManager.getInstance().addtoSentKatalymata(a);
                        break;
                    }
                }

                Toast.makeText(getApplicationContext(), "Sent " , Toast.LENGTH_SHORT).show();


            }
        });


    }

    private String readJsonFromAssets(Context context, String fileName) {
        String jsonString = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }





    private void jsonIterator(String fileName) {
        String[] values = new String[9]; // Array to store the extracted values

        try {
            // Read the JSON file from the assets folder
            String jsonString = readJsonFromAssets(this,fileName);
            if (jsonString != null) {
                // Parse the JSON string
                JSONObject jsonObject = new JSONObject(jsonString);
                values[0] = jsonObject.getString("roomName");
                values[1] = String.valueOf(jsonObject.getInt("noOfPersons"));
                values[2] = jsonObject.getString("area");
                values[3] = String.valueOf(jsonObject.getInt("stars"));
                values[4] = String.valueOf(jsonObject.getInt("noOfReviews"));
                values[5] = String.valueOf(jsonObject.getInt("price"));
                values[6] = jsonObject.getString("roomImage");
                values[7] = "0"; //Client ID
                values[8] = "0"; //Stars to pass


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Katalymata.add(values);
        //System.out.println("Residence with Room Name: " + values[0] + " has been added successfully!!!");


    }
}