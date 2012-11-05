/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import lev.LChannel;
import lev.LFileChannel;
import lev.LShrinkArray;

/**
 *
 * @author Justin Swanson
 */
public class RecordShrinkArray extends LShrinkArray {

    int offset;

    public RecordShrinkArray(final LChannel rhs, final int high) {
	super(rhs, high);
	offset = (int) rhs.pos();
    }

    public RecordShrinkArray(LShrinkArray rhs) {
	super(rhs);
	offset = (int) rhs.pos();
    }

    @Override
    public long pos() {
	return super.pos() + offset;
    }

    @Override
    public void pos(long in) {
	super.pos(in - offset);
    }
}
