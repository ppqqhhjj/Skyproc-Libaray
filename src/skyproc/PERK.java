package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A record fully describing and specifying a perk, including level up perks, as
 * well as more obscure hidden perks related to quests and NPCs.
 *
 * @author Justin Swanson
 */
public class PERK extends MajorRecordDescription {

    static final SubRecordsPrototype PERKproto = new SubRecordsPrototype(MajorRecordDescription.descProto) {

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
    private static Type[] type = {Type.PERK};

    PERK() {
	super();
	subRecords.prototype = PERKproto;
    }

    @Override
    Type[] getTypes() {
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

    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    static class PRKEPackage extends SubShellBulkType {

	private static Type[] types = {Type.PRKF, Type.CTDA, Type.DATA, Type.PRKC, Type.CIS2, Type.CIS1, Type.EPFT, Type.EPFD, Type.EPF2, Type.EPF3};
	SubData PRKE = new SubData(Type.PRKE);
	SubData PRKF = new SubData(Type.PRKF);
	SubRecord subPackage;
	PerkType perkType;

	PRKEPackage() {
	    super(Type.PRKE, types);
	    subRecords.add(PRKE);
	    subRecords.add(PRKF);
	    PRKF.forceExport(true);
	}

	PRKEPackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    PRKE.export(out, srcMod);
	    subPackage.export(out, srcMod);
	    PRKF.export(out, srcMod);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    PRKE.parseData(PRKE.extractRecordData(in));
	    perkType = PerkType.values()[PRKE.getData()[0]];
	    switch (perkType) {
		case QUEST:
		    subPackage = new SubFormData(Type.DATA);
		    break;
		case ABILITY:
		    subPackage = new SubForm(Type.DATA);
		    break;
		case COMPLEX:
		    subPackage = new PRKEComplexSubPackage();
		    break;
	    }
	    subRecords.remove(Type.PRKF);
	    subRecords.add(subPackage);
	    subRecords.add(PRKF);
	    super.parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKEPackage();
	}

	@Override
	Boolean isValid() {
	    return PRKE.isValid() && subPackage.isValid();
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return PRKE.getTotalLength(srcMod) + PRKF.getTotalLength(srcMod)
		    + subPackage.getTotalLength(srcMod);
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    return subPackage.allFormIDs();
	}
    }

    //If PRKE package is type complex, this contains
    //DATA, PRKC packages, and EPFT records
    static class PRKEComplexSubPackage extends SubRecord {

	private final static Type[] types = {Type.DATA, Type.PRKC, Type.CTDA, Type.CIS1, Type.CIS2, Type.EPFT, Type.EPFD, Type.EPF2, Type.EPF3};
	SubData DATA = new SubData(Type.DATA);
	SubList<PRKCpackage> PRKCs = new SubList<PRKCpackage>(new PRKCpackage());
	SubData EPFT = new SubData(Type.EPFT);
	SubRecord EPFD;
	SubData EPF2 = new SubData(Type.EPF2);
	SubData EPF3 = new SubData(Type.EPF3);

	PRKEComplexSubPackage() {
	    super(types);
	}

	PRKEComplexSubPackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKEComplexSubPackage();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    DATA.export(out, srcMod);
	    PRKCs.export(out, srcMod);
	    EPFT.export(out, srcMod);
	    EPF2.export(out, srcMod);
	    EPF3.export(out, srcMod);
	    if (EPFD != null && EPFD.isValid()) {
		EPFD.export(out, srcMod);
	    }
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    switch (getNextType(in)) {
		case DATA:
		    DATA.parseData(in);
		    break;
		case EPFT:
		    EPFT.parseData(in);
		    if (EPFT.toInt() >= 3 && EPFT.toInt() <= 5) {
			EPFD = new SubForm(Type.EPFD);
		    } else {
			EPFD = new SubData(Type.EPFD);
		    }
		    break;
		case EPF2:
		    EPF2.parseData(in);
		    break;
		case EPF3:
		    EPF3.parseData(in);
		    break;
		case EPFD:
		    if (EPFD != null) {
			EPFD.parseData(in);
		    } else {
			throw new BadRecord("EPFD did not get initialized.");
		    }
		    break;
		default:
		    PRKCs.parseData(in);
		    break;
	    }
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    int out = 0;
	    if (DATA.isValid()) {
		out += DATA.getTotalLength(srcMod);
	    }
	    if (PRKCs.isValid()) {
		out += PRKCs.getTotalLength(srcMod);
	    }
	    if (EPFT.isValid()) {
		out += EPFT.getTotalLength(srcMod);
	    }
	    if (EPF2.isValid()) {
		out += EPF2.getTotalLength(srcMod);
	    }
	    if (EPF3.isValid()) {
		out += EPF3.getTotalLength(srcMod);
	    }
	    if (EPFD != null && EPFD.isValid()) {
		out += EPFD.getTotalLength(srcMod);
	    }
	    return out;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>();
	    if (EPFD != null) {
		out.addAll(EPFD.allFormIDs());
	    }
	    out.addAll(PRKCs.allFormIDs());
	    return out;
	}
    }

    //A PRKC package contains a PRKC record and
    //a number of CTDA packages
    static class PRKCpackage extends SubRecord {

	private static Type[] types = {Type.PRKC, Type.CTDA, Type.CIS1, Type.CIS2};
	SubData PRKC = new SubData(Type.PRKC);
	SubList<Condition> CTDAs = new SubList<Condition>(new Condition());

	PRKCpackage() {
	    super(types);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    switch (getNextType(in)) {
		case PRKC:
		    PRKC.parseData(in);
		    break;
		default:
		    CTDAs.parseData(in);
		    break;
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    PRKC.export(out, srcMod);
	    CTDAs.export(out, srcMod);
	}

	@Override
	int getSizeLength() {
	    return 0;
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKCpackage();
	}

	@Override
	Boolean isValid() {
	    return PRKC.isValid()
		    && CTDAs.isValid();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return PRKC.getTotalLength(srcMod)
		    + CTDAs.getTotalLength(srcMod);
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    return CTDAs.allFormIDs();
	}
    }

    enum PerkType {

	QUEST, ABILITY, COMPLEX;
    }
}
