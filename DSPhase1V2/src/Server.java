import dsvp.ValuePasser;



import java.io.*;

import java.net.*;
import java.util.ArrayList;
import java.util.Objects;


// Server einai o Master edw ginontai syndeseis tou Console App (Client) me ton Master (Server)

// Client einai to Console App. Se auto syndeontai idiokthtes kai enoikiastes

/* ActionsForClients einai h polynhmatikh xrhsh tou Master.
Dhladh epitrepei se pollous Clients na syndethoun taytoxrona se enan server */

// Workers einai Threads tou Master pou epitrepoun polynhmatikh diaxeirhsh dedomenwn gia mapreduce




public class Server extends Thread { //AUTOS EINAI O MASTER

    //Client and Worker Server opens
    int connectorType;

    int numOfWorkers;
    /* Define the socket that receives requests */

    ServerSocket s;
    ServerSocket sw;

    ServerSocket sr;
    /* Define the socket that is used to handle the connection */
    Socket providerSocket;

    /*
     * ArrayDeque<String[]> queue
     * Xrhsimopoieitai gia thn epikoinwnia metaksy tou ClientHandler kai tou WorkerHandler
     *
     * queue[0] contains the Action that is happening
     * queue[1] contains the name of the room
     * queue[2] contains the ID of the Worker that must do the calculations
     * queue[3] contains the Dates for rent that are to be added to a room
     * queue[4] same as 2 but needs a different memory
     * queue[5] contains the ID of the Client that sent the data
     * queue[6] contains the Rooms from each Worker
     * queue[7] Booking Start Date
     * queue[8] adds Filters
     * queue[9] Booking End Date
     *
     *
     */
    ArrayList<Object> queue;

    ArrayList<ActionsForWorkers> workers;
    ArrayList<ActionsForWorkers> workers2;

    ArrayList<ActionsForClients> clients;

    public Server(int connectorType, int numOfWorkers, ArrayList<ActionsForWorkers> workers, ArrayList<ActionsForWorkers> workers2, ArrayList<Object> queue, ArrayList<ActionsForClients> clients) {

        this.connectorType = connectorType;
        this.numOfWorkers = numOfWorkers;
        if (connectorType == -1) {
            this.workers = new ArrayList<ActionsForWorkers>();
            this.workers2 = new ArrayList<ActionsForWorkers>();
            this.clients = new ArrayList<ActionsForClients>();
            this.queue = new ArrayList<Object>();
            /*this.queue.add(new String[1]);
            this.queue.add(new String[1]);
            this.queue.add(new String[1]);
            this.queue.add(new String[1]);
            this.queue.add(new String[1]);
            this.queue.add(new String[1]);
            this.queue.add(new String[1]);*/
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
            this.queue.add(null);
        } else {
            this.clients = clients;
            this.workers = workers;
            this.workers2 = workers2;
            this.queue = queue;
        }
        //this.queue = queue;
    }
 
    public static void main(String[] args) {
        
        int workerNum=2;
        // if (args[0] == null) {
        //     workerNum = 2;
        // } else {
        //     workerNum = Integer.parseInt(args[0]);
        // }

        new Server(-1, workerNum, null, null, null, null).start(); //Starting Thread, passes



    }

    public void run() {
        if (connectorType == 0) {
            try {
                s = new ServerSocket(4321, 10);//the same port as before, 10 connections;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            openServer(numOfWorkers, s, "Client");
        } else if (connectorType == 1) {
            try {
                sw = new ServerSocket(4322, 10); //Socket gia Workers
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            openServer(numOfWorkers, sw, "Worker");
        } else if (connectorType == 2) {
            try {
                sr = new ServerSocket(4324, 10); //Socket gia Reducer
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            openServer(numOfWorkers, sr, "Reducer");
        } else {
            new Server(0, numOfWorkers, workers, workers2, queue, clients).start();
            new Server(1, numOfWorkers, workers, workers2, queue, clients).start();
            new Server(2, numOfWorkers, workers, workers2, queue, clients).start();
        }

    }

    void openServer(int numOfWorkers, ServerSocket sws, String connection) {
        System.out.println("Server> New " + connection + " Server Thread started...");

        try {

            int workerCounter = numOfWorkers;
            boolean canOpen = true;
            if (connection == "Worker") {
                System.out.println("Server> Workers allowed to connect: " + workerCounter);
            }



 
            /* Create Server Socket */
            //s = new ServerSocket(4321, 10);//the same port as before, 10 connections
            //sw = new ServerSocket(4322, 10); //Socket gia Workers

            while (true) {
                /* Accept the connection */

                providerSocket = sws.accept();
                if (Objects.equals(connection, "Client")) {
                    System.out.println("Server> New Client connected: " + providerSocket);
                    Thread clientThread = new ActionsForClients(providerSocket, queue, workers, workers2, numOfWorkers, clients);
                    clientThread.start();
                } else if (Objects.equals(connection, "Worker")) {
                    if (workerCounter == 0) {
                        System.out.println("Server> No more Workers are allowed to connect");
                        canOpen = false;
                    } else {
                        System.out.println("Server> New Worker connected with Port: " + providerSocket);
                    }


                    Thread workerThread = new ActionsForWorkers(providerSocket, numOfWorkers, queue, workers, workers2, canOpen);
                    workerThread.start();
                    workerCounter--;


                } else { //Reducer Thread - Server Thread connection and data collector
                    ObjectOutputStream out = null;
                    ObjectInputStream in = null;
                    System.out.println("Server> Server and Reducer are connecting... " + providerSocket);
                    try {

                        out = new ObjectOutputStream(providerSocket.getOutputStream());
                        in = new ObjectInputStream(providerSocket.getInputStream());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Server> Connection Success");

                    while(true) {
                        // System.out.println("Server Thread> I am waiting for Object...");
                        // ValuePasser receiver = (ValuePasser) in.readObject();
                        // if (receiver.getAction() == 3) {
                        //     int manager = receiver.getClient();
                        //     ArrayList<String[]> rooms = (ArrayList<String[]>) receiver.getObjectValues();

                        //     System.out.println("Server Thread>");
                        //     for (String[] i : rooms) {
                        //         System.out.println(i[0]);
                        //     }

                        //     System.out.println("Server Thread> I got " + rooms + " from the request");
                        //     queue.set(6, rooms);
                        //     queue.set(5, manager);
                        //     synchronized (queue) {
                        //         queue.set(0, 0);
                        //         queue.notifyAll();
                        //     }
                        // }

                        System.out.println("Server Thread> I am waiting for Object...");
                        ValuePasser receiver = (ValuePasser) in.readObject();
                        Object[] receiver_copy = (Object[]) receiver.getObjectValues();
                        if (receiver.getAction() == 3) {
                            int manager = receiver.getClient();
                            ArrayList<String[]> rooms = (ArrayList<String[]>) receiver_copy[0];
                            ArrayList<byte[]> photos = (ArrayList<byte[]>) receiver_copy[1];

                            System.out.println("Server Thread>");
                            for (String[] i : rooms) {
                                System.out.println(i[0]);
                            }

                            System.out.println("Server Thread> I got " + rooms + " from the request");
                            queue.set(6, rooms);
                            queue.set(5, manager);
                            queue.set(7, photos);
                            synchronized (queue) {
                                queue.set(0, 0);
                                queue.notifyAll();
                            }
                        }




                    }





                    //System.out.println("Server> OH MY DAMN LAWD IT WORKS");
                    //ArrayList<String[]> rooms = queue.get(0);



                }

            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (providerSocket != null) {
                    providerSocket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
