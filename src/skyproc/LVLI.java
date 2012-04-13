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
