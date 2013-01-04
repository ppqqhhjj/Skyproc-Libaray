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
 * A Magic Effect record
 *
 * @author Justin Swanson
 */
public class MGEF extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype MGEFproto = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    reposition("FULL");
	    add(new SubForm("MDOB"));
	    add(new KeywordSet());
	    add(new DATA());
	    add(new SNDD());
	    SubStringPointer dnam = new SubStringPointer("DNAM", SubStringPointer.Files.STRINGS);
	    dnam.forceExport = true;
	    add(dnam);
	    forceExport("DNAM");
	    remove("DESC");
	    add(new SubForm("ESCE"));
	    add(new SubList<>(new Condition()));
	    add(new SubData("OBND"));
	}
    };

    static class DATA extends SubRecord {

	LFlags flags = new LFlags(4);
	float baseCost = 0;
	FormID relatedID = new FormID();
	ActorValue skillType = ActorValue.UNKNOWN;
	ActorValue resistanceAV = ActorValue.UNKNOWN;
	byte[] unknown = {0x00, 0x00, 0x00, (byte) 0x80};
	FormID lightID = new FormID();
	float taperWeight = 0;
	FormID hitShader = new FormID();
	FormID enchantShader = new FormID();
	int skillLevel = 0;
	int area = 0;
	float castingTime = 0;
	float taperCurve = 0;
	float taperDuration = 0;
	float secondAVWeight = 0;
	int effectType = 0;
	ActorValue primaryAV = ActorValue.Health;
	FormID projectileID = new FormID();
	FormID explosionID = new FormID();
	CastType castType = CastType.ConstantEffect;
	DeliveryType deliveryType = DeliveryType.Self;
	ActorValue secondAV = ActorValue.UNKNOWN;
	FormID castingArt = new FormID();
	FormID hitEffectArt = new FormID();
	FormID impactData = new FormID();
	float skillUsageMult = 0;
	FormID dualCastID = new FormID();
	float dualCastScale = 1;
	FormID enchantArtID = new FormID();
	int nullData = 0;
	int nullData2 = 0;
	FormID equipAbility = new FormID();
	FormID imageSpaceModID = new FormID();
	FormID perkID = new FormID();
	SoundVolume vol = SoundVolume.Normal;
	float scriptAIDataScore = 0;
	float scriptAIDataDelayTime = 0;

	DATA() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export(), 4);
	    out.write(baseCost);
	    relatedID.export(out);
	    out.write(ActorValue.value(skillType));
	    out.write(ActorValue.value(resistanceAV));
	    out.write(unknown, 4);
	    lightID.export(out);
	    out.write(taperWeight);
	    hitShader.export(out);
	    enchantShader.export(out);
	    out.write(skillLevel);
	    out.write(area);
	    out.write(castingTime);
	    out.write(taperCurve);
	    out.write(taperDuration);
	    out.write(secondAVWeight);
	    out.write(effectType);
	    out.write(ActorValue.value(primaryAV));
	    projectileID.export(out);
	    explosionID.export(out);
	    out.write(castType.ordinal());
	    out.write(deliveryType.ordinal());
	    out.write(ActorValue.value(secondAV));
	    castingArt.export(out);
	    hitEffectArt.export(out);
	    impactData.export(out);
	    out.write(skillUsageMult);
	    dualCastID.export(out);
	    out.write(dualCastScale);
	    enchantArtID.export(out);
	    out.write(nullData, 4);
	    out.write(nullData2, 4);
	    equipAbility.export(out);
	    imageSpaceModID.export(out);
	    perkID.export(out);
	    out.write(vol.ordinal());
	    out.write(scriptAIDataScore);
	    out.write(scriptAIDataDelayTime);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    flags.set(in.extract(4));
	    baseCost = in.extractFloat();
	    relatedID.setInternal(in.extract(4));
	    skillType = ActorValue.value(in.extractInt(4));
	    resistanceAV = ActorValue.value(in.extractInt(4));
	    unknown = in.extract(4);
	    lightID.setInternal(in.extract(4));
	    taperWeight = in.extractFloat();
	    hitShader.setInternal(in.extract(4));
	    enchantShader.setInternal(in.extract(4));
	    skillLevel = in.extractInt(4);
	    area = in.extractInt(4);
	    castingTime = in.extractFloat();
	    taperCurve = in.extractFloat();
	    taperDuration = in.extractFloat();
	    secondAVWeight = in.extractFloat();
	    effectType = in.extractInt(4);
	    primaryAV = ActorValue.value(in.extractInt(4));
	    projectileID.setInternal(in.extract(4));
	    explosionID.setInternal(in.extract(4));
	    castType = CastType.values()[in.extractInt(4)];
	    deliveryType = DeliveryType.values()[in.extractInt(4)];
	    secondAV = ActorValue.value(in.extractInt(4));
	    castingArt.setInternal(in.extract(4));
	    hitEffectArt.setInternal(in.extract(4));
	    impactData.setInternal(in.extract(4));
	    skillUsageMult = in.extractFloat();
	    dualCastID.setInternal(in.extract(4));
	    dualCastScale = in.extractFloat();
	    enchantArtID.setInternal(in.extract(4));
	    nullData = in.extractInt(4);
	    nullData2 = in.extractInt(4);
	    equipAbility.setInternal(in.extract(4));
	    imageSpaceModID.setInternal(in.extract(4));
	    perkID.setInternal(in.extract(4));
	    vol = SoundVolume.values()[in.extractInt(4)];
	    scriptAIDataScore = in.extractFloat();
	    scriptAIDataDelayTime = in.extractFloat();
	    print();
	}

	@Override
	public String print() {
	    if (SPGlobal.logging()) {
		logSync("", "DATA:");
		logSync("", "  Flags: " + flags);
		logSync("", "  Base Cost: " + baseCost);
		logSync("", "  Related ID: " + relatedID);
		logSync("", "  skillType: " + skillType);
		logSync("", "  resistanceAV: " + resistanceAV);
		logSync("", "  Light: " + lightID);
		logSync("", "  Taper Weight: " + taperWeight);
		logSync("", "  Hit Shader: " + hitShader);
		logSync("", "  Enchant Shader: " + enchantShader);
		logSync("", "  Skill Level: " + skillLevel);
		logSync("", "  Area: " + area);
		logSync("", "  Casting Time: " + castingTime);
		logSync("", "  Taper Curve: " + taperCurve);
		logSync("", "  Taper Duration: " + taperDuration);
		logSync("", "  second AV weight: " + secondAVWeight);
		logSync("", "  Effect Type: " + effectType);
		logSync("", "  Primary AV: " + primaryAV);
		logSync("", "  Projectile : " + projectileID);
		logSync("", "  Explosion: " + explosionID);
		logSync("", "  Cast Type: " + castType);
		logSync("", "  Delivery Type: " + deliveryType);
		logSync("", "  Second AV: " + secondAV);
		logSync("", "  Casting Art: " + castingArt);
		logSync("", "  Hit Effect Art: " + hitEffectArt);
		logSync("", "  Impact Data: " + impactData);
		logSync("", "  Skill Usage Mult: " + skillUsageMult);
		logSync("", "  Dual Cast ID: " + dualCastID);
		logSync("", "  Dual Cast Scale: " + dualCastScale);
		logSync("", "  Enchant Art: " + enchantArtID);
		logSync("", "  Equip Ability: " + equipAbility);
		logSync("", "  Image Space Mod ID: " + imageSpaceModID);
		logSync("", "  Perk: " + perkID);
		logSync("", "  Volume: " + vol);
		logSync("", "  Script AI Data Score: " + scriptAIDataScore);
		logSync("", "  Script AI Data Delay Time: " + scriptAIDataDelayTime);
	    }
	    return "";
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
	int getContentLength(Mod srcMod) {
	    return 152;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(14);
	    out.add(relatedID);
	    out.add(lightID);
	    out.add(hitShader);
	    out.add(enchantShader);
	    out.add(projectileID);
	    out.add(explosionID);
	    out.add(castingArt);
	    out.add(hitEffectArt);
	    out.add(impactData);
	    out.add(dualCastID);
	    out.add(enchantArtID);
	    out.add(equipAbility);
	    out.add(imageSpaceModID);
	    out.add(perkID);
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }

    static class SNDD extends SubRecord {

	ArrayList<Sound> sounds = new ArrayList<>();

	SNDD() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    for (SNDD.Sound s : sounds) {
		out.write(s.sound.ordinal());
		s.soundID.export(out);
	    }
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    while (!in.isDone()) {
		SNDD.Sound sound = new SNDD.Sound();
		sound.sound = SoundData.values()[in.extractInt(4)];
		sound.soundID.setInternal(in.extract(4));
		sounds.add(sound);
	    }
	}

	@Override
	SubRecord getNew(String type) {
	    return new SNDD();
	}

	@Override
	boolean isValid() {
	    return !sounds.isEmpty();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8 * sounds.size();
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>();
	    for (SNDD.Sound s : sounds) {
		out.add(s.soundID);
	    }
	    return out;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("SNDD");
	}

	class Sound {

	    SoundData sound;
	    FormID soundID = new FormID();
	}
    }

    // Enums
    /**
     *
     */
    public enum SpellEffectFlag {

	/**
	 *
	 */
	Hostile(0),
	/**
	 *
	 */
	Recover(1),
	/**
	 *
	 */
	Detrimental(2),
	/**
	 *
	 */
	SnapToNavmesh(3),
	/**
	 *
	 */
	NoHitEvent(4),
	/**
	 *
	 */
	DispellEffects(8),
	/**
	 *
	 */
	NoDuration(9),
	/**
	 *
	 */
	NoMagnitude(10),
	/**
	 *
	 */
	NoArea(11),
	/**
	 *
	 */
	FXPersist(12),
	/**
	 *
	 */
	NoRecast(13),
	/**
	 *
	 */
	GoryVisual(14),
	/**
	 *
	 */
	HideInUI(15),
	/**
	 *
	 */
	PowerAffectsMagnitude(21),
	/**
	 *
	 */
	PowerAffectsDuration(22),
	/**
	 *
	 */
	Painless(26),
	/**
	 *
	 */
	NoHitEffect(27),
	/**
	 *
	 */
	NoDeathDispel(28);
	int value;

	SpellEffectFlag(int value) {
	    this.value = value;
	}
    }

    /**
     *
     */
    public enum SoundData {

	/**
	 *
	 */
	SheathDraw,
	/**
	 *
	 */
	Charge,
	/**
	 *
	 */
	Ready,
	/**
	 *
	 */
	Release,
	/**
	 *
	 */
	ConcentrationCastLoop,
	/**
	 *
	 */
	OnHit
    }

    // Common Functions
    /**
     *
     * @param modToOriginateFrom
     * @param edid EDID to give the new record. Make sure it is unique.
     * @param name
     */
    public MGEF(Mod modToOriginateFrom, String edid, String name) {
	this();
	originateFrom(modToOriginateFrom, edid);
	this.setName(name);
    }

    MGEF() {
	super();
	subRecords.setPrototype(MGEFproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("MGEF");
    }

    @Override
    Record getNew() {
	return new MGEF();
    }

    // Get/Set
    /**
     *
     * @return Description associated with the Major Record, or <NO TEXT> if
     * empty.
     */
    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }

    @Override
    public String getDescription() {
	return subRecords.getSubString("DNAM").print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    @Override
    public void setDescription(String description) {
	subRecords.setSubString("DNAM", description);
    }

    DATA getDATA() {
	return (DATA) subRecords.get("DATA");
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(SpellEffectFlag flag, boolean on) {
	getDATA().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(SpellEffectFlag flag) {
	return getDATA().flags.get(flag.value);
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }
}
