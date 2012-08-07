/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Justin Swanson
 */
public class LByteChannel extends LChannel {

    byte[] input;
    int pos;

    /**
     *
     */
    public LByteChannel() {
    }

    /**
     *
     * @param path Path to open a channel to.
     * @throws FileNotFoundException
     */
    public LByteChannel(final byte[] input) throws FileNotFoundException {
	openStream(input);
    }

    public final void openStream(byte[] input) {
	this.input = input;
	pos = 0;
    }

    public final void openStream(LShrinkArray in) {
	openStream(in.extractAllBytes());
    }

    @Override
    public int read() throws IOException {
	return Ln.bToUInt(input[pos++]);
    }

    @Override
    public byte[] readInBytes(int skip, int read) throws IOException {
	offset(skip);
	byte[] out = new byte[read];
	for (int i = 0 ; i < read ; i++) {
	    out[i] = input[pos + i];
	}
	offset(read);
	return out;
    }

    @Override
    public void pos(long pos) throws IOException {
	this.pos = (int) pos;
    }

    @Override
    public long pos() throws IOException {
	return pos;
    }

    @Override
    public void close() throws IOException {
	input = null;
    }

    @Override
    public int available() throws IOException {
	return (int)(input.length - pos());
    }

}
