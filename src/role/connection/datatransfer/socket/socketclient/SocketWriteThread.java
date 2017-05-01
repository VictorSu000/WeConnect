package role.connection.datatransfer.socket.socketclient;

import role.Message;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * This class is aim to send data to the socket with multithreading.
 * And it manage the creation and the disposal of that socket and the InputStream.
 * So client just need to give an instance of Message and a starting signal,
 * and then it will finish the other work.
 */
public class SocketWriteThread implements Runnable {

    /**
     * Constructor. The instance of this class will automatically manage the socket.
     * @param ip The ip address to be connected.
     * @param port The port to be connected.
     * @throws IOException if Socket can't be established or socket is established but transferring data failed.
     */
    public SocketWriteThread(String ip, int port) throws IOException{
        socketWrite = new SocketClientWrite(ip, port);
        if (!socketWrite.connect()) {
            socketWrite.close();
            throw new IOException("Socket established but transferring data failed.");
        }
        running = true;
        beginWriting = false;
    }

    /**
     * Set the message with data that is going to be written to the remote.
     * @param messageToWrite the message to be sent
     * @return true means everything's ok, false means there's already a message being sent, please wait till it's finished.
     */
    public boolean setMessageToWrite(Message messageToWrite) {
        if (this.messageToWrite == null) {
            this.messageToWrite = messageToWrite;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calling this method means the sending should be started.
     * @return true means everything's ok, false means there's already a message being sent, please wait till it's finished.
     */
    public boolean beginToWrite() {
        if (!beginWriting) {
            beginWriting = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Write to the remote according to the beginToWrite() command and an InputStream consisting of all data.
     * Afterwards, this method will dispose of messageToWrite.
     */
    @Override
    public void run() {
        // use running to make sure that the thread will survive
        while (running) {
            // use beginWriting to control the time to write message to the remote
            while (beginWriting) {
                write();
                messageToWrite.dispose();
                beginWriting = false;
                messageToWrite = null;
            }
        }

    }

    /**
     * In this method close the socket and this thread.
     */
    public void close() {
        running = false;
        try {
            socketWrite.close();
        } catch (IOException e) {
            System.out.println("Unexpected Exception happens when closing a socket for reading");
            System.out.println("Error message: " + e.getMessage());
            // TODO: use logger.
        }
    }

    /**
     * This method changes the message to a stream and adds the length message at the same time.
     * Then it sends those data.
     */
    private void write() {
        try(PipedOutputStream out = new PipedOutputStream(); PipedInputStream in = new PipedInputStream(out)) {
            String lengthStr = messageToWrite.getLength().toString();
            for (int i = 0; i < lengthStr.length(); ++i) {
                out.write(lengthStr.charAt(i));
            }
            // write the separator
            out.write('-');
            int b;
            while ((b = messageToWrite.getInStream().read()) != -1) {
                out.write(b);
            }
            socketWrite.write(in);
        } catch (IOException e) {
            System.out.println("Unexpected Exception happens when trying to append length data to the stream.");
            System.out.println("Error message: " + e.getMessage());
            // TODO: use logger.
        }
    }

    private final SocketWritable socketWrite;
    private boolean running = true;
    private boolean beginWriting = false;
    private Message messageToWrite;
}
