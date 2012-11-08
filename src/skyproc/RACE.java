/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Race Records
 *
 * @author Justin Swanson
 */
public class RACE extends MajorRecordDescription {

    static final SubRecordsPrototype RACEproto = new SubRecordsPrototype(MajorRecordDescription.descProto) {
	@Override
	protected void addRecords() {
	    add(new SubList<>(Type.SPCT, 4, new SubForm(Type.SPLO)));
	    add(new SubForm(Type.WNAM));
	    add(new BodyTemplate());
	    add(new SubData(Type.BOD2));
	    add(new KeywordSet());
	    add(new DATA());
	    SubMarkerSet mfnam = new SubMarkerSet<>(new MFNAMdata(), Type.MNAM, Type.FNAM);
	    mfnam.forceMarkers = true;
	    add(mfnam);
	    SubList mtnms = new SubList<>(new SubString(Type.MTNM, false));
	    mtnms.allowDups = false;
	    add(mtnms);
	    add(new SubFormArray(Type.VTCK, 2));
	    add(new SubFormArray(Type.DNAM, 2));
	    add(new SubFormArray(Type.HCLF, 2));
	    add(new SubData(Type.TINL));
	    add(new SubData(Type.PNAM));
	    add(new SubData(Type.UNAM));
	    add(new SubForm(Type.ATKR));
	    add(new SubList<>(new ATKDpackage()));
	    add(new NAM1());
	    add(new SubForm(Type.GNAM));
	    add(new SubData(Type.NAM2));
	    add(new NAM3());
	    add(new SubForm(Type.NAM4));
	    add(new SubForm(Type.NAM5));
	    add(new SubForm(Type.NAM7));
	    add(new SubForm(Type.ONAM));
	    add(new SubForm(Type.LNAM));
	    add(new SubList<>(new SubString(Type.NAME, true)));
	    add(new SubList<>(new MTYPpackage()));
	    add(new SubData(Type.VNAM));
	    add(new SubList<>(new SubForm(Type.QNAM)));
	    add(new SubForm(Type.UNES));
	    add(new SubList<>(new SubString(Type.PHTN, true)));
	    add(new SubList<>(new SubData(Type.PHWT)));
	    add(new SubForm(Type.WKMV));
	    add(new SubForm(Type.RNMV));
	    add(new SubForm(Type.SWMV));
	    add(new SubForm(Type.FLMV));
	    add(new SubForm(Type.SNMV));
	    add(new SubForm(Type.SPMV));
	    add(new SubList<>(new NAM0()));
	    add(new SubForm(Type.NAM8));
	    add(new SubForm(Type.RNAM));
	}
    };
    private final static Type[] type = {Type.RACE};

    /**
     *
     */
    RACE() {
	super();
	subRecords.prototype = RACEproto;
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new RACE();
    }

    static class NAM1 extends SubShellBulkNumber {

	SubData NAM1 = new SubData(Type.NAM1);
	SubMarkerSet EGT = new SubMarkerSet(new EGTmodel(), Type.MNAM, Type.FNAM);

	public NAM1() {
	    super(Type.NAM1, 9);
	    NAM1.forceExport = true;
	    subRecords.add(NAM1);

	    EGT.forceMarkers = true;
	    subRecords.add(EGT);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new NAM1();
	}
    }

    static class NAM3 extends SubShellBulkNumber {

	SubData NAM3 = new SubData(Type.NAM3);
	SubMarkerSet HKX = new SubMarkerSet(new HKXmodel(), Type.MNAM, Type.FNAM);

	public NAM3() {
	    super(Type.NAM3, 7);
	    NAM3.forceExport = true;
	    subRecords.add(NAM3);

	    subRecords.add(HKX);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new NAM3();
	}
    }

    static class MFNAMdata extends SubShell {

	SubString ANAM = new SubString(Type.ANAM, true);
	SubData MODT = new SubData(Type.MODT);
	private static Type[] types = {Type.ANAM, Type.MODT};

	public MFNAMdata() {
	    super(types);
	    subRecords.add(ANAM);
	    subRecords.add(MODT);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new MFNAMdata();
	}
    }

    static class ATKDpackage extends SubShell {

	SubData ATKD = new SubData(Type.ATKD);
	SubString ATKE = new SubString(Type.ATKE, true);
	private static Type[] types = {Type.ATKD, Type.ATKE};

	public ATKDpackage() {
	    super(types);
	    subRecords.add(ATKD);
	    subRecords.add(ATKE);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ATKDpackage();
	}
    }

    static class EGTmodel extends SubShell {

	SubData INDX = new SubData(Type.INDX);
	SubString MODL = new SubString(Type.MODL, true);
	SubData MODT = new SubData(Type.MODT);
	private static Type[] types = {Type.INDX, Type.MODL, Type.MODT};

	public EGTmodel() {
	    super(types);
	    subRecords.add(INDX);
	    subRecords.add(MODL);
	    subRecords.add(MODT);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new EGTmodel();
	}
    }

    static class HKXmodel extends SubShell {

	SubString MODL = new SubString(Type.MODL, true);
	SubData MODT = new SubData(Type.MODT);
	private static Type[] types = {Type.MODL, Type.MODT};

	public HKXmodel() {
	    super(types);
	    subRecords.add(MODL);
	    subRecords.add(MODT);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new HKXmodel();
	}
    }

    static class MTYPpackage extends SubShell {

	SubForm MTYP = new SubForm(Type.MTYP);
	SubData SPED = new SubData(Type.SPED);
	private static Type[] types = {Type.MTYP, Type.SPED};

	public MTYPpackage() {
	    super(types);
	    subRecords.add(MTYP);
	    subRecords.add(SPED);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new MTYPpackage();
	}
    }

    static class NAM0 extends SubShellBulkType {

	private static Type[] targets = { Type.MNAM, Type.FNAM, Type.INDX, Type.HEAD,
		    Type.MPAI, Type.MPAV,
		    Type.RPRM, Type.RPRF, Type.AHCM, Type.AHCF,
		    Type.FTSM, Type.FTSF, Type.DFTM, Type.DFTF,
		    Type.TINI, Type.TINT, Type.TINP, Type.TIND, Type.TINC, Type.TINV, Type.TIRS};
	SubData NAM0 = new SubData(Type.NAM0);
	SubData MNAM = new SubData(Type.MNAM);
	SubData FNAM = new SubData(Type.FNAM);
	SubList<HEADs> INDXs = new SubList<>(new HEADs());
	SubList<MPAVs> MPAVs = new SubList<>(new MPAVs());
	SubList<SubForm> RPRM = new SubList<>(new SubForm(Type.RPRM));
	SubList<SubForm> RPRF = new SubList<>(new SubForm(Type.RPRF));
	SubList<SubForm> AHCM = new SubList<>(new SubForm(Type.AHCM));
	SubList<SubForm> AHCF = new SubList<>(new SubForm(Type.AHCF));
	SubList<SubForm> FTSM = new SubList<>(new SubForm(Type.FTSM));
	SubList<SubForm> FTSF = new SubList<>(new SubForm(Type.FTSF));
	SubList<SubForm> DFTM = new SubList<>(new SubForm(Type.DFTM));
	SubList<SubForm> DFTF = new SubList<>(new SubForm(Type.DFTF));
	SubList<TINIs> TINIs = new SubList<>(new TINIs());

	public NAM0() {
	    super(Type.NAM0, targets);
	    subRecords.add(NAM0);
	    subRecords.add(MNAM);
	    subRecords.add(FNAM);
	    subRecords.add(INDXs);
	    subRecords.add(MPAVs);
	    subRecords.add(RPRM);
	    subRecords.add(RPRF);
	    subRecords.add(AHCM);
	    subRecords.add(AHCF);
	    subRecords.add(FTSM);
	    subRecords.add(FTSF);
	    subRecords.add(DFTM);
	    subRecords.add(DFTF);
	    subRecords.add(TINIs);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new NAM0();
	}

	@Override
	Boolean isValid() {
	    return true;
	}
    }

    static class HEADs extends SubShell {

	SubData INDX = new SubData(Type.INDX);
	SubData HEAD = new SubData(Type.HEAD);
	private static Type[] types = {Type.INDX, Type.HEAD};

	public HEADs() {
	    super(types);
	    subRecords.add(INDX);
	    subRecords.add(HEAD);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new HEADs();
	}

	@Override
	Boolean isValid() {
	    return true;
	}
    }

    static class MPAVs extends SubShell {

	SubData MPAI = new SubData(Type.MPAI);
	SubData MPAV = new SubData(Type.MPAV);
	private static Type[] types = {Type.MPAI, Type.MPAV};

	public MPAVs() {
	    super(types);
	    subRecords.add(MPAI);
	    subRecords.add(MPAV);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new MPAVs();
	}

	@Override
	Boolean isValid() {
	    return true;
	}
    }

    static class TINIs extends SubShell {

	SubData TINI = new SubData(Type.TINI);
	SubString TINT = new SubString(Type.TINT, true);
	SubData TINP = new SubData(Type.TINP);
	SubForm TIND = new SubForm(Type.TIND);
	SubList<TINCs> TINCs = new SubList<TINCs>(new TINCs());
	private static Type[] types = {Type.TINI, Type.TINT, Type.TINP, Type.TIND, Type.TINC, Type.TINV, Type.TIRS};

	public TINIs() {
	    super(types);
	    subRecords.add(TINI);
	    subRecords.add(TINT);
	    subRecords.add(TINP);
	    subRecords.add(TIND);
	    subRecords.add(TINCs);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new TINIs();
	}

	@Override
	Boolean isValid() {
	    return true;
	}
    }

    static class TINCs extends SubShell {

	SubData TINC = new SubData(Type.TINC);
	SubData TINV = new SubData(Type.TINV);
	SubData TIRS = new SubData(Type.TIRS);
	private static Type[] types = {Type.TINC, Type.TINV, Type.TIRS};

	public TINCs() {
	    super(types);
	    subRecords.add(TINC);
	    subRecords.add(TINV);
	    subRecords.add(TIRS);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new TINCs();
	}

	@Override
	Boolean isValid() {
	    return true;
	}
    }

    static class DATA extends SubRecord {

	byte[] fluff1 = new byte[16];
	float maleHeight = 0;
	float femaleHeight = 0;
	float maleWeight = 0;
	float femaleWeight = 0;
	LFlags flags = new LFlags(4);
	float startingHealth = 0;
	float startingMagicka = 0;
	float startingStamina = 0;
	float baseCarryWeight = 0;
	float baseMass = 0;
	float accelerationRate = 0;
	float decelerationRate = 0;
	Size size = Size.MEDIUM;
	byte[] fluff3 = new byte[8];
	float injuredHealthPct = 0;
	byte[] fluff4 = new byte[4];
	float healthRegen = 0;
	float magickaRegen = 0;
	float staminaRegen = 0;
	float unarmedDamage = 0;
	float unarmedReach = 0;
	byte[] fluff5 = new byte[4];
	float aimAngleTolerance = 0;
	float flightRadius = 0;
	float angularAcceleration = 0;
	float angularTolerance = 0;
	byte[] fluff6 = new byte[4];

	DATA() {
	    super(Type.DATA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(fluff1, 16);
	    out.write(maleHeight);
	    out.write(femaleHeight);
	    out.write(maleWeight);
	    out.write(femaleWeight);
	    out.write(flags.export(), 4);
	    out.write(startingHealth);
	    out.write(startingMagicka);
	    out.write(startingStamina);
	    out.write(baseCarryWeight);
	    out.write(baseMass);
	    out.write(accelerationRate);
	    out.write(decelerationRate);
	    out.write(size.ordinal(), 4);
	    out.write(fluff3, 8);
	    out.write(injuredHealthPct);
	    out.write(fluff4, 4);
	    out.write(healthRegen);
	    out.write(magickaRegen);
	    out.write(staminaRegen);
	    out.write(unarmedDamage);
	    out.write(unarmedReach);
	    out.write(fluff5, 4);
	    out.write(aimAngleTolerance);
	    out.write(flightRadius);
	    out.write(angularAcceleration);
	    out.write(angularTolerance);
	    out.write(fluff6, 4);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    fluff1 = in.extract(16);
	    maleHeight = in.extractFloat();
	    femaleHeight = in.extractFloat();
	    maleWeight = in.extractFloat();
	    femaleWeight = in.extractFloat();
	    flags.set(in.extract(4));
	    startingHealth = in.extractFloat();
	    startingMagicka = in.extractFloat();
	    startingStamina = in.extractFloat();
	    baseCarryWeight = in.extractFloat();
	    baseMass = in.extractFloat();
	    accelerationRate = in.extractFloat();
	    decelerationRate = in.extractFloat();
	    size = Size.values()[in.extractInt(4)];
	    fluff3 = in.extract(8);
	    injuredHealthPct = in.extractFloat();
	    fluff4 = in.extract(4);
	    healthRegen = in.extractFloat();
	    magickaRegen = in.extractFloat();
	    staminaRegen = in.extractFloat();
	    unarmedDamage = in.extractFloat();
	    unarmedReach = in.extractFloat();
	    fluff5 = in.extract(4);
	    aimAngleTolerance = in.extractFloat();
	    flightRadius = in.extractFloat();
	    angularAcceleration = in.extractFloat();
	    angularTolerance = in.extractFloat();
	    fluff6 = in.extract(4);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 128;
	}
    }

    public static enum RACEFlags {

	Playable,
	FaceGenHead,
	Child,
	TiltFrontBack,
	TiltLeftRight,
	NoShadow,
	Swims,
	Flies,
	Walks,
	Immobile,
	NotPushable,
	NoCombatInWater,
	NoRotatingToHeadTrack,
	DontShowBloodSpray,
	DontShowBloodDecal,
	UsesHeadTrackAnims,
	SpellsAlignWithMagicNode,
	UseWorldRaycastsForFootIK,
	AllowRagdollCollision,
	RegenHPInCombat,
	CantOpenDoors,
	AllowPCDialogue,
	NoKnockdowns,
	AllowPickpocket,
	AlwaysUseProxyController,
	DontShowWeaponBlood,
	OverlayHeadPartList,
	OverrideHeadPartList,
	CanPickupItems,
	AllowMultipleMembraneShaders,
	CanDualWeild,
	AvoidsRoads;
    }

    /**
     *
     */
    public enum Size {

	/**
	 *
	 */
	SMALL,
	/**
	 *
	 */
	MEDIUM,
	/**
	 *
	 */
	LARGE,
	/**
	 *
	 */
	EXTRALARGE,}

    // Get / set
    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
    }

    public void set(RACEFlags flag, boolean on) {
	getDATA().flags.set(flag.ordinal(), on);
    }

    public boolean get(RACEFlags flag) {
	return getDATA().flags.get(flag.ordinal());
    }

    /**
     *
     * @return FormID of the ARMO record that is worn.
     */
    public FormID getWornArmor() {
	return subRecords.getSubForm(Type.WNAM).getForm();
    }

    /**
     *
     * @param id FormID to set the worn ARMO record to.
     */
    public void setWornArmor(FormID id) {
	subRecords.setSubForm(Type.WNAM, id);
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getSpells() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.SPLO));
    }

    /**
     *
     * @param spell
     */
    public void addSpell(FormID spell) {
	subRecords.getSubList(Type.SPLO).add(new SubForm(Type.SPLO, spell));
    }

    /**
     *
     * @param spell
     */
    public void removeSpell(FormID spell) {
	subRecords.getSubList(Type.SPLO).remove(new SubForm(Type.SPLO, spell));
    }

    /**
     *
     */
    public void clearSpells() {
	subRecords.getSubList(Type.SPLO).clear();
    }

    /**
     *
     * @param gender
     * @param model
     */
    public void setModel(Gender gender, String model) {
	SubMarkerSet<MFNAMdata> MFNAM = subRecords.getSubMarker(Type.ANAM);
	switch (gender) {
	    case MALE:
		if (!MFNAM.set.containsKey(Type.MNAM)) {
		    MFNAM.set.put(Type.MNAM, new MFNAMdata());
		}
		MFNAM.set.get(Type.MNAM).ANAM.setString(model);
		break;
	    default:
		if (!MFNAM.set.containsKey(Type.FNAM)) {
		    MFNAM.set.put(Type.FNAM, new MFNAMdata());
		}
		MFNAM.set.get(Type.FNAM).ANAM.setString(model);
		break;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public String getModel(Gender gender) {
	SubMarkerSet<MFNAMdata> MFNAM = subRecords.getSubMarker(Type.ANAM);
	switch (gender) {
	    case MALE:
		if (MFNAM.set.containsKey(Type.MNAM)) {
		    return MFNAM.set.get(Type.MNAM).ANAM.string;
		}
		break;
	    case FEMALE:
		if (MFNAM.set.containsKey(Type.FNAM)) {
		    return MFNAM.set.get(Type.FNAM).ANAM.string;
		}
		break;
	}
	return "";
    }

    /**
     *
     * @param gender
     * @param voice
     */
    public void setVoiceType(Gender gender, FormID voice) {
	SubFormArray VTCK = (SubFormArray) subRecords.get(Type.VTCK);
	switch (gender) {
	    case MALE:
		VTCK.IDs.set(0, voice);
		break;
	    default:
		VTCK.IDs.set(1, voice);
		break;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public FormID getVoiceType(Gender gender) {
	SubFormArray VTCK = (SubFormArray) subRecords.get(Type.VTCK);
	switch (gender) {
	    case MALE:
		return VTCK.IDs.get(0);
	    default:
		return VTCK.IDs.get(1);
	}
    }

    /**
     *
     * @param gender
     * @param color
     */
    public void setHairColor(Gender gender, FormID color) {
	SubFormArray HCLF = (SubFormArray) subRecords.get(Type.HCLF);
	switch (gender) {
	    case MALE:
		HCLF.IDs.set(0, color);
		break;
	    default:
		HCLF.IDs.set(1, color);
		break;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public FormID getHairColor(Gender gender) {
	SubFormArray HCLF = (SubFormArray) subRecords.get(Type.HCLF);
	switch (gender) {
	    case MALE:
		return HCLF.IDs.get(0);
	    default:
		return HCLF.IDs.get(1);
	}
    }

    /**
     *
     * @param gender
     * @param part
     */
    public void setDecapHeadPart(Gender gender, FormID part) {
	SubFormArray DNAM = (SubFormArray) subRecords.get(Type.DNAM);
	switch (gender) {
	    case MALE:
		DNAM.IDs.set(0, part);
		break;
	    default:
		DNAM.IDs.set(1, part);
		break;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public FormID getDecapHeadPart(Gender gender) {
	SubFormArray DNAM = (SubFormArray) subRecords.get(Type.DNAM);
	switch (gender) {
	    case MALE:
		return DNAM.IDs.get(0);
	    default:
		return DNAM.IDs.get(1);
	}
    }

    /**
     *
     * @param gender
     * @param value
     */
    public void setHeight(Gender gender, float value) {
	DATA DATA = getDATA();
	switch (gender) {
	    case MALE:
		DATA.maleHeight = value;
	    case FEMALE:
		DATA.femaleHeight = value;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public float getHeight(Gender gender) {
	DATA DATA = getDATA();
	switch (gender) {
	    case MALE:
		return DATA.maleHeight;
	    default:
		return DATA.femaleHeight;
	}
    }

    /**
     *
     * @return
     */
    public float getAccelerationRate() {
	return getDATA().accelerationRate;
    }

    /**
     *
     * @param accelerationRate
     */
    public void setAccelerationRate(float accelerationRate) {
	getDATA().accelerationRate = accelerationRate;
    }

    /**
     *
     * @return
     */
    public float getAimAngleTolerance() {
	return getDATA().aimAngleTolerance;
    }

    /**
     *
     * @param aimAngleTolerance
     */
    public void setAimAngleTolerance(float aimAngleTolerance) {
	getDATA().aimAngleTolerance = aimAngleTolerance;
    }

    /**
     *
     * @return
     */
    public float getAngularAcceleration() {
	return getDATA().angularAcceleration;
    }

    /**
     *
     * @param angularAcceleration
     */
    public void setAngularAcceleration(float angularAcceleration) {
	getDATA().angularAcceleration = angularAcceleration;
    }

    /**
     *
     * @return
     */
    public float getAngularTolerance() {
	return getDATA().angularTolerance;
    }

    /**
     *
     * @param angularTolerance
     */
    public void setAngularTolerance(float angularTolerance) {
	getDATA().angularTolerance = angularTolerance;
    }

    /**
     *
     * @return
     */
    public float getBaseCarryWeight() {
	return getDATA().baseCarryWeight;
    }

    /**
     *
     * @param baseCarryWeight
     */
    public void setBaseCarryWeight(float baseCarryWeight) {
	getDATA().baseCarryWeight = baseCarryWeight;
    }

    /**
     *
     * @return
     */
    public float getBaseMass() {
	return getDATA().baseMass;
    }

    /**
     *
     * @param baseMass
     */
    public void setBaseMass(float baseMass) {
	getDATA().baseMass = baseMass;
    }

    /**
     *
     * @return
     */
    public float getDecelerationRate() {
	return getDATA().decelerationRate;
    }

    /**
     *
     * @param decelerationRate
     */
    public void setDecelerationRate(float decelerationRate) {
	getDATA().decelerationRate = decelerationRate;
    }

    /**
     *
     * @return
     */
    public float getFemaleWeight() {
	return getDATA().femaleWeight;
    }

    /**
     *
     * @param femaleWeight
     */
    public void setFemaleWeight(float femaleWeight) {
	getDATA().femaleWeight = femaleWeight;
    }

    /**
     *
     * @return
     */
    public float getFlightRadius() {
	return getDATA().flightRadius;
    }

    /**
     *
     * @param flightRadius
     */
    public void setFlightRadius(float flightRadius) {
	getDATA().flightRadius = flightRadius;
    }

    /**
     *
     * @return
     */
    public float getHealthRegen() {
	return getDATA().healthRegen;
    }

    /**
     *
     * @param healthRegen
     */
    public void setHealthRegen(float healthRegen) {
	getDATA().healthRegen = healthRegen;
    }

    /**
     *
     * @return
     */
    public float getInjuredHealthPct() {
	return getDATA().injuredHealthPct;
    }

    /**
     *
     * @param injuredHealthPct
     */
    public void setInjuredHealthPct(float injuredHealthPct) {
	getDATA().injuredHealthPct = injuredHealthPct;
    }

    /**
     *
     * @return
     */
    public float getMagickaRegen() {
	return getDATA().magickaRegen;
    }

    /**
     *
     * @param magickaRegen
     */
    public void setMagickaRegen(float magickaRegen) {
	getDATA().magickaRegen = magickaRegen;
    }

    /**
     *
     * @return
     */
    public float getMaleHeight() {
	return getDATA().maleHeight;
    }

    /**
     *
     * @param maleHeight
     */
    public void setMaleHeight(float maleHeight) {
	getDATA().maleHeight = maleHeight;
    }

    /**
     *
     * @return
     */
    public float getMaleWeight() {
	return getDATA().maleWeight;
    }

    /**
     *
     * @param maleWeight
     */
    public void setMaleWeight(float maleWeight) {
	getDATA().maleWeight = maleWeight;
    }

    /**
     *
     * @return
     */
    public Size getSize() {
	return getDATA().size;
    }

    /**
     *
     * @param size
     */
    public void setSize(Size size) {
	getDATA().size = size;
    }

    /**
     *
     * @return
     */
    public float getStaminaRegen() {
	return getDATA().staminaRegen;
    }

    /**
     *
     * @param staminaRegen
     */
    public void setStaminaRegen(float staminaRegen) {
	getDATA().staminaRegen = staminaRegen;
    }

    /**
     *
     * @return
     */
    public float getStartingHealth() {
	return getDATA().startingHealth;
    }

    /**
     *
     * @param startingHealth
     */
    public void setStartingHealth(float startingHealth) {
	getDATA().startingHealth = startingHealth;
    }

    /**
     *
     * @return
     */
    public float getStartingMagicka() {
	return getDATA().startingMagicka;
    }

    /**
     *
     * @param startingMagicka
     */
    public void setStartingMagicka(float startingMagicka) {
	getDATA().startingMagicka = startingMagicka;
    }

    /**
     *
     * @return
     */
    public float getStartingStamina() {
	return getDATA().startingStamina;
    }

    /**
     *
     * @param startingStamina
     */
    public void setStartingStamina(float startingStamina) {
	getDATA().startingStamina = startingStamina;
    }

    /**
     *
     * @return
     */
    public float getUnarmedDamage() {
	return getDATA().unarmedDamage;
    }

    /**
     *
     * @param unarmedDamage
     */
    public void setUnarmedDamage(float unarmedDamage) {
	getDATA().unarmedDamage = unarmedDamage;
    }

    /**
     *
     * @return
     */
    public float getUnarmedReach() {
	return getDATA().unarmedReach;
    }

    /**
     *
     * @param unarmedReach
     */
    public void setUnarmedReach(float unarmedReach) {
	getDATA().unarmedReach = unarmedReach;
    }

    /**
     *
     */
    public void clearAttackData() {
	subRecords.getSubList(Type.ATKD).clear();
    }

    /**
     *
     * @return
     */
    public FormID getMaterialType() {
	return subRecords.getSubForm(Type.NAM4).getForm();
    }

    /**
     *
     * @param id
     */
    public void setMaterialType(FormID id) {
	subRecords.setSubForm(Type.NAM4, id);
    }

    /**
     *
     * @return
     */
    public FormID getImpactDataSet() {
	return subRecords.getSubForm(Type.NAM5).getForm();
    }

    /**
     *
     * @param id
     */
    public void setImpactDataSet(FormID id) {
	subRecords.setSubForm(Type.NAM5, id);
    }

    /**
     *
     * @return
     */
    public FormID getDecapitationFX() {
	return subRecords.getSubForm(Type.NAM7).getForm();
    }

    /**
     *
     * @param id
     */
    public void setDecapitationFX(FormID id) {
	subRecords.setSubForm(Type.NAM7, id);
    }

    /**
     *
     * @return
     */
    public FormID getOpenLootSound() {
	return subRecords.getSubForm(Type.ONAM).getForm();
    }

    /**
     *
     * @param id
     */
    public void setOpenLootSound(FormID id) {
	subRecords.setSubForm(Type.ONAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getCloseLootSound() {
	return subRecords.getSubForm(Type.LNAM).getForm();
    }

    /**
     *
     * @param id
     */
    public void setCloseLootSound(FormID id) {
	subRecords.setSubForm(Type.LNAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getUnarmedEquipSlot() {
	return subRecords.getSubForm(Type.UNES).getForm();
    }

    /**
     *
     * @param id
     */
    public void setUnarmedEquipSlot(FormID id) {
	subRecords.setSubForm(Type.UNES, id);
    }

    /**
     *
     */
    public void clearTinting() {
	subRecords.getSubList(Type.NAM0).clear();
    }

    SubMarkerSet<EGTmodel> getEGT() {
	NAM1 nam1 = (NAM1) subRecords.get(Type.NAM1);
	return nam1.EGT;
    }

    /**
     *
     * @param gender
     * @return
     */
    public String getLightingModels(Gender gender) {
	SubMarkerSet<EGTmodel> EGTrecords = getEGT();
	switch (gender) {
	    case MALE:
		return EGTrecords.set.get(Type.MNAM).MODL.string;
	    default:
		return EGTrecords.set.get(Type.FNAM).MODL.string;
	}
    }

    /**
     *
     * @param gender
     * @param s
     */
    public void setLightingModels(Gender gender, String s) {
	SubMarkerSet<EGTmodel> EGTrecords = getEGT();
	switch (gender) {
	    case MALE:
		EGTrecords.set.get(Type.MNAM).MODL.setString(s);
	    default:
		EGTrecords.set.get(Type.FNAM).MODL.setString(s);
	}
    }

    SubMarkerSet<HKXmodel> getHKX() {
	NAM3 nam3 = (NAM3) subRecords.get(Type.NAM3);
	return nam3.HKX;
    }

    /**
     *
     * @param gender
     * @return
     */
    public String getPhysicsModels(Gender gender) {
	SubMarkerSet<HKXmodel> HKXrecords = getHKX();
	switch (gender) {
	    case MALE:
		return HKXrecords.set.get(Type.MNAM).MODL.string;
	    default:
		return HKXrecords.set.get(Type.FNAM).MODL.string;
	}
    }

    /**
     *
     * @param gender
     * @param s
     */
    public void setPhysicsModels(Gender gender, String s) {
	SubMarkerSet<HKXmodel> HKXrecords = getHKX();
	switch (gender) {
	    case MALE:
		HKXrecords.set.get(Type.MNAM).MODL.setString(s);
	    default:
		HKXrecords.set.get(Type.FNAM).MODL.setString(s);
	}
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getEquipSlots() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.QNAM));
    }

    /**
     *
     * @param in
     */
    public void addEquipSlot(FormID in) {
	subRecords.getSubList(Type.QNAM).add(new SubForm(Type.QNAM, in));
    }

    /**
     *
     * @param in
     */
    public void removeEquipSlot(FormID in) {
	subRecords.getSubList(Type.QNAM).remove(new SubForm(Type.QNAM, in));
    }

    /**
     *
     */
    public void clearEquipSlots() {
	subRecords.getSubList(Type.QNAM).clear();
    }

    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }
}
