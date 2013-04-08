/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class INFO extends MajorRecord {

    static final SubPrototype INFOprototype = new SubPrototype(MajorRecord.majorProto) {
	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    add(new SubData("DATA"));
	    add(new ENAM());
	    add(new SubForm("TPIC"));
	    add(new SubForm("PNAM"));
	    add(new SubInt("CNAM", 1));
	    add(new SubList<>(new SubForm("TCLT")));
	    add(new SubForm("DNAM"));
	    add(new SubList<>(new Condition()));
	    add(new SubList<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData("SCHR"));
		    add(new SubForm("QNAM"));
		    add(new SubData("NEXT"));
		}
	    })));
	    add(new SubList<>(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new TRDT());
		    add(new SubStringPointer("NAM1", SubStringPointer.Files.ILSTRINGS));
		    add(new SubString("NAM2"));
		    add(new SubData("NAM3"));
		    add(new SubForm("SNAM"));
		    add(new SubList<>(new Condition()));
		    add(new SubForm("LNAM"));
		}
	    }, false)));
	    add(new SubForm("ANAM"));
	    add(new SubStringPointer("RNAM", SubStringPointer.Files.STRINGS));
	    add(new SubForm("TWAT"));
	    add(new SubForm("ONAM"));
	}
    };

    public static class TRDT extends SubRecordTyped {

	EmotionType emotion = EmotionType.Neutral;
	int emotionValue = 0;
	byte[] fluff = new byte[16];

	public TRDT() {
	    super("TRDT");
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(emotion.ordinal());
	    out.write(emotionValue);
	    out.write(fluff);
	}

	@Override
	void parseData(LChannel in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    emotion = EmotionType.values()[in.extractInt(4)];
	    emotionValue = in.extractInt(4);
	    fluff = in.extract(16);
	}

	@Override
	SubRecord getNew(String type) {
	    return new TRDT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 24;
	}
    }

    public static class ENAM extends SubRecordTyped {

	LFlags flags;
	int hoursReset = 0;

	public ENAM() {
	    super("ENAM");
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export(), 2);
	    out.write(hoursReset, 2);
	}

	@Override
	void parseData(LChannel in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    flags = new LFlags(2);
	    flags.set(in.extract(2));
	    hoursReset = in.extractInt(2);
	}

	@Override
	boolean isValid() {
	    return flags != null;
	}

	@Override
	SubRecord getNew(String type) {
	    return new ENAM();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 4;
	}
    }

    // Common Functions
    INFO() {
	super();
	subRecords.setPrototype(INFOprototype);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("INFO");
    }

    @Override
    Record getNew() {
	return new INFO();
    }
}
