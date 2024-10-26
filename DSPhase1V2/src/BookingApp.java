

public class BookingApp extends Thread{
    public static void main(String[] args) {
        Client th = new Client(0);
        th.start();
    }
}
