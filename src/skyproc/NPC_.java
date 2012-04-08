package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
    SubForm GWOR = new SubForm(Type.GWOR);
    SubForm OCOR = new SubForm(Type.OCOR);
    SubList<SubFormInt> perks = new SubList<SubFormInt>(Type.PRKZ, 4, new SubFormInt(Type.PRKR));
    SubList<SubFormInt> items = new SubList<SubFormInt>(Type.COCT, 4, new SubFormInt(Type.CNTO));
    AIDT AIDT = new AIDT();
    SubList<SubForm> aiPackages = new SubList<SubForm>(new SubForm(Type.PKID));
    SubForm CNAM = new SubForm(Type.CNAM);
    SubString SHRT = new SubString(Type.SHRT, true);
    SubData DATA = new SubData(Type.DATA);
    DNAM DNAM = new DNAM();
    SubList<SubForm> PNAMs = new SubList<SubForm>(new SubForm(Type.PNAM));
    SubForm HCLF = new SubForm(Type.HCLF);
    SubData NAM5 = new SubData(Type.NAM5);
    SubFloat NAM6 = new SubFloat(Type.NAM6);
    SubFloat NAM7 = new SubFloat(Type.NAM7);
    SubInt NAM8 = new SubInt(Type.NAM8);
    SubForm WNAM = new SubForm(Type.WNAM);
    SubForm ANAM = new SubForm(Type.ANAM);
    SubForm ATKR = new SubForm(Type.ATKR);
    ATKD ATKD = new ATKD();
    SubString ATKE = new SubString(Type.ATKE, true);
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
    SubForm GNAM = new SubForm(Type.GNAM);
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
	subRecords.add(GWOR);
	subRecords.add(OCOR);
	subRecords.add(ECOR);
	subRecords.add(perks);
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

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    if (deep) {
		ArrayList<FormID> out = new ArrayList<FormID>(soundPairs.size());
		for (SoundPair pairs : soundPairs) {
		    out.add(pairs.CSDI.ID);
		}
		return out;
	    } else {
		return new ArrayList<FormID>(0);
	    }
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
	int gearedUpWeapons = 1;
	byte[] fluff2 = new byte[3];

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
	    gearedUpWeapons = in.extractInt(1);
	    fluff2 = in.extract(3);
	    if (logging()) {
		logSync("", "DNAM record: ");
		String temp;
		for (Skill s : Skill.values()) {
		    temp = " BASE:" + getSkillBase(s) + ", MOD:" + getSkillMod(s);
		    logSync("", "  " + s.toString() + Ln.spaceLeft(false, 15 - s.toString().length() + temp.length(), ' ', temp));
		}
		logSync("", "  " + "Health: " + health + ", Magicka: " + magicka + ", Stamina: " + stamina);
		logSync("", "  " + "Far Away Distance: " + farAwayDistance + ", Geared Up weapons: " + gearedUpWeapons);
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
	    out.write(gearedUpWeapons, 1);
	    out.write(fluff2, 3);
	}

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    return new ArrayList<FormID>(0);
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

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    return new ArrayList<FormID>(0);
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

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    return new ArrayList<FormID>(0);
	}
    }

    static class AIDT extends SubRecord implements Serializable {

	Aggression aggression = Aggression.Unaggressive;
	Confidence confidence = Confidence.Cowardly;
	Morality morality = Morality.AnyCrime;
	Assistance assistance = Assistance.HelpsNobody;
	int energy = 0;
	Mood mood = Mood.Neutral;
	boolean aggroRadiusBehavior = false;
	byte[] fluff = new byte[1];
	int aggroWarn = 0;
	int aggroWarnAttack = 0;
	int aggroAttack = 0;

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
	    aggression = Aggression.values()[in.extractInt(1)];
	    confidence = Confidence.values()[in.extractInt(1)];
	    energy = in.extractInt(1);
	    morality = Morality.values()[in.extractInt(1)];
	    mood = Mood.values()[in.extractInt(1)];
	    assistance = Assistance.values()[in.extractInt(1)];
	    aggroRadiusBehavior = in.extractBool(1);
	    fluff = in.extract(1);
	    aggroWarn = in.extractInt(4);
	    aggroWarnAttack = in.extractInt(4);
	    aggroAttack = in.extractInt(4);
	    if (logging()) {
		logSync("", "AIDT record: ");
		logSync("", "  Aggression: " + aggression + ", Confidence: " + confidence + ", Morality: " + morality);
		logSync("", "  Assistance: " + assistance + ", Mood: " + mood + ", AggroRadiusBehavior: " + aggroRadiusBehavior);
		logSync("", "  Aggro Attack: " + aggroAttack + ", Aggro Warn: " + aggroWarn + ", Aggro Warn/Attack: " + aggroWarnAttack);
	    }
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(aggression.ordinal(), 1);
	    out.write(confidence.ordinal(), 1);
	    out.write(energy, 1);
	    out.write(morality.ordinal(), 1);
	    out.write(mood.ordinal(), 1);
	    out.write(assistance.ordinal(), 1);
	    out.write(aggroRadiusBehavior, 1);
	    out.write(fluff, 1);
	    out.write(aggroWarn);
	    out.write(aggroWarnAttack);
	    out.write(aggroAttack);
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

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    return new ArrayList<FormID>(0);
	}
    }

    static class ATKD extends SubRecord implements Serializable {

	float damageMult;
	float attackChance;
	FormID attackSpell = new FormID();
	LFlags flags = new LFlags(4);
	float attackAngle;
	float strikeAngle;
	float stagger;
	FormID attackType = new FormID();
	float knockdown;
	float recovery;
	float staminaMult;
	boolean valid = false;

	ATKD() {
	    super(Type.ATKD);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    if (isValid()) {
		out.write(damageMult);
		out.write(attackChance);
		attackSpell.export(out);
		out.write(flags.export());
		out.write(attackAngle);
		out.write(strikeAngle);
		out.write(stagger);
		attackType.export(out);
		out.write(knockdown);
		out.write(recovery);
		out.write(staminaMult);
	    }
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    damageMult = in.extractFloat();
	    attackChance = in.extractFloat();
	    attackSpell.setInternal(in.extract(4));
	    flags.set(in.extract(4));
	    attackAngle = in.extractFloat();
	    strikeAngle = in.extractFloat();
	    stagger = in.extractFloat();
	    attackType.setInternal(in.extract(4));
	    knockdown = in.extractFloat();
	    recovery = in.extractFloat();
	    staminaMult = in.extractFloat();
	    valid = true;
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new ATKD();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 44;
	}

	@Override
	ArrayList<FormID> allFormIDs (boolean deep) {
	    if (deep) {
		ArrayList<FormID> out = new ArrayList<FormID>(2);
		out.add(attackSpell);
		out.add(attackType);
		return out;
	    } else {
		return new ArrayList<FormID>(0);
	    }
	}
    }

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
	/**
	 *
	 */
	USE_STATS(1),
	/**
	 *
	 */
	USE_FACTIONS(2),
	/**
	 *
	 */
	USE_SPELL_LIST(3),
	/**
	 *
	 */
	USE_AI_DATA(4),
	/**
	 *
	 */
	USE_AI_PACKAGES(5),
	/**
	 *
	 */
	USE_BASE_DATA(7),
	/**
	 *
	 */
	USE_INVENTORY(8),
	/**
	 *
	 */
	USE_SCRIPTS(9),
	/**
	 *
	 */
	USE_DEF_PACK_LIST(10),
	/**
	 *
	 */
	USE_ATTACK_DATA(11),
	/**
	 *
	 */
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

	/**
	 *
	 */
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
	/**
	 *
	 */
	Unique(5),
	/**
	 *
	 */
	PCLevelMult(7),
	/**
	 *
	 */
	Protected(13),
	/**
	 *
	 */
	Summonable(14),
	/**
	 *
	 */
	DoesntBleed(16),
	/**
	 *
	 */
	BleedoutOverride(18),
	/**
	 *
	 */
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
	Invulnerable(31),
	/**
	 *
	 */
	AggroRadiusBehavior(-1);
	int value;

	NPCFlag(int value) {
	    this.value = value;
	}
    }

    /**
     *
     */
    public enum Aggression {

	/**
	 *
	 */
	Unaggressive,
	/**
	 *
	 */
	Aggressive,
	/**
	 *
	 */
	VeryAggressive,
	/**
	 *
	 */
	Frenzied;
    }

    /**
     *
     */
    public enum Assistance {

	/**
	 *
	 */
	HelpsNobody,
	/**
	 *
	 */
	HelpsAllies,
	/**
	 *
	 */
	HelpsFriends;
    }

    /**
     *
     */
    public enum Morality {

	/**
	 *
	 */
	AnyCrime,
	/**
	 *
	 */
	ViolenceAgainstEnemies,
	/**
	 *
	 */
	PropertyCrimeOnly,
	/**
	 *
	 */
	NoCrime;
    }

    /**
     *
     */
    public enum Confidence {

	/**
	 *
	 */
	Cowardly,
	/**
	 *
	 */
	Cautious,
	/**
	 *
	 */
	Average,
	/**
	 *
	 */
	Brave,
	/**
	 *
	 */
	Foolhardy;
    }

    /**
     *
     */
    public enum Mood {

	/**
	 *
	 */
	Neutral,
	/**
	 *
	 */
	Angry,
	/**
	 *
	 */
	Fear,
	/**
	 *
	 */
	Happy,
	/**
	 *
	 */
	Sad,
	/**
	 *
	 */
	Surprised,
	/**
	 *
	 */
	Puzzled,
	/**
	 *
	 */
	Disgusted,;
    }

    // Special functions
    /**
     * Takes in another NPC, and assumes all the information associated with the
     * input flags. It also unchecks the specific template flags on the
     * NPC.<br><br>
     *
     * If the parameter NPC is templated to another NPC, this function will
     * recursively call in order to get the "correct" template information. If
     * during this recursive call the function encounters a Leveled List on the
     * template chain, then the function will skip assuming that flag type, and
     * instead mark the flag on the NPC (if it wasn't already).<br><br>
     *
     * If no template flags remain checked after this function has run, then the
     * NPC's template reference is set to NULL.<br><br> This function makes a
     * deep copy of all templated info.
     *
     * @param otherNPC NPC to assume info from.
     * @param flags Types of information to assume. If none are given, then the
     * NPCs active flags will be assumed.
     */
    public void templateTo(NPC_ otherNPC, TemplateFlag... flags) {
	if (flags.length == 0) {
	    ArrayList<TemplateFlag> flagsList = new ArrayList<TemplateFlag>();
	    for (TemplateFlag f : TemplateFlag.values()) {
		if (get(f)) {
		    flagsList.add(f);
		}
	    }
	    flags = flagsList.toArray(flags);
	}
	NPC_ dup = (NPC_) Ln.deepCopy(otherNPC);
	for (TemplateFlag f : flags) {
	    if (templateToInternal(dup, f)) {
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

    public void templateTo(FormID npc, TemplateFlag... flags) {
	templateTo((NPC_) SPDatabase.getMajor(npc, GRUP_TYPE.NPC_), flags);
    }

    boolean templateToInternal(NPC_ otherNPC, TemplateFlag flag) {
	if (otherNPC == null) {
	    return false;
	}
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
		    //Sound Tab
		    this.setSoundVolume(otherNPC.getSoundVolume());
		    this.setAudioTemplate(otherNPC.getAudioTemplate());
		    this.soundPackages = otherNPC.soundPackages;
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
		    this.clearPerks();
		    for (SubFormInt s : otherNPC.perks) {
			addPerk(s.getForm(), s.getNum());
		    }
		    break;
		case USE_AI_DATA:
		    this.setAggression(otherNPC.getAggression());
		    this.setMood(otherNPC.getMood());
		    this.setConfidence(otherNPC.getConfidence());
		    this.setAssistance(otherNPC.getAssistance());
		    this.setMorality(otherNPC.getMorality());
		    this.setEnergy(otherNPC.getEnergy());
		    this.set(NPCFlag.AggroRadiusBehavior, otherNPC.get(NPCFlag.AggroRadiusBehavior));
		    this.setAggroWarn(otherNPC.getAggroWarn());
		    this.setAggroWarnAttack(otherNPC.getAggroWarnAttack());
		    this.setAggroAttack(otherNPC.getAggroAttack());
		    this.setCombatStyle(otherNPC.getCombatStyle());
		    this.setGiftFilter(otherNPC.getGiftFilter());
		    break;
		case USE_INVENTORY:
		    this.setDefaultOutfit(otherNPC.getDefaultOutfit());
		    this.setSleepingOutfit(otherNPC.getSleepingOutfit());
		    this.setGearedUpWeapons(otherNPC.getGearedUpWeapons());
		    this.clearItems();
		    for (SubFormInt f : otherNPC.getItems()) {
			this.addItem(f.getForm(), f.getNum());
		    }
		    break;
		case USE_AI_PACKAGES:
		    this.clearAIPackages();
		    for (SubForm id : otherNPC.aiPackages) {
			this.addAIPackage(id.getForm());
		    }
		    break;
		case USE_DEF_PACK_LIST:
		    this.setDefaultPackageList(otherNPC.getDefaultPackageList());
		    this.setSpectatorOverride(otherNPC.getSpectatorOverride());
		    this.setObserveDeadOverride(otherNPC.getObserveDeadOverride());
		    this.setGuardWornOverride(otherNPC.getGuardWornOverride());
		    this.setCombatOverride(otherNPC.getCombatOverride());
		    break;
		case USE_ATTACK_DATA:
		    this.setAttackDataRace(otherNPC.getAttackDataRace());
		    this.ATKD = otherNPC.ATKD;
		    this.ATKE.setString(otherNPC.ATKE.string);
		    break;
		case USE_BASE_DATA:
		    this.setName(otherNPC.getName());
		    this.setShortName(otherNPC.getShortName());
		    this.set(NPCFlag.Essential, otherNPC.get(NPCFlag.Essential));
		    this.set(NPCFlag.Protected, otherNPC.get(NPCFlag.Protected));
		    this.set(NPCFlag.Respawn, otherNPC.get(NPCFlag.Respawn));
		    this.set(NPCFlag.Summonable, otherNPC.get(NPCFlag.Summonable));
		    this.set(NPCFlag.SimpleActor, otherNPC.get(NPCFlag.SimpleActor));
		    this.set(NPCFlag.DoesntAffectStealthMeter, otherNPC.get(NPCFlag.DoesntAffectStealthMeter));
		    break;
		case USE_KEYWORDS:
		    this.keywords = otherNPC.keywords;
		    break;

	    }
	    return true;
	}
    }

    /**
     * Checks the NPC's template chain to see if a Leveled List is on it, and
     * returns it if found.<br><br> Flags can be specified if you only want to
     * return a Leveled List if AT LEAST one of those flags is checked.
     *
     * @param templateFlagsToCheck Flags to consider. If none are given, then
     * all considered.
     * @return Leveled List on the template chain, if the NPC has one of the
     * flags, and a Leveled List exists on its template chain.
     */
    public LVLN isTemplatedToLList(NPC_.TemplateFlag... templateFlagsToCheck) {
	return NiftyFunc.isTemplatedToLList(getForm(), templateFlagsToCheck, 0);
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

    /**
     *
     */
    public void clearFactions() {
	factions.clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<SubFormInt> getPerks() {
	return SubList.subFormIntToPublic(perks);
    }

    /**
     *
     * @param perkRef
     * @param rank
     */
    public void addPerk(FormID perkRef, int rank) {
	perks.add(new SubFormInt(Type.PRKR, perkRef, rank));
    }

    /**
     *
     * @param perkRef
     * @return
     */
    public boolean removePerk(FormID perkRef) {
	return perks.remove(new SubFormInt(Type.PRKR, perkRef, 0));
    }

    /**
     *
     */
    public void clearPerks() {
	perks.clear();
    }

    /**
     *
     * @param flag NPCFlag to get.
     * @return NPCFlag's status.
     */
    public boolean get(NPCFlag flag) {
	switch (flag) {
	    case AggroRadiusBehavior:
		return AIDT.aggroRadiusBehavior;
	    default:
		return ACBS.ACBSflags.get(flag.value);
	}
    }

    /**
     *
     * @param flag NPCFlag to set.
     * @param on What to set the NPCFlag to.
     */
    public void set(NPCFlag flag, boolean on) {
	switch (flag) {
	    case AggroRadiusBehavior:
		AIDT.aggroRadiusBehavior = on;
		break;
	    default:
		ACBS.ACBSflags.set(flag.value, on);
	}
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
     */
    public void setMod(Skill skill, int value) {
	if (value < 0) {
	    value = 0;
	}
	DNAM.setSkillMod(skill, value);
    }

    /**
     *
     * @param level
     */
    public void setAggression(Aggression level) {
	AIDT.aggression = level;
    }

    /**
     *
     * @return
     */
    public Aggression getAggression() {
	return AIDT.aggression;
    }

    /**
     *
     * @param level
     */
    public void setConfidence(Confidence level) {
	AIDT.confidence = level;
    }

    /**
     *
     * @return
     */
    public Confidence getConfidence() {
	return AIDT.confidence;
    }

    /**
     *
     * @param level
     */
    public void setMorality(Morality level) {
	AIDT.morality = level;
    }

    /**
     *
     * @return
     */
    public Morality getMorality() {
	return AIDT.morality;
    }

    /**
     *
     * @param level
     */
    public void setAssistance(Assistance level) {
	AIDT.assistance = level;
    }

    /**
     *
     * @return
     */
    public Assistance getAssistance() {
	return AIDT.assistance;
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

    /**
     *
     */
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

    public void clearItems() {
	items.clear();
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

    public void clearAIPackages() {
	aiPackages.clear();
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
    public void setAttackDataRace(FormID attackRaceRef) {
	ATKR.setForm(attackRaceRef);
    }

    /**
     *
     * @return
     */
    public FormID getAttackDataRace() {
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
     * @param list
     */
    public void setDefaultPackageList(FormID list) {
	DPLT.setForm(list);
    }

    public FormID getDefaultPackageList() {
	return DPLT.getForm();
    }

    /**
     *
     * @param height
     */
    public void setHeight(float height) {
	NAM6.data = height;
    }

    /**
     *
     * @return
     */
    public float getHeight() {
	return NAM6.data;
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	NAM7.data = weight;
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return NAM7.data;
    }

    /**
     *
     * @param id
     */
    public void setFarAwayModelSkin(FormID id) {
	ANAM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getFarAwayModelSkin() {
	return ANAM.getForm();
    }

    /**
     *
     * @param dist
     */
    public void setFarAwayModelDistance(float dist) {
	DNAM.farAwayDistance = dist;
    }

    /**
     *
     * @return
     */
    public float getFarAwayModelDistance() {
	return DNAM.farAwayDistance;
    }

    /**
     *
     * @param value
     */
    public void setHealthOffset(int value) {
	ACBS.healthOffset = value;
    }

    /**
     *
     * @return
     */
    public int getHealthOffset() {
	return ACBS.healthOffset;
    }

    /**
     *
     * @param value
     */
    public void setMagickaOffset(int value) {
	ACBS.magickaOffset = value;
    }

    /**
     *
     * @return
     */
    public int getMagickaOffset() {
	return ACBS.magickaOffset;
    }

    /**
     *
     * @param value
     */
    public void setFatigueOffset(int value) {
	ACBS.fatigueOffset = value;
    }

    /**
     *
     * @return
     */
    public int getFatigueOffset() {
	return ACBS.fatigueOffset;
    }

    /**
     *
     * @param value
     */
    public void setMood(Mood value) {
	AIDT.mood = value;
    }

    /**
     *
     * @return
     */
    public Mood getMood() {
	return AIDT.mood;
    }

    /**
     *
     * @param energy
     */
    public void setEnergy(int energy) {
	AIDT.energy = energy;
    }

    /**
     *
     * @return
     */
    public int getEnergy() {
	return AIDT.energy;
    }

    /**
     *
     * @param aggro
     */
    public void setAggroWarn(int aggro) {
	AIDT.aggroWarn = aggro;
    }

    /**
     *
     * @return
     */
    public int getAggroWarn() {
	return AIDT.aggroWarn;
    }

    /**
     *
     * @param aggro
     */
    public void setAggroWarnAttack(int aggro) {
	AIDT.aggroWarnAttack = aggro;
    }

    /**
     *
     * @return
     */
    public int getAggroWarnAttack() {
	return AIDT.aggroWarnAttack;
    }

    /**
     *
     * @param aggro
     */
    public void setAggroAttack(int aggro) {
	AIDT.aggroAttack = aggro;
    }

    /**
     *
     * @return
     */
    public int getAggroAttack() {
	return AIDT.aggroAttack;
    }

    /**
     *
     * @param id
     */
    public void setGiftFilter(FormID id) {
	GNAM.setForm(id);
    }

    /**
     *
     * @return
     */
    public FormID getGiftFilter() {
	return GNAM.getForm();
    }

    public void setGearedUpWeapons(int value) {
	DNAM.gearedUpWeapons = value;
    }

    public int getGearedUpWeapons() {
	return DNAM.gearedUpWeapons;
    }

    public void setSpectatorOverride(FormID list) {
	SPOR.setForm(list);
    }

    public FormID getSpectatorOverride() {
	return SPOR.getForm();
    }

    public void setObserveDeadOverride(FormID list) {
	OCOR.setForm(list);
    }

    public FormID getObserveDeadOverride() {
	return OCOR.getForm();
    }

    public void setGuardWornOverride(FormID list) {
	GWOR.setForm(list);
    }

    public FormID getGuardWornOverride() {
	return GWOR.getForm();
    }

    public void setCombatOverride(FormID list) {
	ECOR.setForm(list);
    }

    public FormID getCombatOverride() {
	return ECOR.getForm();
    }

    public void setShortName(String alias) {
	SHRT.setString(alias);
    }

    public String getShortName() {
	return SHRT.print();
    }

    public void setSoundVolume(SoundVolume vol) {
	NAM8.data = vol.ordinal();
    }

    public SoundVolume getSoundVolume() {
	return SoundVolume.values()[NAM8.data];
    }
}
