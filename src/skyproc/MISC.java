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

    static final SubRecordsPrototype MISCproto = new SubRecordsPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    add(new SubData(Type.OBND));
	    reposition(Type.FULL);
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new AltTextures(Type.MODS));
	    add(new SubString(Type.ICON, true));
	    add(new SubForm(Type.YNAM));
	    add(new SubForm(Type.ZNAM));
	    add(new KeywordSet());
	    add(new DATA());
	    add(new SubString(Type.MICO, true));
	    add(new DestructionData());
	}
    };
    static Type[] types = new Type[]{Type.MISC};

    MISC() {
	super();
	subRecords.prototype = MISCproto;
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new MISC();
    }

    static class DATA extends SubRecord {

	int value = 0;
	float weight = 0;

	DATA() {
	    super(Type.DATA);
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
	SubRecord getNew(Type type) {
	    return new MISC.DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
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
     * @param path
     */
    public void setModel(String path) {
	subRecords.getSubString(Type.MODL).setString(path);
    }

    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
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
	return ((AltTextures) subRecords.get(Type.MODS)).altTextures;
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
	subRecords.setSubForm(Type.YNAM, sound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return subRecords.getSubForm(Type.YNAM).getForm();
    }

    /**
     *
     * @param sound
     */
    public void setDropSound(FormID sound) {
	subRecords.setSubForm(Type.ZNAM, sound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm(Type.ZNAM).getForm();
    }

    /**
     *
     * @param path
     */
    public void setInventoryImage(String path) {
	subRecords.setSubString(Type.ICON, path);
    }

    /**
     *
     * @return
     */
    public String getInventoryImage() {
	return subRecords.getSubString(Type.ICON).print();
    }

    /**
     *
     * @param path
     */
    public void setMessageImage(String path) {
	subRecords.setSubString(Type.MICO, path);
    }

    /**
     *
     * @return
     */
    public String getMessageImage() {
	return subRecords.getSubString(Type.MICO).print();
    }
}
