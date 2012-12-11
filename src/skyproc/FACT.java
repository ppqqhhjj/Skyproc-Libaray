/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Justin Swanson
 */
public class FACT extends MajorRecordNamed {

    static final SubPrototype FACTproto = new SubPrototype(MajorRecordNamed.namedProto) {
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
    private final static ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.FACT}));

    FACT() {
	super();
	subRecords.setPrototype(FACTproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new FACT();
    }

    static class Rank extends SubShell {

	static SubPrototype rankProto = new SubPrototype() {
	    @Override
	    protected void addRecords() {
		add(new SubInt(Type.RNAM));
		add(new SubStringPointer(Type.MNAM, SubStringPointer.Files.STRINGS));
		add(new SubData(Type.FNAM));
	    }
	};

	Rank() {
	    super(rankProto);
	}

	@Override
	Boolean isValid() {
	    return subRecords.isAnyValid();
	}

	@Override
	SubRecord getNew(Type type) {
	    return new Rank();
	}
    }
}
