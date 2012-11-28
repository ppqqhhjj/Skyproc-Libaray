/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import lev.LFlags;

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
	    add(new DNAM());
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

    static class DNAM extends SubRecord {

	LFlags flags1 = new LFlags(1);
	LFlags flags2 = new LFlags(1);
	int priority = 0;
	byte unknown = 0;
	int unknown2 = 0;
	int questType = 0;
	
	DNAM () {
	    super(Type.DNAM);
	}
	
	@Override
	SubRecord getNew(Type type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}
	
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
