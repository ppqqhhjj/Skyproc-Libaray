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
import skyproc.AltTextures.AltTexture;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Armature records (pieces of armor)
 *
 * @author Justin Swanson
 */
public class ARMA extends MajorRecord {

    static final SubRecordsPrototype ARMAprototype = new SubRecordsPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new BodyTemplate());
	    add(new SubData(Type.BOD2));
	    add(new SubForm(Type.RNAM));
	    add(new DNAM());
	    // Third Person
	    // Male
	    add(new SubString(Type.MOD2, true));
	    add(new SubList<>(new SubData(Type.MO2T)));
	    add(new AltTextures(Type.MO2S));
	    // Female
	    add(new SubString(Type.MOD3, true));
	    add(new SubList<>(new SubData(Type.MO3T)));
	    add(new AltTextures(Type.MO3S));
	    // First person
	    // Male
	    add(new SubString(Type.MOD4, true));
	    add(new SubList<>(new SubData(Type.MO4T)));
	    add(new AltTextures(Type.MO4S));
	    // Female
	    add(new SubString(Type.MOD5, true));
	    add(new SubList<>(new SubData(Type.MO5T)));
	    add(new AltTextures(Type.MO5S));
	    add(new SubForm(Type.NAM0));
	    add(new SubForm(Type.NAM1));
	    add(new SubForm(Type.NAM2));
	    add(new SubForm(Type.NAM3));
	    add(new SubList<>(new SubForm(Type.MODL)));
	    add(new SubForm(Type.SNDD));
	}
    };
    private static final Type[] type = {Type.ARMA};

    /**
     * Armature Major Record
     */
    ARMA() {
	super();
	subRecords.setPrototype(ARMAprototype);
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new ARMA();
    }

    static class DNAM extends SubRecord {

	int malePriority;
	int femalePriority;
	byte[] unknown;
	int detectionSoundValue;
	byte[] unknown2;
	float weaponAdjust;

	DNAM() {
	    super(Type.DNAM);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(malePriority, 1);
	    out.write(femalePriority, 1);
	    out.write(unknown, 4);
	    out.write(detectionSoundValue, 1);
	    out.write(unknown2, 1);
	    out.write(weaponAdjust);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    malePriority = in.extractInt(1);
	    femalePriority = in.extractInt(1);
	    unknown = in.extract(4);
	    detectionSoundValue = in.extractInt(1);
	    unknown2 = in.extract(1);
	    weaponAdjust = in.extractFloat();
	    if (logging()) {
		logSync("", "M-Priority: " + malePriority + ", F-Priority: " + femalePriority + ", DetectionValue: " + detectionSoundValue + ", weaponAdjust: " + weaponAdjust);
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
	    return 12;
	}
    }

    // Get/set
    Type getModelPathType(Gender gender, Perspective perspective) {
	switch (gender) {
	    case MALE:
		switch (perspective) {
		    case THIRD_PERSON:
			return Type.MOD2;
		    case FIRST_PERSON:
			return Type.MOD4;
		}
	    case FEMALE:
		switch (perspective) {
		    case THIRD_PERSON:
			return Type.MOD3;
		    case FIRST_PERSON:
			return Type.MOD5;
		}
	    default:
		return Type.MOD2;
	}
    }

    /**
     *
     * @param path Path of the .nif file to assign.
     * @param gender The gender to assign this model path to.
     * @param perspective Perspective to assign this model path to.
     */
    public void setModelPath(String path, Gender gender, Perspective perspective) {
	subRecords.setSubString(getModelPathType(gender, perspective), path);
    }

    /**
     *
     * @param gender The gender of the desired model path to query.
     * @param perspective The perspective of the model path to query.
     * @return The model path of the specified gender/perspective. Empty string
     * if a model path does not exist for specified parameters.
     */
    public String getModelPath(Gender gender, Perspective perspective) {
	return subRecords.getSubString(getModelPathType(gender, perspective)).print();
    }

    Type getAltTexType(Gender gender, Perspective perspective) {
	switch (gender) {
	    case MALE:
		switch (perspective) {
		    case THIRD_PERSON:
			return Type.MO2S;
		    case FIRST_PERSON:
			return Type.MO4S;
		}
	    default:
		switch (perspective) {
		    case THIRD_PERSON:
			return Type.MO3S;
		    default:
			return Type.MO5S;
		}
	}
    }

    /**
     * Returns the set of AltTextures applied to a specified gender and
     * perspective.
     *
     * @param gender Gender of the AltTexture set to query.
     * @param perspective Perspective of the AltTexture set to query.
     * @return List of the AltTextures applied to the gender/perspective.
     */
    public ArrayList<AltTexture> getAltTextures(Gender gender, Perspective perspective) {
	AltTextures t = (AltTextures) subRecords.get(getAltTexType(gender, perspective));
	return t.altTextures;
    }

    /**
     *
     * @param rhs Other ARMA record.
     * @param gender Gender of the pack to compare.
     * @param perspective Perspective of the pack to compare
     * @return true if:<br> Both sets are empty.<br> or <br> Each set contains
     * matching Alt Textures with the same name and TXST formID reference, in
     * the same corresponding indices.
     */
    public boolean equalAltTextures(ARMA rhs, Gender gender, Perspective perspective) {
	return AltTextures.equal(getAltTextures(gender, perspective), rhs.getAltTextures(gender, perspective));
    }

    /**
     *
     * @param race
     */
    public void setRace(FormID race) {
	subRecords.setSubForm(Type.RNAM, race);
    }

    /**
     *
     * @return
     */
    public FormID getRace() {
	return subRecords.getSubForm(Type.RNAM).getForm();
    }

    /**
     *
     * @param skin
     * @param gender
     */
    public void setSkinTexture(FormID skin, Gender gender) {
	switch (gender) {
	    case MALE:
		subRecords.setSubForm(Type.NAM0, skin);
		return;
	    case FEMALE:
		subRecords.setSubForm(Type.NAM1, skin);
		return;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public FormID getSkinTexture(Gender gender) {
	switch (gender) {
	    case MALE:
		return subRecords.getSubForm(Type.NAM0).getForm();
	    default:
		return subRecords.getSubForm(Type.NAM1).getForm();
	}
    }

    /**
     *
     * @param swapList
     * @param gender
     */
    public void setSkinSwap(FormID swapList, Gender gender) {
	switch (gender) {
	    case MALE:
		subRecords.getSubForm(Type.NAM2).setForm(swapList);
		return;
	    case FEMALE:
		subRecords.getSubForm(Type.NAM3).setForm(swapList);
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public FormID getSkinSwap(Gender gender) {
	switch (gender) {
	    case MALE:
		return subRecords.getSubForm(Type.NAM2).getForm();
	    default:
		return subRecords.getSubForm(Type.NAM3).getForm();
	}
    }

    /**
     *
     * @param addRace
     */
    public void addAdditionalRace(FormID addRace) {
	subRecords.getSubList(Type.MODL).add(new SubForm(Type.MODL, addRace));
    }

    /**
     *
     * @param addRace
     */
    public void removeAdditionalRace(FormID addRace) {
	subRecords.getSubList(Type.MODL).remove(new SubForm(Type.MODL, addRace));
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getAdditionalRaces() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.MODL));
    }

    /**
     *
     */
    public void clearAdditionalRaces() {
	subRecords.getSubList(Type.MODL).clear();
    }

    /**
     *
     * @param footstep
     */
    public void setFootstepSound(FormID footstep) {
	subRecords.setSubForm(Type.SNDD, footstep);
    }

    /**
     *
     * @return
     */
    public FormID getFootstepSound() {
	return subRecords.getSubForm(Type.SNDD).getForm();
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get(Type.DNAM);
    }

    /**
     *
     * @param priority
     * @param gender
     */
    public void setPriority(int priority, Gender gender) {
	switch (gender) {
	    case MALE:
		getDNAM().malePriority = priority;
		return;
	    case FEMALE:
		getDNAM().femalePriority = priority;
		return;
	}
    }

    /**
     *
     * @param gender
     * @return
     */
    public int getPriority(Gender gender) {
	switch (gender) {
	    case MALE:
		return getDNAM().malePriority;
	    default:
		return getDNAM().femalePriority;
	}
    }

    /**
     *
     * @param value
     */
    public void setDetectionSoundValue(int value) {
	getDNAM().detectionSoundValue = value;
    }

    /**
     *
     * @return
     */
    public int getDetectionSoundValue() {
	return getDNAM().detectionSoundValue;
    }

    /**
     *
     * @param adjust
     */
    public void setWeaponAdjust(float adjust) {
	getDNAM().weaponAdjust = adjust;
    }

    /**
     *
     * @return
     */
    public float getWeaponAdjust() {
	return getDNAM().weaponAdjust;
    }
}
