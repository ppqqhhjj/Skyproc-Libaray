package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A record fully describing and specifying a perk, including level up perks, as
 * well as more obscure hidden perks related to quests and NPCs.
 *
 * @author Justin Swanson
 */
public class PERK extends MajorRecordDescription {

    static final SubPrototype PERKproto = new SubPrototype(MajorRecordDescription.descProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    add(new SubList<>(new Condition()));
	    add(new SubData(Type.DATA));
	    add(new SubForm(Type.NNAM));
	    add(new SubString(Type.ICON, true));
	    add(new SubList<>(new PRKEPackage()));
	}
    };
    private static ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.PERK}));

    PERK() {
	super();
	subRecords.setPrototype(PERKproto);
    }

    @Override
    ArrayList<Type> getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new PERK();
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = super.allFormIDs();
//	out.addAll(perkSections.allFormIDs());
//	out.addAll(CTDAs.allFormIDs());
	return out;
    }

    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    static class PRKEPackage extends SubShellBulkType {

	static SubPrototype PRKEproto = new SubPrototype() {
	    @Override
	    protected void addRecords() {
		add(new SubData(Type.PRKE));
		add(new PRKEComplexSubPackage()); // Placeholder
		add(new SubData(Type.PRKF));
		forceExport(Type.PRKF);
	    }
	};

	PRKEPackage() {
	    super(PRKEproto, false);
	}

	@Override
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    SubData PRKE = subRecords.getSubData(Type.PRKE);
	    PRKE.parseData(PRKE.extractRecordData(in));
	    PerkType perkType = PerkType.values()[subRecords.getSubData(Type.PRKE).getData()[0]];
	    switch (perkType) {
		case QUEST:
		    subRecords.remove(Type.DATA);
		    subRecords.add(new SubFormData(Type.DATA));
		    break;
		case ABILITY:
		    subRecords.remove(Type.DATA);
		    subRecords.add(new SubForm(Type.DATA));
		    break;
	    }
	    super.parseData(in);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKEPackage();
	}

	@Override
	Boolean isValid() {
	    return subRecords.get(Type.PRKE).isValid() && subRecords.get(Type.DATA).isValid();
	}
    }

    static class PRKEComplexSubPackage extends SubShell {

	static SubPrototype PRKESubPackageProto = new SubPrototype() {
	    @Override
	    protected void addRecords() {
		add(new SubData(Type.DATA));
		add(new SubList<>(new PRKCpackage()));
		add(new SubData(Type.EPFT));
		add(new SubData(Type.EPF2));
		add(new SubData(Type.EPF3));
		add(new SubData(Type.EPFD));  // Placeholder
	    }
	};

	PRKEComplexSubPackage() {
	    super(PRKESubPackageProto);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKEComplexSubPackage();
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    switch (getNextType(in)) {
		case EPFT:
		    SubData EPFT = subRecords.getSubData(Type.EPFT);
		    EPFT.parseData(in);
		    if (EPFT.toInt() >= 3 && EPFT.toInt() <= 5) {
			subRecords.remove(Type.EPFD);
			subRecords.add(new SubForm(Type.EPFD));
		    }
		    return;
	    }
	    super.parseData(in);
	}

	@Override
	Boolean isValid() {
	    return true;
	}
    }

    static class PRKCpackage extends SubShell {

	static SubPrototype PRKCpackageProto = new SubPrototype() {
	    @Override
	    protected void addRecords() {
		add(new SubData(Type.PRKC));
		add(new SubList<>(new Condition()));
	    }
	};

	PRKCpackage() {
	    super(PRKCpackageProto);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKCpackage();
	}
    }

    enum PerkType {

	QUEST, ABILITY, COMPLEX;
    }
}
