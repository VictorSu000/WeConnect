package role;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Created by Victor on 2017/5/1.<br>
 * <br>
 * A message represent data transferred at one time.
 * It includes the length of data, and a InputStream that consists of all the data
 */
public class Message {

    /**
     * Constructor.
     * @param length The length of the data.
     * @param inStream The InputStream of all the data.
     */
    public Message(BigInteger length, InputStream inStream) {
        this.length = length;
        this.inStream = inStream;
    }

    /**
     * Close this message's InputStream when it's not to be used.
     * If InputStream is closed outside this class, this method should not be called.
     */
    public void dispose() {
        if (inStream != null)
        {
            try {
                inStream.close();
            } catch (IOException e)
            {
                System.out.println("Unexpected error happens when trying to close inStream in Message.");
                System.out.println("Error message" + e.getMessage());
                // TODO: use logger
            }
        }
    }

    public BigInteger getLength() {
        return length;
    }

    public InputStream getInStream() {
        return inStream;
    }

    private final BigInteger length;
    private InputStream inStream;

}
