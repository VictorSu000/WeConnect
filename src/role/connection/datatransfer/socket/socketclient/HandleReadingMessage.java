package role.connection.datatransfer.socket.socketclient;

import role.InputMessage;

/**
 * Created by Victor on 2017/4/30. <br>
 * <br>
 * Complementing this interface means it can handle the messages read from the sockets.
 */
public interface HandleReadingMessage {
    void handleMsg(InputMessage msg);
}
