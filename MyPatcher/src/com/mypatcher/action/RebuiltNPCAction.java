package com.mypatcher.action;

import java.util.ArrayList;
import java.util.List;

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

	}

	private void rebuiltNPC() {
		
		FormID HeavyArmor1=this.merger.getPerks().get("Juggernaut00").getForm();
		FormID lightArmor1 = this.merger.getPerks().get("AgileDefender00").getForm();
		
		GRUP<NPC_> npcs = this.merger.getNPCs();
		for (NPC_ npc : npcs.getRecords()) {

			// Log.console(npc.getEDID());
			if (npc.getName() != null
					&& !npc.getName().trim().equals("<NO TEXT>")) {

				Log.console(npc.getEDID() + " : " + npc.getName());

				npc.set(Skill.LIGHTARMOR, 100);
				npc.set(Skill.HEAVYARMOR, 100);
				
				npc.addPerk(HeavyArmor1, 1);
				npc.addPerk(lightArmor1, 1);

				if (this.isHumanBeing(npc)) {
//					npc.setHealthOffset(0);
				}

				String name = npc.getName();
				if (name.contains("Bear") || name.contains("Troll")
						|| name.contains("Giant") || name.contains("Sabre Cat")
						|| name.contains("Mammoth")) {
					npc.setHealthOffset(50 * npc.get(NPCStat.LEVEL));
				}

				if (name.contains("Dragon")) {
					npc.setHealthOffset(500 * npc.get(NPCStat.LEVEL));
				}

				this.patch.addRecord(npc);
			}
		}
	}

	private boolean isHumanBeing(NPC_ npc) {
		KYWD actorTypeNPC = (KYWD) this.merger.getMajor("ActorTypeNPC",
				GRUP_TYPE.KYWD);
		KYWD actorTypeUndead = (KYWD) this.merger.getMajor("ActorTypeUndead",
				GRUP_TYPE.KYWD);

		RACE race = (RACE) this.merger.getMajor(npc.getRace(), GRUP_TYPE.RACE);

		ArrayList<FormID> keys = race.getKeywordSet().getKeywordRefs();

		if (keys.contains(actorTypeNPC.getForm())
				&& !keys.contains(actorTypeUndead.getForm())) {
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
