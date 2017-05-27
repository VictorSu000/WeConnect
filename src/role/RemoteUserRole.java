package role;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * Created by Victor on 2017/5/10.<br>
 * <br>
 */
public class RemoteUserRole extends Role {
    public RemoteUserRole(String ip, int port, String me_id)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(ip, port, me_id);
    }

    public RemoteUserRole(Socket socket, int socket_type, int pair_hash, String socket_extension_name, String me_id)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(socket, socket_type, pair_hash, socket_extension_name, me_id);
    }
}
