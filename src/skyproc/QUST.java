/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LImport;
import lev.LOutFile;
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

    /**
     *
     */
    public abstract static class Alias extends SubShellBulkType {

	Alias(SubPrototype proto) {
	    super(proto, false);
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name) {
	    subRecords.setSubStringPointer("ALID", name);
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
	    return subRecords.getSubString("ALID").print();
	}

	/**
	 *
	 * @param val
	 */
	public abstract void setAliasID(int val);

	/**
	 *
	 * @return
	 */
	public abstract int getAliasID();

	/**
	 *
	 * @param name
	 */
	public void setAliasName(String name) {
	    subRecords.setSubString("ALID", name);
	}

	/**
	 *
	 * @return
	 */
	public String getAliasName() {
	    return subRecords.getSubString("ALID").print();
	}

	/**
	 *
	 * @param id
	 */
	public void setUniqueActor(FormID id) {
	}

	/**
	 *
	 * @return
	 */
	public FormID getUniqueActor() {
	    return FormID.NULL;
	}
    }

    /**
     *
     */
    public static class AliasLocation extends Alias {

	/**
	 *
	 * @param val
	 */
	public AliasLocation(int val) {
	    this();
	    setAliasID(val);
	}

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
	 * @param val
	 */
	@Override
	public void setAliasID(int val) {
	    subRecords.setSubInt("ALLS", val);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getAliasID() {
	    return subRecords.getSubInt("ALLS").get();
	}
    }

    /**
     *
     */
    public static class AliasReference extends Alias {

	/**
	 *
	 * @param val
	 */
	public AliasReference(int val) {
	    this();
	    setAliasID(val);
	}

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
	 * @param val
	 */
	@Override
	public void setAliasID(int val) {
	    subRecords.setSubInt("ALST", val);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int getAliasID() {
	    return subRecords.getSubInt("ALST").get();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public FormID getUniqueActor() {
	    return subRecords.getSubForm("ALUA").getForm();
	}

	/**
	 *
	 * @param id
	 */
	@Override
	public void setUniqueActor(FormID id) {
	    subRecords.setSubForm("ALUA", id);
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
	int getContentLength(ModExporter out) {
	    return 12;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(flags.export());
	    out.write(priority, 1);
	    out.write(unknown, 1);
	    out.write(unknown2);
	    out.write(questType.ordinal());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
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
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(index, 2);
	    out.write(flags.export());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    index = in.extractInt(2);
	    flags.set(in.extract(2));
	}

	@Override
	SubRecord getNew(String type) {
	    return new INDX();
	}

	@Override
	int getContentLength(ModExporter out) {
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
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
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
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}

	/**
	 *
	 * @param text
	 */
	public void setJournalText(String text) {
	    subRecords.setSubStringPointer("CNAM", text);
	}

	/**
	 *
	 * @return
	 */
	public String getJournalText() {
	    return subRecords.getSubStringPointer("CNAM").print();
	}

	/**
	 *
	 * @param id
	 */
	public void setNextQuest(FormID id) {
	    subRecords.setSubForm("NAM0", id);
	}

	/**
	 *
	 * @return
	 */
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

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 */
	public void clearTargets() {
	    subRecords.getSubList("QSTA").clear();
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
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
	void export(ModExporter out) throws IOException {
	    super.export(out);
	    out.write(targetAlias);
	    out.write(flags.export());
	}

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, BadParameter, DataFormatException {
	    super.parseData(in, srcMod);
	    targetAlias = in.extractInt(4);
	    flags.set(in.extract(4));
	}

	@Override
	SubRecord getNew(String type) {
	    return new QuestTargetData();
	}

	@Override
	int getContentLength(ModExporter out) {
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
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void addCondition(Condition c) {
	    subRecords.getSubList("CTDA").add(c);
	}

	/**
	 * @deprecated modifying the ArrayList will now directly affect the
	 * record.
	 * @param c
	 */
	public void removeCondition(Condition c) {
	    subRecords.getSubList("CTDA").remove(c);
	}

	/**
	 *
	 * @param on
	 */
	public void setCompassMarkersIgnoreLocks(boolean on) {
	    getData().flags.set(0, on);
	}

	/**
	 *
	 * @return
	 */
	public boolean getCompassMarkersIgnoreLocks() {
	    return getData().flags.get(0);
	}
    }

    static class ScriptFragments extends SubRecord {

	byte unknown = 0;
	StringNonNull fragmentFile = new StringNonNull();
	ArrayList<ScriptFragment> fragments = new ArrayList<>();
	boolean valid = false;

	@Override
	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(1)[0];
	    int count = in.extractInt(2);
	    fragmentFile.set(in.extractString(in.extractInt(2)));
	    for (int i = 0 ; i < count ; i++) {
		ScriptFragment frag = new ScriptFragment();
		frag.parseData(in, srcMod);
		fragments.add(frag);
	    }
	    valid = true;
	}

	@Override
	void export(ModExporter out) throws IOException {
	    if (!valid) {
		return;
	    }
	    out.write(unknown, 1);
	    out.write(fragments.size(), 2);
	    fragmentFile.export(out);
	    for (ScriptFragment frag : fragments) {
//		frag.export(out);
	    }
	}

	@Override
	int getContentLength(ModExporter out) {
	    if (!valid) {
		return 0;
	    }
	    int len = 3;
	    len += fragmentFile.getTotalLength(out);
	    for (ScriptFragment frag : fragments) {
		len += frag.getContentLength(out);
	    }
	    return len;
	}

	@Override
	SubRecord getNew(String type) {
	    return new ScriptFragments();
	}

	@Override
	ArrayList<String> getTypes() {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
    }

    static class ScriptFragment {

	byte[] unknown = new byte[9];
	StringNonNull scriptName = new StringNonNull();
	StringNonNull fragmentName = new StringNonNull();

	void parseData(LImport in, Mod srcMod) throws BadRecord, DataFormatException, BadParameter {
	    unknown = in.extract(9);
	    scriptName.set(in.extractString(in.extractInt(2)));
	    fragmentName.set(in.extractString(in.extractInt(2)));
	}

	void export(ModExporter out) throws IOException {
	    out.write(unknown);
	    scriptName.export(out);
	    fragmentName.export(out);
	}

	int getContentLength(ModExporter out) {
	    return 9 + scriptName.getTotalLength(out)
		    + fragmentName.getTotalLength(out);
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

    /**
     *
     */
    public enum QuestFlags {

	/**
	 *
	 */
	StartGameEnabled(0),
	/**
	 *
	 */
	WildernessEncounter(2),
	/**
	 *
	 */
	AllowRepeatedStages(3),
	/**
	 *
	 */
	RunOnce(4),
	/**
	 *
	 */
	ExcludeFromDialogueExport(5),
	/**
	 *
	 */
	WarnOnAliasFillFailure(6);
	int value;

	QuestFlags(int val) {
	    value = val;
	}
    }

    /**
     *
     */
    public enum QuestType {

	/**
	 *
	 */
	None,
	/**
	 *
	 */
	MainQuest,
	/**
	 *
	 */
	MageGuild,
	/**
	 *
	 */
	ThievesGuild,
	/**
	 *
	 */
	DarkBrotherhood,
	/**
	 *
	 */
	Companion,
	/**
	 *
	 */
	Misc,
	/**
	 *
	 */
	Daedric,
	/**
	 *
	 */
	Side,
	/**
	 *
	 */
	CivilWar,
	/**
	 *
	 */
	Vampire,
	/**
	 *
	 */
	Dragonborn
    }

    QUST() {
	super();
	subRecords.setPrototype(QUSTproto);
    }

    /**
     *
     * @param edid
     */
    public QUST(String edid) {
	this();
	originateFromPatch(edid);
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
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param c
     */
    public void addCondition(Condition c) {
	subRecords.getSubList("CTDA").add(c);
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param c
     */
    public void removeCondition(Condition c) {
	subRecords.getSubList("CTDA").remove(c);
    }

    /**
     * @deprecated Duplicate accessor
     * @return
     */
    public ArrayList<QuestStage> getQuestStages() {
	return subRecords.getSubList("INDX").toPublic();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param stage
     */
    public void addQuestStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get("DNAM");
    }

    /**
     *
     * @return
     */
    public int getPriority() {
	return getDNAM().priority;
    }

    /**
     *
     * @param priority
     */
    public void setPriority(int priority) {
	if (priority < 0) {
	    priority = 0;
	} else if (priority > 100) {
	    priority = 100;
	}
	getDNAM().priority = (byte) priority;
    }

    /**
     *
     * @param flag
     * @param on
     */
    public void set(QuestFlags flag, boolean on) {
	getDNAM().flags.set(flag.value, on);
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean get(QuestFlags flag) {
	return getDNAM().flags.get(flag.value);
    }

    /**
     *
     * @param type
     */
    public void setQuestType(QuestType type) {
	getDNAM().questType = type;
    }

    /**
     *
     * @return
     */
    public QuestType getQuestType() {
	return getDNAM().questType;
    }

    /**
     *
     * @return
     */
    public String getShortName() {
	return subRecords.getSubString("ENAM").print();
    }

    /**
     *
     * @param shortName
     */
    public void setShortName(String shortName) {
	subRecords.setSubString("ENAM", shortName);
    }

    /**
     *
     * @return
     */
    public String getObjectWindowFilter() {
	return subRecords.getSubString("FLTR").print();
    }

    /**
     *
     * @param name
     */
    public void setObjectWindowFilter(String name) {
	subRecords.setSubString("FLTR", name);
    }

    /**
     *
     * @return
     */
    public ArrayList<QuestStage> getStages() {
	return subRecords.getSubList("INDX").toPublic();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     */
    public void clearStages() {
	subRecords.getSubList("INDX").clear();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param stage
     */
    public void addStage(QuestStage stage) {
	subRecords.getSubList("INDX").add(stage);
    }

    /**
     *
     * @return
     */
    public ArrayList<QuestObjective> getObjectives() {
	return subRecords.getSubList("QOBJ").toPublic();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     */
    public void clearObjectives() {
	subRecords.getSubList("QOBJ").clear();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param objective
     */
    public void addObjective(QuestObjective objective) {
	subRecords.getSubList("QOBJ").add(objective);
    }

    /**
     *
     * @return
     */
    public ArrayList<Alias> getAliases() {
	return subRecords.getSubList("ALLS").toPublic();
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     * @param alias
     */
    public void addAlias(Alias alias) {
	subRecords.getSubList("ALLS").add(alias);
    }

    /**
     * @deprecated modifying the ArrayList will now directly affect the record.
     */
    public void clearAliases() {
	subRecords.getSubList("ALLS").clear();
    }
}
