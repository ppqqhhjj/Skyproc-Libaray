package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.LStream;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Image Space major record. Used for various lighting settings.
 * @author Plutoman101
 */
public class IMGS extends MajorRecord {

    private static final Type[] type = {Type.IMGS};
    private SubData ENAM = new SubData(Type.ENAM);
    private HNAM HNAM = new HNAM();
    private CNAM CNAM = new CNAM();
    private TNAM TNAM = new TNAM();
    private DNAM DNAM = new DNAM();

    /**
     * Creates a new IMGS record.
     */
    public IMGS() {
        super();
        subRecords.add(ENAM);
        subRecords.add(HNAM);
        subRecords.add(CNAM);
        subRecords.add(TNAM);
        subRecords.add(DNAM);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new IMGS();
    }

    static class HNAM extends SubRecord {

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

        HNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter, IOException {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new HNAM();
        }

        @Override
        final void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
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

    static class CNAM extends SubRecord {

        private float saturation = 0;
        private float brightness = 0;
        private float contrast = 0;
        private boolean valid = true;

        public CNAM() {
            super(Type.CNAM);
            valid = false;
        }

        public CNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter, IOException {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new CNAM();
        }

        @Override
        void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
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

    static class TNAM extends SubRecord {

        private float red = 0;
        private float green = 0;
        private float blue = 0;
        private float alpha = 0;
        private boolean valid = true;

        public TNAM() {
            super(Type.TNAM);
            valid = false;
        }

        public TNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter, IOException {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new TNAM();
        }

        @Override
        void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
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

    static class DNAM extends SubRecord {

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
        void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
            super.parseData(in);
            DOFstrength = in.extractFloat();
            DOFdistance = in.extractFloat();
            DOFrange = in.extractFloat();
            if (in.remaining() >= 4) {
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

    /**
     *
     * @return
     */
    public float getEyeAdaptSpeed() {
        return HNAM.eyeAdaptSpeed;
    }

    /**
     *
     * @return
     */
    public float getBloomRadius() {
        return HNAM.bloomRadius;
    }

    /**
     *
     * @return
     */
    public float getBloomThreshold() {
        return HNAM.bloomThreshold;
    }

    /**
     *
     * @return
     */
    public float getBloomScale() {
        return HNAM.bloomScale;
    }

    /**
     *
     * @return
     */
    public float getTargetLum1() {
        return HNAM.targetLum1;
    }

    /**
     *
     * @return
     */
    public float getTargetLum2() {
        return HNAM.targetLum2;
    }

    /**
     *
     * @return
     */
    public float getSunlightScale() {
        return HNAM.sunlightScale;
    }

    /**
     *
     * @return
     */
    public float getSkyScale() {
        return HNAM.skyScale;
    }

    /**
     *
     * @return
     */
    public float getEyeAdaptStrength() {
        return HNAM.eyeAdaptStrength;
    }

    /**
     *
     * @param in
     */
    public void setEyeAdaptSpeed(float in) {
        HNAM.eyeAdaptSpeed = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomRadius(float in) {
        HNAM.bloomRadius = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomThreshold(float in) {
        HNAM.bloomThreshold = in;
    }

    /**
     *
     * @param in
     */
    public void setBloomScale(float in) {
        HNAM.bloomScale = in;
    }

    /**
     *
     * @param in
     */
    public void setTargetLum1(float in) {
        HNAM.targetLum1 = in;
    }

    /**
     *
     * @param in
     */
    public void setTargetLum2(float in) {
        HNAM.targetLum2 = in;
    }

    /**
     *
     * @param in
     */
    public void setSunlightScale(float in) {
        HNAM.sunlightScale = in;
    }

    /**
     *
     * @param in
     */
    public void setSkyScale(float in) {
        HNAM.skyScale = in;
    }

    /**
     *
     * @param in
     */
    public void setEyeAdaptStrength(float in) {
        HNAM.eyeAdaptStrength = in;
    }

    /**
     *
     * @return
     */
    public float getSaturation() {
        return CNAM.saturation;
    }

    /**
     *
     * @return
     */
    public float getBrightness() {
        return CNAM.brightness;
    }

    /**
     *
     * @return
     */
    public float getContrast() {
        return CNAM.contrast;
    }

    /**
     *
     * @return
     */
    public float getRed() {
        return TNAM.red;
    }

    /**
     *
     * @return
     */
    public float getBlue() {
        return TNAM.blue;
    }

    /**
     *
     * @return
     */
    public float getGreen() {
        return TNAM.green;
    }

    /**
     *
     * @return
     */
    public float getAlpha() {
        return TNAM.alpha;
    }

    /**
     *
     * @param in
     */
    public void setSaturation(float in) {
        CNAM.saturation = in;
    }

    /**
     *
     * @param in
     */
    public void setBrightness(float in) {
        CNAM.brightness = in;
    }

    /**
     *
     * @param in
     */
    public void setContrast(float in) {
        CNAM.contrast = in;
    }

    /**
     *
     * @param in
     */
    public void setRed(float in) {
        TNAM.red = in;
    }

    /**
     *
     * @param in
     */
    public void setBlue(float in) {
        TNAM.blue = in;
    }

    /**
     *
     * @param in
     */
    public void setGreen(float in) {
        TNAM.green = in;
    }

    /**
     *
     * @param in
     */
    public void setAlpha(float in) {
        TNAM.alpha = in;
    }

    /**
     *
     * @param in
     */
    public void setDOFstrength(float in) {
        DNAM.DOFstrength = in;
    }

    /**
     *
     * @return
     */
    public float getDOFstrength() {
        return DNAM.DOFstrength;
    }

    /**
     *
     * @param in
     */
    public void setDOFdistance (float in) {
        DNAM.DOFdistance = in;
    }

    /**
     *
     * @return
     */
    public float getDOFdistance() {
        return DNAM.DOFdistance;
    }

    /**
     *
     * @param in
     */
    public void setDOFrange (float in) {
        DNAM.DOFrange = in;
    }

    /**
     *
     * @return
     */
    public float getDOFrange() {
        return DNAM.DOFrange;
    }
}