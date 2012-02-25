/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Justin Swanson
 */
public class LExportParser {

    private BufferedOutputStream output;

    public LExportParser(String path) throws FileNotFoundException {
        openOutput(path);
    }

    public final void openOutput(String in) throws FileNotFoundException {
        Ln.makeDirs(in);
        output = new BufferedOutputStream(new FileOutputStream(in));
    }

    public void write(byte[] array) throws IOException {
        output.write(array);
    }

    public void write(byte[] array, int size) throws IOException {
        write(array);
        if (size - array.length > 0) {
            writeZeros(size - array.length);
        }
    }

//    public void write(int[] array, int size) throws java.io.IOException {
////        ByteBuffer out = ByteBuffer.allocate(size);
//        for (int i = 0; i < array.length; i++) {
//            output.write(array[i]);
//        }
//        if (size - array.length > 0) {
//            writeZeros(size - array.length);
//        }
//    }

    public void writeZeros(int size) throws IOException {
        output.write(new byte[size]);
    }

    public void write(int input, int size) throws java.io.IOException {
        write(Ln.toByteArray(input, size, size));
    }

    public void write(String input) throws java.io.IOException {
        write(input, 0);
    }

    public void write(String input, int size) throws IOException {
        write(Ln.toByteArray(input), size);
    }

    public void write(float input) throws IOException {
        ByteBuffer out = ByteBuffer.allocate(4);
        out.putInt(Integer.reverseBytes(Float.floatToIntBits(input)));
        write(out.array(), 4);
    }

    public void close() throws IOException {
        output.close();
    }
}