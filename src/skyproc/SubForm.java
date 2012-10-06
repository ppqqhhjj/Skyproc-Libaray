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
import lev.LChannel;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A SubRecord containing a FormID at the start of its structure.
 *
 * @author Justin Swanson
 */
class SubForm extends SubRecord {

    FormID ID = new FormID();

    void setForm(byte[] in) throws BadParameter {
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " FormID: " + Ln.printHex(in, false, true));
	}
	ID.setInternal(in);
    }

    void copyForm(FormID in) {
	ID = new FormID(in);
    }

    /**
     *
     * @param id FormID to set the record's to.
     */
    public void setForm(FormID id) {
	ID = id;
    }

    byte[] getFormArray(Boolean master) {
	return ID.getInternal(master);
    }

    /**
     *
     * @return The FormID string of the Major Record.
     */
    public String getFormStr() {
	return ID.getArrayStr(true);
    }

    /**
     *
     * @return The name of the mod from which this Major Record originates.
     */
    public ModListing getFormMaster() {
	return ID.getMaster();
    }

    FormID copyOfForm() {
	return new FormID(ID);
    }

    /**
     * Returns the FormID object of the Sub Record. Note that any changes made
     * to this FormID will be reflected in the Sub Record also.
     *
     * @return The FormID object of the Sub Record.
     */
    public FormID getForm() {
	return ID;
    }

    SubForm(Type type_) {
	super(type_);
    }

    SubForm(Type[] type_) {
	super(type_);
    }

    SubForm(LShrinkArray in, Type type_) throws BadRecord, DataFormatException, BadParameter {
	this(type_);
	parseData(in);
    }

    SubForm(Type type, FormID form) {
	this(type);
	setForm(form);
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	setForm(in.extract(4));
    }

    @Override
    public String toString() {
	if (isValid()) {
	    return ID.toString() + " - " + super.toString();
	} else {
	    return super.toString();
	}
    }

    @Override
    Boolean isValid() {
	return ID.isValid();
    }

    @Override
    public String print() {
	return ID.getFormStr();
    }

    @Override
    boolean confirmLink() {
	if (SPGlobal.globalDatabase != null) {
	    return confirmLink(SPGlobal.globalDatabase);
	} else {
	    return true;
	}
    }

    boolean confirmLink(SPDatabase db) {
	return true;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return ID.getContentLength();
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	ID.export(out);
    }

    @Override
    SubRecord getNew(Type type_) {
	return new SubForm(type_);
    }

    /**
     * Takes the FormID into the equals calculations
     *
     * @param obj Another SubFormRecord
     * @return
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubForm other = (SubForm) obj;
	if (this.ID != other.ID && (this.ID == null || !this.ID.equals(other.ID))) {
	    return false;
	}
	return true;
    }

    /**
     * Takes the FormID into the hashcode calculations
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 7;
	hash = 29 * hash + (this.ID != null ? this.ID.hashCode() : 0);
	return hash;
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<FormID>(1);
	out.add(ID);
	return out;
    }
}
