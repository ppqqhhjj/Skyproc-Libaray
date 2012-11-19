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

    static final SubRecordsPrototype QUSTproto = new SubRecordsPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    reposition(Type.FULL);
	    add(new SubData(Type.DNAM));
	    add(new SubData(Type.NEXT));
	    add(new SubData(Type.ANAM));
	}
    };
    static Type[] types = { Type.QUST };

    QUST () {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    QUST (Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
	subRecords.getSubData(Type.DNAM).setData(0x111,12);
	subRecords.getSubData(Type.NEXT).forceExport(true);
	subRecords.getSubData(Type.ANAM).initialize(4);
    }


    @Override
    Type[] getTypes() {
	return types;
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }
}
