/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LFlags;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class BOOK extends MajorRecordDescription {
    
    // Static prototypes and definitions
    static final class DATA extends SubRecord {

	LFlags flags = new LFlags(4);
	int bookType = 0;
	int value = 0;
	float weight = 0;

	DATA() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export());
	    out.write(bookType);
	    out.write(value);
	    out.write(weight);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    flags.set(in.extract(4));
	    bookType = in.extractInt(4);
	    value = in.extractInt(4);
	    weight = in.extractFloat();
	}

	@Override
	SubRecord getNew(String type) {
	    return new DATA();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 16;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DATA");
	}
    }
    static final SubPrototype BOOKprototype = new SubPrototype(MajorRecordDescription.descProto) {

	@Override
	protected void addRecords() {
	    add(new ScriptPackage());
	    add(new SubData("OBND"));
	    reposition("FULL");
	    add(SubString.getNew("MODL", true));
	    add(new SubData("MODT"));
	    add(new AltTextures("MODS"));
	    add(new SubString("ICON"));
	    add(new SubString("MICO"));
	    reposition("DESC");
	    add(new DestructionData());
	    add(new SubForm("YNAM"));
	    add(new SubForm("ZNAM"));
	    add(new KeywordSet());
	    add(new DATA());
	    add(new SubForm("INAM"));
	    add(new SubStringPointer("CNAM", SubStringPointer.Files.DLSTRINGS));
	}
    };

    // Common Functions
    BOOK() {
	super();
	subRecords.setPrototype(BOOKprototype);
    }

    @Override
    Record getNew() {
	return new BOOK();
    }
    
    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("BOOK");
    }
}
