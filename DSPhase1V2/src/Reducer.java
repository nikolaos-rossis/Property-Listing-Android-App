
import dsvp.ValuePasser;
import java.io.*;
import java.net.*;
import java.util.*;


public class Reducer extends Thread{
    int code;

    ArrayList<Object> queue2;

    ArrayList<Integer> workers;

    ArrayList<Integer> workers2;

    ServerSocket serverSocket;

    /* Define the socket that is used to handle the connection */

    Socket socket;

    ObjectInputStream in = null;
    ObjectOutputStream out = null;

    public Reducer(int code, ArrayList<Object> queue2, ArrayList<Integer> workers, ArrayList<Integer> workers2) {
        this.code = code;
        if (code == - 1) {
            this.queue2 = new ArrayList<Object>();
            this.workers = new ArrayList<Integer>();
            this.workers2 = new ArrayList<Integer>();
            this.queue2.add(new ArrayList<>());
            this.queue2.add(new ArrayList<>());
        } else {
            this.queue2 = queue2;
            this.workers = workers;
            this.workers2 = workers2;
        }
    }

    public void run() {
        if (code == -1) {
            new Reducer(0, queue2, workers, workers2).start(); //Worker - Reducer Connection
            new Reducer(1, queue2, workers, workers2).start(); //Reducer - Server Connection
        } else if (code == 0) {
            this.openServer(); //Worker - Reducer(WorkerReducerHandler) Connection;
        } else {
            try {

                String host = "localhost";
                /* Create socket for contacting the server on port 4324*/
                this.socket = new Socket(host, 4324);

                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(this.socket.getOutputStream());
                in = new ObjectInputStream(this.socket.getInputStream());

                System.out.println("Reducer> Connection Success");
                while(true) {
                    System.out.println("Reducer> Waiting for notify...");
                    synchronized (queue2) {
                        queue2.wait();
                    }

                    // System.out.println("Reducer> " + queue2.get(0));
                    // ArrayList<String[]> heheheYUP = (ArrayList<String[]>) queue2.get(0);
                    // System.out.println("Reducer>");
                    // for (String[] i : heheheYUP) {
                    //     System.out.println(i[0]);
                    // }
                    // //queue2.set(0, null);

                    // ValuePasser sender = new ValuePasser(heheheYUP, 3, 0);
                    // out.writeObject(sender);
                    // out.flush();

                    // queue2.set(0, null);

                    System.out.println("Reducer> " + queue2.get(0));
                    ArrayList<String[]> rooms = (ArrayList<String[]>) queue2.get(0);
                    ArrayList<byte[]> photos = (ArrayList<byte[]>) queue2.get(1);
                    System.out.println("Reducer>");
                    for (String[] i : rooms) {
                        System.out.println(i[0]);
                    }
                    //queue2.set(0, null);

                    Object[] list = {rooms, photos};

                    ValuePasser sender = new ValuePasser(list, 3, 0);
                    out.writeObject(sender);
                    out.flush();

                    queue2.set(0, null);
                    queue2.set(1, null);



                }



            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static void main(String args[]) {

        new Reducer(-1, null, null, null).start();

    }

    /* Define the socket that receives requests */



    void openServer() {
        try {



            /* Create Server Socket */

            this.serverSocket = new ServerSocket(4323);


            int counter = 0;
            while (true) {
                /* Accept the connection */
                System.out.println("Waiting for a Connection");
                this.socket = serverSocket.accept();

                WorkerReducerHandler th = new WorkerReducerHandler(this.socket, queue2, workers, workers2);
                th.start();

                System.out.println("Got a Connection");


            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


}
