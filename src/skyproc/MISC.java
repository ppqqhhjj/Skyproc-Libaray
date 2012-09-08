/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LStream;
import skyproc.AltTextures.AltTexture;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class MISC extends MajorRecordNamed {

    SubData OBND = new SubData(Type.OBND);
    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    public ScriptPackage scripts = new ScriptPackage();
    public KeywordSet keywords = new KeywordSet();
    DATA DATA = new DATA();
    AltTextures altTex = new AltTextures(Type.MODS);
    SubForm YNAM = new SubForm(Type.YNAM);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    SubString ICON = new SubString(Type.ICON, true);
    SubString MICO = new SubString(Type.MICO, true);
    DestructionData dest = new DestructionData();

    static Type[] types = new Type[] { Type.MISC };

    MISC() {
	super();
	subRecords.remove(Type.FULL);

	subRecords.add(scripts);
	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(MODL);
	subRecords.add(MODT);
	subRecords.add(altTex);
	subRecords.add(ICON);
	subRecords.add(YNAM);
	subRecords.add(ZNAM);
	subRecords.add(keywords);
	subRecords.add(DATA);
	subRecords.add(MICO);
	subRecords.add(dest);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new MISC();
    }

    class DATA extends SubRecord {

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
	void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
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
	return MODL.print();
    }

    public void setModel(String path) {
	MODL.setString(path);
    }

    public void setValue (int value) {
	DATA.value = value;
    }

    public int getValue () {
	return DATA.value;
    }

    public void setWeight(float weight) {
	DATA.weight = weight;
    }

    public float getWeight () {
	return DATA.weight;
    }

    /**
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTexture> getAltTextures() {
	return altTex.altTextures;
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
	YNAM.setForm(sound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return YNAM.getForm();
    }

    /**
     *
     * @param sound
     */
    public void setDropSound(FormID sound) {
	ZNAM.setForm(sound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return ZNAM.getForm();
    }

    public void setInventoryImage(String path) {
	ICON.setString(path);
    }

    public String getInventoryImage() {
	return ICON.print();
    }

    public void setMessageImage(String path) {
	MICO.setString(path);
    }

    public String getMessageImage () {
	return MICO.print();
    }
}
