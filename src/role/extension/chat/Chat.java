package role.extension.chat;

import role.Message;
import role.connection.HandleReadingMessage;
import role.extension.Extension;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Victor on 2017/5/5.<br>
 * <br>
 * An extension for chatting with each other.
 * In this extension, messages from the remote will be read and instantly outputted.
 * Meanwhile, it sends what you input to the remote.
 */
public class Chat extends Extension implements HandleReadingMessage{

    /**
     * Constructor.
     * @param ip The remote IP address.
     * @param port The remote port.
     * @param me_id The client id to identify THIS client. If logging in, the id is the user_id, or it's the ip address.
     */
    public Chat(String ip, int port, String me_id) {
        super(ip, port, me_id, EXTENSION_NAME);
    }

    /**
     * Create the Chat extension from an already existing socket. Usually it's call when the THIS user acts as a server.
     * @param socket The existing socket.
     * @param socket_type The type of the socket.
     * @param pair_hash The hash of the socket pair which the socket is in.
     * @param me_id The client id to identify THIS client. If logging in, the id is the user_id, or it's the ip address.
     */
    public Chat(Socket socket, int socket_type, int pair_hash, String me_id) {
        super(socket, socket_type, pair_hash, me_id, EXTENSION_NAME);
    }

    /**
     * Run() method for a thread, also, the main function of Chat Extension.
     * In this method, a connection will be really made.
     */
    @Override
    public void run() {
        // creating connection
        createConnections(1);

        // TODO: use GUI
        // get and send messages.
        Scanner input;
        synchronized (this) {
            input = new Scanner(System.in);
        }
        while (input.hasNextLine()) {
            Message msg;
            try {
                synchronized (this) {
                    msg = new Message(input.nextLine());
                }
            } catch (IOException e) {
                synchronized (this) {
                    System.out.println("Getting string to build message failed, which shouldn't happen actually.)");
                    System.out.println("Last line hasn't been sent. Please input again.");
                    e.printStackTrace();
                }
                // TODO: use logger
                continue;
            }
            try {
                connectionMap.values().iterator().next().write(msg);
            } catch (IOException e) {
                synchronized (this) {
                    System.out.println("Connecting failed.");
                    e.printStackTrace();
                }
                // TODO: pop up error message.
            }
        }

        close();
    }

    /**
     * Methods to handle the messages received from the remote.
     * @param msg The Message to be handled.
     */
    @Override
    public void handleMsg(Message msg) {
        for(BigInteger i = new BigInteger("0"); i.compareTo(msg.getLength()) < 0; i = i.add(new BigInteger("1"))) {
            try {
                synchronized (this) {
                    System.out.print((char) msg.getInStream().read());
                }
            } catch (IOException e) {
                synchronized (this) {
                    System.out.println("Handling gotten message failed. (This shouldn't happen actually.)");
                    e.printStackTrace();
                }
                // TODO: use logger
            }
        }
    }

    final static private String EXTENSION_NAME = "chat.Chat";
}
