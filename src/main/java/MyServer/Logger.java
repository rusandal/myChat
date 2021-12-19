package MyServer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger instance;
    public static File file;

    private Logger(File file) {
        this.file = file;
    }

    public static Logger getInstance(File file) {
        if(instance==null){
            instance=new Logger(file);
        }
        return instance;
    }

    public void loggerWriter(String text, String user) {
        try (FileWriter writer = new FileWriter(file, true);
             BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line=null;
            String lastMessageLine=null;
            while ((line = br.readLine()) != null) {
                lastMessageLine=line;
                //System.out.println(line);
            }
            if (lastMessageLine==null) {
                writer.write(1 + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " User: " + user + " Сообщение: " + text);
                writer.append('\n');
                writer.flush();
            } else {
                String[] words = lastMessageLine.split(" ");
                writer.write(Integer.parseInt(words[0]) + 1 + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " User: " + user + " Сообщение: " + text);
                writer.append('\n');
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
