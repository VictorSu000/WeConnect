package user;

import user.datatransfer.DataTransfer;

/**
 * Created by Victor on 2017/4/30.<br>
 *<br>
 * This is a class to describe the user to whom the client is chatting.
 */
abstract class User {
    private String nickName;
    private String ip;
    private int port;
    private DataTransfer dataTransfer;
}
