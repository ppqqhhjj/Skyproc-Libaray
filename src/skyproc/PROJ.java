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
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class PROJ extends MajorRecordNamed {

    static final SubRecordsPrototype PROJprototype = new SubRecordsPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    add(new SubData(Type.OBND));
	    reposition(Type.FULL);
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new SubData(Type.MODS));
	    add(new DestructionData());
	    add(new DATA());
	    add(new SubString(Type.NAM1, true));
	    add(new SubData(Type.NAM2));
	    add(new SubData(Type.VNAM)); // SoundVolume
	}
    };
    static Type[] types = {Type.PROJ};

    PROJ() {
	super();
	subRecords.setPrototype(PROJprototype);
    }

    static class DATA extends SubRecord {

	LFlags flags = new LFlags(2);
	LFlags projType = new LFlags(2);
	float gravity = 0;
	float speed = 0;
	float range = 0;   //1
	FormID light = new FormID();
	FormID muzzleLight = new FormID();
	float tracerChance = 0;
	float proximity = 0;  // 2
	float timer = 0;
	FormID explosionType = new FormID();
	FormID sound = new FormID();
	float muzzleFlashDuration = 0;  //3
	float fadeDuration = 0;
	float impactForce = 0;
	FormID explosionSound = new FormID();
	FormID disableSound = new FormID();  //4
	FormID defaultWeaponSource = new FormID();
	float coneSpread = 0;
	float collisionRadius = 0;
	float lifetime = 0; //5
	float relaunchInterval = 0;
	FormID decalData = new FormID();
	byte[] collisionLayer = new byte[4];

	DATA() {
	    super(Type.DATA);
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>();
	    out.add(light);
	    out.add(muzzleLight);
	    out.add(explosionType);
	    out.add(sound);
	    out.add(explosionSound);
	    out.add(disableSound);
	    out.add(defaultWeaponSource);
	    out.add(decalData);
	    return out;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export(), 2);
	    out.write(projType.export(), 2);
	    out.write(gravity);
	    out.write(speed);
	    out.write(range);
	    light.export(out);
	    muzzleLight.export(out);
	    out.write(tracerChance);
	    out.write(proximity);
	    out.write(timer);
	    explosionType.export(out);
	    sound.export(out);
	    out.write(muzzleFlashDuration);
	    out.write(fadeDuration);
	    out.write(impactForce);
	    explosionSound.export(out);
	    disableSound.export(out);
	    defaultWeaponSource.export(out);
	    out.write(coneSpread);
	    out.write(collisionRadius);
	    out.write(lifetime);
	    out.write(relaunchInterval);
	    decalData.export(out);
	    out.write(collisionLayer);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    flags.set(in.extract(2));
	    projType.set(in.extract(2));
	    gravity = in.extractFloat();
	    speed = in.extractFloat();
	    range = in.extractFloat();   //16
	    light.setInternal(in.extract(4));
	    muzzleLight.setInternal(in.extract(4));
	    tracerChance = in.extractFloat();
	    proximity = in.extractFloat();  //32
	    timer = in.extractFloat();
	    explosionType.setInternal(in.extract(4));
	    sound.setInternal(in.extract(4));
	    muzzleFlashDuration = in.extractFloat();  //48
	    fadeDuration = in.extractFloat();
	    impactForce = in.extractFloat();
	    explosionSound.setInternal(in.extract(4));
	    disableSound.setInternal(in.extract(4));  //64
	    defaultWeaponSource.setInternal(in.extract(4));
	    coneSpread = in.extractFloat();
	    collisionRadius = in.extractFloat();
	    lifetime = in.extractFloat(); // 80
	    relaunchInterval = in.extractFloat();
	    if (!in.isDone()) {
		decalData.setInternal(in.extract(4));
	    }
	    if (!in.isDone()) {
		collisionLayer = in.extract(4);  // 92
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 92;
	}
    }

    public enum ProjectileFlag {

	Explosion(1),
	MuzzleFlash(3),
	CanBeDisabled(5),
	CanBePickedUp(6),
	SuperSonic(7),
	CritPinsLimbs(8),
	PassThroughSmallTransparent(9),
	DisableCombatAimCorrection(10);
	int value;

	ProjectileFlag(int val) {
	    value = val;
	}
    }

    public enum ProjectileType {

	Missile, //1
	Lobber, //2
	Beam, //4
	Flame, //8
	Cone, //10
	Barrier, //20
	Arrow; //40

	static ProjectileType get(int value) {
	    switch (value) {
		case 1:
		    return Missile;
		case 2:
		    return Lobber;
		case 4:
		    return Beam;
		case 8:
		    return Flame;
		case 16:
		    return Cone;
		case 32:
		    return Barrier;
		default:
		    return Arrow;
	    }
	}
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new PROJ();
    }

    //Get/Set
    /**
     *
     * @param filename
     */
    public void setModel(String filename) {
	subRecords.setSubString(Type.MODL, filename);
    }

    /**
     *
     * @return
     */
    public String getModel() {
	return subRecords.getSubString(Type.MODL).print();
    }

    /**
     *
     * @param filename
     */
    public void setEffectModel(String filename) {
	subRecords.setSubString(Type.NAM1, filename);
    }

    /**
     *
     * @return
     */
    public String getEffectModel() {
	return subRecords.getSubString(Type.NAM1).print();
    }

    DATA getDATA() {
	return (DATA) subRecords.get(Type.DATA);
    }

    public void set(ProjectileFlag flag, boolean on) {
	getDATA().flags.set(flag.value, on);
    }

    public boolean get(ProjectileFlag flag) {
	return getDATA().flags.get(flag.value);
    }

    public void setType(ProjectileType t) {
	LFlags flags = getDATA().projType;
	flags.clear();
	flags.set(t.ordinal(), true);
    }

}
