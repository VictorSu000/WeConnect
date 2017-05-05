package role.connection.datatransfer.socket.socketclient;

import role.Message;
import role.connection.HandleReadingMessage;

import java.io.IOException;
import java.io.PipedInputStream;
import java.math.BigInteger;

/**
 * Created by Victor on 2017/4/30.<br>
 * <br>
 * This class is aim to receive data from socket with multithreading.
 * And it manage the creation and the disposal of that socket, so client just need to create an instance of this class
 * and tell it what to do with the data received. Then everything will be done.
 */
public class SocketReadThread implements Runnable{

    /**
     * Constructor. The instance of this class will automatically manage the socket.
     * @param ip The ip address to be connected.
     * @param port The port to be connected.
     * @param handle Specify how to handle the message received.
     * @param role_id The id of the current client.
     * @param extension_name The name of the extension which created this socket.
     * @param pair_hash The hash id of socket pair. Use pair_hash to identify it from other pairs of sockets.
     * @throws IOException if Socket can't be established or socket is established but transferring data failed.
     */
    public SocketReadThread(String ip, int port, HandleReadingMessage handle,
                            String role_id, String extension_name, int pair_hash) throws IOException{
        socketRead = new SocketClientRead(ip, port, role_id, extension_name, pair_hash);
        if (!socketRead.connect()) {
            socketRead.close();
            throw new IOException("Socket establishing failed.");
        }
        this.handle = handle;
        running = true;
    }

    /**
     * This method is called in multithreading.
     * In this method, this thread continuously calls the read() method from SocketReadable,
     * fetches a stream of a complete piece of message, pack it as an instance of class Message,
     * and then give it to HandleReadingMessage to handle it.
     * Note: in HandleReadingMessage, the message doesn't need to be disposed of,
     * because this work has been done in this method.
     */
    @Override
    public void run() {
        while (running) {
            try(PipedInputStream socketIn = socketRead.read()) {
                char c;
                StringBuilder lengthStr = new StringBuilder();
                while((c = (char) socketIn.read()) != '-') {
                    lengthStr.append(c);
                }
                synchronized (handle) {
                    Message msg = new Message(new BigInteger(lengthStr.toString()), socketIn);
                    handle.handleMsg(msg);
                    msg.dispose();
                }
            } catch (IOException e) {
                if (running) {
                    System.out.println("Unexpected Exception happens when running thread to receive data.");
                    System.out.println("Maybe the socket is closed unexpectedly. Or creating or disposing of message failed.");
                    e.printStackTrace();
                    close();
                    // TODO: use logger.
                }
            }
        }

    }

    /**
     * In this method close the socket and this thread.
     * Attention here, if this thread is reading messages and close() is called at the same time,
     * socket will be closed and those messages can't be received correctly.
     */
    synchronized public void close() {
        // Must set running false before close socketRead, because run() method is still trying to fetch data and
        // an exception will be thrown if socketRead is closed. Only when running is false won't that exception be logged.
        running = false;
        try {
            socketRead.close();
        } catch (IOException e) {
            System.out.println("Unexpected Exception happens when closing a socket for reading");
            e.printStackTrace();
            // TODO: use logger.
        }
    }

    private final SocketReadable socketRead;
    private final HandleReadingMessage handle;
    volatile private boolean running = true;
}
