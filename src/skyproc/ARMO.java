/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Armor Records
 * @author Justin Swanson
 */
public class ARMO extends MajorRecordDescription {

    private static final Type[] type = {Type.ARMO};
    SubData OBND = new SubData(Type.OBND);
    /**
     * A script package containing scripts and their properties
     */
    public ScriptPackage scripts = new ScriptPackage();
    SubForm EITM = new SubForm(Type.EITM);
    SubString MOD2 = new SubString(Type.MOD2, true);
    SubData MO2T = new SubData(Type.MO2T);
    SubData MO2S = new SubData(Type.MO2S);
    SubString MOD4 = new SubString(Type.MOD4, true);
    SubData MO4T = new SubData(Type.MO4T);
    SubData MO4S = new SubData(Type.MO4S);
    /**
     *
     */
    public BodyTemplate bodyTemplate = new BodyTemplate();
    SubForm YNAM = new SubForm(Type.YNAM);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    SubForm ETYP = new SubForm(Type.ETYP);
    SubForm BIDS = new SubForm(Type.BIDS);
    SubForm BAMT = new SubForm(Type.BAMT);
    SubForm RNAM = new SubForm(Type.RNAM);
    /**
     *
     */
    public KeywordSet keywords = new KeywordSet();
    SubList<SubForm> MODLs = new SubList<SubForm>(new SubForm(Type.MODL));
    DATA DATA = new DATA();
    SubData DNAM = new SubData(Type.DNAM);
    SubForm TNAM = new SubForm(Type.TNAM);

    /**
     * Armor Major Record
     */
    ARMO() {
        super();
        subRecords.remove(Type.FULL);
        subRecords.remove(Type.DESC);

        subRecords.add(scripts);
        subRecords.add(OBND);
        subRecords.add(FULL);
        subRecords.add(EITM);
        subRecords.add(MOD2);
        subRecords.add(MO2T);
        subRecords.add(MO2S);
        subRecords.add(MOD4);
        subRecords.add(MO4T);
        subRecords.add(MO4S);
        subRecords.add(bodyTemplate);
        subRecords.add(YNAM);
        subRecords.add(ZNAM);
        subRecords.add(ETYP);
        subRecords.add(BIDS);
        subRecords.add(BAMT);
        subRecords.add(RNAM);
        subRecords.add(keywords);
        subRecords.add(description);
        subRecords.add(MODLs);
        subRecords.add(DATA);
        subRecords.add(DNAM);
        subRecords.add(TNAM);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new ARMO();
    }

    class DATA extends SubRecord {

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
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
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
        public void clear() {
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
	return SubList.subFormToPublic(MODLs);
    }

    /**
     *
     * @param id Adds an ARMA record to the MODL list.
     */
    public void addArmature(FormID id) {
        MODLs.add(new SubForm(Type.MODL, id));
    }

    /**
     *
     * @param id Removes an ARMA record from the MODL list if it exists.
     */
    public void removeArmature(FormID id) {
        MODLs.remove(new SubForm(Type.MODL, id));
    }

    /**
     *
     * @param id
     */
    public void setEnchantment(FormID id) {
        EITM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getEnchantment() {
        return EITM.getForm();
    }

    /**
     *
     * @param path
     * @param perspective
     */
    public void setModel(String path, Perspective perspective) {
        switch (perspective) {
            case THIRD_PERSON:
                MOD2.setString(path);
                break;
            case FIRST_PERSON:
                MOD4.setString(path);
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
                return MOD2.print();
            default:
                return MOD4.print();
        }
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

    /**
     *
     * @param slot
     */
    public void setEquipSlot(FormID slot) {
        ETYP.setForm(slot);
    }

    /**
     *
     * @return
     */
    public FormID getEquipSet() {
        return ETYP.getForm();
    }

    /**
     *
     * @param set
     */
    public void setBashImpactData(FormID set) {
        BIDS.setForm(set);
    }

    /**
     *
     * @return
     */
    public FormID getBashImpactData() {
        return BIDS.getForm();
    }

    /**
     *
     * @param race
     */
    public void setRace(FormID race) {
        RNAM.setForm(race);
    }

    /**
     *
     * @return
     */
    public FormID getRace() {
        return RNAM.getForm();
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
        DATA.value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return DATA.value;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
        DATA.weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
        return DATA.weight;
    }

    /**
     *
     * @param rating
     */
    public void setArmorRating(int rating) {
        DNAM.setData(rating * 100);
    }

    /**
     *
     * @return
     */
    public int getArmorRating() {
        return DNAM.toInt() / 100;
    }

    /**
     *
     * @param template
     */
    public void setTemplate(FormID template) {
        TNAM.setForm(template);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
        return TNAM.getForm();
    }
//
//    public SubList<SubForm> getKeywordIDs () {
//	return keywords;
//    }
}
