package user.datatransfer.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Victor on 2017/4/30.
 *
 * This is a abstract class for sockets in client.
 * This class specify the most fundamental elements of a socket for clients, including the basic methods,
 * connecting to the server and how to dispose the socket if it's no longer to be used.
 */
abstract class SocketClient {

    /**
     * Constructor.
     * @param ip The ip address to be connected.
     * @param port The port to be connected.
     */
    public SocketClient(String ip, int port) {
        this.IP = ip;
        this.PORT = port;
    }

    /**
     * This method is used to connect to the server. After creating a socket and the streams for reading and writing,
     * it will call a private method checkConnection() to make sure the connection is established.
     * @return true for successfully establishing connection . false for failure.
     * @throws IOException if connection error happens.
     */
    public boolean connect() throws IOException {
        socket = new Socket(IP, PORT);
        inStream = new BufferedInputStream(socket.getInputStream());
        outStream = new BufferedOutputStream(socket.getOutputStream());
        return checkConnection();
    }

    /**
     * This method should be called if the socket is not to be used anymore.
     * @throws IOException if closing streams and socket failed.
     */
    public void dispose() throws IOException {
        inStream.close();
        outStream.close();
        socket.close();
        inStream = null;
        outStream = null;
        socket = null;
    }

    /**
     * Check if connection is established.
     * @return true means success, false means failure.
     * @throws IOException if socket established but cannot transfer messages.
     */
    private boolean checkConnection() throws IOException {
        final int CONNECT_OK = 1;
        outStream.write("Connecting.".getBytes());
        outStream.flush();
        return inStream.read() == CONNECT_OK;
    }

    private final String IP;
    private final int PORT;
    private Socket socket;
    private BufferedInputStream inStream = null;
    private BufferedOutputStream outStream = null;
}
