/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class NiftyFunc {

    public static SPEL generateScriptAttachingSpell(Mod originateFrom, ScriptRef script, String uniqueID) {
	String name = "SP_" + uniqueID + "_" + script.name.data + "_attacher";
	MGEF mgef = new MGEF(originateFrom, name + "_MGEF", name + "_MGEF");
	mgef.scripts.addScript(script);
	SPEL spel = new SPEL(originateFrom, name + "_SPEL");
	spel.setSpellType(SPEL.SPELType.Ability);
	spel.addMagicEffect(mgef);
	return spel;
    }
}
