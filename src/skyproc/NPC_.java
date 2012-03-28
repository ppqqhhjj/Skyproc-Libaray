package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * An actor in the world.
 *
 * @author Justin Swanson
 */
public class NPC_ extends Actor implements Serializable {

    private static final Type[] type = {Type.NPC_};
    /**
     * A script package containing scripts and their properties
     */
    public ScriptPackage scripts = new ScriptPackage();
    SubData OBND = new SubData(Type.OBND);
    ACBS ACBS = new ACBS();
    SubList<SubFormInt> factions = new SubList<SubFormInt>(new SubFormInt(Type.SNAM));
    SubForm INAM = new SubForm(Type.INAM);
    SubForm VTCK = new SubForm(Type.VTCK);
    SubForm TPLT = new SubForm(Type.TPLT);
    SubForm RNAM = new SubForm(Type.RNAM);
    SubList<SubForm> spells = new SubList<SubForm>(Type.SPCT, 4, new SubForm(Type.SPLO));
    SubForm ECOR = new SubForm(Type.ECOR);
    SubForm SPOR = new SubForm(Type.SPOR);
    SubList<SubFormData> PRKRs = new SubList<SubFormData>(Type.PRKZ, 4, new SubFormData(Type.PRKR));
    SubList<SubFormInt> items = new SubList<SubFormInt>(Type.COCT, 4, new SubFormInt(Type.CNTO));
    AIDT AIDT = new AIDT();
    SubList<SubForm> aiPackages = new SubList<SubForm>(new SubForm(Type.PKID));
    SubForm CNAM = new SubForm(Type.CNAM);
    SubData SHRT = new SubData(Type.SHRT);
    SubData DATA = new SubData(Type.DATA);
    DNAM DNAM = new DNAM();
    SubList<SubForm> PNAMs = new SubList<SubForm>(new SubForm(Type.PNAM));
    SubForm HCLF = new SubForm(Type.HCLF);
    SubData NAM5 = new SubData(Type.NAM5);
    SubFloat NAM6 = new SubFloat(Type.NAM6);
    SubFloat NAM7 = new SubFloat(Type.NAM7);
    SubData NAM8 = new SubData(Type.NAM8);
    SubForm WNAM = new SubForm(Type.WNAM);
    SubForm ANAM = new SubForm(Type.ANAM);
    SubForm ATKR = new SubForm(Type.ATKR);
    SubData ATKD = new SubData(Type.ATKD);
    SubData ATKE = new SubData(Type.ATKE);
    /**
     *
     */
    public KeywordSet keywords = new KeywordSet();
    SubData QNAM = new SubData(Type.QNAM);
    SubData NAM9 = new SubData(Type.NAM9);
    SubList<CSDTpackage> soundPackages = new SubList<CSDTpackage>(new CSDTpackage());
    SubForm DOFT = new SubForm(Type.DOFT);
    SubForm SOFT = new SubForm(Type.SOFT);
    SubForm ZNAM = new SubForm(Type.ZNAM);
    SubForm CRIF = new SubForm(Type.CRIF);
    SubForm FTST = new SubForm(Type.FTST);
    SubForm CSCR = new SubForm(Type.CSCR);
    SubForm DPLT = new SubForm(Type.DPLT);
    SubData NAMA = new SubData(Type.NAMA);
    SubData DEST = new SubData(Type.DEST);
    SubData DSTD = new SubData(Type.DSTD);
    SubData DSTF = new SubData(Type.DSTF);
    SubData GNAM = new SubData(Type.GNAM);
    SubList<TINIpackage> tintPackages = new SubList<TINIpackage>(new TINIpackage());

    NPC_() {
	super();
	subRecords.remove(Type.FULL);  // Placing this in a different export order

	// This order validates with all of Skyrim.esm. Don't change without good reason.
	subRecords.add(scripts);
	subRecords.add(OBND);
	subRecords.add(ACBS);
	subRecords.add(factions);
	subRecords.add(INAM);
	subRecords.add(VTCK);
	subRecords.add(TPLT);
	subRecords.add(RNAM);
	spells.allowDuplicates(false);
	subRecords.add(spells);
	subRecords.add(DEST);
	subRecords.add(DSTD);
	subRecords.add(DSTF);
	subRecords.add(WNAM);
	subRecords.add(ANAM);
	subRecords.add(ATKR);
	subRecords.add(ATKD);
	subRecords.add(ATKE);
	subRecords.add(SPOR);
	subRecords.add(ECOR);
	subRecords.add(PRKRs);
	subRecords.add(items);
	subRecords.add(AIDT);
	subRecords.add(aiPackages);
	subRecords.add(keywords);
	subRecords.add(CNAM);
	subRecords.add(FULL);
	subRecords.add(SHRT);
	DATA.forceExport(true);
	subRecords.add(DATA);
	subRecords.add(DNAM);
	subRecords.add(PNAMs);
	subRecords.add(HCLF);
	subRecords.add(ZNAM);
	subRecords.add(GNAM);
	subRecords.add(NAM5);
	subRecords.add(NAM6);
	subRecords.add(NAM7);
	subRecords.add(NAM8);
	subRecords.add(CSCR);
	subRecords.add(DOFT);
	subRecords.add(SOFT);
	subRecords.add(DPLT);
	subRecords.add(CRIF);
	subRecords.add(FTST);
	subRecords.add(soundPackages);
	subRecords.add(QNAM);
	subRecords.add(NAM9);
	subRecords.add(NAMA);
	subRecords.add(tintPackages);
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new NPC_();
    }

    // Inner Classes
    static class CSDTpackage extends SubRecord implements Serializable {

	SubData CSDT = new SubData(Type.CSDT);
	ArrayList<SoundPair> soundPairs = new ArrayList<SoundPair>();
	private static Type[] types = {Type.CSDT, Type.CSDI, Type.CSDC};

	CSDTpackage() {
	    super(types);
	}

	CSDTpackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	public void clear() {
	    CSDT.clear();
	    for (SoundPair s : soundPairs) {
		s.CSDC.clear();
		s.CSDI.clear();
	    }
	}

	@Override
	Boolean isValid() {
	    boolean valid = CSDT.isValid();
	    if (valid) {
		for (SoundPair s : soundPairs) {
		    valid = valid && s.CSDC.isValid();
		    valid = valid && s.CSDI.isValid();
		}
	    }
	    return valid;
	}

	class SoundPair implements Serializable {

	    SubForm CSDI;
	    SubData CSDC = new SubData(Type.CSDC);

	    SoundPair(SubForm csdi) {
		CSDI = csdi;
	    }

	    void setChance(SubData csdc) {
		CSDC = csdc;
	    }
	}

	@Override
	SubRecord getNew(Type type_) {
	    return new CSDTpackage();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    if (isValid()) {
		int length = CSDT.getContentLength(srcMod);
		for (SoundPair pair : soundPairs) {
		    length += pair.CSDI.getTotalLength(srcMod) + pair.CSDC.getTotalLength(srcMod);
		}
		return length;
	    } else {
		return 0;
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    if (isValid()) {
		CSDT.export(out, srcMod);
		for (SoundPair pair : soundPairs) {
		    pair.CSDI.export(out, srcMod);
		    pair.CSDC.export(out, srcMod);
		}
	    }
	}

	@Override
	void standardizeMasters(Mod srcMod) {
	    for (SoundPair pairs : soundPairs) {
		pairs.CSDI.standardizeMasters(srcMod);
	    }
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    // Not calling super
	    Type t = Type.valueOf(Ln.arrayToString(in.extractInts(4)));
	    int size = Ln.arrayToInt(in.extractInts(2));
	    switch (t) {
		case CSDT:
		    CSDT.setData(in.extract(size));
		    break;
		case CSDI:
		    if (size == 4) {
			SubForm CSDI = new SubForm(Type.CSDI);
			CSDI.setForm(in.extract(size));
			soundPairs.add(new SoundPair(CSDI));
		    } else {
			throw new BadRecord("CSDI data length was not 4, as expected to be a formID");
		    }
		    break;
		case CSDC:
		    SubData CSDC = new SubData(Type.CSDC);
		    CSDC.setData(in.extract(size));
		    soundPairs.get(soundPairs.size() - 1).setChance(CSDC);
		    break;
		default:
		    throw new BadRecord("CSDT package does not know what to do with record of type: " + t);
	    }
	}
    }

    static class DNAM extends SubRecord implements Serializable {

	byte[] skills = new byte[36];
	int health = 1;
	int magicka = 1;
	int stamina = 1;
	byte[] fluff1 = new byte[2];
	float farAwayDistance = 0;
	byte[] fluff2 = new byte[4];

	DNAM() {
	    super(Type.DNAM);
	}

	DNAM(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new DNAM();
	}

	@Override
	public void clear() {
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 52;
	}

	int getSkillBase(Skill in) {
	    return skills[in.ordinal()];
	}

	int getSkillMod(Skill in) {
	    return skills[in.ordinal() + 18];
	}

	void setSkillBase(Skill in, int to) {
	    skills[in.ordinal()] = (byte) to;
	}

	void setSkillMod(Skill in, int to) {
	    skills[in.ordinal() + 18] = (byte) to;
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    skills = in.extract(36);
	    health = in.extractInt(2);
	    magicka = in.extractInt(2);
	    stamina = in.extractInt(2);
	    fluff1 = in.extract(2);
	    farAwayDistance = in.extractFloat();
	    fluff2 = in.extract(4);
	    if (logging()) {
		logSync("", "DNAM record: ");
		String temp;
		for (Skill s : Skill.values()) {
		    temp = " BASE:" + getSkillBase(s) + ", MOD:" + getSkillMod(s);
		    logSync("", "  " + s.toString() + Ln.spaceLeft(false, 15 - s.toString().length() + temp.length(), ' ', temp));
		}
		logSync("", "  " + "Health: " + health + ", Magicka: " + magicka + ", Stamina: " + stamina);
		logSync("", "  " + "Far Away Distance: " + farAwayDistance);
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(skills, 36);
	    out.write(health, 2);
	    out.write(magicka, 2);
	    out.write(stamina, 2);
	    out.write(fluff1, 2);
	    out.write(farAwayDistance);
	    out.write(fluff2, 4);
	}
    }

    static class TINIpackage extends SubRecord implements Serializable {

	SubData TINI = new SubData(Type.TINI);
	SubData TINC = new SubData(Type.TINC);
	SubData TINV = new SubData(Type.TINV);
	SubData TIAS = new SubData(Type.TIAS);
	private static Type[] types = {Type.TINI, Type.TINC, Type.TINV, Type.TIAS};

	TINIpackage() {
	    super(types);
	}

	TINIpackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    TINI.parseData(in);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException {
	    // Not calling super
	    Type t = Type.valueOf(Ln.arrayToString(in.extractInts(4)));
	    int size = Ln.arrayToInt(in.extractInts(2));
	    switch (t) {
		case TINI:
		    TINI.setData(in.extract(size));
		    break;
		case TINC:
		    TINC.setData(in.extract(size));
		    break;
		case TINV:
		    TINV.setData(in.extract(size));
		    break;
		case TIAS:
		    TIAS.setData(in.extract(size));
		    break;
		default:
		    throw new BadRecord("TINI package does not know what to do with record of type: " + t);
	    }
	}

	@Override
	SubRecord getNew(Type type_) {
	    return new TINIpackage();
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    if (isValid()) {
		return TINI.getTotalLength(srcMod)
			+ TINC.getTotalLength(srcMod)
			+ TINV.getTotalLength(srcMod)
			+ TIAS.getTotalLength(srcMod);
	    } else {
		return 0;
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    if (isValid()) {
		TINI.export(out, srcMod);
		TINC.export(out, srcMod);
		TINV.export(out, srcMod);
		TIAS.export(out, srcMod);
	    }
	}

	@Override
	public void clear() {
	    TINI.clear();
	    TINC.clear();
	    TINV.clear();
	    TIAS.clear();
	}

	@Override
	Boolean isValid() {
	    return TINI.isValid()
		    && TINC.isValid()
		    && TINV.isValid()
		    && TIAS.isValid();
	}
    }

    static class ACBS extends SubRecord implements Serializable {

	LFlags ACBSflags = new LFlags(4);
	int magickaOffset = 0;
	int fatigueOffset = 0;
	int level = 0;
	int minCalcLevel = 0;
	int maxCalcLevel = 0;
	int speed = 100;
	int dispositionBase = 0;
	LFlags templateFlags = new LFlags(2);
	int healthOffset = 0;
	int bleedout = 0;

	ACBS() {
	    super(Type.ACBS);
	}

	ACBS(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ACBS();
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    ACBSflags.set(in.extract(4));
	    magickaOffset = in.extractInt(2);
	    fatigueOffset = in.extractInt(2);
	    level = in.extractInt(2);
	    minCalcLevel = in.extractInt(2);
	    maxCalcLevel = in.extractInt(2);
	    speed = in.extractInt(2);
	    dispositionBase = in.extractInt(2);
	    templateFlags.set(in.extract(2));
	    healthOffset = in.extractInt(2);
	    bleedout = in.extractInt(2);
	    if (logging()) {
		logSync("", "ACBS record: ");
		logSync("", "  " + "Base Spell Points: " + magickaOffset + ", Base Fatigue: " + fatigueOffset);
		logSync("", "  " + "Level: " + level + ", Min Calculated Level: " + minCalcLevel + ", Max Calculated Level: " + maxCalcLevel);
		logSync("", "  " + "Speed Multiplier: " + speed + ", Disposition Base: " + dispositionBase);
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(ACBSflags.export(), 4);
	    out.write(magickaOffset, 2);
	    out.write(fatigueOffset, 2);
	    out.write(level, 2);
	    out.write(minCalcLevel, 2);
	    out.write(maxCalcLevel, 2);
	    out.write(speed, 2);
	    out.write(dispositionBase, 2);
	    out.write(templateFlags.export(), 2);
	    out.write(healthOffset, 2);
	    out.write(bleedout, 2);
	}

	@Override
	public void clear() {
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 24;
	}
    }

    static class AIDT extends SubRecord implements Serializable {

	int aggression = 0;
	int confidence = 0;
	int morality = 0;
	int assistance = 0;
	int aggroradius = 0;
	byte[] fluff1 = new byte[1];
	byte[] fluff2 = new byte[1];
	byte[] fluff3 = new byte[10];

	AIDT() {
	    super(Type.AIDT);
	}

	AIDT(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new AIDT();
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    aggression = in.extractInt(1);
	    confidence = in.extractInt(1);
	    fluff1 = in.extract(1);
	    morality = in.extractInt(1);
	    fluff2 = in.extract(1);
	    assistance = in.extractInt(1);
	    fluff3 = in.extract(10);
	    aggroradius = in.extractInt(4);
	    if (logging()) {
		logSync("", "AIDT record: ");
		logSync("", "  " + "Aggression: " + aggression + ", Confidence: " + confidence + ", Morality: " + morality);
		logSync("", "  " + "Assistance: " + assistance + ", Aggro Radius: " + aggroradius);
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(aggression, 1);
	    out.write(confidence, 1);
	    out.write(fluff1, 1);
	    out.write(morality, 1);
	    out.write(fluff2, 1);
	    out.write(assistance, 1);
	    out.write(fluff3, 10);
	    out.write(aggroradius);
	}

	@Override
	public void clear() {
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 20;
	}
    }

    /**
     * Enum representing the various AI data types for an NPC.
     */
    public enum AI_Data {

	/**
	 * Determines when and if the NPC will attack. 0: Unaggressive 1:
	 * Aggressive 2: Very Aggressive 3: Frenzied
	 */
	AGGRESSION,
	/**
	 * Determines the radius that the NPC will attack another actor, given
	 * the correct conditions.<br> <br> No known limit. Contact
	 * Leviathan1753 if you discover one.
	 */
	AGGRO_RADIUS,
	/**
	 * Determines who the NPC will assist in combat. 0: Helps nobody 1:
	 * Helps allies (within the same faction) 2: Helps friends and allies
	 * (everyone they don't hate)
	 */
	ASSISTANCE,
	/**
	 * Determines at what health level the NPC will flee. 0: Cowardly
	 * (always flees?) 1: Cautious 2: Average 3: Brave 4: Foolhardy (never
	 * flees?)
	 */
	CONFIDENCE,
	/**
	 * Determines if/when the NPC will report a crime. NPC will report: 0:
	 * Any crime 1: Violence 2: Property crime only 3: No crime
	 */
	MORALITY,}

    /**
     * Enum representing the various stats of the NPC
     */
    public enum NPCStat {

	/**
	 * Determines the initial disposition of the NPC to the player.
	 */
	DISPOSITION_BASE,
	/**
	 * Determines the fatigue of the NPC.<br> Not confirmed whether this is
	 * actually used.
	 */
	FATIGUE_BASE,
	/**
	 * Level of the NPC.
	 */
	LEVEL,
	/**
	 * Min level when calculated for PC offset.
	 */
	MIN_CALC_LEVEL,
	/**
	 * Max level when calculated for PC offset.
	 */
	MAX_CALC_LEVEL,
	/**
	 * Determines the speed of the NPC.
	 */
	SPEED_MULT,
	/**
	 * Determines the initial mana of the NPC.<br> Not confirmed whether
	 * this is actually used.
	 */
	SPELL_POINTS_BASE
    }

    /**
     * The template flags telling the NPC which parts to use from its target
     * template.
     */
    public enum TemplateFlag {

	/**
	 * Flag to use the traits page of its template.
	 */
	USE_TRAITS(0),
	USE_STATS(1),
	USE_FACTIONS(2),
	USE_SPELL_LIST(3),
	USE_AI_DATA(4),
	USE_AI_PACKAGES(5),
	USE_BASE_DATA(7),
	USE_INVENTORY(8),
	USE_SCRIPTS(9),
	USE_DEF_PACK_LIST(10),
	USE_ATTACK_DATA(11),
	USE_KEYWORDS(12);
	int value;

	TemplateFlag(int in) {
	    value = in;
	}
    }

    /**
     * Collection of various flags applied to the NPC (ACBS flags)
     */
    public enum NPCFlag {

	Female(0),
	/**
	 *
	 */
	Essential(1),
	/**
	 *
	 */
	IsCharGenFacePreset(2),
	/**
	 *
	 */
	Respawn(3),
	/**
	 *
	 */
	AutoCalcStats(4),
	Unique(5),
	/**
	 *
	 */
	PCLevelMult(7),
	Protected(13),
	/**
	 *
	 */
	Summonable(14),
	/**
	 *
	 */
	DoesntBleed(16),
	BleedoutOverride(18),
	OppositeGenderAnims(19),
	/**
	 *
	 */
	SimpleActor(20),
	/**
	 *
	 */
	DoesntAffectStealthMeter(21),
	/**
	 *
	 */
	IsGhost(29),
	/**
	 *
	 */
	Invulnerable(31);
	int value;

	NPCFlag(int value) {
	    this.value = value;
	}
    }

    // Special functions
    public void templateTo(NPC_ otherNPC, TemplateFlag... flags) {
	if (flags.length == 0) {
	    flags = TemplateFlag.values();
	}
	for (TemplateFlag f : flags) {
	    if (templateToInternal(otherNPC, f)) {
		set(f, false);
	    }
	}

	// If NPC no longer has any template flags on, remove template.
	boolean templated = false;
	for (TemplateFlag f : TemplateFlag.values()) {
	    if (get(f)) {
		templated = true;
		break;
	    }
	}
	if (!templated) {
	    setTemplate(FormID.NULL);
	}
    }

    boolean templateToInternal(NPC_ otherNPC, TemplateFlag flag) {
	if (otherNPC.get(flag)) {
	    NPC_ otherNPCsTemplate = (NPC_) SPDatabase.getMajor(otherNPC.getTemplate(), GRUP_TYPE.NPC_);
	    if (otherNPCsTemplate != null) {
		return templateToInternal(otherNPCsTemplate, flag);
	    } else {
		return false;
	    }
	} else {
	    switch (flag) {
		case USE_TRAITS:
		    set(NPCFlag.Female, otherNPC.get(NPCFlag.Female));
		    setRace(otherNPC.getRace());
		    setSkin(otherNPC.getSkin());
		    setHeight(otherNPC.getHeight());
		    setWeight(otherNPC.getWeight());
		    setFarAwayModelSkin(otherNPC.getFarAwayModelSkin());
		    setVoiceType(otherNPC.getVoiceType());
		    ACBS.dispositionBase = otherNPC.ACBS.dispositionBase;
		    setDeathItem(otherNPC.getDeathItem());
		    set(NPCFlag.OppositeGenderAnims, otherNPC.get(NPCFlag.OppositeGenderAnims));
		    break;
		case USE_STATS:
		    ACBS.level = otherNPC.ACBS.level;
		    set(NPCFlag.PCLevelMult, otherNPC.get(NPCFlag.PCLevelMult));
		    set(NPCStat.MIN_CALC_LEVEL, otherNPC.get(NPCStat.MIN_CALC_LEVEL));
		    set(NPCStat.MAX_CALC_LEVEL, otherNPC.get(NPCStat.MAX_CALC_LEVEL));
		    ACBS.healthOffset = otherNPC.ACBS.healthOffset;
		    ACBS.magickaOffset = otherNPC.ACBS.magickaOffset;
		    ACBS.fatigueOffset = otherNPC.ACBS.fatigueOffset;
		    for (Skill s : Skill.values()) {
			this.set(s, otherNPC.get(s));
			this.setMod(s, otherNPC.getMod(s));
		    }
		    this.ACBS.speed = otherNPC.ACBS.speed;
		    this.ACBS.bleedout = otherNPC.ACBS.bleedout;
		    this.setNPCClass(otherNPC.getNPCClass());
		    break;
		case USE_FACTIONS:
		    this.clearFactions();
		    for (SubFormInt s : otherNPC.getFactions()) {
			addFaction(s.getForm(), s.getNum());
		    }
		    this.setCrimeFaction(otherNPC.getCrimeFaction());
		    break;
		case USE_SPELL_LIST:
		    this.clearSpells();
		    for (FormID f : otherNPC.getSpells()) {
			addSpell(f);
		    }
		    break;
	    }
	    return true;
	}
    }

    // Get/Set methods
    /**
     *
     * @param flag Template flag to set.
     * @param on What to set the template flag to.
     */
    public void set(TemplateFlag flag, boolean on) {
	ACBS.templateFlags.set(flag.value, on);
    }

    /**
     *
     * @param flag Template flag to get.
     * @return Template flag's status.
     */
    public boolean get(TemplateFlag flag) {
	return ACBS.templateFlags.get(flag.value);
    }

    /**
     * Returns the group of factions assigned to the NPC. Changing this group by
     * adding or removing factions will affect that NPC.
     *
     * @see SubRecordList
     * @return The group of factions assigned to the NPC.
     */
    public ArrayList<SubFormInt> getFactions() {
	return SubList.subFormIntToPublic(factions);
    }

    /**
     *
     * @param factionRef FormID of the faction to add the NPC into.
     * @param rank Rank within the faction to set the NPC at.
     * @return True if faction was added.
     */
    public boolean addFaction(FormID factionRef, int rank) {
	return factions.add(new SubFormInt(Type.SNAM, factionRef, rank));
    }

    /**
     *
     * @param factionRef FormID matching the FactionRef record to remove.
     * @return True if faction was removed.
     */
    public boolean removeFaction(FormID factionRef) {
	return factions.remove(new SubFormInt(Type.SNAM, factionRef, 0));
    }

    public void clearFactions () {
	factions.clear();
    }

    /**
     *
     * @param flag NPCFlag to get.
     * @return NPCFlag's status.
     */
    public boolean get(NPCFlag flag) {
	return ACBS.ACBSflags.get(flag.value);
    }

    /**
     *
     * @param flag NPCFlag to set.
     * @param on What to set the NPCFlag to.
     */
    public void set(NPCFlag flag, boolean on) {
	ACBS.ACBSflags.set(flag.value, on);
    }

    /**
     * Returns the base value of the skill represented by the given enum.
     *
     * @see Skills
     * @param skill The enum of the skill to return the base value of.
     * @return The base value of the skill represented by the given enum.
     */
    public int get(Skill skill) {
	return DNAM.getSkillBase(skill);
    }

    /**
     * Sets the base value of the skill represented by the given enum.
     *
     * @see Skills
     * @param skill The enum of the skill to set to the value.
     * @param value Sets the base value of the skill to this value.
     * @throws BadParameter If value is < 0.
     */
    public void set(Skill skill, int value) {
	if (value < 0) {
	    value = 0;
	}
	DNAM.setSkillBase(skill, value);
    }

    /**
     * Returns the mod value of the skill represented by the given enum.
     *
     * @see Skills
     * @param skill The enum of the skill to return the mod value of.
     * @return The mod value of the skill represented by the given enum.
     */
    public int getMod(Skill skill) {
	return DNAM.getSkillMod(skill);
    }

    /**
     * Sets the mod value of the skill represented by the given enum.
     *
     * @see Skills
     * @param skill The enum of the skill to set to the value.
     * @param value Sets the mod value of the skill to this value.
     * @throws BadParameter If value is < 0.
     */
    public void setMod(Skill skill, int value) {
	if (value < 0) {
	    value = 0;
	}
	DNAM.setSkillMod(skill, value);
    }

    /**
     * Returns the value of the AI data represented by the given enum.
     *
     * @see AI_Data
     * @param aiType The enum of the AI data to return.
     * @return The value of the AI data represented by the given enum.
     */
    public int get(AI_Data aiType) {
	switch (aiType) {
	    case AGGRESSION:
		return AIDT.aggression;
	    case CONFIDENCE:
		return AIDT.confidence;
	    case MORALITY:
		return AIDT.morality;
	    case ASSISTANCE:
		return AIDT.assistance;
	    case AGGRO_RADIUS:
		return AIDT.aggroradius;
	    default:
		return -1;
	}
    }

    /**
     * Sets the value of the AI data represented by the given enum.
     *
     * @see AI_Data
     * @param aiType The enum of the AI data to set to the value.
     * @param value Sets the value of the AI data to this value.
     * @throws BadParameter If value is < 0, or outside the predefined range of
     * the specific AI data type.
     */
    public void set(AI_Data aiType, int value) throws BadParameter {
	if (value < 0) {
	    throw new BadParameter("Value must be > 0.");
	}
	switch (aiType) {
	    case AGGRESSION:
		if (value <= 3) {
		    AIDT.aggression = value;
		} else {
		    throw new BadParameter("Value for aggression must be 0 < x <= 3");
		}
	    case CONFIDENCE:
		if (value <= 4) {
		    AIDT.confidence = value;
		} else {
		    throw new BadParameter("Value for confidence must be 0 < x <= 4");
		}
	    case MORALITY:
		if (value <= 3) {
		    AIDT.morality = value;
		} else {
		    throw new BadParameter("Value for morality must be 0 < x <= 3");
		}
	    case ASSISTANCE:
		if (value <= 2) {
		    AIDT.assistance = value;
		} else {
		    throw new BadParameter("Value for assistance must be 0 < x <= 3");
		}
	    case AGGRO_RADIUS:
		AIDT.aggroradius = value;
	}
    }

    /**
     * Returns the value of the stat data represented by the given enum.
     *
     * @see Stat_Values
     * @param stat The enum of the stat data to return.
     * @return The value of the stat data represented by the given enum.
     */
    public int get(NPCStat stat) {
	switch (stat) {
	    case SPELL_POINTS_BASE:
		return ACBS.magickaOffset;
	    case FATIGUE_BASE:
		return ACBS.fatigueOffset;
	    case LEVEL:
		return ACBS.level;
	    case MIN_CALC_LEVEL:
		return ACBS.minCalcLevel;
	    case MAX_CALC_LEVEL:
		return ACBS.maxCalcLevel;
	    case SPEED_MULT:
		return ACBS.speed;
	    case DISPOSITION_BASE:
		return ACBS.dispositionBase;
	    default:
		return -1;
	}
    }

    /**
     * Sets the value of the stat data represented by the given enum.
     *
     * @see Stat_Values
     * @param stat The enum of the stat data to set to the value.
     * @param value Sets the value of the stat data to this value.
     * @throws BadParameter If value is < 0
     */
    public void set(NPCStat stat, int value) {
	if (value < 0) {
	    value = 0;
	}
	switch (stat) {
	    case SPELL_POINTS_BASE:
		ACBS.magickaOffset = value;
	    case FATIGUE_BASE:
		ACBS.fatigueOffset = value;
	    case LEVEL:
		ACBS.level = value;
	    case MIN_CALC_LEVEL:
		ACBS.minCalcLevel = value;
	    case MAX_CALC_LEVEL:
		ACBS.maxCalcLevel = value;
	    case SPEED_MULT:
		ACBS.speed = value;
	    case DISPOSITION_BASE:
		ACBS.dispositionBase = value;
	}
    }

    /**
     * The item to be added to the NPC's inventory upon death.
     *
     * @param deathItemRef
     */
    public void setDeathItem(FormID deathItemRef) {
	INAM.setForm(deathItemRef);
    }

    /**
     * The item to be added to the NPC's inventory upon death.
     *
     * @return
     */
    public FormID getDeathItem() {
	return INAM.getForm();
    }

    /**
     * The voice type of the NPC.
     *
     * @param voiceTypeRef
     */
    public void setVoiceType(FormID voiceTypeRef) {
	VTCK.setForm(voiceTypeRef);
    }

    /**
     * The voice type of the NPC.
     *
     * @return
     */
    public FormID getVoiceType() {
	return VTCK.getForm();
    }

    /**
     *
     * @param templateRef
     */
    public void setTemplate(FormID templateRef) {
	TPLT.setForm(templateRef);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
	return TPLT.getForm();
    }

    /**
     *
     * @param raceRef
     */
    public void setRace(FormID raceRef) {
	RNAM.setForm(raceRef);
    }

    /**
     *
     * @return
     */
    public FormID getRace() {
	return RNAM.getForm();
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getSpells() {
	return SubList.subFormToPublic(spells);
    }

    /**
     *
     * @param spellReference FormID of the spell to give to the NPC.
     * @return True if spell was added.
     */
    public boolean addSpell(FormID spellReference) {
	return spells.add(new SubForm(Type.SPLO, spellReference));
    }

    /**
     * Removes a spell from the NPC. If a spell with this FormID does not exist,
     * this spell does nothing.
     *
     * @param spellReference FormID of the spell to remove from the NPC
     * @return True if spell was removed.
     */
    public boolean removeSpell(FormID spellReference) {
	return spells.remove(new SubForm(Type.SPLO, spellReference));
    }

    public void clearSpells() {
	spells.clear();
    }

    /**
     *
     * @param itemReference
     * @param count
     * @return
     */
    public boolean addItem(FormID itemReference, int count) {
	return items.add(new SubFormInt(Type.CNTO, itemReference, count));
    }

    /**
     *
     * @param itemReference
     * @return
     */
    public boolean removeItem(FormID itemReference) {
	return items.remove(new SubFormInt(Type.CNTO, itemReference, 1));
    }

    /**
     *
     * @return
     */
    public ArrayList<SubFormInt> getItems() {
	return SubList.subFormIntToPublic(items);
    }

    /**
     * Adds an AI package with the FormID to the NPC.
     *
     * @param aiPackageReference
     * @return True if AI package was added.
     */
    public boolean addAIPackage(FormID aiPackageReference) {
	return aiPackages.add(new SubForm(Type.PKID, aiPackageReference));
    }

    /**
     *
     * @param aiPackageReference
     * @return True if AI package was removed.
     */
    public boolean removeAIPackage(FormID aiPackageReference) {
	return aiPackages.remove(new SubForm(Type.PKID, aiPackageReference));
    }

    /**
     *
     * @return SubRecordList of AI packages.
     */
    public ArrayList<FormID> getAIPackages() {
	return SubList.subFormToPublic(aiPackages);
    }

    /**
     *
     * @param classReference
     */
    public void setNPCClass(FormID classReference) {
	CNAM.setForm(classReference);
    }

    /**
     *
     * @return
     */
    public FormID getNPCClass() {
	return CNAM.getForm();
    }

    /**
     *
     * @param hairColorRef
     */
    public void setHairColor(FormID hairColorRef) {
	HCLF.setForm(hairColorRef);
    }

    /**
     *
     * @return
     */
    public FormID getHairColor() {
	return HCLF.getForm();
    }

    /**
     *
     * @param wornArmorRef
     */
    public void setSkin(FormID wornArmorRef) {
	WNAM.setForm(wornArmorRef);
    }

    /**
     *
     * @return
     */
    public FormID getSkin() {
	return WNAM.getForm();
    }

    /**
     *
     * @param attackRaceRef
     */
    public void setAttackRace(FormID attackRaceRef) {
	ATKR.setForm(attackRaceRef);
    }

    /**
     *
     * @return
     */
    public FormID getAttackRace() {
	return ATKR.getForm();
    }

    /**
     *
     * @param defaultOutfitRef
     */
    public void setDefaultOutfit(FormID defaultOutfitRef) {
	DOFT.setForm(defaultOutfitRef);
    }

    /**
     *
     * @return
     */
    public FormID getDefaultOutfit() {
	return DOFT.getForm();
    }

    /**
     *
     * @param sleepingOutfitRef
     */
    public void setSleepingOutfit(FormID sleepingOutfitRef) {
	SOFT.setForm(sleepingOutfitRef);
    }

    /**
     *
     * @return
     */
    public FormID getSleepingOutfit() {
	return SOFT.getForm();
    }

    /**
     *
     * @param combatRef
     */
    public void setCombatStyle(FormID combatRef) {
	ZNAM.setForm(combatRef);
    }

    /**
     *
     * @return
     */
    public FormID getCombatStyle() {
	return ZNAM.getForm();
    }

    /**
     *
     * @param crimeFactionRef
     */
    public void setCrimeFaction(FormID crimeFactionRef) {
	this.CRIF.setForm(crimeFactionRef);
    }

    /**
     *
     * @return
     */
    public FormID getCrimeFaction() {
	return CRIF.getForm();
    }

    /**
     *
     * @param headPartsRef
     */
    public void setFeatureSet(FormID headPartsRef) {
	FTST.setForm(headPartsRef);
    }

    /**
     *
     * @return
     */
    public FormID getFeatureSet() {
	return FTST.getForm();
    }

    /**
     *
     * @param audioTemplateRef
     */
    public void setAudioTemplate(FormID audioTemplateRef) {
	CSCR.setForm(audioTemplateRef);
    }

    /**
     *
     * @return
     */
    public FormID getAudioTemplate() {
	return CSCR.getForm();
    }

    /**
     *
     * @param ref
     */
    public void setDefaultPackageList(FormID ref) {
	DPLT.setForm(ref);
    }

    public void setHeight(float height) {
	NAM6.data = height;
    }

    public float getHeight() {
	return NAM6.data;
    }

    public void setWeight(float weight) {
	NAM7.data = weight;
    }

    public float getWeight() {
	return NAM7.data;
    }

    public void setFarAwayModelSkin(FormID id) {
	ANAM.setForm(id);
    }

    public FormID getFarAwayModelSkin() {
	return ANAM.getForm();
    }

    public void setFarAwayModelDistance(float dist) {
	DNAM.farAwayDistance = dist;
    }

    public float getFarAwayModelDistance() {
	return DNAM.farAwayDistance;
    }

    public void setHealthOffset(int value) {
	ACBS.healthOffset = value;
    }

    public int getHealthOffset() {
	return ACBS.healthOffset;
    }

    public void setMagickaOffset(int value) {
	ACBS.magickaOffset = value;
    }

    public int getMagickaOffset() {
	return ACBS.magickaOffset;
    }

    public void setFatigueOffset(int value) {
	ACBS.fatigueOffset = value;
    }

    public int getFatigueOffset() {
	return ACBS.fatigueOffset;
    }
}
