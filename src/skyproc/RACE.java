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
 * Race Records
 *
 * @author Justin Swanson
 */
public class RACE extends MajorRecordDescription {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.RACE}));
    static final SubPrototype RACEproto = new SubPrototype(MajorRecordDescription.descProto) {
	@Override
	protected void addRecords() {
	    add(new SubListCounted<>(Type.SPCT, 4, new SubForm(Type.SPLO)));
	    add(new SubForm(Type.WNAM));
	    add(new BodyTemplate());
	    add(new SubData(Type.BOD2));
	    add(new KeywordSet());
	    add(new DATA());
	    SubMarkerSet mfnam = new SubMarkerSet<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(SubString.getNew(Type.ANAM, true));
		    add(new SubData(Type.MODT));
		}
	    }), Type.MNAM, Type.FNAM);
	    mfnam.forceMarkers = true;
	    add(mfnam);
	    add(new SubList<>(SubString.getNew(Type.MTNM, false)));
	    add(new SubFormArray(Type.VTCK, 2));
	    add(new SubFormArray(Type.DNAM, 2));
	    add(new SubFormArray(Type.HCLF, 2));
	    add(new SubData(Type.TINL));
	    add(new SubData(Type.PNAM));
	    add(new SubData(Type.UNAM));
	    add(new SubForm(Type.ATKR));
	    add(new SubList<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData(Type.ATKD));
		    add(SubString.getNew(Type.ATKE, true));
		}
	    })));
	    add(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData(Type.NAM1));
		    forceExport(Type.NAM1);

		    SubMarkerSet EGT = new SubMarkerSet(new SubShell(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubData(Type.INDX));
			    add(SubString.getNew(Type.MODL, true));
			    add(new SubData(Type.MODT));
			}
		    }), Type.MNAM, Type.FNAM);
		    EGT.forceMarkers = true;
		    add(EGT);
		}
	    }, false));
	    add(new SubForm(Type.GNAM));
	    add(new SubData(Type.NAM2));
	    add(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData(Type.NAM3));
		    forceExport(Type.NAM3);
		    add(new SubMarkerSet(new SubShell(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(SubString.getNew(Type.MODL, true));
			    add(new SubData(Type.MODT));
			}
		    }), Type.MNAM, Type.FNAM));
		}
	    }, false));
	    add(new SubForm(Type.NAM4));
	    add(new SubForm(Type.NAM5));
	    add(new SubForm(Type.NAM7));
	    add(new SubForm(Type.ONAM));
	    add(new SubForm(Type.LNAM));
	    add(new SubList<>(SubString.getNew(Type.NAME, true)));
	    add(new SubList<>(new SubShell(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubForm(Type.MTYP));
		    add(new SubData(Type.SPED));
		}
	    })));
	    add(new SubData(Type.VNAM));
	    add(new SubList<>(new SubForm(Type.QNAM)));
	    add(new SubForm(Type.UNES));
	    add(new SubList<>(SubString.getNew(Type.PHTN, true)));
	    add(new SubList<>(new SubData(Type.PHWT)));
	    add(new SubForm(Type.WKMV));
	    add(new SubForm(Type.RNMV));
	    add(new SubForm(Type.SWMV));
	    add(new SubForm(Type.FLMV));
	    add(new SubForm(Type.SNMV));
	    add(new SubForm(Type.SPMV));
	    add(new SubList<>(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData(Type.NAM0));
		    add(new SubData(Type.MNAM));
		    add(new SubData(Type.FNAM));
		    add(new SubList<>(new SubShell(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubData(Type.INDX));
			    add(new SubData(Type.HEAD));
			}
		    })));
		    add(new SubList<>(new SubShell(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubData(Type.MPAI));
			    add(new SubData(Type.MPAV));
			}
		    })));
		    add(new SubList<>(new SubForm(Type.RPRM)));
		    add(new SubList<>(new SubForm(Type.RPRF)));
		    add(new SubList<>(new SubForm(Type.AHCM)));
		    add(new SubList<>(new SubForm(Type.AHCF)));
		    add(new SubList<>(new SubForm(Type.FTSM)));
		    add(new SubList<>(new SubForm(Type.FTSF)));
		    add(new SubList<>(new SubForm(Type.DFTM)));
		    add(new SubList<>(new SubForm(Type.DFTF)));
		    add(new SubList<>(new SubShell(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubData(Type.TINI));
			    add(SubString.getNew(Type.TINT, true));
			    add(new SubData(Type.TINP));
			    add(new SubForm(Type.TIND));
			    add(new SubList<>(new SubShell(new SubPrototype() {
				@Override
				protected void addRecords() {
				    add(new SubData(Type.TINC));
				    add(new SubData(Type.TINV));
				    add(new SubData(Type.TIRS));
				}
			    })));
			}
		    })));
		}
	    }, false)));
	    add(new SubForm(Type.NAM8));
	    add(new SubForm(Type.RNAM));
	}
    };

    static final class DATA extends SubRecordTyped {

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

    // Enums
    /**
     *
     */
    public static enum RACEFlags {

	/**
	 *
	 */
	Playable,
	/**
	 *
	 */
	FaceGenHead,
	/**
	 *
	 */
	Child,
	/**
	 *
	 */
	TiltFrontBack,
	/**
	 *
	 */
	TiltLeftRight,
	/**
	 *
	 */
	NoShadow,
	/**
	 *
	 */
	Swims,
	/**
	 *
	 */
	Flies,
	/**
	 *
	 */
	Walks,
	/**
	 *
	 */
	Immobile,
	/**
	 *
	 */
	NotPushable,
	/**
	 *
	 */
	NoCombatInWater,
	/**
	 *
	 */
	NoRotatingToHeadTrack,
	/**
	 *
	 */
	DontShowBloodSpray,
	/**
	 *
	 */
	DontShowBloodDecal,
	/**
	 *
	 */
	UsesHeadTrackAnims,
	/**
	 *
	 */
	SpellsAlignWithMagicNode,
	/**
	 *
	 */
	UseWorldRaycastsForFootIK,
	/**
	 *
	 */
	AllowRagdollCollision,
	/**
	 *
	 */
	RegenHPInCombat,
	/**
	 *
	 */
	CantOpenDoors,
	/**
	 *
	 */
	AllowPCDialogue,
	/**
	 *
	 */
	NoKnockdowns,
	/**
	 *
	 */
	AllowPickpocket,
	/**
	 *
	 */
	AlwaysUseProxyController,
	/**
	 *
	 */
	DontShowWeaponBlood,
	/**
	 *
	 */
	OverlayHeadPartList,
	/**
	 *
	 */
	OverrideHeadPartList,
	/**
	 *
	 */
	CanPickupItems,
	/**
	 *
	 */
	AllowMultipleMembraneShaders,
	/**
	 *
	 */
	CanDualWeild,
	/**
	 *
	 */
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
	EXTRALARGE;
    }

    // Common Functions
    /**
     *
     */
    RACE() {
	super();
	subRecords.setPrototype(RACEproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new RACE();
    }

    // Get / set
    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(RACEFlags flag, boolean on) {
	getDATA().flags.set(flag.ordinal(), on);
    }

    /**
     *
     * @param flag
     * @return
     */
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
	getMFData(gender).subRecords.setSubString(Type.ANAM, model);
    }

    SubShell getMFData(Gender gender) {
	SubMarkerSet<SubShell> MFNAM = subRecords.getSubMarker(Type.ANAM);
	switch (gender) {
	    case MALE:
		return MFNAM.get(Type.MNAM);
	    default:
		return MFNAM.get(Type.FNAM);
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public String getModel(Gender gender) {
	return getMFData(gender).subRecords.getSubString(Type.ANAM).print();
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

    SubMarkerSet<SubShell> getEGT() {
	return subRecords.getSubShell(Type.NAM1).subRecords.getSubMarker(Type.INDX);
    }

    SubShell getEGTmodel(Gender gender) {
	switch (gender) {
	    case MALE:
		return getEGT().set.get(Type.MNAM);
	    default:
		return getEGT().set.get(Type.FNAM);
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public String getLightingModels(Gender gender) {
	return getEGTmodel(gender).subRecords.getSubString(Type.MODL).print();
    }

    /**
     *
     * @param gender
     * @param s
     */
    public void setLightingModels(Gender gender, String s) {
	getEGTmodel(gender).subRecords.setSubString(Type.MODL, s);
    }

    SubMarkerSet<SubShell> getHKX() {
	return subRecords.getSubShell(Type.NAM3).subRecords.getSubMarker(Type.MODL);
    }

    SubShell getHKXmodel(Gender gender) {
	switch (gender) {
	    case MALE:
		return getHKX().set.get(Type.MNAM);
	    default:
		return getHKX().set.get(Type.FNAM);
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public String getPhysicsModels(Gender gender) {
	return getHKXmodel(gender).subRecords.getSubString(Type.MODL).print();
    }

    /**
     *
     * @param gender
     * @param s
     */
    public void setPhysicsModels(Gender gender, String s) {
	getHKXmodel(gender).subRecords.setSubString(Type.MODL, s);
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

    /**
     *
     * @return
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(BodyTemplate.FirstPersonFlags flag, boolean on) {
	subRecords.getBodyTemplate().set(flag, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(BodyTemplate.FirstPersonFlags flag) {
	return subRecords.getBodyTemplate().get(flag);
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(BodyTemplate.GeneralFlags flag, boolean on) {
	subRecords.getBodyTemplate().set(flag, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(BodyTemplate.GeneralFlags flag) {
	return subRecords.getBodyTemplate().get(flag);
    }

    /**
     *
     * @param type
     */
    public void setArmorType(ArmorType type) {
	subRecords.getBodyTemplate().armorType = type;
    }

    /**
     *
     * @return
     */
    public ArmorType getArmorType() {
	return subRecords.getBodyTemplate().armorType;
    }
}
