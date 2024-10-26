package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dsvp.ValuePasser;

public class CSeeRooms extends AppCompatActivity {

    private ArrayList<String[]> recRes= new ArrayList<>();

    private ArrayList<File> imgs=new ArrayList<>();

    private Button b1;
    private Button b2;

    private ListView l;
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csee_rooms);
        l= findViewById(R.id.bookings_list);
        l.setVisibility(View.INVISIBLE);
        t=findViewById(R.id.text4);
        t.setVisibility(View.INVISIBLE);

        b1=findViewById(R.id.button1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recRes.clear();
                ValuePasser send = new ValuePasser(DataManager.getInstance().getFilter(),3,0);
                new SendVP().execute(send);
                new ReceiveRooms().execute();

                while(recRes.isEmpty()){}

                if(recRes.get(0)[0]=="k"){
                    recRes.clear();
                    showmessage();
                }else{
                showlist();}

            }
        });

        b2=findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recRes.clear();
                new SendVP().execute(new ValuePasser(null, 3, 0));
                new ReceiveRooms().execute();
                while(recRes.isEmpty()){}
                if(recRes.get(0)[0]=="k"){
                    recRes.clear();
                    showmessage();
                }else{
                    showlist();}

            }
        });


        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);
                for( String[] a : recRes){
                    if (a[0]==selectedItem){
                        DataManager.getInstance().setcurrent(a);
                        DataManager.getInstance().setcurimg(imgs.get(recRes.indexOf(a)));
                        Intent intent = new Intent(CSeeRooms.this, SeeRoomDetails.class);
                        startActivity(intent);

                        break;

                    }
                }




            }
        });



    }

    private class ReceiveRooms extends AsyncTask<Void, String, ValuePasser> {

        @Override
        protected ValuePasser doInBackground(Void... voids) {
            ValuePasser receiver;
            try {

                receiver = (ValuePasser)SocketManager.getInstance().getIn().readObject();
                Object[] receiver_copy = (Object[]) receiver.getObjectValues();

                ArrayList<byte[]> photos = (ArrayList<byte[]>) receiver_copy[1];

                for (int l = 0; l <  photos.size(); l++) {

                    String imageName= ((ArrayList<String[]>) receiver_copy[0]).get(l)[6].substring(1);
                    System.out.println(imageName);
                    File imageFile = FileUtils.saveImage(getApplicationContext(), photos.get(l) , imageName);
                    imgs.add(imageFile);

                }



                recRes.addAll((ArrayList<String[]>) receiver_copy[0]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (recRes.isEmpty()){
                recRes.add(new String[]{"k"});
            }
            return receiver;
        }
    }

    private void showlist(){

        while(recRes.isEmpty()){}
        t.setVisibility(View.INVISIBLE);
        l.setVisibility(View.VISIBLE);


        ArrayList<String> items = new ArrayList<>();


        for (String[] a : recRes) {
            items.add(a[0]);
        }

        CustomAdapter adapter = new CustomAdapter(this, items);

        ListView listView = findViewById(R.id.bookings_list);
        listView.setAdapter(adapter);

    }

    private void showmessage(){
        l.setVisibility(View.INVISIBLE);
        t.setVisibility(View.VISIBLE);
    }
}