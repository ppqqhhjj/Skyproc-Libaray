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
public class QUST extends MajorRecordNamed {

    // Static prototypes and definitions
    static final SubPrototype ALSTALLSproto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(SubString.getNew("ALID", true));
	    add(new SubFlag("FNAM", 4));
	    add(new SubForm("ALUA"));
	    add(new SubForm("ALCO"));
	    add(new SubList<>(new SubForm("ALEQ")));
	    add(SubString.getNew("ALFE", false));
	    add(new SubForm("ALFL"));
	    add(new SubForm("ALFR"));
	    add(new SubInt("ALFA"));
	    add(new SubForm("ALRT"));
	    add(new SubInt("ALFD"));
	    add(new SubList<>(new Condition()));
	    add(new SubData("ALCA"));
	    add(new SubInt("ALCL"));
	    add(new SubInt("ALEA"));
	    add(new SubInt("ALNA"));
	    add(new SubInt("ALNT"));
	    add(new SubForm("VTCK"));
	    add(new SubData("ALED"));
	    add(new SubForm("ALDN"));
	    add(new SubList<>(new SubForm("ALFC")));
	    add(new SubList<>(new SubInt("ALFI")));
	    add(new SubList<>(new SubForm("ALPC")));
	    add(new SubList<>(new SubForm("ALSP")));
	    add(new SubListCounted<>("COCT", 4, new SubFormInt("CNTO")));
	    add(new SubForm("SPOR"));
	    add(new SubForm("ECOR"));
	    add(new SubForm("KNAM"));
	    add(new KeywordSet());
	    add(new SubInt("NAM0"));
	    add(new SubInt("QTGL"));
	}
    };
    static final SubPrototype aliasLocationProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubInt("ALLS"));
	    mergeIn(ALSTALLSproto);
	}
    };
    static final SubPrototype aliasReferenceProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubInt("ALST"));
	    mergeIn(ALSTALLSproto);
	    forceExport("VTCK");
	}
    };
    static final SubPrototype questLogEntryProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubFlag("QSDT", 1));
	    add(new SubForm("NAM0"));
	    add(new SubStringPointer("CNAM", SubStringPointer.Files.DLSTRINGS));
	    add(new SubData("SCHR"));
	    add(new SubForm("QNAM"));
	    add(SubString.getNew("SCTX", false));
	    add(new SubList<>(new Condition()));
	}
    };
    static final SubPrototype questStageProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new INDX());
	    add(new SubList<>(new QuestLogEntry()));
	}
    };
    static final SubPrototype questTargetProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new QuestTargetData());
	    add(new SubList<>(new Condition()));
	}
    };
    static final SubPrototype questObjectiveProto = new SubPrototype() {
	@Override
	protected void addRecords() {
	    add(new SubInt("QOBJ", 2));
	    add(new SubData("FNAM"));
	    add(new SubStringPointer("NNAM", SubStringPointer.Files.DLSTRINGS));
	    add(new SubList<>(new QuestTarget()));
	}
    };
    static final SubPrototype QUSTproto = new SubPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), "EDID");
	    reposition("FULL");
	    add(new DNAM());
	    add(SubString.getNew("ENAM", false));
	    add(new SubList<>(new SubForm("QTGL")));
	    add(SubString.getNew("FLTR", true));
	    add(new SubList<>(new Condition()));
	    add(new SubShellBulkType(new SubPrototype() {
		@Override
		protected void addRecords() {
		    add(new SubData("NEXT"));
		    forceExport("NEXT");
		    add(new SubList<>(new Condition()));
		}
	    }, false));
	    add(new SubList<>(new QuestStage()));
	    add(new SubList<>(new QuestObjective()));
	    add(new SubInt("ANAM"));
	    add(new SubListMulti<Alias>(new AliasLocation(), new AliasReference()));
	}
    };

    public abstract static class Alias extends SubShellBulkType {

	Alias(SubPrototype proto) {
	    super(proto, false);
	}

	public void setName(String name) {
	    subRecords.setSubStringPointer("ALID", name);
	}

	public String getName() {
	    return subRecords.getSubString("ALID").print();
	}
    }

    /**
     *
     */
    public static class AliasLocation extends Alias {

	AliasLocation() {
	    super(aliasLocationProto);
	}

	/**
	 *
	 * @param name
	 */
	public AliasLocation(String name) {
	    this();
	    setName(name);
	}

	/**
	 *
	 * @param loc
	 */
	public void setLocation(int loc) {
	    subRecords.setSubInt("ALLS", loc);
	}

	/**
	 *
	 * @return
	 */
	public int getLocation() {
	    return subRecords.getSubInt("ALLS").get();
	}
    }

    /**
     *
     */
    public static class AliasReference extends Alias {

	AliasReference() {
	    super(aliasReferenceProto);
	}

	/**
	 *
	 * @param name
	 */
	public AliasReference(String name) {
	    this();
	    setName(name);
	}

	/**
	 *
	 * @param ref
	 */
	public void setReference(int ref) {
	    subRecords.setSubInt("ALST", ref);
	}

	/**
	 *
	 * @return
	 */
	public int getReference() {
	    return subRecords.getSubInt("ALST").get();
	}
    }

    static class DNAM extends SubRecord {

	LFlags flags = new LFlags(2);
	byte priority = 0;
	byte unknown = 0;
	int unknown2 = 0;
	QuestType questType = QuestType.None;

	DNAM() {
	    super();
	}

	@Override
	SubRecord getNew(String type) {
	    return new DNAM();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export());
	    out.write(priority, 1);
	    out.write(unknown, 1);
	    out.write(unknown2);
	    out.write(questType.ordinal());
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    flags.set(in.extract(2));
	    priority = in.extract(1)[0];
	    unknown = in.extract(1)[0];
	    unknown2 = in.extractInt(4);
	    questType = QuestType.values()[in.extractInt(4)];
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("DNAM");
	}
    }

    static class INDX extends SubRecord {

	int index = 0;
	LFlags flags = new LFlags(2);

	INDX() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(index, 2);
	    out.write(flags.export());
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    index = in.extractInt(2);
	    flags.set(in.extract(2));
	}

	@Override
	SubRecord getNew(String type) {
	    return new INDX();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 4;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("INDX");
	}
    }

    /**
     *
     */
    public static class QuestStage extends SubShellBulkType {

	/**
	 *
	 */
	public QuestStage() {
	    super(questStageProto, false);
	}

	INDX getINDX() {
	    return (INDX) subRecords.get("INDX");
	}

	/**
	 *
	 * @return
	 */
	public int getJournalIndex() {
	    return getINDX().index;
	}

	/**
	 *
	 * @param value
	 */
	public void setJournalIndex(int value) {
	    getINDX().index = value;
	}

	/**
	 *
	 * @param flag
	 * @return
	 */
	public boolean get(QuestStageFlags flag) {
	    return getINDX().flags.get(flag.ordinal() + 1);
	}

	/**
	 *
	 * @param flag
	 * @param on
	 */
	public void set(QuestStageFlags flag, boolean on) {
	    getINDX().flags.set(flag.ordinal() + 1, on);
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<QuestLogEntry> getLogEntries() {
	    return subRecords.getSubList("QSDT").toPublic();
	}

	/**
	 *
	 * @param entry
	 */
	public void addLogEntry(QuestLogEntry entry) {
	    subRecords.getSubList("QSDT").add(entry);
	}
    }

    /**
     *
     */
    public static class QuestLogEntry extends SubShell {

	/**
	 *
	 */
	public QuestLogEntry() {
	    super(questLogEntryProto);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestLogEntry();
	}

	/**
	 *
	 * @param flag
	 * @param on
	 */
	public void set(QuestLogFlags flag, boolean on) {
	    subRecords.setSubFlag("QSDT", flag.ordinal(), on);
	}

	/**
	 *
	 * @param flag
	 * @return
	 */
	public boolean get(QuestLogFlags flag) {
	    return subRecords.getSubFlag("QSDT").is(flag.ordinal());
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Condition> getConditions() {
	    return subRecords.getSubList("CTDA").toPublic();
	}

	/**
	 *
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 *
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}
	
	public void setJournalText(String text) {
	    subRecords.setSubStringPointer("CNAM", text);
	}
	
	public String getJournalText() {
	    return subRecords.getSubStringPointer("CNAM").print();
	}
	
	public void setNextQuest(FormID id) {
	    subRecords.setSubForm("NAM0", id);
	}
	
	public FormID getNextQuest() {
	    return subRecords.getSubForm("NAM0").getForm();
	}
    }

    /**
     *
     */
    public static class QuestObjective extends SubShellBulkType {

	QuestObjective() {
	    super(questObjectiveProto, false);
	}

	/**
	 *
	 * @param index
	 * @param name
	 */
	public QuestObjective(int index, String name) {
	    this();
	    setIndex(index);
	    setName(name);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestObjective();
	}

	/**
	 *
	 * @param index
	 */
	public final void setIndex(int index) {
	    subRecords.setSubInt("QOBJ", index);
	}

	/**
	 *
	 * @return
	 */
	public int getIndex() {
	    return subRecords.getSubInt("QOBJ").get();
	}

	/**
	 *
	 * @param in
	 */
	public final void setName(String in) {
	    subRecords.setSubStringPointer("NNAM", in);
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
	    return subRecords.getSubStringPointer("NNAM").print();
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<QuestTarget> getTargets() {
	    return subRecords.getSubList("QSTA").toPublic();
	}
	
	public void clearTargets() {
	    subRecords.getSubList("QSTA").clear();
	}

	/**
	 *
	 * @param target
	 */
	public void addTarget(QuestTarget target) {
	    subRecords.getSubList("QSTA").add(target);
	}
    }

    static class QuestTargetData extends SubRecord {

	int targetAlias = 0;
	LFlags flags = new LFlags(4);

	QuestTargetData() {
	    super();
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(targetAlias);
	    out.write(flags.export());
	}

	@Override
	void parseData(LChannel in) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in);
	    targetAlias = in.extractInt(4);
	    flags.set(in.extract(4));
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTargetData();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 8;
	}

	@Override
	ArrayList<String> getTypes() {
	    return Record.getTypeList("QSTA");
	}
    }

    /**
     *
     */
    public static class QuestTarget extends SubShell {

	QuestTarget() {
	    super(questTargetProto);
	}

	/**
	 *
	 * @param aliasID
	 */
	public QuestTarget(int aliasID) {
	    this();
	    setTargetAlias(aliasID);
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTarget();
	}

	QuestTargetData getData() {
	    return (QuestTargetData) subRecords.get("QSDT");
	}

	/**
	 *
	 * @param aliasID
	 */
	public final void setTargetAlias(int aliasID) {
	    getData().targetAlias = aliasID;

	}

	/**
	 *
	 * @return
	 */
	public int getTargetAlias() {
	    return getData().targetAlias;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Condition> getConditions() {
	    return subRecords.getSubList("CTDA").toPublic();
	}

	/**
	 *
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 *
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}
	
	public void setCompassMarkersIgnoreLocks(boolean on) {
	    getData().flags.set(0, on);
	}
	
	public boolean getCompassMarkersIgnoreLocks() {
	    return getData().flags.get(0);
	}
    }

    // Enums
    /**
     *
     */
    public enum QuestStageFlags {

	/**
	 *
	 */
	StartUpStage,
	/**
	 *
	 */
	ShutDownStage,
	/**
	 *
	 */
	KeepInstanceDataFromHereOn;
    }

    /**
     *
     */
    public enum QuestLogFlags {

	/**
	 *
	 */
	CompleteQuest,
	/**
	 *
	 */
	FailQuest;
    }
    
    public enum QuestFlags {
	StartGameEnabled (0),
	WildernessEncounter(2),
	AllowRepeatedStages(3),
	RunOnce(4),
	ExcludeFromDialogueExport(5),
	WarnOnAliasFillFailure(6);
	
	int value;
	QuestFlags(int val) {
	    value = val;
	}
    }
    
    public enum QuestType {
	None,
	MainQuest,
	MageGuild,
	ThievesGuild,
	DarkBrotherhood,
	Companion,
	Misc,
	Daedric,
	Side,
	CivilWar,
	Vampire,
	Dragonborn
    }

    QUST() {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    QUST(Mod modToOriginateFrom, String edid) {
	this();
	originateFrom(modToOriginateFrom, edid);
	DNAM dnam = (DNAM) subRecords.get("DNAM");
	dnam.flags.set(0, true);
	dnam.flags.set(4, true);
	dnam.flags.set(8, true);
	subRecords.getSubInt("ANAM").set(0);
    }

    @Override
    Record getNew() {
	return new QUST();
    }

    @Override
    ArrayList<String> getTypes() {
	return Record.getTypeList("QUST");
    }

    // Get Set Functions
    
    /**
     *
     * @return
     */
    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @return
     */
    public ArrayList<Condition> getConditions() {
	return subRecords.getSubList("CTDA").toPublic();
    }

    /**
     *
     * @param c
     */
    public void addCondition(Condition c) {
	subRecords.getSubList("CTDA").add(c);
    }

    /**
     *
     * @param c
     */
    public void removeCondition(Condition c) {
	subRecords.getSubList("CTDA").remove(c);
    }

    /**
     *
     * @return
     */
    public ArrayList<QuestStage> getQuestStages() {
	return subRecords.getSubList("INDX").toPublic();
    }

    /**
     *
     * @param stage
     */
    public void addQuestStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }

    /**
     *
     * @return
     */
    public ArrayList<AliasReference> getReferenceAliases() {
	return subRecords.getSubList("ALST").toPublic();
    }

    /**
     *
     * @return
     */
    public ArrayList<AliasLocation> getLocationAliases() {
	return subRecords.getSubList("ALLS").toPublic();
    }
    
    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
    }
    
    public int getPriority() {
	return getDNAM().priority;
    }
    
    public void setPriority(int priority) {
	if (priority < 0) {
	    priority = 0;
	} else if (priority > 100) {
	    priority = 100;
	}
	getDNAM().priority = (byte) priority;
    }
    
    public void set(QuestFlags flag, boolean on) {
	getDNAM().flags.set(flag.value, on);
    }
    
    public boolean get(QuestFlags flag) {
	return getDNAM().flags.get(flag.value);
    }
    
    public void setQuestType(QuestType type) {
	getDNAM().questType = type;
    }
    
    public QuestType getQuestType() {
	return getDNAM().questType;
    }
    
    public String getShortName() {
	return subRecords.getSubString("ENAM").print();
    }
    
    public void setShortName(String shortName) {
	subRecords.setSubString("ENAM", shortName);
    }
    
    public String getObjectWindowFilter() {
	return subRecords.getSubString("FLTR").print();
    }
    
    public void setObjectWindowFilter(String name) {
	subRecords.setSubString("FLTR", name);
    }
    
    public ArrayList<QuestStage> getStages() {
	return subRecords.getSubList("INDX").toPublic();
    }
    
    public void clearStages() {
	subRecords.getSubList("INDX").clear();
    }
    
    public void addStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }
    
    public ArrayList<QuestObjective> getObjectives() {
	return subRecords.getSubList("QOBJ").toPublic();
    }
    
    public void clearObjectives() {
	subRecords.getSubList("QOBJ").clear();
    }
    
    public void addObjective(QuestStage stage) {
	subRecords.getSubList("QOBJ").add(stage);
    }
    
    public ArrayList<Alias> getAliases() {
	return subRecords.getSubList("ALLS").toPublic();
    }
    
    public void addAlias(Alias alias) {
	subRecords.getSubList("ALLS").add(alias);
    }
    
    public void clearAliases() {
	subRecords.getSubList("ALLS").clear();
    }
}
