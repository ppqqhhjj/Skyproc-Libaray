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

    static final SubRecordsPrototype prototype = new SubRecordsPrototype(MajorRecordDescription.descProto);
    static {
	prototype.add(new SubData(Type.OBND));
	prototype.reposition(Type.FULL);
	prototype.add(new ScriptPackage());
	prototype.add(new SubForm(Type.EITM));
	prototype.add(new SubString(Type.MOD2, true));
	prototype.add(new SubData(Type.MO2T));
	prototype.add(new SubData(Type.MO2S));
	prototype.add(new SubString(Type.MOD4, true));
	prototype.add(new SubData(Type.MO4T));
	prototype.add(new SubData(Type.MO4S));
	prototype.add(new BodyTemplate());
	prototype.add(new SubForm(Type.YNAM));
	prototype.add(new SubForm(Type.ZNAM));
	prototype.add(new SubForm(Type.ETYP));
	prototype.add(new SubForm(Type.BIDS));
	prototype.add(new SubForm(Type.BAMT));
	prototype.add(new SubForm(Type.RNAM));
	prototype.add(new KeywordSet());
	prototype.reposition(Type.DESC);
	prototype.add(new SubList<>(new SubForm(Type.MODL)));
	prototype.add(new DATA());
	prototype.add(new SubData(Type.DNAM));
	prototype.add(new SubForm(Type.TNAM));
    }
    private final static Type[] type = {Type.ARMO};

    /**
     * Armor Major Record
     */
    ARMO() {
	super();
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
	    throw new UnsupportedOperationException("Not supported yet.");
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
}
