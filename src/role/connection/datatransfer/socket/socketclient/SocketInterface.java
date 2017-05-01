package role.connection.datatransfer.socket.socketclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * Implement this interface means it's a socket which can be read to receive data.
 */
interface SocketReadable {
    PipedOutputStream read() throws IOException;
    void close() throws IOException;
    boolean connect() throws IOException;
}

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * Implement this interface means it's a socket which can be write to send data.
 */
interface SocketWritable {
    void write(InputStream in) throws IOException;
    void close() throws IOException;
    boolean connect() throws IOException;
}

