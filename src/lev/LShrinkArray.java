/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * A special array with artificial min/max bounds.  This allows smaller parts of the array
 * to be passed around without actually copying the data to a new smaller array.
 * It also provides extract functions which bump the lower limit of the ShrinkArray's scope, so that
 * a following extract function can be called without having to worry about offset indexing.
 * @author Justin Swanson
 */
public class LShrinkArray {

    private static String header = "Shrink Array";
    private static Inflater decompresser = new Inflater();
    private ByteBuffer buffer;

    /**
     * Wraps bytes in a ShrinkArray.
     * @param in
     */
    public LShrinkArray(final byte[] in) {
        buffer = ByteBuffer.wrap(in);
    }

    /**
     * Wraps ByteBuffer in a ShrinkArray.
     * @param array_
     */
    public LShrinkArray(final ByteBuffer array_) {
        buffer = array_.duplicate();
    }

    /**
     * Creates a new ShrinkArray based on the same underlying array, starting
     * at the same beginning index of the rhs ShrinkArray, but with an upper limit
     * of high.
     * @param rhs ShrinkArray to copy bounds from.
     * @param high New upper limit to give to the ShrinkArray.
     */
    public LShrinkArray(final LShrinkArray rhs, final int high) {
        this(rhs);
        buffer.limit(high);
    }

    /**
     * Creates a new ShrinkArray based on the same underlying array, starting
     * at the same bounds of the rhs ShrinkArray.
     * @param rhs ShrinkArray to copy bounds from.
     */
    public LShrinkArray(final LShrinkArray rhs) {
        buffer = rhs.buffer.slice();
    }

    /**
     * Loads in a file as raw data and wraps it in a ShrinkArray.
     * @param f File to load in.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public LShrinkArray(final File f) throws FileNotFoundException, IOException {
        LFileChannel in = new LFileChannel(f);
        buffer = ByteBuffer.wrap(in.readInBytes(0, in.available()));
    }

    /**
     *
     * @return True if ShrinkArray has no bytes left to extract.
     */
    public Boolean isEmpty() {
        return !buffer.hasRemaining();
    }

    /**
     *
     * @return Amount of bytes left in the bounds of the ShrinkArray.
     */
    public final int length() {
        return buffer.remaining();
    }

    /**
     * Bumps the lower bound up to "skip" that many bytes.
     * @param skip Bytes to skip over.
     */
    public final void skip(final int skip) {
        buffer.position(buffer.position() + skip);
    }

    /**
     * Bumps the lower bound back to include bytes previously read/skipped or outside the current bounds.
     * @param amount Amount to jump back.
     */
    public void jumpBack(final int amount) {
        skip(-amount);
    }

    /**
     * Returns the remaining contents as an int array, but does NOT adjust the
     * bounds of the ShrinkArray.
     * @return The remaining contents as an int array.
     */
    public int[] getAllInts() {
        return getInts(0, length());
    }

    /**
     * Returns the remaining contents as an int array, and adjusts bounds of the ShrinkArray
     * to be empty.
     * @return The remaining contents as an int array.
     */
    public int[] extractAllInts() {
        return extractInts(0, length());
    }

    /**
     * Returns the remaining contents as a byte array, but does NOT adjust the
     * bounds of the ShrinkArray.
     * @return The remaining contents as a byte array.
     */
    public byte[] getAllBytes() {
        return getBytes(0, length());
    }

    /**
     * Returns the remaining contents as a byte array, and adjusts bounds of the ShrinkArray
     * to be empty.
     * @return The remaining contents as a byte array.
     */
    public byte[] extractAllBytes() {
        return extract(length());
    }

    /**
     * Gets specified number of bytes after skipping the desired amount.  Does not adjust
     * bounds of the ShrinkArray.
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
     * bounds of the ShrinkArray.
     * @param skip Amount to skip.
     * @param amount Amount to read.
     * @return int array containing desired offset.
     */
    public int[] getInts(int skip, int amount) {
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
    public int[] extractInts (int amount) {
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
    public byte[] extractLine () {
        return extractUntil(10);
    }

    /**
     * Extracts 4 bytes and returns their float representation<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @return Float represented by the next 4 bytes.
     */
    public float extractFloat() {
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

    public String getString(int amount) {
        return Ln.arrayToString(getInts(0,amount));
    }

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
     * Extracts bytes until the delemiter is read.<br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param delimiter Integer at which to stop reading.
     * @return Bytes read excluding the signaling delimiter.
     */
    public byte[] extractUntil(int delimiter) {
        int counter = 1;
        byte[] array = buffer.array();
        for (int i = buffer.arrayOffset() + buffer.position(); i < buffer.limit() + buffer.arrayOffset(); i++) {
            if (array[i] != delimiter) {
                counter++;
            } else {
                byte[] out = extract(counter - 1);
                skip(1);
                return out;
            }
        }
        return new byte[0];
    }

    /**
     * Extracts specified number of bytes. <br>
     * Bumps the lower bound up so that following extracts do not extract the same data.
     * @param amount Amount to read.
     * @return byte array.
     */
    public byte[] extract(int amount) {
        byte[] bytes = new byte[amount];
        buffer.get(bytes);
        return bytes;
    }

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
     * Assumes the contents of the ShrinkArray is raw zipped data in its entirety, and nothing else.
     * It then unzips that data in the ShrinkArray.
     * @throws DataFormatException
     */
    public void correctForCompression() throws DataFormatException {

        int uncompressedSize = Ln.arrayToInt(extractInts(4));

        byte[] compressedByteData = getAllBytes();

        //Uncompress
        decompresser.setInput(compressedByteData, 0, length());
        byte[] uncompressedByteData = new byte[uncompressedSize];
        decompresser.inflate(uncompressedByteData);
        decompresser.reset();

        buffer = ByteBuffer.wrap(uncompressedByteData);

    }
}
