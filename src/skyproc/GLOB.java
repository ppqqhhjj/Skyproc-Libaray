/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class GLOB extends MajorRecord {

    static final SubRecordsPrototype GLOBproto = new SubRecordsPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    SubData fnam = new SubData(Type.FNAM);
	    fnam.data = new byte[1];
	    add(fnam);
	    add(new SubFloat(Type.FLTV));
	}
    };
    static Type[] types = { Type.GLOB };

    GLOB () {
	super();
	subRecords.setPrototype(GLOBproto);
    }

    /**
     *
     * @param modToOriginateFrom
     * @param edid
     * @param type
     */
    public GLOB(Mod modToOriginateFrom, String edid, GLOBType type) {
	this();
	originateFrom(modToOriginateFrom, edid);
	setType(type);
    }

    /**
     *
     * @param modToOriginateFrom
     * @param edid
     * @param type
     * @param value
     * @param constant
     */
    public GLOB(Mod modToOriginateFrom, String edid, GLOBType type, float value, Boolean constant) {
	this(modToOriginateFrom, edid, type);
	setValue(value);
	setConstant(constant);
    }

    @Override
    Record getNew() {
	return new GLOB();
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    /**
     *
     */
    public enum GLOBType {
	/**
	 *
	 */
	Short (0x73),
	/**
	 *
	 */
	Long (0x6C),
	/**
	 *
	 */
	Float (0x66);

	int value;

	GLOBType (int value) {
	    this.value = value;
	}
    }

    /**
     *
     * @return
     */
    public GLOBType getGLOBType () {
	SubData fnam = subRecords.getSubData(Type.FNAM);
	if ((int)fnam.data[0] == GLOBType.Short.value) {
	    return GLOBType.Short;
	} else if ((int)fnam.data[0] == GLOBType.Long.value) {
	    return GLOBType.Long;
	}
	return GLOBType.Float;
    }

    /**
     *
     * @param type
     */
    final public void setType (GLOBType type) {
	subRecords.getSubData(Type.FNAM).data[0] = (byte) type.value;
    }

    @Override
    public ModListing getFormMaster() {
	return super.getFormMaster();
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	if (SPGlobal.logging()) {
	    logSync("GLOB", "Constant: " + get(MajorFlags.RelatedToShields));
	}
    }

    /**
     *
     * @return
     */
    public float getValue () {
	return subRecords.getSubFloat(Type.FLTV).get();
    }

    /**
     *
     * @param value
     */
    final public void setValue (Float value) {
	subRecords.setSubFloat(Type.FLTV, value);
    }

    /**
     *
     * @param value
     */
    final public void setValue (Boolean value) {
	setValue((float)Integer.valueOf(Ln.convertBoolTo1(value)));
    }

    /**
     *
     * @return
     */
    public boolean isConstant () {
	return get(MajorFlags.RelatedToShields);
    }

    /**
     *
     * @param on
     */
    final public void setConstant (boolean on) {
	set(MajorFlags.RelatedToShields, on);
    }
}
