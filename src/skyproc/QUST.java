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
    
    public ScriptPackage scripts = new ScriptPackage();
    SubData DNAM = new SubData(Type.DNAM);    
    SubData NEXT = new SubData(Type.NEXT);    
    SubData ANAM = new SubData(Type.ANAM);
    
    QUST () {
	super();	
	init();
    }
    
    QUST (Mod modToOriginateFrom, String edid) {
	super(modToOriginateFrom, edid);
	DNAM.setData(0x111,12);
	NEXT.forceExport(true);
	ANAM.initialize(4);
	init();
    }
    
    final void init() {
	subRecords.remove(Type.FULL);
	
	subRecords.add(scripts);
	subRecords.add(FULL);
	subRecords.add(DNAM);
	subRecords.add(NEXT);
	subRecords.add(ANAM);
    }
    
    @Override
    Type[] getTypes() {
	return types;
    }
    
}
