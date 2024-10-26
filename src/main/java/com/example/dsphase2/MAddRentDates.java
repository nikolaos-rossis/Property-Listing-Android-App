package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import dsdate.Dates;
import dsvp.ValuePasser;

public class MAddRentDates extends AppCompatActivity {

    private EditText date1;
    private EditText date2;

    private Button buttonSave;

    private String from, to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madd_rent_dates);


        ArrayList<String> items = new ArrayList<>();
        for (String[] a : DataManager.getInstance().getSentKatalymata()) {
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

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                LocalDate F = LocalDate.parse(from, formatter);
                LocalDate T = LocalDate.parse(to, formatter);

                for (String[] a : DataManager.getInstance().getSentKatalymata()) {
                    if (a[0] == selectedItem) {
                        Dates tosend = new Dates(F, T, selectedItem, Integer.parseInt(a[7]), a[2]);
                        ValuePasser d = new ValuePasser(tosend, 2, 0);//add residence
                        new SendVP().execute(d);
                    }
                }


                Toast.makeText(getApplicationContext(), "Sent ", Toast.LENGTH_SHORT).show();


            }


        });

        date1 = findViewById(R.id.Dates1);
        date2 = findViewById(R.id.Dates2);

        buttonSave = findViewById(R.id.button1);


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from = date1.getText().toString();
                to = date2.getText().toString();
                Toast.makeText(getApplicationContext(), "Saved ", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
