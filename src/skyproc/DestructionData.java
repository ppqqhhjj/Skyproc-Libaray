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
    SubData DSTD = new SubData(Type.DSTD);
    SubData DSTF = new SubData(Type.DSTF);
    SubData DMDL = new SubData(Type.DMDL);
    SubData DMDT = new SubData(Type.DMDT);
    SubData DMDS = new SubData(Type.DMDS);

    static Type[] types = new Type[] { Type.DEST, Type.DSTD, Type.DMDL, Type.DMDT, Type.DMDS, Type.DSTF };

    DestructionData () {
	super(types);

	subRecords.add(DEST);
	subRecords.add(DSTD);
	subRecords.add(DSTF);
	subRecords.add(DMDL);
	subRecords.add(DMDT);
	subRecords.add(DMDS);
    }

    @Override
    SubRecord getNew(Type type) {
	return new DestructionData();
    }

}
