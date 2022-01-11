import client.Client;
import client.InputThread;
import client.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import server.ConnectExecutor;
import server.OutputThread;
import server.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TestClient {
    public static File file;
    public static String logDirName = "TestClientLog";
    public static String logFileName = "file.txt";

    @BeforeAll
    public static void createTestLogFile() {
        try {
            new File(logDirName).mkdir();
            file = new File(logDirName + "//" + logFileName);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("1 2021-12-18 07:18:26 User: Ivan Message: Hello Jon\n");
            fileWriter.write("2 2021-12-18 07:19:18 User: Jon Message: Hello\n");
            fileWriter.write("3 2021-12-18 07:19:19 User: Jon Message: and bye\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreateLogFile() {
        try {
            Main.createLogFile(logDirName, "testfile.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertTrue(Main.log_file.exists());
    }

    @Test
    void testGetNumberLastMessage() {
        int actual = Client.getNumberLastMessage(file);
        Assertions.assertEquals(3, actual);
    }

    /*@Test
    void testConnect(){
            Client testClient = new Client();
            Assertions.assertEquals(testClient.receiveMessage(), "Добро пожаловать в чат! Введите имя");
    }*/

    @Test
    void testConnectAndSendMessageAndReceiveMessage() {
        Client testClient = new Client();
        testClient.sendMessage(" Тест ");
        Assertions.assertFalse(testClient.receiveMessage().isEmpty());
    }

    @Test
    void testInputThread() {
        Server serverSpy = Mockito.spy(Server.class);
        /*new Thread(()->{
            serverSpy.getConnect();
            //Server.getInstance().getConnect();
        });*/
        Client client = new Client();
        //String[] answers = {"test name", "test "};
        client.sendMessage("exit");
        //Server.getInstance().addMessageToQueue("999", "My name", "My message", "2021-12-18 00:00:00");
        /*while(client.receiveMessage().equals("start message")){

        }*/
        //ConnectExecutor connectExecutorSpy = Mockito.spy(ConnectExecutor.class);
        //Mockito.when(connectExecutorSpy.)

        ConcurrentLinkedQueue<String[]> messagesQueue = new ConcurrentLinkedQueue<>();
        String[] message = {"999", "My name", "My message", "2021-12-18 00:00:00"};
        messagesQueue.add(message);
        Mockito.when(serverSpy.getMessages())
                .thenReturn(messagesQueue);
        System.out.println(messagesQueue);
        /*Mockito.when(Server.getClients())
                .then(client.get)*/

        Thread clientInputThread = new Thread(new InputThread(client));
        clientInputThread.start();
        /*InputThread inputThread = new InputThread(client);
        inputThread.run();
        */
        //new Thread(new OutputThread()).start();
        //Thread testInputStreamClientThread = new Thread();
        //testInputStreamClientThread.start();
        try {
            Main.createLogFile(logDirName, logFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Assertions.assertTrue(Server.getInstance().getMessages().isEmpty());
        //Assertions.assertEquals(Client.getNumberLastMessage(file), 999);
    }
}
