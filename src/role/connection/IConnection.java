package role.connection;

import role.Message;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Victor on 2017/5/5.<br>
 * <br>
 * The interface for a connection.
 * Complementing this interface means it can write messages and (should be able to) read messages with HandleReadingMessage.
 * Besides, it can get DataTransferPair's PAIR_HASH and add socket to the connection.
 */
public interface IConnection {
    boolean write(Message msg) throws IOException;
    void close();
    int getPAIR_HASH();
    void addSocket(Socket socket, int socket_type);
}
