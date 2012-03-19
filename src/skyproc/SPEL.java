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
import skyproc.exceptions.NotFound;

/**
 *
 * @author Plutoman101
 */
public class SPEL extends MajorRecordDescription {

    static final Type[] type = {Type.SPEL};
    SubData OBND = new SubData(Type.OBND);
    SubForm MDOB = new SubForm(Type.MDOB);
    SubForm ETYP = new SubForm(Type.ETYP);
    SPIT SPIT = new SPIT();
    SubList<MagicEffectRef> spellSections = new SubList<MagicEffectRef>(new MagicEffectRef());

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new SPEL();
    }

    SPEL() {
	super();
	init();
    }

    public SPEL(Mod modToOriginateFrom, String edid) {
	super(modToOriginateFrom, edid);
	init();
	this.revision = new byte[]{(byte) 0x13, (byte) 0x6F, 0, 0};
	OBND.initialize(12);
	ETYP.getForm().setInternal(new byte[]{(byte) 0x44, (byte) 0x3F, (byte) 0x01, (byte) 0x00});
	SPIT.valid = true;
    }

    final void init() {
	subRecords.remove(Type.FULL);
	subRecords.remove(Type.DESC);

	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(MDOB);
	subRecords.add(ETYP);
	subRecords.add(description);
	subRecords.add(SPIT);
	subRecords.add(spellSections);
    }

    @Override
    void standardizeMasters(Mod srcMod) {
	super.standardizeMasters(srcMod);
	spellSections.standardizeMasters(srcMod);
    }

    static class SPIT extends SubRecord {

	private int baseCost = 0;
	private LFlags flags = new LFlags(4);
	private int baseType = 0;
	private float chargeTime = 0;
	private CastType castType = CastType.ConstantEffect;
	private DeliveryType targetType = DeliveryType.Self;
	private float castDuration = 0;
	private float range = 0;
	private boolean valid = true;
	private SubForm perkType = new SubForm(Type.PERK);

	SPIT() {
	    super(Type.SPIT);
	    valid = false;
	}

	SPIT(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new SPIT();
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);

	    baseCost = in.extractInt(4);
	    flags = new LFlags(in.extract(4));
	    baseType = in.extractInt(4);
	    chargeTime = in.extractFloat();
	    castType = CastType.values()[in.extractInt(4)];
	    targetType = DeliveryType.values()[in.extractInt(4)];
	    castDuration = in.extractFloat();
	    range = in.extractFloat();
	    perkType.setForm(in.extract(4));

	    if (logging()) {
		logSync("", "SPIT record: ");
		logSync("", "  " + "Base Spell Cost: " + baseCost + ", flags: " + flags
			+ ", Base Type: " + baseType + ", Spell Charge Time: " + chargeTime);
		logSync("", "  " + "cast type: " + castType + ", targetType: " + targetType
			+ ", Cast Duration: " + castDuration
			+ ", Spell Range: " + range + ", Perk for Spell: " + perkType.print());
	    }

	    valid = true;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    if (isValid()) {
		out.write(baseCost);
		out.write(flags.export(), 4);
		out.write(baseType);
		out.write(chargeTime);
		out.write(castType.ordinal());
		out.write(targetType.ordinal());
		out.write(castDuration);
		out.write(range);
		out.write(perkType.getFormArray(true), 4);
	    }
	}

	@Override
	public void clear() {
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	    perkType.standardizeMasters(srcMod);
	}

	@Override
	int getContentLength(Mod srcMod) {
	    if (isValid()) {
		return 36;
	    } else {
		return 0;
	    }
	}
    }

    public enum SPELFlag {

	ManualCostCalculation(0),
	PCStartSpell(17),
	AreaEffectIgnoresLOS(19),
	IgnoreResistance(20),
	NoAbsorbOrReflect(21),
	NoDualCastModification(23);
	int value;

	SPELFlag(int valuein) {
	    value = valuein;
	}
    }

    public enum SPELType {

	Spell(0),
	Disease(1),
	Power(2),
	LesserPower(3),
	Ability(4),
	Addition(10),
	Voice(11),
	UNKNOWN(-1);
	int value;

	SPELType(int valuein) {
	    value = valuein;
	}

	static SPELType value(int value) {
	    for (SPELType s : SPELType.values()) {
		if (s.value == value) {
		    return s;
		}
	    }
	    return UNKNOWN;
	}
    }

    // Get Set functions
    public void setInventoryModel(FormID invModel) {
	MDOB.setForm(invModel);
    }

    public FormID getInventoryModel() {
	return MDOB.getForm();
    }

    public void setEquipSlot(FormID equipType) {
	ETYP.setForm(equipType);
    }

    public FormID getEquipSlot() {
	return ETYP.getForm();
    }

    public void setBaseCost(int baseCost) {
	SPIT.baseCost = baseCost;
    }

    public int getBaseCost() {
	return SPIT.baseCost;
    }

    public void set(SPELFlag flag, boolean on) {
	SPIT.flags.set(flag.value, on);
    }

    public boolean get(SPELFlag flag) {
	return SPIT.flags.is(flag.value);
    }

    public void setSpellType(SPELType type) {
	SPIT.baseType = type.value;
    }

    public SPELType getSpellType() {
	return SPELType.value(SPIT.baseType);
    }

    public void setChargeTime (float chargeTime) {
	SPIT.chargeTime = chargeTime;
    }

    public float getChargeTime () {
	return SPIT.chargeTime;
    }

    public void setCastType (CastType type) {
	SPIT.castType = type;
    }

    public CastType getCastType () {
	return SPIT.castType;
    }

    public void setDeliveryType (DeliveryType type) {
	SPIT.targetType = type;
    }

    public DeliveryType getDeliveryType () {
	return SPIT.targetType;
    }

    public void setCastDuration (float duration) {
	SPIT.castDuration = duration;
    }

    public float getCastDuration () {
	return SPIT.castDuration;
    }

    public void setRange (float range) {
	SPIT.range = range;
    }

    public float getRange () {
	return SPIT.range;
    }

    /**
     *
     * @return The PERK ref associated with the SPEL.
     */
    public FormID getPerkRef() {
	return SPIT.perkType.getForm();
    }

    /**
     *
     * @param perkRef FormID to set the SPELs PERK ref to.
     * @throws NotFound This functionality to come. Skyproc does NOT confirm
     * that the FormID associated truly points to a correct record. You will
     * have to confirm the accuracy yourself for now.
     */
    public void setPerkRef(FormID perkRef) throws NotFound {
	SPIT.perkType.setForm(perkRef);
    }

    public ArrayList<MagicEffectRef> getMagicEffects () {
	return spellSections.toPublic();
    }

    public void removeMagicEffect(MagicEffectRef magicEffect) {
	spellSections.remove(magicEffect);
    }

    public void addMagicEffect(MagicEffectRef magicEffect) {
	spellSections.add(magicEffect);
    }

    public void clearMagicEffects () {
	spellSections.clear();
    }

}
