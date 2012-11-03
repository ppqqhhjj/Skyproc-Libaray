package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.DataFormatException;
import lev.*;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * An actor in the world.
 *
 * @author Justin Swanson
 */
public class NPC_ extends Actor implements Serializable {

    static final SubRecordsPrototype NPC_proto = new SubRecordsPrototype(MajorRecordNamed.namedProto) {
	@Override
	protected void addRecords() {
	    after(new ScriptPackage(), Type.EDID);
	    add(new SubData(Type.OBND));
	    add(new ACBS());
	    add(new SubList<>(new SubFormInt(Type.SNAM)));
	    add(new SubForm(Type.INAM));
	    add(new SubForm(Type.VTCK));
	    add(new SubForm(Type.TPLT));
	    add(new SubForm(Type.RNAM));
	    SubList spells = new SubList<>(Type.SPCT, 4, new SubForm(Type.SPLO));
	    spells.allowDuplicates(false);
	    add(spells);
	    add(new SubData(Type.DEST));
	    add(new SubData(Type.DSTD));
	    add(new SubData(Type.DSTF));
	    add(new SubForm(Type.WNAM));
	    add(new SubForm(Type.ANAM));
	    add(new SubForm(Type.ATKR));
	    add(new ATKD());
	    add(new SubString(Type.ATKE, true));
	    add(new SubForm(Type.SPOR));
	    add(new SubForm(Type.GWOR));
	    add(new SubForm(Type.OCOR));
	    add(new SubForm(Type.ECOR));
	    add(new SubList<>(Type.PRKZ, 4, new SubFormInt(Type.PRKR)));
	    add(new SubList<>(Type.COCT, 4, new SubFormInt(Type.CNTO)));
	    add(new AIDT());
	    add(new SubList<>(new SubForm(Type.PKID)));
	    add(new KeywordSet());
	    add(new SubForm(Type.CNAM));
	    reposition(Type.FULL);
	    add(new SubString(Type.SHRT, true));
	    SubData data = new SubData(Type.DATA);
	    data.forceExport = true;
	    add(data);
	    add(new DNAM());
	    add(new SubList<>(new SubForm(Type.PNAM)));
	    add(new SubForm(Type.HCLF));
	    add(new SubForm(Type.ZNAM));
	    add(new SubForm(Type.GNAM));
	    add(new SubData(Type.NAM5));
	    add(new SubFloat(Type.NAM6));
	    add(new SubFloat(Type.NAM7));
	    add(new SubInt(Type.NAM8));
	    add(new SubForm(Type.CSCR));
	    add(new SubList<>(new SoundPackage()));
	    add(new SubForm(Type.DOFT));
	    add(new SubForm(Type.SOFT));
	    add(new SubForm(Type.DPLT));
	    add(new SubForm(Type.CRIF));
	    add(new SubForm(Type.FTST));
	    add(new SubRGB(Type.QNAM));
	    add(new NAM9());
	    add(new NAMA());
	    add(new SubList<>(new TintLayer()));
	}
    };
    private final static Type[] type = {Type.NPC_};

    NPC_() {
	super();
	subRecords.prototype = NPC_proto;
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new NPC_();
    }

    /**
     * Sound package containing sounds to play for different actions
     */
    public static class SoundPackage extends SubRecord implements Serializable {

	SubInt CSDT = new SubInt(Type.CSDT);
	ArrayList<SoundPair> soundPairs = new ArrayList<>();
	private static Type[] types = {Type.CSDT, Type.CSDI, Type.CSDC};

	SoundPackage(SoundLocation location) {
	    this();
	    setLocation(location);
	}

	SoundPackage() {
	    super(types);
	}

	SoundPackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	Boolean isValid() {
	    boolean valid = CSDT.isValid();
	    if (valid) {
		for (SoundPair s : soundPairs) {
		    valid = valid && s.CSDC.isValid();
		}
	    }
	    return valid;
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<FormID>(soundPairs.size());
	    for (SoundPair pairs : soundPairs) {
		out.add(pairs.CSDI.ID);
	    }
	    return out;
	}

	@Override
	SubRecord getNew(Type type_) {
	    return new SoundPackage();
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
	    CSDT.export(out, srcMod);
	    for (SoundPair pair : soundPairs) {
		pair.CSDI.export(out, srcMod);
		pair.CSDC.export(out, srcMod);
	    }
	}

	@Override
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    // Not calling super
	    Type t = Type.valueOf(Ln.arrayToString(in.extractInts(4)));
	    int size = Ln.arrayToInt(in.extractInts(2));
	    switch (t) {
		case CSDT:
		    CSDT.set(in.extractInt(size));
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
		    SubInt CSDC = new SubInt(Type.CSDC, 1);
		    CSDC.set(in.extractInt(size));
		    soundPairs.get(soundPairs.size() - 1).setChance(CSDC);
		    break;
		default:
		    throw new BadRecord("CSDT package does not know what to do with record of type: " + t);
	    }
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) {
		return true;
	    }
	    if (o == null) {
		return false;
	    }
	    if (!(o instanceof SoundPackage)) {
		return false;
	    }
	    SoundPackage cs = (SoundPackage) o; // Convert the object to a Person
	    return (this.CSDT.equals(cs.CSDT) && this.soundPairs.equals(cs.soundPairs));
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 97 * hash + Objects.hashCode(this.CSDT);
	    hash = 97 * hash + Objects.hashCode(this.soundPairs);
	    return hash;
	}

	/**
	 *
	 * @param loc
	 */
	public final void setLocation(SoundLocation loc) {
	    CSDT.set(loc.ordinal());
	}

	/**
	 *
	 * @return
	 */
	public SoundLocation getLocation() {
	    if (CSDT.get() < SoundLocation.values().length) {
		return SoundLocation.values()[CSDT.get()];
	    } else {
		return SoundLocation.Idle;
	    }
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<SoundPair> getSoundPairs() {
	    return soundPairs;
	}

	/**
	 *
	 * @param pair
	 */
	public void addSoundPair(SoundPair pair) {
	    soundPairs.add(pair);
	}
    }

    /**
     * Pair containing sound to play and chance to play it
     */
    public static class SoundPair implements Serializable {

	SubForm CSDI;
	SubInt CSDC = new SubInt(Type.CSDC);

	/**
	 *
	 * @param sound
	 * @param chance
	 */
	public SoundPair(FormID sound, int chance) {
	    setChance(chance);
	    setSound(sound);
	}

	SoundPair(SubForm csdi) {
	    CSDI = csdi;
	}

	void setChance(SubInt csdc) {
	    CSDC = csdc;
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) {
		return true;
	    }
	    if (o == null) {
		return false;
	    }
	    if (!(o instanceof SoundPair)) {
		return false;
	    }
	    SoundPair cs = (SoundPair) o; // Convert the object to a Person
	    return (this.CSDC.equals(cs.CSDC) && this.CSDI.equals(cs.CSDI));
	}

	@Override
	public int hashCode() {
	    int hash = 7;
	    hash = 89 * hash + Objects.hashCode(this.CSDI);
	    hash = 89 * hash + Objects.hashCode(this.CSDC);
	    return hash;
	}

	/**
	 *
	 * @param chance
	 */
	public final void setChance(int chance) {
	    if (chance < 0) {
		chance = 0;
	    } else if (chance > 100) {
		chance = 100;
	    }

	    CSDC.set(chance);
	}

	/**
	 *
	 * @return
	 */
	public int getChance() {
	    return CSDC.get();
	}

	/**
	 *
	 * @param sound
	 */
	public final void setSound(FormID sound) {
	    CSDI.setForm(sound);
	}

	/**
	 *
	 * @return
	 */
	public FormID getSound() {
	    return CSDI.getForm();
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
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
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
    }

    /**
     *
     */
    public static class TintLayer extends SubShell implements Serializable {

	SubInt TINI = new SubInt(Type.TINI, 2);
	SubRGBshort TINC = new SubRGBshort(Type.TINC);
	SubFloat TINV = new SubFloat(Type.TINV);
	SubInt TIAS = new SubInt(Type.TIAS, 2);
	private static Type[] types = {Type.TINI, Type.TINC, Type.TINV, Type.TIAS};

	TintLayer() {
	    super(types);
	    subRecords.add(TINI);
	    subRecords.add(TINC);
	    subRecords.add(TINV);
	    subRecords.add(TIAS);
	}

	@Override
	SubRecord getNew(Type type_) {
	    return new TintLayer();
	}

	@Override
	int getHeaderLength() {
	    return 0;
	}

	public void setIndex(int in) {
	    TINI.set(in);
	}

	public int getIndex() {
	    return TINI.get();
	}

	public void setColor(RGBA color, short value) {
	    TINC.set(color, value);
	}

	public short getColor(RGBA color) {
	    return TINC.get(color);
	}

	public void setInterpolation(float value) {
	    TINV.set(value);
	}

	public float getInterpolation() {
	    return TINV.get();
	}

	public void setPreset (int value) {
	    TIAS.set(value);
	}

	public int getPreset() {
	    return TIAS.get();
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
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
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
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 24;
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
	final void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
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
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 20;
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
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
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
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(2);
	    out.add(attackSpell);
	    out.add(attackType);
	    return out;
	}
    }

    static class NAM9 extends SubRecord implements Serializable {

	float noseLong = 0;
	float noseUp = 0;
	float jawUp = 0;
	float jawWide = 0;
	float jawForward = 0;
	float cheekUp = 0;
	float cheekForward = 0;
	float eyeUp = 0;
	float eyeIn = 0;
	float browUp = 0;
	float browIn = 0;
	float browForward = 0;
	float lipUp = 0;
	float lipIn = 0;
	float chinWide = 0;
	float chinUp = 0;
	float chinOverbite = 0;
	float eyesForward = 0;
	byte[] unknown = new byte[4];
	boolean valid = false;

	NAM9() {
	    super(Type.NAM9);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(noseLong);
	    out.write(noseUp);
	    out.write(jawUp);
	    out.write(jawWide);
	    out.write(jawForward);
	    out.write(cheekUp);
	    out.write(cheekForward);
	    out.write(eyeUp);
	    out.write(eyeIn);
	    out.write(browUp);
	    out.write(browIn);
	    out.write(browForward);
	    out.write(lipUp);
	    out.write(lipIn);
	    out.write(chinWide);
	    out.write(chinUp);
	    out.write(chinOverbite);
	    out.write(eyesForward);
	    out.write(unknown, 4);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    noseLong = in.extractFloat();
	    noseUp = in.extractFloat();
	    jawUp = in.extractFloat();
	    jawWide = in.extractFloat();
	    jawForward = in.extractFloat();
	    cheekUp = in.extractFloat();
	    cheekForward = in.extractFloat();
	    eyeUp = in.extractFloat();
	    eyeIn = in.extractFloat();
	    browUp = in.extractFloat();
	    browIn = in.extractFloat();
	    browForward = in.extractFloat();
	    lipUp = in.extractFloat();
	    lipIn = in.extractFloat();
	    chinWide = in.extractFloat();
	    chinUp = in.extractFloat();
	    chinOverbite = in.extractFloat();
	    eyesForward = in.extractFloat();
	    unknown = in.extract(4);
	    valid = true;
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new NAM9();
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 76;
	}
    }

    static class NAMA extends SubRecord implements Serializable {

	int nose;
	int unknown;
	int eyes;
	int mouth;
	boolean valid = false;

	NAMA() {
	    super(Type.NAMA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(nose);
	    out.write(unknown);
	    out.write(eyes);
	    out.write(mouth);
	}

	@Override
	void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    nose = in.extractInt(4);
	    unknown = in.extractInt(4);
	    eyes = in.extractInt(4);
	    mouth = in.extractInt(4);
	    valid = true;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new NAMA();
	}

	@Override
	Boolean isValid() {
	    return valid;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 16;
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
     *
     */
    public enum SoundLocation {

	/**
	 *
	 */
	Idle,
	/**
	 *
	 */
	Aware,
	/**
	 *
	 */
	Attack,
	/**
	 *
	 */
	Hit,
	/**
	 *
	 */
	Death,
	/**
	 *
	 */
	Weapon,
	/**
	 *
	 */
	MovementLoop,
	/**
	 *
	 */
	ConsciousLoop;
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

    /**
     *
     */
    public enum FacePart {

	/**
	 *
	 */
	NoseLongShort,
	/**
	 *
	 */
	NoseUpDown,
	/**
	 *
	 */
	JawUpDown,
	/**
	 *
	 */
	JawNarrowWide,
	/**
	 *
	 */
	JawForwardBack,
	/**
	 *
	 */
	CheeksUpDown,
	/**
	 *
	 */
	CheeksForwardBack,
	/**
	 *
	 */
	EyesUpDown,
	/**
	 *
	 */
	EyesInOut,
	/**
	 *
	 */
	BrowsUpDown,
	/**
	 *
	 */
	BrowsInOut,
	/**
	 *
	 */
	BrowsForwardBack,
	/**
	 *
	 */
	LipsUpDown,
	/**
	 *
	 */
	LipsInOut,
	/**
	 *
	 */
	ChinThinWide,
	/**
	 *
	 */
	ChinUpDown,
	/**
	 *
	 */
	ChinOverbite,
	/**
	 *
	 */
	EyesForwardBack;
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
     * @param otherNPC NPC FormID to assume info from.
     * @param flags Types of information to assume. If none are given, then the
     * NPCs active flags will be assumed.
     */
    public void templateTo(NPC_ otherNPC, TemplateFlag... flags) {
	if (flags.length == 0) {
	    ArrayList<TemplateFlag> flagsList = new ArrayList<>();
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
     * @param npc
     * @param flags Types of information to assume. If none are given, then the
     * NPCs active flags will be assumed.
     */
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
		    getACBS().dispositionBase = otherNPC.getACBS().dispositionBase;
		    setDeathItem(otherNPC.getDeathItem());
		    set(NPCFlag.OppositeGenderAnims, otherNPC.get(NPCFlag.OppositeGenderAnims));
		    //Sound Tab
		    this.setSoundVolume(otherNPC.getSoundVolume());
		    this.setAudioTemplate(otherNPC.getAudioTemplate());
		    subRecords.add(otherNPC.subRecords.getSubList(Type.CSDT));
		    break;
		case USE_STATS:
		    getACBS().level = otherNPC.getACBS().level;
		    set(NPCFlag.PCLevelMult, otherNPC.get(NPCFlag.PCLevelMult));
		    set(NPCStat.MIN_CALC_LEVEL, otherNPC.get(NPCStat.MIN_CALC_LEVEL));
		    set(NPCStat.MAX_CALC_LEVEL, otherNPC.get(NPCStat.MAX_CALC_LEVEL));
		    getACBS().healthOffset = otherNPC.getACBS().healthOffset;
		    getACBS().magickaOffset = otherNPC.getACBS().magickaOffset;
		    getACBS().fatigueOffset = otherNPC.getACBS().fatigueOffset;
		    for (Skill s : Skill.values()) {
			this.set(s, otherNPC.get(s));
			this.setMod(s, otherNPC.getMod(s));
		    }
		    getACBS().speed = otherNPC.getACBS().speed;
		    getACBS().bleedout = otherNPC.getACBS().bleedout;
		    setNPCClass(otherNPC.getNPCClass());
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
		    for (SubFormInt s : otherNPC.getPerks()) {
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
		    for (FormID id : otherNPC.getAIPackages()) {
			this.addAIPackage(id);
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
		    setATKD(otherNPC.getATKD());
		    setATKE(otherNPC.getATKE());
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
		    subRecords.add(otherNPC.getKeywordSet());
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
    ACBS getACBS() {
	return (ACBS) subRecords.get(Type.ACBS);
    }

    public ScriptPackage getScriptPackage() {
	return subRecords.getScripts();
    }

    /**
     *
     * @param flag Template flag to set.
     * @param on What to set the template flag to.
     */
    public void set(TemplateFlag flag, boolean on) {
	getACBS().templateFlags.set(flag.value, on);
    }

    /**
     *
     * @param flag Template flag to get.
     * @return Template flag's status.
     */
    public boolean get(TemplateFlag flag) {
	return getACBS().templateFlags.get(flag.value);
    }

    /**
     * Returns the group of factions assigned to the NPC. Changing this group by
     * adding or removing factions will affect that NPC.
     *
     * @see SubRecordList
     * @return The group of factions assigned to the NPC.
     */
    public ArrayList<SubFormInt> getFactions() {
	return SubList.subFormIntToPublic(subRecords.getSubList(Type.SNAM));
    }

    /**
     *
     * @param factionRef FormID of the faction to add the NPC into.
     * @param rank Rank within the faction to set the NPC at.
     * @return True if faction was added.
     */
    public boolean addFaction(FormID factionRef, int rank) {
	return subRecords.getSubList(Type.SNAM).add(new SubFormInt(Type.SNAM, factionRef, rank));
    }

    /**
     *
     * @param factionRef FormID matching the FactionRef record to remove.
     * @return True if faction was removed.
     */
    public boolean removeFaction(FormID factionRef) {
	return subRecords.getSubList(Type.SNAM).remove(new SubFormInt(Type.SNAM, factionRef, 0));
    }

    /**
     *
     */
    public void clearFactions() {
	subRecords.getSubList(Type.SNAM).clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<SubFormInt> getPerks() {
	return SubList.subFormIntToPublic(subRecords.getSubList(Type.PRKR));
    }

    /**
     *
     * @param perkRef
     * @param rank
     */
    public void addPerk(FormID perkRef, int rank) {
	subRecords.getSubList(Type.PRKR).add(new SubFormInt(Type.PRKR, perkRef, rank));
    }

    /**
     *
     * @param perkRef
     * @return
     */
    public boolean removePerk(FormID perkRef) {
	return subRecords.getSubList(Type.PRKR).remove(new SubFormInt(Type.PRKR, perkRef, 0));
    }

    /**
     *
     */
    public void clearPerks() {
	subRecords.getSubList(Type.PRKR).clear();
    }

    AIDT getAIDT() {
	return (AIDT) subRecords.get(Type.AIDT);
    }

    /**
     *
     * @param flag NPCFlag to get.
     * @return NPCFlag's status.
     */
    public boolean get(NPCFlag flag) {
	switch (flag) {
	    case AggroRadiusBehavior:
		return getAIDT().aggroRadiusBehavior;
	    default:
		return getACBS().ACBSflags.get(flag.value);
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
		getAIDT().aggroRadiusBehavior = on;
		break;
	    default:
		getACBS().ACBSflags.set(flag.value, on);
	}
    }

    DNAM getDNAM() {
	return (DNAM) subRecords.get(Type.DNAM);
    }

    /**
     * Returns the base value of the skill represented by the given enum.
     *
     * @see Skills
     * @param skill The enum of the skill to return the base value of.
     * @return The base value of the skill represented by the given enum.
     */
    public int get(Skill skill) {
	return getDNAM().getSkillBase(skill);
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
	getDNAM().setSkillBase(skill, value);
    }

    /**
     * Returns the mod value of the skill represented by the given enum.
     *
     * @see Skills
     * @param skill The enum of the skill to return the mod value of.
     * @return The mod value of the skill represented by the given enum.
     */
    public int getMod(Skill skill) {
	return getDNAM().getSkillMod(skill);
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
	getDNAM().setSkillMod(skill, value);
    }

    /**
     *
     * @param level
     */
    public void setAggression(Aggression level) {
	getAIDT().aggression = level;
    }

    /**
     *
     * @return
     */
    public Aggression getAggression() {
	return getAIDT().aggression;
    }

    /**
     *
     * @param level
     */
    public void setConfidence(Confidence level) {
	getAIDT().confidence = level;
    }

    /**
     *
     * @return
     */
    public Confidence getConfidence() {
	return getAIDT().confidence;
    }

    /**
     *
     * @param level
     */
    public void setMorality(Morality level) {
	getAIDT().morality = level;
    }

    /**
     *
     * @return
     */
    public Morality getMorality() {
	return getAIDT().morality;
    }

    /**
     *
     * @param level
     */
    public void setAssistance(Assistance level) {
	getAIDT().assistance = level;
    }

    /**
     *
     * @return
     */
    public Assistance getAssistance() {
	return getAIDT().assistance;
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
		return getACBS().magickaOffset;
	    case FATIGUE_BASE:
		return getACBS().fatigueOffset;
	    case LEVEL:
		return getACBS().level;
	    case MIN_CALC_LEVEL:
		return getACBS().minCalcLevel;
	    case MAX_CALC_LEVEL:
		return getACBS().maxCalcLevel;
	    case SPEED_MULT:
		return getACBS().speed;
	    case DISPOSITION_BASE:
		return getACBS().dispositionBase;
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
		getACBS().magickaOffset = value;
		break;
	    case FATIGUE_BASE:
		getACBS().fatigueOffset = value;
		break;
	    case LEVEL:
		getACBS().level = value;
		break;
	    case MIN_CALC_LEVEL:
		getACBS().minCalcLevel = value;
		break;
	    case MAX_CALC_LEVEL:
		getACBS().maxCalcLevel = value;
		break;
	    case SPEED_MULT:
		getACBS().speed = value;
		break;
	    case DISPOSITION_BASE:
		getACBS().dispositionBase = value;
		break;
	}
    }

    /**
     * The item to be added to the NPC's inventory upon death.
     *
     * @param deathItemRef
     */
    public void setDeathItem(FormID deathItemRef) {
	subRecords.setSubForm(Type.INAM, deathItemRef);
    }

    /**
     * The item to be added to the NPC's inventory upon death.
     *
     * @return
     */
    public FormID getDeathItem() {
	return subRecords.getSubForm(Type.INAM).getForm();
    }

    /**
     * The voice type of the NPC.
     *
     * @param voiceTypeRef
     */
    public void setVoiceType(FormID voiceTypeRef) {
	subRecords.setSubForm(Type.VTCK, voiceTypeRef);
    }

    /**
     * The voice type of the NPC.
     *
     * @return
     */
    public FormID getVoiceType() {
	return subRecords.getSubForm(Type.VTCK).getForm();
    }

    /**
     *
     * @param templateRef
     */
    public void setTemplate(FormID templateRef) {
	subRecords.setSubForm(Type.TPLT, templateRef);
    }

    /**
     *
     * @return
     */
    public FormID getTemplate() {
	return subRecords.getSubForm(Type.TPLT).getForm();
    }

    /**
     *
     * @return True if NPC has a template actor
     */
    public boolean isTemplated() {
	return !getTemplate().equals(FormID.NULL);
    }

    /**
     *
     * @param raceRef
     */
    public void setRace(FormID raceRef) {
	subRecords.setSubForm(Type.RNAM, raceRef);
    }

    /**
     *
     * @return
     */
    public FormID getRace() {
	return subRecords.getSubForm(Type.RNAM).getForm();
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getSpells() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.SPLO));
    }

    /**
     *
     * @param spellReference FormID of the spell to give to the NPC.
     * @return True if spell was added.
     */
    public boolean addSpell(FormID spellReference) {
	return subRecords.getSubList(Type.SPLO).add(new SubForm(Type.SPLO, spellReference));
    }

    /**
     * Removes a spell from the NPC. If a spell with this FormID does not exist,
     * this spell does nothing.
     *
     * @param spellReference FormID of the spell to remove from the NPC
     * @return True if spell was removed.
     */
    public boolean removeSpell(FormID spellReference) {
	return subRecords.getSubList(Type.SPLO).remove(new SubForm(Type.SPLO, spellReference));
    }

    /**
     *
     */
    public void clearSpells() {
	subRecords.getSubList(Type.SPLO).clear();
    }

    /**
     *
     * @param itemReference
     * @param count
     * @return
     */
    public boolean addItem(FormID itemReference, int count) {
	return subRecords.getSubList(Type.CNTO).add(new SubFormInt(Type.CNTO, itemReference, count));
    }

    /**
     *
     * @param itemReference
     * @return
     */
    public boolean removeItem(FormID itemReference) {
	return subRecords.getSubList(Type.CNTO).remove(new SubFormInt(Type.CNTO, itemReference, 1));
    }

    /**
     *
     */
    public void clearItems() {
	subRecords.getSubList(Type.CNTO).clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<SubFormInt> getItems() {
	return SubList.subFormIntToPublic(subRecords.getSubList(Type.CNTO));
    }

    /**
     * Adds an AI package with the FormID to the NPC.
     *
     * @param aiPackageReference
     * @return True if AI package was added.
     */
    public boolean addAIPackage(FormID aiPackageReference) {
	return subRecords.getSubList(Type.PKID).add(new SubForm(Type.PKID, aiPackageReference));
    }

    /**
     *
     * @param aiPackageReference
     * @return True if AI package was removed.
     */
    public boolean removeAIPackage(FormID aiPackageReference) {
	return subRecords.getSubList(Type.PKID).remove(new SubForm(Type.PKID, aiPackageReference));
    }

    /**
     *
     * @return SubRecordList of AI packages.
     */
    public ArrayList<FormID> getAIPackages() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.PKID));
    }

    /**
     *
     */
    public void clearAIPackages() {
	subRecords.getSubList(Type.PKID).clear();
    }

    /**
     *
     * @param classReference
     */
    public void setNPCClass(FormID classReference) {
	subRecords.setSubForm(Type.CNAM, classReference);
    }

    /**
     *
     * @return
     */
    public FormID getNPCClass() {
	return subRecords.getSubForm(Type.CNAM).getForm();
    }

    /**
     *
     * @param hairColorRef
     */
    public void setHairColor(FormID hairColorRef) {
	subRecords.setSubForm(Type.HCLF, hairColorRef);
    }

    /**
     *
     * @return
     */
    public FormID getHairColor() {
	return subRecords.getSubForm(Type.HCLF).getForm();
    }

    /**
     *
     * @param wornArmorRef
     */
    public void setSkin(FormID wornArmorRef) {
	subRecords.setSubForm(Type.WNAM, wornArmorRef);
    }

    /**
     *
     * @return
     */
    public FormID getSkin() {
	return subRecords.getSubForm(Type.WNAM).getForm();
    }

    /**
     *
     * @param attackRaceRef
     */
    public void setAttackDataRace(FormID attackRaceRef) {
	subRecords.setSubForm(Type.ATKR, attackRaceRef);
    }

    /**
     *
     * @return
     */
    public FormID getAttackDataRace() {
	return subRecords.getSubForm(Type.ATKR).getForm();
    }

    /**
     *
     * @param defaultOutfitRef
     */
    public void setDefaultOutfit(FormID defaultOutfitRef) {
	subRecords.setSubForm(Type.DOFT, defaultOutfitRef);
    }

    /**
     *
     * @return
     */
    public FormID getDefaultOutfit() {
	return subRecords.getSubForm(Type.DOFT).getForm();
    }

    /**
     *
     * @param sleepingOutfitRef
     */
    public void setSleepingOutfit(FormID sleepingOutfitRef) {
	subRecords.setSubForm(Type.SOFT, sleepingOutfitRef);
    }

    /**
     *
     * @return
     */
    public FormID getSleepingOutfit() {
	return subRecords.getSubForm(Type.SOFT).getForm();
    }

    /**
     *
     * @param combatRef
     */
    public void setCombatStyle(FormID combatRef) {
	subRecords.setSubForm(Type.ZNAM, combatRef);
    }

    /**
     *
     * @return
     */
    public FormID getCombatStyle() {
	return subRecords.getSubForm(Type.ZNAM).getForm();
    }

    /**
     *
     * @param crimeFactionRef
     */
    public void setCrimeFaction(FormID crimeFactionRef) {
	subRecords.setSubForm(Type.CRIF, crimeFactionRef);
    }

    /**
     *
     * @return
     */
    public FormID getCrimeFaction() {
	return subRecords.getSubForm(Type.CRIF).getForm();
    }

    /**
     *
     * @param headPartsRef
     */
    public void setFeatureSet(FormID headPartsRef) {
	subRecords.setSubForm(Type.FTST, headPartsRef);
    }

    /**
     *
     * @return
     */
    public FormID getFeatureSet() {
	return subRecords.getSubForm(Type.FTST).getForm();
    }

    /**
     *
     * @param audioTemplateRef
     */
    public void setAudioTemplate(FormID audioTemplateRef) {
	subRecords.setSubForm(Type.CSCR, audioTemplateRef);
    }

    /**
     *
     * @return
     */
    public FormID getAudioTemplate() {
	return subRecords.getSubForm(Type.CSCR).getForm();
    }

    /**
     *
     * @param list
     */
    public void setDefaultPackageList(FormID list) {
	subRecords.setSubForm(Type.DPLT, list);
    }

    /**
     *
     * @return
     */
    public FormID getDefaultPackageList() {
	return subRecords.getSubForm(Type.DPLT).getForm();
    }

    /**
     *
     * @param height
     */
    public void setHeight(float height) {
	subRecords.setSubFloat(Type.NAM6, height);
    }

    /**
     *
     * @return
     */
    public float getHeight() {
	return subRecords.getSubFloat(Type.NAM6).get();
    }

    /**
     *
     * @param weight
     */
    public void setWeight(float weight) {
	subRecords.setSubFloat(Type.NAM7, weight);
    }

    /**
     *
     * @return
     */
    public float getWeight() {
	return subRecords.getSubFloat(Type.NAM7).get();
    }

    /**
     *
     * @param id
     */
    public void setFarAwayModelSkin(FormID id) {
	subRecords.setSubForm(Type.ANAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getFarAwayModelSkin() {
	return subRecords.getSubForm(Type.ANAM).getForm();
    }

    /**
     *
     * @param dist
     */
    public void setFarAwayModelDistance(float dist) {
	getDNAM().farAwayDistance = dist;
    }

    /**
     *
     * @return
     */
    public float getFarAwayModelDistance() {
	return getDNAM().farAwayDistance;
    }

    /**
     *
     * @param value
     */
    public void setHealthOffset(int value) {
	getACBS().healthOffset = value;
    }

    /**
     *
     * @return
     */
    public int getHealthOffset() {
	return getACBS().healthOffset;
    }

    /**
     *
     * @param value
     */
    public void setMagickaOffset(int value) {
	getACBS().magickaOffset = value;
    }

    /**
     *
     * @return
     */
    public int getMagickaOffset() {
	return getACBS().magickaOffset;
    }

    /**
     *
     * @param value
     */
    public void setFatigueOffset(int value) {
	getACBS().fatigueOffset = value;
    }

    /**
     *
     * @return
     */
    public int getFatigueOffset() {
	return getACBS().fatigueOffset;
    }

    /**
     *
     * @param value
     */
    public void setMood(Mood value) {
	getAIDT().mood = value;
    }

    /**
     *
     * @return
     */
    public Mood getMood() {
	return getAIDT().mood;
    }

    /**
     *
     * @param energy
     */
    public void setEnergy(int energy) {
	getAIDT().energy = energy;
    }

    /**
     *
     * @return
     */
    public int getEnergy() {
	return getAIDT().energy;
    }

    /**
     *
     * @param aggro
     */
    public void setAggroWarn(int aggro) {
	getAIDT().aggroWarn = aggro;
    }

    /**
     *
     * @return
     */
    public int getAggroWarn() {
	return getAIDT().aggroWarn;
    }

    /**
     *
     * @param aggro
     */
    public void setAggroWarnAttack(int aggro) {
	getAIDT().aggroWarnAttack = aggro;
    }

    /**
     *
     * @return
     */
    public int getAggroWarnAttack() {
	return getAIDT().aggroWarnAttack;
    }

    /**
     *
     * @param aggro
     */
    public void setAggroAttack(int aggro) {
	getAIDT().aggroAttack = aggro;
    }

    /**
     *
     * @return
     */
    public int getAggroAttack() {
	return getAIDT().aggroAttack;
    }

    /**
     *
     * @param id
     */
    public void setGiftFilter(FormID id) {
	subRecords.setSubForm(Type.GNAM, id);
    }

    /**
     *
     * @return
     */
    public FormID getGiftFilter() {
	return subRecords.getSubForm(Type.ACBS).getForm();
    }

    /**
     *
     * @param value
     */
    public void setGearedUpWeapons(int value) {
	getDNAM().gearedUpWeapons = value;
    }

    /**
     *
     * @return
     */
    public int getGearedUpWeapons() {
	return getDNAM().gearedUpWeapons;
    }

    /**
     *
     * @param list
     */
    public void setSpectatorOverride(FormID list) {
	subRecords.setSubForm(Type.SPOR, list);
    }

    /**
     *
     * @return
     */
    public FormID getSpectatorOverride() {
	return subRecords.getSubForm(Type.SPOR).getForm();
    }

    /**
     *
     * @param list
     */
    public void setObserveDeadOverride(FormID list) {
	subRecords.setSubForm(Type.OCOR, list);
    }

    /**
     *
     * @return
     */
    public FormID getObserveDeadOverride() {
	return subRecords.getSubForm(Type.OCOR).getForm();
    }

    /**
     *
     * @param list
     */
    public void setGuardWornOverride(FormID list) {
	subRecords.setSubForm(Type.GWOR, list);
    }

    /**
     *
     * @return
     */
    public FormID getGuardWornOverride() {
	return subRecords.getSubForm(Type.GWOR).getForm();
    }

    /**
     *
     * @param list
     */
    public void setCombatOverride(FormID list) {
	subRecords.setSubForm(Type.ECOR, list);
    }

    /**
     *
     * @return
     */
    public FormID getCombatOverride() {
	return subRecords.getSubForm(Type.ECOR).getForm();
    }

    /**
     *
     * @param alias
     */
    public void setShortName(String alias) {
	subRecords.setSubString(Type.SHRT, alias);
    }

    /**
     *
     * @return
     */
    public String getShortName() {
	return subRecords.getSubString(Type.SHRT).print();
    }

    /**
     *
     * @param vol
     */
    public void setSoundVolume(SoundVolume vol) {
	subRecords.setSubInt(Type.NAM8, vol.ordinal());
    }

    /**
     *
     * @return
     */
    public SoundVolume getSoundVolume() {
	return SoundVolume.values()[subRecords.getSubInt(Type.NAM8).get()];
    }

    NAM9 getNAM9() {
	return (NAM9) subRecords.get(Type.NAM9);
    }

    /**
     *
     * @param part
     * @param value
     */
    public void setFaceValue(FacePart part, float value) {
	switch (part) {
	    case NoseLongShort:
		getNAM9().noseLong = value;
		break;
	    case NoseUpDown:
		getNAM9().noseUp = value;
		break;
	    case JawUpDown:
		getNAM9().jawUp = value;
		break;
	    case JawNarrowWide:
		getNAM9().jawWide = value;
		break;
	    case JawForwardBack:
		getNAM9().jawForward = value;
		break;
	    case CheeksUpDown:
		getNAM9().cheekUp = value;
		break;
	    case CheeksForwardBack:
		getNAM9().cheekForward = value;
		break;
	    case EyesUpDown:
		getNAM9().eyeUp = value;
		break;
	    case EyesInOut:
		getNAM9().eyeIn = value;
		break;
	    case BrowsUpDown:
		getNAM9().browUp = value;
		break;
	    case BrowsForwardBack:
		getNAM9().browForward = value;
		break;
	    case LipsUpDown:
		getNAM9().lipUp = value;
		break;
	    case LipsInOut:
		getNAM9().lipIn = value;
		break;
	    case ChinThinWide:
		getNAM9().chinWide = value;
		break;
	    case ChinUpDown:
		getNAM9().chinUp = value;
		break;
	    case ChinOverbite:
		getNAM9().chinOverbite = value;
		break;
	    case EyesForwardBack:
		getNAM9().eyesForward = value;
		break;
	}
	getNAM9().valid = true;
    }

    /**
     *
     * @param part
     * @return
     */
    public float getFaceValue(FacePart part) {
	switch (part) {
	    case NoseLongShort:
		return getNAM9().noseLong;
	    case NoseUpDown:
		return getNAM9().noseUp;
	    case JawUpDown:
		return getNAM9().jawUp;
	    case JawNarrowWide:
		return getNAM9().jawWide;
	    case JawForwardBack:
		return getNAM9().jawForward;
	    case CheeksUpDown:
		return getNAM9().cheekUp;
	    case CheeksForwardBack:
		return getNAM9().cheekForward;
	    case EyesUpDown:
		return getNAM9().eyeUp;
	    case EyesInOut:
		return getNAM9().eyeIn;
	    case BrowsUpDown:
		return getNAM9().browUp;
	    case BrowsForwardBack:
		return getNAM9().browForward;
	    case LipsUpDown:
		return getNAM9().lipUp;
	    case LipsInOut:
		return getNAM9().lipIn;
	    case ChinThinWide:
		return getNAM9().chinWide;
	    case ChinUpDown:
		return getNAM9().chinUp;
	    case ChinOverbite:
		return getNAM9().chinOverbite;
	    default:
		return getNAM9().eyesForward;
	}
    }

    /**
     *
     * @return
     */
    public ArrayList<TintLayer> getTinting() {
	return subRecords.getSubList(Type.TINI).collection;
    }

    /**
     *
     * @param tinting
     * @return
     */
    public boolean addTinting(TintLayer tinting) {
	return subRecords.getSubList(Type.TINI).add(tinting);
    }

    /**
     *
     * @param tinting
     * @return
     */
    public boolean removeTinting(TintLayer tinting) {
	return subRecords.getSubList(Type.TINI).remove(tinting);
    }

    /**
     *
     */
    public void clearTinting() {
	subRecords.getSubList(Type.TINI).clear();
    }

    /**
     *
     * @return
     */
    public ArrayList<SoundPackage> getSounds() {
	return subRecords.getSubList(Type.CSDT).collection;
    }

    /**
     *
     * @param sounds
     * @return
     */
    public boolean addSoundPackage(SoundPackage sounds) {
	return subRecords.getSubList(Type.CSDT).add(sounds);
    }

    /**
     *
     * @param sounds
     * @return
     */
    public boolean removeSoundPackage(SoundPackage sounds) {
	return subRecords.getSubList(Type.CSDT).remove(sounds);
    }

    /**
     *
     */
    public void clearSoundPackages() {
	subRecords.getSubList(Type.CSDT).clear();
    }

    /**
     *
     * @param color
     * @return
     */
    public float getFaceTint(RGB color) {
	return subRecords.getSubRGB(Type.QNAM).get(color);
    }

    /**
     *
     * @param color
     * @param value
     */
    public void setFaceTint(RGB color, float value) {
	subRecords.setSubRGB(Type.QNAM, color, value);
    }

    NAMA getNAMA() {
	return (NAMA) subRecords.get(Type.NAMA);
    }

    /**
     *
     * @return
     */
    public int getNosePreset() {
	return getNAMA().nose;
    }

    /**
     *
     * @param val
     */
    public void setNosePreset(int val) {
	getNAMA().nose = val;
	getNAMA().valid = true;
    }

    /**
     *
     * @return
     */
    public int getEyePreset() {
	return getNAMA().eyes;
    }

    /**
     *
     * @param val
     */
    public void setEyePreset(int val) {
	NAMA n = getNAMA();
	n.eyes = val;
	n.valid = true;
    }

    /**
     *
     * @return
     */
    public int getMouthPreset() {
	return getNAMA().mouth;
    }

    /**
     *
     * @param val
     */
    public void setMouthPreset(int val) {
	NAMA n = getNAMA();
	n.mouth = val;
	n.valid = true;
    }

    /**
     *
     * @return
     */
    public ArrayList<FormID> getHeadParts() {
	return SubList.subFormToPublic(subRecords.getSubList(Type.PNAM));
    }

    /**
     *
     * @param pnam
     */
    public void addHeadPart(FormID pnam) {
	subRecords.getSubList(Type.PNAM).add(new SubForm(Type.PNAM, pnam));
    }

    /**
     *
     * @param pnam
     */
    public void removeHeadPart(FormID pnam) {
	subRecords.getSubList(Type.PNAM).remove(new SubForm(Type.PNAM, pnam));
    }

    /**
     *
     */
    public void clearHeadParts() {
	subRecords.getSubList(Type.PNAM).clear();
    }

    ATKD getATKD() {
	return (ATKD) subRecords.get(Type.ATKD);
    }

    void setATKD(ATKD rhs) {
	subRecords.add(rhs);
    }

    void setATKE(String in) {
	subRecords.setSubString(Type.ATKE, in);
    }

    String getATKE() {
	return subRecords.getSubString(Type.ATKE).print();
    }

    public KeywordSet getKeywordSet() {
	return subRecords.getKeywords();
    }
}
