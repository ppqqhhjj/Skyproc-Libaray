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
abstract class MagicItem extends MajorRecordDescription {

    SubData OBND = new SubData(Type.OBND);
    SubList<MagicEffectRef> magicEffects = new SubList<MagicEffectRef>(new MagicEffectRef());
    public Keywords keywords = new Keywords();

    MagicItem () {
	super();
    }

    MagicItem (Mod modToOriginateFrom, String edid) {
	super(modToOriginateFrom, edid);
    }

    void init() {
	subRecords.remove(Type.FULL);
	subRecords.remove(Type.DESC);
	OBND.initialize(12);
    }

    static class SPIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);
	int baseType = 0;
	float chargeTime = 0;
	CastType castType = CastType.ConstantEffect;
	DeliveryType targetType = DeliveryType.Self;
	float castDuration = 0;
	float range = 0;
	boolean valid = true;
	SubForm perkType = new SubForm(Type.PERK);

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

    public ArrayList<MagicEffectRef> getMagicEffects () {
	return magicEffects.toPublic();
    }

    public void removeMagicEffect(MagicEffectRef magicEffect) {
	magicEffects.remove(magicEffect);
    }

    public void addMagicEffect(MagicEffectRef magicEffect) {
	magicEffects.add(magicEffect);
    }

    public void addMagicEffect(MGEF magicEffect) {
	magicEffects.add(new MagicEffectRef(magicEffect.getForm()));
    }

    public void clearMagicEffects () {
	magicEffects.clear();
    }
}
