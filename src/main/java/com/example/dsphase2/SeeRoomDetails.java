package com.example.dsphase2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import dsdate.Dates;
import dsvp.ValuePasser;

public class SeeRoomDetails extends AppCompatActivity {

    private TextView name,area,rev,vis;
    private ImageView img;
    private EditText d1,d2;
    private RatingBar ratingBar;
    private Button button;

    private int bookstatus=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_room_details);

        name=findViewById(R.id.name);
        area=findViewById(R.id.area);
        rev=findViewById(R.id.rev);
        vis=findViewById(R.id.vis);
        img=findViewById(R.id.imageView);
        ratingBar=findViewById(R.id.ratingBar);
        d1=findViewById(R.id.d1);
        d2=findViewById(R.id.d2);
        button=findViewById(R.id.button1);

        System.out.println(DataManager.getInstance().getcur()[6]);
        String imgname= DataManager.getInstance().getcur()[6].substring(1,DataManager.getInstance().getcur()[6].indexOf("."));
        int resId=getResources().getIdentifier(imgname,"drawable", getPackageName());

        ratingBar.setRating(Float.parseFloat(DataManager.getInstance().getcur()[3]));
        name.setText(DataManager.getInstance().getcur()[0]);
        area.setText("Area: " +DataManager.getInstance().getcur()[2]);
        rev.setText("Reviews: "+DataManager.getInstance().getcur()[4]);
        vis.setText("Visitors: "+ DataManager.getInstance().getcur()[1]);

        Bitmap bitmap = BitmapFactory.decodeFile(DataManager.getInstance().getcurimg().getAbsolutePath());
        img.setImageBitmap(bitmap);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    DataManager.getInstance().getcur()[8]=String.valueOf(rating);
                    new SendVP().execute(new ValuePasser(DataManager.getInstance().getcur(),4,Integer.parseInt(DataManager.getInstance().getcur()[7])));
                    Toast.makeText(SeeRoomDetails.this, "Rated: " + rating, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String D1= d1.getText().toString();
                String D2=d2.getText().toString();


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                LocalDate F = LocalDate.parse(D1, formatter);
                LocalDate T = LocalDate.parse(D2, formatter);

                Dates katal= new Dates(F,T,DataManager.getInstance().getcur()[0],Integer.parseInt(DataManager.getInstance().getcur()[7]),
                        DataManager.getInstance().getcur()[2]);

                new SendVP().execute(new ValuePasser(katal,5,0));
                new ReceiveBookstatus().execute();

                while(bookstatus == 0){}
                if(bookstatus==1){
                    Toast.makeText(SeeRoomDetails.this, "Booking success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SeeRoomDetails.this, "Booking failed, dates unavailable", Toast.LENGTH_SHORT).show();
                }
                bookstatus=0;


            }
        });


    }

    private class ReceiveBookstatus extends AsyncTask<Void, String, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {

                bookstatus =SocketManager.getInstance().getIn().readInt();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return bookstatus;
        }
    }
}