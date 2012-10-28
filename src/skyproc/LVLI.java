/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class LVLI extends LeveledRecord {

    static final SubRecordsPrototype LVLIproto = new SubRecordsPrototype(LeveledRecord.LeveledProto){

	@Override
	protected void addRecords() {
	    before(new SubForm(Type.LVLG), Type.LVLO);
	}
    };
    static Type[] types = {Type.LVLI};

    LVLI () {
	super();
	subRecords.prototype = LVLIproto;
    }

    /**
     * Creates a new LVLI record with a FormID originating from the mod parameter.
     * @param modToOriginateFrom Mod to mark the LVLI as originating from.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLI(Mod modToOriginateFrom, String edid) {
        super(modToOriginateFrom, edid);
	subRecords.prototype = LVLIproto;
    }
    
    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new LVLI();
    }

    /**
     *
     * @param id
     */
    public void setGlobalForm (FormID id) {
	subRecords.setSubForm(Type.LVLG, id);
    }

    /**
     * s
     * @return
     */
    public FormID getGlobalForm () {
	return subRecords.getSubForm(Type.LVLG).getForm();
    }

}
