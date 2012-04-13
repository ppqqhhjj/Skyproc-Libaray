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

    SubForm LVLG = new SubForm(Type.LVLG);

    static Type[] types = {Type.LVLI};

    LVLI () {
	super();
	init();
    }

    /**
     * Creates a new LVLI record with a FormID originating from the mod parameter.
     * @param modToOriginateFrom Mod to mark the LVLI as originating from.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLI(Mod modToOriginateFrom, String edid) {
        super(modToOriginateFrom, edid);
        init();
    }

    final void init() {
	subRecords.add(OBND);
	subRecords.add(LVLD);
	subRecords.add(LVLF);
	subRecords.add(LVLG);
	subRecords.add(entries);
    }


    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new LVLI();
    }

    public void setGlobalForm (FormID id) {
	LVLG.setForm(id);
    }

    public FormID getGlobalForm () {
	return LVLG.getForm();
    }

}
