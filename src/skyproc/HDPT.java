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

    static final SubRecordsPrototype HDPTproto = new SubRecordsPrototype(MajorRecordNamed.namedProto){

	@Override
	protected void addRecords() {
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new SubData(Type.DATA));
	    add(new SubInt(Type.PNAM));
	    add(new SubList<>(new SubForm(Type.HNAM)));
	    add(new SubList<>(new NAMs()));
	    add(new SubForm(Type.CNAM));
	    add(new SubForm(Type.TNAM));
	    add(new SubForm(Type.RNAM));
	}
    };
    static Type[] types = { Type.HDPT };

    HDPT() {
	super();
	subRecords.prototype = HDPTproto;
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
    /**
     *
     * @param path
     */
    public void setModel(String path) {
	subRecords.setSubString(Type.MODL, path);
    }

    /**
     *
     * @return
     */
    public String getModel () {
	return subRecords.getSubString(Type.MODL).print();
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getHeadParts() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.HNAM));
    }

    /**
     *
     * @param id
     */
    public void addHeadPart(FormID id) {
	subRecords.getSubList(Type.HNAM).add(new SubForm(Type.HNAM, id));
    }

    /**
     *
     * @param id
     */
    public void removeHeadPart(FormID id) {
	subRecords.getSubList(Type.HNAM).remove(new SubForm(Type.HNAM, id));
    }

    /**
     *
     */
    public void clearHeadParts() {
	subRecords.getSubList(Type.HNAM).clear();
    }

    /**
     *
     * @param txst
     */
    public void setBaseTexture(FormID txst) {
	subRecords.setSubForm(Type.TNAM, txst);
    }

    /**
     *
     * @return
     */
    public FormID getBaseTexture() {
	return subRecords.getSubForm(Type.TNAM).getForm();
    }

    /**
     *
     * @param id
     */
    public void setResourceList(FormID id) {
	subRecords.setSubForm(Type.RNAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getResourceList() {
	return subRecords.getSubForm(Type.RNAM).getForm();
    }
}
