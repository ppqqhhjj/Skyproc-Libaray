package lev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A FileChannel setup that supports easy extraction/getting of information.
 *
 * @author Justin Swanson
 */
public class LFileChannel extends LChannel {

    FileInputStream iStream;
    FileChannel iChannel;
    long end;

    /**
     *
     */
    public LFileChannel() {
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws IOException
     */
    public LFileChannel(final String path) throws IOException {
	openFile(path);
    }

    /**
     *
     * @param f File to open a channel to.
     * @throws IOException
     */
    public LFileChannel(final File f) throws IOException {
	openFile(f);
    }

    /**
     *
     * @param rhs
     * @param allocation
     * @throws IOException
     */
    public LFileChannel(LFileChannel rhs, long allocation) throws IOException {
	LFileChannel fc = (LFileChannel) rhs;
	iStream = fc.iStream;
	iChannel = fc.iChannel;
	end = iChannel.position() + allocation;
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws IOException 
     */
    final public void openFile(final String path) throws IOException {
	iStream = new FileInputStream(path);
	iChannel = iStream.getChannel();
	end = iChannel.size();
    }

    /**
     *
     * @param f File to open a channel to.
     * @throws IOException
     */
    final public void openFile(final File f) throws IOException {
	openFile(f.getPath());
    }

    /**
     * Reads in a byte and moves forward one position in the file.
     *
     * @return The next int in the file.
     * @throws IOException
     */
    @Override
    final public int read() throws IOException {
	return iStream.read();
    }

    /**
     * Reads in the desired bytes and wraps them in a ByteBuffer.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return ByteBuffer containing read bytes.
     * @throws IOException
     */
    final public ByteBuffer extractByteBuffer(int skip, int read) throws IOException {
	super.skip(skip);
	ByteBuffer buf = ByteBuffer.allocate(read);
	iChannel.read(buf);
	buf.flip();
	return buf;
    }

    /**
     *
     * @param pos Position to move to.
     * @throws IOException
     */
    @Override
    final public void pos(long pos) throws IOException {
	iChannel.position(pos);
    }

    /**
     *
     * @return Current position.
     * @throws IOException
     */
    @Override
    final public long pos() throws IOException {
	return iChannel.position();
    }

    /**
     * Closes streams.
     *
     * @throws IOException
     */
    @Override
    final public void close() throws IOException {
	if (iStream != null) {
	    iStream.close();
	    iChannel.close();
	}
    }

    /**
     *
     * @return Bytes left to read in the file.
     * @throws IOException
     */
    @Override
    final public int available() throws IOException {
	return (int) (end - iChannel.position());
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public Boolean isDone() throws IOException {
	return iChannel.position() == end;
    }

    /**
     *
     * @param amount
     * @return
     * @throws IOException
     */
    @Override
    public byte[] extract(int amount) throws IOException {
	ByteBuffer allocate = ByteBuffer.allocate(amount);
	iChannel.read(allocate);
	return allocate.array();
    }

    @Override
    public byte[] extractUntil(int delimiter) throws IOException {
	int counter = 1;
	while (!isDone()) {
	    if (iStream.read() != delimiter) {
		counter++;
	    } else {
		jumpBack(counter);
		byte[] out = extract(counter - 1);
		skip(1);
		return out;
	    }
	}
	return new byte[0];
    }
}
