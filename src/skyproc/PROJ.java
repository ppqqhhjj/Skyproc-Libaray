/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
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
	    add(new SubString(Type.NAM1, true));
	    add(new SubData(Type.NAM2));
	    add(new DATA());
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
	ProjectileType projType = ProjectileType.Missile;
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
	float muzzleFlashDuration =0;  //3
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
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    flags.set(in.extract(2));
	    projType = ProjectileType.get(in.extractInt(2));
	    gravity = in.extractFloat();
	    speed = in.extractFloat();
	    range = in.extractFloat();
	    light.setInternal(in.extract(4));
	    muzzleLight.setInternal(in.extract(4));
	    tracerChance = in.extractFloat();
	    proximity = in.extractFloat();
	    timer = in.extractFloat();
	    explosionType.setInternal(in.extract(4));
	    sound.setInternal(in.extract(4));
	    muzzleFlashDuration = in.extractFloat();
	    fadeDuration = in.extractFloat();
	    impactForce = in.extractFloat();
	    explosionSound.setInternal(in.extract(4));
	    disableSound.setInternal(in.extract(4));
	    defaultWeaponSource.setInternal(in.extract(4));
	    coneSpread = in.extractFloat();
	    collisionRadius = in.extractFloat();
	    lifetime = in.extractFloat();
	    relaunchInterval = in.extractFloat();
	    decalData.setInternal(in.extract(4));
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

    public enum ProjectileFlags {

	Explosion(1),
	MuzzleFlash(3),
	CanBeDisabled(5),
	CanBePickedUp(6),
	SuperSonic(7),
	CritPinsLimbs(8),
	PassThroughSmallTransparent(9),
	DisableCombatAimCorrection(10);

	int value;
	ProjectileFlags (int val) {
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
}
