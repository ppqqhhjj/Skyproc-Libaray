/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 * Keyword Records
 * @author Justin Swanson
 */
public class KYWD extends MajorRecord {

    static final SubRecordsPrototype KYWDproto = new SubRecordsPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubData(Type.CNAM));
	}
    };
    private final static Type[] type = {Type.KYWD};

    KYWD () {
	super();
	subRecords.setPrototype(KYWDproto);
    }

    /**
     *
     * @param modToOriginateFrom
     * @param edid EDID to assign the record.  Make sure it's unique.
     * @param color Color to have the keyword highlight as.
     */
    public KYWD (Mod modToOriginateFrom, String edid, int color) {
	this(modToOriginateFrom, edid);
	subRecords.setSubData(Type.CNAM, color);
    }

    /**
     *
     * @param modToOriginateFrom
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public KYWD (Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new KYWD();
    }

}
