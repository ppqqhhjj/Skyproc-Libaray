package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

import com.mypatcher.Context;

import skyproc.FormID;
import skyproc.GRUP;
import skyproc.GRUP_TYPE;
import skyproc.LVLN;
import skyproc.LeveledEntry;
import skyproc.NPC_;

public class RebuiltGenderAction extends Action{
	
	List<String> needDeleteNpcs = new ArrayList<>();

	public RebuiltGenderAction(Context context) {
		super(context);
	}

	@Override
	public void doAction() {
		
	}
	
	private void rebuildGender(){
		GRUP<LVLN> leveledNPCs = this.merger.getLeveledCreatures();

		for (LVLN lvln : leveledNPCs.getRecords()) {
			
			boolean needChange = false;
			
			List<FormID> needtoDeleteEntry= new ArrayList<>();
			
			for (LeveledEntry entry : lvln.getEntries()) {
				
				FormID formID = entry.getForm();
				NPC_ npc = (NPC_)this.merger.getMajor(formID, GRUP_TYPE.NPC_);
				String editID = npc.getEDID();
				
				if (editID.startsWith("EncBandit")) {
					
					if(editID.contains("Magic")&&editID.endsWith("M")){
						needtoDeleteEntry.add(formID);
						needChange = true;
					}
					
					if(editID.contains("Magic")&&editID.endsWith("M")){
						needtoDeleteEntry.add(formID);
						needChange = true;
					}
					
				}
			}

			if (needChange) {
				
				for(FormID formID:needtoDeleteEntry){
					lvln.removeAllEntries(formID);
				}
				
				this.patch.addRecord(lvln);
			}
		} 
	}
	
	private void initDeleteList(){
		this.needDeleteNpcs.add("");
	}

}
