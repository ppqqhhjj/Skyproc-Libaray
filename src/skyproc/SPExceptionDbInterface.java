/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.exceptions.Uninitialized;

/**
 * An interface required to create a custom Exception Set to use with SkyProc.
 * @author Justin Swanson
 */
interface SPExceptionDbInterface<E extends Enum<E>> {

    public E getException (Record record) throws Uninitialized ;

    public Boolean is (Record record, E exceptionType) ;

    public enum NullException {
        NULL;
    }

}
