package lev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author Justin Swanson
 */
public class LFileChannel {

    FileInputStream iStream;
    FileChannel iChannel;
    boolean returnf = false;
    long returnBack = 0;

    public LFileChannel() {
    }

    public LFileChannel(final String path) throws FileNotFoundException {
        openFile(path);
    }

    public LFileChannel(final File f) throws FileNotFoundException {
        openFile(f);
    }

    final public void openFile(final String path) throws FileNotFoundException {
        iStream = new FileInputStream(path);
        iChannel = iStream.getChannel();
    }

    final public void openFile(final File f) throws FileNotFoundException {
        openFile(f.getPath());
    }

    final public int read() throws IOException {
        return iStream.read();
    }

    final public long readInLong(final int skip, final int read) throws IOException {
        return Ln.arrayToLong(readInBytes(skip, read));
    }

    final public String readInString (final int skip, final int read) throws IOException {
        return Ln.arrayToString(readInBytes(skip,read));
    }

    final public int readInInt(final int skip, final int read) throws IOException {
        return Ln.arrayToInt(readInBytes(skip, read));
    }

    final public int[] readInInts(final int skip, final int read) throws IOException {
        return Ln.toIntArray(readInBytes(skip, read));
    }

    final public byte[] readInBytes(final int skip, final int read) throws IOException {
        offset(skip);
        ByteBuffer allocate = ByteBuffer.allocate(read);
        iChannel.read(allocate);
        return allocate.array();
    }

    final public ByteBuffer readInByteBuffer(int skip, int read) throws IOException {
        offset(skip);
        ByteBuffer buf = ByteBuffer.allocate(read);
        iChannel.read(buf);
        buf.flip();
        return buf;
    }

    final public void offset(final int skip) throws IOException {
        if (returnBack != 0) {
            returnBack += skip;
        }
        iChannel.position(iChannel.position() + skip);
    }

    final public void jumpBack() throws IOException {
        if (returnf) {
            iChannel.position(returnBack);
            returnf = false;
        }
    }

    final public void posAndReturn(long pos) throws IOException {
        markReturn();
        pos(pos);
    }

    final public void pos(long pos) throws IOException {
        iChannel.position(pos);
    }

    final public long pos() throws IOException {
        return iChannel.position();
    }

    final public void markReturn() throws IOException {
        returnBack = iChannel.position();
        returnf = true;
    }

    final public String scanTo(String... targets) throws IOException {
        LSearcher search = new LSearcher(targets);
        String result;
        int inputInt = iStream.read();

        while (inputInt != -1) {
            if (!(result = search.next(inputInt)).equals("")) {
                return result;
            }

            inputInt = iStream.read();
        }

        return "";
    }

    final public void close () throws IOException {
        iStream.close();
        iChannel.close();
    }

    final public int available() throws IOException {
        return iStream.available();
    }
}
