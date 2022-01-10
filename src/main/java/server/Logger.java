package server;

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

    public synchronized String loggerWriter(String user, String text, String time) {
        try (FileWriter writer = new FileWriter(file, true);
             BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line=null;
            String lastMessageLine=null;
            while ((line = br.readLine()) != null) {
                lastMessageLine=line;
                //System.out.println(line);
            }
            if(lastMessageLine==null){
                lastMessageLine="0";
            }

            /*if (lastMessageLine==null) {
                writer.write(1 + " " +time+ " User: " + user + " Message: " + text);
                writer.append('\n');
                writer.flush();
                return 1;
            } else {*/
                String[] words = lastMessageLine.split(" ");
                int lastNumberMessage = Integer.parseInt(words[0]) + 1;
                writer.write(lastNumberMessage + " " +time+ " User: " + user + " Message: " + text);
                writer.append('\n');
                writer.flush();
                return String.valueOf(lastNumberMessage);
           // }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
