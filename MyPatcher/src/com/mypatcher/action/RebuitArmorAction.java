package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

import com.mypatcher.Context;
import com.mypatcher.utility.Log;

import skyproc.ARMO;
import skyproc.GRUP;
import skyproc.GRUP_TYPE;
import skyproc.KYWD;
import skyproc.KeywordSet;
import skyproc.WEAP;
import skyproc.MajorRecord.MajorFlags;
import skyproc.genenums.ArmorType;

public class RebuitArmorAction extends Action {

	private List<String> originalMods = new ArrayList<>();

	public RebuitArmorAction(Context context) {
		super(context);
		getOriginalMod();
	}

	@Override
	public void doAction() {
		this.setArmorRate();
//		this.removeFatasticArmor();

	}

	private void setArmorRate() {
		GRUP<ARMO> armors = this.merger.getArmors();

		KYWD cuirass = (KYWD) this.merger.getMajor("ArmorCuirass", GRUP_TYPE.KYWD);
		KYWD helmet = (KYWD) this.merger.getMajor("ArmorHelmet", GRUP_TYPE.KYWD);

		for (ARMO armor : armors) {
			int armorRating = armor.getArmorRating();
			KeywordSet keys = armor.getKeywordSet();
			if (armorRating > 0) {
				if (keys.getKeywordRefs().contains(cuirass.getForm())) {
					if (armor.getBodyTemplate().getArmorType() == ArmorType.HEAVY) {
						armor.setArmorRating(armorRating * 4);
					} else {
						armor.setArmorRating(armorRating * 2);
					}
				}

				// if (keys.getKeywordRefs().contains(helmet.getForm())) {
				// armor.setArmorRating(armorRating * 2);
				// }
				this.patch.addRecord(armor);
			}
		}
	}

	private void removeFatasticArmor() {
		GRUP<ARMO> armors = this.merger.getArmors();

		for (ARMO armor : armors) {
			if (!this.originalMods.contains(armor.getFormStr().substring(6))) {
				continue;
			}
			String name = armor.getName();
			if (name != null) {
				if (name.contains(" Armor of ") || name.contains(" Boots of ") || name.contains(" Gauntlets of ")
						|| name.contains(" Helmet of ") || name.contains(" Shield of ")
						|| name.startsWith("Necklace of ") || name.startsWith("Ring of ")
						|| name.contains(" Bracers of ")) {

					if (armor.getEnchantment() != null && armor.getEDID().startsWith("Ench")) {
						armor.set(MajorFlags.Deleted, true);
//						Log.console("delete: "+armor.getEDID()+" : "+armor.getName());
						this.patch.addRecord(armor);
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
