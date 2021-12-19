import MyClient.Client;
import MyServer.Server;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class TestClient {
    public static File file = new File("TestClientLog//file.txt");

    @BeforeAll
    public static void createTestFile(){
        try {
            new File("TestClientLog").mkdir();
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("1 2021-12-18 07:18:26 User: Ivan Сообщение: Hello Jon\n");
            fileWriter.write("2 2021-12-18 07:19:18 User: Jon Сообщение: Hello\n");
            fileWriter.write("3 2021-12-18 07:19:19 User: Jon Сообщение: and bye\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetNumberLastMessage(){

        int actual = Client.getNumberLastMessage(file);

        Assertions.assertEquals(3, actual);
    }

    @Test
    void testConnect(){
        try {
            Client testClient = new Client();
            Assertions.assertEquals(testClient.receiveMessage(), "Добро пожаловать в чат! Введите имя");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testReceiveMessage(){
        try {
            Client testClient = new Client();
            testClient.sendMessage(" Тест ");
            Assertions.assertTrue(!testClient.receiveMessage().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
