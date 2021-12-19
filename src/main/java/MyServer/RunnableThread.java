package MyServer;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RunnableThread implements Runnable {
    private Socket clientSocket;
    private static Lock lock = new ReentrantLock(true);
    private StringBuffer sb = new StringBuffer();

    public RunnableThread(Socket clienSocket) {
        this.clientSocket = clienSocket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             //FileWriter writer = new FileWriter("ServerLog//file.txt", true);

        ) {
            File file = new File("ServerLog//file.txt");
            file.createNewFile();
            FileReader reader = new FileReader(file);
            System.out.println("Новое соединение установлено");
            out.println("Добро пожаловать в чат! Введите имя");
            String name = in.readLine();
            if (name.equals("exit")) return;
            out.println("file.txt exist?");
            String clientFileExist = in.readLine();
            //Получаем номер последнего сообщения клиента
            int clientLastMessageNumber = Integer.parseInt(clientFileExist.split(" ")[1]);
            //Получаем список сообщений, которых нет у клиента
            LinkedList<String> list = getNewMessagesForClient(clientLastMessageNumber,reader);
            //Отправляем клиенту для записи в историю
            while (!list.isEmpty()){
                out.println(list.pollFirst());
            }
            out.println("end");

            out.println("Вы зарегистрированы в чате как " + name);

            String text = "";

            while (true) {
                //Получаем сообщение от клиента
                text = in.readLine();
                //Проверяем на сигнал завершения от клиента
                if (text.equals("/exit")) {
                    System.out.println("Клиент закрыл соединение");
                    return;
                }
                lock.lock();
                //Записываем сообщение в файл сервера
                Logger.getInstance(file).loggerWriter(text, name);
                //следом получаем номер последнего сообщения клиента записанный в файл
                clientLastMessageNumber=Integer.parseInt(in.readLine());
                lock.unlock();
                reader = new FileReader("ServerLog//file.txt");
                //Получаем все записи из файла сервера, которых нет у клиента
                list = getNewMessagesForClient(clientLastMessageNumber, reader);
                //Отправляем все новые сообщения клиенту, в том числе и текущее
                while (!list.isEmpty()){
                    out.println(list.pollFirst());
                }
                //Отправляем флаг, что передача закончена
                out.println("end");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
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
        } catch (IOException e){
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
            return list;
        }
    }
}
