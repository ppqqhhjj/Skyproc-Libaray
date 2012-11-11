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
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Armor Records
 *
 * @author Justin Swanson
 */
public class ARMO extends MajorRecordDescription {

    static final SubRecordsPrototype ARMOprototype = new SubRecordsPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    add(new SubData(Type.OBND));
	    reposition(Type.FULL);
	    add(new SubForm(Type.EITM));
	    add(new SubString(Type.MOD2, true));
	    add(new SubData(Type.MO2T));
	    add(new SubData(Type.MO2S));
	    add(new SubString(Type.MOD4, true));
	    add(new SubData(Type.MO4T));
	    add(new SubData(Type.MO4S));
	    add(new BodyTemplate());
	    add(new SubData(Type.BOD2));
	    add(new DestructionData());
	    add(new SubForm(Type.YNAM));
	    add(new SubForm(Type.ZNAM));
	    add(new SubForm(Type.ETYP));
	    add(new SubForm(Type.BIDS));
	    add(new SubForm(Type.BAMT));
	    add(new SubForm(Type.RNAM));
	    add(new KeywordSet());
	    reposition(Type.DESC);
	    add(new SubList<>(new SubForm(Type.MODL)));
	    add(new DATA());
	    add(new SubData(Type.DNAM));
	    add(new SubForm(Type.TNAM));
	}
    };
    private final static Type[] type = {Type.ARMO};

    /**
     * Armor Major Record
     */
    ARMO() {
	super();
	subRecords.prototype = ARMOprototype;
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new ARMO();
    }

    static class DATA extends SubRecord {

	int value;
	float weight;

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
	    if (logging()) {
		logSync("", "Value: " + value + ", weight " + weight);
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
    }

    // Get/Set
    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    /**
     *
     * @return Returns the list of ARMA records associated with the ARMO.
     */
    public ArrayList<FormID> getArmatures() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.MODL));
    }

    /**
     *
     * @param id Adds an ARMA record to the MODL list.
     */
    public void addArmature(FormID id) {
	subRecords.getSubList(Type.MODL).add(new SubForm(Type.MODL, id));
    }

    /**
     *
     * @param id Removes an ARMA record from the MODL list if it exists.
     */
    public void removeArmature(FormID id) {
	subRecords.getSubList(Type.MODL).remove(new SubForm(Type.MODL, id));
    }

    /**
     *
     * @param id
     */
    public void setEnchantment(FormID id) {
	subRecords.setSubForm(Type.EITM, id);
    }

    /**
     *
     * @return
     */
    public FormID getEnchantment() {
	return subRecords.getSubForm(Type.EITM).getForm();
    }

    /**
     *
     * @param path
     * @param perspective
     */
    public void setModel(String path, Perspective perspective) {
	switch (perspective) {
	    case THIRD_PERSON:
		subRecords.setSubString(Type.MOD2, path);
		break;
	    case FIRST_PERSON:
		subRecords.setSubString(Type.MOD4, path);
		break;
	}
    }

    /**
     *
     * @param perspective
     * @return
     */
    public String getModel(Perspective perspective) {
	switch (perspective) {
	    case THIRD_PERSON:
		return subRecords.getSubString(Type.MOD2).print();
	    default:
		return subRecords.getSubString(Type.MOD4).print();
	}
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
     * @param slot
     */
    public void setEquipSlot(FormID slot) {
	subRecords.setSubForm(Type.ETYP, slot);
    }

    /**
     *
     * @return
     */
    public FormID getEquipSet() {
	return subRecords.getSubForm(Type.ETYP).getForm();
    }

    /**
     *
     * @param set
     */
    public void setBashImpactData(FormID set) {
	subRecords.setSubForm(Type.BIDS, set);
    }

    /**
     *
     * @return
     */
    public FormID getBashImpactData() {
	return subRecords.getSubForm(Type.BIDS).getForm();
    }

    /**
     *
     * @param race
     */
    public void setRace(FormID race) {
	subRecords.setSubForm(Type.RNAM, race);
    }

    /**
     *
     * @return
     */
    public FormID getRace() {
	return subRecords.getSubForm(Type.RNAM).getForm();
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
     * @param rating
     */
    public void setArmorRating(int rating) {
	subRecords.setSubData(Type.DNAM, rating * 100);
    }

    /**
     *
     * @return
     */
    public int getArmorRating() {
	return subRecords.getSubData(Type.DNAM).toInt() / 100;
    }

    /**
     *
     * @param template
     */
    public void setTemplate(FormID template) {
	subRecords.setSubForm(Type.TNAM, template);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
	return subRecords.getSubForm(Type.TNAM).getForm();
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }
}
