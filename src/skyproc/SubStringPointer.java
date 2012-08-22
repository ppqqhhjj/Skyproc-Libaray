package skyproc;

import java.io.IOException;
import java.util.Map;
import java.util.zip.DataFormatException;
import lev.*;
import skyproc.Mod.Mod_Flags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class SubStringPointer extends SubRecord {

    SubData data;
    SubString text;
    SubStringPointer.Files file;
    boolean forceExport = false;
    static boolean shortNull = true;

    SubStringPointer(Type type, SubStringPointer.Files file) {
	super(type);
	data = new SubData(type, new byte[1]);
	text = new SubString(type, true);
	this.file = file;
    }

    @Override
    SubRecord getNew(Type type) {
	return new SubStringPointer(type, SubStringPointer.Files.STRINGS);
    }

    void setText(String textIn) {
	text.setString(textIn);
    }

    @Override
    Boolean isValid() {
	return (text.isValid() && !text.print().equals("")) || forceExport;
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    if (srcMod.isFlag(Mod.Mod_Flags.STRING_TABLED)) {
		data.setData(Ln.toByteArray(srcMod.addOutString(text.string, file), 4));
		data.export(out, srcMod);
	    } else {
		if (text.isValid()) {
		    text.export(out, srcMod);
		} else if (forceExport) {
		    if (data.getData().length < 4 && !shortNull) {
			data.setData(0, 4);
		    }
		    data.export(out, srcMod);
		}
	    }
	}
    }

    @Override
    void parseData(LStream in) throws BadRecord, DataFormatException, BadParameter, IOException {
	data.parseData(in);
	if (logging()) {
	    logSync(toString(), "Setting " + toString() + " to : " + Ln.arrayToString(data.getData()));
	}
    }

    @Override
    void fetchStringPointers(Mod srcMod, Record r, Map<SubStringPointer.Files, LChannel> streams) throws IOException {
	if (srcMod.isFlag(Mod_Flags.STRING_TABLED)) {
	    if (data.isValid() && streams.containsKey(file)) {
		int index = Ln.arrayToInt(data.getData());
		if (srcMod.strings.get(file).containsKey(index)) {
		    int offset = srcMod.strings.get(file).get(index);
		    LChannel stream = streams.get(file);

		    stream.pos(offset);

		    switch (file) {
			case STRINGS:
			    int input;
			    String string = "";
			    while ((input = stream.read()) != 0) {
				string += (char) input;
			    }
			    text.setString(string);
			    break;
			default:
			    int length = Ln.arrayToInt(stream.readInInts(0, 4));
			    String in = Ln.arrayToString(stream.readInInts(0, length - 1)); // -1 to exclude null end
			    if (!in.equals("")) {
				text.setString(in);
			    }
		    }

		    if (logging() && SPGlobal.debugStringPairing) {
			logSync("", r.toString() + " " + file + " pointer " + Ln.printHex(data.getData(), true, false) + " set to : " + print());
		    }

		} else {
		    if (logging() && SPGlobal.debugStringPairing) {
			boolean nullPtr = true;
			for (byte b : data.getData()) {
			    if (b != 0) {
				nullPtr = false;
				break;
			    }
			}
			if (!nullPtr) {
			    logSync("", r.toString() + " " + file + " pointer " + Ln.printHex(data.getData(), true, false) + " COULD NOT BE PAIRED");
			}
		    }
		    data.setData(0, 1); // Invalidate data to stop export
		}
	    }
	} else {
	    text.setString(Ln.arrayToString(data.getData()));
	}
    }

    @Override
    public String print() {
	if (text.isValid()) {
	    return text.print();
	} else {
	    return "<NO TEXT>";
	}
    }

    @Override
    int getContentLength(Mod srcMod) {
	if (isValid()) {
	    if (srcMod.isFlag(Mod_Flags.STRING_TABLED)) {
		return 4; // length of 4
	    } else if (text.isValid()) {
		return text.getContentLength(srcMod);
	    }
	}

	if (shortNull) {
	    return 1;
	} else {
	    return 4; // empty data with 4 zeros
	}
    }

    @Override
    int getTotalLength(Mod srcMod) {
	if (isValid() || forceExport) {
	    return getContentLength(srcMod) + getHeaderLength();
	} else {
	    return 0;
	}
    }

    enum Files {

	STRINGS, ILSTRINGS, DLSTRINGS;
    }
}
