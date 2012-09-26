/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public class HDPT extends MajorRecordNamed {

    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    SubData DATA = new SubData(Type.DATA);
    SubInt PNAM = new SubInt(Type.PNAM);
    SubList<SubForm> HNAMs = new SubList<>(new SubForm(Type.HNAM));
    SubList<NAMs> NAMs = new SubList<>(new NAMs());
    SubForm TNAM = new SubForm(Type.TNAM);
    SubForm RNAM = new SubForm(Type.RNAM);

    static Type[] types = { Type.HDPT };

    HDPT() {
	super();

	subRecords.add(MODL);
	subRecords.add(MODT);
	subRecords.add(DATA);
	subRecords.add(PNAM);
	subRecords.add(HNAMs);
	subRecords.add(NAMs);
	subRecords.add(TNAM);
	subRecords.add(RNAM);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new HDPT();
    }

    static class NAMs extends SubShell {

	SubInt NAM0 = new SubInt(Type.NAM0);
	SubString NAM1 = new SubString(Type.NAM1, true);

	public static Type[] types = { Type.NAM0, Type.NAM1 };

	public NAMs () {
	    super(types);

	    subRecords.add(NAM0);
	    subRecords.add(NAM1);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new NAMs();
	}

    }

    //GetSet
    public void setModel(String path) {
	MODL.setString(path);
    }

    public String getModel () {
	return MODL.print();
    }

    public ArrayList<FormID> getHeadParts() {
	return SubList.subFormToPublic(HNAMs);
    }

    public void addHeadPart(FormID id) {
	HNAMs.add(new SubForm(Type.HNAM, id));
    }

    public void removeHeadPart(FormID id) {
	HNAMs.remove(new SubForm(Type.HNAM, id));
    }

    public void clearHeadParts() {
	HNAMs.clear();
    }

    public void setBaseTexture(FormID txst) {
	TNAM.setForm(txst);
    }

    public FormID getBaseTexture() {
	return TNAM.getForm();
    }

    public void setResourceList(FormID id) {
	RNAM.setForm(id);
    }

    public FormID getResourceList() {
	return RNAM.getForm();
    }
}
