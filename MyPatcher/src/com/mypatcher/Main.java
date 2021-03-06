package com.mypatcher;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mypatcher.action.RebuiltNPCAction;
import com.mypatcher.action.RebuiltWeaponAction;
import com.mypatcher.action.RebuitArmorAction;
import com.mypatcher.action.UnleveledSkyrimAction;
import com.mypatcher.utility.Log;

import lev.gui.LSaveFile;
import skyproc.ARMO;
import skyproc.GRUP;
import skyproc.GRUP_TYPE;
import skyproc.Mod;
import skyproc.ModListing;
import skyproc.SPGlobal;
import skyproc.SPImporter;
import skyproc.gui.SPMainMenuPanel;
import skyproc.gui.SUM;
import skyproc.gui.SUMGUI;

public class Main implements SUM {

	GRUP_TYPE[] importRequests = new GRUP_TYPE[] { GRUP_TYPE.LVLI, GRUP_TYPE.ARMO, GRUP_TYPE.ARMA, GRUP_TYPE.WEAP,
			GRUP_TYPE.FLST, GRUP_TYPE.KYWD, GRUP_TYPE.OTFT, GRUP_TYPE.ENCH, GRUP_TYPE.LVLN, GRUP_TYPE.NPC_,
			GRUP_TYPE.RACE, GRUP_TYPE.PERK };

	private static String myPatchName = "Woody";
	private static String version = "0.1";
	private static String welcomeText = "My Pather";
	private Mod merger;
	private Mod patch;

	public static void main(String[] args) {

		File esp = new File("../../" + myPatchName + ".esp");
		try {
			if (esp.exists()) {
				Files.delete(esp.toPath());
			}
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		try {
			SPGlobal.createGlobalLog();
			SUMGUI.open(new Main(), args);
		} catch (Exception e) {
			SPGlobal.logException(e);
			JOptionPane.showMessageDialog(null, "There was an exception thrown during program execution: '" + e
					+ "'  Check the debug logs or contact the author.");
			SPGlobal.closeDebug();
		}
	}

	@Override
	public String getName() {
		return myPatchName;
	}

	@Override
	public GRUP_TYPE[] dangerousRecordReport() {
		return new GRUP_TYPE[] { GRUP_TYPE.LVLI };
	}

	@Override
	public GRUP_TYPE[] importRequests() {
		return importRequests;
	}

	@Override
	public boolean importAtStart() {
		return true;
	}

	@Override
	public boolean hasStandardMenu() {
		return false;
	}

	@Override
	public SPMainMenuPanel getStandardMenu() {
		final SPMainMenuPanel settingsMenu = new SPMainMenuPanel();
		return settingsMenu;
	}

	@Override
	public boolean hasCustomMenu() {
		return false;
	}

	@Override
	public JFrame openCustomMenu() {
		return null;
	}

	@Override
	public boolean hasLogo() {
		return false;
	}

	@Override
	public String description() {
		return null;
	}

	@Override
	public URL getLogo() {
		return null;
	}

	@Override
	public boolean hasSave() {
		return false;
	}

	@Override
	public LSaveFile getSave() {
		return null;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public ModListing getListing() {
		return new ModListing(this.getName(), false);
	}

	@Override
	public Mod getExportPatch() {
		return new Mod(this.getListing());
	}

	@Override
	public Color getHeaderColor() {
		return null;
	}

	@Override
	public boolean needsPatching() {
		return false;
	}

	@Override
	public ArrayList<ModListing> requiredMods() {
		return new ArrayList<>();
	}

	@Override
	public void onStart() throws Exception {

	}

	@Override
	public void onExit(boolean patchWasGenerated) throws Exception {

	}

	@Override
	public void runChangesToPatch() throws Exception {

		try {
			this.patch = SPGlobal.getGlobalPatch();
			ArrayList<ModListing> activeMods = SPImporter.getActiveModList();
			Log.console(activeMods.toString());

			merger = new Mod("merger", false);
			merger.addAsOverrides(SPGlobal.getDB());

			Context c = new Context();
			c.setMerger(this.merger);
			c.setPatch(this.patch);

			GRUP<ARMO> armors = merger.getArmors();

			// new ReplaceArmorModelAction(c).doAction();
			// new ReplaceWeaponModelAction(c).doAction();
			// new RemoveFantasticItemsAction(c).doAction();
//			new DistributeNewItemsAction(c).doAction();
			new UnleveledSkyrimAction(c).doAction();
			new RebuiltNPCAction(c).doAction();
			new RebuitArmorAction(c).doAction();
			new RebuiltWeaponAction(c).doAction();

			// for (ARMO armor : armors) {
			// print(armor.getEDID());
			// ArrayList<FormID> ids = armor.getArmatures();
			// for (FormID id : ids) {
			// MajorRecord re = merger.getMajor(id, GRUP_TYPE.ARMA);
			// ARMA aa = (ARMA) re;
			// }
			//
			// BodyTemplate body = armor.getBodyTemplate();
			// for (FirstPersonFlags f : FirstPersonFlags.values()) {
			//
			// if (!f.equals(FirstPersonFlags.NONE)) {
			// if (body.get(BodyTemplateType.valueOf("Normal"), f)) {
			// print(f.name());
			// }
			// }
			// }
			//
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
