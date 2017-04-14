package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

import skyproc.ARMO;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.KYWD;
import skyproc.KeywordSet;
import skyproc.LVLI;
import skyproc.MajorRecord;
import skyproc.genenums.ArmorType;

import com.mypatcher.Context;
import com.mypatcher.utility.Log;

public class DistributeNewItemsAction extends Action {

	private List<String> originalMods = new ArrayList<>();
	private List<String> ignoreItem = new ArrayList<>();

	private List<LVLI> helmetHeavy = new ArrayList<>();
	private List<LVLI> cuirassHeavy = new ArrayList<>();
	private List<LVLI> gauntletHeavy = new ArrayList<>();
	private List<LVLI> bootHeavy = new ArrayList<>();
	private List<LVLI> shieldHeavy = new ArrayList<>();

	private List<LVLI> helmetLight = new ArrayList<>();
	private List<LVLI> cuirassLight = new ArrayList<>();
	private List<LVLI> gauntletLight = new ArrayList<>();
	private List<LVLI> bootLight = new ArrayList<>();
	private List<LVLI> shieldLight = new ArrayList<>();

	public DistributeNewItemsAction(Context context) {
		super(context);
		getOriginalMod();
		getIgnoreItem();
		InitLevledList();

	}

	@Override
	public void doAction() {
		this.distributeArmor();
		this.distributeWeapon();

	}

	private void distributeArmor() {
		for (ARMO armor : this.merger.getArmors()) {
			if (this.isIgnore(armor)) {
				continue;
			}

			this.addArmorToList(armor);

		}
	}

	private void distributeWeapon() {

	}

	private void getOriginalMod() {
		this.originalMods.add("Skyrim.esm");
		this.originalMods.add("Update.esm");
		this.originalMods.add("Dawnguard.esm");
		this.originalMods.add("HearthFires.esm");
		this.originalMods.add("Dragonborn.esm");

	}

	private void getIgnoreItem() {
		this.ignoreItem.add("");
	}

	// do not distribute original items.
	private boolean isIgnore(MajorRecord record) {
		if (this.originalMods.contains(record.getFormStr().substring(6))
				|| this.ignoreItem.contains(record.getEDID())) {
			return true;
		}

		return false;
	}

	private void addArmorToList(ARMO armor) {
		KYWD helmet = (KYWD) this.merger
				.getMajor("ArmorHelmet", GRUP_TYPE.KYWD);
		KYWD cuirass = (KYWD) this.merger.getMajor("ArmorCuirass",
				GRUP_TYPE.KYWD);
		KYWD gauntlet = (KYWD) this.merger.getMajor("ArmorGauntlets",
				GRUP_TYPE.KYWD);
		KYWD boot = (KYWD) this.merger.getMajor("ArmorBoots", GRUP_TYPE.KYWD);

		KYWD shield = (KYWD) this.merger
				.getMajor("ArmorShield", GRUP_TYPE.KYWD);

		ArrayList<FormID> keys = armor.getKeywordSet().getKeywordRefs();

		if (armor.getBodyTemplate().getArmorType() == ArmorType.HEAVY) {
			if (keys.contains(helmet.getForm())) {
				this.addToList(armor, this.helmetHeavy);
			} else if (keys.contains(cuirass.getForm())) {
				this.addToList(armor, this.cuirassHeavy);
			} else if (keys.contains(gauntlet.getForm())) {
				this.addToList(armor, this.gauntletHeavy);
			} else if (keys.contains(boot.getForm())) {
				this.addToList(armor, this.bootHeavy);
			} else if (keys.contains(shield.getForm())) {
				this.addToList(armor, this.shieldHeavy);
			} else {
				Log.console("Item: " + armor.getEDID()
						+ " has no valid keyword");
			}

		} else {
			if (keys.contains(helmet.getForm())) {
				this.addToList(armor, this.helmetLight);
			} else if (keys.contains(cuirass.getForm())) {
				this.addToList(armor, this.cuirassLight);
			} else if (keys.contains(gauntlet.getForm())) {
				this.addToList(armor, this.gauntletLight);
			} else if (keys.contains(boot.getForm())) {
				this.addToList(armor, this.bootLight);
			} else if (keys.contains(shield.getForm())) {
				this.addToList(armor, this.shieldLight);
			} else {
				Log.console("Item: " + armor.getEDID()
						+ " has no valid keyword");
			}
		}
	}

	private void InitLevledList() {

		FormID helmetLight = this.merger.getMajor("ArmorLeatherHelmet",
				GRUP_TYPE.ARMO).getForm();
		FormID cuirassLight = this.merger.getMajor("ArmorLeatherCuirass",
				GRUP_TYPE.ARMO).getForm();
		FormID gauntletLight = this.merger.getMajor("ArmorLeatherGauntlets",
				GRUP_TYPE.ARMO).getForm();
		FormID bootLight = this.merger.getMajor("ArmorLeatherBoots",
				GRUP_TYPE.ARMO).getForm();
		FormID shieldLight = this.merger.getMajor("ArmorHideShield",
				GRUP_TYPE.ARMO).getForm();

		FormID helmetHeavy = this.merger.getMajor("ArmorIronHelmet",
				GRUP_TYPE.ARMO).getForm();
		FormID cuirassHeavy = this.merger.getMajor("ArmorIronCuirass",
				GRUP_TYPE.ARMO).getForm();
		FormID gauntletHeavy = this.merger.getMajor("ArmorIronGauntlets",
				GRUP_TYPE.ARMO).getForm();
		FormID bootHeavy = this.merger.getMajor("ArmorIronBoots",
				GRUP_TYPE.ARMO).getForm();
		FormID shieldHeavy = this.merger.getMajor("ArmorIronShield",
				GRUP_TYPE.ARMO).getForm();

		for (LVLI leveledItems : this.merger.getLeveledItems()) {

			// Only add items to original leveled list, not mod.
			if (!this.originalMods.contains(leveledItems.getFormStr()
					.substring(6)) || !leveledItems.getEDID().contains("LItem")) {
				continue;
			}
			if (leveledItems.getEntryForms().contains(helmetLight)) {
				this.helmetLight.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(cuirassLight)) {
				this.cuirassLight.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(gauntletLight)) {
				this.gauntletLight.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(bootLight)) {
				this.bootLight.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(shieldLight)) {
				this.shieldLight.add(leveledItems);
			}

			if (leveledItems.getEntryForms().contains(helmetHeavy)) {
				this.helmetHeavy.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(cuirassHeavy)) {
				this.cuirassHeavy.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(gauntletHeavy)) {
				this.gauntletHeavy.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(bootHeavy)) {
				this.bootHeavy.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(shieldHeavy)) {
				this.shieldHeavy.add(leveledItems);
			}

		}
	}

	private void addToList(ARMO armor, List<LVLI> list) {
		for (LVLI leveledItem : list) {
			if (!leveledItem.contains(armor)) {
				leveledItem.addEntry(armor.getForm(), 1, 1);
				this.patch.addRecord(leveledItem);
			}
		}
	}

}
