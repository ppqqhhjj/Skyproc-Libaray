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

    static final SubRecordsPrototype prototype = new SubRecordsPrototype(MajorRecordNamed.namedProto);
    static {
	prototype.add(new ScriptPackage());
	prototype.add(new SubData(Type.OBND));
	prototype.reposition(Type.FULL);
	prototype.add(new SubString(Type.MODL, true));
	prototype.add(new SubData(Type.MODT));
	prototype.add(new AltTextures(Type.MODS));
	prototype.add(new SubString(Type.ICON, true));
	prototype.add(new SubForm(Type.YNAM));
	prototype.add(new SubForm(Type.ZNAM));
	prototype.add(new KeywordSet());
	prototype.add(new DATA());
	prototype.add(new SubString(Type.MICO, true));
	prototype.add(new DestructionData());
    }
    static Type[] types = new Type[] { Type.MISC };

    MISC() {
	super();
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

	DATA () {
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

    public String getModel() {
	return subRecords.getSubString(Type.MODL).print();
    }

    public void setModel(String path) {
	subRecords.getSubString(Type.MODL).setString(path);
    }

    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
    }

    public void setValue (int value) {
	getDATA().value = value;
    }

    public int getValue () {
	return getDATA().value;
    }

    public void setWeight(float weight) {
	getDATA().weight = weight;
    }

    public float getWeight () {
	return getDATA().weight;
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

    public void setInventoryImage(String path) {
	subRecords.setSubString(Type.ICON, path);
    }

    public String getInventoryImage() {
	return subRecords.getSubString(Type.ICON).print();
    }

    public void setMessageImage(String path) {
	subRecords.setSubString(Type.MICO, path);
    }

    public String getMessageImage () {
	return subRecords.getSubString(Type.MICO).print();
    }
}
