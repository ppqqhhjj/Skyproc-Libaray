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
    int end;

    /**
     *
     */
    public LByteChannel() {
    }

    /**
     *
     * @param input 
     * @throws FileNotFoundException
     */
    public LByteChannel(final byte[] input) throws FileNotFoundException {
	openStream(input);
    }

    /**
     *
     * @param input
     */
    public final void openStream(byte[] input) {
	this.input = input;
	pos = 0;
	end = input.length;
    }

    /**
     *
     * @param in
     * @throws IOException
     */
    public final void openStream(LShrinkArray in) throws IOException {
	openStream(in.extractAllBytes());
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
	return Ln.bToUInt(input[pos++]);
    }

    /**
     *
     * @param pos
     * @throws IOException
     */
    @Override
    public void pos(long pos) throws IOException {
	this.pos = (int) pos;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public long pos() throws IOException {
	return pos;
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
	input = null;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public int available() throws IOException {
	return end - pos;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public Boolean isDone() throws IOException {
	return pos == end;
    }

    @Override
    public void skip(int skip) throws IOException {
	pos += skip;
    }

    /**
     *
     * @param amount
     * @throws IOException
     */
    @Override
    public void jumpBack(int amount) throws IOException {
	skip(-amount);
    }

    /**
     *
     * @param read
     * @return
     * @throws IOException
     */
    @Override
    public byte[] extract(int read) throws IOException {
	byte[] out = new byte[read];
	for (int i = 0 ; i < read ; i++) {
	    out[i] = input[pos + i];
	}
	skip(read);
	return out;
    }

}
