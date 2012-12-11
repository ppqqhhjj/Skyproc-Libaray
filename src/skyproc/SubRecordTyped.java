/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Justin Swanson
 */
public abstract class SubRecordTyped extends SubRecord {

    ArrayList<Type> types;

    SubRecordTyped(Type t) {
	types = new ArrayList<>(1);
	types.add(t);
    }

    SubRecordTyped(ArrayList<Type> t) {
	types = t;
    }

    @Override
    ArrayList<Type> getTypes() {
	return types;
    }

}
