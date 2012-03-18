/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
class Conditions extends SubShell {

    Conditions () {
	super(Type.CTDA);
    }

    @Override
    SubRecord getNew(Type type) {
	return new Conditions();
    }

}
