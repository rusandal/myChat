package server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectExecutor implements Runnable {
    private Socket clientSocket;
    private String clientName;
    private static Lock lock = new ReentrantLock(true);
    private StringBuffer sb = new StringBuffer();
    //public static ConcurrentHashMap<String, String> concurrentHashMapMessages = new ConcurrentHashMap();

    public ConnectExecutor(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             //FileWriter writer = new FileWriter("ServerLog//file.txt", true);

        ) {
            FileReader reader = new FileReader(Server.getLogFile());
            System.out.println("Новое соединение установлено");
            out.println("Добро пожаловать в чат! Введите имя");
            clientName = in.readLine();
            if (clientName.equals("exit")) return;
            out.println("file.txt exist?");
            String clientFileExist = in.readLine();
            //Получаем номер последнего сообщения клиента
            int clientLastMessageNumber = Integer.parseInt(clientFileExist.split(" ")[1]);
            //Получаем список сообщений, которых нет у клиента
            LinkedList<String> list = getNewMessagesForClient(clientLastMessageNumber, reader);
            //Отправляем клиенту для записи в историю
            while (!list.isEmpty()) {
                out.println(list.pollFirst());
            }
            out.println("end");

            out.println("Вы зарегистрированы в чате как " + clientName);
            Server.addActiveListClients(clientSocket);

            String text = "";
            String time;
            while (true) {
                if (!clientSocket.isConnected()) {
                    Server.removeActiveSocket(clientSocket);
                    break;
                }
                //Получаем сообщение от клиента
                text = in.readLine();
                //Проверяем на сигнал завершения от клиента
                if (text.equals("/exit")) {
                    System.out.println("Клиент закрыл соединение");
                    return;
                }

                //Записываем сообщение в файл сервера
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String numberLastMessageHistory = Logger.getInstance(Server.getLogFile()).loggerWriter(clientName, text, time);
                Server.addMessageToQueue(numberLastMessageHistory,clientName, text, time);
                //concurrentHashMapMessages.put(name, text);
                //следом получаем номер последнего сообщения клиента записанный в его файле истории


                /*clientLastMessageNumber=Integer.parseInt(in.readLine());
                lock.unlock();
                reader = new FileReader("ServerLog//file.txt");
                //Получаем все записи из файла сервера, которых нет у клиента
                list = getNewMessagesForClient(clientLastMessageNumber, reader);
                //Отправляем все новые сообщения клиенту, в том числе и текущее
                while (!list.isEmpty()){
                    out.println("log:"+list.pollFirst());
                }
                //Отправляем флаг, что передача закончена
                out.println("end");*/
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



    public static LinkedList<String> getNewMessagesForClient(int clientLastMessageNumber, FileReader reader){
        LinkedList<String> list = new LinkedList<>();
        try(BufferedReader br = new BufferedReader(reader)){
            String line;
            int numberMessage;
            lock.lock();
            //Создаем список сообщений, которых нет у клиента, сверяя по номеру строки
            while ((line = br.readLine())!=null){
                numberMessage = Integer.parseInt(line.split(" ")[0]);
                if (numberMessage>clientLastMessageNumber){
                    list.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
            return list;
        }
    }

    public String getClientName() {
        return clientName;
    }
}
