package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

import skyproc.ARMO;
import skyproc.FormID;
import skyproc.GRUP;
import skyproc.GRUP_TYPE;
import skyproc.KYWD;
import skyproc.KeywordSet;
import skyproc.LVLI;
import skyproc.LeveledEntry;
import skyproc.MajorRecord;
import skyproc.Record;
import skyproc.WEAP;

import com.mypatcher.Context;
import com.mypatcher.utility.Log;

public class RemoveFantasticItemsAction extends Action {

	public RemoveFantasticItemsAction(Context context) {
		super(context);
	}

	@Override
	public void doAction() {
		this.remove();

	}

	public void remove() {

		List<FormID> needRemoveItems = this.getNeedRemoveItems();

		GRUP<LVLI> leveledItems = this.merger.getLeveledItems();
		for (LVLI lItems : leveledItems) {

			for (FormID item : needRemoveItems) {
				if (lItems.getEntryForms().contains(item)) {
					Log.console("processing Leveled item: " + lItems.getEDID());
					lItems.removeAllEntries(item);
					this.patch.addRecord(lItems);
					Log.console("remove item: " + item);
				}
			}
		}
	}

	private List<FormID> getNeedRemoveMeterial() {
		List<FormID> meterialformIDs = new ArrayList<>();

		List<String> meterialList = new ArrayList<>();
		meterialList.add("ArmorMaterialGlass");
		meterialList.add("ArmorMaterialDwarven");
		meterialList.add("ArmorMaterialEbony");
		meterialList.add("ArmorMaterialOrcish");

		for (String meterial : meterialList) {
			MajorRecord record = this.merger.getMajor(meterial, GRUP_TYPE.KYWD);
			meterialformIDs.add(record.getForm());
		}

		return meterialformIDs;
	}

	private List<FormID> getNeedRemoveItems() {
		List<FormID> needRemoveItems = new ArrayList<>();
		List<FormID> meterialformIDs = this.getNeedRemoveMeterial();

		GRUP<ARMO> armors = this.merger.getArmors();
		for (ARMO armor : armors.getRecords()) {
			for (FormID key : armor.getKeywordSet().getKeywordRefs()) {
				if (meterialformIDs.contains(key)) {
					needRemoveItems.add(armor.getForm());
					Log.console("need remove item: " + armor.getForm() + ": "
							+ armor.getName());
				}
			}
		}

		GRUP<WEAP> weapons = this.merger.getWeapons();
		for (WEAP weapon : weapons.getRecords()) {
			for (FormID key : weapon.getKeywordSet().getKeywordRefs()) {
				if (meterialformIDs.contains(key)) {
					needRemoveItems.add(weapon.getForm());
					Log.console("need remove item: " + weapon.getForm() + ": "
							+ weapon.getName());
				}
			}
		}

		return needRemoveItems;
	}

}
