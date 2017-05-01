package role.connection.datatransfer.socket.socketclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.math.BigInteger;

/**
 * Created by Victor on 2017/4/30.<br>
 * <br>
 * This class is a basic socket class for client, but it can only read message.
 */
class SocketClientRead extends SocketClient implements SocketReadable{

    /**
     * An encapsulation of reading data from the socket. Firstly read the length of the message to be read.
     * Then get all the data according to the length, and put them into a piped stream. Afterwards return the stream.<br>
     * Each time calling this method, a piece of message will be completely received and saved in a piped stream.
     * So if transferring huge amounts of data at a time, for example, transferring a large file,
     * a lot of computer resources will be cost.
     * @return an integer converted from one byte.
     * @throws IOException  if this input stream has been closed, or an I/O error occurs.
     */
    public PipedOutputStream read() throws IOException{
        StringBuilder lengthStr;
        try(InputStreamReader lengthReader = new InputStreamReader(inStream)) {
            lengthStr = new StringBuilder();
            // Read the length of the message. The length is transferred in the beginning of the message as string,
            // and it ends with the char '-'
            char c;
            while ((c = (char) lengthReader.read()) != '-') {
                lengthStr.append(c);
            }
        }
        BigInteger length = new BigInteger(String.valueOf(lengthStr));
        PipedOutputStream returnStream = new PipedOutputStream();
        for (BigInteger i = new BigInteger(0 + ""); i.compareTo(length) < 0; i = i.add(new BigInteger(1 + ""))) {
            returnStream.write(inStream.read());
        }
        return returnStream;
    }

    /**
     * Close this socket and the related streams.
     * @throws IOException if closing streams and socket failed.
     */
    @Override
    public void close() throws IOException {
        super.close();
    }

    /**
     * Connect to the server and try to check whether the connection is successfully established.
     * @return true for success. false for failure.
     * @throws IOException if an I/O error occurs when creating the socket or creating the streams.
     */
    public boolean connect() throws IOException {
        return super.connect();
    }

    /**
     * Constructor.
     *
     * @param ip   The ip address to be connected.
     * @param port The port to be connected.
     */
    SocketClientRead(String ip, int port) {
        super(ip, port, "read");
    }
}
