package skyproc;

import lev.gui.LProgressBarInterface;

/**
 * A boundary class that will eventually offer an interface to the the LevGUI library.
 * @author Justin Swanson
 */
public class SPGUI {

    /**
     * SkyProc's import and export functions hook up their progress output to this
     * variable.  If you want to display import/export progress bar data on your
     * GUI, then assign this variable to the progress bar you are displaying.
     */
    public static LProgressBarInterface progress = new ProgressBarPlaceholder();

    static class ProgressBarPlaceholder implements LProgressBarInterface {

        @Override
        public void setMax(int i) {
        }

        @Override
        public void incrementBar() {
        }

        @Override
        public void reset() {
        }

        @Override
        public void setBar(int i) {
        }

        @Override
        public void setMax(int i, String string) {
        }

        @Override
        public void setStatus(String string) {
        }

	@Override
	public int getBar() {
	    return 0;
	}

	@Override
	public int getMax() {
	    return 0;
	}

	@Override
	public void setStatus(int min, int max, String status) {
	}

	@Override
	public void pause(boolean on) {
	}

	@Override
	public boolean paused() {
	    return true;
	}

	@Override
	public void done() {
	}
    }

}
