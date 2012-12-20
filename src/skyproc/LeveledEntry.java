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
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class LeveledEntry extends SubShell {

    static final SubPrototype LVLOproto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new LVLOin());
	    add(new COED());
	}
    };

    /**
     *
     * @param id
     * @param level
     * @param count
     */
    public LeveledEntry(FormID id, int level, int count) {
	this();
	setForm(id);
	setLevel(level);
	setCount(count);
    }

    LeveledEntry() {
	super(LVLOproto);
    }

    @Override
    Boolean isValid() {
	return subRecords.isAnyValid();
    }

    @Override
    SubRecord getNew(String type) {
	return new LeveledEntry();
    }

    static class LVLOin extends SubRecordTyped {

	int level = 1;
	FormID entry = new FormID();
	int count = 1;

	LVLOin() {
	    super("LVLO");
	}

	@Override
	SubRecord getNew(String type) {
	    return new LVLOin();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(entry);
	    return out;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(level, 4);
	    entry.export(out);
	    out.write(count, 4);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    level = in.extractInt(4);
	    entry.setInternal(in.extract(4));
	    count = in.extractInt(4);
	}
    }

    static class COED extends SubRecordTyped {

	FormID owner = new FormID();
	int reqRank;
	byte[] fluff;
	boolean valid = false;

	COED() {
	    super("COED");
	}

	@Override
	SubRecord getNew(String type) {
	    return new COED();
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(owner);
	    return out;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    if (isValid()) {
		owner.export(out);
		out.write(reqRank);
		out.write(fluff, 4);
	    }
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    owner.setInternal(in.extract(4));
	    reqRank = in.extractInt(4);
	    fluff = in.extract(4);
	    valid = true;
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}
    }

    LVLOin getEntry() {
	return (LVLOin) subRecords.get("LVLO");
    }

    /**
     *
     * @return The level this entry is marked on the LVLN.
     */
    public int getLevel() {
	return getEntry().level;
    }

    /**
     *
     * @param in The level to mark the entry as on the LVLN.
     */
    final public void setLevel(int in) {
	getEntry().level = in;
    }

    /**
     *
     * @param in The number to set the spawn count to.
     */
    final public void setCount(int in) {
	getEntry().count = in;
    }

    /**
     *
     * @return The spawn counter.
     */
    public int getCount() {
	return getEntry().count;
    }

    /**
     *
     * @param id
     */
    final public void setForm(FormID id) {
	getEntry().entry = id;
    }

    /**
     *
     * @return
     */
    public FormID getForm() {
	return getEntry().entry;
    }
}
