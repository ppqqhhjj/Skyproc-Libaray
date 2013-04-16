/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public enum FirstPersonFlags {

    /**
     *
     */
    HEAD,
    /**
     *
     */
    HAIR,
    /**
     *
     */
    BODY,
    /**
     *
     */
    HANDS,
    /**
     *
     */
    FOREARMS,
    /**
     *
     */
    AMULET,
    /**
     *
     */
    RING,
    /**
     *
     */
    FEET,
    /**
     *
     */
    CALVES,
    /**
     *
     */
    SHIELD,
    /**
     *
     */
    TAIL,
    /**
     *
     */
    LONG_HAIR,
    /**
     *
     */
    CIRCLET,
    /**
     *
     */
    EARS,
    /**
     *
     */
    BodyAddOn3,
    /**
     *
     */
    BodyAddOn4,
    /**
     *
     */
    BodyAddOn5,
    /**
     *
     */
    BodyAddOn6,
    /**
     *
     */
    BodyAddOn7,
    /**
     *
     */
    BodyAddOn8,
    /**
     *
     */
    DecapitateHead,
    /**
     *
     */
    Decapitate,
    /**
     *
     */
    BodyAddOn9,
    /**
     *
     */
    BodyAddOn10,
    /**
     *
     */
    BodyAddOn11,
    /**
     *
     */
    BodyAddOn12,
    /**
     *
     */
    BodyAddOn13,
    /**
     *
     */
    BodyAddOn14,
    /**
     *
     */
    BodyAddOn15,
    /**
     *
     */
    BodyAddOn16,
    /**
     *
     */
    BodyAddOn17,
    /**
     *
     */
    FX01,
    NONE;
    
    int getValue() {
	switch(this) {
	    case NONE:
		return -1;
	    default:
		return ordinal();
	}
    }
    
    static FirstPersonFlags getValue(int val) {
	if (-1 == val) {
	    return NONE;
	} else {
	    return values()[val];
	}
    }
}
