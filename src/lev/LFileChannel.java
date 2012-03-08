package lev;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * A FileChannel setup that supports easy extraction/getting of information.
 *
 * @author Justin Swanson
 */
public class LFileChannel {

    FileInputStream iStream;
    FileChannel iChannel;
    boolean returnf = false;
    long returnBack = 0;

    /**
     *
     */
    public LFileChannel() {
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws FileNotFoundException
     */
    public LFileChannel(final String path) throws FileNotFoundException {
	openFile(path);
    }

    /**
     *
     * @param f File to open a channel to.
     * @throws FileNotFoundException
     */
    public LFileChannel(final File f) throws FileNotFoundException {
	openFile(f);
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws FileNotFoundException
     */
    final public void openFile(final String path) throws FileNotFoundException {
	iStream = new FileInputStream(path);
	iChannel = iStream.getChannel();
    }

    /**
     *
     * @param f File to open a channel to.
     * @throws FileNotFoundException
     */
    final public void openFile(final File f) throws FileNotFoundException {
	openFile(f.getPath());
    }

    /**
     * Reads in a byte and moves forward one position in the file.
     *
     * @return The next int in the file.
     * @throws IOException
     */
    final public int read() throws IOException {
	return iStream.read();
    }

    /**
     * Reads in the desired bytes and converts them to a long (little endian
     * assumed).
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return Long representation of read bytes
     * @throws IOException
     */
    final public long readInLong(final int skip, final int read) throws IOException {
	return Ln.arrayToLong(readInBytes(skip, read));
    }

    /**
     * Reads in the desired bytes and converts them to a string.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return String representation of read bytes
     * @throws IOException
     */
    final public String readInString(final int skip, final int read) throws IOException {
	return Ln.arrayToString(readInBytes(skip, read));
    }

    /**
     * Reads in the desired bytes and converts them to a int (little endian
     * assumed).
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return Integer representation of read bytes
     * @throws IOException
     */
    final public int readInInt(final int skip, final int read) throws IOException {
	return Ln.arrayToInt(readInBytes(skip, read));
    }

    /**
     * Reads in the desired bytes and converts them to an int array.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return
     * @throws IOException
     */
    final public int[] readInInts(final int skip, final int read) throws IOException {
	return Ln.toIntArray(readInBytes(skip, read));
    }

    /**
     * Reads in the desired bytes.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return
     * @throws IOException
     */
    final public byte[] readInBytes(final int skip, final int read) throws IOException {
	offset(skip);
	ByteBuffer allocate = ByteBuffer.allocate(read);
	iChannel.read(allocate);
	return allocate.array();
    }

    /**
     * Reads in the desired bytes and wraps them in a ByteBuffer.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return ByteBuffer containing read bytes.
     * @throws IOException
     */
    final public ByteBuffer readInByteBuffer(int skip, int read) throws IOException {
	offset(skip);
	ByteBuffer buf = ByteBuffer.allocate(read);
	iChannel.read(buf);
	buf.flip();
	return buf;
    }

    /**
     * Reads in characters until a null byte (0) is read, and converts them to a
     * string.
     *
     * @return Next string in the file.
     * @throws IOException
     */
    public String readString() throws IOException {
	return Ln.arrayToString(readUntil(0));
    }

    /**
     * Reads in a line until a newline character is found. (Byte of 10, or bytes 13 -> 10)
     * @return 
     * @throws IOException
     */
    public String readLine() throws IOException {
	byte[] read1 = { 10 };
	byte[] read2 = { 13 , 10 };
	return Ln.arrayToString(readUntil(read2, read1));
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter char to stop reading at.
     * @param bufsize Buffer size to hold readings.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(char delimiter, int bufsize) throws IOException {
	return readUntil((int) delimiter, bufsize);
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter char to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(char delimiter) throws IOException {
	return readUntil((int) delimiter);
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter Byte to stop reading at.
     * @param bufsize Buffer size to hold readings.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(int delimiter, int bufsize) throws IOException {
	byte[] buffer = new byte[bufsize];
	int counter = 0;
	int in;
	while (available() > 0 && (in = read()) != delimiter) {
	    buffer[counter++] = (byte) in;
	}
	byte[] out = new byte[counter];
	System.arraycopy(buffer, 0, out, 0, counter);
	return out;
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter int to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(int delimiter) throws IOException {
	byte[] delimiterB = {(byte) delimiter};
	return readUntil(delimiterB);
    }

    /**
     * Reads in bytes until any of the delimiters are read.  Returns the
     * first delimiter found, so parameter order matters.
     * @param delimiters Byte arrays of patterns to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(byte[] ... delimiters) throws IOException {
	ArrayList<Byte> buffer = new ArrayList<Byte>(50);
	LByteSearcher search = new LByteSearcher(delimiters);
	int in;
	byte[] stop = new byte[0];
	while (available() > 0 && stop.length == 0) {
	    in = read();
	    if ((stop = search.next(in)).length == 0) {
		buffer.add((byte) in);
	    }
	}
	byte[] out = new byte[buffer.size() - (stop.length - 1)];
	for (int i = 0; i < buffer.size() - (stop.length - 1); i++) {
	    out[i] = buffer.get(i);
	}
	return out;
    }

    /**
     * Bumps the position the desired offset.
     *
     * @param offset Desired offset.
     * @throws IOException
     */
    final public void offset(final int offset) throws IOException {
	if (returnBack != 0) {
	    returnBack += offset;
	}
	iChannel.position(iChannel.position() + offset);
    }

    /**
     * Jumps back to the marked location that was set using posAndReturn()
     *
     * @throws IOException
     */
    final public void jumpBack() throws IOException {
	if (returnf) {
	    iChannel.position(returnBack);
	    returnf = false;
	}
    }

    /**
     * Moves to the desired position, but marks the current location for
     * jumpBack().
     *
     * @param pos Position to move to.
     * @throws IOException
     */
    final public void posAndReturn(long pos) throws IOException {
	markReturn();
	pos(pos);
    }

    /**
     *
     * @param pos Position to move to.
     * @throws IOException
     */
    final public void pos(long pos) throws IOException {
	iChannel.position(pos);
    }

    /**
     *
     * @return Current position.
     * @throws IOException
     */
    final public long pos() throws IOException {
	return iChannel.position();
    }

    /**
     * Marks the current position for jumpBack().
     *
     * @throws IOException
     */
    final public void markReturn() throws IOException {
	returnBack = iChannel.position();
	returnf = true;
    }

    /**
     * Uses an LStringSearcher to read file contents until one of the targets is
     * found.
     *
     * @param targets
     * @return The target found, or the empty string if none found.
     * @throws IOException
     */
    final public String scanTo(String... targets) throws IOException {
	LStringSearcher search = new LStringSearcher(targets);
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

    /**
     * Closes streams.
     *
     * @throws IOException
     */
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
    final public int available() throws IOException {
	return iStream.available();
    }
}
