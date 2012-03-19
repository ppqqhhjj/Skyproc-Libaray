/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class MagicEffectRef extends SubShell {

    private static Type[] types = {Type.EFID, Type.EFIT, Type.CTDA, Type.CIS1, Type.CIS2};
    SubForm EFID = new SubForm(Type.EFID);
    EFIT EFIT = new EFIT();
    SubList<Condition> CTDAs = new SubList<Condition>(new Condition());

    public MagicEffectRef (FormID magicEffectRef) {
	super(types);
	init();
	EFID.setForm(magicEffectRef);
    }

    MagicEffectRef() {
	super(types);
	init();
    }

    MagicEffectRef(LShrinkArray in) throws DataFormatException, BadParameter, BadRecord {
	this();
	parseData(in);
    }

    final void init() {
	subRecords.add(EFID);
	subRecords.add(EFIT);
	subRecords.add(CTDAs);
    }

    @Override
    SubRecord getNew(Type type) {
	return new MagicEffectRef();
    }

    @Override
    Boolean isValid() {
	return EFID.isValid() && EFIT.isValid();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final MagicEffectRef other = (MagicEffectRef) obj;
	if (this.EFID != other.EFID && (this.EFID == null || !this.EFID.equals(other.EFID))) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 71 * hash + (this.EFID != null ? this.EFID.hashCode() : 0);
	return hash;
    }

    static class EFIT extends SubRecord {

	float magnitude = 0;
	int AOE = 0;
	int duration = 0;

	EFIT () {
	    super(Type.EFIT);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(magnitude);
	    out.write(AOE);
	    out.write(duration);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    magnitude = in.extractFloat();
	    AOE = in.extractInt(4);
	    duration = in.extractInt(4);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new EFIT();
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
	    return 12;
	}
    }

    // Get/Set
    public void setMagicRef (FormID magicRef) {
	EFID.setForm(magicRef);
    }

    public FormID getMagicRef () {
	return EFID.getForm();
    }

    public void setMagnitude (float magnitude) {
	EFIT.magnitude = magnitude;
    }

    public float getMagnitude () {
	return EFIT.magnitude;
    }

    public void setAreaOfEffect (int aoe) {
	EFIT.AOE = aoe;
    }

    public int getAreaOfEffect () {
	return EFIT.AOE;
    }

    public void setDuration (int duration) {
	EFIT.duration = duration;
    }

    public int getDuration () {
	return EFIT.duration;
    }
}