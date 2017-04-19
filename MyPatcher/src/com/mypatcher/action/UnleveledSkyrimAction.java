package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

import skyproc.GRUP;
import skyproc.LVLI;
import skyproc.LVLN;
import skyproc.LeveledEntry;

import com.mypatcher.Context;

public class UnleveledSkyrimAction extends Action {

	public UnleveledSkyrimAction(Context context) {
		super(context);

	}

	@Override
	public void doAction() {
		this.unLeveledNPC();
		this.unLeveledItem();

	}

	private void unLeveledItem() {
		GRUP<LVLI> leveledItems = this.merger.getLeveledItems();
		for (LVLI lvli : leveledItems.getRecords()) {
			boolean needChange = false;
			for (LeveledEntry entry : lvli.getEntries()) {
				if (entry.getLevel() > 1) {
					entry.setLevel(1);
					needChange = true;
				}
			}

			if (needChange) {
				this.patch.addRecord(lvli);
			}
		}
	}

	private void unLeveledNPC() {
		GRUP<LVLN> leveledNPCs = this.merger.getLeveledCreatures();
		for (LVLN lvln : leveledNPCs.getRecords()) {
			boolean needChange = false;
			for (LeveledEntry entry : lvln.getEntries()) {
				if (entry.getLevel() > 1) {
					entry.setLevel(1);
					needChange = true;
				}
			}

			if (needChange) {
				this.patch.addRecord(lvln);
			}
		}
	}

}
