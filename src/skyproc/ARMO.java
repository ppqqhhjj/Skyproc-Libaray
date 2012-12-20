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

    // Static prototypes and definitions
    static final SubPrototype ARMOprototype = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    add(new SubData("OBND"));
	    reposition("FULL");
	    add(new SubForm("EITM"));
	    add(SubString.getNew("MOD2", true));
	    add(new SubData("MO2T"));
	    add(new SubData("MO2S"));
	    add(SubString.getNew("ICON", true));
	    add(SubString.getNew("MOD4", true));
	    add(new SubData("MO4T"));
	    add(new SubData("MO4S"));
	    add(SubString.getNew("ICO2", true));
	    add(new BodyTemplate());
	    add(new DestructionData());
	    add(new SubForm("YNAM"));
	    add(new SubForm("ZNAM"));
	    add(new SubString("BMCT"));
	    add(new SubForm("ETYP"));
	    add(new SubForm("BIDS"));
	    add(new SubForm("BAMT"));
	    add(new SubForm("RNAM"));
	    add(new KeywordSet());
	    reposition("DESC");
	    add(new SubList<>(new SubForm("MODL")));
	    add(new DATA());
	    add(new SubData("DNAM"));
	    add(new SubForm("TNAM"));
	}
    };

    /**
     * Armor Major Record
     */
    ARMO() {
	super();
	subRecords.setPrototype(ARMOprototype);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("ARMO");
    }

    @Override
    Record getNew() {
	return new ARMO();
    }

    static class DATA extends SubRecordTyped {

	int value;
	float weight;

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
	    if (logging()) {
		logSync("", "Value: " + value + ", weight " + weight);
	    }
	}

	@Override
	SubRecord getNew(String type) {
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
	return SubList.subFormToPublic(subRecords.getSubList("MODL"));
    }

    /**
     *
     * @param id Adds an ARMA record to the MODL list.
     */
    public void addArmature(FormID id) {
	subRecords.getSubList("MODL").add(new SubForm("MODL", id));
    }

    /**
     *
     * @param id Removes an ARMA record from the MODL list if it exists.
     */
    public void removeArmature(FormID id) {
	subRecords.getSubList("MODL").remove(new SubForm("MODL", id));
    }

    /**
     *
     * @param id
     */
    public void setEnchantment(FormID id) {
	subRecords.setSubForm("EITM", id);
    }

    /**
     *
     * @return
     */
    public FormID getEnchantment() {
	return subRecords.getSubForm("EITM").getForm();
    }

    /**
     *
     * @param path
     * @param perspective
     */
    public void setModel(String path, Perspective perspective) {
	switch (perspective) {
	    case THIRD_PERSON:
		subRecords.setSubString("MOD2", path);
		break;
	    case FIRST_PERSON:
		subRecords.setSubString("MOD4", path);
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
		return subRecords.getSubString("MOD2").print();
	    default:
		return subRecords.getSubString("MOD4").print();
	}
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
     * @param slot
     */
    public void setEquipSlot(FormID slot) {
	subRecords.setSubForm("ETYP", slot);
    }

    /**
     *
     * @return
     */
    public FormID getEquipSet() {
	return subRecords.getSubForm("ETYP").getForm();
    }

    /**
     *
     * @param set
     */
    public void setBashImpactData(FormID set) {
	subRecords.setSubForm("BIDS", set);
    }

    /**
     *
     * @return
     */
    public FormID getBashImpactData() {
	return subRecords.getSubForm("BIDS").getForm();
    }

    /**
     *
     * @param race
     */
    public void setRace(FormID race) {
	subRecords.setSubForm("RNAM", race);
    }

    /**
     *
     * @return
     */
    public FormID getRace() {
	return subRecords.getSubForm("RNAM").getForm();
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
     * @param rating
     */
    public void setArmorRating(int rating) {
	subRecords.setSubData("DNAM", rating * 100);
    }

    /**
     *
     * @return
     */
    public int getArmorRating() {
	return subRecords.getSubData("DNAM").toInt() / 100;
    }

    /**
     *
     * @param template
     */
    public void setTemplate(FormID template) {
	subRecords.setSubForm("TNAM", template);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
	return subRecords.getSubForm("TNAM").getForm();
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(BodyTemplate.FirstPersonFlags flag, boolean on) {
	subRecords.getBodyTemplate().set(flag, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(BodyTemplate.FirstPersonFlags flag) {
	return subRecords.getBodyTemplate().get(flag);
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(BodyTemplate.GeneralFlags flag, boolean on) {
	subRecords.getBodyTemplate().set(flag, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(BodyTemplate.GeneralFlags flag) {
	return subRecords.getBodyTemplate().get(flag);
    }

    /**
     *
     * @param type
     */
    public void setArmorType(ArmorType type) {
	subRecords.getBodyTemplate().setArmorType(type);
    }

    /**
     *
     * @return
     */
    public ArmorType getArmorType () {
	return subRecords.getBodyTemplate().getArmorType();
    }
}
