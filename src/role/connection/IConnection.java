package role.connection;

import role.Message;

/**
 * Created by Victor on 2017/5/5.<br>
 * <br>
 * The interface for a connection.
 * Complementing this interface means it can write messages and (should be able to) read messages with HandleReadingMessage.
 */
public interface IConnection {
    void write(Message msg);
    void close();
}
