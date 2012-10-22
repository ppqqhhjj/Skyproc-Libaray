package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubFlag extends SubRecord {
    LFlags flags;

    SubFlag(Type type_, int size) {
	super(type_);
	flags = new LFlags(size);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	out.write(flags.export(), flags.length());
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	flags.set(in.getAllBytes());
    }

    void set(int bit, boolean on) {
	flags.set(bit, on);
    }

    boolean is(int bit) {
	return flags.get(bit);
    }

    @Override
    public String print() {
	return flags.toString();
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubFlag(type, flags.length());
    }

    @Override
    public Boolean isValid() {
	return flags != null;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return flags.length();
    }
}
