/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.debug;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import lev.Ln;
import skyproc.SPGlobal;

/**
 * A debug log package that offers several locations and options for outputting
 * debug information.
 *
 * @author Justin Swanson
 */
public class LLogger {

    private String debugPath;
    private LDebug main;
    private LDebug synced;
    private boolean syncing = false;
    private LDebug asynced;
    private boolean logging = true;
    private boolean loggingSync = true;
    private static boolean mainLogSwitch = true;
    private Map<Enum, LDebug> special = new HashMap<Enum, LDebug>();

    /**
     *
     * @param debugPath Path to create a LLogger debug package.
     */
    public LLogger(String debugPath) {
	this.debugPath = debugPath;
	Ln.removeDirectory(new File(debugPath));
	main = new LDebug(debugPath + "=--Debug Overview--=.txt", 50);
	asynced = new LDebug(debugPath + "Asynchronous log.txt", 50);
	synced = new LDebug();
    }

    /**
     * Adds a special log tied to the Enum key, that can be exported to by using
     * that key.
     *
     * @param key Key to add the special log under
     * @param filePath Path to give the special log.
     */
    public void addSpecial(Enum key, String filePath) {
	special.put(key, new LDebug(debugPath + filePath, 50));
    }

    /**
     * A function that will log messages to the synchronized log if the syncing
     * flag is on. Otherwise, it will output to the asynchronized log.
     *
     * @param header
     * @param log
     */
    public void logSync(String header, String... log) {
	if (loggingSync() || syncing && logging()) {
	    if (syncing) {
		synced.w(header, log);
	    } else {
		log(header, log);
	    }
	}
    }

    /**
     * Creates a new sync log in the desired location.
     *
     * @param filePath
     */
    public void newSyncLog(String filePath) {
	if (logging()) {
	    synced.openDebug(debugPath + filePath, 50);
	}
    }

    /**
     * Turn the synchronized logging on/off. If this is on, all logSync() output
     * will go to the synchronized log; if off, the messages will go to the
     * asynchronous log instead.
     *
     * @param on
     */
    public void sync(boolean on) {
	syncing = on;
    }

    /**
     *
     * @return Whether the LLogger is current output to the sync log.
     */
    public boolean sync() {
	return syncing;
    }

    /**
     * Outputs a message to the main debug overview log.
     *
     * @param header
     * @param log
     */
    public void logMain(String header, String... log) {
	main.w(header, log);
	main.flushDebug();
    }

    /**
     * Logs an error message on both to the sync log and to the main overview
     * log.
     *
     * @param header
     * @param log
     */
    public void logError(String header, String... log) {
	LDebug logger = sync() ? synced : asynced;
	logMain("ERROR", "File " + logger.getOpenPath());
	logMain("ERROR", "   Line " + logger.line());
	String message = "   Message: ";
	for (String s : log) {
	    message += s;
	}
	logMain("ERROR", message);
	logSync(header, log);
	flush();
    }

    /**
     *
     * @return Whether the LLogger is on/off.
     */
    public boolean logging() {
	return logging && mainLogSwitch;
    }

    /**
     * Turns the LLogger on/off.
     *
     * @param on
     */
    public void logging(Boolean on) {
	logging = on;
    }

    /**
     *
     * @return Whether the LLogger's sync log is on/off.
     */
    public boolean loggingSync() {
	return loggingSync && logging && mainLogSwitch;
    }

    /**
     * Turns the LLogger sync log on/off.
     *
     * @param on
     */
    public void loggingSync(Boolean on) {
	loggingSync = on;
    }

    void logException(String dump) {
	main.writeException(dump);
    }

    /**
     * Used for printing exception stack data to logs.
     *
     * @param e Exception to print.
     */
    public void logException(Exception e) {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw, true);
	e.printStackTrace(pw);
	pw.flush();
	sw.flush();
	logSync("EXCEPTION", sw.toString());
	logException(sw.toString());
    }

    /**
     * A global switch that allows/blocks all LLoggers to output.
     *
     * @param in
     */
    public void setAllLogging(boolean in) {
	mainLogSwitch = in;
    }

    /**
     * Forces all logs to export their buffers.
     */
    public void flush() {
	if (logging()) {
	    main.flushDebug();
	    asynced.flushDebug();
	    synced.flushDebug();
	    for (LDebug d : special.values()) {
		d.flushDebug();
	    }
	}
    }

    /**
     * Logs to a special log based on the given enum. You must create these
     * special logs ahead of time.
     *
     * @param e Enum key to log to.
     * @param header
     * @param log
     */
    public void logSpecial(Enum e, String header, String... log) {
	if (logging()) {
	    if (special.containsKey(e)) {
		special.get(e).w(header, log);
	    }
	}
    }

    /**
     * Logs to the asynchronous log.
     *
     * @param header
     * @param log
     */
    public void log(String header, String... log) {
	if (logging()) {
	    asynced.w(header, log);
	}
    }

    /**
     * Creates a new asynchronous log.
     *
     * @param filePath
     */
    public void newLog(String filePath) {
	asynced.openDebug(debugPath + filePath, 50);
    }
}
