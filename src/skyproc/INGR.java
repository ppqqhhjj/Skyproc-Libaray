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
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class INGR extends MagicItem {

    Type[] types = {Type.INGR};
    SubString MODL = new SubString(Type.MODL, true);
    SubData MODT = new SubData(Type.MODT);
    SubForm YNAM = new SubForm(Type.YNAM);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    DATA DATA = new DATA();
    ENIT ENIT = new ENIT();
    SubString ICON = new SubString(Type.ICON, true);
    SubString MICO = new SubString(Type.MICO, true);
    SubForm ETYP = new SubForm(Type.ETYP);
    public ScriptPackage scripts = new ScriptPackage();

    INGR() {
	super();
	init();
    }

    @Override
    final void init() {
	super.init();
	subRecords.add(scripts);
	subRecords.add(OBND);
	subRecords.add(FULL);
	subRecords.add(keywords);
	subRecords.add(MODL);
	subRecords.add(MODT);
	subRecords.add(YNAM);
	subRecords.add(ZNAM);
	subRecords.add(DATA);
	subRecords.add(ENIT);
	subRecords.add(magicEffects);
	subRecords.add(ICON);
	subRecords.add(MICO);
	subRecords.add(ETYP);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new INGR();
    }

    class DATA extends SubRecord {

	byte[] fluff = new byte[4];
	float weight = 0;

	DATA () {
	    super(Type.DATA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(fluff,4);
	    out.write(weight);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    fluff = in.extract(4);
	    weight = in.extractFloat();
	    if (SPGlobal.logging()) {
		logSync("", "Setting DATA:    Weight: " + weight);
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}
    }

    class ENIT extends SubRecord {

	int baseCost = 0;
	LFlags flags = new LFlags(4);

	ENIT () {
	    super(Type.ENIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(baseCost);
	    out.write(flags.export(),4);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    baseCost = in.extractInt(4);
	    flags.set(in.extract(4));
	    if (SPGlobal.logging()) {
		logSync("", "Base cost: " + baseCost + ", flags: " + flags);
	    }
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ENIT();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}

    }

    public enum INGRFlag {
	ManualCalc(0),
	Food(1),
	ReferencesPersist(8)
	;

	int value;

	INGRFlag (int value) {
	    this.value = value;
	}
    }

    // Get/set

    public void setModel (String groundModel) {
	MODL.setString(groundModel);
    }

    public String getModel () {
	return MODL.string;
    }

    public void setPickupSound (FormID pickupSound) {
	YNAM.setForm(pickupSound);
    }

    public FormID getPickupSound () {
	return YNAM.getForm();
    }

    public void setDropSound (FormID dropSound) {
	ZNAM.setForm(dropSound);
    }

    public FormID getDropSound () {
	return ZNAM.getForm();
    }

    public void setWeight (float weight) {
	DATA.weight = weight;
    }

    public float getWeight() {
	return DATA.weight;
    }

    public void setBaseCost (int baseCost) {
	ENIT.baseCost = baseCost;
    }

    public int getBaseCost () {
	return ENIT.baseCost;
    }

    public void set (INGRFlag flag, boolean on) {
	ENIT.flags.set(flag.value, on);
    }

    public boolean get (INGRFlag flag) {
	return ENIT.flags.is(flag.value);
    }

    public void setInventoryIcon (String filename) {
	ICON.setString(filename);
    }

    public String getInventoryIcon () {
	return ICON.print();
    }

    public void setMessageIcon (String filename) {
	MICO.setString(filename);
    }

    public String getMessageIcon () {
	return MICO.print();
    }

    public void setEquipType (FormID equipType) {
	ETYP.setForm(equipType);
    }

    public FormID getEquipType () {
	return ETYP.getForm();
    }
}
