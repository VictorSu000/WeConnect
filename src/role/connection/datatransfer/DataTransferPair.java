package role.connection.datatransfer;

import role.Message;
import role.connection.IConnection;
import role.connection.HandleReadingMessage;
import role.connection.datatransfer.socket.socketclient.SocketReadThread;
import role.connection.datatransfer.socket.socketclient.SocketType;
import role.connection.datatransfer.socket.socketclient.SocketWriteThread;

import java.io.IOException;
import java.net.Socket;
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
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @throws IOException if connection failed.
     */
    public DataTransferPair(String ip, int port, String me_id, String extension_name, HandleReadingMessage handle)
            throws IOException {
        this.handle = handle;
        PAIR_HASH = (new Random()).nextInt();
        EXTENSION_NAME = extension_name;
        ME_ID = me_id;
        init(ip, port);
    }

    /**
     * Constructor. Construct a DataTransferPair with pair_hash.
     * It gets the ip, the port and a HandleReadingMessage and then will create two threads to get and send messages.
     * @param ip The ip address to connect to.
     * @param port The remote port.
     * @param pair_hash The hash to identify the socket pair from others.
     * @param handle Specify how to handle the messages from the remote.
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @throws IOException if connection failed.
     */
    public DataTransferPair(String ip, int port, String me_id, String extension_name, int pair_hash, HandleReadingMessage handle)
            throws IOException {
        this.handle = handle;
        PAIR_HASH = pair_hash;
        EXTENSION_NAME = extension_name;
        ME_ID = me_id;
        init(ip, port);
    }

    /**
     * Constructor. Construct a DataTransferPair with pair_hash and without socketRead or socketWrite.
     * Those should be added later. Usually this constructor is related to a SocketServer.
     * Because the sockets are created by the remote clients and they are to be added to the DataTransferPair.
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @param pair_hash The hash number to identify different transferring pairs.
     * @param handle Specify how to handle the messages from the remote.
     */
    public DataTransferPair(String me_id, String extension_name, int pair_hash, HandleReadingMessage handle) {
        this.handle = handle;
        EXTENSION_NAME = extension_name;
        PAIR_HASH = pair_hash;
        ME_ID = me_id;
    }

    /**
     * Add SocketReadThread or SocketWriteThread.
     * @param socket The socket to be added.
     * @param socket_type The type of the socket, SocketType.READ or SocketType.WRITE
     */
    public void addSocket(Socket socket, int socket_type) {
        // if socket_type == WRITE, create a SocketReadThread (Because the "WRITE" is for the remote.)
        if (socket_type == SocketType.WRITE) {
            sRead = new SocketReadThread(socket, handle, ME_ID, EXTENSION_NAME, PAIR_HASH);
            Thread sReadThread = new Thread(sRead);
            sReadThread.start();
        } else {
            sWrite = new SocketWriteThread(socket, ME_ID, EXTENSION_NAME, PAIR_HASH);
            Thread sWriteThread = new Thread(sWrite);
            sWriteThread.start();
            // TODO: what if only sRead was added successfully
        }
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
     * Get PAIR_HASH.
     * @return PAIR_HASH
     */
    public int getPAIR_HASH() {
        return  PAIR_HASH;
    }

    /**
     * Initialization of the two threads.
     * @throws IOException if creating sockets failed.
     */
    private void init(String ip, int port) throws IOException {
        sRead = new SocketReadThread(ip, port, handle, ME_ID, EXTENSION_NAME, PAIR_HASH);
        Thread sReadThread = new Thread(sRead);
        sWrite = new SocketWriteThread(ip, port, ME_ID, EXTENSION_NAME, PAIR_HASH);
        Thread sWriteThread = new Thread(sWrite);
        sReadThread.start();
        sWriteThread.start();
    }

    private SocketReadThread sRead;
    private SocketWriteThread sWrite;
    final private HandleReadingMessage handle;
    final private int PAIR_HASH;
    final private String EXTENSION_NAME;
    final private String ME_ID;
}
