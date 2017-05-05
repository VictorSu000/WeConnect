package role.connection.datatransfer.socket.socketclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Victor on 2017/4/30.<br>
 *<br>
 * This is a abstract class for sockets in client.<br>
 * This class specify the most fundamental elements of a socket for clients, including the basic methods,
 * connecting to the server and how to dispose the socket if it's no longer to be used.
 */
abstract class SocketClient {

    /**
     * This method is used to connect to the server. After creating a socket and the streams for reading and writing,
     * it will call a private method checkConnection() to make sure the connection is established.
     * @return true for successfully establishing connection . false for failure.
     * @throws IOException if an I/O error occurs when creating the socket or creating the streams.
     */
    boolean connect() throws IOException {
        socket = new Socket(IP, PORT);
        inStream = new BufferedInputStream(socket.getInputStream());
        outStream = new BufferedOutputStream(socket.getOutputStream());
        return checkConnection();
    }

    /**
     * This method should be called if the socket is not to be used anymore.
     * @throws IOException if closing streams and socket failed.
     */
    void close() throws IOException {
        if (inStream != null) {
            inStream.close();
        }
        if (outStream != null) {
            outStream.close();
        }
        if (socket != null) {
            socket.close();
        }
        inStream = null;
        outStream = null;
        socket = null;
    }

    BufferedInputStream inStream = null;
    BufferedOutputStream outStream = null;

    /**
     * Constructor.
     * @param ip The ip address to be connected.
     * @param port The port to be connected.
     * @param role_id The id of the current client.
     * @param socketType The socketType ("read" or "write")
     * @param extension_name The name of the extension which created this socket.
     * @param pair_hash The hash id of socket pair. Use pair_hash to identify it from other pairs of sockets.
     */
    SocketClient(String ip, int port, String role_id, String socketType, String extension_name, int pair_hash) {
        IP = ip;
        PORT = port;
        ROLE_ID = role_id;
        SOCKET_TYPE = socketType;
        EXTENSION_NAME = extension_name;
        PAIR_HASH = pair_hash;
    }

    /**
     * Check if connection is established. And send some sockets related messages.
     * @return true means success, false means failure.
     * @throws IOException if socket established but cannot transfer messages.
     */
    private boolean checkConnection() throws IOException {
        final int CONNECT_OK = 1;
        outStream.write((ROLE_ID + "|" + EXTENSION_NAME + "|" + PAIR_HASH + "|" + SOCKET_TYPE).getBytes());
        outStream.flush();
        return inStream.read() == CONNECT_OK;
    }

    final private String IP;
    final private int PORT;
    final private String ROLE_ID;
    final private String SOCKET_TYPE;
    final private String EXTENSION_NAME;
    final private int PAIR_HASH;
    private Socket socket;
}
