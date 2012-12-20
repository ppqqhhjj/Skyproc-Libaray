/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.AltTextures.AltTexture;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class MISC extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype MISCproto = new SubPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    add(new SubData("OBND"));
	    reposition("FULL");
	    add(SubString.getNew("MODL", true));
	    add(new SubData("MODT"));
	    add(new AltTextures("MODS"));
	    add(SubString.getNew("ICON", true));
	    add(new SubForm("YNAM"));
	    add(new SubForm("ZNAM"));
	    add(new KeywordSet());
	    add(new DATA());
	    add(SubString.getNew("MICO", true));
	    add(new DestructionData());
	}
    };
    static class DATA extends SubRecordTyped {

	int value = 0;
	float weight = 0;

	DATA() {
	    super("DATA");
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(value);
	    out.write(weight);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	    if (SPGlobal.logging()) {
		logSync("", "Setting DATA:    Weight: " + weight);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new MISC.DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
    }

    // Common Functions
    MISC() {
	super();
	subRecords.setPrototype(MISCproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("MISC");
    }

    @Override
    Record getNew() {
	return new MISC();
    }

    // Get/Set
    /**
     *
     * @return
     */
    public String getModel() {
	return subRecords.getSubString("MODL").print();
    }

    /**
     *
     * @param path
     */
    public void setModel(String path) {
	subRecords.getSubString("MODL").setString(path);
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getDATA().value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getDATA().value;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	getDATA().weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return getDATA().weight;
    }

    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTexture> getAltTextures() {
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
     * @param sound
     */
    public void setPickupSound(FormID sound) {
	subRecords.setSubForm("YNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return subRecords.getSubForm("YNAM").getForm();
    }

    /**
     *
     * @param sound
     */
    public void setDropSound(FormID sound) {
	subRecords.setSubForm("ZNAM", sound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm("ZNAM").getForm();
    }

    /**
     *
     * @param path
     */
    public void setInventoryImage(String path) {
	subRecords.setSubString("ICON", path);
    }

    /**
     *
     * @return
     */
    public String getInventoryImage() {
	return subRecords.getSubString("ICON").print();
    }

    /**
     *
     * @param path
     */
    public void setMessageImage(String path) {
	subRecords.setSubString("MICO", path);
    }

    /**
     *
     * @return
     */
    public String getMessageImage() {
	return subRecords.getSubString("MICO").print();
    }
}
