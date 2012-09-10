/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public abstract class LChannel {

    /**
     * Reads in one integer and moves the position up one
     * @return
     * @throws IOException
     */
    public abstract int read() throws IOException;

    /**
     * Reads in the desired bytes and converts them to a long (little endian
     * assumed).
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return Long representation of read bytes
     * @throws IOException
     */
    final public long extractLong(final int skip, final int read) throws IOException {
	return Ln.arrayToLong(extract(skip, read));
    }

    /**
     * Reads in the desired bytes and converts them to a string.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return String representation of read bytes
     * @throws IOException
     */
    final public String extractString(final int skip, final int read) throws IOException {
	return Ln.arrayToString(extract(skip, read));
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
    final public int extractInt(final int skip, final int read) throws IOException {
	return Ln.arrayToInt(extract(skip, read));
    }

    /**
     * Reads in the desired bytes and converts them to an int array.
     *
     * @param skip Bytes to skip
     * @param read Bytes to read and convert
     * @return
     * @throws IOException
     */
    final public int[] extractInts(final int skip, final int read) throws IOException {
	return Ln.toIntArray(extract(skip, read));
    }

    /**
     * Skips an amount of bytes, reads in amount of bytes and converts them to integers.<br>
     * Does NOT move position.
     * @param skip
     * @param read
     * @return
     * @throws IOException
     */
    final public int[] getInts(final int skip, final int read) throws IOException {
	int[] out = extractInts(skip, read);
	jumpBack(skip + read);
	return out;
    }

    /**
     * Reads in characters until a null byte (0) is read, and converts them to a
     * string.
     *
     * @return Next string in the file.
     * @throws IOException
     */
    public String extractString() throws IOException {
	return Ln.arrayToString(extractUntil(0));
    }

    public String extractAllString() throws IOException {
	return Ln.arrayToString(extract(0, available()));
    }

    /**
     * Reads in a line until a newline character is found. (Byte of 10, or bytes 13 -> 10)
     * @return
     * @throws IOException
     */
    public String extractLine() throws IOException {
	byte[] read1 = { 10 };
	byte[] read2 = { 13 , 10 };
	return Ln.arrayToString(extractUntil(read2, read1));
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter char to stop reading at.
     * @param bufsize Buffer size to hold readings.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] extractUntil(char delimiter, int bufsize) throws IOException {
	return extractUntil((int) delimiter, bufsize);
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter char to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] extractUntil(char delimiter) throws IOException {
	return extractUntil((int) delimiter);
    }

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter int to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] extractUntil(int delimiter) throws IOException {
	byte[] delimiterB = {(byte) delimiter};
	return extractUntil(delimiterB);
    }

    /**
     * Skips an amount of bytes, reads in amount of bytes and converts them to integers.<br>
     * Moves position forward.
     * @param skip
     * @param read
     * @return
     * @throws IOException
     */
    public byte[] extract(final int skip, final int read) throws IOException {
	skip(skip);
	return extract(read);
    }

    /**
     * Reads in amount of bytes and converts them to integers.<br>
     * Moves position forward.
     * @param amount
     * @return
     * @throws IOException
     */
    public abstract byte[] extract(final int amount) throws IOException;

    /**
     * Reads in bytes until the delimiter is read.
     *
     * @param delimiter Byte to stop reading at.
     * @param bufsize Buffer size to hold readings.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] extractUntil(int delimiter, int bufsize) throws IOException {
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
     * Reads in bytes until any of the delimiters are read.  Returns the
     * first delimiter found, so parameter order matters.
     * @param delimiters Byte arrays of patterns to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] extractUntil(byte[] ... delimiters) throws IOException {
	ArrayList<Byte> buffer = new ArrayList<>(50);
	LByteSearcher search = new LByteSearcher(delimiters);
	int in;
	byte[] stop = new byte[0];
	while (available() > 0 && stop.length == 0) {
	    in = read();
	    stop = search.next(in);
	    if (stop.length == 0) {
		buffer.add((byte) in);
	    }
	}
	byte[] out = new byte[buffer.size() - (stop.length - 1)];
	for (int i = 0; i < buffer.size() - (stop.length - 1) && i < buffer.size() ; i++) {
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
    public void skip(final int offset) throws IOException {
	pos(pos() + offset);
    }

    /**
     * Moves position back an amount of bytes.
     * @param amount
     * @throws IOException
     */
    public void jumpBack(int amount) throws IOException {
	skip(-amount);
    }

    /**
     *
     * @param pos
     * @throws IOException
     */
    public abstract void pos(long pos) throws IOException;

    /**
     *
     * @return
     * @throws IOException
     */
    public abstract long pos() throws IOException;

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
	int inputInt = read();

	while (inputInt != -1) {
	    if (!(result = search.next(inputInt)).equals("")) {
		return result;
	    }

	    inputInt = read();
	}

	return "";
    }

    /**
     *
     * @throws IOException
     */
    public abstract void close() throws IOException;

    /**
     *
     * @return
     * @throws IOException
     */
    public abstract int available() throws IOException;
}
