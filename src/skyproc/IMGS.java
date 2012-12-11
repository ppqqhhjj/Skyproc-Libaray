package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Image Space major record. Used for various lighting settings.
 * @author Plutoman101
 */
public class IMGS extends MajorRecord {

    static final SubPrototype IMGSproto = new SubPrototype(MajorRecord.majorProto){

	@Override
	protected void addRecords() {
	    add(new SubData(Type.ENAM));
	    add(new HNAM());
	    add(new CNAM());
	    add(new TNAM());
	    add(new DNAM());
	}
    };
    private final static ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.IMGS}));

    /**
     * Creates a new IMGS record.
     */
    public IMGS() {
        super();
        subRecords.setPrototype(IMGSproto);
    }

    @Override
    ArrayList<Type> getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new IMGS();
    }

    static class HNAM extends SubRecordTyped {

        private float eyeAdaptSpeed = 0;
        private float bloomRadius = 0;
        private float bloomThreshold = 0;
        private float bloomScale = 0;
        private float targetLum1 = 0;
        private float targetLum2 = 0;
        private float sunlightScale = 0;
        private float skyScale = 0;
        private float eyeAdaptStrength = 0;
        private boolean valid = true;

        HNAM() {
            super(Type.HNAM);
            valid = false;
        }

        HNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new HNAM();
        }

        @Override
        final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);

            eyeAdaptSpeed = in.extractFloat();
            bloomRadius = in.extractFloat();
            bloomThreshold = in.extractFloat();
            bloomScale = in.extractFloat();
            targetLum1 = in.extractFloat();
            targetLum2 = in.extractFloat();
            sunlightScale = in.extractFloat();
            skyScale = in.extractFloat();
            eyeAdaptStrength = in.extractFloat();

            if (logging()) {
                logSync("", "HNAM record: ");
                logSync("", "  " + "Eye Adapt Speed: " + eyeAdaptSpeed + ", Bloom Radius: " + bloomRadius);
                logSync("", "  " + "Bloom Threshold: " + bloomThreshold + ", Bloom Scale: " + bloomScale + ", Target Lum #1: " + targetLum1);
                logSync("", "  " + "Target Lum #2: " + targetLum2 + ", Sunlight Scale: " + sunlightScale);
                logSync("", "  " + "Sky Scale: " + skyScale + ", Eye Adapt Strength: " + eyeAdaptStrength);
            }

            valid = true;
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                out.write(eyeAdaptSpeed);
                out.write(bloomRadius);
                out.write(bloomThreshold);
                out.write(bloomScale);
                out.write(targetLum1);
                out.write(targetLum2);
                out.write(sunlightScale);
                out.write(skyScale);
                out.write(eyeAdaptStrength);
            }
        }

        @Override
        Boolean isValid() {
            return valid;
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

    static class CNAM extends SubRecordTyped {

        private float saturation = 0;
        private float brightness = 0;
        private float contrast = 0;
        private boolean valid = true;

        public CNAM() {
            super(Type.CNAM);
            valid = false;
        }

        public CNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new CNAM();
        }

        @Override
        void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);

            saturation = in.extractFloat();
            brightness = in.extractFloat();
            contrast = in.extractFloat();

            if (logging()) {
                logSync("", "CNAM record: ");
                logSync("", "  " + "Saturation: " + saturation + ", Brightness: " + brightness + ", Contrast: " + contrast);
            }

            valid = true;
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                out.write(saturation);
                out.write(brightness);
                out.write(contrast);
            }
        }

        @Override
        Boolean isValid() {
            return valid;
        }

        @Override
        int getContentLength(Mod srcMod) {
            if (isValid()) {
                return 12;
            } else {
                return 0;
            }
        }

    }

    static class TNAM extends SubRecordTyped {

        private float red = 0;
        private float green = 0;
        private float blue = 0;
        private float alpha = 0;
        private boolean valid = true;

        public TNAM() {
            super(Type.TNAM);
            valid = false;
        }

        public TNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new TNAM();
        }

        @Override
        void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);

            alpha = in.extractFloat();
            red = in.extractFloat();
            green = in.extractFloat();
            blue = in.extractFloat();

            if (logging()) {
                logSync("", "TNAM record: RWX Format");
                logSync("", "  " + "Red: " + red + ", Green: " + green);
                logSync("", "  " + "Blue: " + blue + ", Alpha: " + alpha);
            }

            valid = true;
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                out.write(alpha);
                out.write(red);
                out.write(green);
                out.write(blue);
            }
        }

        @Override
        Boolean isValid() {
            return valid;
        }

        @Override
        int getContentLength(Mod srcMod) {
            if (isValid()) {
                return 16;
            } else {
                return 0;
            }
        }

    }

    static class DNAM extends SubRecordTyped {

        float DOFstrength = 0;
        float DOFdistance = 0;
        float DOFrange = 0;
        byte[] unknown;
        boolean valid = false;

        public DNAM() {
            super(Type.DNAM);
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                out.write(DOFstrength);
                out.write(DOFdistance);
                out.write(DOFrange);
                if (unknown != null) {
                    out.write(unknown, 4);
                }
            }
        }

        @Override
        void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);
            DOFstrength = in.extractFloat();
            DOFdistance = in.extractFloat();
            DOFrange = in.extractFloat();
            if (in.available() >= 4) {
                unknown = in.extract(4);
            }
            valid = true;
        }

        @Override
        SubRecord getNew(Type type) {
            return new DNAM();
        }

        @Override
        Boolean isValid() {
            return valid;
        }

        @Override
        int getContentLength(Mod srcMod) {
            if (isValid()) {
                if (unknown != null) {
                    return 16;
                } else {
                    return 12;
                }
            } else {
                return 0;
            }
        }

    }

    HNAM getHNAM() {
	return (HNAM) subRecords.get(Type.HNAM);
    }

    CNAM getCNAM() {
	return (CNAM) subRecords.get(Type.CNAM);
    }

    TNAM getTNAM() {
	return (TNAM) subRecords.get(Type.TNAM);
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get(Type.DNAM);
    }

    /**
     *
     * @return
     */
    public float getEyeAdaptSpeed() {
        return getHNAM().eyeAdaptSpeed;
    }

    /**
     *
     * @return
     */
    public float getBloomRadius() {
        return getHNAM().bloomRadius;
    }

    /**
     *
     * @return
     */
    public float getBloomThreshold() {
        return getHNAM().bloomThreshold;
    }

    /**
     *
     * @return
     */
    public float getBloomScale() {
        return getHNAM().bloomScale;
    }

    /**
     *
     * @return
     */
    public float getTargetLum1() {
        return getHNAM().targetLum1;
    }

    /**
     *
     * @return
     */
    public float getTargetLum2() {
        return getHNAM().targetLum2;
    }

    /**
     *
     * @return
     */
    public float getSunlightScale() {
        return getHNAM().sunlightScale;
    }

    /**
     *
     * @return
     */
    public float getSkyScale() {
        return getHNAM().skyScale;
    }

    /**
     *
     * @return
     */
    public float getEyeAdaptStrength() {
        return getHNAM().eyeAdaptStrength;
    }

    /**
     *
     * @param in
     */
    public void setEyeAdaptSpeed(float in) {
        getHNAM().eyeAdaptSpeed = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomRadius(float in) {
        getHNAM().bloomRadius = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomThreshold(float in) {
        getHNAM().bloomThreshold = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomScale(float in) {
        getHNAM().bloomScale = in;
    }

    /**
     *
     * @param in
     */
    public void setTargetLum1(float in) {
        getHNAM().targetLum1 = in;
    }

    /**
     *
     * @param in
     */
    public void setTargetLum2(float in) {
        getHNAM().targetLum2 = in;
    }

    /**
     *
     * @param in
     */
    public void setSunlightScale(float in) {
        getHNAM().sunlightScale = in;
    }

    /**
     *
     * @param in
     */
    public void setSkyScale(float in) {
        getHNAM().skyScale = in;
    }

    /**
     *
     * @param in
     */
    public void setEyeAdaptStrength(float in) {
        getHNAM().eyeAdaptStrength = in;
    }

    /**
     *
     * @return
     */
    public float getSaturation() {
        return getCNAM().saturation;
    }

    /**
     *
     * @return
     */
    public float getBrightness() {
        return getCNAM().brightness;
    }

    /**
     *
     * @return
     */
    public float getContrast() {
        return getCNAM().contrast;
    }

    /**
     *
     * @return
     */
    public float getRed() {
        return getTNAM().red;
    }

    /**
     *
     * @return
     */
    public float getBlue() {
        return getTNAM().blue;
    }

    /**
     *
     * @return
     */
    public float getGreen() {
        return getTNAM().green;
    }

    /**
     *
     * @return
     */
    public float getAlpha() {
        return getTNAM().alpha;
    }

    /**
     *
     * @param in
     */
    public void setSaturation(float in) {
        getCNAM().saturation = in;
    }

    /**
     *
     * @param in
     */
    public void setBrightness(float in) {
        getCNAM().brightness = in;
    }

    /**
     *
     * @param in
     */
    public void setContrast(float in) {
        getCNAM().contrast = in;
    }

    /**
     *
     * @param in
     */
    public void setRed(float in) {
        getTNAM().red = in;
    }

    /**
     *
     * @param in
     */
    public void setBlue(float in) {
        getTNAM().blue = in;
    }

    /**
     *
     * @param in
     */
    public void setGreen(float in) {
        getTNAM().green = in;
    }

    /**
     *
     * @param in
     */
    public void setAlpha(float in) {
        getTNAM().alpha = in;
    }

    /**
     *
     * @param in
     */
    public void setDOFstrength(float in) {
        getDNAM().DOFstrength = in;
    }

    /**
     *
     * @return
     */
    public float getDOFstrength() {
        return getDNAM().DOFstrength;
    }

    /**
     *
     * @param in
     */
    public void setDOFdistance (float in) {
        getDNAM().DOFdistance = in;
    }

    /**
     *
     * @return
     */
    public float getDOFdistance() {
        return getDNAM().DOFdistance;
    }

    /**
     *
     * @param in
     */
    public void setDOFrange (float in) {
        getDNAM().DOFrange = in;
    }

    /**
     *
     * @return
     */
    public float getDOFrange() {
        return getDNAM().DOFrange;
    }
}