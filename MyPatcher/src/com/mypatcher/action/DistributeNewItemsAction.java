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
import skyproc.WEAP;
import skyproc.genenums.ArmorType;

import com.mypatcher.Context;
import com.mypatcher.utility.Log;

public class DistributeNewItemsAction extends Action {

	private List<String> originalMods = new ArrayList<>();
	private List<String> ignoreItem = new ArrayList<>();
	private List<String> allowedMods = new ArrayList<>();

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

	private List<LVLI> Warhammer = new ArrayList<>();
	private List<LVLI> WarAxe = new ArrayList<>();
	private List<LVLI> Sword = new ArrayList<>();
	private List<LVLI> Mace = new ArrayList<>();
	private List<LVLI> Greatsword = new ArrayList<>();
	private List<LVLI> Dagger = new ArrayList<>();
	private List<LVLI> Battleaxe = new ArrayList<>();

	public DistributeNewItemsAction(Context context) {
		super(context);
		InitModList();
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

			if (this.isAllowed(armor)) {
				this.addArmorToList(armor);
			}

		}
	}

	private void distributeWeapon() {

		for (WEAP weapon : this.merger.getWeapons()) {
			if (this.isIgnore(weapon)) {
				continue;
			}

			if (this.isAllowed(weapon)) {
				this.addWeaponToList(weapon);
			}

		}

	}

	private void addWeaponToList(WEAP weapon) {
		KYWD WeapTypeWarhammer = (KYWD) this.merger.getMajor("WeapTypeWarhammer", GRUP_TYPE.KYWD);
		KYWD WeapTypeWarAxe = (KYWD) this.merger.getMajor("WeapTypeWarAxe", GRUP_TYPE.KYWD);
		KYWD WeapTypeSword = (KYWD) this.merger.getMajor("WeapTypeSword", GRUP_TYPE.KYWD);
		KYWD WeapTypeMace = (KYWD) this.merger.getMajor("WeapTypeMace", GRUP_TYPE.KYWD);

		KYWD WeapTypeGreatsword = (KYWD) this.merger.getMajor("WeapTypeGreatsword", GRUP_TYPE.KYWD);

		KYWD WeapTypeDagger = (KYWD) this.merger.getMajor("WeapTypeDagger", GRUP_TYPE.KYWD);

		KYWD WeapTypeBattleaxe = (KYWD) this.merger.getMajor("WeapTypeBattleaxe", GRUP_TYPE.KYWD);

		ArrayList<FormID> keys = weapon.getKeywordSet().getKeywordRefs();

		if (keys.contains(WeapTypeBattleaxe)) {
			this.addToList(weapon, this.Battleaxe);
		} else if (keys.contains(WeapTypeDagger)) {
			this.addToList(weapon, this.Dagger);
		} else if (keys.contains(WeapTypeGreatsword)) {
			this.addToList(weapon, this.Greatsword);
		} else if (keys.contains(WeapTypeMace)) {
			this.addToList(weapon, this.Mace);
		} else if (keys.contains(WeapTypeSword)) {
			this.addToList(weapon, this.Sword);
		} else if (keys.contains(WeapTypeWarAxe)) {
			this.addToList(weapon, this.WarAxe);
		} else if (keys.contains(WeapTypeWarhammer)) {
			this.addToList(weapon, this.Warhammer);
		} else {
			Log.console("Item: " + weapon.getEDID() + " has no valid keyword");
		}

	}

	private void InitModList() {
		this.originalMods.add("Skyrim.esm");
		this.originalMods.add("Update.esm");
		this.originalMods.add("Dawnguard.esm");
		this.originalMods.add("HearthFires.esm");
		this.originalMods.add("Dragonborn.esm");

		this.allowedMods.add("");

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

	// do not distribute original items.
	private boolean isAllowed(MajorRecord record) {
		if (this.allowedMods.contains(record.getFormStr().substring(6))) {
			return true;
		}

		return false;
	}

	private void addArmorToList(ARMO armor) {
		KYWD helmet = (KYWD) this.merger.getMajor("ArmorHelmet", GRUP_TYPE.KYWD);
		KYWD cuirass = (KYWD) this.merger.getMajor("ArmorCuirass", GRUP_TYPE.KYWD);
		KYWD gauntlet = (KYWD) this.merger.getMajor("ArmorGauntlets", GRUP_TYPE.KYWD);
		KYWD boot = (KYWD) this.merger.getMajor("ArmorBoots", GRUP_TYPE.KYWD);

		KYWD shield = (KYWD) this.merger.getMajor("ArmorShield", GRUP_TYPE.KYWD);

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
				Log.console("Item: " + armor.getEDID() + " has no valid keyword");
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
				Log.console("Item: " + armor.getEDID() + " has no valid keyword");
			}
		}
	}

	private void InitLevledList() {

		FormID helmetLight = this.merger.getMajor("ArmorHideHelmet", GRUP_TYPE.ARMO).getForm();
		FormID cuirassLight = this.merger.getMajor("ArmorHideCuirass", GRUP_TYPE.ARMO).getForm();
		FormID gauntletLight = this.merger.getMajor("ArmorHideGauntlets", GRUP_TYPE.ARMO).getForm();
		FormID bootLight = this.merger.getMajor("ArmorHideBoots", GRUP_TYPE.ARMO).getForm();
		FormID shieldLight = this.merger.getMajor("ArmorHideShield", GRUP_TYPE.ARMO).getForm();

		FormID helmetHeavy = this.merger.getMajor("ArmorIronHelmet", GRUP_TYPE.ARMO).getForm();
		FormID cuirassHeavy = this.merger.getMajor("ArmorIronCuirass", GRUP_TYPE.ARMO).getForm();
		FormID gauntletHeavy = this.merger.getMajor("ArmorIronGauntlets", GRUP_TYPE.ARMO).getForm();
		FormID bootHeavy = this.merger.getMajor("ArmorIronBoots", GRUP_TYPE.ARMO).getForm();
		FormID shieldHeavy = this.merger.getMajor("ArmorIronShield", GRUP_TYPE.ARMO).getForm();

		FormID SteelWarhammer = this.merger.getMajor("SteelWarhammer", GRUP_TYPE.WEAP).getForm();
		FormID SteelWarAxe = this.merger.getMajor("SteelWarAxe", GRUP_TYPE.WEAP).getForm();
		FormID SteelSword = this.merger.getMajor("SteelSword", GRUP_TYPE.WEAP).getForm();
		FormID SteelMace = this.merger.getMajor("SteelMace", GRUP_TYPE.WEAP).getForm();
		FormID SteelGreatsword = this.merger.getMajor("SteelGreatsword", GRUP_TYPE.WEAP).getForm();
		FormID SteelDagger = this.merger.getMajor("SteelDagger", GRUP_TYPE.WEAP).getForm();
		FormID SteelBattleaxe = this.merger.getMajor("SteelBattleaxe", GRUP_TYPE.WEAP).getForm();

		for (LVLI leveledItems : this.merger.getLeveledItems()) {

			// Only add items to original leveled list, not mod.
			if (!this.originalMods.contains(leveledItems.getFormStr().substring(6))
					|| !leveledItems.getEDID().contains("LItem")) {
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

			if (leveledItems.getEntryForms().contains(SteelWarhammer)) {
				this.Warhammer.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(SteelWarAxe)) {
				this.WarAxe.add(leveledItems);
			}

			if (leveledItems.getEntryForms().contains(SteelSword)) {
				this.Sword.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(SteelMace)) {
				this.Mace.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(SteelGreatsword)) {
				this.Greatsword.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(SteelDagger)) {
				this.Dagger.add(leveledItems);
			}
			if (leveledItems.getEntryForms().contains(SteelBattleaxe)) {
				this.Battleaxe.add(leveledItems);
			}

		}
	}

	private void addToList(MajorRecord record, List<LVLI> list) {
		for (LVLI leveledItem : list) {
			if (!leveledItem.contains(record)) {
				leveledItem.addEntry(record.getForm(), 1, 1);
				this.patch.addRecord(leveledItem);
			}
		}
	}

}
