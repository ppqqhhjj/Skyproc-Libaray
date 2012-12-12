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
public class HDPT extends MajorRecordNamed {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.HDPT}));
    static final SubPrototype HDPTproto = new SubPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new AltTextures(Type.MODS));
	    add(new SubData(Type.DATA));
	    add(new SubInt(Type.PNAM));
	    add(new SubList<>(new SubForm(Type.HNAM)));
	    add(new SubList<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt(Type.NAM0));
		    add(new SubString(Type.NAM1, true));
		}
	    })));
	    add(new SubForm(Type.CNAM));
	    add(new SubForm(Type.TNAM));
	    add(new SubForm(Type.RNAM));
	}
    };

    // Common Functions
    HDPT() {
	super();
	subRecords.setPrototype(HDPTproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new HDPT();
    }

    // Get/Set
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
    public String getModel() {
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
