package com.mypatcher.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import skyproc.GRUP_TYPE;
import skyproc.WEAP;

import com.mypatcher.Context;
import com.mypatcher.data.ReplaceItem;
import com.mypatcher.type.GenderType;
import com.mypatcher.utility.GeneralUtility;
import com.mypatcher.utility.Log;

public class ReplaceWeaponModelAction extends Action {

	public ReplaceWeaponModelAction(Context context) {
		super(context);
	}

	@Override
	public void doAction() {
		this.replaceModel();

	}

	private void replaceModel() {
		List<ReplaceItem> replaceItems = this.readReplaceFile();

		for (ReplaceItem item : replaceItems) {
			Log.console("replacing: " + item.getTarget());
			
			WEAP target = (WEAP) this.merger.getMajor(item.getTarget(),
					GRUP_TYPE.WEAP);
			GeneralUtility.checkNull(target, "Cannot find target weapon: "
					+ item.getTarget());
			

			String resourceEditID = item.getResources().get(0);
			WEAP resource = (WEAP) this.merger.getMajor(resourceEditID,
					GRUP_TYPE.WEAP);
			GeneralUtility.checkNull(resource, "Cannot find resource weapon: "
					+ resourceEditID);

			target.getModelData().setFileName(
					resource.getModelData().getFileName());
			target.setFirstPersonModel(resource.getFirstPersonModel());
			
			this.patch.addRecord(target);

		}
	}

	private List<ReplaceItem> readReplaceFile() {
		File replaceFile = new File("replaceweapon.txt");
		List<ReplaceItem> replaceList = new ArrayList<>();

		try {
			BufferedReader fileReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(replaceFile)));
			String line = "";
			while ((line = fileReader.readLine()) != null && !line.equals("")) {
				Log.console("read line: " +line);
				String[] splits = line.split("\\|");
				String target = splits[0].split(":")[1].trim();
				String resource = splits[1].split(":")[1];
				ReplaceItem replaceItem = new ReplaceItem(target, resource,
						GenderType.Both);
				replaceList.add(replaceItem);

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return replaceList;
	}

}
