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
public abstract class LChannel extends LStream {

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
     * @param delimiter int to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(int delimiter) throws IOException {
	byte[] delimiterB = {(byte) delimiter};
	return readUntil(delimiterB);
    }

    public abstract byte[] readInBytes(final int skip, final int read) throws IOException;

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
     * Reads in bytes until any of the delimiters are read.  Returns the
     * first delimiter found, so parameter order matters.
     * @param delimiters Byte arrays of patterns to stop reading at.
     * @return Byte array containing read data without delimiter.
     * @throws IOException
     */
    public byte[] readUntil(byte[] ... delimiters) throws IOException {
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
    final public void offset(final int offset) throws IOException {
	pos(pos() + offset);
    }

    public abstract void pos(long pos) throws IOException;

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

    public abstract void close() throws IOException;

    public abstract int available() throws IOException;
}
