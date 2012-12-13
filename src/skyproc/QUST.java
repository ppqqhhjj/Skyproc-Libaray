/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class QUST extends MajorRecordNamed {

    // Static prototypes and definitions
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.QUST}));
    static SubPrototype ALSTALLSproto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubString(Type.ALID, true));
	    add(new SubFlag(Type.FNAM, 4));
	    add(new SubForm(Type.ALUA));
	    add(new SubForm(Type.ALCO));
	    add(new SubList<>(new SubForm(Type.ALEQ)));
	    add(new SubString(Type.ALFE, false));
	    add(new SubForm(Type.ALFL));
	    add(new SubForm(Type.ALFR));
	    add(new SubForm(Type.ALRT));
	    add(new SubInt(Type.ALFD));
	    add(new SubList<>(new Condition()));
	    add(new SubInt(Type.ALCA));
	    add(new SubInt(Type.ALCL));
	    add(new SubInt(Type.ALEA));
	    add(new SubInt(Type.ALFA));
	    add(new SubInt(Type.ALNA));
	    add(new SubInt(Type.ALNT));
	    add(new SubForm(Type.VTCK));
	    forceExport(Type.VTCK);
	    add(new SubData(Type.ALED));
	    add(new SubForm(Type.ALDN));
	    add(new SubList<>(new SubForm(Type.ALFC)));
	    add(new SubList<>(new SubInt(Type.ALFI)));
	    add(new SubList<>(new SubForm(Type.ALPC)));
	    add(new SubList<>(new SubForm(Type.ALSP)));
	    add(new SubListCounted<>(Type.COCT, 4, new SubFormInt(Type.CNTO)));
	    add(new SubForm(Type.SPOR));
	    add(new SubForm(Type.ECOR));
	    add(new SubForm(Type.KNAM));
	    add(new KeywordSet());
	    add(new SubInt(Type.NAM0));
	    add(new SubInt(Type.QTGL));
	}
    };
    static final SubPrototype QUSTproto = new SubPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    reposition(Type.FULL);
	    add(new DNAM());
	    add(new SubString(Type.ENAM, false));
	    add(new SubForm(Type.QTGL));
	    add(new SubString(Type.FLTR, true));
	    add(new SubList<>(new Condition()));
	    add(new SubData(Type.NEXT));
	    forceExport(Type.NEXT);
	    add(new SubList<>(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt(Type.INDX));
		    add(new SubList<>(new SubShell(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubFlag(Type.QSDT, 1));
			    add(new SubForm(Type.NAM0));
			    add(new SubString(Type.CNAM, true));
			    add(new SubData(Type.SCHR));
			    add(new SubForm(Type.QNAM));
			    add(new SubString(Type.SCTX, false));
			    add(new SubList<>(new Condition()));
			}
		    })));
		}
	    }, false)));
	    add(new SubInt(Type.ANAM));
	    add(new SubList<>(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt(Type.QOBJ, 2));
		    add(new SubData(Type.FNAM));
		    add(new SubStringPointer(Type.NNAM, SubStringPointer.Files.DLSTRINGS));
		    add(new SubList<>(new SubData(Type.QSTA)));
		    add(new SubList<>(new Condition()));
		}
	    }, false)));
	    add(new SubList<>(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt(Type.ALLS));
		    mergeIn(ALSTALLSproto);
		}
	    }, false)));
	    add(new SubList<>(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubInt(Type.ALST));
		    mergeIn(ALSTALLSproto);
		}
	    }, false)));
	}
    };

    static class DNAM extends SubRecordTyped {

	LFlags flags1 = new LFlags(1);
	LFlags flags2 = new LFlags(1);
	byte priority = 0;
	byte unknown = 0;
	int unknown2 = 0;
	int questType = 0;

	DNAM() {
	    super(Type.DNAM);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags1.export());
	    out.write(flags2.export());
	    out.write(priority, 1);
	    out.write(unknown, 1);
	    out.write(unknown2);
	    out.write(questType);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    flags1.set(in.extract(1));
	    flags2.set(in.extract(1));
	    priority = in.extract(1)[0];
	    unknown = in.extract(1)[0];
	    unknown2 = in.extractInt(4);
	    questType = in.extractInt(4);
	}
    }

    QUST() {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    QUST(Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
	DNAM dnam = (DNAM) subRecords.get(Type.DNAM);
	dnam.flags1.set(0, true);
	dnam.flags1.set(4, true);
	dnam.flags2.set(0, true);
	subRecords.getSubData(Type.NEXT).forceExport(true);
	subRecords.getSubInt(Type.ANAM).set(0);
    }

    @Override
    Record getNew() {
	return new QUST();
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }
}
