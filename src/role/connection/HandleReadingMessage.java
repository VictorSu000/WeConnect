package role.connection;

import role.Message;

/**
 * Created by Victor on 2017/4/30. <br>
 * <br>
 * Complementing this interface means it can handle the messages read from the sockets.
 * Note: Inside handleMsg(), no need to call msg.dispose().
 */
public interface HandleReadingMessage {
    void handleMsg(Message msg);
}
