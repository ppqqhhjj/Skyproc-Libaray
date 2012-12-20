/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class DestructionData extends SubShell {

    static SubPrototype destructionProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubData("DEST"));
	    add(new SubList<>(new DSTD()));
	    add(new SubData("DMDL"));
	    add(new SubData("DMDT"));
	    add(new SubData("DMDS"));
	}
    };

    DestructionData() {
	super(destructionProto);
    }

    static class DSTD extends SubShell {

	static SubPrototype dstdProto = new SubPrototype() {
	    @Override
	    protected void addRecords() {
		add(new SubData("DSTD"));
		add(new SubData("DSTF"));
	    }
	};

	DSTD() {
	    super(dstdProto);
	}

	@Override
	SubRecord getNew(String type) {
	    return new DSTD();
	}
    }

    @Override
    SubRecord getNew(String type) {
	return new DestructionData();
    }

    @Override
    Boolean isValid() {
	return true;
    }
}
