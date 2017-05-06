package role.extension.chat;

import role.Message;
import role.connection.Connection;
import role.connection.IConnection;
import role.extension.IExtension;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Created by Victor on 2017/5/5.<br>
 * <br>
 * An extension for chatting with each other.
 * In this extension, messages from the remote will be read and instantly outputted.
 * Meanwhile, it sends what you input to the remote.
 */
public class Chat implements IExtension{

    /**
     * Constructor.
     * @param ip The remote IP address.
     * @param port The remote port.
     * @param role_id The client id to identify this client. If logging in, the id is the user_id, or it's the ip address.
     */
    public Chat(String ip, int port, String role_id) {
        IP = ip;
        PORT = port;
        ROLE_ID = role_id;
    }

    /**
     * Run() method for a thread, also, the main function of Chat Extension.
     * In this method, a connection will be really made.
     */
    @Override
    public void run() {
        // creating connection
        chatConnection = new Connection(IP, PORT, ROLE_ID, "Chat", (Message msg) -> {
            for(BigInteger i = new BigInteger("0"); i.compareTo(msg.getLength()) < 0; i = i.add(new BigInteger("1"))) {
                try {
                    System.out.println((char)msg.getInStream().read());
                } catch (IOException e) {
                    System.out.println("Handling gotten message failed. (This shouldn't happen actually.)");
                    e.printStackTrace();
                    // TODO: use logger
                }
            }
        });

        // TODO: use GUI
        // get and send messages.
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()) {
            Message msg;
            try {
                msg = new Message(input.nextLine());
            } catch (IOException e) {
                System.out.println("Getting string to build message failed, which shouldn't happen actually.)");
                System.out.println("Last line hasn't been sent. Please input again.");
                e.printStackTrace();
                // TODO: use logger
                continue;
            }
            try {
                chatConnection.write(msg);
            } catch (IOException e) {
                System.out.println("Connecting failed.");
                e.printStackTrace();
                // TODO: pop up error message.
            }
        }
        close();
    }

    /**
     * Close the related resource, that is, the Connection made by Chat extension.
     */
    @Override
    public void close() {
        if (chatConnection != null) {
            chatConnection.close();
            chatConnection = null;
        }
    }

    private IConnection chatConnection = null;
    final private String IP;
    final private int PORT;
    final private String ROLE_ID;
}
