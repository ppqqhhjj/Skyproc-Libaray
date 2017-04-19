package com.mypatcher.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import skyproc.FormID;
import skyproc.GRUP;
import skyproc.GRUP_TYPE;
import skyproc.KYWD;
import skyproc.KeywordSet;
import skyproc.MajorRecord;
import skyproc.NPC_;
import skyproc.NPC_.NPCFlag;
import skyproc.NPC_.NPCStat;
import skyproc.NPC_.TemplateFlag;
import skyproc.RACE;
import skyproc.genenums.ActorValue;
import skyproc.genenums.Skill;

import com.mypatcher.Context;
import com.mypatcher.utility.Log;

public class RebuiltNPCAction extends Action {

	public RebuiltNPCAction(Context context) {
		super(context);
	}

	@Override
	public void doAction() {
		this.rebuiltNPC();
		this.rebuiltRace();

	}

	private void rebuiltRace() {

		GRUP<RACE> races = this.merger.getRaces();

		for (RACE race : races) {
			boolean hasHeavyArmor = false;
			boolean hasLightArmor = false;

			List<Integer> uselessSkill = new ArrayList<>();

			for (int i = 0; i < 7; i++) {
				ActorValue skill = race.getSkillBoostSkill(i);
				Integer value = Integer.valueOf(race.getSkillBoostValue(i));

				if (skill == ActorValue.HeavyArmor) {
					value = 100;
					hasHeavyArmor = true;
					race.setSkillBoost(i, skill, value);
				}

				if (skill == ActorValue.LightArmor) {
					value = 100;
					hasLightArmor = true;
					race.setSkillBoost(i, skill, value);
				}

				if (skill == ActorValue.NONE) {
					uselessSkill.add(0, Integer.valueOf(i));
				}

				if (skill == ActorValue.Speechcraft || skill == ActorValue.Alchemy || skill == ActorValue.Smithing
						|| skill == ActorValue.Pickpocket || skill == ActorValue.Lockpicking
						|| skill == ActorValue.Enchanting) {
					uselessSkill.add(Integer.valueOf(i));
				}

			}

			if (!hasHeavyArmor && !hasLightArmor) {
				race.setSkillBoost(uselessSkill.get(0), ActorValue.HeavyArmor, 100);
				race.setSkillBoost(uselessSkill.get(1), ActorValue.LightArmor, 100);
			}

			if (!hasHeavyArmor && hasLightArmor) {
				race.setSkillBoost(uselessSkill.get(0), ActorValue.HeavyArmor, 100);
			}

			if (hasHeavyArmor && !hasLightArmor) {
				race.setSkillBoost(uselessSkill.get(0), ActorValue.LightArmor, 100);
			}

			this.patch.addRecord(race);
		}
	}

	private void rebuiltNPC() {

		FormID BoneBreaker90 = this.merger.getPerks().get("BoneBreaker90").getForm();
		FormID HackAndSlash90 = this.merger.getPerks().get("HackAndSlash90").getForm();
		
		FormID Skullcrusher90 = this.merger.getPerks().get("Skullcrusher90").getForm();
		FormID Limbsplitter90 = this.merger.getPerks().get("Limbsplitter90").getForm();
		
		FormID DeflectArrows = this.merger.getPerks().get("DeflectArrows").getForm();
		FormID PowerBashPerk = this.merger.getPerks().get("PowerBashPerk").getForm();
		FormID ElementalProtection = this.merger.getPerks().get("ElementalProtection").getForm();
		
		FormID Conditioning = this.merger.getPerks().get("Conditioning").getForm();
		

		GRUP<NPC_> npcs = this.merger.getNPCs();
		for (NPC_ npc : npcs.getRecords()) {
			
			if(npc.getEDID().equals("Player")){
				continue;
			}

			// Log.console(npc.getEDID());

			// Log.console(npc.getEDID() + " : " + npc.getName());

			npc.addPerk(BoneBreaker90, 1);
			npc.addPerk(HackAndSlash90, 1);
			
			npc.addPerk(Skullcrusher90, 1);
			npc.addPerk(Limbsplitter90, 1);
			
			npc.addPerk(DeflectArrows, 1);
//			npc.addPerk(PowerBashPerk, 1);
			npc.addPerk(ElementalProtection, 1);
			
			npc.addPerk(Conditioning, 1);
			
			
			npc.set(Skill.HEAVYARMOR, 100);
			npc.set(Skill.LIGHTARMOR, 100);

			if (this.isHumanBeing(npc)) {
				// npc.setHealthOffset(0);
			}

			if (npc.getName() != null && !npc.getName().trim().equals("<NO TEXT>")) {
				String name = npc.getName();
//				if (name.contains("Bear") || name.contains("Troll") || name.contains("Giant")
//						|| name.contains("Sabre Cat") || name.contains("Mammoth")) {
//					npc.setHealthOffset(50 * npc.get(NPCStat.LEVEL));
//				}

				if (name.contains("Dragon")) {
					npc.setHealthOffset(200 * npc.get(NPCStat.LEVEL));
				}
			}
			this.patch.addRecord(npc);
		}
	}

	private boolean isHumanBeing(NPC_ npc) {
		KYWD actorTypeNPC = (KYWD) this.merger.getMajor("ActorTypeNPC", GRUP_TYPE.KYWD);
		KYWD actorTypeUndead = (KYWD) this.merger.getMajor("ActorTypeUndead", GRUP_TYPE.KYWD);

		RACE race = (RACE) this.merger.getMajor(npc.getRace(), GRUP_TYPE.RACE);

		ArrayList<FormID> keys = race.getKeywordSet().getKeywordRefs();

		if (keys.contains(actorTypeNPC.getForm()) && !keys.contains(actorTypeUndead.getForm())) {
			return true;
		}

		return false;
	}

	private void setMinLevel(NPC_ npc) {
		int level = npc.get(NPCStat.LEVEL);
		if (level == 0) {
			level = npc.get(NPCStat.MIN_CALC_LEVEL);
		}

		int newLevel = (int) (30 + level * 0.71);
		if (newLevel > 50) {
			level = 50;
		}

		npc.set(NPCStat.LEVEL, newLevel);
		npc.set(NPCStat.MIN_CALC_LEVEL, newLevel);

	}

}
