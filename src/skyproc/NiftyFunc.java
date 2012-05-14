/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 * A class to hold many common/useful functions.
 * @author Justin Swanson
 */
public class NiftyFunc {

    /**
     * A common way to attach scripts to NPCs that normally cannot have scripts attached<br>
     * (Any NPC that is referenced by a LVLN)<br>
     * is to give a racial spell to them that has a magic effect that has the desired script.<br><br>
     * This function streamlines the process and gives you a SPEL/MGEF setup that will attach the desired script.<br>
     * Simply give this SPEL to the NPC's race.<br><br>
     * NOTE:  Attaching a script attachment spell to an NPCs race will affect ALL NPCs with that same race.<br>
     * If you do not want this, then consider using genSafeScriptAttachingRace().
     * @param originateFrom Mod to make the new records originate from.
     * @param script Script to have the SPEL attach
     * @param uniqueID A unique string to differentiate the records from any other SkyProc user's setups.<br>
     * (using your mod's name is usually sufficient)
     * @return The generated SPEL record that can be attached to any RACE to have it attach the desired script.
     */
    public static SPEL genScriptAttachingSpel(Mod originateFrom, ScriptRef script, String uniqueID) {
	String name = "SP_" + uniqueID + "_" + script.name.data + "_attacher";
	MGEF mgef = new MGEF(originateFrom, name + "_MGEF", name + "_MGEF");
	mgef.scripts.addScript(script);
	mgef.set(MGEF.SpellEffectFlag.HideInUI, true);
	SPEL spel = new SPEL(originateFrom, name + "_SPEL");
	spel.setSpellType(SPEL.SPELType.Ability);
	spel.addMagicEffect(mgef);
	return spel;
    }

    /**
     * A common way to attach scripts to NPCs that normally cannot have scripts attached<br>
     * (Any NPC that is referenced by a LVLN)<br>
     * is to give a racial spell to them that has a magic effect that has the desired script.<br><br>
     * This function streamlines the process and gives you a duplicate race which will attach the desired script.<br>
     * You can then set it to be the target NPCs race.  Since it is a duplicate, it will only affect NPCs you explicitly attach it to, and
     * not ALL NPCs that shared the original race.  <br>
     * It is a full duplicate and will retain any settings of the original race.
     * @param originateFrom Mod to make the new records originate from.
     * @param script Script to have the SPEL attach
     * @param uniqueID A unique string to differentiate the records from any other SkyProc user's setups.<br>
     * (using your mod's name is usually sufficient)
     * @param raceToDup Original race that you wish to duplicate.
     * @return A duplicate of the input race, with the only difference being it has a script attachment racial spell.
     */
    public static RACE genSafeScriptAttachingRace(Mod originateFrom, ScriptRef script, RACE raceToDup, String uniqueID) {
	SPEL attachmentSpel = genScriptAttachingSpel(originateFrom, script, uniqueID);
	RACE attachmentRace = (RACE) originateFrom.makeCopy(raceToDup);
	attachmentRace.addSpell(attachmentSpel.getForm());
	return attachmentRace;
    }

    /**
     * Checks the given template flag "chains" to see if the NPC is templated to a Leveled List at any point.
     * If it is, that Leveled List is returned; Null if not.
     *
     * @param npc NPC formID to investigate.
     * @param templateFlagsToCheck Template flags to consider.
     * @return LVLN that it is templated to, or null.
     */
    public static LVLN isTemplatedToLList(FormID npc, NPC_.TemplateFlag ... templateFlagsToCheck) {
	return isTemplatedToLList(npc, templateFlagsToCheck, 0);
    }

    /**
     * Checks the given template flag "chains" to see if the NPC is templated to a Leveled List at any point.
     * If it is, that Leveled List is returned; Null if not.
     *
     * @param npc NPC to investigate.
     * @param templateFlagsToCheck Template flags to consider.
     * @return LVLN that it is templated to, or null.
     */
    public static LVLN isTemplatedToLList(NPC_ npc, NPC_.TemplateFlag... templateFlagsToCheck) {
	return isTemplatedToLList(npc.getForm(), templateFlagsToCheck);
    }

    static LVLN isTemplatedToLList(FormID npc, NPC_.TemplateFlag[] templateFlagsToCheck, int depth) {
	if (depth > 100) {
	    return null; // avoid circular template overflows
	}

	if (templateFlagsToCheck.length == 0) {
	    templateFlagsToCheck = NPC_.TemplateFlag.values();
	}

	NPC_ npcSrc = (NPC_) SPDatabase.getMajor(npc, GRUP_TYPE.NPC_);

	if (npcSrc != null && !npcSrc.getTemplate().equals(FormID.NULL)) {
	    boolean hasTargetTemplate = false;
	    for (NPC_.TemplateFlag flag : templateFlagsToCheck) {
		if (npcSrc.get(flag)) {
		    hasTargetTemplate = true;
		    break;
		}
	    }
	    if (!hasTargetTemplate) {
		return null;
	    }

	    NPC_ templateN = (NPC_) SPDatabase.getMajor(npcSrc.getTemplate(), GRUP_TYPE.NPC_);
	    if (templateN != null) { // If template is an NPC, recursively chain the check
		return isTemplatedToLList(templateN.getForm(), templateFlagsToCheck, depth + 1);
	    } else {
		return (LVLN) SPDatabase.getMajor(npcSrc.getTemplate(), GRUP_TYPE.LVLN);
	    }
	}
	return null;
    }

}
