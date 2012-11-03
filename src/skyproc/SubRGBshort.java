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
public class SubRGBshort extends SubRecord {

    short r;
    short g;
    short b;
    short a;

    SubRGBshort(Type type, short red, short green, short blue, short alpha) {
	this(type);
	r = red;
	g = green;
	b = blue;
    }

    SubRGBshort(Type type) {
	super(type);
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubRGB(type);
    }

    @Override
    int getContentLength(Mod srcMod) {
	return 4;
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	r = (short) in.extractInt(1);
	g = (short) in.extractInt(1);
	b = (short) in.extractInt(1);
	a = (short) in.extractInt(1);
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " to : " + print());
	}
    }

    @Override
    public String print() {
	    return "R: " + r + " G: " + g + " B: " + b + " A: " + a;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    super.export(out, srcMod);
	    out.write(r, 1);
	    out.write(g, 1);
	    out.write(b, 1);
	    out.write(a, 1);
	}
    }

    /**
     *
     * @param color
     * @param val
     */
    public void set (RGBA color, short val) {
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
	    case Alpha:
		a = val;
		break;
	}
    }

    /**
     *
     * @param color
     * @return
     */
    public short get (RGBA color) {
	if (!isValid()) {
	    return 0;
	}
	switch (color) {
	    case Red:
		return r;
	    case Green:
		return g;
	    case Blue:
		return b;
	    default:
		return a;
	}
    }
}
