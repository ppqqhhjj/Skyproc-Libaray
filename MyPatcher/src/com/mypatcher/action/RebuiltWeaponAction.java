package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

import com.mypatcher.Context;
import com.mypatcher.utility.Log;

import skyproc.GRUP;
import skyproc.MajorRecord.MajorFlags;
import skyproc.WEAP;

public class RebuiltWeaponAction extends Action {

	private List<String> originalMods = new ArrayList<>();
	
	public RebuiltWeaponAction(Context context) {
		super(context);
		getOriginalMod();
	}

	@Override
	public void doAction() {
//		this.removeFatasticWeapon();
	}

	private void removeFatasticWeapon() {
		GRUP<WEAP> weapons = this.merger.getWeapons();

		for (WEAP weapon : weapons) {
			
			if(!this.originalMods.contains(weapon.getFormStr().substring(6))){
				continue;
			}
			
			String name = weapon.getName();
			if (name != null) {
				if (name.contains(" Battleaxe of ") || name.contains(" Bow of ") || name.contains(" Dagger of ")
						|| name.contains(" Greatsword of ") || name.contains(" Mace of ") || name.contains(" Sword of")
						|| name.contains(" War Axe of ") || name.contains(" Warhammer of ")
						|| name.contains(" Armor of ") || name.contains(" Boots of ") || name.contains(" Gauntlets of ")
						|| name.contains(" Helmet of ") || name.contains(" Shield of ")) {

					if (weapon.getEnchantment() != null && weapon.getEDID().startsWith("Ench")) {
						weapon.set(MajorFlags.Deleted, true);
//						Log.console("delete: "+weapon.getEDID()+" : "+weapon.getName());
						this.patch.addRecord(weapon);
					}

				}
			}
		}
	}
	
	private void getOriginalMod() {
		this.originalMods.add("Skyrim.esm");
		this.originalMods.add("Update.esm");
		this.originalMods.add("Dawnguard.esm");
		this.originalMods.add("HearthFires.esm");
		this.originalMods.add("Dragonborn.esm");

	}

}
