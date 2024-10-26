import dsvp.ValuePasser;

import com.sun.jdi.Value;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;

public class WorkerReducerHandler extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    ArrayList<Object> queue2;//queue will be used to connect with Reducer !!!DIFFERENT QUEUE FROM SERVER/WORKER/CLIENT!!!
    ArrayList<Integer> workers;

    ArrayList<Integer> workers2;

    public WorkerReducerHandler(Socket connection, ArrayList<Object> queue2, ArrayList<Integer> workers, ArrayList<Integer> workers2) {
        this.queue2 = queue2;
        this.workers = workers;
        this.workers2 = workers2;
        try {

            this.in = new ObjectInputStream(connection.getInputStream());
            this.out = new ObjectOutputStream(connection.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        System.out.println("WorkerReducerHandler> New Worker Connected...");
        workers.add(0);
        workers2.add(0);
        System.out.println("WorkerReducerHandler> Size of Workers: " + workers.size());
        //ArrayList<Object> temp = new ArrayList<>();
        while(true) {
            int activeWorkers = workers.size();
            try {
                System.out.println("WorkerReducerHandler" + activeWorkers + "> Waiting for Object...");
                ValuePasser receiver = (ValuePasser) in.readObject(); //Gets Object
                Object[] receiver_copy = (Object[]) receiver.getObjectValues(); // Giannhs
                //ArrayList<String[]> test = (ArrayList<String[]>) receiver.getObjectValues();
                System.out.println("WorkerReducerHandler" + activeWorkers + "> I got " + receiver.getObjectValues());
                /*for (String[] k : test) {
                    System.out.println("WorkerReducerHandler" + activeWorkers + "> " + k[0] + ", " + k[7]);
                }*/
                synchronized (queue2) {
                    if (receiver.getAction() == 3) {
                        // //temp.add(receiver.getObjectValues());
                        // ArrayList<String[]> rooms = (ArrayList<String[]>) receiver.getObjectValues();
                        // if (queue2.get(0) == null) {
                        //     System.out.println("WorkerReducerHandler" + activeWorkers + "> I set: " + rooms);
                        //     queue2.set(0, rooms);
                        //     workers.remove(0);
                        // } else {
                        //     ArrayList<String[]> temp = new ArrayList<>();
                        //     temp.addAll((ArrayList<String[]>) queue2.get(0));
                        //     System.out.println("WorkerReducerHandler" + activeWorkers + "> I got from queue and put to temp: " + temp);
                        //     temp.addAll(rooms);
                        //     System.out.println("WorkerReducerHandler" + activeWorkers + "> I put rooms to temp and temp is: " + temp);
                        //     queue2.set(0, temp);
                        //     workers.remove(0);
                        // }

                        // if (workers.isEmpty()) {
                        //     for (int i = 0; i < workers2.size(); i++) {
                        //         workers.add(0);
                        //     }

                        //     System.out.println("WorkerReducerHandler" + activeWorkers + "> " + queue2.get(0));
                        //     synchronized (queue2) {
                        //         queue2.notifyAll();
                        //     }
                        // }

                        ArrayList<String[]> rooms = (ArrayList<String[]>) receiver_copy[0];
                        ArrayList<byte[]> photos = (ArrayList<byte[]>) receiver_copy[1];
                        if (queue2.get(0) == null) {
                            System.out.println("WorkerReducerHandler" + activeWorkers + "> I set: " + rooms);
                            queue2.set(0, rooms);
                            queue2.set(1, photos);
                            workers.remove(0);
                        } else {
                            ArrayList<String[]> temp = new ArrayList<>();
                            ArrayList<byte[]> temp_photos = new ArrayList<>();

                            temp.addAll((ArrayList<String[]>) queue2.get(0));
                            temp_photos.addAll((ArrayList<byte[]>) queue2.get(1));
                            System.out.println("WorkerReducerHandler" + activeWorkers + "> I got from queue and put to temp: " + temp);
                            temp.addAll(rooms);
                            temp_photos.addAll(photos);
                            System.out.println("WorkerReducerHandler" + activeWorkers + "> I put rooms to temp and temp is: " + temp);
                            queue2.set(0, temp);
                            queue2.set(1, temp_photos);
                            workers.remove(0);
                        }

                        if (workers.isEmpty()) {
                            for (int i = 0; i < workers2.size(); i++) {
                                workers.add(0);
                            }

                            System.out.println("WorkerReducerHandler" + activeWorkers + "> " + queue2.get(0));

                            synchronized (queue2) {
                                queue2.notifyAll();
                            }
                        }
                    }
                }







            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
