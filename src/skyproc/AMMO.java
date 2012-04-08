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

    public enum AMMOFlag {

	IgnoresWeaponResistance,
	VanishesWhenNotInFlight;
    }

    //Get/Set
    public void setModel(String path) {
	MODL.setString(path);
    }

    public String getModel() {
	return MODL.print();
    }

    public void setPickupSound(FormID sound) {
	YNAM.setForm(sound);
    }

    public FormID getPickupSound() {
	return YNAM.getForm();
    }

    public void setDropSound(FormID sound) {
	ZNAM.setForm(sound);
    }

    public FormID getDropSound() {
	return ZNAM.getForm();
    }

    public void setProjectile(FormID projectile) {
	DATA.projectile = projectile;
    }

    public FormID getProjectile() {
	return DATA.projectile;
    }

    public void set(AMMOFlag flag, boolean on) {
	DATA.flags.set(flag.ordinal() + 1, on);
    }

    public boolean get(AMMOFlag flag) {
	return DATA.flags.get(flag.ordinal() + 1);
    }

    public void setDamage(float damage) {
	DATA.damage = damage;
    }

    public float getDamage() {
	return DATA.damage;
    }

    public void setValue(int gold) {
	DATA.value = gold;
    }

    public int getValue() {
	return DATA.value;
    }
}
