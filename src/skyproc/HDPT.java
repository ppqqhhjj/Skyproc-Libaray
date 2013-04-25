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
	    add(new Model());
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
     * @deprecated use getModelData()
     * @param path
     */
    public void setModel(String path) {
	subRecords.getModel().setFileName(path);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModel() {
	return subRecords.getModel().getFileName();
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getHeadParts() {
	return subRecords.getSubList("HNAM").toPublic();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param id
     */
    public void addHeadPart(FormID id) {
	subRecords.getSubList("HNAM").add(id);
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param id
     */
    public void removeHeadPart(FormID id) {
	subRecords.getSubList("HNAM").remove(id);
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
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
     * @deprecated use getModelData()
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return subRecords.getModel().getAltTextures();
    }

    public Model getModelData() {
	return subRecords.getModel();
    }
}
