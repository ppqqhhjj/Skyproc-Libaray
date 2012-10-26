/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class QUST extends MajorRecordNamed {

    static Type[] types = { Type.QUST };

    /**
     * Returns the scripts package of the QUST
     */
    public ScriptPackage scripts = new ScriptPackage();
    SubData DNAM = new SubData(Type.DNAM);
    SubData NEXT = new SubData(Type.NEXT);
    SubData ANAM = new SubData(Type.ANAM);

    QUST () {
	super();
	subRecords.remove(Type.FULL);

	subRecords.add(scripts);
//	subRecords.add(FULL);
	subRecords.add(DNAM);
	subRecords.add(NEXT);
	subRecords.add(ANAM);
    }

    QUST (Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
	DNAM.setData(0x111,12);
	NEXT.forceExport(true);
	ANAM.initialize(4);
    }

    final void init() {
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    
}
