package role.connection;

import role.Message;
import role.connection.datatransfer.DataTransferPair;
import role.connection.datatransfer.socket.socketclient.HandleReadingMessage;

import java.io.IOException;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * This is a virtual proxy for DataTransferPair.
 * Creating a Connection means there will be a DataTransferPair,
 * but only when the DataTransferPair is needed will it be really created.
 */
public class Connection implements IConnection{

    /**
     * Constructor for create a connection.
     * @param ip The ip address to connect to.
     * @param port The remote port.
     * @param handle Specify how to handle the messages from the remote.
     */
    public Connection(String ip, int port, HandleReadingMessage handle) {
        IP = ip;
        PORT = port;
        this.handle = handle;
    }

    public void write(Message msg) {
        if (dataTransferPair == null) {
            try {
                dataTransferPair = new DataTransferPair(IP, PORT, handle);
            } catch (IOException e) {
                System.out.println("Cannot create DataTransferPair");
                e.printStackTrace();
                // TODO: fix exception. throw it.
            }
        }
        dataTransferPair.write(msg);
    }

    public void close() {
        if (dataTransferPair != null) {
            dataTransferPair.close();
        }
    }

    final private String IP;
    final private int PORT;
    private HandleReadingMessage handle;
    private DataTransferPair dataTransferPair = null;
}
