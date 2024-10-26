import dsdate.Dates;
import dsvp.ValuePasser;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

//Worker Thread pou tha xeirizontai dedomena
public class Worker extends Thread {

    private ArrayList<Thread> threadlist;
    private int fatherID;

    protected ObjectInputStream in;
    private int id;

    protected ObjectOutputStream out;


    /* katalymata[i][x] [i] - >
    * [i] -> [1...length] = to katalyma (String[])
    * [x] -> [0] = roomName, [1] = noOfPersons, [2] = area, [3] = stars, [4] = NoOfreviews, [5] = roomImage
     */
    protected ArrayList<String[]> katalymata;
    protected ArrayList<Dates> dates;

    protected ValuePasser valueObject;
    private ArrayList<String[]> bookings;
    private ArrayList<Dates> bookingDates;

    private ArrayList<Object> threadconnector;

    private ArrayList<Object> photos;



    public Worker(int id, ObjectOutputStream out, ObjectInputStream in, int fatherID, ArrayList<Dates> dates,
                  ArrayList<String[]> katalymata, ValuePasser valueObject,
                  ArrayList<String[]> bookings, ArrayList<Thread> threadlist, ArrayList<Dates> bookingDates, ArrayList<Object> threadconnector,ArrayList<Object> photos) {
        this.id = id;
        this.out = out;
        this.in = in;
        this.fatherID = fatherID;
        if (id <= 100) {
            this.katalymata = new ArrayList<String[]>();
            this.dates = new ArrayList<Dates>();
            this.bookings = new ArrayList<String[]>();
            this.threadlist = new ArrayList<Thread>();
            this.bookingDates = new ArrayList<Dates>();
            this.threadconnector = new ArrayList<Object>();
            this.photos=new ArrayList<Object>();
            this.threadconnector.add(null);
            this.threadconnector.add(null);


        } else {
            this.dates = dates;
            this.katalymata = katalymata;
            this.valueObject = valueObject;
            this.bookings = bookings;
            this.threadlist = threadlist;
            this.bookingDates = bookingDates;
            this.threadconnector = threadconnector;
            this.photos=photos;
        }


    }

    public int getTID() {
        return this.id;
    }

    public void setTID(int id) {
        this.id = id;
    }

    public int getFID() {
        return this.fatherID;
    }

    public void setFID(int fatherID) {
        this.fatherID = fatherID;
    }


    public void run() {

        //redcon
        if (id > 200) {
            Socket socket = null;

            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            try {

                /* Create socket for contacting the server on port 4321*/

                socket = new Socket("localhost",4323);


                /* Create the streams to send and receive data from server */

                out = new ObjectOutputStream(socket.getOutputStream());

                in = new ObjectInputStream(socket.getInputStream());

                //System.out.println("ALL GOOD WHAT THE F???");

                while(true) {

                    synchronized (threadconnector) {
                       threadconnector.wait();

                    }
                    // System.out.println("Worker " + getTID() + "> I was notified...");
                    // //ArrayList<String[]> testing = (ArrayList<String[]>) threadconnector.get(0);
                    // int worID = (int) threadconnector.get(1);
                    // ValuePasser katal = (ValuePasser) threadconnector.get(0); //gets the object and sends it to reducer
                    // if (!((ArrayList<String[]>) katal.getObjectValues()).isEmpty())
                    //     System.out.println("Worker " + getTID() + "> " + ((ArrayList<String[]>) katal.getObjectValues()) + " from thread: " + worID);
                    // try {
                    //     out.writeObject(katal); // reducer send (WorkerReducerHandler)
                    //     out.flush();
                    // } catch (IOException e) {
                    //     throw new RuntimeException(e);
                    // }

                    
                    System.out.println("Worker " + getTID() + "> I was notified...");
                    //ArrayList<String[]> testing = (ArrayList<String[]>) threadconnector.get(0);
                    int worID = (int) threadconnector.get(1);
                    ValuePasser katal = (ValuePasser) threadconnector.get(0); //gets the object and sends it to reducer
                    Object[] received = (Object[]) katal.getObjectValues();
                    ArrayList<String[]> receivedRooms = (ArrayList<String[]>) received[0];
                    //ArrayList<byte[]> receivedPhoto = (ArrayList<byte[]>) received[1];
                    if (!receivedRooms.isEmpty())
                        System.out.println("Worker " + getTID() + "> " + receivedRooms + " from thread: " + worID);
                    try {
                        out.writeObject(katal); // reducer send (WorkerReducerHandler)
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }


            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    in.close();	out.close();
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } else if (id > 100) { //Thread that handles alla pragmata
            //ValuePasser receiver;
            System.out.println("Worker "+ getTID() +"> Thread Started");
            if (valueObject.getAction() == 1) {
                // String[] roomValues = (String[]) valueObject.getObjectValues();
                // System.out.println("Worker "+ getTID() +"> I got room: " + roomValues[0]);
                // katalymata.add(roomValues);

                Object[] room_and_icon = (Object[]) valueObject.getObjectValues();
                String[] roomValues = (String[]) room_and_icon[0];
                byte[] fileData = (byte[]) room_and_icon[1];
                String fileName = (roomValues[0] + "manager" + roomValues[7]);
                System.out.println("Worker "+ getTID() +"> I got room: " + roomValues[0]);
                katalymata.add(roomValues);
                photos.add(roomValues[0]);//stores name of room (0-2),(3-5)...
                photos.add(roomValues[7]);//stores name manager of room
                photos.add(fileData);//stores photo

                File directory = new File("received_files_Worker" + getFID());
                if (!directory.exists()) {
                    directory.mkdir();
                }

                // Save the file inside the directory
                File file = new File(directory, fileName + ".jpg");
                FileOutputStream fileOut = null;
                try {
                    fileOut = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    fileOut.write(fileData);
                    fileOut.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }






            } else if (valueObject.getAction() == 2) {
                Dates date = (Dates) valueObject.getObjectValues();
                System.out.println("Worker "+ getTID() +"> I got Dates for Room: " + date.getRoomName() + " and manager " + date.getClientID());
                dates.add(date);
                // TODO check if clientID orizetai swsta


            } else if (valueObject.getAction() == 3) {


                // ArrayList<String[]> newkatal = new ArrayList<String[]>();
                // //threadconnector.add()
                // System.out.println("Worker "+ getTID() +"> Got request for rooms, sending rooms");
                // if (valueObject.getObjectValues() == null) {
                //     System.out.println("Worker "+ getTID() +"> We dont got filters...");
                //     System.out.println("Worker "+ getTID() +"> Im gonna send: " + katalymata.size() + " rooms");
                //     System.out.println("Worker "+ getTID() +"> I got rooms: " + katalymata);
                //     for (String[] k : katalymata) {
                //         System.out.println(k[0] + ", " + k[7]);
                //     }
                //     newkatal.addAll(katalymata);
                // } else {
                //     System.out.println("Worker "+ getTID() +"> We got filters!!!");
                //     ArrayList<String[]> rooms = filter((Object[]) valueObject.getObjectValues());
                //     newkatal.addAll(rooms);
                // }


                // ValuePasser katal = new ValuePasser(newkatal, 3, getTID());
                // System.out.println("Worker "+ getTID() +"> " + katal.getObjectValues());
                // threadconnector.set(0, katal); //communication between threads with shared object. Worker 100 - Worker 200
                // threadconnector.set(1, getTID());
                // synchronized (threadconnector) { //Goes to send to reducer
                //     threadconnector.notifyAll();
                // }
                // /*try {
                //     out.writeObject(katal);
                //     out.flush();
                // } catch (IOException e) {
                //     throw new RuntimeException(e);
                // }*/

                ArrayList<String[]> newkatal = new ArrayList<String[]>();
                ArrayList<byte[]> photos_to_send = new ArrayList<byte[]>();
                //threadconnector.add()
                System.out.println("Worker "+ getTID() +"> Got request for rooms, sending rooms");
                if (valueObject.getObjectValues() == null) {
                    System.out.println("Worker "+ getTID() +"> We dont got filters...");
                    System.out.println("Worker "+ getTID() +"> Im gonna send: " + katalymata.size() + " rooms");
                    System.out.println("Worker "+ getTID() +"> I got rooms: " + katalymata);
                    for (String[] k : katalymata) {
                        System.out.println(k[0] + ", " + k[7]);
                    }
                    newkatal.addAll(katalymata);
                } else {
                    System.out.println("Worker "+ getTID() +"> We got filters!!!");
                    ArrayList<String[]> rooms = filter((Object[]) valueObject.getObjectValues());
                    newkatal.addAll(rooms);
                }


                for (String[] k : newkatal) {
                    for (int l = 0; l < photos.size(); l = l + 3) {
                        if (Objects.equals(k[0], (String) photos.get(l)) && Objects.equals(k[7], (String) photos.get(l + 1))) {
                            System.out.println("Worker "+ getTID() +"> Room: " + k[0] + "," + k[7]);
                            System.out.println("Worker "+ getTID() +"> Photo: " + (String) photos.get(l) + "," + (String) photos.get(l + 1));
                            photos_to_send.add((byte[]) photos.get(l + 2));
                        }
                    }
                }

                Object[] list = {newkatal, photos_to_send};

                ValuePasser katal = new ValuePasser(list, 3, getTID());
                Object[] receivetest = (Object[]) katal.getObjectValues();

                System.out.println("Worker "+ getTID() +"> " +  receivetest[0]);
                threadconnector.set(0, katal); //communication between threads with shared object. Worker 100 - Worker 200
                threadconnector.set(1, getTID());//type redcon in control f to get there
                synchronized (threadconnector) { //Goes to send to reducer
                    threadconnector.notifyAll();
                }



            } else if (valueObject.getAction() == 4) {
                String[] roomValues = (String[]) valueObject.getObjectValues();
                for (String[] i : katalymata) {
                    if (Objects.equals(i[0], roomValues[0]) && Objects.equals(i[7], roomValues[7])) {


                        float totalStars = (Float.parseFloat(i[3]) * Float.parseFloat(i[4]));
                        i[4] = String.valueOf(Float.parseFloat(i[4]) + 1);


                        System.out.println("Worker "+ getTID() +"> " + totalStars);
                        i[3] = String.valueOf((totalStars + Float.parseFloat(roomValues[8])) / Float.parseFloat(i[4]));


                        float x = Float.parseFloat(i[3]);
                        float roundedX = Math.round(x * 10.0f) / 10.0f;
                        i[3] = String.format("%.1f", roundedX);
                        //System.out.println(i[3]); // Output will be 1.7


                        System.out.println("Worker "+ getTID() +"> new Rating for room " + i[0] + " is: " + i[3]);
                    }
                }
            } else if (valueObject.getAction() == 5) {
                Dates roomValues = (Dates) valueObject.getObjectValues();
                int bookStatus = 0;
                System.out.println("Worker "+ getTID() +"> Room sent: " + roomValues.getRoomName() + ", Room here: " + katalymata.get(0)[0]);
                System.out.println("Worker "+ getTID() +"> Client sent: " + roomValues.getClientID() + ", Client here: " + katalymata.get(0)[7]);
                for (Dates j: bookingDates) { //an yparxei hdh sta bookings
                    if (Objects.equals(j.getRoomName(), roomValues.getRoomName()) && Objects.equals(j.getClientID(), roomValues.getClientID())
                            && Objects.equals(j.getStartDate(), roomValues.getStartDate()) && Objects.equals(j.getEndDate(), roomValues.getEndDate())) {
                        System.out.println("OK");
                        bookStatus = 2;
                    }
                }

                for (Dates k : dates) {
                    if (Objects.equals(k.getRoomName(), roomValues.getRoomName()) && k.getClientID() == roomValues.getClientID() && bookStatus != 2) {

                        if ((roomValues.getStartDate().isBefore(k.getStartDate())) || (k.getEndDate().isBefore(roomValues.getEndDate()))) {

                            bookStatus = 3;
                        }
                    }
                }




                for (String[] i : katalymata) {
                    if (Objects.equals(i[0], roomValues.getRoomName()) && Integer.parseInt(i[7]) == roomValues.getClientID() && bookStatus != 2) {

                        System.out.println("Worker "+ getTID() +"> New Booking for room " + i[0] + " on manager " + i[7]);
                        System.out.println("Worker "+ getTID() +"> On dates: " + roomValues.getStartDate() + ", " + roomValues.getEndDate());
                        bookStatus = 1;
                        bookings.add(i);
                        bookingDates.add(roomValues);
                    }
                }

                System.out.println("Worker "+ getTID() +"> Im gonna send: " + bookStatus);
                try {
                    out.writeInt(bookStatus);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            } else if (valueObject.getAction() == 6) {
                int manager = valueObject.getClient();
                ArrayList<String[]> bookingsReturn = new ArrayList<String[]>();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                int counter = 0;
                for (String[] i : bookings) {

                    if (Objects.equals(i[7], String.valueOf(manager))) {
                        System.out.println(i[0] + ", " + manager);
                        String startDate = bookingDates.get(counter).getStartDate().format(formatter);
                        System.out.println(startDate);
                        String endDate = bookingDates.get(counter).getEndDate().format(formatter);
                        System.out.println(endDate);
                        bookingsReturn.add(i);
                        bookingsReturn.add(new String[]{startDate, endDate});

                    }
                    counter++;
                }

                ValuePasser returner = new ValuePasser(bookingsReturn, 3, getTID());
                System.out.println("Worker "+ getTID() +"> " + returner.getObjectValues());
                try {
                    out.writeObject(returner);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            


            } else if (valueObject.getAction() == 7) {

                int manager = valueObject.getClient();
                LocalDate[] dates2 = (LocalDate[]) valueObject.getObjectValues();
                ArrayList<String> toreturn_areas = new ArrayList<String>();
                for (Dates i : dates) {
                    if (i.isAvailableOn(i.getStartDate(), dates2[0], dates2[1])
                            && i.isAvailableOn(i.getEndDate(), dates2[0], dates2[1]) && i.getClientID() == manager) {
                                System.out.println(i.getRoomName());
                                toreturn_areas.add(i.getRoomArea());
                    }
                }
                System.out.println("Worker "+ getTID() +"> Printing Areas from Filter");
                System.out.println(toreturn_areas);
                ValuePasser returner = new ValuePasser(toreturn_areas, 7, getTID());
                try {
                    out.writeObject(returner);
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
        }
        //System.out.println("Worker " + getFID() + ">" + valueObject.getAction());
        else {
            ObjectOutputStream out= null ;
            ObjectInputStream in = null ;
            Socket requestSocket= null ;

            //System.out.println("Thread: " + getId() + " is up");
            //System.out.println("Worker Thread: " + getTID() + " is up");
            String host = "localhost";

            try {
                /* Create socket for contacting the server on port 4321*/
                requestSocket = new Socket(host, 4322);



                /* Create the streams to send and receive data from server */
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());
                // Create an object to send
                //Test testObject = new Test(10);

                // Send the object to ActionsForWorkers
                //out.writeObject(testObject);
                //out.flush();


                ValuePasser receiver = (ValuePasser) in.readObject();
                //Action must be 0, no test needed
                String answer = (String) receiver.getObjectValues();
                //System.out.println(answer);
                if (Objects.equals(answer, "Close")) {
                    System.out.println("Worker Connection Limit has been reached. Can't connect...");
                } else {
                    ValuePasser workerID = new ValuePasser(this.getTID(),0, 0);
                    out.writeObject(workerID);
                    out.flush();


                    /*for (int i = 0; i < 4; i++) {

                        Worker workthread = new Worker(i + 101, out, in, fatherID, dates, katalymata, valueObject);
                        threadslist.add(workthread);
                        workthread.start();

                    }*/
                    Worker th = new Worker(getFID() + 200, out, in, fatherID, dates, katalymata, receiver, bookings, null, bookingDates, threadconnector,photos);
                    th.start();
                    int counter = 1;
                    while(true) {

                        if (!threadlist.isEmpty()) {
                            Worker th3 = (Worker) threadlist.get(0);
                            th3.join();

                            threadlist.remove(th3);
                            System.out.println("Worker "+ getTID() +"> " + th3.getTID() + " Thread has been removed");
                        }
                        System.out.println("Worker "+ getTID() +"> Im waiting for object");
                        receiver = (ValuePasser) in.readObject(); //to pairnei epityxws
                        Worker th4 = new Worker(100 + counter, out, in, fatherID, dates, katalymata, receiver, bookings, null, bookingDates, threadconnector,photos);
                        threadlist.add(th4);
                        th4.start();
                        counter++;



                        // TO start thread, then do action then send stuff back then send to reducer.

                    }
                }





            /*while(true) {
                ValuePasser receiver = (ValuePasser) in.readObject(); //to pairnei epityxws
                String[] roomValues = (String[]) receiver.getObjectValues();
                System.out.println("Room with name: " + roomValues[0] + " has been successfully " +
                        "been passed to Worker");
                katalymata.add(roomValues);
                for (String[] i : katalymata) {
                    if (katalymata.size() > 1) {
                        System.out.println(i[0]);
                    }
                }
            }*/

            } catch (UnknownHostException unknownHost) {
                System.err.println("You are trying to connect to an unknown host!");
            } catch (IOException ioException) {
                ioException.printStackTrace();
        /*} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);*/
            } catch (ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    if (requestSocket != null) {
                        requestSocket.close();
                    }

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

    }

    private ArrayList<String[]> filter(Object[] objectValues) {
        ArrayList<String[]> rooms = new ArrayList<String[]>();

        for (Object i : objectValues){
            System.err.println(i.getClass());
            System.err.println(i.toString());
        }


        for (String[] i : katalymata) {
            int filterLevel = 0;
            System.out.println(i[2] + i[1] + i[3]);
            //checks for area, numOfPeople, stars
            /*System.out.println((String) objectValues[0]);
            System.out.println(i[2]);
            System.out.println((String) objectValues[3]);*/
            if (Objects.equals((String) objectValues[0], i[2]) && Objects.equals((String) objectValues[3], i[1])
                    &&  Objects.equals((String) objectValues[6] , i[3])) {
                    System.out.println("Worker "+ getTID() +"> Room: " + i[0] + " matches filter for Area, NumOfPeople and Stars");
                    filterLevel++;
            } else {
                continue;
            }


            //checks for min, max prise
            if (Integer.parseInt(i[5]) >= Integer.parseInt((String) objectValues[4]) && Integer.parseInt(i[5]) <= Integer.parseInt((String) objectValues[5])) {
                System.out.println("Worker "+ getTID() +"> Room: " + i[0] + " matches filter for Price");
                filterLevel++;
            } else {
                continue;
            }

            //gia veltistopoihsh kane thn dates hashmap
            for (Dates j : dates) {
                if (Objects.equals(j.getRoomName(), i[0])) {
                    if (j.isAvailableOn(((LocalDate) objectValues[1]), j.getStartDate(), j.getEndDate())
                            && j.isAvailableOn(((LocalDate) objectValues[2]), j.getStartDate(), j.getEndDate())) {


                        System.out.println("Worker "+ getTID() +"> Room: " + i[0] + " matches filter for Dates");
                        filterLevel++;


                    }
                }
            }

            if (filterLevel == 3) {
                rooms.add(i);
            }


        }
        return rooms;
    }


    public static void main(String[] args) {

        Random random = new Random();
        int randomNumber = random.nextInt(100);
        new Worker(randomNumber, null, null, randomNumber, null, null, null, null, null, null, null,null).start();
        //new Worker(randomNumber + 200, null, null, randomNumber, null, null, null, null, null, null, null).start();
        //new Worker().start();




    }
}
