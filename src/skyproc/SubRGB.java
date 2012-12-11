/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubRGB extends SubRecordTyped {

    float r;
    float g;
    float b;

    SubRGB(Type type, float red, float green, float blue) {
	this(type);
	r = red;
	g = green;
	b = blue;
    }

    SubRGB(Type type) {
	super(type);
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubRGB(type);
    }

    @Override
    int getContentLength(Mod srcMod) {
	return 12;
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	r = in.extractFloat();
	g = in.extractFloat();
	b = in.extractFloat();
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " to : " + print());
	}
    }

    @Override
    public String print() {
	if (isValid()) {
	    return "R: " + r + " G: " + g + " B: " + b;
	} else {
	    return super.toString();
	}
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    super.export(out, srcMod);
	    out.write(r);
	    out.write(g);
	    out.write(b);
	}
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final SubRGB other = (SubRGB) obj;
	if (Float.floatToIntBits(this.r) != Float.floatToIntBits(other.r)) {
	    return false;
	}
	if (Float.floatToIntBits(this.g) != Float.floatToIntBits(other.g)) {
	    return false;
	}
	if (Float.floatToIntBits(this.b) != Float.floatToIntBits(other.b)) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 19 * hash + Float.floatToIntBits(this.r);
	hash = 19 * hash + Float.floatToIntBits(this.g);
	hash = 19 * hash + Float.floatToIntBits(this.b);
	return hash;
    }

    /**
     *
     * @param color
     * @param val
     */
    public void set (RGB color, float val) {
	switch (color) {
	    case Red:
		r = val;
		break;
	    case Green:
		g = val;
		break;
	    case Blue:
		b = val;
		break;
	}
    }

    /**
     *
     * @param color
     * @return
     */
    public float get (RGB color) {
	if (!isValid()) {
	    return 0;
	}
	switch (color) {
	    case Red:
		return r;
	    case Green:
		return g;
	    default:
		return b;
	}
    }
}
