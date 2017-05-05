package role.connection;

import role.Message;
import role.connection.datatransfer.DataTransferPair;

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

    /**
     * Write messages to the remote.
     * If the real connection has not been set up, this work will be done before writing messages.
     * @param msg An instance of Message. It contains the message to be written.
     * @throws IOException if setting up real connection failed.
     */
    @Override
    public boolean write(Message msg) throws IOException{
        if (dataTransferPair == null) {
            dataTransferPair = new DataTransferPair(IP, PORT, handle);
        }
        return dataTransferPair.write(msg);
    }

    /**
     * Closing the connection.
     */
    @Override
    public void close() {
        if (dataTransferPair != null) {
            dataTransferPair.close();
        }
    }

    final private String IP;
    final private int PORT;
    final private HandleReadingMessage handle;
    private DataTransferPair dataTransferPair = null;
}
