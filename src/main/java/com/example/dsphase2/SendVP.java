package com.example.dsphase2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import dsvp.ValuePasser;

public class SendVP extends AsyncTask<ValuePasser, Void, Void> {
    @Override
    protected Void doInBackground(ValuePasser... valuePassers) {
        try {
            SocketManager.getInstance().getOut().writeObject(valuePassers[0]);
            SocketManager.getInstance().getOut().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }}
