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

    // Static prototypes and definitions
    static final SubPrototype HDPTproto = new SubPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    add(SubString.getNew("MODL", true));
	    add(new SubData("MODT"));
	    add(new AltTextures("MODS"));
	    add(new SubData("DATA"));
	    add(new SubInt("PNAM"));
	    add(new SubList<>(new SubForm("HNAM")));
	    add(new SubList<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt("NAM0"));
		    add(SubString.getNew("NAM1", true));
		}
	    })));
	    add(new SubForm("CNAM"));
	    add(new SubForm("TNAM"));
	    add(new SubForm("RNAM"));
	}
    };

    // Common Functions
    HDPT() {
	super();
	subRecords.setPrototype(HDPTproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("HDPT");
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
	subRecords.setSubString("MODL", path);
    }

    /**
     *
     * @return
     */
    public String getModel() {
	return subRecords.getSubString("MODL").print();
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getHeadParts() {
	return SubList.subFormToPublic(subRecords.getSubList("HNAM"));
    }

    /**
     *
     * @param id
     */
    public void addHeadPart(FormID id) {
	subRecords.getSubList("HNAM").add(new SubForm("HNAM", id));
    }

    /**
     *
     * @param id
     */
    public void removeHeadPart(FormID id) {
	subRecords.getSubList("HNAM").remove(new SubForm("HNAM", id));
    }

    /**
     *
     */
    public void clearHeadParts() {
	subRecords.getSubList("HNAM").clear();
    }

    /**
     *
     * @param txst
     */
    public void setBaseTexture(FormID txst) {
	subRecords.setSubForm("TNAM", txst);
    }

    /**
     *
     * @return
     */
    public FormID getBaseTexture() {
	return subRecords.getSubForm("TNAM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setResourceList(FormID id) {
	subRecords.setSubForm("RNAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getResourceList() {
	return subRecords.getSubForm("RNAM").getForm();
    }

    /**
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return ((AltTextures) subRecords.get("MODS")).altTextures;
    }

    /**
     *
     * @param rhs Other MISC record.
     * @return true if:<br> Both sets are empty.<br> or <br> Each set contains
     * matching Alt Textures with the same name and TXST formID reference, in
     * the same corresponding indices.
     */
    public boolean equalAltTextures(MISC rhs) {
	return AltTextures.equal(getAltTextures(), rhs.getAltTextures());
    }
}
