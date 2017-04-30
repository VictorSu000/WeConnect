package user.datatransfer.socket.socketclient;

import java.io.IOException;

/**
 * Created by Victor on 2017/4/30.<br>
 * <br>
 * This class is a socket class for client, but it can only read message.
 */
public class SocketClientRead extends SocketClient{

    /**
     * Constructor.
     *
     * @param ip   The ip address to be connected.
     * @param port The port to be connected.
     */
    public SocketClientRead(String ip, int port) {
        super(ip, port);
    }

    /**
     * Read from the stream.
     * @return an integer converted from one byte.
     * @throws IOException if error happens
     */
    public int read() throws IOException{
        return inStream.read();
    }
}
