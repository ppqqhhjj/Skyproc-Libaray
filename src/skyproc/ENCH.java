/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class ENCH extends MagicItem {

    ENIT ENIT = new ENIT();
    static Type[] types = {Type.ENCH};

    ENCH() {
	super();
	init();

	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(ENIT);
	subRecords.add(magicEffects);
	subRecords.add(keywords);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new ENCH();
    }

    class ENIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);
	CastType castType = CastType.ConstantEffect;
	int chargeAmount = 0;
	DeliveryType targetType = DeliveryType.Self;
	EnchantType enchantType = EnchantType.Enchantment;
	float chargeTime = 0;
	FormID baseEnchantment = new FormID();
	FormID wornRestrictions = new FormID();

	ENIT() {
	    super(Type.ENIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(baseCost, 4);
	    out.write(flags.export());
	    out.write(castType.ordinal(), 4);
	    out.write(chargeAmount, 4);
	    out.write(targetType.ordinal(), 4);
	    out.write(enchantType.value, 4);
	    out.write(chargeTime);
	    baseEnchantment.export(out);
	    wornRestrictions.export(out);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    baseCost = in.extractInt(4);
	    flags.set(in.extract(4));
	    castType = CastType.values()[in.extractInt(4)];
	    chargeAmount = in.extractInt(4);
	    targetType = DeliveryType.values()[in.extractInt(4)];
	    enchantType = EnchantType.value(in.extractInt(4));
	    chargeTime = in.extractFloat();
	    baseEnchantment.setInternal(in.extract(4));
	    wornRestrictions.setInternal(in.extract(4));
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 36;
	}

	@Override
	ArrayList<FormID> allFormIDs(boolean deep) {
	    return super.allFormIDs(deep);
	}
    }

    /**
     *
     */
    public enum EnchantType {

	/**
	 *
	 */
	Enchantment(6),
	/**
	 *
	 */
	StaffEnchantment(12);
	int value;

	EnchantType(int value) {
	    this.value = value;
	}

	static EnchantType value(int in) {
	    switch (in) {
		case 12:
		    return StaffEnchantment;
		default:
		    return Enchantment;
	    }
	}
    }

    /**
     *
     */
    public enum ENCHFlag {

	/**
	 *
	 */
	ManualCalc(0),
	/**
	 *
	 */
	ExtendDurationOnRecast(3);
	int value;

	ENCHFlag(int in) {
	    value = in;
	}
    }

    /**
     *
     * @param cost
     */
    public void setBaseCost(int cost) {
	ENIT.baseCost = cost;
    }

    /**
     *
     * @return
     */
    public int getBaseCost() {
	return ENIT.baseCost;
    }

    /**
     *
     * @param in
     * @param on
     */
    public void set(ENCHFlag in, boolean on) {
	ENIT.flags.set(in.value, on);
    }

    /**
     * 
     * @param in
     * @return
     */
    public boolean get(ENCHFlag in) {
	return ENIT.flags.get(in.value);
    }

    /**
     *
     * @param type
     */
    public void setCastType(CastType type) {
	ENIT.castType = type;
    }

    /**
     *
     * @return
     */
    public CastType getCastType() {
	return ENIT.castType;
    }

    /**
     *
     * @param amount
     */
    public void setChargeAmount(int amount) {
	ENIT.chargeAmount = amount;
    }

    /**
     *
     * @return
     */
    public int getChargeAmount() {
	return ENIT.chargeAmount;
    }

    /**
     *
     * @param type
     */
    public void setDeliveryType(DeliveryType type) {
	ENIT.targetType = type;
    }

    /**
     *
     * @return
     */
    public DeliveryType getDeliveryType() {
	return ENIT.targetType;
    }

    /**
     *
     * @param type
     */
    public void setEnchantType(EnchantType type) {
	ENIT.enchantType = type;
    }

    /**
     *
     * @return
     */
    public EnchantType getEnchantType() {
	return ENIT.enchantType;
    }

    /**
     *
     * @param time
     */
    public void setChargeTime(float time) {
	ENIT.chargeTime = time;
    }

    /**
     *
     * @return
     */
    public float getChargeTime() {
	return ENIT.chargeTime;
    }

    /**
     *
     * @param id
     */
    public void setBaseEnchantment(FormID id) {
	ENIT.baseEnchantment = id;
    }

    /**
     *
     * @return
     */
    public FormID getBaseEnchantment () {
	return ENIT.baseEnchantment;
    }

    /**
     *
     * @param id
     */
    public void setWornRestrictions (FormID id) {
	ENIT.wornRestrictions = id;
    }

    /**
     *
     * @return
     */
    public FormID getWornRestrictions () {
	return ENIT.wornRestrictions;
    }
}
