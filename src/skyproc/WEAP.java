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
 * Weapon Records
 *
 * @author Justin Swanson
 */
public class WEAP extends MajorRecordDescription {

    static final SubRecordsPrototype WEAPproto = new SubRecordsPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    add(new SubData(Type.OBND));
	    reposition(Type.FULL);
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new SubForm(Type.EITM));
	    add(new SubData(Type.EAMT));
	    add(new SubData(Type.MODS));
	    add(new SubForm(Type.ETYP));
	    add(new SubForm(Type.BIDS));
	    add(new SubForm(Type.BAMT));
	    add(new KeywordSet());
	    reposition(Type.DESC);
	    add(new SubString(Type.NNAM, true));
	    add(new SubForm(Type.INAM));
	    add(new SubForm(Type.SNAM));
	    add(new SubForm(Type.XNAM));
	    add(new SubForm(Type.WNAM));
	    add(new SubForm(Type.TNAM));
	    add(new SubForm(Type.UNAM));
	    add(new SubForm(Type.NAM9));
	    add(new SubForm(Type.NAM8));
	    add(new DATA());
	    add(new DNAM());
	    add(new CRDT());
	    add(new SubData(Type.VNAM));
	    add(new SubForm(Type.CNAM));
	}
    };
    private final static Type[] type = {Type.WEAP};

    WEAP() {
	super();
	subRecords.prototype = WEAPproto;
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new WEAP();
    }

    static class DNAM extends SubRecord {

	WeaponType wtype;
	byte[] unknown1;
	float speed;
	float reach;
	LFlags flags1 = new LFlags(4);
	float sightFOV;
	byte[] unknown2;
	int vats;
	byte[] unknown3;
	int numProjectiles;
	int embeddedWeapActorValue;
	float minRange;
	float maxRange;
	byte[] unknown5;
	LFlags flags2 = new LFlags(4);
	byte[] unknown6;
	LFlags flags3 = new LFlags(4);
	byte[] unknown7;
	byte[] resist;
	byte[] unknown8;
	float stagger;

	public DNAM() {
	    super(Type.DNAM);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(wtype.ordinal(), 1);
	    out.write(unknown1, 3);
	    out.write(speed);
	    out.write(reach);
	    out.write(flags1.export());
	    out.write(sightFOV);
	    out.write(unknown2, 4);
	    out.write(vats, 1);
	    out.write(unknown3, 1);
	    out.write(numProjectiles, 1);
	    out.write(embeddedWeapActorValue, 1);
	    out.write(minRange);
	    out.write(maxRange);
	    out.write(unknown5, 4);
	    out.write(flags2.export());
	    out.write(unknown6, 24);
	    out.write(flags3.export());
	    out.write(unknown7, 16);
	    out.write(resist, 4);
	    out.write(unknown8, 4);
	    out.write(stagger);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    wtype = WeaponType.values()[in.extractInt(1)];
	    unknown1 = in.extract(3);
	    speed = in.extractFloat();
	    reach = in.extractFloat();
	    flags1.set(in.extract(4));
	    sightFOV = in.extractFloat();
	    unknown2 = in.extract(4);
	    vats = in.extractInt(1);
	    unknown3 = in.extract(1);
	    numProjectiles = in.extractInt(1);
	    embeddedWeapActorValue = in.extractInt(1);
	    minRange = in.extractFloat();
	    maxRange = in.extractFloat();
	    unknown5 = in.extract(4);
	    flags2.set(in.extract(4));
	    unknown6 = in.extract(24);
	    flags3.set(in.extract(4));
	    unknown7 = in.extract(16);
	    resist = in.extract(4);
	    unknown8 = in.extract(4);
	    stagger = in.extractFloat();
	    if (logging()) {
		logSync("", "WType: " + wtype + ", speed: " + speed + ", reach: " + reach);
		logSync("", "SightFOV: " + sightFOV + ", vats: " + vats + ", numProjectiles: " + numProjectiles);
		logSync("", "EmbeddedWeapActorVal: " + embeddedWeapActorValue + ", MinRange: " + minRange + ", MaxRange: " + maxRange);
		logSync("", "stagger: " + stagger + ", Bound: " + get(WeaponFlag.BoundWeapon) + ", Cant Drop: " + get(WeaponFlag.CantDrop));
		logSync("", "Hide Backpack: " + get(WeaponFlag.HideBackpack) + ", Ignore Normal Weapon Resistance: " + get(WeaponFlag.IgnoresNormalWeaponResistance) + ", Minor Crime: " + get(WeaponFlag.MinorCrime));
		logSync("", "NPCs Use Ammo: " + get(WeaponFlag.NPCsUseAmmo) + ", No jam after reload: " + get(WeaponFlag.NoJamAfterReload) + ", Non Hostile: " + get(WeaponFlag.NonHostile));
		logSync("", "Non Playable: " + get(WeaponFlag.NonPlayable) + ", Not used in normal combat: " + get(WeaponFlag.NotUsedInNormalCombat) + ", Player Only: " + get(WeaponFlag.PlayerOnly));
	    }
	}

	public boolean get(WeaponFlag flag) {
	    switch (flag.flagSet) {
		case 0:
		    return flags1.get(flag.value);
		case 1:
		    return flags2.get(flag.value);
		default:
		    return false;
	    }
	}

	public void set(WeaponFlag flag, boolean on) {
	    switch (flag.flagSet) {
		case 0:
		    flags1.set(flag.value, on);
		    break;
		case 1:
		    flags2.set(flag.value, on);
		    break;
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DNAM();
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 100;
	}
    }

    static class DATA extends SubRecord {

	int value = 0;
	float weight = 0;
	int damage = 0;

	public DATA() {
	    super(Type.DATA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(value);
	    out.write(weight);
	    out.write(damage, 2);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	    damage = in.extractInt(2);
	    if (logging()) {
		logSync("", "Value: " + value + ", weight: " + weight + ", damage: " + damage);
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
	    return 10;
	}
    }

    static class CRDT extends SubRecord {

	int critDmg;
	byte[] unknown0;
	float critMult;
	int onDeath;
	byte[] unknown;
	FormID critEffect = new FormID();

	public CRDT() {
	    super(Type.CRDT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(critDmg);
	    out.write(critMult);
	    out.write(onDeath, 1);
	    out.write(unknown, 3);
	    critEffect.export(out);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    critDmg = in.extractInt(2);
	    unknown0 = in.extract(2);
	    critMult = in.extractFloat();
	    onDeath = in.extractInt(1);
	    unknown = in.extract(3);
	    critEffect.setInternal(in.extract(4));
	    if (logging()) {
		logSync("", "critDmg: " + critDmg + ", critMult: " + critMult + ", crit effect: " + critEffect);
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new CRDT();
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 16;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(1);
	    out.add(critEffect);
	    return out;
	}
    }

    /**
     * An enum to represent to Weapon Type options
     */
    public enum WeaponType {

	/**
	 *
	 */
	Projectile,
	/**
	 *
	 */
	OneHSword,
	/**
	 *
	 */
	Dagger,
	/**
	 *
	 */
	OneHAxe,
	/**
	 *
	 */
	OneHBlunt,
	/**
	 *
	 */
	TwoHSword,
	/**
	 *
	 */
	TwoHBluntAxe,
	/**
	 *
	 */
	Bow,
	/**
	 *
	 */
	Staff
    }

    /**
     *
     */
    public enum WeaponFlag {

	/**
	 *
	 */
	IgnoresNormalWeaponResistance(1, 0),
	/**
	 *
	 */
	HideBackpack(4, 0),
	/**
	 *
	 */
	NonPlayable(7, 0),
	/**
	 *
	 */
	CantDrop(3, 0),
	/**
	 *
	 */
	PlayerOnly(0, 1),
	/**
	 *
	 */
	NPCsUseAmmo(1, 1),
	/**
	 *
	 */
	NoJamAfterReload(3, 1),
	/**
	 *
	 */
	MinorCrime(4, 1),
	/**
	 *
	 */
	NotUsedInNormalCombat(6, 1),
	/**
	 *
	 */
	NonHostile(8, 1),
	/**
	 *
	 */
	BoundWeapon(13, 1),;
	int value;
	int flagSet;

	WeaponFlag(int value, int flagSet) {
	    this.value = value;
	    this.flagSet = flagSet;
	}
    }

    // Get /set
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getDATA().value = Math.abs(value);
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
     * @param damage
     */
    public void setDamage(int damage) {
	getDATA().damage = Math.abs(damage) % 0xFFFF;  // can't be more than 2 bytes
    }

    /**
     *
     * @param amount
     */
    public void setEnchantmentCharge(int amount) {
	subRecords.getSubData(Type.EAMT).setDataAbs(amount, 2, 2);
    }

    /**
     *
     * @return
     */
    public int getEnchantmentCharge() {
	return subRecords.getSubData(Type.EAMT).toInt();
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
     * @param id
     */
    public void setEquipmentSlot(FormID id) {
	subRecords.setSubForm(Type.ETYP, id);
    }

    /**
     *
     * @return
     */
    public FormID getEquipmentSlot() {
	return subRecords.getSubForm(Type.ETYP).getForm();
    }

    /**
     *
     * @param id
     */
    public void setImpactSet(FormID id) {
	subRecords.setSubForm(Type.INAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getImpactSet() {
	return subRecords.getSubForm(Type.INAM).getForm();
    }

    /**
     *
     * @param filename
     */
    public void setModelFilename(String filename) {
	subRecords.setSubString(Type.MODL, filename);
    }

    /**
     *
     * @return
     */
    public String getModelFilename() {
	return subRecords.getSubString(Type.MODL).print();
    }

    /**
     *
     * @param id
     */
    public void setSheathSound(FormID id) {
	subRecords.setSubForm(Type.NAM8, id);
    }

    /**
     *
     * @return
     */
    public FormID getSheathSound() {
	return subRecords.getSubForm(Type.NAM8).getForm();
    }

    /**
     *
     * @param id
     */
    public void setDrawSound(FormID id) {
	subRecords.setSubForm(Type.NAM9, id);
    }

    /**
     *
     * @return
     */
    public FormID getDrawSound() {
	return subRecords.getSubForm(Type.NAM9).getForm();
    }

    /**
     *
     * @param id
     */
    public void setSwingSound(FormID id) {
	subRecords.setSubForm(Type.TNAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getSwingSound() {
	return subRecords.getSubForm(Type.TNAM).getForm();
    }

    /**
     *
     * @param id
     */
    public void setBoundWeaponSound(FormID id) {
	subRecords.setSubForm(Type.UNAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getBoundWeaponSound() {
	return subRecords.getSubForm(Type.UNAM).getForm();
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get(Type.DNAM);
    }

    /**
     *
     * @param in
     */
    public void setWeaponType(WeaponType in) {
	getDNAM().wtype = in;
    }

    /**
     *
     * @return
     */
    public WeaponType getWeaponType() {
	return getDNAM().wtype;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(float speed) {
	getDNAM().speed = speed;
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
	return getDNAM().speed;
    }

    /**
     *
     * @param reach
     */
    public void setReach(float reach) {
	getDNAM().reach = reach;
    }

    /**
     *
     * @return
     */
    public float getReach() {
	return getDNAM().reach;
    }

    /**
     *
     * @param fov
     */
    public void setSightFOV(float fov) {
	getDNAM().sightFOV = fov;
    }

    /**
     *
     * @return
     */
    public float getSightFOV() {
	return getDNAM().sightFOV;
    }

    /**
     *
     * @param vats
     */
    public void setVATS(int vats) {
	getDNAM().vats = vats;
    }

    /**
     *
     * @return
     */
    public int getVATS() {
	return getDNAM().vats;
    }

    /**
     *
     * @param numProj
     */
    public void setNumProjectiles(int numProj) {
	getDNAM().numProjectiles = numProj;
    }

    /**
     *
     * @return
     */
    public int getNumProjectiles() {
	return getDNAM().numProjectiles;
    }

    /**
     *
     * @param minRange
     */
    public void setMinRange(float minRange) {
	getDNAM().minRange = minRange;
    }

    /**
     *
     * @return
     */
    public float getMinRange() {
	return getDNAM().minRange;
    }

    /**
     *
     * @param maxRange
     */
    public void setMaxRange(float maxRange) {
	getDNAM().maxRange = maxRange;
    }

    /**
     *
     * @return
     */
    public float getMaxRange() {
	return getDNAM().maxRange;
    }

    /**
     *
     * @param stagger
     */
    public void setStagger(float stagger) {
	getDNAM().stagger = stagger;
    }

    /**
     *
     * @return
     */
    public float getStagger() {
	return getDNAM().stagger;
    }

    CRDT getCRDT() {
	return (CRDT) subRecords.get(Type.CRDT);
    }

    /**
     *
     * @param critDmg
     */
    public void setCritDamage(int critDmg) {
	getCRDT().critDmg = critDmg;
    }

    /**
     *
     * @return
     */
    public int getCritDamage() {
	return getCRDT().critDmg;
    }

    /**
     *
     * @param critMult
     */
    public void setCritMult(float critMult) {
	getCRDT().critMult = critMult;
    }

    /**
     *
     * @return
     */
    public float getCritMult() {
	return getCRDT().critMult;
    }

    /**
     *
     * @param onDeath
     */
    public void setCritEffectOnDeath(boolean onDeath) {
	if (onDeath) {
	    getCRDT().onDeath = 1;
	} else {
	    getCRDT().onDeath = 0;
	}
    }

    /**
     *
     * @return
     */
    public boolean getCritEffectOnDeath() {
	if (getCRDT().onDeath == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     *
     * @param critEffect
     */
    public void setCritEffect(FormID critEffect) {
	getCRDT().critEffect = critEffect;
    }

    /**
     *
     * @return
     */
    public FormID getCritEffect() {
	return getCRDT().critEffect;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(WeaponFlag flag, boolean on) {
	getDNAM().set(flag, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(WeaponFlag flag) {
	return getDNAM().get(flag);
    }

    /**
     *
     * @param weap
     */
    public void setTemplate(FormID weap) {
	subRecords.setSubForm(Type.CNAM, weap);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
	return subRecords.getSubForm(Type.CNAM).getForm();
    }
}
