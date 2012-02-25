/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.debug;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lev.Ln;

/**
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
    private static boolean mainLogSwitch = true;
    private Map<Enum, LDebug> special = new HashMap<Enum, LDebug>();
    private int specificCounter = 0;

    public LLogger(String debugPath) {
        this.debugPath = debugPath;
        Ln.removeDirectory(new File(debugPath));
        main = new LDebug(debugPath + "=--Debug Overview--=.txt", 50);
        asynced = new LDebug(debugPath + "Asynchronous log.txt", 50);
        synced = new LDebug();
    }

    public void addSpecial(Enum key, String fileName) {
        special.put(key, new LDebug(debugPath + fileName, 50));
    }

    public void logSync(String header, String... log) {
        if (logging()) {
            if (syncing) {
                synced.w(header, log);
            } else {
                asynced.w(header, log);
            }
        }
    }

    public void newSyncLog(String fileName) {
        if (logging()) {
            synced.openDebug(debugPath + fileName, 50);
        }
    }

    public void sync(boolean on) {
        syncing = on;
    }

    public boolean sync() {
        return syncing;
    }

    public void logMain(String header, String... log) {
        main.w(header, log);
        main.flushDebug();
    }

    public void logError(String header, String... log) {
        logMain(header, log);
        logSync(header, log);
        flush();
    }

    public boolean logging() {
        return logging && mainLogSwitch;
    }

    public void logging(Boolean on) {
        logging = on;
    }

    public void logException(String dump) {
        main.writeException(dump);
    }

    public void setAllLogging(boolean in) {
        mainLogSwitch = in;
    }

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

    public void logSpecial(Enum e, String header, String... log) {
        if (logging()) {
            if (special.containsKey(e)) {
                special.get(e).w(header, log);
            }
        }
    }

    public void log(String header, String... log) {
        if (logging()) {
            asynced.w(header, log);
        }
    }

    public void newLog(String fileName) {
        asynced.openDebug(debugPath + fileName, 50);
    }
}
