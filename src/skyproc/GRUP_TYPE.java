package skyproc;

/**
 * Types of GRUP records that skyproc can currently import.
 * @see SPImporter
 * @author Justin Swanson
 */
public enum GRUP_TYPE {

    /**
     *
     */
    KYWD,
    /**
     * Texture Sets
     */
    TXST,
    /**
     * Races
     */
    RACE,
    MGEF,
    /**
     * Spells
     */
    SPEL,
    /**
     * Armors
     */
    ARMO,
    ALCH,
    /**
     * Weapons
     */
    WEAP,
    /**
     * Non-Player Characters (Actors)
     */
    NPC_,
    /**
     * Leveled Lists
     */
    LVLN,
    /**
     * Image Spaces
     */
    IMGS,
    /**
     *
     */
    FLST,
    /**
     * Perks
     */
    PERK,
    /**
     * Armatures
     */
    ARMA;

    static GRUP_TYPE toRecord (Enum e) {
	return GRUP_TYPE.valueOf(e.toString());
    }
}