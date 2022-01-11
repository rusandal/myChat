import server.Logger;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;

public class TestServer {
    public static final File file = new File("TestServerLog//file.txt");

    @Test
    void testConnect(){
        boolean status=false;
        try(Socket testSocket = new Socket("127.0.0.1", 23444)){
            status=true;
        }catch (IOException e){
            status=false;
        }finally {
            Assertions.assertTrue(status);
        }
    }

    @Test
    void testMultiConnect(){
        boolean status=false;
        try(Socket testSocket1 = new Socket("127.0.0.1", 23444);
            Socket testSocket2 = new Socket("127.0.0.1", 23444);
            Socket testSocket3 = new Socket("127.0.0.1", 23444);
        ){
            status=true;
        }catch (IOException e){
            status=false;
        }finally {
            Assertions.assertTrue(status);
        }
    }

    @Test
    void testLoggerWriter(){
        try {
            new File("TestServerLog").mkdir();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("1 2021-12-18 07:18:26 User: Ivan Message: Hello Jon\n");
            fileWriter.write("2 2021-12-18 07:19:18 User: Jon Message: Hello\n");
            fileWriter.write("3 2021-12-18 07:19:19 User: Jon Message: and bye\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String text = "Тестовое сообщение";
            String name = "Тестовый пользователь";
            String time = "2021-12-18 07:19:19";

            Logger.getInstance(file).loggerWriter(name, text, time);

            Assertions.assertTrue(br.lines().anyMatch(s->s.contains(time+" User: "+name+" Message: "+text)));
        } catch (IOException e){
            e.getMessage();
        }
    }


}
