/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class FACT extends MajorRecordNamed {

    static Type[] types = {Type.FACT};
    SubList<SubData> XNAMs = new SubList<SubData>(new SubData(Type.XNAM));
    SubData DATA = new SubData(Type.DATA);
    SubForm JAIL = new SubForm(Type.JAIL);
    SubForm WAIT = new SubForm(Type.WAIT);
    SubForm STOL = new SubForm(Type.STOL);
    SubForm PLCN = new SubForm(Type.PLCN);
    SubForm CRGR = new SubForm(Type.CRGR);
    SubForm JOUT = new SubForm(Type.JOUT);
    SubData CRVA = new SubData(Type.CRVA);
    SubForm VEND = new SubForm(Type.VEND);
    SubForm VENC = new SubForm(Type.VENC);
    SubData VENV = new SubData(Type.VENV);
    SubList<Rank> ranks = new SubList<Rank>(new Rank());
    SubInt CITC = new SubInt(Type.CITC);
    SubString CIS2 = new SubString(Type.CIS2, true);
    SubData CTDA = new SubData(Type.CTDA);
    SubData PLVD = new SubData(Type.PLVD);

    FACT() {
	super();

	subRecords.add(XNAMs);
	subRecords.add(DATA);
	subRecords.add(JAIL);
	subRecords.add(WAIT);
	subRecords.add(STOL);
	subRecords.add(PLCN);
	subRecords.add(CRGR);
	subRecords.add(JOUT);
	subRecords.add(CRVA);
	subRecords.add(ranks);
	subRecords.add(VEND);
	subRecords.add(VENC);
	subRecords.add(VENV);
	subRecords.add(PLVD);
	subRecords.add(CITC);
	subRecords.add(CTDA);
	subRecords.add(CIS2);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    static class Rank extends SubShell {

	SubInt RNAM = new SubInt(Type.RNAM);
	SubStringPointer MNAM = new SubStringPointer(Type.MNAM, SubStringPointer.Files.STRINGS);
	SubData FNAM = new SubData(Type.FNAM);

	static Type[] types = {Type.RNAM, Type.MNAM, Type.FNAM};

	Rank() {
	    super(types);
	    this.allValid = false;
	    subRecords.add(RNAM);
	    subRecords.add(MNAM);
	    subRecords.add(FNAM);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new Rank();
	}
    }
}
