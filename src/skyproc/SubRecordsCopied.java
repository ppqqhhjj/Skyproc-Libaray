/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import lev.LExporter;
import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
public class SubRecordsCopied extends SubRecords {

    SubRecords orig;

    @Override
    protected void export(LExporter out, Mod srcMod) throws IOException {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<SubRecord> iterator() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(Type t) {
	return orig.contains(t);
    }

    @Override
    public SubRecord get(Type in) {
	if (!map.containsKey(in)) {
	    map.put(in, (SubRecord)Ln.deepCopy(orig.get(in)));
	}
	return map.get(in);
    }
    
    @Override
    public Set<Type> getTypes() {
	return orig.getTypes();
    }
}
