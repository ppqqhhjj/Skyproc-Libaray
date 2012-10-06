/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Ingredient Records
 * @author Justin Swanson
 */
public class INGR extends MagicItem {

    Type[] types = {Type.INGR};
    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    SubForm YNAM = new SubForm(Type.YNAM);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    DATA DATA = new DATA();
    ENIT ENIT = new ENIT();
    SubString ICON = new SubString(Type.ICON, true);
    SubString MICO = new SubString(Type.MICO, true);
    SubForm ETYP = new SubForm(Type.ETYP);
    /**
     *
     */
    public ScriptPackage scripts = new ScriptPackage();

    INGR() {
	super();
	init();
    }

    @Override
    final void init() {
	super.init();
	subRecords.add(scripts);
	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(keywords);
	subRecords.add(MODL);
	subRecords.add(MODT);
	subRecords.add(YNAM);
	subRecords.add(ZNAM);
	subRecords.add(DATA);
	subRecords.add(ENIT);
	subRecords.add(magicEffects);
	subRecords.add(ICON);
	subRecords.add(MICO);
	subRecords.add(ETYP);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new INGR();
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
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
    }

    class ENIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);

	ENIT () {
	    super(Type.ENIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(baseCost);
	    out.write(flags.export(),4);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    baseCost = in.extractInt(4);
	    flags.set(in.extract(4));
	    if (SPGlobal.logging()) {
		logSync("", "Base cost: " + baseCost + ", flags: " + flags);
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}

    }

    /**
     *
     */
    public enum INGRFlag {
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
	ReferencesPersist(8)
	;

	int value;

	INGRFlag (int value) {
	    this.value = value;
	}
    }

    // Get/set

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
     * @param weight
     */
    public void setWeight (float weight) {
	DATA.weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return DATA.weight;
    }

    public int getValue() {
	return DATA.value;
    }

    public void setValue (int value) {
	DATA.value = value;
    }

    /**
     *
     * @param baseCost
     */
    public void setBaseCost (int baseCost) {
	ENIT.baseCost = baseCost;
    }

    /**
     *
     * @return
     */
    public int getBaseCost () {
	return ENIT.baseCost;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set (INGRFlag flag, boolean on) {
	ENIT.flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get (INGRFlag flag) {
	return ENIT.flags.get(flag.value);
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
