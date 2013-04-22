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
public class CONT extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype CONTprototype = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(SubString.getNew("MODL", true));
	    add(new SubData("MODT"));
	    add(new AltTextures("MODS"));
	    add(new SubData("MODD"));
	    add(new SubListCounted<>("COCT", 4, new ItemListing()));
	    add(new DestructionData());
	    add(new SubData("DATA"));
	    add(new SubForm("SNAM"));
	    add(new SubForm("QNAM"));
	}
    };

    // Common Functions
    CONT() {
	super();
	subRecords.setPrototype(CONTprototype);
    }

    @Override
    Record getNew() {
	return new CONT();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("CONT");
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
    /**
     *
     * @param itemReference
     * @param count
     * @return
     */
    public boolean addItem(FormID itemReference, int count) {
	return subRecords.getSubList("CNTO").add(new ItemListing(itemReference, count));
    }

    /**
     *
     * @param item
     * @return
     */
    public boolean addItem(ItemListing item) {
	return subRecords.getSubList("CNTO").add(item);
    }

    /**
     *
     * @param itemReference
     * @return
     */
    public boolean removeItem(FormID itemReference) {
	return subRecords.getSubList("CNTO").remove(new ItemListing(itemReference));
    }

    /**
     *
     */
    public void clearItems() {
	subRecords.getSubList("CNTO").clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<ItemListing> getItems() {
	return subRecords.getSubList("CNTO").toPublic();
    }

    /**
     *
     * @param target
     * @param replacement
     * @return
     */
    final public int replace(MajorRecord target, MajorRecord replacement) {
	int out = 0;
	FormID targetF = target.getForm();
	FormID replaceF = replacement.getForm();
	for (ItemListing item : getItems()) {
	    if (item.getForm().equals(targetF)) {
		out++;
		item.setForm(replaceF);
	    }
	}
	return out;
    }

    /**
     *
     * @param sound
     */
    public void setOpenSound(FormID sound) {
	subRecords.setSubForm("SNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getOpenSound() {
	return subRecords.getSubForm("SNAM").getForm();
    }

    /**
     *
     * @param sound
     */
    public void setCloseSound(FormID sound) {
	subRecords.setSubForm("QNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getCloseSound() {
	return subRecords.getSubForm("QNAM").getForm();
    }
}
