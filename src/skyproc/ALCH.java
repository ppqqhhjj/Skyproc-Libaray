/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * Alchemy Records
 *
 * @author Justin Swanson
 */
public class ALCH extends MagicItem {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.ALCH}));
    static final SubPrototype ALCHproto = new SubPrototype(MagicItem.magicItemProto) {

	@Override
	protected void addRecords() {
	    remove(Type.DESC);
	    add(new SubString(Type.MODL, true));
	    add(new SubData(Type.MODT));
	    add(new AltTextures(Type.MODS));
	    add(new SubForm(Type.YNAM));
	    add(new SubForm(Type.ZNAM));
	    add(new SubData(Type.MODS));
	    add(new SubFloat(Type.DATA));
	    add(new ENIT());
	    add(new SubString(Type.ICON, true));
	    add(new SubString(Type.MICO, true));
	    add(new SubForm(Type.ETYP));
	    reposition(Type.EFID);
	}
    };
    static final class ENIT extends SubRecordTyped {

	int value;
	LFlags flags = new LFlags(4);
	FormID addiction = new FormID();
	byte[] addictionChance = new byte[4];
	FormID useSound = new FormID();

	ENIT() {
	    super(Type.ENIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(value);
	    out.write(flags.export());
	    addiction.export(out);
	    out.write(addictionChance, 4);
	    useSound.export(out);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    value = in.extractInt(4);
	    flags.set(in.extract(4));
	    addiction.setInternal(in.extract(4));
	    addictionChance = in.extract(4);
	    useSound.setInternal(in.extract(4));
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(2);
	    out.add(addiction);
	    out.add(useSound);
	    return out;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 20;
	}
    }
    
    // Enums
    /**
     *
     */
    public enum ALCHFlag {

	/**
	 *
	 */
	ManualCalc(0),
	/**
	 *
	 */
	Food(1),
	/**
	 *
	 */
	Medicine(16),
	/**
	 *
	 */
	Poison(17);
	int value;

	ALCHFlag(int in) {
	    value = in;
	}
    }
    
    // Common Functions
    ALCH() {
	super();
	subRecords.setPrototype(ALCHproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new ALCH();
    }

    // Get / set
    /**
     *
     * @param groundModel
     */
    public void setModel(String groundModel) {
	subRecords.setSubString(Type.MODL, groundModel);
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
     * @param pickupSound
     */
    public void setPickupSound(FormID pickupSound) {
	subRecords.setSubForm(Type.YNAM, pickupSound);
    }

    /**
     *
     * @return
     */
    public FormID getPickupSound() {
	return subRecords.getSubForm(Type.YNAM).getForm();
    }

    /**
     *
     * @param dropSound
     */
    public void setDropSound(FormID dropSound) {
	subRecords.setSubForm(Type.ZNAM, dropSound);
    }

    /**
     *
     * @return
     */
    public FormID getDropSound() {
	return subRecords.getSubForm(Type.ZNAM).getForm();
    }

    ENIT getEnit() {
	return (ENIT) subRecords.get(Type.ENIT);
    }

    /**
     *
     * @param value
     */
    public void setValue(int value) {
	getEnit().value = value;
    }

    /**
     *
     * @return
     */
    public int getValue() {
	return getEnit().value;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(ALCHFlag flag, boolean on) {
	getEnit().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(ALCHFlag flag) {
	return getEnit().flags.get(flag.value);
    }

    /**
     *
     * @param addiction
     */
    public void setAddiction(FormID addiction) {
	getEnit().addiction = addiction;
    }

    /**
     *
     * @return
     */
    public FormID getAddiction() {
	return getEnit().addiction;
    }

    /**
     *
     * @param useSound
     */
    public void setUseSound(FormID useSound) {
	getEnit().useSound = useSound;
    }

    /**
     *
     * @return
     */
    public FormID getUseSound() {
	return getEnit().useSound;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	subRecords.setSubFloat(Type.DATA, weight);
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return subRecords.getSubFloat(Type.DATA).get();
    }

    /**
     *
     * @param filename
     */
    public void setInventoryIcon(String filename) {
	subRecords.setSubString(Type.ICON, filename);
    }

    /**
     *
     * @return
     */
    public String getInventoryIcon() {
	return subRecords.getSubString(Type.ICON).print();
    }

    /**
     *
     * @param filename
     */
    public void setMessageIcon(String filename) {
	subRecords.setSubString(Type.MICO, filename);
    }

    /**
     *
     * @return
     */
    public String getMessageIcon() {
	return subRecords.getSubString(Type.MICO).print();
    }

    /**
     *
     * @param equipType
     */
    public void setEquipType(FormID equipType) {
	subRecords.setSubForm(Type.ETYP, equipType);
    }

    /**
     *
     * @return
     */
    public FormID getEquipType() {
	return subRecords.getSubForm(Type.ETYP).getForm();
    }
}
