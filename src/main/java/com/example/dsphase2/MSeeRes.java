package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import dsvp.ValuePasser;

public class MSeeRes extends AppCompatActivity {

    HashMap<String, Integer> areas= new HashMap<>();
    private EditText d1,d2;
    private TextView msg;
    private ListView list;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msee_res);

        msg=findViewById(R.id.text3);
        d1=findViewById(R.id.d1);
        d2=findViewById(R.id.d2);
        list=findViewById(R.id.bookings_list);
        button=findViewById(R.id.button1);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date1= d1.getText().toString();
                String date2= d2.getText().toString();

                areas.clear();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                LocalDate[] datefilter = new LocalDate[2];
                datefilter[0]=LocalDate.parse(date1, formatter);
                datefilter[1] = LocalDate.parse(date2, formatter);

                new SendVP().execute(new ValuePasser(datefilter,7,0));
                new ReceiveRes().execute();


                while(areas.isEmpty()){

                }

                if(areas.get("k")!= null){
                    areas.clear();
                    msg.setVisibility(View.VISIBLE);
                    list.setVisibility(View.INVISIBLE);
                }else{

                    msg.setVisibility(View.INVISIBLE);
                    list.setVisibility(View.VISIBLE);

                    ArrayList<String> items = new ArrayList<>();
                    for( String area : areas.keySet()){
                        String xoxo = area + ": "+ areas.get(area);
                        items.add(xoxo);
                    }

                    CustomAdapter adapter = new CustomAdapter(MSeeRes.this , items);

                    ListView listView = findViewById(R.id.bookings_list);
                    listView.setAdapter(adapter);


                }


            }
        });
    }

    private class ReceiveRes extends AsyncTask<Void, String, ValuePasser> {

        @Override
        protected ValuePasser doInBackground(Void... voids) {
            ValuePasser receiver;
            try {

                receiver = (ValuePasser)SocketManager.getInstance().getIn().readObject();
                areas= (HashMap<String, Integer>) receiver.getObjectValues();

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (areas.isEmpty()){
                areas.put("k",1);
            }
            return receiver;
        }
    }
}