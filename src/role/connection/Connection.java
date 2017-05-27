package role.connection;

import role.Message;
import role.connection.datatransfer.DataTransferPair;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * This is a virtual proxy for DataTransferPair.
 * Creating a Connection means there will be a DataTransferPair,
 * but only when the DataTransferPair is needed will it be really created.
 */
public class Connection implements IConnection{

    /**
     * Constructor for create a connection.
     * @param ip The ip address to connect to.
     * @param port The remote port.
     * @param handle Specify how to handle the messages from the remote.
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     */
    public Connection(String ip, int port, String me_id, String extension_name, HandleReadingMessage handle) {
        IP = ip;
        PORT = port;
        ME_ID = me_id;
        EXTENSION_NAME = extension_name;
        this.handle = handle;
        PAIR_HASH = (new Random()).nextInt();
    }

    /**
     * Constructor. Construct a Connection with given pair_hash and a socket.
     * Because the socket has already been established, now the virtual proxy is not necessary.
     * @param socket The existing socket.
     * @param socket_type The type of the socket, SocketType.READ or SocketType.WRITE
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @param pair_hash The hash number to identify different transferring pairs.
     * @param handle Specify how to handle the messages from the remote.
     */
    public Connection(Socket socket, int socket_type, String me_id, String extension_name, int pair_hash, HandleReadingMessage handle) {
        IP = socket.getInetAddress().getHostAddress();
        PORT = socket.getPort();
        ME_ID = me_id;
        EXTENSION_NAME = extension_name;
        PAIR_HASH = pair_hash;
        this.handle = handle;
        dataTransferPair = new DataTransferPair(ME_ID, EXTENSION_NAME, PAIR_HASH, handle);
        addSocket(socket, socket_type);
    }

    /**
     * Add a socket to the connection.
     * @param socket The socket to be added.
     * @param socket_type The type of the socket, SocketType.READ or SocketType.WRITE
     */
    public void addSocket(Socket socket, int socket_type) {
        dataTransferPair.addSocket(socket, socket_type);
    }

    /**
     * Write messages to the remote.
     * If the real connection has not been set up, this work will be done before writing messages.
     * @param msg An instance of Message. It contains the message to be written.
     * @throws IOException if setting up real connection failed.
     */
    @Override
    public boolean write(Message msg) throws IOException{
        if (dataTransferPair == null) {
            dataTransferPair = new DataTransferPair(IP, PORT, ME_ID, EXTENSION_NAME, PAIR_HASH, handle);
        }
        return dataTransferPair.write(msg);
    }

    /**
     * Closing the connection.
     */
    @Override
    public void close() {
        if (dataTransferPair != null) {
            dataTransferPair.close();
        }
    }

    /**
     * Get DataTransferPair's PAIR_HASH. (Also the connection's PAIR_HASH)
     * @return DataTransferPair's PAIR_HASH.
     */
    public int getPAIR_HASH() {
        return PAIR_HASH;
    }

    final private String IP;
    final private int PORT;
    final private String ME_ID;
    final private String EXTENSION_NAME;
    final private HandleReadingMessage handle;
    final private int PAIR_HASH;
    private DataTransferPair dataTransferPair = null;
}
