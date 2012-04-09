/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFileChannel;
import lev.LShrinkArray;
import skyproc.SubStringPointer.Files;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class GMST extends MajorRecord {

    static Type[] types = {Type.GMST};
    DATA DATA = new DATA();

    GMST() {
	super();
	subRecords.add(DATA);
    }

    @Override
    Record getNew() {
	return new GMST();
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    public GMSTType getType() {
	if (getEDID().length() == 0) {
	    return GMSTType.Unknown;
	}
	switch (getEDID().charAt(0)) {
	    case 'b':
		return GMSTType.Bool;
	    case 'i':
		return GMSTType.Int;
	    case 'f':
		return GMSTType.Float;
	    case 's':
	    case 'S':
		return GMSTType.String;
	    default:
		return GMSTType.Unknown;
	}
    }

    public enum GMSTType {

	Bool,
	Int,
	Float,
	String,
	Unknown;
    }

    public void setData(Boolean b) {
	if (b) {
	    DATA.DATA.setData(1, 4);
	} else {
	    DATA.DATA.setData(0, 4);
	}
    }

    public void setData(String s) {
	DATA.DATAs.setText(s);
    }

    public void setData(int i) {
	DATA.DATA.setData(i, 4);
    }

    public void setData(float f) {
	ByteBuffer out = ByteBuffer.allocate(4);
	out.putInt(Integer.reverseBytes(Float.floatToIntBits(f)));
	DATA.DATA.setData(out.array());
    }

    public boolean getBool() {
	if (DATA.DATA.toInt() == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    public String getString() {
	return DATA.DATAs.print();
    }

    public int getInt() {
	return DATA.DATA.toInt();
    }

    public float getFloat() {
	return Float.intBitsToFloat(DATA.DATA.toInt());
    }

    class DATA extends SubRecord {
	SubData DATA = new SubData(Type.DATA);
	SubStringPointer DATAs = new SubStringPointer(Type.DATA, SubStringPointer.Files.STRINGS);

	DATA () {
	    super(Type.DATA);
	    DATAs.forceExport = true;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    switch (getType()) {
		case String:
		    DATAs.export(out, srcMod);
		    break;
		default:
		    DATA.export(out, srcMod);
	    }
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    switch (getType()) {
		case String:
		    DATAs.parseData(in);
		    break;
		default:
		    DATA.parseData(in);
	    }
	}

	@Override
	int getContentLength(Mod srcMod) {
	    switch(getType()) {
		case String:
		    return DATAs.getContentLength(srcMod);
		default:
		    return DATA.getContentLength(srcMod);
	    }
	}

	@Override
	void fetchStringPointers(Mod srcMod, Record r, Map<Files, LFileChannel> streams) throws IOException {
	    DATAs.fetchStringPointers(srcMod, r, streams);
	}

    }
}
