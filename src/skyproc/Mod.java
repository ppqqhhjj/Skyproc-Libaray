package skyproc;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import lev.LExportParser;
import lev.LFileChannel;
import lev.LFlags;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.MajorRecord.Mask;
import skyproc.SubStringPointer.Files;

/**
 * A mod is a collection of GRUPs which contain records. Mods are used to create
 * patches by creating an empty one, adding records, and then calling its export
 * function.
 *
 * @author Justin Swanson
 */
public class Mod extends ExportRecord implements Comparable, Iterable<GRUP> {

    TES4 header = new TES4();
    ModListing modInfo;
    Map<Type, GRUP> GRUPs = new EnumMap<Type, GRUP>(Type.class);
    GRUP<LVLN> LLists = new GRUP<LVLN>(this, new LVLN());
    GRUP<NPC_> NPCs = new GRUP<NPC_>(this, new NPC_());
    GRUP<PERK> perks = new GRUP<PERK>(this, new PERK());
    GRUP<IMGS> imageSpaces = new GRUP<IMGS>(this, new IMGS());
    GRUP<SPEL> spells = new GRUP<SPEL>(this, new SPEL());
    GRUP<RACE> races = new GRUP<RACE>(this, new RACE());
    GRUP<ARMO> armors = new GRUP<ARMO>(this, new ARMO());
    GRUP<ARMA> armatures = new GRUP<ARMA>(this, new ARMA());
    GRUP<TXST> textures = new GRUP<TXST>(this, new TXST());
    GRUP<WEAP> weapons = new GRUP<WEAP>(this, new WEAP());
    GRUP<KYWD> keywords = new GRUP<KYWD>(this, new KYWD());
    Map<SubStringPointer.Files, Map<Integer, Integer>> strings = new EnumMap<SubStringPointer.Files, Map<Integer, Integer>>(SubStringPointer.Files.class);
    private ArrayList<String> outStrings = new ArrayList<String>();
    private ArrayList<String> outDLStrings = new ArrayList<String>();
    private ArrayList<String> outILStrings = new ArrayList<String>();
    private static int indexCount = 1000;

    /**
     * Creates an empty Mod with the name and master flag set to match info.
     *
     * @see ModListing
     * @param info ModListing object containing name and master flag.
     */
    public Mod(ModListing info) {
	init(info, Long.MAX_VALUE - indexCount--);
	SPGlobal.getDB().add(this);
    }

    Mod(ModListing info, long inputDate, ByteBuffer headerInfo) throws BadRecord, DataFormatException, BadParameter {
	this(info, inputDate);
	logSync("MOD", "Parsing header");
	header.parseData(headerInfo);
    }

    Mod(ModListing info, long inputDate) {
	init(info, inputDate);
    }

    final void init(ModListing info, long inputDate) {
	this.modInfo = info;
	this.modInfo.date = inputDate;
	this.setFlag(Mod_Flags.MASTER, info.getMasterTag());
	this.setFlag(Mod_Flags.STRING_TABLED, true);
	strings.put(SubStringPointer.Files.STRINGS, new TreeMap<Integer, Integer>());
	strings.put(SubStringPointer.Files.DLSTRINGS, new TreeMap<Integer, Integer>());
	strings.put(SubStringPointer.Files.ILSTRINGS, new TreeMap<Integer, Integer>());
	GRUPs.put(LLists.getContainedType(), LLists);
	GRUPs.put(NPCs.getContainedType(), NPCs);
	GRUPs.put(perks.getContainedType(), perks);
	GRUPs.put(imageSpaces.getContainedType(), imageSpaces);
	GRUPs.put(spells.getContainedType(), spells);
	GRUPs.put(races.getContainedType(), races);
	GRUPs.put(armors.getContainedType(), armors);
	GRUPs.put(armatures.getContainedType(), armatures);
	GRUPs.put(textures.getContainedType(), textures);
	GRUPs.put(weapons.getContainedType(), weapons);
	GRUPs.put(keywords.getContainedType(), keywords);
    }

    /**
     * Creates an empty Mod with the name and master flag set to parameters.
     *
     * @param name String to set the Mod name to.
     * @param master Sets the Mod's master flag (which appends ".esp" and ".esm"
     * to the modname as appropriate)
     */
    public Mod(String name, Boolean master) {
	this(new ModListing(name, master));
    }

    void addMaster(ModListing input) {
	header.addMaster(input);
    }

    /**
     * Returns the ModListing associated with the nth master of this mod.
     * Changing values on the returned ModListing will affect the mod it is tied
     * to.
     *
     * @param i The index of the master to return.
     * @return The ModListing object associated with the Nth master
     */
    public ModListing getNthMaster(int i) {
	if (header.getMasters().size() > i) {
	    return header.getMasters().get(i);
	} else {
	    return getInfo();
	}
    }

    /**
     *
     * @return The number of masters in the mod.
     */
    public int numMasters() {
	return header.getMasters().size();
    }

    /**
     *
     * @return True if no GRUP in the mod has any records.
     */
    public Boolean isEmpty() {
	for (GRUP g : GRUPs.values()) {
	    if (g.numRecords() > 0) {
		return false;
	    }
	}
	return true;
    }

    FormID getNextID(String edid) {
	// If not global patch, just give next id
	if (!equals(SPGlobal.getGlobalPatch())) {
	    return new FormID(header.HEDR.nextID++, getInfo());

	// If global patch, check for consistency
	} else {
	    // If has an EDID match, grab old FormID
	    if (SPGlobal.edidToForm.containsKey(edid)) {
		if (SPGlobal.logging()) {
		    SPGlobal.logSync(getName(), "Assigning old FormID " + SPGlobal.edidToForm.get(edid) + " for EDID " + edid);
		}
		return SPGlobal.edidToForm.get(edid);
	    } else {

		//Find next open FormID
		FormID possibleID = new FormID(header.HEDR.nextID++, getInfo());
		for (int i = 0 ; i < SPGlobal.edidToForm.size() ; i++) {
		    if (!SPGlobal.edidToForm.containsValue(possibleID)) {
			break;
		    }
		    possibleID = new FormID(header.HEDR.nextID++, getInfo());
		}
		if (SPGlobal.logging()) {
		    SPGlobal.logSync(getName(), "Assigning new FormID " + possibleID + " for EDID " + edid);
		}
		return possibleID;
	    }
	}
    }

    void mergeMasters(Mod in) {
	for (ModListing m : in.header.masters) {
	    header.masters.add(m);
	}
	if (!in.equals(SPGlobal.getGlobalPatch())) {
	    header.masters.add(in.modInfo);
	}
    }

    /**
     * Makes a copy of the Major Record and loads it into the mod, giving a new
     * Major Record a FormID originating from the mod. This function also
     * automatically adds the new copied record to the mod. This makes two
     * separate records independent of each other.<br><br>
     *
     * This function requires there to be a GlobalDB set, as it adds the
     * necessary masters from it.<br><br>
     *
     * NOTE: The record returned can only be determined by the compiler to be a
     * Major Record. You must cast it yourself to be the correct type of major
     * record.<br> ex. NPC_ newNPC = (NPC_) myPatch.makeCopy(otherNPC);
     *
     * @param m Major Record to make a copy of and add to the mod.
     * @return The copied record.
     */
    public MajorRecord makeCopy(MajorRecord m) {
	mergeMasters(SPGlobal.getDB().modLookup.get(m.getFormMaster()));
	m = m.copyOf(this);
	GRUPs.get(m.getTypes()[0]).addRecord(m);
	return m;
    }

    /**
     * This function requires there to be a GlobalDB set, as it adds the
     * necessary masters from it.<br><br>
     *
     * @param g GRUP to make a copy of.
     * @return ArrayList of duplicated Major Records.
     */
    public ArrayList<MajorRecord> makeCopy(GRUP g) {
	ArrayList<MajorRecord> out = new ArrayList<MajorRecord>();
	for (Object o : g) {
	    MajorRecord m = (MajorRecord) o;
	    out.add(makeCopy(m));
	}
	return out;
    }

    /**
     *
     * This function requires there to be a GlobalDB set, as it adds the
     * necessary masters from it.
     *
     * @param m Major Record to add as an override.
     */
    public void addRecord(MajorRecord m) {
	GRUPs.get(m.getTypes()[0]).addRecord(m);
	mergeMasters(SPGlobal.getDB().modLookup.get(m.getFormMaster()));
    }

    /**
     * Prints each GRUP in the mod to the asynchronous log.
     */
    public void print() {

	newSyncLog("Mod Export/" + getName() + ".txt");

	if (!getMastersStrings().isEmpty()) {
	    logSync(getName(), "=======================================================================");
	    logSync(getName(), "======================= Printing Mod Masters ==========================");
	    logSync(getName(), "=======================================================================");
	    for (String s : getMastersStrings()) {
		logSync(getName(), s);
	    }
	}
	for (GRUP g : GRUPs.values()) {
	    g.toString();
	}
	logSync(getName(), "------------------------  DONE PRINTING -------------------------------");
    }

    void standardizeMasters() {
	logSync("Standardizing", getName());
	for (GRUP g : GRUPs.values()) {
	    g.standardizeMasters();
	}
    }

    void fetchExceptions(SPDatabase database) {
	for (GRUP g : GRUPs.values()) {
	    g.fetchExceptions(database);
	}
    }

    void fetchStringPointers() throws IOException {
	Map<SubStringPointer.Files, LFileChannel> streams = null;
	if (this.isFlag(Mod_Flags.STRING_TABLED)) {
	    streams = new EnumMap<SubStringPointer.Files, LFileChannel>(SubStringPointer.Files.class);
	    for (Files f : SubStringPointer.Files.values()) {
		addStream(streams, f);
	    }
	}
	for (GRUP g : GRUPs.values()) {
	    g.fetchStringPointers(this, streams);
	}
    }

    void addStream(Map<SubStringPointer.Files, LFileChannel> streams, SubStringPointer.Files file) {
	try {
	    streams.put(file, new LFileChannel(SPImporter.pathToStringFile(this, file)));
	} catch (FileNotFoundException ex) {
	    logSync(getName(), "Could not open a strings stream for mod " + getName() + " to type: " + file);
	}
    }

    /**
     *
     * @see ModListing
     * @return The names of all the masters of the mod.
     */
    public ArrayList<String> getMastersStrings() {
	ArrayList<String> out = new ArrayList<String>();
	for (ModListing m : header.getMasters()) {
	    out.add(m.print());
	}
	return out;
    }

    /**
     * Returns the ModListings of all the masters of the mod. Note that changing
     * any ModListings will have an effect on their associated mods.
     *
     * @see ModListing
     * @return The ModListings of all the masters of the mod.
     */
    public ArrayList<ModListing> getMasters() {
	ArrayList<ModListing> out = new ArrayList<ModListing>();
	for (ModListing m : header.getMasters()) {
	    out.add(m);
	}
	return out;
    }

    /**
     * Clears all GRUPS in the Mod except for the GRUPs specified in the
     * parameter.
     *
     * @param grup_type Any amount of GRUPs to keep, separated by commas
     */
    public void keep(GRUP_TYPE... grup_type) {
	ArrayList<Type> grups = new ArrayList<Type>();
	for (GRUP_TYPE t : grup_type) {
	    grups.add(Type.toRecord(t));
	}
	for (GRUP g : GRUPs.values()) {
	    if (!grups.contains(g.getContainedType())) {
		g.clear();
	    }
	}
    }

    /**
     * This function will merge all GRUPs from the rhs mod into the calling
     * mod.<br> Any records already in the mod will be overwritten by the
     * version from rhs.<br><br> NOTE: Merging will NOT add records from a mod
     * with a matching ModListing. This is to prevent patches taking in
     * information from old versions of themselves.
     *
     * @param rhs Mod whose GRUPs to add in.
     * @param grup_types Any amount of GRUPs to merge in, separated by commas.
     * Leave this empty if you want all GRUPs merged.
     */
    public void addAsOverrides(Mod rhs, GRUP_TYPE... grup_types) {
	if (grup_types.length == 0) {
	    grup_types = GRUP_TYPE.values();
	}
	ArrayList<Type> grups = new ArrayList<Type>();
	for (GRUP_TYPE t : grup_types) {
	    grups.add(Type.toRecord(t));
	}
	if (!this.equals(rhs)) {
	    mergeMasters(rhs);
	    for (Type t : GRUPs.keySet()) {
		if (grups.contains(t)) {
		    GRUPs.get(t).merge(rhs.GRUPs.get(t));
		}
	    }
	}
    }

    /**
     * Iterates through each mod in the ArrayList, in order, and merges them in
     * one by one.<br> This means any conflicting records within the list will
     * end up with the last mod's version.<br><br> NOTE: Merging will NOT add
     * records from a mod with a matching ModListing. This is to prevent patches
     * taking in information from old versions of themselves.
     *
     * @param in ArrayList of mods to merge in.
     * @param grup_types Any amount of GRUPs to merge in, separated by commas.
     * Leave this empty if you want all GRUPs merged.
     */
    public void addAsOverrides(ArrayList<Mod> in, GRUP_TYPE... grup_types) {
	for (Mod m : in) {
	    addAsOverrides(m, grup_types);
	}
    }

    /**
     * NOTE: To sort the mods in load order, use a TreeSet.<br> Iterates through
     * each mod in the Set and merges them in one by one.<br> This means any
     * conflicting records within the set will end up with the last mod's
     * version.<br><br> NOTE: Merging will NOT add records from a mod with a
     * matching ModListing. This is to prevent patches taking in information
     * from old versions of themselves.
     *
     * @param in Set of mods to merge in.
     * @param grup_types Any amount of GRUPs to merge in, separated by commas.
     * Leave this empty if you want all GRUPs merged.
     */
    public void addAsOverrides(Collection<Mod> in, GRUP_TYPE... grup_types) {
	for (Mod m : in) {
	    addAsOverrides(m, grup_types);
	}
    }

    /**
     * Iterates through each mod in the SPDatabase, in load order, and merges
     * them in one by one.<br> This means any conflicting records within the
     * database will end up with the last mod's version.<br><br> NOTE: Merging
     * will NOT add records from a mod with a matching ModListing. This is to
     * prevent patches taking in information from old versions of themselves.
     *
     * @param db The SPDatabase to merge in.
     * @param grup_types Any amount of GRUPs to merge in, separated by commas.
     * Leave this empty if you want all GRUPs merged.
     */
    public void addAsOverrides(SPDatabase db, GRUP_TYPE... grup_types) {
	addAsOverrides(db.modLookup.values(), grup_types);
    }

    /**
     *
     * @return The number of records contained in all the GRUPs in the mod.
     */
    public int numRecords() {
	int sum = 0;
	for (GRUP g : GRUPs.values()) {
	    if (g.numRecords() != 0) {
		sum += g.numRecords() + 1;
	    }
	}
	return sum;
    }

    int getTotalLength() {
	return getContentLength() + header.getTotalLength(this);
    }

    int getContentLength() {
	int sum = 0;
	for (GRUP g : GRUPs.values()) {
	    sum += g.getTotalLength(this);
	}
	return sum;
    }

    /**
     * Exports the mod to the path designated by SPGlobal.pathToData.
     *
     * @see SPGlobal
     * @throws IOException If there are any unforseen disk errors exporting the
     * data.
     */
    public void export() throws IOException {
	export(SPGlobal.pathToData);
    }

    /**
     * Exports the mod to the path given by the parameter. (You shouldn't
     * include the mod name in the string)
     *
     * @param path
     * @throws IOException
     */
    public void export(String path) throws IOException {
	export(new LExportParser(path + getName()), this);
    }

    @Override
    void export(LExportParser out, Mod srcMod) throws IOException {
	header.setNumRecords(numRecords());
	header.export(out, srcMod);

	standardizeMasters();
	if (logging()) {
	    logSync(this.getName(), "Exporting " + this.numRecords() + " records.");
	}
	for (GRUP g : GRUPs.values()) {
	    g.export(out, srcMod);
	}

	if (srcMod.isFlag(Mod_Flags.STRING_TABLED)) {
	    exportStringsFile(outStrings, SubStringPointer.Files.STRINGS);
	    exportStringsFile(outDLStrings, SubStringPointer.Files.DLSTRINGS);
	    exportStringsFile(outILStrings, SubStringPointer.Files.ILSTRINGS);
	}
	out.close();
    }

    int addOutString(String in, SubStringPointer.Files file) {
	switch (file) {
	    case DLSTRINGS:
		return addOutString(in, outDLStrings);
	    case ILSTRINGS:
		return addOutString(in, outILStrings);
	    default:
		return addOutString(in, outStrings);
	}
    }

    int addOutString(String in, ArrayList<String> list) {
	if (!list.contains(in)) {
	    list.add(in);
	}
	return list.indexOf(in) + 1;  // To prevent indexing starting at 0
    }

    void exportStringsFile(ArrayList<String> list, SubStringPointer.Files file) throws FileNotFoundException, IOException {
	int stringLength = 0;
	for (String s : list) {
	    stringLength += s.length() + 1;

	}
	int outLength = stringLength + 8 * list.size() + 8;
	LExportParser out = new LExportParser(SPGlobal.pathToData + "Strings/" + getNameNoSuffix() + "_" + SPGlobal.language + "." + file);

	out.write(list.size(), 4);
	out.write(stringLength, 4);

	int i = 1;  // To prevent indexing starting at 0
	int offset = 0;
	for (String s : list) {
	    out.write(i++, 4);
	    out.write(offset, 4);
	    offset += s.length() + 1;
	}

	for (String s : list) {
	    out.write(s);
	    out.write(0, 1);  // Null terminator
	}

	list.clear();
	out.close();
    }

    /**
     * Sets the author name of the mod.
     *
     * @param in Your name here.
     */
    public void setAuthor(String in) {
	header.setAuthor(in);
    }

    void parseData(Type type, ByteBuffer data, Map<Type, Mask> masks) throws BadRecord, DataFormatException, BadParameter {
	if (masks.containsKey(type)) {
	    GRUPs.get(type).parseData(data, masks.get(type));
	} else {
	    GRUPs.get(type).parseData(data);
	}
    }

    /**
     * Returns whether the given flag is on or off. <br> <br> An example use of
     * this function is as follows: <br> boolean isaMasterMod =
     * myMod.isFlag(Mod_Flags.MASTER);
     *
     * @see Mod_Flags
     * @param flag Mod_Flags enum to check.
     * @return True if the given flag is on in the mod.
     */
    public boolean isFlag(Mod_Flags flag) {
	return header.flags.is(flag.value);
    }

    /**
     * Sets the given flag in the mod. <br> <br> An example of using this
     * function to set its master flag is as follows: <br>
     * myMod.setFlag(Mod_Flags.MASTER, true);
     *
     * @see Mod_Flags
     * @param flag Mod_Flags enum to set.
     * @param on What to set the flag to.
     */
    public final void setFlag(Mod_Flags flag, boolean on) {
	header.flags.set(flag.value, on);
	if (flag == Mod_Flags.MASTER) {
	    getInfo().setMasterTag(on);
	}
    }

    // Get Set
    /**
     *
     * @see GRUP
     * @return The GRUP containing Leveled List records.
     */
    public GRUP<LVLN> getLeveledLists() {
	return LLists;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing NPC records.
     */
    public GRUP<NPC_> getNPCs() {
	return NPCs;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Image Space records.
     */
    public GRUP<IMGS> getImageSpaces() {
	return imageSpaces;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Perk records.
     */
    public GRUP<PERK> getPerks() {
	return perks;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Spell records.
     */
    public GRUP<SPEL> getSpells() {
	return spells;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Race records
     */
    public GRUP<RACE> getRaces() {
	return races;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Armor records
     */
    public GRUP<ARMO> getArmors() {
	return armors;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Armature records
     */
    public GRUP<ARMA> getArmatures() {
	return armatures;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Texture records
     */
    public GRUP<TXST> getTextureSets() {
	return textures;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Weapon records
     */
    public GRUP<WEAP> getWeapons() {
	return weapons;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Keyword records
     */
    public GRUP<KYWD> getKeywords() {
	return keywords;
    }

    /**
     *
     * @return The name of the mod (including suffix)
     */
    public String getName() {
	return modInfo.print();
    }

    /**
     *
     * @see ModListing
     * @return The ModListing object associated with the mod.
     */
    public ModListing getInfo() {
	return modInfo;
    }

    /**
     *
     * @return The name of the mod (without suffix)
     */
    public String getNameNoSuffix() {
	return getName().substring(0, getName().indexOf(".es"));
    }

    /**
     * Returns the datestamp of when the mod was last modified, which also is
     * used for load order. A larger datestamp means later in load order.
     *
     * @return The datestamp of when the mod was last modified.
     */
    public long getDate() {
	return getInfo().date;
    }

    void newSyncLog(String fileName) {
	SPGlobal.newSyncLog(fileName);
    }

    boolean logging() {
	return SPGlobal.logging();
    }

    final void logMain(String header, String... log) {
	SPGlobal.logMain(header, log);
    }

    final void log(String header, String... log) {
	SPGlobal.log(header, log);
    }

    void newLog(String fileName) {
	SPGlobal.newLog(fileName);
    }

    final void logSync(String header, String... log) {
	SPGlobal.logSync(header, log);
    }

    void logError(String header, String... log) {
	SPGlobal.logError(header, log);
    }

    void logSpecial(Enum e, String header, String... log) {
	SPGlobal.logSpecial(e, header, log);
    }

    void flush() {
	SPGlobal.flush();
    }

    /**
     * A compare function used for sorting Mods in load order. <br> .esp > .esm
     * <br> later date > earlier date <br>
     *
     * @param o Another Mod object
     * @return 1: Mod is later in the load order <br> -1: Mod is earlier in the
     * load order <br> 0: Mod is equal in load order (should not happen)
     */
    @Override
    public int compareTo(Object o) {
	Mod rhs = (Mod) o;
	if (this.getInfo().getMasterTag() == true
		&& rhs.getInfo().getMasterTag() == false) {
	    return -1;
	}
	if (this.getInfo().getMasterTag() == false
		&& rhs.getInfo().getMasterTag() == true) {
	    return 1;
	}
	if (getInfo().date < rhs.getInfo().date) {
	    return -1;
	}
	if (getInfo().date > rhs.getInfo().date) {
	    return 1;
	}

	return 0;
    }

    /**
     * An equals function that compares only mod name. It does NOT compare mod
     * contents.
     *
     * @param obj
     * @return True if obj is a mod with the same name and suffix (Skyrim.esm ==
     * Skyrim.esm)
     */
    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Mod other = (Mod) obj;
	if (!this.getName().equalsIgnoreCase(other.getName())) {
	    return false;
	}
	return true;
    }

    /**
     * A custom hash function that takes the mod header into account for better
     * hashing.
     *
     * @return
     */
    @Override
    public int hashCode() {
	int hash = 5;
	hash = 97 * hash + (this.modInfo != null ? this.modInfo.hashCode() : 0);
	return hash;
    }

    @Override
    public Iterator<GRUP> iterator() {
	return GRUPs.values().iterator();
    }

    // Internal Classes
    static class TES4 extends Record {

	private static byte[] defaultINTV = Ln.parseHexString("C5 26 01 00", 4);
	SubRecords subRecords = new SubRecords();
	private LFlags flags = new LFlags(4);
	private int fluff1 = 0;
	private int fluff2 = 0;
	private int fluff3 = 0;
	HEDR HEDR = new HEDR();
	SubString author = new SubString(Type.CNAM, true);
	SubSortedList<ModListing> masters = new SubSortedList<ModListing>(new ModListing());
	SubString description = new SubString(Type.SNAM, true);
	SubData INTV = new SubData(Type.INTV, defaultINTV);
	private static final Type[] type = {Type.TES4};

	TES4() {
	    subRecords.add(HEDR);
	    subRecords.add(author);
	    subRecords.add(description);
	    subRecords.add(masters);
	    subRecords.add(INTV);
	}

	TES4(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	@Override
	final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    flags.set(in.extract(4));
	    fluff1 = Ln.arrayToInt(in.extractInts(4));
	    fluff2 = Ln.arrayToInt(in.extractInts(4));
	    fluff3 = Ln.arrayToInt(in.extractInts(4));
	    subRecords.importSubRecords(in);
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	public String toString() {
	    return "HEDR";
	}

	@Override
	Type[] getTypes() {
	    return type;
	}

	@Override
	void export(LExportParser out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export(), 4);
	    out.write(fluff1, 4);
	    out.write(fluff2, 4);
	    out.write(fluff3, 4);
	    HEDR.export(out, srcMod);
	    author.export(out, srcMod);
	    masters.export(out, srcMod);
	}

	void addMaster(ModListing mod) {
	    masters.add(mod);
	}

	void clearMasters() {
	    masters.clear();
	}

	SubSortedList<ModListing> getMasters() {
	    return masters;
	}

	void setAuthor(String in) {
	    author.setString(in);
	}

	@Override
	int getFluffLength() {
	    return 16;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    int out = 0;
	    out += HEDR.getTotalLength(srcMod);
	    out += author.getTotalLength(srcMod);
	    out += masters.getTotalLength(srcMod);
	    return out;
	}

	@Override
	int getSizeLength() {
	    return 4;
	}

	void setNumRecords(int num) {
	    HEDR.setRecords(num);
	}

	@Override
	Record getNew() {
	    return new TES4();
	}

	@Override
	public String print() {
	    return toString();
	}
    }

    static class HEDR extends SubRecord {

	byte[] version;
	int numRecords;
	int nextID;

	HEDR() {
	    super(Type.HEDR);
	    clear();
	}

	HEDR(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    this();
	    parseData(in);
	}

	void setRecords(int num) {
	    numRecords = num;
	}

	int numRecords() {
	    return numRecords;
	}

	void addRecords(int num) {
	    setRecords(numRecords() + num);
	}

	int nextID() {
	    return nextID++;
	}

	@Override
	void export(LExportParser out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(version, 4);
	    out.write(numRecords, 4);
	    out.write(nextID, 4);
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    version = in.extract(4);
	    numRecords = in.extractInt(4);
	    nextID = in.extractInt(4);
	}

	@Override
	SubRecord getNew(Type type) {
	    return new HEDR();
	}

	@Override
	final public void clear() {
	    version = Ln.parseHexString("D7 A3 70 3F", 4);
	    numRecords = 0;
	    nextID = 3426;  // first available ID on empty CS plugins
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}
    }

    /**
     * The various flags found in the Mod header.
     */
    public enum Mod_Flags {

	/**
	 * Master flag determines whether a mod is labeled as a master plugin
	 * and receives a ".esm" suffix.
	 */
	MASTER(0),
	/**
	 * String Tabled flag determines whether names and descriptions are
	 * stored inside the mod itself, or in external STRINGS files. <br><br>
	 *
	 * In general, it is suggested to disable this flag for most patches, as
	 * the only benefit of external STRINGS files is easy multi-language
	 * support. Skyproc can offer this same multilanguage support without
	 * external STRINGS files. See SPGlobal.language for more information.
	 *
	 * @see SPGlobal
	 */
	STRING_TABLED(7);
	int value;

	private Mod_Flags(int in) {
	    value = in;
	}
    };
}
