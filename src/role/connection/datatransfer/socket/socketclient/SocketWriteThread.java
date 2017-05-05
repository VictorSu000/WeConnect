package role.connection.datatransfer.socket.socketclient;

import role.Message;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * This class is aim to send data to the socket with multithreading.
 * And it manage the creation and the disposal of that socket and the message.
 * So client just need to give an instance of Message and a starting signal,
 * and then it will finish the other work. Afterwards, that message will be disposed of correctly.
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
            throw new IOException("Socket establishing failed.");
        }
        running = true;
    }

    /**
     * Set the message with data that is going to be written to the remote.
     * Attention: One message should only be given to one thread. If you want to send another message, please new one.
     * And also, please use new stream in the message.
     * @param messageToWrite the message to be sent
     * @return true if successfully add the message to message queue, false if fail to do so.
     */
    synchronized public boolean setMessageToWrite(Message messageToWrite) {
        return messageToWriteQueue.offer(messageToWrite);
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
            boolean msgQueueEmpty;
            synchronized (this) {
                msgQueueEmpty = messageToWriteQueue.isEmpty();
            }
            // use while to make sure before checking stopSignal, all the messages have been sent.
            while (!msgQueueEmpty) {
                Message msg;
                synchronized (this){
                    msg = messageToWriteQueue.poll();
                }
                write(msg);
                try {
                    msg.dispose();
                } catch (IOException e) {
                    System.out.println("Disposing message failed. An I/O error happened.");
                    e.printStackTrace();
                }
                synchronized (this) {
                    msgQueueEmpty = messageToWriteQueue.isEmpty();
                }
            }
            if (stopSignal) {
                running = false;
                try {
                    socketWrite.close();
                } catch (IOException e) {
                    System.out.println("Unexpected Exception happens when closing a socket for reading");
                    e.printStackTrace();
                    // TODO: use logger.
                }
            }
        }

    }

    /**
     * In this method close the socket and this thread.
     */
    synchronized public void close() {
        // Use stop signal here and don't do any real closing work to avoid waiting in the thread which tries to stop
        // another thread. And also, in this way real stopping will be delayed till everything is finished.
        // Real stopping work is done in THIS thread itself (SocketWriteThread) in run() method.
        stopSignal = true;
    }

    /**
     * This method changes the message to a stream and adds the length message at the same time.
     * Then it sends those data.
     */
    private void write(Message msg) {
        if (msg.getInStream() == null) {
            System.out.println("Error: message is null! Maybe this message was given to different threads at the same time.");
            return;
        }
        try(PipedOutputStream out = new PipedOutputStream(); PipedInputStream in = new PipedInputStream(out)) {
            String lengthStr = msg.getLength().toString();
            for (int i = 0; i < lengthStr.length(); ++i) {
                out.write(lengthStr.charAt(i));
            }
            // write the separator
            out.write('-');
            int b;
            BigInteger length = msg.getLength();
            // don't use (b = messageToWrite.getInStream().read())!=-1 here.
            // if client use "try" to create a piped stream and doesn't close it manually,
            // messageToWrite.getInStream().read() won't receive -1.
            for (BigInteger i = new BigInteger("0"); i.compareTo(length) < 0; i = i.add(new BigInteger("1"))) {
                b = msg.getInStream().read();
                out.write(b);
            }
            out.flush();
            // Close "out" to make sure stream "in" can receive an ending and in.read()!=-1 can be used.
            try {
                out.close();
            } catch (IOException e) {
                System.out.println("Closing temp PipedOutputSteam failed in writing data to the remote.");
                e.printStackTrace();
            }
            socketWrite.write(in);
        } catch (IOException e) {
            System.out.println("Unexpected Exception happens when trying to append length data to the stream.");
            e.printStackTrace();
            // TODO: use logger.
        }
    }

    final private SocketWritable socketWrite;
    volatile private boolean running = true;
    final private Queue<Message> messageToWriteQueue = new LinkedList<>();
    volatile private boolean stopSignal = false;
}
