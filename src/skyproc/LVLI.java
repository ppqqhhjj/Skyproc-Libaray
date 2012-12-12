/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Justin Swanson
 */
public class LVLI extends LeveledRecord {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.LVLI}));
    static final SubPrototype LVLIproto = new SubPrototype(LeveledRecord.LeveledProto){

	@Override
	protected void addRecords() {
	    before(new SubForm(Type.LVLG), Type.LVLO);
	}
    };

    // Common Functions
    LVLI () {
	super();
	subRecords.setPrototype(LVLIproto);
    }

    /**
     * Creates a new LVLI record with a FormID originating from the mod parameter.
     * @param modToOriginateFrom Mod to mark the LVLI as originating from.
     * @param edid EDID to assign the record.  Make sure it's unique.
     */
    public LVLI(Mod modToOriginateFrom, String edid) {
        super(modToOriginateFrom, edid);
	subRecords.setPrototype(LVLIproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new LVLI();
    }

    // Get/Set
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
