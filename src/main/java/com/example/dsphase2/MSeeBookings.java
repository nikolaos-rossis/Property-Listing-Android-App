package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dsvp.ValuePasser;

public class MSeeBookings extends AppCompatActivity {

    ArrayList<String[]> katalymata = new ArrayList<>() ;
    private TextView t;
    private ListView listView;
    private CustomSubAdapter adapter;
    private List<ListItem> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msee_bookings);

        t=findViewById(R.id.text2);
        listView = findViewById(R.id.bookings_list);
        new seeBookings().execute();
        new ReceiveBookings().execute();

        while (katalymata.isEmpty()){}

        if(katalymata.get(0)[0]=="k") {

            t.setVisibility(View.VISIBLE);
            listView.setVisibility(View.INVISIBLE);
        }else{
            t.setVisibility(View.INVISIBLE);

            listItems = new ArrayList<>();


            for (int i =0;i<katalymata.size();i+=2){
                String d = katalymata.get(i+1)[0] + "-" + katalymata.get(i+1)[1];
                listItems.add(new ListItem(katalymata.get(i)[0],d));

            }

            adapter = new CustomSubAdapter(this, listItems);
            listView.setAdapter(adapter);
            }


    }




    private class seeBookings extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            ValuePasser req = new ValuePasser(null, 6,0);
            try {
                SocketManager.getInstance().getOut().writeObject(req);
                SocketManager.getInstance().getOut().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    private class ReceiveBookings extends AsyncTask<Void, String, ValuePasser> {

        @Override
        protected ValuePasser doInBackground(Void... voids) {
            ValuePasser receiver;
            try {
                receiver = (ValuePasser)SocketManager.getInstance().getIn().readObject();

                if(((ArrayList<String[]>) receiver.getObjectValues()).isEmpty()){
                    katalymata.add(new String[]{"k"});
                }else{
                katalymata.addAll((ArrayList<String[]>) receiver.getObjectValues());}
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return receiver;
        }
    }

}