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
	    add(new SubData(Type.DEST));
	    add(new SubList<>(new DSTD()));
	    add(new SubData(Type.DMDL));
	    add(new SubData(Type.DMDT));
	    add(new SubData(Type.DMDS));
	}
    };

    DestructionData() {
	super(destructionProto);
    }

    static class DSTD extends SubShell {

	static SubPrototype dstdProto = new SubPrototype() {
	    @Override
	    protected void addRecords() {
		add(new SubData(Type.DSTD));
		add(new SubData(Type.DSTF));
	    }
	};

	DSTD() {
	    super(dstdProto);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DSTD();
	}
    }

    @Override
    SubRecord getNew(Type type) {
	return new DestructionData();
    }

    @Override
    Boolean isValid() {
	return true;
    }
}
