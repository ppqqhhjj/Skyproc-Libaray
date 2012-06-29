/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import lev.debug.LDebug;
import lev.gui.*;
import skyproc.SPGlobal;
import skyproc.SPImporter;

/**
 * The standard GUI setup used in SUM. This can be used to create GUIs that
 * manage your settings, handle savefiles, and hook directly into SUM.
 *
 * @author Justin Swanson
 */
public class SUMGUI extends JFrame {

    static JFrame singleton = null;
    /**
     * Bounds of the SUM GUI.
     */
    public final static Rectangle fullDimensions = new Rectangle(0, 0, 950, 632);
    /**
     * Bounds of the left column
     */
    public final static Rectangle leftDimensions = new Rectangle(0, 0, 299, fullDimensions.height - 28); // For status update
    /**
     * Bounds of the middle column
     */
    public final static Rectangle middleDimensions = new Rectangle(leftDimensions.x + leftDimensions.width + 7, 0, 330, fullDimensions.height);
    /**
     * Bounds of the right column
     */
    public final static Rectangle rightDimensions = new Rectangle(middleDimensions.x + middleDimensions.width + 7, 0, 305, fullDimensions.height);
    /**
     * Bounds of the two right columns
     */
    public final static Rectangle middleRightDimensions = new Rectangle(middleDimensions.x, 0, rightDimensions.x + rightDimensions.width, fullDimensions.height);
    /**
     * Bounds of the two left columns
     */
    public final static Rectangle middleLeftDimensions = new Rectangle(0, 0, middleDimensions.x + middleDimensions.width, middleDimensions.height);
    static final Color light = new Color(238, 233, 204);
    static final Color lightGray = new Color(190, 190, 190);
    static final Color darkGray = new Color(110, 110, 110);
    static final Color lightred = Color.red;
    static SUM hook;
    static final String header = "SUM";
    /**
     * Import/Export background thread is stored here for access.
     */
    static public Thread parser;
    static boolean imported = false;
    static boolean exitRequested = false;
    /**
     * Progress bar frame that pops up at the end when creating the patch.
     */
    static public LProgressBarFrame progress = new LProgressBarFrame(
            new Font("SansSerif", Font.PLAIN, 12), Color.GRAY,
            new Font("SansSerif", Font.PLAIN, 10), Color.lightGray);
    /**
     * Help panel on the right column of the GUI.
     */
    static public LHelpPanel helpPanel = new LHelpPanel(rightDimensions, new Font("Serif", Font.BOLD, 18), light, lightGray, true, 10);
    // Non static
    static LImagePane backgroundPanel;
    LLabel willMakePatch;
    LImagePane skyProcLogo;
    JTextArea statusUpdate;
    LLabel versionNum;
    LButton patchStartButton;

    SUMGUI() {
        super(hook.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        Dimension GUISIZE = new Dimension(954, 658);
        setSize(GUISIZE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - GUISIZE.width / 2, dim.height / 2 - GUISIZE.height / 2);
        setLayout(null);
        addComponents();
        addWindowListener(new WindowListener() {

            @Override
            public void windowClosed(WindowEvent arg0) {
            }

            @Override
            public void windowActivated(WindowEvent arg0) {
            }

            @Override
            public void windowClosing(WindowEvent arg0) {
                exitProgram();
            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
            }

            @Override
            public void windowIconified(WindowEvent arg0) {
            }

            @Override
            public void windowOpened(WindowEvent arg0) {
            }
        });
        helpPanel.setHeaderColor(hook.getHeaderColor());
    }

    final void addComponents() {
        try {

            backgroundPanel = new LImagePane(SUMGUI.class.getResource("background.jpg"));
            super.add(backgroundPanel);

            patchStartButton = new LButton("Start Patch!", 90, 30);
            patchStartButton.getSource().setFont(new Font("Serif", Font.BOLD, 14));
            patchStartButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        closingGUIwindow();
                    } catch (Exception ex) {
                    }
                }
            });

            //willMakePatch = new LLabel("A patch will be generated upon exit.", new Font("SansSerif", Font.PLAIN, 10), Color.GRAY);
            patchStartButton.setLocation(backgroundPanel.getWidth() - patchStartButton.getWidth() - 18, 580);
            backgroundPanel.add(patchStartButton);

            progress.addWindowListener(new WindowListener() {

                @Override
                public void windowClosed(WindowEvent arg0) {
                }

                @Override
                public void windowActivated(WindowEvent arg0) {
                }

                @Override
                public void windowClosing(WindowEvent arg0) {
                    if (progress.closeOp == JFrame.DISPOSE_ON_CLOSE) {
                        SPGlobal.log(header, "Progress bar window closing");
                        exitProgram();
                    }
                }

                @Override
                public void windowDeactivated(WindowEvent arg0) {
                }

                @Override
                public void windowDeiconified(WindowEvent arg0) {
                }

                @Override
                public void windowIconified(WindowEvent arg0) {
                }

                @Override
                public void windowOpened(WindowEvent arg0) {
                }
            });
            progress.setGUIref(singleton);

            statusUpdate = new JTextArea();
            statusUpdate.setSize(250, 18);
            statusUpdate.setLocation(5, getFrameHeight() - statusUpdate.getHeight());
            statusUpdate.setForeground(Color.LIGHT_GRAY);
            statusUpdate.setOpaque(false);
            statusUpdate.setText("Started application");
            statusUpdate.setEditable(false);
            statusUpdate.setVisible(true);
            backgroundPanel.add(statusUpdate);

            skyProcLogo = new LImagePane(SPDefaultGUI.class.getResource("SkyProc Logo Small.png"));
            skyProcLogo.setLocation(5, statusUpdate.getY() - skyProcLogo.getHeight() - 5);
            backgroundPanel.add(skyProcLogo);

            helpPanel.setBounds(rightDimensions);
            backgroundPanel.add(helpPanel);

            SPProgressBarPlug.progress = new SUMProgress();

            setVisible(true);

        } catch (IOException ex) {
            SPGlobal.logException(ex);
        }

    }

    /**
     * Opens and hooks onto a program that implements the SUM interface.
     *
     * @param hook Program to open and hook to
     */
    public static void open(final SUM hook) {
        SUMGUI.hook = hook;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (singleton == null) {
                    if (hook.hasCustomMenu()) {
                        singleton = hook.openCustomMenu();
                    } else {
                        singleton = new SUMGUI();
                    }
                    progress.setGUIref(singleton);
                    progress.moveToCorrectLocation();

                    if (hook.hasSave()) {
                        hook.getSave().init();
                    }

                    if (hook.importAtStart()) {
                        runThread();
                    }

                    if (hook.hasStandardMenu()) {
                        singleton.add(hook.getStandardMenu());
                    }
                }
            }
        });
    }

    int getFrameHeight() {
        return this.getHeight() - 28;
    }

    static void closingGUIwindow() {
        SPGlobal.log(header, "Window Closing.");

        progress.setExitOnClose();
        progress.open(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                exitProgram();
            }
        });
        exitRequested = true;
        runThread();
    }

    /**
     * Immediately saves settings to file, closes debug logs, and exits the
     * program.<br> NO patch is generated.
     */
    static public void exitProgram() {
        SPGlobal.log(header, "Exit requested.");
        if (hook.hasSave()) {
            hook.getSave().saveToFile();
        }
        LDebug.wrapUpAndExit();
    }

    static class ProcessingThread implements Runnable {

        @Override
        public void run() {
            SPGlobal.log("START IMPORT THREAD", "Starting of process thread.");
            try {
                if (!imported) {
                    imported = true;
                    SPGlobal.setGlobalPatch(hook.getExportPatch());
                    SPImporter importer = new SPImporter();
                    importer.importActiveMods(hook.importRequests());
                    SPProgressBarPlug.progress.setStatus("Done importing.");
                }
                if (exitRequested) {
                    hook.runChangesToPatch();

                    try {
                        // Export your custom patch.
                        SPGlobal.getGlobalPatch().export();
                    } catch (Exception ex) {
                        // If something goes wrong, show an error message.
                        SPGlobal.logException(ex);
                        JOptionPane.showMessageDialog(null, "There was an error exporting the custom patch.\n(" + ex.getMessage() + ")\n\nPlease contact Leviathan1753.");
                    }

                    SPProgressBarPlug.progress.done();
                    exitProgram();
                }
            } catch (Exception e) {
                System.err.println(e.toString());
                SPGlobal.logException(e);
                JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e + "'  Check the debug logs.");

                // if exception occurs
                exitProgram();
            }
        }

        public void main(String args[]) {
            (new Thread(new ProcessingThread())).start();
        }
    }

    static void runThread() {
        if (parser == null || !parser.isAlive()) {
            parser = new Thread(new ProcessingThread());
            parser.start();
        }
    }

    @Override
    public Component add(Component comp) {
        return backgroundPanel.add(comp);
    }

    /**
     * Interface that hooks SkyProc's progress bar output to the SUM GUI's
     * progress bar display.
     */
    public class SUMProgress implements LProgressBarInterface {

        @Override
        public void setMax(int in) {
            progress.setMax(in);
        }

        @Override
        public void setMax(int in, String status) {
            progress.setMax(in, status);
            if (!progress.paused()) {
                statusUpdate.setText(status);
            }
        }

        @Override
        public void setStatus(String status) {
            progress.setStatus(status);
            if (!progress.paused()) {
                statusUpdate.setText(status);
            }
        }

        /**
         *
         * @param min
         * @param max
         * @param status
         */
        @Override
        public void setStatus(int min, int max, String status) {
            progress.setStatus(min, max, status);
            if (!progress.paused()) {
                statusUpdate.setText(status);
            }
        }

        @Override
        public void incrementBar() {
            progress.incrementBar();
        }

        @Override
        public void reset() {
            progress.incrementBar();
        }

        @Override
        public void setBar(int in) {
            progress.setBar(in);
        }

        /**
         *
         * @return
         */
        @Override
        public int getBar() {
            return progress.getBar();
        }

        /**
         *
         * @return
         */
        @Override
        public int getMax() {
            return progress.getMax();
        }

        /**
         *
         * @param on
         */
        @Override
        public void pause(boolean on) {
            progress.pause(on);
        }

        /**
         *
         * @return
         */
        @Override
        public boolean paused() {
            return progress.paused();
        }

        /**
         *
         */
        @Override
        public void done() {
            progress.done();
        }
    }
}
