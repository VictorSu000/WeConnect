package role.extension;

import role.connection.Connection;
import role.connection.HandleReadingMessage;
import role.connection.IConnection;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 2017/5/25.<br>
 * <br>
 * Each extension starts as a separate thread and has a method close() to close all the related resources.
 * In client each role will have an instance of all the extension,
 * but only when the function of the extension is to be used will the extension thread be really started.
 * So it's strongly recommended that the extension has a simple constructor, that is, just save some parameters,
 * and the complicated part (like generating GUI) is made in run() method to save computer resources.
 * Additionally, at last the client will call method close() of all the extension.<br>
 * As for the connections, there are two ways to create connections. One is creating by THIS computer as a client,
 * connecting via ip and port. The other is creating as a server, connecting by the sockets which already exist.
 * All the connections should be created in the coincident way.<br>
 * <br>
 * Notes for making an extension: <br>
 * The extension should extend Extension and implement HandleReadingMessage, have two constructors (see Chat.java) and a run() method.
 * In run() method createConnections() should be called first and close() should be called at last.
 * If the extension created some other resources, it needs to create a new close() method to close those resources and 
 * call super.close() in the end.
 */
abstract public class Extension implements Runnable, HandleReadingMessage {
    
    @Override
    abstract public void run();

    /**
     * Close connections.
     */
    public void close() {
        if (connectionMap != null) {
            for (Map.Entry<Integer, IConnection> entry : connectionMap.entrySet()) {
                entry.getValue().close();
            }
            connectionMap = null;
        }
    }

    /**
     * Constructor. Mainly use ip and port.
     * @param ip The remote IP address.
     * @param port The remote port.
     * @param me_id The client id to identify THIS client. If logging in, the id is the user_id, or it's the ip address.
     * @param extension_name The name of the extension.
     */
    public Extension(String ip, int port, String me_id, String extension_name) {
        IP = ip;
        this.port = port;
        ME_ID = me_id;
        EXTENSION_NAME = extension_name;
    }

    /**
     * Constructor. Mainly use the already existing socket. Creating Connection here because the socket has already been established.
     * @param socket The existing socket.
     * @param socket_type The type of the socket.
     * @param pair_hash The hash of the socket pair which the socket is in.
     * @param me_id The client id to identify THIS client. If logging in, the id is the user_id, or it's the ip address.
     * @param extension_name The name of the extension.
     */
    public Extension(Socket socket, int socket_type, int pair_hash, String me_id, String extension_name) {
        ME_ID = me_id;
        IP = socket.getInetAddress().getHostAddress();
        port = socket.getPort();
        EXTENSION_NAME = extension_name;
        connectionMap = new HashMap<>();
        connectionMap.put(pair_hash, new Connection(socket, socket_type, me_id, EXTENSION_NAME, pair_hash, this));
    }

    /**
     * Add a socket to the connection.
     * Notes: The connection can contain and must contain two sockets. One is SocketType.READ, the other is SocketType.Write.
     * @param socket The socket to be added.
     * @param socket_type The type of the socket.
     * @param pair_hash The hash to identify the target connection.
     */
    public void addSocket(Socket socket, int socket_type, int pair_hash) {
        if (connectionMap != null) {
            // reset port, because if this extension was setup using ip and port,
            // but later the remote client connect to THIS computer, the port will be wrong.
            port = socket.getPort();
            connectionMap.get(pair_hash).addSocket(socket, socket_type);
        }
    }

    /**
     * Create connections from mainly ip and port, if the connections have not been established yet.
     */
    protected void createConnections(int number) {
        if (connectionMap == null || connectionMap.isEmpty()) {
            connectionMap = new HashMap<>();
            for (int i = 0; i < number; ++i) {
                IConnection connection = new Connection(IP, port, ME_ID, EXTENSION_NAME, this);
                int pair_hash = connection.getPAIR_HASH();
                connectionMap.put(pair_hash, connection);
            }
        }
    }

//    protected IConnection connection = null;
//    protected int pair_hash;
    protected Map<Integer, IConnection> connectionMap = null;
    protected final String IP;
    protected int port;
    protected final String ME_ID;

    final private String EXTENSION_NAME;
}
