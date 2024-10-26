

public class ManagerApp extends Thread{
    public static void main(String[] args) {
        Client th = new Client(1);
        th.start();
    }
}
