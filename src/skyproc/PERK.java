package skyproc;

import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A record fully describing and specifying a perk, including level up perks, as
 * well as more obscure hidden perks related to quests and NPCs.
 *
 * @author Justin Swanson
 */
public class PERK extends MajorRecordDescription {

    // Static prototypes and definitions
    static final SubPrototype PERKproto = new SubPrototype(MajorRecordDescription.descProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    add(new SubList<>(new Condition()));
	    add(new SubData("DATA"));
	    add(new SubForm("NNAM"));
	    add(SubString.getNew("ICON", true));
	    add(new SubList<>(new PRKEPackage(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData("PRKE"));
		    add(new PRKEComplexSubPackage(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubData("DATA"));
			    add(new SubList<>(new SubShell(new SubPrototype() {
				@Override
				protected void addRecords() {
				    add(new SubData("PRKC"));
				    add(new SubList<>(new Condition()));
				}
			    })));
			    add(new SubData("EPFT"));
			    add(new SubData("EPF2"));
			    add(new SubData("EPF3"));
			    add(new SubData("EPFD"));
			}
		    }));
		    add(new SubData("PRKF"));
		    forceExport("PRKF");
		}
	    })));
	}
    };

    static class PRKEPackage extends SubShellBulkType {

	PRKEPackage(SubPrototype proto) {
	    super(proto, false);
	}

	@Override
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    SubData PRKE = subRecords.getSubData("PRKE");
	    PRKE.parseData(PRKE.extractRecordData(in));
	    PerkType perkType = PerkType.values()[subRecords.getSubData("PRKE").getData()[0]];
	    switch (perkType) {
		case QUEST:
		    subRecords.remove("DATA");
		    subRecords.add(new SubFormData("DATA"));
		    break;
		case ABILITY:
		    subRecords.remove("DATA");
		    subRecords.add(new SubForm("DATA"));
		    break;
	    }
	    super.parseData(in);
	}

	@Override
	SubRecord getNew(String type) {
	    return new PRKEPackage(getPrototype());
	}

	@Override
	Boolean isValid() {
	    return subRecords.get("PRKE").isValid() && subRecords.get("DATA").isValid();
	}
    }

    static class PRKEComplexSubPackage extends SubShell {

	PRKEComplexSubPackage(SubPrototype proto) {
	    super(proto);
	}

	@Override
	SubRecord getNew(String type) {
	    return new PRKEComplexSubPackage(getPrototype());
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    switch (getNextType(in)) {
		case "EPFT":
		    SubData EPFT = subRecords.getSubData("EPFT");
		    EPFT.parseData(in);
		    if (EPFT.toInt() >= 3 && EPFT.toInt() <= 5) {
			subRecords.remove("EPFD");
			subRecords.add(new SubForm("EPFD"));
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

    // Enums
    enum PerkType {

	QUEST, ABILITY, COMPLEX;
    }

    // Common Functions
    PERK() {
	super();
	subRecords.setPrototype(PERKproto);
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("PERK");
    }

    @Override
    Record getNew() {
	return new PERK();
    }

    // Get/Set
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }
}
