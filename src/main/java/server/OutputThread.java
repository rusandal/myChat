package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OutputThread implements Runnable {

    @Override
    public void run() {
        PrintWriter out;
        String text;
        ConcurrentLinkedQueue<String[]> messagesQueue;
        String[] message;
        while (true) {
            if (!(messagesQueue = Server.getMessages()).isEmpty()) {
                message = messagesQueue.poll();
                for (Socket socket : Server.getClients()) {
                    if (socket.isConnected()) {
                        try {
                            out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("start message");
                            out.println(message[0]+"::text:"+message[1]+"::time:"+message[2]+"::numberMessage:"+message[3]);
                            out.println("end message");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
