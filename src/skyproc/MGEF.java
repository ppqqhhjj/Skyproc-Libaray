/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.genenums.SoundVolume;
import skyproc.genenums.DeliveryType;
import skyproc.genenums.CastType;
import skyproc.genenums.ActorValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
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
	    add(new SubData("OBND", new byte[12]));
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
	void export(ModExporter out) throws IOException {
	    super.export(out);
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
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
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
	int getContentLength(ModExporter out) {
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
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    for (SNDD.Sound s : sounds) {
		out.write(s.sound.ordinal());
		s.soundID.export(out);
	    }
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in, srcMod);
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
	int getContentLength(ModExporter out) {
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
    public MGEF(String edid, String name) {
	this();
	originateFromPatch(edid);
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
	return subRecords.getSubStringPointer("DNAM").print();
    }

    /**
     *
     * @param description String to set as the Major Record description.
     */
    @Override
    public void setDescription(String description) {
	subRecords.setSubStringPointer("DNAM", description);
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

    /**
     *
     * @param value
     */
    public void setBaseCost (float value) {
	getDATA().baseCost = value;
    }

    /**
     *
     * @return
     */
    public float getBaseCost () {
	return getDATA().baseCost;
    }

    /**
     *
     * @param id
     */
    public void setRelatedID (FormID id) {
	getDATA().relatedID = id;
    }

    /**
     *
     * @return
     */
    public FormID getRelatedID () {
	return getDATA().relatedID;
    }

    /**
     *
     * @param val
     */
    public void setSkillType (ActorValue val) {
	getDATA().skillType = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getSkillType () {
	return getDATA().skillType;
    }

    /**
     *
     * @param val
     */
    public void setResistanceAV (ActorValue val) {
	getDATA().resistanceAV = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getResistanceAV () {
	return getDATA().resistanceAV;
    }

    /**
     *
     * @param light
     */
    public void setLight (FormID light) {
	getDATA().lightID = light;
    }

    /**
     *
     * @return
     */
    public FormID getLight () {
	return getDATA().lightID;
    }

    /**
     *
     * @param value
     */
    public void setTaperWeight (float value) {
	getDATA().taperWeight = value;
    }

    /**
     *
     * @return
     */
    public float getTaperWeight () {
	return getDATA().taperWeight;
    }

    /**
     *
     * @param hitShader
     */
    public void setHitShader (FormID hitShader) {
	getDATA().hitShader = hitShader;
    }

    /**
     *
     * @return
     */
    public FormID getHitShader () {
	return getDATA().hitShader;
    }

    /**
     *
     * @param enchantShader
     */
    public void setEnchantShader (FormID enchantShader) {
	getDATA().enchantShader = enchantShader;
    }

    /**
     *
     * @return
     */
    public FormID getEnchantShader () {
	return getDATA().enchantShader;
    }

    /**
     *
     * @param level
     */
    public void setSkillLevel(int level) {
	getDATA().skillLevel = level;
    }

    /**
     *
     * @return
     */
    public int getSkillLevel() {
	return getDATA().skillLevel;
    }

    /**
     *
     * @param area
     */
    public void setArea(int area) {
	getDATA().area = area;
    }

    /**
     *
     * @return
     */
    public int getArea() {
	return getDATA().area;
    }

    /**
     *
     * @param value
     */
    public void setCastingTime (float value) {
	getDATA().castingTime = value;
    }

    /**
     *
     * @return
     */
    public float getCastingTime () {
	return getDATA().castingTime;
    }

    /**
     *
     * @param value
     */
    public void setTaperCurve (float value) {
	getDATA().taperCurve = value;
    }

    /**
     *
     * @return
     */
    public float getTaperCurve () {
	return getDATA().taperCurve;
    }

    /**
     *
     * @param value
     */
    public void setTaperDuration (float value) {
	getDATA().taperDuration = value;
    }

    /**
     *
     * @return
     */
    public float getTaperDuration () {
	return getDATA().taperDuration;
    }

    /**
     *
     * @param value
     */
    public void setSecondAVWeight (float value) {
	getDATA().secondAVWeight = value;
    }

    /**
     *
     * @return
     */
    public float getSecondAVWeight () {
	return getDATA().secondAVWeight;
    }

    /**
     *
     * @param value
     */
    public void setEffectType (int value) {
	getDATA().effectType = value;
    }

    /**
     *
     * @return
     */
    public float getEffectType () {
	return getDATA().effectType;
    }

    /**
     *
     * @param val
     */
    public void setPrimaryAV (ActorValue val) {
	getDATA().primaryAV = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getPrimaryAV () {
	return getDATA().primaryAV;
    }

    /**
     *
     * @param id
     */
    public void setProjectile(FormID id) {
	getDATA().projectileID = id;
    }

    /**
     *
     * @return
     */
    public FormID getProjectile() {
	return getDATA().projectileID;
    }

    /**
     *
     * @param id
     */
    public void setExplosion(FormID id) {
	getDATA().explosionID = id;
    }

    /**
     *
     * @return
     */
    public FormID getExplosion() {
	return getDATA().explosionID;
    }

    /**
     *
     * @param cast
     */
    public void setCastType (CastType cast) {
	getDATA().castType = cast;
    }

    /**
     *
     * @return
     */
    public CastType getCastType () {
	return getDATA().castType;
    }

    /**
     *
     * @param del
     */
    public void setDeliveryType (DeliveryType del) {
	getDATA().deliveryType = del;
    }

    /**
     *
     * @return
     */
    public DeliveryType getDeliveryType () {
	return getDATA().deliveryType;
    }

    /**
     *
     * @param val
     */
    public void setSecondAV (ActorValue val) {
	getDATA().secondAV = val;
    }

    /**
     *
     * @return
     */
    public ActorValue getSecondAV () {
	return getDATA().secondAV;
    }

    /**
     *
     * @param art
     */
    public void setCastingArt (FormID art) {
	getDATA().castingArt = art;
    }

    /**
     *
     * @return
     */
    public FormID getCastingArt () {
	return getDATA().castingArt;
    }

    /**
     *
     * @param art
     */
    public void setHitEffectArt (FormID art) {
	getDATA().hitEffectArt = art;
    }

    /**
     *
     * @return
     */
    public FormID getHitEffectArt () {
	return getDATA().hitEffectArt;
    }

    /**
     *
     * @param data
     */
    public void setImpactData (FormID data) {
	getDATA().impactData = data;
    }

    /**
     *
     * @return
     */
    public FormID getImpactData () {
	return getDATA().impactData;
    }

    /**
     *
     * @param mult
     */
    public void setSkillUsageMult (float mult) {
	getDATA().skillUsageMult = mult;
    }

    /**
     *
     * @return
     */
    public float getSkillUsageMult () {
	return getDATA().skillUsageMult;
    }

    /**
     *
     * @param id
     */
    public void setDualCast (FormID id) {
	getDATA().dualCastID = id;
    }

    /**
     *
     * @return
     */
    public FormID getDualCast () {
	return getDATA().dualCastID;
    }

    /**
     *
     * @param scale
     */
    public void setDualCastScale (float scale) {
	getDATA().dualCastScale = scale;
    }

    /**
     *
     * @return
     */
    public float getDualCastScale () {
	return getDATA().dualCastScale;
    }

    /**
     *
     * @param art
     */
    public void setEnchantArt (FormID art) {
	getDATA().enchantArtID = art;
    }

    /**
     *
     * @return
     */
    public FormID getEnchantArt () {
	return getDATA().enchantArtID;
    }

    /**
     *
     * @param id
     */
    public void setEquipAbility (FormID id) {
	getDATA().equipAbility = id;
    }

    /**
     *
     * @return
     */
    public FormID getEquipAbility () {
	return getDATA().equipAbility;
    }

    /**
     *
     * @param id
     */
    public void setImageSpaceMod (FormID id) {
	getDATA().imageSpaceModID = id;
    }

    /**
     *
     * @return
     */
    public FormID getImageSpaceMod () {
	return getDATA().imageSpaceModID;
    }

    /**
     *
     * @param id
     */
    public void setPerk (FormID id) {
	getDATA().perkID = id;
    }

    /**
     *
     * @return
     */
    public FormID getPerk () {
	return getDATA().perkID;
    }

    /**
     *
     * @param vol
     */
    public void setSoundVolume(SoundVolume vol) {
	getDATA().vol = vol;
    }

    /**
     *
     * @return
     */
    public SoundVolume getSoundVolume() {
	return getDATA().vol;
    }

    /**
     *
     * @param score
     */
    public void setScriptAIDataScore(float score) {
	getDATA().scriptAIDataScore = score;
    }

    /**
     *
     * @return
     */
    public float getScriptAIDataScore() {
	return getDATA().scriptAIDataScore;
    }

    /**
     *
     * @param score
     */
    public void setScriptAIDataTime(float score) {
	getDATA().scriptAIDataDelayTime = score;
    }

    /**
     *
     * @return
     */
    public float getScriptAIDataTime() {
	return getDATA().scriptAIDataDelayTime;
    }

    /**
     *
     * @return
     */
    public ArrayList<Condition> getConditions() {
	return subRecords.getSubList("CTDA").toPublic();
    }

    /**
     *
     * @param c
     */
    public void addCondition(Condition c) {
	subRecords.getSubList("CTDA").add(c);
    }

    /**
     *
     * @param c
     */
    public void removeCondition(Condition c) {
	subRecords.getSubList("CTDA").remove(c);
    }

}
