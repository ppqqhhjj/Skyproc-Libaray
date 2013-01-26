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
 * A internal structure found in many major records representing body setups.
 * The use depends on the context of the major record it is inside of.
 *
 * @author Justin Swanson
 */
public class BodyTemplate extends SubShell {

    static SubPrototype BODTproto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new BodyTemplateMain("BODT"));
	    add(new BodyTemplateMain("BOD2"));
	}
    };

    static class BodyTemplateMain extends SubRecordTyped {

	LFlags bodyParts = new LFlags(4);
	LFlags flags = new LFlags(4);
	ArmorType armorType = null;
	boolean valid = false;

	BodyTemplateMain(String type) {
	    super(type);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(bodyParts.export(), 4);
	    if (isBODT()) {
		out.write(flags.export(), 4);
	    }
	    if (armorType != null) {
		out.write(armorType.ordinal());
	    }
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    bodyParts = new LFlags(in.extract(4));
	    if (isBODT()) {
		flags = new LFlags(in.extract(4));
	    }
	    if (!in.isDone()) {
		armorType = ArmorType.values()[in.extractInt(4)];
	    }
	    valid = true;
	}

	@Override
	SubRecord getNew(String type) {
	    return new BodyTemplateMain(type);
	}

	boolean isBODT() {
	    return "BODT".equals(getType());
	}

	@Override
	boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    int out = 4;
	    if (isBODT()) {
		out += 4;
	    }
	    if (armorType != null) {
		out += 4;
	    }
	    return out;
	}
    }

    BodyTemplate() {
	super(BODTproto);
    }

    /**
     * An enum containg various body part slots.
     *
     * @author Justin Swanson
     */
    public enum FirstPersonFlags {

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
	BODY,
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
	CIRCLET,
	/**
	 *
	 */
	EARS,
	/**
	 *
	 */
	BodyAddOn3,
	/**
	 *
	 */
	BodyAddOn4,
	/**
	 *
	 */
	BodyAddOn5,
	/**
	 *
	 */
	BodyAddOn6,
	/**
	 *
	 */
	BodyAddOn7,
	/**
	 *
	 */
	BodyAddOn8,
	/**
	 *
	 */
	DecapitateHead,
	/**
	 *
	 */
	Decapitate,
	/**
	 *
	 */
	BodyAddOn9,
	/**
	 *
	 */
	BodyAddOn10,
	/**
	 *
	 */
	BodyAddOn11,
	/**
	 *
	 */
	BodyAddOn12,
	/**
	 *
	 */
	BodyAddOn13,
	/**
	 *
	 */
	BodyAddOn14,
	/**
	 *
	 */
	BodyAddOn15,
	/**
	 *
	 */
	BodyAddOn16,
	/**
	 *
	 */
	BodyAddOn17,
	/**
	 *
	 */
	FX01,
    }

    /**
     *
     */
    public enum GeneralFlags {

	/**
	 *
	 */
	ModulatesVoice(0),
	/**
	 *
	 */
	NonPlayable(4);
	int value;

	GeneralFlags(int val) {
	    value = val;
	}
    }

    public enum BodyTemplateType {
	Normal ("BODT"),
	Biped ("BOD2");
	String type;
	BodyTemplateType (String in) {
	    type = in;
	}
    }

    BodyTemplateMain getMain(BodyTemplateType type) {
	return (BodyTemplateMain) subRecords.get(type.type);
    }

    @Override
    SubRecord getNew(String type) {
	return new BodyTemplate();
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(BodyTemplateType type, FirstPersonFlags flag, boolean on) {
	BodyTemplateMain main = getMain(type);
	main.bodyParts.set(flag.ordinal(), on);
	main.valid = true;

    }

    /**
     *
     * @param part
     * @return
     */
    public boolean get(BodyTemplateType type, FirstPersonFlags part) {
	BodyTemplateMain main = getMain(type);
	main.valid = true;
	return main.bodyParts.get(part.ordinal());
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(GeneralFlags flag, boolean on) {
	BodyTemplateMain main = getMain(BodyTemplateType.Normal);
	main.valid = true;
	main.flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(GeneralFlags flag) {
	BodyTemplateMain main = getMain(BodyTemplateType.Normal);
	main.valid = true;
	return main.flags.get(flag.value);
    }

    /**
     *
     * @param type
     */
    public void setArmorType(BodyTemplateType type, ArmorType armorType) {
	BodyTemplateMain main = getMain(type);
	main.valid = true;
	main.armorType = armorType;
    }

    /**
     *
     * @return
     */
    public ArmorType getArmorType(BodyTemplateType type) {
	BodyTemplateMain main = getMain(type);
	main.valid = true;
	return main.armorType;
    }
}