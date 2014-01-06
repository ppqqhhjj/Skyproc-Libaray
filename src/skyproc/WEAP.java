/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Weapon Records
 *
 * @author Justin Swanson
 */
public class WEAP extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype WEAPproto = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    add(new SubData("OBND", new byte[12]));
	    reposition("FULL");
	    add(new Model());
            add(new SubString("ICON"));
            add(new SubString("MICO"));
	    add(new SubForm("EITM"));
	    add(new SubInt("EAMT", 2));
	    add(new SubForm("ETYP"));
	    add(new SubForm("BIDS"));
	    add(new SubForm("BAMT"));
            add(new SubForm("YNAM"));
            add(new SubForm("ZNAM"));
	    add(new KeywordSet());
	    reposition("DESC");
	    add(SubString.getNew("NNAM", true));
	    add(new SubForm("INAM"));
	    add(new SubForm("WNAM"));
	    add(new SubList<>(new SubData("ENAM")));
	    add(new SubForm("SNAM"));
	    add(new SubForm("XNAM"));
            add(new SubForm("NAM7"));
	    add(new SubForm("TNAM"));
	    add(new SubForm("UNAM"));
	    add(new SubForm("NAM9"));
	    add(new SubForm("NAM8"));
	    add(new DATA());
	    add(new DNAM());
	    add(new CRDT());
	    add(new SubData("VNAM"));
	    add(new SubForm("CNAM"));
	}
    };
    static final class DNAM extends SubRecord {

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
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
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
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
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
		logMod(srcMod, "", "WType: " + wtype + ", speed: " + speed + ", reach: " + reach);
		logMod(srcMod, "", "SightFOV: " + sightFOV + ", vats: " + vats + ", numProjectiles: " + numProjectiles);
		logMod(srcMod, "", "EmbeddedWeapActorVal: " + embeddedWeapActorValue + ", MinRange: " + minRange + ", MaxRange: " + maxRange);
		logMod(srcMod, "", "stagger: " + stagger + ", Bound: " + get(WeaponFlag.BoundWeapon) + ", Cant Drop: " + get(WeaponFlag.CantDrop));
		logMod(srcMod, "", "Hide Backpack: " + get(WeaponFlag.HideBackpack) + ", Ignore Normal Weapon Resistance: " + get(WeaponFlag.IgnoresNormalWeaponResistance) + ", Minor Crime: " + get(WeaponFlag.MinorCrime));
		logMod(srcMod, "", "NPCs Use Ammo: " + get(WeaponFlag.NPCsUseAmmo) + ", No jam after reload: " + get(WeaponFlag.NoJamAfterReload) + ", Non Hostile: " + get(WeaponFlag.NonHostile));
		logMod(srcMod, "", "Non Playable: " + get(WeaponFlag.NonPlayable) + ", Not used in normal combat: " + get(WeaponFlag.NotUsedInNormalCombat) + ", Player Only: " + get(WeaponFlag.PlayerOnly));
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
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 100;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DNAM");
	}
    }

    static final class DATA extends SubRecord {

	int value = 0;
	float weight = 0;
	int damage = 0;

	public DATA() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(value);
	    out.write(weight);
	    out.write(damage, 2);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	    damage = in.extractInt(2);
	    if (logging()) {
		logMod(srcMod, "", "Value: " + value + ", weight: " + weight + ", damage: " + damage);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 10;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    static final class CRDT extends SubRecord {

	int critDmg;
	byte[] unknown0;
	float critMult;
	int onDeath;
	byte[] unknown;
	FormID critEffect = new FormID();

	public CRDT() {
	    super();
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(critDmg);
	    out.write(critMult);
	    out.write(onDeath, 1);
	    out.write(unknown, 3);
	    critEffect.export(out);
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
	    critDmg = in.extractInt(2);
	    unknown0 = in.extract(2);
	    critMult = in.extractFloat();
	    onDeath = in.extractInt(1);
	    unknown = in.extract(3);
	    critEffect.parseData(in, srcMod);
	    if (logging()) {
		logMod(srcMod, "", "critDmg: " + critDmg + ", critMult: " + critMult + ", crit effect: " + critEffect);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new CRDT();
	}

	@Override
	boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(ModExporter out) {
	    return 16;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(critEffect);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("CRDT");
	}
    }

    // Enums
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
	Staff,
	/**
	 *
	 */
	Crossbow;
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

    // Common Functions
    WEAP() {
	super();
	subRecords.setPrototype(WEAPproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("WEAP");
    }

    @Override
    Record getNew() {
	return new WEAP();
    }

    // Get /set
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
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
	getDATA().damage = Math.abs(damage);
    }

    /**
     *
     * @return
     */
    public int getDamage() {
	return getDATA().damage;
    }

    /**
     *
     * @param amount
     */
    public void setEnchantmentCharge(int amount) {
	subRecords.setSubInt("EAMT", amount);
    }

    /**
     *
     * @return
     */
    public int getEnchantmentCharge() {
	return subRecords.getSubInt("EAMT").get();
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
     * @param id
     */
    public void setEquipmentSlot(FormID id) {
	subRecords.setSubForm("ETYP", id);
    }

    /**
     *
     * @return
     */
    public FormID getEquipmentSlot() {
	return subRecords.getSubForm("ETYP").getForm();
    }

    /**
     *
     * @param id
     */
    public void setImpactSet(FormID id) {
	subRecords.setSubForm("INAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getImpactSet() {
	return subRecords.getSubForm("INAM").getForm();
    }

    /**
     * @deprecated use getModelData()
     * @param filename
     */
    public void setModelFilename(String filename) {
	subRecords.getModel().setFileName(filename);
    }

    /**
     * @deprecated use getModelData()
     * @return
     */
    public String getModelFilename() {
	return subRecords.getModel().getFileName();
    }

    /**
     *
     * @param id
     */
    public void setSheathSound(FormID id) {
	subRecords.setSubForm("NAM8", id);
    }

    /**
     *
     * @return
     */
    public FormID getSheathSound() {
	return subRecords.getSubForm("NAM8").getForm();
    }

    /**
     *
     * @param id
     */
    public void setDrawSound(FormID id) {
	subRecords.setSubForm("NAM9", id);
    }

    /**
     *
     * @return
     */
    public FormID getDrawSound() {
	return subRecords.getSubForm("NAM9").getForm();
    }

    /**
     *
     * @param id
     */
    public void setSwingSound(FormID id) {
	subRecords.setSubForm("TNAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getSwingSound() {
	return subRecords.getSubForm("TNAM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setBoundWeaponSound(FormID id) {
	subRecords.setSubForm("UNAM", id);
    }

    /**
     *
     * @return
     */
    public FormID getBoundWeaponSound() {
	return subRecords.getSubForm("UNAM").getForm();
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
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
	return (CRDT) subRecords.get("CRDT");
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
	subRecords.setSubForm("CNAM", weap);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
	return subRecords.getSubForm("CNAM").getForm();
    }

    /**
     *
     * @return
     */
    public boolean isTemplated() {
	return !FormID.NULL.equals(getTemplate());
    }

    /**
     * Returns the top of the Weapon Template "chain" and returns the top
     * (the one without any template).
     * Only returns null if a template formID is found, but no record exists with
     *  that formid.
     * @return
     */
    public WEAP getTemplateTop () {
	if (!isTemplated()) {
	    return this;
	} else {
	    WEAP template = (WEAP) SPDatabase.getMajor(getTemplate(), GRUP_TYPE.WEAP);
	    if (template != null) {
		return template.getTemplateTop();
	    }
	}
	return null;
    }

    /**
     * @deprecated use getModelData()
     * @return List of the AltTextures applied.
     */
    public ArrayList<AltTextures.AltTexture> getAltTextures() {
	return subRecords.getModel().getAltTextures();
    }

    /**
     *
     * @return
     */
    public Model getModelData() {
	return subRecords.getModel();
    }

    /**
     * @deprecated use getModelData()
     * @param rhs
     * @return
     */
    public boolean equalAltTextures(WEAP rhs) {
	return AltTextures.equal(getAltTextures(), rhs.getAltTextures());
    }

    /**
     *
     * @return
     */
    public FormID getFirstPersonModel() {
	return subRecords.getSubForm("WNAM").getForm();
    }

    /**
     *
     * @param id
     */
    public void setFirstPersonModel(FormID id) {
	subRecords.setSubForm("WNAM", id);
    }
}
