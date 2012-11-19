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

    static final SubRecordsPrototype FACTproto = new SubRecordsPrototype(MajorRecordNamed.namedProto) {

	@Override
	protected void addRecords() {
	    add(new SubList<>(new SubData(Type.XNAM)));
	    add(new SubData(Type.DATA));
	    add(new SubForm(Type.JAIL));
	    add(new SubForm(Type.WAIT));
	    add(new SubForm(Type.STOL));
	    add(new SubForm(Type.PLCN));
	    add(new SubForm(Type.CRGR));
	    add(new SubForm(Type.JOUT));
	    add(new SubData(Type.CRVA));
	    add(new SubList<>(new Rank()));
	    add(new SubForm(Type.VEND));
	    add(new SubForm(Type.VENC));
	    add(new SubData(Type.VENV));
	    add(new SubData(Type.PLVD));
	    add(new SubInt(Type.CITC));
	    add(new SubData(Type.CTDA));
	    add(new SubString(Type.CIS2, true));
	}
    };
    static Type[] types = {Type.FACT};

    FACT() {
	super();
	subRecords.setPrototype(FACTproto);
    }

    @Override
    Type[] getTypes() {
	return types;
    }

    @Override
    Record getNew() {
	return new FACT();
    }

    static class Rank extends SubShell {

	SubInt RNAM = new SubInt(Type.RNAM);
	SubStringPointer MNAM = new SubStringPointer(Type.MNAM, SubStringPointer.Files.STRINGS);
	SubData FNAM = new SubData(Type.FNAM);

	static Type[] types = {Type.RNAM, Type.MNAM, Type.FNAM};

	Rank() {
	    super(types);
	    subRecords.add(RNAM);
	    subRecords.add(MNAM);
	    subRecords.add(FNAM);
	}

	@Override
	Boolean isValid() {
	    return RNAM.isValid()
		    || MNAM.isValid()
		    || FNAM.isValid();
	}

	@Override
	SubRecord getNew(Type type) {
	    return new FACT.Rank();
	}

    }
}
