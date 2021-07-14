import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread{
    private  int serverPort;
    private ArrayList<ServerWorker> workerslist=new ArrayList<>();
    public Server(int serverPort) {
        this.serverPort=serverPort;
    }
    public ArrayList<ServerWorker> getWorkerslist(){
        return workerslist;
    }
    public void run(){
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("About to accept connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted");
                ServerWorker worker=new ServerWorker(this,clientSocket);
                workerslist.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
        workerslist.remove(serverWorker);
    }
}
