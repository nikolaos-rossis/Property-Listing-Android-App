package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class CAddFilter extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadd_filter);

        EditText area = findViewById(R.id.area);
        EditText date1 = findViewById(R.id.Dates1);
        EditText date2 = findViewById(R.id.Dates2);
        EditText vis = findViewById(R.id.visitors);
        EditText p1 = findViewById(R.id.prange1);
        EditText p2 = findViewById(R.id.prange2);
        EditText stars = findViewById(R.id.stars);

        Button button = findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String A= area.getText().toString();
                String D1= date1.getText().toString();
                String D2= date2.getText().toString();
                String V= vis.getText().toString();
                String P1= p1.getText().toString();
                String P2= p2.getText().toString();
                String S= stars.getText().toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


                LocalDate F = LocalDate.parse(D1, formatter);
                LocalDate T = LocalDate.parse(D2, formatter);


                DataManager.getInstance().setFilter(A,F,T,V,P1,P2,S);

                System.out.println(DataManager.getInstance().getFilter()[0]);
                System.out.println(DataManager.getInstance().getFilter()[1]);
                System.out.println(DataManager.getInstance().getFilter()[2]);
                System.out.println(DataManager.getInstance().getFilter()[3]);
                System.out.println(DataManager.getInstance().getFilter()[4]);
                System.out.println(DataManager.getInstance().getFilter()[5]);


                Toast.makeText(getApplicationContext(), "Saved ", Toast.LENGTH_SHORT).show();
            }
        });



    }
}