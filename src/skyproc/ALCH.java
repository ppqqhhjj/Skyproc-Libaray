/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Alchemy Records
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

    ALCH () {
	super();
	init();
    }

    @Override
    final void init() {
	super.init();
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
	    out.write(addictionChance,4);
	    useSound.export(out);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    value = in.extractInt(4);
	    flags.set(in.extract(4));
	    addiction.setInternal(in.extract(4));
	    addictionChance = in.extract(4);
	    useSound.setInternal(in.extract(4));
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	    addiction.standardize(srcMod);
	    useSound.standardize(srcMod);
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
	Poison(17)
	;

	int value;

	ALCHFlag (int in) {
	    value = in;
	}
    }

    // Get / set

    /**
     *
     * @param groundModel
     */
    public void setModel (String groundModel) {
	MODL.setString(groundModel);
    }

    /**
     *
     * @return
     */
    public String getModel () {
	return MODL.string;
    }

    /**
     *
     * @param pickupSound
     */
    public void setPickupSound (FormID pickupSound) {
	YNAM.setForm(pickupSound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound () {
	return YNAM.getForm();
    }

    /**
     *
     * @param dropSound
     */
    public void setDropSound (FormID dropSound) {
	ZNAM.setForm(dropSound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound () {
	return ZNAM.getForm();
    }

    /**
     *
     * @param value
     */
    public void setValue (int value) {
	ENIT.value = value;
    }

    /**
     *
     * @return
     */
    public int getValue () {
	return ENIT.value;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ALCHFlag flag, boolean on) {
	ENIT.flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ALCHFlag flag) {
	return ENIT.flags.get(flag.value);
    }

    /**
     *
     * @param addiction
     */
    public void setAddiction (FormID addiction) {
	ENIT.addiction = addiction;
    }

    /**
     *
     * @return
     */
    public FormID getAddiction () {
	return ENIT.addiction;
    }

    /**
     *
     * @param useSound
     */
    public void setUseSound (FormID useSound) {
	ENIT.useSound = useSound;
    }

    /**
     *
     * @return
     */
    public FormID getUseSound () {
	return ENIT.useSound;
    }

    /**
     *
     * @param weight
     */
    public void setWeight (float weight) {
	DATA.data = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight () {
	return DATA.data;
    }

    /**
     *
     * @param filename
     */
    public void setInventoryIcon (String filename) {
	ICON.setString(filename);
    }

    /**
     *
     * @return
     */
    public String getInventoryIcon () {
	return ICON.print();
    }

    /**
     *
     * @param filename
     */
    public void setMessageIcon (String filename) {
	MICO.setString(filename);
    }

    /**
     *
     * @return
     */
    public String getMessageIcon () {
	return MICO.print();
    }

    /**
     *
     * @param equipType
     */
    public void setEquipType (FormID equipType) {
	ETYP.setForm(equipType);
    }

    /**
     *
     * @return
     */
    public FormID getEquipType () {
	return ETYP.getForm();
    }
}
