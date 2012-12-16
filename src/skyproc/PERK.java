package skyproc;

import java.util.ArrayList;
import java.util.Arrays;
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
    static final ArrayList<Type> type = new ArrayList<>(Arrays.asList(new Type[]{Type.PERK}));
    static final SubPrototype PERKproto = new SubPrototype(MajorRecordDescription.descProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    add(new SubList<>(new Condition()));
	    add(new SubData(Type.DATA));
	    add(new SubForm(Type.NNAM));
	    add(SubString.getNew(Type.ICON, true));
	    add(new SubList<>(new PRKEPackage(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData(Type.PRKE));
		    add(new PRKEComplexSubPackage(new SubPrototype() {
			@Override
			protected void addRecords() {
			    add(new SubData(Type.DATA));
			    add(new SubList<>(new SubShell(new SubPrototype() {
				@Override
				protected void addRecords() {
				    add(new SubData(Type.PRKC));
				    add(new SubList<>(new Condition()));
				}
			    })));
			    add(new SubData(Type.EPFT));
			    add(new SubData(Type.EPF2));
			    add(new SubData(Type.EPF3));
			    add(new SubData(Type.EPFD));
			}
		    }));
		    add(new SubData(Type.PRKF));
		    forceExport(Type.PRKF);
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
	SubRecord getNew(Type type) {
	    return new PRKEPackage(getPrototype());
	}

	@Override
	Boolean isValid() {
	    return subRecords.get(Type.PRKE).isValid() && subRecords.get(Type.DATA).isValid();
	}
    }

    static class PRKEComplexSubPackage extends SubShell {

	PRKEComplexSubPackage(SubPrototype proto) {
	    super(proto);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new PRKEComplexSubPackage(getPrototype());
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
    ArrayList<Type> getTypes() {
	return type;
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
