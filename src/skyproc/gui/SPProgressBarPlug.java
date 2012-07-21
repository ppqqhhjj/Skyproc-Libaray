package skyproc.gui;

import java.util.ArrayList;
import lev.gui.LProgressBarInterface;

/**
 * A boundary class that will eventually offer an interface to the the LevGUI library.
 * @author Justin Swanson
 */
public class SPProgressBarPlug {

    static ArrayList<LProgressBarInterface> bars = new ArrayList<>();

    public static void addProgressBar (LProgressBarInterface progressBar) {
	bars.add(progressBar);
    }

    public static void setMax(int in) {
	for (LProgressBarInterface p : bars) {
	    p.setMax(in);
	}
    }

    public static void setMax(int in, String status) {
	for (LProgressBarInterface p : bars) {
	    p.setMax(in, status);
	}
    }

    public static void setStatus(String status) {
	for (LProgressBarInterface p : bars) {
	    p.setStatus(status);
	}
    }

    public static void setStatus(int cur, int max, String status) {
	for (LProgressBarInterface p : bars) {
	    p.setStatus(cur, max, status);
	}
    }

    public static void incrementBar() {
	for (LProgressBarInterface p : bars) {
	    p.incrementBar();
	}
    }

    public static void reset() {
	for (LProgressBarInterface p : bars) {
	    p.reset();
	}
    }

    public static void setBar(int in) {
	for (LProgressBarInterface p : bars) {
	    p.setBar(in);
	}
    }

    public static int getBar() {
	if (bars.isEmpty()) {
	    return 0;
	} else {
	    return bars.get(0).getBar();
	}
    }

    public static int getMax() {
	if (bars.isEmpty()) {
	    return 0;
	} else {
	    return bars.get(0).getMax();
	}
    }

    public static void pause(boolean on) {
	for (LProgressBarInterface p : bars) {
	    p.pause(on);
	}
    }

    public static boolean paused() {
	if (bars.isEmpty()) {
	    return true;
	} else {
	    return bars.get(0).paused();
	}
    }

    public static void done() {
	for (LProgressBarInterface p : bars) {
	    p.done();
	}
    }

}
