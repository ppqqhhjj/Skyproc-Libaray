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
 *
 * @author Justin Swanson
 */
public class LShrinkArray {

    private static String header = "Shrink Array";
    private static Inflater decompresser = new Inflater();
    private ByteBuffer buffer;

    public LShrinkArray(final byte[] in) {
        buffer = ByteBuffer.wrap(in);
    }

    public LShrinkArray(final ByteBuffer array_) {
        buffer = array_.duplicate();
    }

    public LShrinkArray(final LShrinkArray rhs, final int high) {
        this(rhs);
        buffer.limit(high);
    }

    public LShrinkArray(final LShrinkArray rhs) {
        buffer = rhs.buffer.slice();
    }

    public LShrinkArray(final File f) throws FileNotFoundException, IOException {
        LFileChannel in = new LFileChannel(f);
        buffer = ByteBuffer.wrap(in.readInBytes(0, in.available()));
    }

    public Boolean isEmpty() {
        return !buffer.hasRemaining();
    }

    public final int length() {
        return buffer.remaining();
    }

    public final void skip(final int low) {
        buffer.position(buffer.position() + low);
    }

    private void jumpBack(final int amount) {
        skip(-amount);
    }

    public int[] getAllInts() {
        return getInts(0, length());
    }

    public int[] extractAllInts() {
        return extractInts(0, length());
    }

    public byte[] getAllBytes() {
        return getBytes(0, length());
    }

    public byte[] extractAllBytes() {
        return extract(0,length());
    }

    public byte[] getBytes(int skip, int amount) {
        byte[] out = extract(skip, amount);
        jumpBack(skip + amount);
        return out;
    }

    public int[] getInts(int skip, int amount) {
        return Ln.toIntArray(getBytes(skip, amount));
    }

    public int[] extractInts(int skip, int amount) {
        skip(skip);
        return Ln.toIntArray(extract(amount));
    }

    public int[] extractInts (int amount) {
        return Ln.toIntArray(extract(amount));
    }

    public int extractInt(int skip, int amount) {
        return Ln.arrayToInt(extractInts(skip, amount));
    }

    public int extractInt(int amount) {
        return Ln.arrayToInt(extractInts(amount));
    }

    public byte[] extractLine () {
        return extractUntil(10);
    }

    public float extractFloat() {
        return Float.intBitsToFloat(extractInt(0, 4));
    }

    public String extractString(int skip, int amount) {
        return Ln.arrayToString(getInts(skip, amount));
    }

    public String extractString(int amount) {
        return Ln.arrayToString(extractInts(amount));
    }

    public String extractString() {
        return Ln.arrayToString(extractUntil(0));
    }

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

    public byte[] extract(int amount) {
        byte[] bytes = new byte[amount];
        buffer.get(bytes);
        return bytes;
    }

    public byte[] extract(int skip, int amount) {
        skip(skip);
        return extract(amount);
    }

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
