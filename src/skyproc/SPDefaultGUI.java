package skyproc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import lev.gui.LImagePane;
import lev.gui.LLabel;
import lev.gui.LProgressBar;
import lev.gui.LTextPane;
import lev.gui.resources.LFonts;
import lev.gui.resources.LImages;

/**
 *
 * @author Justin Swanson
 */
public class SPDefaultGUI extends JFrame {

    LImagePane backgroundPanel;
    LLabel pluginLabel;
    LLabel patching;
    LTextPane description;
    LImagePane skyprocLogo;
    LProgressBar pbar;

    /**
     * Creates and displays the SkyProc default GUI.
     *
     * @param yourPatcherName This will be used as the title on the GUI.
     * @param yourDescription This will be displayed under the title.
     */
    public SPDefaultGUI(final String yourPatcherName, final String yourDescription) {
	super(yourPatcherName);
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		init(yourPatcherName, yourDescription);
	    }
	});

    }

    final void init(String pluginName, String descriptionText) {
	try {
	    // Set up frame
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setSize(600, 400);
	    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
	    setResizable(false);
	    setLayout(null);

	    // Background Panel
	    backgroundPanel = new LImagePane(LImages.multipurpose());
	    super.add(backgroundPanel);
	    skyprocLogo = new LImagePane(SPDefaultGUI.class.getResource("SkyProc Logo Small.png"));
	    skyprocLogo.setLocation(5, this.getHeight() - skyprocLogo.getHeight() - 30);
	    backgroundPanel.add(skyprocLogo, 0);


	    // Label
	    pluginLabel = new LLabel("[ " + pluginName + " ]", LFonts.OptimusPrinceps(30), new Color(61, 143, 184));
	    pluginLabel.addShadow();
	    pluginLabel.centerIn(this, 20);
	    backgroundPanel.add(pluginLabel, 0);


	    //Description
	    description = new LTextPane(new Dimension(this.getWidth() - 100, 195), new Color(200, 200, 200));
	    description.centerIn(this, pluginLabel.getY() + pluginLabel.getHeight() + 20);
	    description.setEditable(false);
	    description.setText(descriptionText);
	    description.setFontSize(14);
	    description.centerText();
	    backgroundPanel.add(description, 0);


	    //Creating Patch
	    patching = new LLabel("Creating patch.", LFonts.Typo3(15), new Color(210, 210, 210));
	    patching.addShadow();
	    patching.centerIn(this, description.getY() + description.getHeight() + 10);
	    backgroundPanel.add(patching, 0);
	    
	    //ProgressBar
	    pbar = new LProgressBar(250, 15, LFonts.Typo3(8), new Color(210, 210, 210));
	    pbar.centerIn(this, patching.getY() + patching.getHeight() + 5);
	    pbar.setFooterOffset(6);
	    backgroundPanel.add(pbar,0);

	    setVisible(true);
	} catch (IOException ex) {
	}
    }

    void finishRun() {
	patching.setText("Patch is complete!");
	patching.centerIn(this, patching.getY());
	patching.setFontColor(Color.orange);
    }

    /**
     *
     * @param c GUI component to add to the default GUI
     * @return The component added.
     */
    @Override
    public Component add(final Component c) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		SwingUtilities.invokeLater(new Runnable() {

		    @Override
		    public void run() {
			backgroundPanel.add(c, 1);
		    }
		});
	    }
	});
	return c;
    }

    /**
     * Sets the title text color in the default GUI.
     *
     * @param c Color to set the title to.
     */
    public void setHeaderColor(final Color c) {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		pluginLabel.setFontColor(c);
	    }
	});
    }

    /**
     * Tells the default GUI to switch the text and tell the user the patch is
     * complete.
     */
    public void finished() {
	SwingUtilities.invokeLater(new Runnable() {

	    @Override
	    public void run() {
		finishRun();
	    }
	});
    }
}
