package MyServer;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        getConnect();
    }

    public static void getConnect(){
        try (
                ServerSocket serverSocket = new ServerSocket(23444);
        ) {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            while (true) {
                Socket clientSocket = serverSocket.accept();
                File dir = new File("ServerLog");
                if (dir.mkdir()) System.out.println("Каталог создан");
                threadPool.execute(new RunnableThread(clientSocket));
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
