/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 *
 * @author Justin Swanson
 */
public abstract class LStream {

    static Inflater decompresser = new Inflater();

    /**
     *
     * @return True if Stream has no more bytes.
     */
    public abstract Boolean isDone();

    /**
     *
     * @return Number of bytes left in the stream
     */
    public abstract int available();

    /**
     *
     * @param skip Number of bytes to skip
     */
    public abstract void skip(final int skip);

    /**
     *
     * @param amount Number of bytes to move the position back
     */
    public abstract void jumpBack(final int amount);

    /**
     * Returns the remaining contents as an int array, but does NOT adjust the
     * bounds.
     * @return The remaining contents as an int array.
     */
    public int[] getAllInts()  {
        return getInts(0, available());
    }


    /**
     * Returns the remaining contents as an int array, and adjusts bounds
     * to be empty.
     * @return The remaining contents as an int array.
     */
    public int[] extractAllInts() {
        return extractInts(0, available());
    }


    /**
     * Returns the remaining contents as a byte array, but does NOT adjust the
     * bounds.
     * @return The remaining contents as a byte array.
     */
    public byte[] getAllBytes() {
        return getBytes(0, available());
    }

    /**
     * Returns the remaining contents as a byte array, and adjusts bounds
     * to be empty.
     * @return The remaining contents as a byte array.
     */
    public byte[] extractAllBytes()  {
        return extract(available());
    }

    /**
     * Gets specified number of bytes after skipping the desired amount.  Does not adjust
     * bounds.
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return byte array containing desired offset.
     */
    public byte[] getBytes(int skip, int amount) {
        byte[] out = extract(skip, amount);
        jumpBack(skip + amount);
        return out;
    }

    /**
     * Gets specified number of ints after skipping the desired amount.  Does not adjust
     * bounds.
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return int array containing desired offset.
     */
    public int[] getInts(int skip, int amount){
        return Ln.toIntArray(getBytes(skip, amount));
    }

    /**
     * Extracts specified number of ints after skipping the desired amount. <br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return int array containing desired offset.
     */
    public int[] extractInts(int skip, int amount) {
        skip(skip);
        return Ln.toIntArray(extract(amount));
    }

    /**
     * Extracts specified number of ints. <br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param amount Amount to read.
     * @return int array containing desired offset.
     */
    public int[] extractInts (int amount){
        return Ln.toIntArray(extract(amount));
    }

    /**
     * Returns the little endian integer representation of the extracted ints after skipping the desired amount. <br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return little endian int that was represented by the extracted bytes.
     */
    public int extractInt(int skip, int amount) {
        return Ln.arrayToInt(extract(skip, amount));
    }

    /**
     * Returns the little endian integer representation of the extracted ints. <br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param amount Amount to read.
     * @return little endian int that was represented by the extracted bytes.
     */
    public int extractInt(int amount) {
        return Ln.arrayToInt(extract(amount));
    }

    /**
     * Extracts data until a newline (byte == 10) is extracted. <br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @return bytes that make up a line.
     */
    public byte[] extractLine (){
        return extractUntil(10);
    }

    /**
     * Extracts 4 bytes and returns their float representation<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @return Float represented by the next 4 bytes.
     */
    public float extractFloat(){
        return Float.intBitsToFloat(extractInt(0, 4));
    }

    /**
     *
     * @param amount
     * @return
     */
    public boolean extractBool(int amount) {
	if (extract(amount)[0] == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Gets specified number of bytes and converts them to a string.  Does not adjust
     * bounds.
     * @param amount
     * @return
     */
    public String getString(int amount) {
        return Ln.arrayToString(getInts(0,amount));
    }

    /**
     * Gets specified number of bytes after skipping the desired amount and converts them to a string.  Does not adjust
     * bounds.
     * @param skip
     * @param amount
     * @return
     */
    public String getString(int skip, int amount) {
        return Ln.arrayToString(getInts(skip, amount));
    }

    /**
     * Extracts the specified number of bytes after skipping the desired amount,
     * and returns the string representation.<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param skip Amount to skip
     * @param amount Amount to read.
     * @return String representation of the bytes read.
     */
    public String extractString(int skip, int amount) {
        String out = getString(skip, amount);
	skip(skip + amount);
	return out;
    }

    /**
     * Extracts the specified number of bytes, and returns the string representation.<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param amount Amount to read.
     * @return String representation of the bytes read.
     */
    public String extractString(int amount) {
	String out = getString(amount);
	skip(amount);
	return out;
    }

    /**
     * Extracts bytes until a null character is read, and then returns
     * the string representation.<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @return A null-terminated string.
     */
    public String extractString() {
        return Ln.arrayToString(extractUntil(0));
    }

    /**
     * Gets specified number of bytes.<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param amount
     * @return
     */
    public abstract byte[] extract(int amount);

    /**
     * Gets specified number of bytes after skipping the desired amount.<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return byte array containing desired offset.
     */
    public byte[] extract(int skip, int amount) {
        skip(skip);
        return extract(amount);
    }

    /**
     * Extracts bytes until the delimiter integer is extracted. (inclusive)
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param delimiter
     * @return
     */
    public abstract byte[] extractUntil(int delimiter);

    /**
     * Assumes the contents of the ShrinkArray is raw zipped data in its entirety, and nothing else.
     * It then unzips that data in the ShrinkArray.
     * @return new ShrinkArray with uncompressed data.
     * @throws DataFormatException
     */
    public LShrinkArray correctForCompression() throws DataFormatException {

        int uncompressedSize = Ln.arrayToInt(extractInts(4));

        byte[] compressedByteData = getAllBytes();

        //Uncompress
        decompresser.setInput(compressedByteData, 0, available());
        byte[] uncompressedByteData = new byte[uncompressedSize];
        decompresser.inflate(uncompressedByteData);
        decompresser.reset();

	return new LShrinkArray(ByteBuffer.wrap(uncompressedByteData));
    }
}
