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

    SubData DEST = new SubData(Type.DEST);
    SubList<DSTD> DSTDs = new SubList<>(new DSTD());
    SubData DMDL = new SubData(Type.DMDL);
    SubData DMDT = new SubData(Type.DMDT);
    SubData DMDS = new SubData(Type.DMDS);

    static Type[] types = new Type[] { Type.DEST, Type.DSTD, Type.DMDL, Type.DMDT, Type.DMDS, Type.DSTF };

    DestructionData () {
	super(types);

	subRecords.add(DEST);
	subRecords.add(DSTDs);
	subRecords.add(DMDL);
	subRecords.add(DMDT);
	subRecords.add(DMDS);
    }

    static class DSTD extends SubShell {

	SubData DSTD = new SubData(Type.DSTD);
	SubData DSTF = new SubData(Type.DSTF);

	static Type[] types = { Type.DSTD, Type.DSTF };

	DSTD() {
	    super(types);
	    subRecords.add(DSTD);
	    subRecords.add(DSTF);
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
