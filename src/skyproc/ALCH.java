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
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Alchemy Records
 *
 * @author Justin Swanson
 */
public class ALCH extends MagicItem {

    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    SubForm YNAM = new SubForm(Type.YNAM);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    ENIT ENIT = new ENIT();
    SubFloat DATA = new SubFloat(Type.DATA);
    SubString ICON = new SubString(Type.ICON, true);
    SubString MICO = new SubString(Type.MICO, true);
    SubForm ETYP = new SubForm(Type.ETYP);
    SubData MODS = new SubData(Type.MODS);
    Type[] type = {Type.ALCH};

    ALCH() {
	super();
	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(keywords);
	subRecords.add(MODL);
	subRecords.add(MODT);
	subRecords.add(YNAM);
	subRecords.add(ZNAM);
	subRecords.add(MODS);
	subRecords.add(DATA);
	subRecords.add(ENIT);
	subRecords.add(ICON);
	subRecords.add(MICO);
	subRecords.add(ETYP);
	subRecords.add(magicEffects);
    }
    
    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new ALCH();
    }

    class ENIT extends SubRecord {

	int value;
	LFlags flags = new LFlags(4);
	FormID addiction = new FormID();
	byte[] addictionChance = new byte[4];
	FormID useSound = new FormID();

	ENIT() {
	    super(Type.ENIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(value);
	    out.write(flags.export());
	    addiction.export(out);
	    out.write(addictionChance, 4);
	    useSound.export(out);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    value = in.extractInt(4);
	    flags.set(in.extract(4));
	    addiction.setInternal(in.extract(4));
	    addictionChance = in.extract(4);
	    useSound.setInternal(in.extract(4));
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(2);
	    out.add(addiction);
	    out.add(useSound);
	    return out;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 20;
	}
    }

    /**
     *
     */
    public enum ALCHFlag {

	/**
	 *
	 */
	ManualCalc(0),
	/**
	 *
	 */
	Food(1),
	/**
	 *
	 */
	Medicine(16),
	/**
	 *
	 */
	Poison(17);
	int value;

	ALCHFlag(int in) {
	    value = in;
	}
    }

    // Get / set
    /**
     *
     * @param groundModel
     */
    public void setModel(String groundModel) {
	subRecords.setSubString(Type.MODL, groundModel);
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
     * @param pickupSound
     */
    public void setPickupSound(FormID pickupSound) {
	subRecords.setSubForm(Type.YNAM, pickupSound);
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
     * @param dropSound
     */
    public void setDropSound(FormID dropSound) {
	subRecords.setSubForm(Type.ZNAM, dropSound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm(Type.ZNAM).getForm();
    }

    ENIT getEnit() {
	return (ENIT) subRecords.get(Type.ENIT);
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getEnit().value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getEnit().value;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ALCHFlag flag, boolean on) {
	getEnit().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ALCHFlag flag) {
	return getEnit().flags.get(flag.value);
    }

    /**
     *
     * @param addiction
     */
    public void setAddiction(FormID addiction) {
	getEnit().addiction = addiction;
    }

    /**
     *
     * @return
     */
    public FormID getAddiction() {
	return getEnit().addiction;
    }

    /**
     *
     * @param useSound
     */
    public void setUseSound(FormID useSound) {
	getEnit().useSound = useSound;
    }

    /**
     *
     * @return
     */
    public FormID getUseSound() {
	return getEnit().useSound;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	subRecords.setSubFloat(Type.DATA, weight);
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return subRecords.getSubFloat(Type.DATA).get();
    }

    /**
     *
     * @param filename
     */
    public void setInventoryIcon(String filename) {
	subRecords.setSubString(Type.ICON, filename);
    }

    /**
     *
     * @return
     */
    public String getInventoryIcon() {
	return subRecords.getSubString(Type.ICON).print();
    }

    /**
     *
     * @param filename
     */
    public void setMessageIcon(String filename) {
	subRecords.setSubString(Type.MICO, filename);
    }

    /**
     *
     * @return
     */
    public String getMessageIcon() {
	return subRecords.getSubString(Type.MICO).print();
    }

    /**
     *
     * @param equipType
     */
    public void setEquipType(FormID equipType) {
	subRecords.setSubForm(Type.ETYP, equipType);
    }

    /**
     *
     * @return
     */
    public FormID getEquipType() {
	return subRecords.getSubForm(Type.ETYP).getForm();
    }
}
