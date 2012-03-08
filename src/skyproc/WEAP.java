/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class WEAP extends MajorRecordDescription {

    private static final Type[] type = {Type.WEAP};
    SubForm BAMT = new SubForm(Type.BAMT);
    SubForm BIDS = new SubForm(Type.BIDS);
    SubForm CNAM = new SubForm(Type.CNAM);
    CRDT CRDT = new CRDT();
    DATA DATA = new DATA();
    DNAM DNAM = new DNAM();
    SubData EAMT = new SubData(Type.EAMT);
    SubForm EITM = new SubForm(Type.EITM);
    SubForm ETYP = new SubForm(Type.ETYP);
    SubForm INAM = new SubForm(Type.INAM);
    Keywords keywords = new Keywords();
    SubData OBND = new SubData(Type.OBND);
    SubString MODL = new SubString(Type.MODL, true);
    SubData MODS = new SubData(Type.MODS);
    SubData MODT = new SubData(Type.MODT);
    SubForm NAM8 = new SubForm(Type.NAM8);
    SubForm NAM9 = new SubForm(Type.NAM9);
    SubString NNAM = new SubString(Type.NNAM, true);
    SubForm SNAM = new SubForm(Type.SNAM);
    SubForm TNAM = new SubForm(Type.TNAM);
    SubForm UNAM = new SubForm(Type.UNAM);
    /**
     * A script package containing scripts and their properties
     */
    public ScriptPackage scripts = new ScriptPackage();
    SubData VNAM = new SubData(Type.VNAM);
    SubForm WNAM = new SubForm(Type.WNAM);

    WEAP() {
        super();
        subRecords.remove(Type.FULL);
        subRecords.remove(Type.DESC);

        subRecords.add(scripts);
        subRecords.add(OBND);
        subRecords.add(FULL);
        subRecords.add(MODL);
        subRecords.add(MODT);
        subRecords.add(EITM);
        subRecords.add(EAMT);
        subRecords.add(MODS);
        subRecords.add(ETYP);
        subRecords.add(BIDS);
        subRecords.add(BAMT);
        subRecords.add(keywords);
        subRecords.add(DESC);
        subRecords.add(NNAM);
        subRecords.add(INAM);
        subRecords.add(SNAM);
        subRecords.add(WNAM);
        subRecords.add(TNAM);
        subRecords.add(UNAM);
        subRecords.add(NAM9);
        subRecords.add(NAM8);
        subRecords.add(DATA);
        subRecords.add(DNAM);
        subRecords.add(CRDT);
        subRecords.add(VNAM);
        subRecords.add(CNAM);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new WEAP();
    }

    static class DNAM extends SubRecord {

        WeaponType wtype;
        float speed;
        float reach;
        byte[] unknown1;
        float sightFOV;
        byte[] unknown2;
        int vats;
        byte[] unknown3;
        int numProjectiles;
        int embeddedWeapActorValue;
        float minRange;
        float maxRange;
        byte[] unknown5;
        byte[] resist;
        byte[] unknown6;
        float stagger;

        public DNAM() {
            super(Type.DNAM);
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            out.write(wtype.ordinal(), 4);
            out.write(speed);
            out.write(reach);
            out.write(unknown1, 4);
            out.write(sightFOV);
            out.write(unknown2, 4);
            out.write(vats, 1);
            out.write(unknown3, 1);
            out.write(numProjectiles, 1);
            out.write(embeddedWeapActorValue, 1);
            out.write(minRange);
            out.write(maxRange);
            out.write(unknown5, 52);
            out.write(resist, 4);
            out.write(unknown6, 4);
            out.write(stagger);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);
            wtype = WeaponType.values()[in.extractInt(4)];
            speed = in.extractFloat();
            reach = in.extractFloat();
            unknown1 = in.extract(4);
            sightFOV = in.extractFloat();
            unknown2 = in.extract(4);
            vats = in.extractInt(1);
            unknown3 = in.extract(1);
            numProjectiles = in.extractInt(1);
            embeddedWeapActorValue = in.extractInt(1);
            minRange = in.extractFloat();
            maxRange = in.extractFloat();
            unknown5 = in.extract(52);
            resist = in.extract(4);
            unknown6 = in.extract(4);
            stagger = in.extractFloat();
            if (logging()) {
                logSync("", "WType: " + wtype + ", speed: " + speed + ", reach: " + reach);
                logSync("", "SightFOV: " + sightFOV + ", vats: " + vats + ", numProjectiles: " + numProjectiles);
                logSync("", "EmbeddedWeapActorVal: " + embeddedWeapActorValue + ", MinRange: " + minRange + ", MaxRange: " + maxRange);
                logSync("", "stagger: " + stagger);
            }
        }

        @Override
        SubRecord getNew(Type type) {
            return new DNAM();
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
            return 100;
        }
    }

    static class DATA extends SubRecord {

        int value = 0;
        float weight = 0;
        int damage = 0;

        public DATA() {
            super(Type.DATA);
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            out.write(value, 4);
            out.write(weight);
            out.write(damage, 2);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);
            value = in.extractInt(4);
            weight = in.extractFloat();
            damage = in.extractInt(2);
            if (logging()) {
                logSync("", "Value: " + value + ", weight: " + weight + ", damage: " + damage);
            }
        }

        @Override
        SubRecord getNew(Type type) {
            return new DATA();
        }

        @Override
        public void clear() {
            value = 0;
            weight = 0;
            damage = 0;
        }

        @Override
        Boolean isValid() {
            return true;
        }

        @Override
        int getContentLength(Mod srcMod) {
            return 10;
        }
    }

    static class CRDT extends SubRecord {

        int critDmg;
        byte[] unknown0;
        float critMult;
        int onDeath;
        byte[] unknown;
        FormID critEffect = new FormID();

        public CRDT() {
            super(Type.CRDT);
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            out.write(critDmg, 4);
            out.write(critMult);
            out.write(onDeath, 1);
            out.write(unknown, 3);
            critEffect.export(out);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);
            critDmg = in.extractInt(2);
            unknown0 = in.extract(2);
            critMult = in.extractFloat();
            onDeath = in.extractInt(1);
            unknown = in.extract(3);
            critEffect.setInternal(in.extract(4));
            if (logging()) {
                logSync("", "critDmg: " + critDmg + ", critMult: " + critMult + ", crit effect: " + critEffect);
            }
        }

        @Override
        SubRecord getNew(Type type) {
            return new CRDT();
        }

        @Override
        final public void clear() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        Boolean isValid() {
            return true;
        }

        @Override
        int getContentLength(Mod srcMod) {
            return 16;
        }
    }

    /**
     * An enum to represent to Weapon Type options
     */
    public enum WeaponType {

        /**
         *
         */
        Projectile,
        /**
         *
         */
        OneHSword,
        /**
         *
         */
        Dagger,
        /**
         *
         */
        OneHAxe,
        /**
         *
         */
        OneHBlunt,
        /**
         *
         */
        TwoHSword,
        /**
         *
         */
        TwoHBluntAxe,
        /**
         *
         */
        Bow,
        /**
         *
         */
        Staff
    }

    // Get /set
    /**
     *
     * @param value
     */
    public void setValue(int value) {
        DATA.value = Math.abs(value);
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return DATA.value;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
        DATA.weight = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
        return DATA.weight;
    }

    /**
     *
     * @param damage
     */
    public void setDamage(int damage) {
        DATA.damage = Math.abs(damage) % 0xFFFF;  // can't be more than 2 bytes
    }

    /**
     *
     * @param amount
     */
    public void setEnchantmentCharge(int amount) {
        EAMT.setDataAbs(amount, 2, 2);
    }

    /**
     *
     * @return
     */
    public int getEnchantmentCharge() {
        return EAMT.toInt();
    }

    /**
     *
     * @param id
     */
    public void setEnchantment(FormID id) {
        EITM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getEnchantment() {
        return EITM.getForm();
    }

    /**
     *
     * @param id
     */
    public void setEquipmentSlot(FormID id) {
        ETYP.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getEquipmentSlot() {
        return ETYP.getForm();
    }

    /**
     *
     * @param id
     */
    public void setImpactSet(FormID id) {
        INAM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getImpactSet() {
        return INAM.getForm();
    }

    /**
     *
     * @param filename
     */
    public void setModelFilename(String filename) {
        MODL.setString(filename);
    }

    /**
     *
     * @return
     */
    public String getModelFilename() {
        return MODL.print();
    }

    /**
     *
     * @param id
     */
    public void setSheathSound(FormID id) {
        NAM8.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getSheathSound() {
        return NAM8.getForm();
    }

    /**
     *
     * @param id
     */
    public void setDrawSound(FormID id) {
        NAM9.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getDrawSound() {
        return NAM9.getForm();
    }

    /**
     *
     * @param id
     */
    public void setSwingSound(FormID id) {
        TNAM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getSwingSound() {
        return TNAM.getForm();
    }

    /**
     *
     * @param id
     */
    public void setBoundWeaponSound(FormID id) {
        UNAM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getBoundWeaponSound() {
        return UNAM.getForm();
    }

    /**
     *
     * @param in
     */
    public void setWeaponType(WeaponType in) {
        DNAM.wtype = in;
    }

    /**
     *
     * @return
     */
    public WeaponType getWeaponType() {
        return DNAM.wtype;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(float speed) {
        DNAM.speed = speed;
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
        return DNAM.speed;
    }

    /**
     *
     * @param reach
     */
    public void setReach(float reach) {
        DNAM.reach = reach;
    }

    /**
     *
     * @return
     */
    public float getReach() {
        return DNAM.reach;
    }

    /**
     *
     * @param fov
     */
    public void setSightFOV(float fov) {
        DNAM.sightFOV = fov;
    }

    /**
     *
     * @return
     */
    public float getSightFOV() {
        return DNAM.sightFOV;
    }

    /**
     *
     * @param vats
     */
    public void setVATS(int vats) {
        DNAM.vats = vats;
    }

    /**
     *
     * @return
     */
    public int getVATS() {
        return DNAM.vats;
    }

    /**
     *
     * @param numProj
     */
    public void setNumProjectiles(int numProj) {
        DNAM.numProjectiles = numProj;
    }

    /**
     *
     * @return
     */
    public int getNumProjectiles() {
        return DNAM.numProjectiles;
    }

    /**
     *
     * @param minRange
     */
    public void setMinRange(float minRange) {
        DNAM.minRange = minRange;
    }

    /**
     *
     * @return
     */
    public float getMinRange() {
        return DNAM.minRange;
    }

    /**
     *
     * @param maxRange
     */
    public void setMaxRange(float maxRange) {
        DNAM.maxRange = maxRange;
    }

    /**
     *
     * @return
     */
    public float getMaxRange() {
        return DNAM.maxRange;
    }

    /**
     *
     * @param stagger
     */
    public void setStagger(float stagger) {
        DNAM.stagger = stagger;
    }

    /**
     *
     * @return
     */
    public float getStagger() {
        return DNAM.stagger;
    }

    /**
     *
     * @param critDmg
     */
    public void setCritDamage(int critDmg) {
        CRDT.critDmg = critDmg;
    }

    /**
     *
     * @return
     */
    public int getCritDamage() {
        return CRDT.critDmg;
    }

    /**
     *
     * @param critMult
     */
    public void setCritMult(float critMult) {
        CRDT.critMult = critMult;
    }

    /**
     *
     * @return
     */
    public float getCritMult() {
        return CRDT.critMult;
    }

    /**
     *
     * @param onDeath
     */
    public void setCritEffectOnDeath(boolean onDeath) {
        if (onDeath) {
            CRDT.onDeath = 1;
        } else {
            CRDT.onDeath = 0;
        }
    }

    /**
     *
     * @return
     */
    public boolean getCritEffectOnDeath () {
        if (CRDT.onDeath == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @param critEffect
     */
    public void setCritEffect (FormID critEffect) {
        CRDT.critEffect = critEffect;
    }

    /**
     *
     * @return
     */
    public FormID getCritEffect () {
        return CRDT.critEffect;
    }
//
//    public SubList<SubForm> getKeywordIDs () {
//	return keywords;
//    }
}
