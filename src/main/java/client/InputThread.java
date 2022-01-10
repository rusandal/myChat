package client;

public class InputThread implements Runnable {
    Client client;

    public InputThread(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        String inputString;
        String text;
        String[] split;
        String author;
        String time;
        String numberMessage;

        while (true) {
            if ((inputString=client.receiveMessage())!=null) {
                if(inputString.equals("start message")){
                    inputString = client.receiveMessage();
                    split = inputString.split("::text:");
                    author = split[0];
                    split = split[1].split("::time:");
                    text = split[0];
                    split = split[1].split("::numberMessage:");
                    time = split[0];
                    numberMessage = split[1];
                    if (client.receiveMessage().equals("end message")) {
                        client.updateHistory(numberMessage + " " + time + " User: " + author + " Message: " + text);
                        if (!author.equals(client.getName()) & !client.getMyTexts().contains(text)) {
                            System.out.println(time + " User: " + author + " Message: " + text);
                        }
                    }
                }

            }
        }
    }
}
