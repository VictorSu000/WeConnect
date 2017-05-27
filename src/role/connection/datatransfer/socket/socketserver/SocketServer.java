package role.connection.datatransfer.socket.socketserver;

import me.Me;
import role.Role;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Created by Victor on 2017/4/30.<br>
 * <br>
 * This class specify the most fundamental elements of a socket for servers, including the basic methods,
 * connecting to the server and closing the socket.
 */
public class SocketServer implements Runnable{

    public SocketServer(Me me) {
        this.me = me;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Creating a server failed.");
            e.printStackTrace();
            // TODO: logger
        }
        while (running) {
            try {
                Socket socket = server.accept();
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder connectMsgStr = new StringBuilder();
                char c;
                while((c = (char)br.read()) != '@') {
                    connectMsgStr.append(c);
                }
                System.out.println(connectMsgStr);
                StringTokenizer strToken = new StringTokenizer(connectMsgStr.toString(), "|");
                final String ROLE_ID = strToken.nextToken();
                final String EXTENSION_NAME = strToken.nextToken();
                final int PAIR_HASH = Integer.parseInt(strToken.nextToken());
                final int SOCKET_TYPE = Integer.parseInt(strToken.nextToken());
                if (me.getRoleMap().containsKey(ROLE_ID)) {
                    // if the role exists
                    Role role = me.getRoleMap().get(ROLE_ID);
                    if (role.hasExtension(EXTENSION_NAME)) {
                        // if the role in THIS computer has the extension
                        role.addSocketToExtension(EXTENSION_NAME, socket, SOCKET_TYPE, PAIR_HASH);
                        // TODO: is it right to run extension here ?
                        role.runExtension(EXTENSION_NAME);
                    } else {
                        // TODO: use GUI to notice the user that the extension doesn't exist.
                        System.out.println("The extension " + EXTENSION_NAME + "has not been installed!");
                        br.close();
                        socket.close();
                    }
                } else {
                    me.createRemoteUserRole(socket, SOCKET_TYPE, PAIR_HASH, EXTENSION_NAME, ROLE_ID);
                }
                final int CONNECT_OK = 1;
                OutputStreamWriter w = new OutputStreamWriter(socket.getOutputStream());
                w.write(CONNECT_OK);
                w.flush();
            } catch (IOException e) {
                if (running) {
                    System.out.println("Accepting a socket failed.");
                    e.printStackTrace();
                    // TODO: logger
                }
            }
        }
    }

    public void close() {
        running = false;
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                System.out.println("Closing the server for socket failed.");
                e.printStackTrace();
                // TODO: logger
            }
        }
    }

    // use PORT 5205 to listen for socket connections
    public final static int PORT = 5205;
    private ServerSocket server = null;
    private Me me;
    volatile private boolean running = true;
}
