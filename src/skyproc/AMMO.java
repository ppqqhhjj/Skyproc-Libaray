/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
public class AMMO extends MajorRecordDescription {

    static Type[] types = {Type.AMMO};
    SubData OBND = new SubData(Type.OBND);
    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    SubForm YNAM = new SubForm(Type.YNAM);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    /**
     *
     */
    public KeywordSet keywords = new KeywordSet();
    DATA DATA = new DATA();

    AMMO() {
	super();
	subRecords.remove(Type.FULL);
	subRecords.remove(Type.DESC);

	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(MODL);
	subRecords.add(MODT);
	subRecords.add(YNAM);
	subRecords.add(ZNAM);
	subRecords.add(description);
	subRecords.add(keywords);
	subRecords.add(DATA);
    }

    @Override
    Record getNew() {
	return new AMMO();
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    class DATA extends SubRecord {

	FormID projectile = new FormID();
	LFlags flags = new LFlags(4);
	float damage = 0;
	int value = 0;

	DATA() {
	    super(Type.DATA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    projectile.export(out);
	    out.write(flags.export(), 4);
	    out.write(damage);
	    out.write(value);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    projectile.setInternal(in.extract(4));
	    flags.set(in.extract(4));
	    damage = in.extractFloat();
	    value = in.extractInt(4);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 16;
	}

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    return new ArrayList<FormID>(0);
	}
    }

    /**
     *
     */
    public enum AMMOFlag {

	/**
	 *
	 */
	IgnoresWeaponResistance,
	/**
	 *
	 */
	VanishesWhenNotInFlight;
    }

    //Get/Set
    /**
     *
     * @param path
     */
    public void setModel(String path) {
	MODL.setString(path);
    }

    /**
     *
     * @return
     */
    public String getModel() {
	return MODL.print();
    }

    /**
     *
     * @param sound
     */
    public void setPickupSound(FormID sound) {
	YNAM.setForm(sound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return YNAM.getForm();
    }

    /**
     *
     * @param sound
     */
    public void setDropSound(FormID sound) {
	ZNAM.setForm(sound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return ZNAM.getForm();
    }

    /**
     *
     * @param projectile
     */
    public void setProjectile(FormID projectile) {
	DATA.projectile = projectile;
    }

    /**
     *
     * @return
     */
    public FormID getProjectile() {
	return DATA.projectile;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(AMMOFlag flag, boolean on) {
	DATA.flags.set(flag.ordinal() + 1, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(AMMOFlag flag) {
	return DATA.flags.get(flag.ordinal() + 1);
    }

    /**
     *
     * @param damage
     */
    public void setDamage(float damage) {
	DATA.damage = damage;
    }

    /**
     *
     * @return
     */
    public float getDamage() {
	return DATA.damage;
    }

    /**
     *
     * @param gold
     */
    public void setValue(int gold) {
	DATA.value = gold;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return DATA.value;
    }
}
