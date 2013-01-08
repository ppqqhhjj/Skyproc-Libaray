/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.zip.DataFormatException;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class SubFormValid extends SubForm {
    
    SubFormValid(String type) {
	super(type);
	ID = null;
    }
    
    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	ID = new FormID();
	super.parseData(in);
    }
    
    @Override
    boolean isValid() {
	return ID != null;
    }
}
