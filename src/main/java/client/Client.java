package client;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Client {
    private static final Properties PROPERTIES = new Properties();
    private static Socket clientSocket;
    private String host;
    private Integer port;
    private PrintWriter out;
    //private BufferedReader reader;
    private BufferedReader in;
    //private static File dir;
    private String name;
    private Set<String> myTexts = new HashSet<>();

    public Set<String> getMyTexts() {
        return myTexts;
    }

    public void setMyTexts(String myTexts) {
        this.myTexts.add(myTexts);
    }

    public Client(){
        setProperties();
        try {
            clientSocket = new Socket(host, port);
            this.in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
            this.out = new
                    PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateHistory(String string) {
        try (FileWriter writer = new FileWriter(Main.log_file, true)) {
            writer.write(string);
            writer.append('\n');
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(String text) {
        out.println(text);
    }

    public String receiveMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static int getNumberLastMessage(File file) {
        //создаем BufferedReader с существующего FileReader для построчного считывания
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // считаем сначала первую строку
            String line;
            String lastMessage = null;
            // считываем остальные строки в цикле
            while ((line = reader.readLine()) != null) {
                lastMessage = line;
            }
            return Integer.parseInt(lastMessage.split(" ")[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NullPointerException e) {

        }
        return 0;
    }

    public void setProperties() {
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/ClientCfg.ini");
            PROPERTIES.load(fis);
            host = PROPERTIES.getProperty("server_host");
            port = Integer.parseInt(PROPERTIES.getProperty("server_port"));
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл конфигурации отсутствует!");
        }
    }

    /*public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }*/

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Socket getClientSocket() {
        return clientSocket;
    }
}
