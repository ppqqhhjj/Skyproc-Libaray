/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Plutoman101
 */
public class ECZN extends MajorRecord {

    static final SubRecordsPrototype ECZNproto = new SubRecordsPrototype(MajorRecord.majorProto) {
	@Override
	protected void addRecords() {
	    add(new DATA());
	}
    };
    private final static Type[] type = {Type.ECZN};

    /**
     * Creates a new ECZN record.
     */
    ECZN() {
	super();
	subRecords.prototype = ECZNproto;
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new ECZN();
    }

    static class DATA extends SubRecord implements Serializable {

	private FormID owner = new FormID();
	private FormID location = new FormID();
	private int rank = 0;
	private int minLevel = 0;
	LFlags flags = new LFlags(1);
	private int maxLevel = 0;
	private boolean valid = true;

	DATA() {
	    super(Type.DATA);
	    valid = false;
	}

	DATA(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);

	    owner.setInternal(in.extract(4));
	    location.setInternal(in.extract(4));
	    if (in.isDone()) {
		rank = in.extractInt(1);
		minLevel = in.extractInt(1);
		flags.set(in.extract(1));
		maxLevel = in.extractInt(1);
	    }
	    if (logging()) {
		logSync("", "DATA record: ");
		logSync("", "  " + "Owner: " + owner.getFormStr() + ", Location: " + location.getFormStr());
		logSync("", "  " + "Required Rank: " + rank + ", Minimum Level: " + minLevel);
		logSync("", "  " + "Max Level: " + maxLevel + ", Flags: " + flags);
	    }

	    valid = true;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    if (isValid()) {
		owner.export(out);
		location.export(out);
		out.write(rank, 1);
		out.write(minLevel, 1);
		out.write(flags.export(), 1);
		out.write(maxLevel, 1);
	    }
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    if (isValid()) {
		return 12;
	    } else {
		return 0;
	    }
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(2);
	    out.add(owner);
	    out.add(location);
	    return out;
	}
    }

    /**
     *
     */
    public enum ECZNFlags {

	/**
	 *
	 */
	NeverResets(1),
	/**
	 *
	 */
	MatchPCBelowMin(2),
	/**
	 *
	 */
	DisableCombatBoundary(4),;
	int value;

	ECZNFlags(int value) {
	    this.value = value;
	}
    }

    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ECZNFlags flag) {
	return getDATA().flags.get(flag.value);
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ECZNFlags flag, boolean on) {
	getDATA().flags.set(flag.value, on);
    }

    /**
     *
     * @return
     */
    public FormID getLocation() {
	return getDATA().location;
    }

    /**
     *
     * @param location
     */
    public void setLocation(FormID location) {
	getDATA().location = location;
    }

    /**
     *
     * @return
     */
    public int getMaxLevel() {
	return getDATA().maxLevel;
    }

    /**
     *
     * @param maxLevel
     */
    public void setMaxLevel(int maxLevel) {
	getDATA().maxLevel = maxLevel;
    }

    /**
     *
     * @return
     */
    public int getMinLevel() {
	return getDATA().minLevel;
    }

    /**
     *
     * @param minLevel
     */
    public void setMinLevel(int minLevel) {
	getDATA().minLevel = minLevel;
    }

    /**
     *
     * @return
     */
    public FormID getOwner() {
	return getDATA().owner;
    }

    /**
     *
     * @param owner
     */
    public void setOwner(FormID owner) {
	getDATA().owner = owner;
    }

    /**
     *
     * @return
     */
    public int getRank() {
	return getDATA().rank;
    }

    /**
     *
     * @param rank
     */
    public void setRank(int rank) {
	getDATA().rank = rank;
    }
}
