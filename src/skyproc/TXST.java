/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class TXST extends MajorRecord {

    private static final Type[] type = {Type.TXST};

    SubData OBND = new SubData(Type.OBND);
    SubString colorMap = new SubString(Type.TX00, true);
    SubString normalMap = new SubString(Type.TX01, true);
    SubString maskMap = new SubString(Type.TX02, true);
    SubString toneMap = new SubString(Type.TX03, true);
    SubString detailMap = new SubString(Type.TX04, true);
    SubString environmentMap = new SubString(Type.TX05, true);
    SubString TX06 = new SubString(Type.TX06, true);
    SubString specularityMap = new SubString(Type.TX07, true);
    SubData DODT = new SubData(Type.DODT);
    SubFlag DNAM = new SubFlag(Type.DNAM, 2);

    public TXST(Mod srcMod) {
        super(srcMod);
        init();
        OBND.setData(new byte[12]);
    }

    TXST () {
        super();
        init();
    }

    final void init() {
        subRecords.add(OBND);
        subRecords.add(colorMap);
        subRecords.add(normalMap);
        subRecords.add(maskMap);
        subRecords.add(toneMap);
        subRecords.add(detailMap);
        subRecords.add(environmentMap);
        subRecords.add(TX06);
        subRecords.add(specularityMap);
        subRecords.add(DODT);
        subRecords.add(DNAM);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new TXST();
    }

    public void setNthMap(int i, String path) {
        switch (i) {
            case 0:
                setColorMap(path);
                return;
            case 1:
                setNormalMap(path);
                return;
            case 2:
                setMaskMap(path);
                return;
            case 3:
                setToneMap(path);
                return;
            case 4:
                setDetailMap(path);
                return;
            case 5:
                setEnvironmentMap(path);
                return;
            case 6:
                set6thMap(path);
                return;
            case 7:
                setSpecularityMap(path);
                return;
        }
    }

    public String getNthMap(int i) {
        switch (i) {
            case 0:
                return getColorMap();
            case 1:
                return getNormalMap();
            case 2:
                return getMaskMap();
            case 3:
                return getToneMap();
            case 4:
                return getDetailMap();
            case 5:
                return getEnvironmentMap();
            case 6:
                return get6thMap();
            case 7:
                return getSpecularityMap();
            default:
                return "";
        }
    }

    public void setColorMap(String path) {
        colorMap.setString(path);
    }

    public String getColorMap() {
        return colorMap.print();
    }

    public void setNormalMap (String path) {
        normalMap.setString(path);
    }

    public String getNormalMap () {
        return normalMap.print();
    }

    public void setMaskMap (String path) {
        maskMap.setString(path);
    }

    public String getMaskMap () {
        return maskMap.print();
    }

    public void setToneMap (String path) {
        toneMap.setString(path);
    }

    public String getToneMap () {
        return toneMap.print();
    }

    public void setDetailMap (String path) {
        detailMap.setString(path);
    }

    public String getDetailMap () {
        return detailMap.print();
    }

    public void setEnvironmentMap (String path) {
        environmentMap.setString(path);
    }

    public String getEnvironmentMap () {
        return environmentMap.print();
    }

    void set6thMap(String path) {
        TX06.setString(path);
    }

    String get6thMap() {
        return TX06.print();
    }

    public void setSpecularityMap (String path) {
        specularityMap.setString(path);
    }

    public String getSpecularityMap () {
        return specularityMap.print();
    }

    public boolean isFlag(TXSTflag flag) {
        return DNAM.is(flag.ordinal());
    }

    public void setFlag(TXSTflag flag, boolean to) {
        DNAM.set(flag.ordinal(), to);
    }

    public enum TXSTflag {
        SPECULAR_MAP_OFF,
        FACEGEN_TEXTURES
    }

}
