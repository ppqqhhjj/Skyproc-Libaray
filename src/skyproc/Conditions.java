/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class Conditions extends SubRecord {

    int operator;
    LFlags flags = new LFlags(1);
    byte[] fluff;
    FormID comparisonValueForm = new FormID();
    float comparisonValueFloat;
    int functionIndex;
    byte[] padding;


    Conditions () {
	super(Type.CTDA);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	byte[] tmp = in.extract(1);
	byte[] tmp2 = new byte[3];
	System.arraycopy(tmp, 0, tmp2, 0, 3);
	operator = Ln.arrayToInt(tmp2);
	byte[] tmp3 = new byte[5];
	System.arraycopy(tmp, 3, tmp3, 0, 5);
	flags.set(tmp3);
	fluff = in.extract(3);

	if (get(CondFlag.UseGlobal)) {
	    comparisonValueForm.setInternal(in.extract(4));
	} else {
	    comparisonValueFloat = in.extractFloat();
	}

	functionIndex = in.extractInt(2);
	padding = in.extract(2);

    }

    @Override
    void standardizeMasters(Mod srcMod) {
	super.standardizeMasters(srcMod);
    }

    @Override
    SubRecord getNew(Type type) {
	return new Conditions();
    }

    @Override
    public void clear() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    Boolean isValid() {
	return true;
    }

    @Override
    int getContentLength(Mod srcMod) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public enum CondFlag {
	OR,
	UseAliases,
	UseGlobal,
	UsePackData,
	SwapSubjectAndTarget;
    }


    public boolean get (CondFlag flag) {
	return flags.is(flag.ordinal());
    }

    public void set (CondFlag flag, boolean on) {
	flags.set(flag.ordinal(), on);
    }
}
