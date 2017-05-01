package user.datatransfer.socket.socketclient;

import java.io.IOException;
import java.io.PipedOutputStream;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 *
 */
interface SocketReadable{
    PipedOutputStream read() throws IOException;
}

