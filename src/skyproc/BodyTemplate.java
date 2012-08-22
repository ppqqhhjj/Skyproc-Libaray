/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.LStream;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A internal structure found in many major records representing body setups.
 * The use depends on the context of the major record it is inside of.
 *
 * @author Justin Swanson
 */
public class BodyTemplate extends SubRecord {

    LFlags bodyParts;
    LFlags flags;
    int armorType;
    boolean old = false;

    BodyTemplate() {
	super(Type.BODT);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	out.write(bodyParts.export(), 4);
	out.write(flags.export(), 4);
	if (!old) {
	    out.write(armorType);
	}
    }

    @Override
    void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
	super.parseData(in);
	bodyParts = new LFlags(in.extract(4));
	flags = new LFlags(in.extract(4));
	if (!in.isDone()) {
	    armorType = in.extractInt(4);
	} else {
	    old = true;
	}
    }

    @Override
    SubRecord getNew(Type type) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    Boolean isValid() {
	return true;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return old ? 8 : 12;
    }

    /**
     * An enum containg various body part slots.
     *
     * @author Justin Swanson
     */
    public enum BodyPart {

	/**
	 *
	 */
	HEAD,
	/**
	 *
	 */
	HAIR,
	/**
	 *
	 */
	CHEST,
	/**
	 *
	 */
	HANDS,
	/**
	 *
	 */
	FOREARMS,
	/**
	 *
	 */
	AMULET,
	/**
	 *
	 */
	RING,
	/**
	 *
	 */
	FEET,
	/**
	 *
	 */
	CALVES,
	/**
	 *
	 */
	SHIELD,
	/**
	 *
	 */
	TAIL,
	/**
	 *
	 */
	LONG_HAIR,
	/**
	 *
	 */
	FOREHEAD,
	/**
	 *
	 */
	EARS_EYES
    }

    /**
     *
     */
    public enum BodyTemplateFlag {

	/**
	 *
	 */
	PLAYABLE
    }

    /**
     *
     * @param part
     * @param on
     */
    public void set(BodyPart part, Boolean on) {
	bodyParts.set(part.ordinal(), on);
    }

    /**
     *
     * @param part
     * @return
     */
    public boolean get(BodyPart part) {
	return bodyParts.get(part.ordinal());
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(BodyTemplateFlag flag, Boolean on) {
	flags.set(4, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(BodyTemplateFlag flag) {
	return flags.get(4);
    }

    /**
     *
     * @param type
     */
    public void setArmorType(ArmorType type) {
	armorType = type.ordinal();
	old = false;
    }

    /**
     *
     * @return
     */
    public ArmorType getArmorType() {
	if (!old) {
	    return ArmorType.values()[armorType];
	} else {
	    return ArmorType.CLOTHING;
	}
    }
}