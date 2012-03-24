package lev.debug;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
public class LDebug {

    /**
     * Turns on debug info regarding how much time has passed since the last
     * debug output.
     */
    public static boolean timeStamp = false;
    /**
     * Turns on debug info regarding how much time has passed since the start
     * of the program.
     */
    public static boolean timeElapsed = false;
    private static Boolean on = true;
    private static ArrayList<String> bannedHeaders = new ArrayList<String>();
    private FileWriter writer;
    private int debugCounter = 0;
    private int spacing = 0;
    private long startTime;
    private long lastStamp = System.nanoTime();
    private static ArrayList<LDebug> debugs = new ArrayList<LDebug>();

    /**
     * Creates a debug log at the path desired, and with the given character
     * space for the header text.
     * @param path
     * @param space
     */
    public LDebug(String path, int space) {
        openDebug(path, space);
        init();
    }

    /**
     *
     */
    public LDebug () {
        init();
    }

    private void init() {
        debugs.add(this);
    }

    /**
     *
     * @param b
     */
    public static void on(Boolean b) {
        on = b;
    }

    /**
     *
     * @return
     */
    public static boolean on () {
        return on;
    }

    /**
     *
     * @param path
     * @param space
     */
    public final void openDebug(String path, int space) {
        try {
            closeDebugFile();
            startTime = System.currentTimeMillis();
            debugCounter = 1;
            spacing = space;
            int index = path.lastIndexOf('\\');
            int index2 = path.lastIndexOf('/');
            if (index2 > index) {
                index = index2;
            }

            File f = new File(path.substring(0, index + 1));
            if (f.exists() == false) {
                f.mkdirs();
            }
            writer = new FileWriter(path);
            w("OPEN DEBUG FILE", "Opening Debug File");

        } catch (Exception e) {
            System.err.println("Caught Exception: "
                    + e.getMessage());
        }
    }

    /**
     * Writes to the debug log.
     * @param header
     * @param input
     */
    public void w(final String header, final String... input) {

        if (on && writer != null) {
            if (bannedHeaders.contains(input[0])) {
                return;
            }
            try {
                long timestamp = System.nanoTime();
                String times = "";
                if (timeElapsed) {
                    times = Ln.nanoTimeString(System.currentTimeMillis() - startTime);
                }
                if (timeElapsed && timeStamp) {
                    times = times + "][";
                }
                if (timeStamp) {
                    times = times + (timestamp - lastStamp) / 1000;
                }
                lastStamp = timestamp;
                writer.write(spaceLeft(true, spacing, ' ',
                        debugCounter++,
                        times, "[" + header + "]  "));
                for (String x : input) {
                    writer.write(x);
                }
                writer.write(13);
                writer.write(10);
            } catch (IOException e) {
            } catch (Exception e) {

                System.err.println("Caught Exception: "
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param in
     */
    public void writeException(String in) {
        if (in.length() > 0) {
            w("EXCEPTION", in);
        }
    }

    /**
     *
     * @param in
     * @throws IOException
     */
    public void writeException(char in) throws IOException {
        writer.write(in);
    }

    /**
     *
     */
    public void flushDebug() {
        try {
            if (on && writer != null) {
                writer.flush();
            }
        } catch (java.io.IOException e) {
        }
    }

    /**
     *
     */
    public void closeDebugFile() {
        if (writer != null) {
            w("DEBUG", "Closing Debug File.  Time was: " + (System.currentTimeMillis() - startTime));
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     *
     */
    public void clearBannedHeaders() {
        bannedHeaders.clear();
    }

    /**
     *
     * @param in
     */
    public void addBannedHeader(String in) {
        bannedHeaders.add(in);
    }

    /**
     *
     * @param input
     */
    public void addBannedHeader(String[] input) {
        bannedHeaders.addAll(Arrays.asList(input));
    }

    private static String spaceLeft(Boolean concat, int spaces, char c, int counter, String time, String... input) {
	String counterStr = "[" + Integer.toString(counter) + "]";
        if (!time.equals("")) {
            counterStr = counterStr + "[" + time + "]";
        }
	return counterStr + Ln.spaceLeft(concat, spaces - counterStr.length(), c, input);
    }

    /**
     * Closes debug logs and flushes their buffers.
     * @throws IOException
     */
    public static void wrapUp() throws IOException {
        for (LDebug d : debugs) {
            d.closeDebugFile();
        }
    }

    public static void wrapUpAndExit() {
	try {
	    wrapUp();
	} catch (IOException ex) {
	}
	System.exit(0);
    }
}
