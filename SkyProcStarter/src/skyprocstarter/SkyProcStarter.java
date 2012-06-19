package skyprocstarter;

import java.io.IOException;
import javax.swing.JOptionPane;
import skyproc.*;
import skyproc.gui.SPDefaultGUI;

/**
 *
 * @author Your Name Here
 */
public class SkyProcStarter {

    public static void main(String[] args) {

        try {
            /*
             * Custom names and descriptions
             */
            // Used to export a patch such as "My Patch.esp"
            String myPatchName = "My Patch";
            // Used in the GUI as the title
            String myPatcherName = "My Patcher";
            // Used in the GUI as the description of what your patcher does
            String myPatcherDescription =
                    "I did not change the description,\n"
                    + "but I'm patching your setup to do something fantastic!";
	    // The types of records you want your patcher to import
	    // At the moment, it imports NPC_ and LVLN records.  Change this to
	    // customize the import to what you need.
	    GRUP_TYPE[] typesToImport = {
		GRUP_TYPE.NPC_,
		GRUP_TYPE.LVLN
	    };



            /*
             * Initializing Debug Log and Globals
             */
            // Go up two folders:  SkyprocPatchers/YourFolderName/
            SPGlobal.pathToData = "../../";
            SPGlobal.createGlobalLog();
            // Turn Debugging off except for errors
            SPGlobal.logging(false);



            /*
             * Creating SkyProc Default GUI
             */
            // Create the SkyProc Default GUI
            SPDefaultGUI gui = new SPDefaultGUI(myPatcherName, myPatcherDescription);




            /*
             * Create your patch to export.  (false means it is NOT an .esm file)
             */
            Mod patch = new Mod(myPatchName, false);
            SPGlobal.setGlobalPatch(patch);




            /*
             * Importing all Active Plugins
             */
            try {
                SPImporter importer = new SPImporter();
                // Import all Mods and requested GRUPs and merges them as overrides into your patch.
                // NOTE:  all the separate mods can still be accessed via the global database.
                patch.addAsOverrides(importer.importActiveMods(typesToImport));
            } catch (IOException ex) {
                // If things go wrong, create an error box.
                JOptionPane.showMessageDialog(null, "There was an error importing plugins.\n(" + ex.getMessage() + ")\n\nPlease contact the author.");
                System.exit(0);
            }




            /*
             * =======================================
             *      Your custom code begins here.
             * =======================================
             */







            /*
             * Close up shop.
             */
            try {
                // Export your custom patch.
                patch.export();
            } catch (IOException ex) {
                // If something goes wrong, show an error message.
                JOptionPane.showMessageDialog(null, "There was an error exporting the custom patch.\n(" + ex.getMessage() + ")\n\nPlease contact the author.");
                System.exit(0);
            }
            // Tell the GUI to display "Done Patching"
            gui.finished();

        } catch (Exception e) {
            // If a major error happens, print it everywhere and display a message box.
            System.out.println(e.toString());
            SPGlobal.logException(e);
            JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution.  Check the debug logs.");
        }

        // Close debug logs before program exits.
        SPGlobal.closeDebug();
    }
}
