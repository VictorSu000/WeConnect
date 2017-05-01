package role;

import role.connection.Connection;

/**
 * Created by Victor on 2017/4/30.<br>
 *<br>
 * This is a class to describe the user to whom the client is chatting.
 */
abstract class Role {
    private String nickName;
    private String ip;
    private int port;
    private Connection connection;
}
