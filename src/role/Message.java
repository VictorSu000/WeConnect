package role;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * A message represent data transferred at one time.
 * It includes the length of data, and a InputStream that consists of all the data
 */
public class Message {

    /**
     * Constructor. Use a BigInteger length and an InputStream to construct message.
     * Attention: One message should only be given to one thread. If you want to send another message, please new one.
     * And also, please use new stream in the message.
     * @param length The length of the data.
     * @param in The InputStream of all the data.
     * @throws IOException if the InputStream is unexpectedly closed or other I/O error happened.
     */
    public Message(BigInteger length, InputStream in) throws IOException {
        this.length = length;
        int b;
        PipedInputStream inStream = new PipedInputStream();
        // Get a copy of the parameter in and make a new stream for field inStream.
        // So that closing the InputStream "in" after calling the constructor won't affect the message.
        try (PipedOutputStream out = new PipedOutputStream()) {
            inStream.connect(out);
            for (BigInteger i = new BigInteger("0"); i.compareTo(this.length) < 0; i = i.add(new BigInteger("1"))) {
                b = in.read();
                out.write(b);
            }
            out.flush();
        }
        this.inStream = inStream;
    }

    /**
     * Constructor. Use a string to build a message.
     * @param content The string to use to create a message.
     * @throws IOException if an I/O error happened.
     */
    public Message(String content) throws IOException {
        length = new BigInteger(String.valueOf(content.length()));
        PipedInputStream inStream = new PipedInputStream();
        try (PipedOutputStream out = new PipedOutputStream()) {
            inStream.connect(out);
            for (int i = 0; i < content.length(); ++i) {
                out.write(content.charAt(i));
            }
            out.flush();
        }
        this.inStream = inStream;
    }

    /**
     * Close this message's InputStream when it's not to be used.
     * This method should be called if the message is no longer to be used.
     */
    public void dispose() throws IOException {
        if (inStream != null)
        {
            inStream.close();
        }
    }

    public BigInteger getLength() {
        return length;
    }

    public InputStream getInStream() {
        return inStream;
    }

    private final BigInteger length;
    private final InputStream inStream;

}
