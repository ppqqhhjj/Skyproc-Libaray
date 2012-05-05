package skyproc;

import lev.debug.LLogger;

/**
 * An extended Levnifty LLogger object that also has a BLOCKED logstream as a place
 * to record any records that were blocked/skipped during the patch.<br>
 * Look at levnifty JavaDocs for more information about LLogger.
 * @author Justin Swanson
 */
class SPLogger extends LLogger {

    /**
     * Creates a new LLogger object with the debug path specified.  If the folder
     * does not exist, it will be created.
     * @param in Path to the folder where all the debug logs should be exported.
     */
    public SPLogger (String in) {
        super(in);
        addSpecial(SpecialTypes.BLOCKED, "Blocked Records.txt");
        addSpecial(PrivateTypes.CONSISTENCY, "Consistency.txt");
    }

    /**
     * List of special types offered by SPLogger.  To log to one of them, use
     * logSpecial from LLogger.
     */
    public static enum SpecialTypes {
	/**
	 * A logstream used for logging which records have been skipped/blockec.
	 */
	BLOCKED;
    }
    
    static enum PrivateTypes {
	CONSISTENCY;
    }
}
