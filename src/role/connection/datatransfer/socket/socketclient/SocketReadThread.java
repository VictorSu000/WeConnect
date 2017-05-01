package role.connection.datatransfer.socket.socketclient;

import role.Message;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
     * @throws IOException if Socket can't be established or socket is established but transferring data failed.
     */
    public SocketReadThread(String ip, int port, HandleReadingMessage handle) throws IOException{
        socketRead = new SocketClientRead(ip, port);
        if (!socketRead.connect()) {
            socketRead.close();
            throw new IOException("Socket established but transferring data failed.");
        }
        this.handle = handle;
        running = true;
    }

    /**
     * This method is calling in multithreading.
     * In this method, this thread continuously calls the read() method from SocketReadable,
     * fetches a stream of a complete piece of message, pack it as an instance of class Message,
     * and then give it to HandleReadingMessage to handle it.
     */
    @Override
    public void run() {
        while (running) {
            try(PipedOutputStream socketOut = socketRead.read(); PipedInputStream in = new PipedInputStream(socketOut)) {
                char c;
                StringBuilder lengthStr = new StringBuilder();
                while((c = (char) in.read()) != '-') {
                    lengthStr.append(c);
                }
                handle.handleMsg(new Message(new BigInteger(lengthStr.toString()), in));
            } catch (IOException e) {
                System.out.println("Unexpected Exception happens when running thread to receive data.");
                System.out.println("Error message: " + e.getMessage());
                // TODO: use logger.
            }
        }

    }

    /**
     * In this method close the socket and this thread.
     */
    public void close() {
        running = false;
        try {
            socketRead.close();
        } catch (IOException e) {
            System.out.println("Unexpected Exception happens when closing a socket for reading");
            System.out.println("Error message: " + e.getMessage());
            // TODO: use logger.
        }
    }

    private final SocketReadable socketRead;
    private HandleReadingMessage handle;
    private boolean running = true;

}
