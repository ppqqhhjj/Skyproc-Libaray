package skyproc.gui;

import java.util.ArrayList;
import lev.gui.LProgressBarInterface;

/**
 * A boundary class that will eventually offer an interface to the the LevGUI
 * library.
 *
 * @author Justin Swanson
 */
public class SPProgressBarPlug {

    static final ArrayList<LProgressBarInterface> bars = new ArrayList<>();

    static {
	bars.add(new Placeholder());
    }

    public static void addProgressBar(LProgressBarInterface progressBar) {
	bars.add(progressBar);
	progressBar.setBar(getBar());
	progressBar.setMax(getMax());
	progressBar.pause(paused());
    }

    static class Placeholder implements LProgressBarInterface {

	int max = 0;
	int cur = 0;
	boolean paused = false;

	@Override
	public void setMax(int in) {
	    if (!paused) {
		max = in;
	    }
	}

	@Override
	public void setMax(int in, String status) {
	    setMax(in);
	}

	@Override
	public void setStatus(String status) {
	}

	@Override
	public void setStatus(int cur, int max, String status) {
	    setMax(max);
	    setBar(cur);
	}

	@Override
	public void incrementBar() {
	    if (!paused) {
		cur++;
	    }
	}

	@Override
	public void reset() {
	    if (!paused) {
		cur = 0;
	    }
	}

	@Override
	public void setBar(int in) {
	    if (!paused) {
		cur = in;
	    }
	}

	@Override
	public int getBar() {
	    return cur;
	}

	@Override
	public int getMax() {
	    return max;
	}

	@Override
	public void pause(boolean on) {
	    paused = on;
	}

	@Override
	public boolean paused() {
	    return paused;
	}

	@Override
	public void done() {
	    setBar(getMax());
	}
    }

    public static void setMax(int in) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setMax(in);
	    }
	}
    }

    public static void setMax(int in, String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setMax(in, status);
	    }
	}
    }

    public static void setStatus(String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setStatus(status);
	    }
	}
    }

    public static void setStatus(int cur, int max, String status) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setStatus(cur, max, status);
	    }
	}
    }

    public static void incrementBar() {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.incrementBar();
	    }
	}
    }

    public static void reset() {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.reset();
	    }
	}
    }

    public static void setBar(int in) {
	if (!paused()) {
	    for (LProgressBarInterface p : bars) {
		p.setBar(in);
	    }
	}
    }

    public static int getBar() {
	return bars.get(0).getBar();
    }

    public static int getMax() {
	return bars.get(0).getMax();
    }

    public static void pause(boolean on) {
	for (LProgressBarInterface p : bars) {
	    p.pause(on);
	}
    }

    public static boolean paused() {
	return bars.get(0).paused();
    }

    public static void done() {
	for (LProgressBarInterface p : bars) {
	    p.done();
	}
    }
}
