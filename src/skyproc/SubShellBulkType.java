/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lev.LChannel;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public abstract class SubShellBulkType extends SubShell {

    Set<Type> targets;

    SubShellBulkType(Type type_, Type... targets) {
	super(type_);
	this.targets = new HashSet<>(Arrays.asList(targets));
    }

    @Override
    public int getRecordLength(LChannel in) {
	int size = super.getRecordLength(in);
	in.skip(size);
	Type nextType;
	while (!in.isDone()) {
	    try {
		nextType = getNextType(in);
		if (!targets.contains(nextType)) {
		    break;
		}
	    } catch (BadRecord ex) {
		SPGlobal.logException(ex);
		break;
	    }
	    int newSize = super.getRecordLength(in);
	    size += newSize;
	    in.skip(newSize);
	}
	in.pos(in.pos() - size);
	return size;
    }
}
