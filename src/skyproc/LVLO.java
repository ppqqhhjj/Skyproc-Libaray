/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class LVLO extends SubShell {

    LVLOin entry = new LVLOin();
    COED COED = new COED();
    static Type[] types = {Type.LVLO, Type.COED};

    /**
     *
     * @param id
     * @param level
     * @param count
     */
    public LVLO(FormID id, int level, int count) {
	this();
	setForm(id);
	setLevel(level);
	setCount(count);
    }

    LVLO() {
	super(types);
	subRecords.add(entry);
	subRecords.add(COED);
    }

    @Override
    Boolean isValid() {
	return entry.isValid();
    }

    @Override
    SubRecord getNew(Type type) {
	return new LVLO();
    }

    class LVLOin extends SubRecord {

	int level = 1;
	FormID entry = new FormID();
	int count = 1;

	LVLOin() {
	    super(Type.LVLO);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new LVLOin();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(1);
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
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    level = in.extractInt(4);
	    entry.setInternal(in.extract(4));
	    count = in.extractInt(4);
	}
    }

    class COED extends SubRecord {

	FormID owner = new FormID();
	int reqRank;
	byte[] fluff;
	boolean valid = false;

	COED() {
	    super(Type.COED);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new COED();
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(1);
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
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
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

    /**
     *
     * @return The level this entry is marked on the LVLN.
     */
    public int getLevel() {
	return entry.level;
    }

    /**
     *
     * @param in The level to mark the entry as on the LVLN.
     */
    final public void setLevel(int in) {
	entry.level = in;
    }

    /**
     *
     * @param in The number to set the spawn count to.
     */
    final public void setCount(int in) {
	entry.count = in;
    }

    /**
     *
     * @return The spawn counter.
     */
    public int getCount() {
	return entry.count;
    }

    /**
     *
     * @param id
     */
    final public void setForm(FormID id) {
	entry.entry = id;
    }

    /**
     *
     * @return
     */
    public FormID getForm() {
	return entry.entry;
    }
}
