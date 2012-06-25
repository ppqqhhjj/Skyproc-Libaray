/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.zip.DataFormatException;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class GLOB extends MajorRecord {

    SubData FNAM = new SubData(Type.FNAM);
    SubFloat FLTV = new SubFloat(Type.FLTV);

    static Type[] types = { Type.GLOB };

    GLOB () {
	super();

	subRecords.add(FNAM);
	subRecords.add(FLTV);
    }

    @Override
    Record getNew() {
	return new GLOB();
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    public enum GLOBType {
	Short (0x73),
	Long (0x6C),
	Float (0x66);

	int value;

	GLOBType (int value) {
	    this.value = value;
	}
    }

    public GLOBType getType () {
	if ((int)FNAM.data[0] == GLOBType.Short.value) {
	    return GLOBType.Short;
	} else if ((int)FNAM.data[0] == GLOBType.Long.value) {
	    return GLOBType.Long;
	}
	return GLOBType.Float;
    }

    public void setType (GLOBType type) {
	FNAM.data[0] = (byte) type.value;
    }

    @Override
    public ModListing getFormMaster() {
	return super.getFormMaster();
    }

    @Override
    void parseData(LShrinkArray in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in, mask);
	if (SPGlobal.logging()) {
	    logSync("GLOB", "Constant: " + get(MajorFlags.RelatedToShields));
	}
    }

    public float getValue () {
	return FLTV.data;
    }

    public void setValue (Float value) {
	FLTV.data = value;
    }

    public boolean isConstant () {
	return get(MajorFlags.RelatedToShields);
    }

    public void setConstant (boolean on) {
	set(MajorFlags.RelatedToShields, on);
    }
}
