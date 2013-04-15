package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Justin Swanson
 */
public class SubStringNonNull extends SubString {

    SubStringNonNull(String t) {
	super(t);
    }

    @Override
    void parseData(LChannel in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	in.skip(getIdentifierLength() + getSizeLength());
	string = Ln.arrayToString(in.extractInts(in.available()));
	if (logging()) {
	    logSync(getType().toString(), "Setting " + toString() + " to " + print());
	}
    }

    @Override
    int getContentLength(ModExporter out) {
	return string.length();
    }

    @Override
    void export(ModExporter out) throws IOException {
	out.write(getType().toString());
	out.write(getContentLength(out), 2);
	out.write(string);
    }

    @Override
    SubRecord getNew(String type_) {
	return new SubStringNonNull(type_);
    }
}
