package com.mypatcher.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lev.LImport;
import skyproc.ARMA;
import skyproc.ARMO;
import skyproc.BodyTemplate;
import skyproc.BodyTemplate.BodyTemplateType;
import skyproc.BodyTemplate.GeneralFlags;
import skyproc.FormID;
import skyproc.GRUP_TYPE;
import skyproc.KeywordSet;
import skyproc.Mod;
import skyproc.genenums.FirstPersonFlags;
import skyproc.genenums.Gender;
import skyproc.genenums.Perspective;

import com.mypatcher.Context;
import com.mypatcher.data.ReplaceItem;
import com.mypatcher.type.GenderType;
import com.mypatcher.utility.GeneralUtility;
import com.mypatcher.utility.Log;

public class ReplaceArmorModelAction extends Action {

	public ReplaceArmorModelAction(Context c) {
		super(c);
	}

	@Override
	public void doAction() {
		this.replaceModel();
		// List<ReplaceItem> replaceList = this.readReplaceFile();
		// ARMO targetArmor = (ARMO)
		// this.merger.getMajor(replaceList.get(0).getTarget(),
		// GRUP_TYPE.ARMO);
		// this.patch.addRecord(targetArmor);

	}

	private void replaceModel() {
		// gendertype:targetmodname(xx.esp/esm):targetEditID|sourceModName(xx.esp/esm):sourceEditID1(body),sourceEditID2(non-body),...
		// Female:skyrim.esm:ArmorSteelCuirassA|Hothtrooper44_ArmorCompilation_orig.esp:IAImperialKnightCuirass,IAImperialKnightCape,IAImperialKnightGreaves
		List<ReplaceItem> replaceList = new ArrayList<>();

		replaceList = this.readReplaceFile();

		for (ReplaceItem item : replaceList) {
			Log.console("replacing: " + item.getTarget());
			ARMO targetArmor = (ARMO) this.merger.getMajor(item.getTarget(),
					GRUP_TYPE.ARMO);

			GeneralUtility.checkNull(targetArmor, "cannot find target armor:"
					+ item.getTarget());

			List<ARMO> resourceArmors = new ArrayList<>();
			for (String resID : item.getResources()) {
				ARMO resArmor = (ARMO) this.merger.getMajor(resID,
						GRUP_TYPE.ARMO);
				GeneralUtility.checkNull(resArmor, "cannot find source armor:"
						+ resID);
				resourceArmors.add(resArmor);
			}

			List<FormID> targetAddonList = targetArmor.getArmatures();
			if (targetAddonList.size() > 1) {
				throw new RuntimeException(
						"target armor has more than 1 addons, can't process");
			}

			ARMA targetAddon = (ARMA) this.merger.getMajor(
					targetAddonList.get(0), GRUP_TYPE.ARMA);

			for (int i = 0; i < resourceArmors.size(); i++) {

				Log.console("replacing " + targetArmor + " with "
						+ resourceArmors.get(i));

				ARMO resourceArmor = resourceArmors.get(i);
				ARMA resourceArmorAddon = (ARMA) this.merger.getMajor(
						resourceArmor.getArmatures().get(0), GRUP_TYPE.ARMA);

				// replace body part
				if (i == 0) {
					Log.console("replacing body");
					if (item.getGenderType() == GenderType.Female
							|| item.getGenderType() == GenderType.Both) {
						this.setModel(targetAddon, resourceArmorAddon,
								Gender.FEMALE);
					} else if (item.getGenderType() == GenderType.Male
							|| item.getGenderType() == GenderType.Both) {

						this.setModel(targetAddon, resourceArmorAddon,
								Gender.MALE);
						this.setWorldModel(targetArmor, resourceArmor);

					} else {
						throw new RuntimeException("unsupported GenderType: "
								+ item.getGenderType());
					}

				} else {// add non-body parts

					targetArmor.getArmatures()
							.add(resourceArmorAddon.getForm());
					Log.console("add non-body addon: "
							+ resourceArmorAddon.getForm());
					
					BodyTemplate targeTemplate = targetArmor.getBodyTemplate();

					for (FirstPersonFlags f : resourceArmorAddon.getBodyTemplate()
							.getFirstPersonFlags()) {

						targeTemplate.set(f, true);
						Log.console("set new flag:" + f);
					}

				}
			}

			this.patch.addRecord(targetArmor);
			this.patch.addRecord(targetAddon);

		}
	}

	private void setModel(ARMA target, ARMA resource, Gender gender) {
		String firstFemaleModel = resource.getModelPath(gender,
				Perspective.FIRST_PERSON);
		String thirdFemailModel = resource.getModelPath(gender,
				Perspective.THIRD_PERSON);
		target.setModelPath(firstFemaleModel, gender, Perspective.FIRST_PERSON);
		target.setModelPath(thirdFemailModel, gender, Perspective.THIRD_PERSON);
	}

	private void setWorldModel(ARMO target, ARMO resource) {
		target.setModel(resource.getModel(Gender.MALE), Gender.MALE);
		target.setModel(resource.getModel(Gender.FEMALE), Gender.FEMALE);
	}

	private List<ReplaceItem> readReplaceFile() {
		File replaceFile = new File("replace.txt");
		List<ReplaceItem> replaceList = new ArrayList<>();

		try {
			BufferedReader fileReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(replaceFile)));
			String line = "";
			while ((line = fileReader.readLine()) != null && !line.equals("")) {
				Log.console("read line: " + line);
				String[] splits = line.split("\\|");
				// for(String s:splits){
				// print(s);
				// }
				Log.console(splits[0].split(":")[0].trim());
				GenderType genderType = GenderType
						.valueOf(splits[0].split(":")[0].trim());
				String target = splits[0].split(":")[2].trim();
				String resource = splits[1].split(":")[1];
				ReplaceItem replaceItem = new ReplaceItem(target, resource,
						genderType);
				replaceList.add(replaceItem);

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return replaceList;
	}
	

	// private List<FirstPersonFlags> getAddonFlag(ARMA addon) {
	// List<FirstPersonFlags> flagList = new ArrayList<>();
	// BodyTemplate body = addon.getBodyTemplate();
	// for (FirstPersonFlags f : FirstPersonFlags.values()) {
	// if (!(f == FirstPersonFlags.NONE)) {
	// if (body.get(BodyTemplateType.valueOf("Normal"), f)) {
	// flagList.add(f);
	// } else if (body.get(BodyTemplateType.valueOf("Biped"), f)) {
	// flagList.add(f);
	// }
	// }
	// }
	// return flagList;
	// }
}
