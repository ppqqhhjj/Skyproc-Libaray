/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
public enum MovementType {
    Walk,
    Run,
    Swim,
    Fly,
    Sneak,
    Sprint;

    static String translate (MovementType type) {
	switch (type) {
	    case Walk:
		return "WALK";
	    case Run:
		return "RUN1";
	    case Sneak:
		return "SNEK";
	    case Swim:
		return "SWIM";
	    case Sprint:
		return "BLDO";
	    default:
		return "UNKN";
	}
    }

    static MovementType translate (String type) throws BadParameter {
	if (type.equals("WALK")) {
	    return Walk;
	} else if (type.equals("RUN1")) {
	    return Run;
	} else if (type.equals("SNEK")) {
	    return Sneak;
	} else if (type.equals("SWIM")) {
	    return Swim;
	} else if (type.equals("BLDO")) {
	    return Sprint;
	}

	throw new BadParameter ("Movement type " + type + " unrecognized.");

    }
}
