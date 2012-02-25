package skyproc;

import java.io.IOException;
import java.util.zip.DataFormatException;
import lev.LExportParser;
import lev.LFlags;
import lev.LShrinkArray;
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
    void export(LExportParser out, Mod srcMod) throws IOException {
	super.export(out, srcMod);
	out.write(flags.export(), flags.length());
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	super.parseData(in);
	flags.set(in.getAllBytes());
    }

    void set(int bit, boolean on) {
	flags.set(bit, on);
    }

    boolean is(int bit) {
	return flags.is(bit);
    }

    @Override
    public String print() {
	return flags.toString();
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubFlag(type, 0);
    }

    @Override
    public void clear() {
	flags.clear();
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
