package MyClient;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class Client {
    private static final Properties properties = new Properties();
    public static final File file = new File("ClientLog//file.txt");
    private static String host;
    private static Integer port;
    public static BufferedReader in;
    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader reader;
    private static File dir;
    private String name;

    public Client() throws IOException {
        setProperties();
        clientSocket = new Socket(host, port);
        in = new BufferedReader(new
                InputStreamReader(clientSocket.getInputStream()));
        out = new
                PrintWriter(clientSocket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(System.in));
        dir = new File("ClientLog");
        if (dir.mkdir()) System.out.println("Каталог создан");
        if (!file.exists()) {
            file.createNewFile();
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


    public static void main(String[] args) {
        try {
            Client client = new Client();

            System.out.println("Соединение с сервером установлено");
            System.out.println(client.receiveMessage());
            //System.out.println(in.readLine());

            //FileReader fileReader=new FileReader(file);
            //Пишем имя
            client.name = reader.readLine();
            //Отправляем на сервер имя
            client.sendMessage(client.name);

            //Смотрим файл с историей и получаем последнее сообщение
            int numberLastMessage = getNumberLastMessage(file);

            //Получаем запрос на наличие файла с историей
            client.receiveMessage();
            if (file.exists()) {
                //Отправляем что файл существует и добавляем серверу информацию о последней записи
                out.println("exists " + numberLastMessage);
            } else {
                out.println("not exists");
                System.out.println("Проверьте права доступа на создание файлов и запустите программу заново");
                return;
            }

            String text=null;
            //Получаем историю от сервера с момента последней записи и записываем в файл
            while (!(text=client.receiveMessage()).equals("end")) {
                updateHistory(text);
            }
            //Получаем и печатаем сообщение о регистрации в чате
            System.out.println(client.receiveMessage());
            String[] split;
            while (true) {
                text = reader.readLine();
                numberLastMessage = getNumberLastMessage(file);
                if (text.equals("/exit")) break;
                if (text.isEmpty()) continue;
                //Отправляем текст
                out.println(text.trim());
                //Отправляем номер последнего сообщения в файле с историей
                client.sendMessage(Integer.toString(numberLastMessage));
                //Получаем новые сообщения с сервера, сохраняем в файл, делим и выводим сообщения которые еще не получали ранее
                while (!(text = in.readLine()).equals("end")) {
                    updateHistory(text);
                    split = text.split(" Сообщение: ");
                    if (!split[1].equals(text) & !split[0].split(" ")[4].equals(client.name)) {
                        System.out.println(text);
                    }

                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void updateHistory(String string) {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(string);
            writer.append('\n');
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int getNumberLastMessage(File file) {
        //создаем BufferedReader с существующего FileReader для построчного считывания
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // считаем сначала первую строку
            String line;
            String lastMessage = null;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                // считываем остальные строки в цикле
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
            properties.load(fis);
            host = properties.getProperty("server_host");
            port = Integer.parseInt(properties.getProperty("server_port"));
        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }
    }

    public static String getMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHost() {
        return host;
    }
}
