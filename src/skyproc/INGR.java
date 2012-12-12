/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Ingredient Records
 *
 * @author Justin Swanson
 */
public class INGR extends MagicItem {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.INGR}));
    static final SubPrototype INGRproto = new SubPrototype(MagicItem.magicItemProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    remove(Type.DESC);
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new AltTextures(Type.MODS));
	    add(new SubForm(Type.YNAM));
	    add(new SubForm(Type.ZNAM));
	    add(new DATA());
	    add(new ENIT());
	    reposition(Type.EFID);
	    add(new SubString(Type.ICON, true));
	    add(new SubString(Type.MICO, true));
	    add(new SubForm(Type.ETYP));
	}
    };

    static class DATA extends SubRecordTyped {

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
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
    }

    static class ENIT extends SubRecordTyped {

	int baseCost = 0;
	LFlags flags = new LFlags(4);

	ENIT() {
	    super(Type.ENIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(baseCost);
	    out.write(flags.export(), 4);
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

    // Enums
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
	ReferencesPersist(8);
	int value;

	INGRFlag(int value) {
	    this.value = value;
	}
    }
    
    // Common Functions
    INGR() {
	super();
	subRecords.setPrototype(INGRproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new INGR();
    }

    // Get/set
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

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

    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
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
    public int getValue() {
	return getDATA().value;
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getDATA().value = value;
    }

    ENIT getENIT() {
	return (ENIT) subRecords.get(Type.ENIT);
    }

    /**
     *
     * @param baseCost
     */
    public void setBaseCost(int baseCost) {
	getENIT().baseCost = baseCost;
    }

    /**
     *
     * @return
     */
    public int getBaseCost() {
	return getENIT().baseCost;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(INGRFlag flag, boolean on) {
	getENIT().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(INGRFlag flag) {
	return getENIT().flags.get(flag.value);
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
