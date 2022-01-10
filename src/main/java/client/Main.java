package client;

import java.io.*;

public class Main {
    public static File log_file;

    public static void main(String[] args) {
        Client client = new Client();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))){
            System.out.println("Соединение с сервером установлено");
            //Печатаем полученное от сервера
            System.out.println(client.receiveMessage());
            //Пишем имя
            client.setName(reader.readLine());
            //Отправляем на сервер имя
            client.sendMessage(client.getName());
            createLogFile("ClientLog", "file.txt");
            //Смотрим файл с историей и получаем номер последнее сообщение
            int numberLastMessage = client.getNumberLastMessage(log_file);
            //Получаем запрос на наличие файла с историей
            client.receiveMessage();
            if (log_file.exists()) {
                //Отправляем что файл существует и добавляем серверу информацию о последней записи
                client.sendMessage("exists " + numberLastMessage);
            } else {
                client.sendMessage("not exists");
                System.out.println("Проверьте права доступа на создание файлов и запустите программу заново");
                return;
            }

            String text=null;
            //Получаем историю от сервера с момента последней записи и записываем в файл
            while (!(text=client.receiveMessage()).equals("end")) {
                client.updateHistory(text);
                System.out.println("Пропущенное сообщение: "+text.substring(text.indexOf(" ")));
            }
            //Получаем и печатаем сообщение о регистрации в чате
            System.out.println(client.receiveMessage());
            //Запускаем поток для отслеживания очереди сообщений и отправки их клиентам
            Thread thread = new Thread(new InputThread(client));
            thread.setDaemon(true);
            thread.start();
            //Запускаем цикл отправки сообщений на сервер
            while (true) {
                text = reader.readLine();
                if (text.equals("/exit")) {
                    break;
                }
                if (text.isEmpty()) continue;
                //Отправляем текст на сервер
                client.sendMessage(text.trim());
                //Добавляем текст в Set для исключения печати собственных сообщений в консоли
                client.setMyTexts(text.trim());
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void createLogFile(String nameDir, String file) throws IOException{
        File dir = new File(nameDir);
        if (dir.mkdir()) System.out.println("Каталог создан");
        log_file=new File(nameDir+"//"+file);
        if (!log_file.exists()) {
            log_file.createNewFile();
        }
    }
}
