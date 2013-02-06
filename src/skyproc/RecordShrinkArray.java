/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import lev.LChannel;
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

    public RecordShrinkArray() {
	super(new byte[0]);
	offset = 0;
    }

    public RecordShrinkArray(byte[] in) {
	super(in);
	offset = 0;
    }

    @Override
    public long pos() {
	return super.pos() + offset;
    }

    @Override
    public void pos(long in) {
	super.pos(in - offset);
    }

    public FormID extractFormID(Mod modToStandardizeTo) {
	FormID out = new FormID();
	if (!isDone()) {
	    byte[] extract = extract(4);
	    byte[] copy = new byte[4];
	    System.arraycopy(extract, 0, copy, 0, copy.length);
	    out.setInternal(copy);
	    out.standardize(modToStandardizeTo);
	}
	return out;
    }

    public FormID extractFormID(ModListing modToStandardizeTo) {
	Mod mod = SPGlobal.getDB().getMod(modToStandardizeTo);
	if (mod == null) {
	    return new FormID();
	}
	return extractFormID(mod);
    }

    public FormID extractFormID(String type, Mod modToStandardizeTo) {
	extractUntil(type);
	if (!isDone()) {
	    skip(2); // Length bytes
	}
	return extractFormID(modToStandardizeTo);
    }

    public FormID extractFormID(String type, ModListing modToStandardizeTo) {
	Mod mod = SPGlobal.getDB().getMod(modToStandardizeTo);
	if (mod == null) {
	    return new FormID();
	}
	return extractFormID(type, mod);
    }
}
