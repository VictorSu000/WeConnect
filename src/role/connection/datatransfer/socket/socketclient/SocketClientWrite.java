package role.connection.datatransfer.socket.socketclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * This class is a basic socket class for client, but it can only write message to the remote.
 */
class SocketClientWrite extends SocketClient implements SocketWritable {

    /**
     * Send all the data in the InputStream to the remote.
     * If client want to add any extra messages like the length, those should be added previously.
     * @param in The Stream that stores all the data to be sent
     * @throws IOException  if this input stream has been closed, or an I/O error occurs.
     */
    @Override
    public void write(InputStream in) throws IOException {
        int d;
        while ((d = in.read()) != -1) {
            outStream.write(d);
        }
        outStream.flush();
    }

    /**
     * Close this socket and the related streams.
     * @throws IOException if closing streams and socket failed.
     */
    @Override
    public void close() throws IOException {
        super.close();
    }

    /**
     * Connect to the server and try to check whether the connection is successfully established.
     * @return true for success. false for failure.
     * @throws IOException if an I/O error occurs when creating the socket or creating the streams.
     */
    @Override
    public boolean connect() throws IOException {
        return super.connect();
    }

    /**
     * Constructor.
     *
     * @param ip   The ip address to be connected.
     * @param port The port to be connected.
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @param pair_hash The hash id of socket pair. Use pair_hash to identify it from other pairs of sockets.
     */
    SocketClientWrite(String ip, int port, String me_id, String extension_name, int pair_hash) {
        super(ip, port, me_id, SocketType.WRITE, extension_name, pair_hash);
    }

    /**
     * Constructor.
     * @param socket The existing socket.
     * @param me_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @param pair_hash The hash id of socket pair. Use pair_hash to identify it from other pairs of sockets.
     */
    SocketClientWrite(Socket socket, String me_id, String extension_name, int pair_hash) {
        super(socket, me_id, SocketType.WRITE, extension_name, pair_hash);
    }
}
