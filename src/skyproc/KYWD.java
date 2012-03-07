/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class KYWD extends MajorRecord {

    private static final Type[] type = {Type.KYWD};

    SubData color = new SubData(Type.CNAM);

    KYWD () {
	super();
	subRecords.add(color);
    }

    /**
     * 
     * @param modToOriginateFrom
     * @param edid EDID to assign the record.  Make sure it's unique.
     * @param color Color to have the keyword highlight as.
     */
    public KYWD (Mod modToOriginateFrom, String edid, int color) {
	this(modToOriginateFrom, edid);
	this.color.setData(color);
    }

    /**
     * 
     * @param modToOriginateFrom
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public KYWD (Mod modToOriginateFrom, String edid) {
	super(modToOriginateFrom, edid);
	EDID.setString(edid);
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
