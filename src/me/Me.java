package me;

import role.RemoteUserRole;
import role.Role;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Victor on 2017/5/6.<br>
 * <br>
 */
public class Me {

    public Me() {
        try {
            ID = getIPAddress();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Role> getRoleMap() {
        return roleMap;
    }

    public Role getRole(String role_id) {
        return roleMap.get(role_id);
    }

    public void createRemoteUserRole(String remote_ip, int remote_port, String role_id) {
        try {
            roleMap.put(role_id, new RemoteUserRole(remote_ip, remote_port, ID));
        } catch (InvocationTargetException | IllegalAccessException | IOException | InstantiationException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createRemoteUserRole(Socket socket, int socket_type, int pair_hash, String socket_extension_name, String role_id) {
        try {
            roleMap.put(role_id, new RemoteUserRole(socket, socket_type, pair_hash, socket_extension_name, ID));
        } catch (IOException | InstantiationException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException | IllegalAccessException e) {
            // TODO: handle all the exceptions
            e.printStackTrace();
        }
    }

    private String getIPAddress() throws SocketException {
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            // System.out.println(netInterface.getName());
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address)
                {
                    return ip.getHostAddress();
                }
            }
        }
        return "unknown IP address";
    }

    private String ID;
    private Map<String, Role> roleMap = new HashMap<>();
}
