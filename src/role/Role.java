package role;

import role.extension.Extension;
import role.connection.datatransfer.socket.socketserver.SocketServer;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Victor on 2017/4/30.<br>
 *<br>
 * This is a class to describe the user to whom the client is chatting.
 */
abstract public class Role {
//    final static String extensionProPath = "../config/extensions.properties";
    private final static String extensionClassPath = "/role/extensions.properties";
    private final static String extensionPackage = "role.extension";
    private final String ME_ID;
    private Map<String, Extension> extensionMap = new HashMap<>();

    public void runExtension(String extensionName) {
        Thread extensionThread = new Thread(extensionMap.get(extensionName));
        extensionThread.start();
    }

    public void shutdownExtension(String extensionName) {
        extensionMap.get(extensionName).close();
    }

    public boolean hasExtension(String extensionName) {
        return extensionMap.containsKey(extensionName);
    }

    public void addSocketToExtension(String extensionName, Socket socket, int socket_type, int pair_hash) {
        extensionMap.get(extensionName).addSocket(socket, socket_type, pair_hash);
    }

    public Map<String, Extension> getExtensionMap() {
        return extensionMap;
    }

    Role(String ip, int port, String me_id)
            throws IOException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        ME_ID = me_id;
        Properties prop = new Properties();
        String extensionProPath = "";
        try {
            extensionProPath = Role.class.getResource(extensionClassPath).toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // TODO: deal with exception.
        }
        prop.load(new FileInputStream(extensionProPath));
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
            if (entry.getValue().equals("On")) {
                Class<?> extensionClass = Class.forName(extensionPackage + "." + entry.getKey());
                Constructor cs = extensionClass.getConstructor(String.class, int.class, String.class);
                extensionMap.put((String) entry.getKey(), (Extension) cs.newInstance(ip, port, ME_ID));
            }
        }
    }

    Role(Socket socket, int socket_type, int pair_hash, String socket_extension_name, String me_id)
            throws IOException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        final String IP = socket.getInetAddress().getHostAddress();
        ME_ID = me_id;
        Properties prop = new Properties();
        String extensionProPath = "";
        try {
            extensionProPath = Role.class.getResource(extensionClassPath).toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            // TODO: deal with exception.
        }
        prop.load(new FileInputStream(extensionProPath));
        for (Map.Entry<Object, Object> entry : prop.entrySet()) {
            // use equals() rather than == !
            if (entry.getValue().equals("On")) {
                if (entry.getKey().equals(socket_extension_name)) {
                    Class<?> extensionClass = Class.forName(extensionPackage + "." + entry.getKey());
                    Constructor cs = extensionClass.getConstructor(Socket.class, int.class, int.class, String.class);
                    extensionMap.put((String) entry.getKey(), (Extension) cs.newInstance(socket, socket_type, pair_hash, ME_ID));
                } else {
                    Class<?> extensionClass = Class.forName(extensionPackage + "." + entry.getKey());
                    Constructor cs = extensionClass.getConstructor(String.class, int.class, String.class);
                    extensionMap.put((String) entry.getKey(), (Extension) cs.newInstance(IP, SocketServer.PORT, ME_ID));
                }
            }
        }
    }
}
