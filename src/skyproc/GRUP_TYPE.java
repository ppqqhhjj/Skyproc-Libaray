package skyproc;

/**
 * Types of GRUP records that skyproc can currently import.
 * @see SPImporter
 * @author Justin Swanson
 */
public enum GRUP_TYPE {

    /**
     * Game Settings
     */
    GMST,
    /**
     * Keywords
     */
    KYWD,
    /**
     * Texture Sets
     */
    TXST,
    /**
     * Factions
     */
    FACT,
    /**
     * Races
     */
    RACE,
    /**
     * Magic Effects
     */
    MGEF,
    ENCH,
    /**
     * Spells
     */
    SPEL,
    /**
     * Armors
     */
    ARMO,
    /**
     * Ingredients
     */
    INGR,
    /**
     * Alchemy
     */
    ALCH,
    /**
     * Weapons
     */
    WEAP,
    /**
     * Ammo
     */
    AMMO,
    /**
     * Non-Player Characters (Actors)
     */
    NPC_,
    /**
     * Leveled Lists
     */
    LVLN,
    LVLI,
    QUST,
    /**
     * Image Spaces
     */
    IMGS,
    /**
     * Form Lists
     */
    FLST,
    /**
     * Perks
     */
    PERK,
    AVIF,
    /**
     * Armatures
     */
    ARMA;

    static GRUP_TYPE toRecord (Enum e) {
	return GRUP_TYPE.valueOf(e.toString());
    }
    
    static boolean unfinished(GRUP_TYPE t) {
	switch(t) {
	    case QUST:
		return true;
	    default:
		return false;
	}
    }
}