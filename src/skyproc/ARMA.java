/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExportParser;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class ARMA extends MajorRecord {

    private static final Type[] type = {Type.ARMA};
    SubData BODT = new SubData(Type.BODT);  // flags of which bodyparts to take up
    SubForm RNAM = new SubForm(Type.RNAM);
    DNAM DNAM = new DNAM();
    // Third Person
    // Male
    SubString MOD2 = new SubString(Type.MOD2, true);
    SubList<SubData> MO2T = new SubList<SubData>(new SubData(Type.MO2T));
    AltTextures MO2S = new AltTextures(Type.MO2S);
    // Female
    SubString MOD3 = new SubString(Type.MOD3, true);
    SubList<SubData> MO3T = new SubList<SubData>(new SubData(Type.MO3T));
    AltTextures MO3S = new AltTextures(Type.MO3S);
    // First person
    // Male
    SubString MOD4 = new SubString(Type.MOD4, true);
    SubList<SubData> MO4T = new SubList<SubData>(new SubData(Type.MO4T));
    AltTextures MO4S = new AltTextures(Type.MO4S);
    // Female
    SubString MOD5 = new SubString(Type.MOD5, true);
    SubList<SubData> MO5T = new SubList<SubData>(new SubData(Type.MO5T));
    AltTextures MO5S = new AltTextures(Type.MO5S);
    SubForm maleSkinTexture = new SubForm(Type.NAM0);
    SubForm femaleSkinTexture = new SubForm(Type.NAM1);
    SubForm maleSkinSwapList = new SubForm(Type.NAM2);
    SubForm femaleSkinSwapList = new SubForm(Type.NAM3);
    SubList<SubForm> additionalRaces = new SubList<SubForm>(new SubForm(Type.MODL));
    SubForm footstepSound = new SubForm(Type.SNDD);

    /**
     * Armature Major Record
     */
    ARMA() {
        super();

        subRecords.add(BODT);
        subRecords.add(RNAM);
        subRecords.add(DNAM);
        subRecords.add(MOD2);
        subRecords.add(MO2T);
        subRecords.add(MO2S);
        subRecords.add(MOD3);
        subRecords.add(MO3T);
        subRecords.add(MO3S);
        subRecords.add(MOD4);
        subRecords.add(MO4T);
        subRecords.add(MO4S);
        subRecords.add(MOD5);
        subRecords.add(MO5T);
        subRecords.add(MO5S);
        subRecords.add(maleSkinTexture);
        subRecords.add(femaleSkinTexture);
        subRecords.add(maleSkinSwapList);
        subRecords.add(femaleSkinSwapList);
        subRecords.add(additionalRaces);
        subRecords.add(footstepSound);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new ARMA();
    }

    class AltTextures extends SubRecord {

        ArrayList<AltTexture> altTextures = new ArrayList<AltTexture>();

        AltTextures(Type t) {
            super(t);
        }

        @Override
        void export(LExportParser out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                out.write(altTextures.size(), 4);
                for (AltTexture t : altTextures) {
                    t.export(out);
                }
            }
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);
            int numTextures = in.extractInt(4);
            for (int i = 0; i < numTextures; i++) {
                int strLength = Ln.arrayToInt(in.getInts(0, 4));
                AltTexture newText = new AltTexture(new LShrinkArray(in.extract(12 + strLength)));
                altTextures.add(newText);
                if (logging()) {
                    logSync("", "New Texture Alt -- Name: " + newText.name + ", texture: " + newText.texture + ", index: " + newText.index);
                }
            }
        }

	@Override
	void standardizeMasters(Mod srcMod) {
	    super.standardizeMasters(srcMod);
	    for (AltTexture t : altTextures) {
		t.standardizeMasters(srcMod);
	    }
	}

        @Override
        SubRecord getNew(Type type) {
            return new AltTextures(type);
        }

        @Override
        public void clear() {
            altTextures.clear();
        }

        @Override
        Boolean isValid() {
            return !altTextures.isEmpty();
        }

        @Override
        int getContentLength(Mod srcMod) {
            int out = 4;  // num Textures
            for (AltTexture t : altTextures) {
                out += t.getTotalLength();
            }
            return out;
        }
    }

    static public class AltTexture implements Serializable {

        String name;
        FormID texture = new FormID();
        int index;

        public AltTexture(String name, FormID txst, int index) {
            this.name = name;
            this.texture = txst;
            this.index = index;
        }

        AltTexture(LShrinkArray in) {
            parseData(in);
        }

        final void parseData(LShrinkArray in) {
            int strLength = in.extractInt(4);
            name = in.extractString(strLength);
            texture.setInternal(in.extract(4));
            index = in.extractInt(4);
        }

        void export(LExportParser out) throws IOException {
            out.write(name.length(), 4);
            out.write(name);
            texture.export(out);
            out.write(index, 4);
        }

	void standardizeMasters(Mod srcMod) {
	    texture.standardize(srcMod);
	}

        int getTotalLength() {
            return name.length() + 12;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setTexture(FormID txst) {
            texture = txst;
        }

        public FormID getTexture() {
            return texture;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    class DNAM extends SubRecord {

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
        void export(LExportParser out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if ("FalmerHelmetKhajiitAA".equals(getEDID())) {
                int werw = 2;
            }
            out.write(malePriority, 1);
            out.write(femalePriority, 1);
            out.write(unknown, 4);
            out.write(detectionSoundValue, 1);
            out.write(unknown2, 1);
            out.write(weaponAdjust);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
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
            throw new UnsupportedOperationException("Not supported yet.");
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
            return 12;
        }
    }

    // Get/set
    public void setModelPath(String path, Gender gender, Perspective perspective) {
        switch (gender) {
            case MALE:
                switch (perspective) {
                    case THIRD_PERSON:
                        MOD2.setString(path);
                        return;
                    case FIRST_PERSON:
                        MOD4.setString(path);
                        return;
                }
            case FEMALE:
                switch (perspective) {
                    case THIRD_PERSON:
                        MOD3.setString(path);
                        return;
                    case FIRST_PERSON:
                        MOD5.setString(path);
                        return;
                }
        }
    }

    public String getModelPath(Gender gender, Perspective perspective) {
        switch (gender) {
            case MALE:
                switch (perspective) {
                    case THIRD_PERSON:
                        return MOD2.print();
                    case FIRST_PERSON:
                        return MOD4.print();
                }
            default:
                switch (perspective) {
                    case THIRD_PERSON:
                        return MOD3.print();
                    default:
                        return MOD5.print();
                }
        }
    }

    public ArrayList<AltTexture> getAltTextures(Gender gender, Perspective perspective) {
        switch (gender) {
            case MALE:
                switch (perspective) {
                    case THIRD_PERSON:
                        return MO2S.altTextures;
                    case FIRST_PERSON:
                        return MO4S.altTextures;
                }
            default:
                switch (perspective) {
                    case THIRD_PERSON:
                        return MO3S.altTextures;
                    default:
                        return MO5S.altTextures;
                }
        }
    }

    public void setRace(FormID race) {
        RNAM.setForm(race);
    }

    public FormID getRace() {
        return RNAM.getForm();
    }

    public void setSkinTexture(FormID skin, Gender gender) {
        switch (gender) {
            case MALE:
                maleSkinTexture.setForm(skin);
                return;
            case FEMALE:
                femaleSkinTexture.setForm(skin);
                return;
        }
    }

    public FormID getSkinTexture(Gender gender) {
        switch (gender) {
            case MALE:
                return maleSkinTexture.getForm();
            default:
                return femaleSkinTexture.getForm();
        }
    }

    public void setSkinSwap(FormID swapList, Gender gender) {
        switch (gender) {
            case MALE:
                maleSkinSwapList.setForm(swapList);
                return;
            case FEMALE:
                femaleSkinSwapList.setForm(swapList);
                return;
        }
    }

    public FormID getSkinSwap(Gender gender) {
        switch (gender) {
            case MALE:
                return maleSkinSwapList.getForm();
            default:
                return femaleSkinSwapList.getForm();
        }
    }

    public void addAdditionalRace(FormID addRace) {
        additionalRaces.add(new SubForm(Type.MODL, addRace));
    }

    public void removeAdditionalRace(FormID addRace) {
        additionalRaces.remove(new SubForm(Type.MODL, addRace));
    }

    public ArrayList<FormID> getAdditionalRaces() {
        return SubList.subFormToPublic(additionalRaces);
    }

    public void setFootstepSound(FormID footstep) {
        footstepSound.setForm(footstep);
    }

    public FormID getFootstepSound() {
        return footstepSound.getForm();
    }

    public void setPriority(int priority, Gender gender) {
        switch (gender) {
            case MALE:
                DNAM.malePriority = priority;
                return;
            case FEMALE:
                DNAM.femalePriority = priority;
                return;
        }
    }

    public int getPriority(Gender gender) {
        switch (gender) {
            case MALE:
                return DNAM.malePriority;
            default:
                return DNAM.femalePriority;
        }
    }

    public void setDetectionSoundValue(int value) {
        DNAM.detectionSoundValue = value;
    }

    public int getDetectionSoundValue() {
        return DNAM.detectionSoundValue;
    }

    public void setWeaponAdjust(float adjust) {
        DNAM.weaponAdjust = adjust;
    }

    public float getWeaponAdjust() {
        return DNAM.weaponAdjust;
    }
}
