package role.connection.datatransfer.socket.socketclient;

import java.io.IOException;
import java.io.InputStream;

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
     */
    SocketClientWrite(String ip, int port) {
        super(ip, port, "write");
    }
}
