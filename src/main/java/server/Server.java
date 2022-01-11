package server;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final Server instance = new Server();
    private List<Socket> clients = new LinkedList<>();
    private ConcurrentLinkedQueue<String[]> messages = new ConcurrentLinkedQueue<>();
    private File file;
    //private static ConcurrentHashMap<Socket, LinkedList<String>> messages = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Server.getInstance().getConnect();
    }

    public void getConnect(){
        try (
                ServerSocket serverSocket = new ServerSocket(23444);
        ) {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            ExecutorService outputThread = Executors.newSingleThreadExecutor();
            outputThread.execute(new OutputThread());
            file = createLogFile("ServerLog", "file.txt");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ConnectExecutor(clientSocket));
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static File createLogFile(String dir, String file){
        File logDir = new File(dir);
        if (logDir.mkdir()) System.out.println("Каталог существует");
        File logFile = new File(dir+"//"+file);
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logFile;
    }

    public File getLogFile(){
        return file;
    }

    public List<Socket> getClients() {
        return clients;
    }

    public ConcurrentLinkedQueue<String[]> getMessages() {
        return messages;
    }

    public void removeActiveSocket(Socket socket){
        clients.remove(socket);
    }

    public void addMessageToQueue(String numberMessageHistory, String name, String text, String time) {
        String[] message = {name, text, time, numberMessageHistory};
        Server.getInstance().messages.add(message);
    }

    public void addActiveListClients(Socket socket) {
        clients.add(socket);
    }

    public static Server getInstance(){
        return instance;
    }
}
