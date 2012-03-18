/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
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
public class MGEF extends MajorRecordDescription {

    private static Type[] types = {Type.MGEF};

    DATA DATA = new DATA();
    SubForm ESCE = new SubForm(Type.ESCE);
    Keywords keywords = new Keywords();
    SubForm MODB = new SubForm(Type.MDOB);
    SubData OBND = new SubData(Type.OBND);
    SubList<SNDD> sounds = new SubList<SNDD>(new SNDD());
    public ScriptPackage scripts = new ScriptPackage();

    MGEF() {
	super();

	subRecords.remove(Type.DESC);
	description = new SubStringPointer(Type.DNAM, SubStringPointer.Files.DLSTRINGS);
	subRecords.add(description);

	subRecords.add(DATA);
	subRecords.add(ESCE);
	subRecords.add(keywords);
	subRecords.add(MODB);
	subRecords.add(OBND);
	subRecords.add(sounds);
	subRecords.add(scripts);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new MGEF();
    }

    class DATA extends SubRecord {

	LFlags flags = new LFlags(4);
	float baseCost;
	FormID relatedID = new FormID();
	ActorValue skillType;
	ActorValue resistanceAV;
	byte[] unknown;
	FormID lightID = new FormID();
	float taperWeight;
	FormID hitShader = new FormID();
	FormID enchantShader = new FormID();
	int skillLevel;
	int area;
	float castingTime;
	float taperCurve;
	float taperDuration;
	float secondAVWeight;
	int effectType;
	ActorValue primaryAV;
	FormID projectileID = new FormID();
	FormID explosionID = new FormID();
	CastType castType;
	DeliveryType deliveryType;
	ActorValue secondAV;
	FormID castingArt = new FormID();
	FormID hitEffectArt = new FormID();
	FormID impactData = new FormID();
	float skillUsageMult;
	FormID dualCastID = new FormID();
	float dualCastScale;
	FormID enchantArtID = new FormID();
	int nullData;
	int nullData2;
	FormID equipAbility = new FormID();
	FormID imageSpaceModID = new FormID();
	FormID perkID = new FormID();
	SoundVolume vol;
	float scriptAIDataScore;
	float scriptAIDataDelayTime;

	DATA() {
	    super(Type.DATA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export(),4);
	    out.write(baseCost);
	    relatedID.export(out);
	    out.write(ActorValue.value(skillType),4);
	    out.write(ActorValue.value(resistanceAV),4);
	    out.write(unknown,4);
	    lightID.export(out);
	    out.write(taperWeight);
	    hitShader.export(out);
	    enchantShader.export(out);
	    out.write(skillLevel,4);
	    out.write(area,4);
	    out.write(castingTime);
	    out.write(taperCurve);
	    out.write(taperDuration);
	    out.write(secondAVWeight);
	    out.write(effectType,4);
	    out.write(ActorValue.value(primaryAV),4);
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
	    out.write(nullData,4);
	    out.write(nullData2,4);
	    equipAbility.export(out);
	    imageSpaceModID.export(out);
	    perkID.export(out);
	    out.write(vol.ordinal(),4);
	    out.write(scriptAIDataScore);
	    out.write(scriptAIDataDelayTime);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
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
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	    relatedID.standardize(srcMod);
	    lightID.standardize(srcMod);
	    hitShader.standardize(srcMod);
	    enchantShader.standardize(srcMod);
	    projectileID.standardize(srcMod);
	    explosionID.standardize(srcMod);
	    castingArt.standardize(srcMod);
	    hitEffectArt.standardize(srcMod);
	    impactData.standardize(srcMod);
	    dualCastID.standardize(srcMod);
	    enchantArtID.standardize(srcMod);
	    equipAbility.standardize(srcMod);
	    imageSpaceModID.standardize(srcMod);
	    perkID.standardize(srcMod);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	public void clear() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 152;
	}
    }

    class SNDD extends SubRecord {

	SoundData sound;
	FormID soundID = new FormID();

	SNDD(){
	    super(Type.SNDD);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(sound.ordinal(),4);
	    soundID.export(out);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    sound = SoundData.values()[in.extractInt(4)];
	    soundID.setInternal(in.extract(4));
	}

	@Override
	SubRecord getNew(Type type) {
	    return new SNDD();
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	    soundID.standardize(srcMod);
	}

	@Override
	public void clear() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}

    }

    public enum CastType {
	ConstantEffect,
	FireAndForget,
	Concentration
    }

    public enum DeliveryType {
	Self,
	Touch,
	Aimed,
	TargetActor,
	TargetLocation
    }

    public enum SpellEffectFlag {

	Hostile(0),
	Recover(1),
	Detrimental(2),
	SnapToNavmesh(3),
	NoHitEvent(4),
	DispellEffects(8),
	NoDuration(9),
	NoMagnitude(10),
	NoArea(11),
	FXPersist(12),
	NoRecast(13),
	GoryVisual(14),
	HideInUI(15),
	PowerAffectsMagnitude(21),
	PowerAffectsDuration(22),
	Painless(26),
	NoHitEffect(27),
	NoDeathDispel(28),
	;
	int value;

	SpellEffectFlag(int value) {
	    this.value = value;
	}
    }

    public enum SoundData {
	SheathDraw,
	Charge,
	Ready,
	Release,
	ConcentrationCastLoop,
	OnHit
    }
}
