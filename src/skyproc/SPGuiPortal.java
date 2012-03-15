package skyproc;

import lev.gui.LProgressBarInterface;

/**
 * A boundary class that will eventually offer an interface to the the LevGUI library.
 * @author Justin Swanson
 */
public class SPGuiPortal {

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
    }
    
}
