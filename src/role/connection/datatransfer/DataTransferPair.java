package role.connection.datatransfer;

import role.Message;
import role.connection.IConnection;
import role.connection.HandleReadingMessage;
import role.connection.datatransfer.socket.socketclient.SocketReadThread;
import role.connection.datatransfer.socket.socketclient.SocketWriteThread;

import java.io.IOException;
import java.util.Random;

/**
 * Created by Victor on 2017/4/30.<br>
 *<br>
 * This is a class to manage the data transferred between different computers.
 * It manages the two threads, one for reading messages, one for writing to the remote.
 * It needs a HandleReadingMessage to handle the messages received from the remote,
 * and it has a write() method to write messages to the remote.
 * In the end, it need to be closed.
 */
public class DataTransferPair implements IConnection{

    /**
     * Constructor.
     * It gets the ip, the port and a HandleReadingMessage and then will create two threads to get and send messages.
     * @param ip The ip address to connect to.
     * @param port The remote port.
     * @param handle Specify how to handle the messages from the remote.
     * @param role_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @throws IOException if connection failed.
     */
    public DataTransferPair(String ip, int port, HandleReadingMessage handle, String role_id, String extension_name)
            throws IOException {
        this.handle = handle;
        PAIR_HASH = (new Random()).nextInt();
        init(ip, port, role_id, extension_name);
    }

    /**
     * Write messages to the remote.
     * @param msg An instance of Message. It contains the message to be written.
     */
    @Override
    public boolean write(Message msg) {
        return sWrite.setMessageToWrite(msg);
    }

    /**
     * Close the connection, or, the pair of sockets.
     */
    @Override
    public void close() {
        sRead.close();
        sWrite.close();
    }

    /**
     * Initialization of the two threads.
     * @throws IOException if creating sockets failed.
     */
    private void init(String ip, int port, String role_id, String extension_name) throws IOException {
        sRead = new SocketReadThread(ip, port, handle, role_id, extension_name, PAIR_HASH);
        Thread sReadThread = new Thread(sRead);
        sWrite = new SocketWriteThread(ip, port, role_id, extension_name, PAIR_HASH);
        Thread sWriteThread = new Thread(sWrite);
        sReadThread.start();
        sWriteThread.start();
    }

    private SocketReadThread sRead;
    private SocketWriteThread sWrite;
    final private HandleReadingMessage handle;
    final private int PAIR_HASH;
}
