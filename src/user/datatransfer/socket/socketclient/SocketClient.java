package user.datatransfer.socket.socketclient;

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
     * @param socketType The socketType ("read" or "write")
     */
    SocketClient(String ip, int port, String socketType) {
        this.IP = ip;
        this.PORT = port;
        this.socketType = socketType;
    }

    /**
     * Check if connection is established.
     * @return true means success, false means failure.
     * @throws IOException if socket established but cannot transfer messages.
     */
    private boolean checkConnection() throws IOException {
        final int CONNECT_OK = 1;
        outStream.write(("Connecting." + socketType).getBytes());
        outStream.flush();
        return inStream.read() == CONNECT_OK;
    }

    private final String IP;
    private final int PORT;
    private Socket socket;
    private final String socketType;
}
