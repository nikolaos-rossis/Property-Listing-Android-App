package com.example.dsphase2;


import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

public class SocketManager {

    private static SocketManager instance;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Socket socket = null;

    private SocketManager() {}

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public void setSocket(Socket socket) throws Exception {
        this.socket = socket;
        this.out = new ObjectOutputStream( socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }
}
