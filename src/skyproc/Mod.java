package skyproc;

import skyproc.gui.SPProgressBarPlug;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.DataFormatException;
import javax.swing.JOptionPane;
import lev.*;
import skyproc.MajorRecord.Mask;
import skyproc.SubStringPointer.Files;
import skyproc.exceptions.BadMod;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

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
    Map<GRUP_TYPE, GRUP> GRUPs = new EnumMap<GRUP_TYPE, GRUP>(GRUP_TYPE.class);
    GRUP<GMST> gameSettings = new GRUP<GMST>(this, new GMST());
    GRUP<KYWD> keywords = new GRUP<KYWD>(this, new KYWD());
    GRUP<TXST> textures = new GRUP<TXST>(this, new TXST());
    GRUP<FACT> factions = new GRUP<FACT>(this, new FACT());
    GRUP<RACE> races = new GRUP<RACE>(this, new RACE());
    GRUP<MGEF> magicEffects = new GRUP<MGEF>(this, new MGEF());
    GRUP<ENCH> enchantments = new GRUP<ENCH>(this, new ENCH());
    GRUP<SPEL> spells = new GRUP<SPEL>(this, new SPEL());
    GRUP<ARMO> armors = new GRUP<ARMO>(this, new ARMO());
    GRUP<INGR> ingredients = new GRUP<INGR>(this, new INGR());
    GRUP<ALCH> alchemy = new GRUP<ALCH>(this, new ALCH());
    GRUP<WEAP> weapons = new GRUP<WEAP>(this, new WEAP());
    GRUP<AMMO> ammo = new GRUP<AMMO>(this, new AMMO());
    GRUP<NPC_> NPCs = new GRUP<NPC_>(this, new NPC_());
    GRUP<LVLN> leveledCreatures = new GRUP<LVLN>(this, new LVLN());
    GRUP<LVLI> leveledItems = new GRUP<LVLI>(this, new LVLI());
    GRUP<QUST> quests = new GRUP<QUST>(this, new QUST());
    GRUP<IMGS> imageSpaces = new GRUP<IMGS>(this, new IMGS());
    GRUP<FLST> formLists = new GRUP<FLST>(this, new FLST());
    GRUP<PERK> perks = new GRUP<PERK>(this, new PERK());
    GRUP<AVIF> actorValues = new GRUP<AVIF>(this, new AVIF());
    GRUP<ARMA> armatures = new GRUP<ARMA>(this, new ARMA());
    GRUP<ECZN> encounterZones = new GRUP<ECZN>(this, new ECZN());
    Map<SubStringPointer.Files, Map<Integer, Integer>> strings = new EnumMap<SubStringPointer.Files, Map<Integer, Integer>>(SubStringPointer.Files.class);
    private ArrayList<String> outStrings = new ArrayList<String>();
    private ArrayList<String> outDLStrings = new ArrayList<String>();
    private ArrayList<String> outILStrings = new ArrayList<String>();

    /**
     * Creates an empty Mod with the name and master flag set to match info.
     *
     * @see ModListing
     * @param info ModListing object containing name and master flag.
     */
    public Mod(ModListing info) {
	init(info);
	SPGlobal.getDB().add(this);
    }

    Mod(ModListing info, ByteBuffer headerInfo) throws BadRecord, DataFormatException, BadParameter {
	this(info, true);
	logSync("MOD", "Parsing header");
	header.parseData(headerInfo);
    }

    Mod(ModListing info, boolean temp) {
	init(info);
    }

    void deleteStringsFiles() {
	File STRINGS = new File(genStringsPath(SubStringPointer.Files.STRINGS));
	File DLSTRINGS = new File(genStringsPath(SubStringPointer.Files.STRINGS));
	File ILSTRINGS = new File(genStringsPath(SubStringPointer.Files.STRINGS));
	if (STRINGS.exists()) {
	    STRINGS.delete();
	}
	if (DLSTRINGS.exists()) {
	    DLSTRINGS.delete();
	}
	if (ILSTRINGS.exists()) {
	    ILSTRINGS.delete();
	}
    }

    final void init(ModListing info) {
	this.modInfo = info;
	this.setFlag(Mod_Flags.MASTER, info.getMasterTag());
	this.setFlag(Mod_Flags.STRING_TABLED, true);
	strings.put(SubStringPointer.Files.STRINGS, new TreeMap<Integer, Integer>());
	strings.put(SubStringPointer.Files.DLSTRINGS, new TreeMap<Integer, Integer>());
	strings.put(SubStringPointer.Files.ILSTRINGS, new TreeMap<Integer, Integer>());
	GRUPs.put(gameSettings.getContainedType(), gameSettings);
	GRUPs.put(keywords.getContainedType(), keywords);
	GRUPs.put(textures.getContainedType(), textures);
	factions.dateStamp = new byte[]{3, (byte) 0x3D, 2, 0};
	GRUPs.put(factions.getContainedType(), factions);
	GRUPs.put(races.getContainedType(), races);
	GRUPs.put(magicEffects.getContainedType(), magicEffects);
	enchantments.dateStamp = new byte[]{(byte) 0x12, (byte) 0x4A, (byte) 0x20, 0};
	GRUPs.put(enchantments.getContainedType(), enchantments);
	GRUPs.put(spells.getContainedType(), spells);
	GRUPs.put(armors.getContainedType(), armors);
	ingredients.dateStamp = new byte[]{1, (byte) 0x4C, (byte) 0x2F, 0};
	GRUPs.put(ingredients.getContainedType(), ingredients);
	alchemy.dateStamp = new byte[]{3, (byte) 0x3D, 2, 0};
	GRUPs.put(alchemy.getContainedType(), alchemy);
	GRUPs.put(weapons.getContainedType(), weapons);
	ammo.dateStamp = new byte[]{(byte) 0x0E, (byte) 0x4D, (byte) 0x2B, 0};
	GRUPs.put(ammo.getContainedType(), ammo);
	GRUPs.put(NPCs.getContainedType(), NPCs);
	GRUPs.put(leveledCreatures.getContainedType(), leveledCreatures);
	leveledItems.dateStamp = new byte[]{(byte) 0x1E, (byte) 0x4C, (byte) 0x23, 0};
	GRUPs.put(leveledItems.getContainedType(), leveledItems);
	GRUPs.put(quests.getContainedType(), quests);
	GRUPs.put(imageSpaces.getContainedType(), imageSpaces);
	GRUPs.put(formLists.getContainedType(), formLists);
	GRUPs.put(perks.getContainedType(), perks);
	actorValues.dateStamp = new byte[]{(byte) 0x1B, (byte) 0x4D, (byte) 0x2B, 0};
	GRUPs.put(actorValues.getContainedType(), actorValues);
	GRUPs.put(armatures.getContainedType(), armatures);
	GRUPs.put(encounterZones.getContainedType(), encounterZones);
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
	// If has an EDID match, grab old FormID
	FormID oldFormID = Consistency.getOldForm(edid);
	if (oldFormID != null) {
	    return oldFormID;
	} else {
	    //Find next open FormID
	    FormID possibleID = new FormID(header.HEDR.nextID++, getInfo());
	    while (!Consistency.requestID(possibleID)) {
		header.HEDR.nextID++;
		if (header.HEDR.nextID > 0xFFFFFF) {
		    if (!Consistency.cleaned) {
			header.HEDR.nextID = HEDR.firstAvailableID;
			Consistency.cleanConsistency();
			return getNextID(edid);
		    } else {
			SPGlobal.logError(getInfo().print(), "Ran out of available formids.");
			JOptionPane.showMessageDialog(null, "<html>The output patch ran out of available FormIDs.<br>"
				+ "Please contact Leviathan1753.</html>");
			
		    }
		}
		possibleID = new FormID(header.HEDR.nextID++, getInfo());
	    }
	    if (SPGlobal.debugConsistencyTies && SPGlobal.logging()) {
		SPGlobal.logSync(getName(), "Assigning new FormID " + possibleID + " for EDID " + edid);
	    }
	    return possibleID;
	}
    }

    void mergeMasters(Mod in) {
	for (ModListing m : in.header.masters) {
	    addMaster(m);
	}
	if (!in.equals(SPGlobal.getGlobalPatch())) {
	    addMaster(in.modInfo);
	}
	if (in.getInfo().equals(ModListing.skyrim)) {
	    addMaster(ModListing.update);
	}
    }

    void addMaster(ModListing input) {
	if (!getInfo().equals(input)) {
	    header.addMaster(input);
	}
    }

    /**
     * Makes a copy of the Major Record and loads it into the mod, giving a new
     * Major Record a FormID originating from the mod. This function also
     * automatically adds the new copied record to the mod. This makes two
     * separate records independent of each other.<br><br>
     *
     * CONSISTENCY NOTE: This functions appends "_DUP" to the end of the EDID,
     * and then a random number if that EDID already exists. It is suggested you
     * only use this function if you are only making one duplicate of the
     * record. For multiple duplicates, use the version with a specified EDID,
     * for better consistencyFile results<br><br>
     *
     * COMPILER NOTE: The record returned can only be determined by the compiler
     * to be a Major Record. You must cast it yourself to be the correct type of
     * major record.<br> ex. NPC_ newNPC = (NPC_) myPatch.makeCopy(otherNPC);
     *
     * @param m Major Record to make a copy of and add to the mod.
     * @return The copied record.
     */
    public MajorRecord makeCopy(MajorRecord m) {
	mergeMasters(SPGlobal.getDB().modLookup.get(m.getFormMaster()));
	m = m.copyOf(this);
	GRUPs.get(GRUP_TYPE.toRecord(m.getTypes()[0])).addRecord(m);
	return m;
    }

    /**
     * Makes a copy of the Major Record and loads it into the mod, giving a new
     * Major Record a FormID originating from the mod. This function also
     * automatically adds the new copied record to the mod. This makes two
     * separate records independent of each other.<br><br>
     *
     * COMPILER NOTE: The record returned can only be determined by the compiler
     * to be a Major Record. You must cast it yourself to be the correct type of
     * major record.<br> ex. NPC_ newNPC = (NPC_) myPatch.makeCopy(otherNPC);
     *
     * @param m Major Record to make a copy of and add to the mod.
     * @param newEDID EDID to assign to the new record. Make sure it's unique.
     * @return The copied record.
     */
    public MajorRecord makeCopy(MajorRecord m, String newEDID) {
	mergeMasters(SPGlobal.getDB().modLookup.get(m.getFormMaster()));
	m = m.copyOf(this, newEDID);
	GRUPs.get(GRUP_TYPE.toRecord(m.getTypes()[0])).addRecord(m);
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
	GRUP grup = GRUPs.get(GRUP_TYPE.toRecord(m.getTypes()[0]));
	if (!grup.contains(m.getForm())) {
	    grup.addRecord(m);
	    mergeMasters(SPGlobal.getDB().modLookup.get(m.getFormMaster()));
	}
    }

    final void addRecordSilent(MajorRecord m) {
	GRUPs.get(GRUP_TYPE.toRecord(m.getTypes()[0])).addRecordSilent(m);
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

    @Override
    public String toString() {
	return getName();
    }

    void standardizeMasters() {
	logSync("Standardizing", getName());
	for (GRUP g : GRUPs.values()) {
	    g.standardizeMasters();
	}
    }

    ArrayList<FormID> allFormIDs(boolean deep) {
	ArrayList<FormID> out = new ArrayList<FormID>();
	for (GRUP g : GRUPs.values()) {
	    out.addAll(g.allFormIDs(deep));
	}
	return out;
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
	ArrayList<GRUP_TYPE> grups = new ArrayList<GRUP_TYPE>();
	grups.addAll(Arrays.asList(grup_type));
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
	ArrayList<GRUP_TYPE> grups = new ArrayList<GRUP_TYPE>();
	grups.addAll(Arrays.asList(grup_types));
	if (!this.equals(rhs)) {
	    mergeMasters(rhs);
	    for (GRUP_TYPE t : GRUPs.keySet()) {
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
     * @throws BadRecord If duplicate EDIDs are found in the mod. This has been
     * deemed an unacceptable mod format, and is thrown to promote the
     * investigation and elimination of duplicate EDIDs.
     */
    public void export() throws IOException, BadRecord {
	export(SPGlobal.pathToData);
    }

    /**
     * Exports the mod to the path given by the parameter. (You shouldn't
     * include the mod name in the string)
     *
     * @param path
     * @throws IOException
     * @throws BadRecord If duplicate EDIDs are found in the mod. This has been
     * deemed an unacceptable mod format, and is thrown to promote the
     * investigation and elimination of duplicate EDIDs.
     */
    public void export(String path) throws IOException, BadRecord {
	File tmp = new File(SPGlobal.pathToInternalFiles + "tmp.esp");
	if (tmp.isFile()) {
	    tmp.delete();
	}
	File dest = new File(path + getName());
	File backup = new File(SPGlobal.pathToInternalFiles + getName() + ".bak");
	if (backup.isFile()) {
	    backup.delete();
	}
	export(new LExporter(tmp), this);

	Ln.moveFile(dest, backup, false);
	Ln.moveFile(tmp, dest, false);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException, BadRecord {
	int fullGRUPS = 0;
	for (GRUP g : GRUPs.values()) {
	    if (!g.isEmpty()) {
		fullGRUPS++;
	    }
	}
	if (srcMod.isFlag(Mod_Flags.STRING_TABLED)) {
	    fullGRUPS++;
	}
	SPProgressBarPlug.progress.reset();
	SPProgressBarPlug.progress.setMax(fullGRUPS, "Exporting " + srcMod);

	// Confirm all formID references are added as
	// masters, even if no actual major record from that
	// mod was added.
	for (FormID ID : srcMod.allFormIDs(true)) {
	    if (!ID.equals(FormID.NULL)) {
		this.addMaster(ID.getMaster());
	    }
	}
	standardizeMasters();

	header.setNumRecords(numRecords());
	if (logging()) {
	    SPGlobal.newSyncLog("Export - " + srcMod.getName() + ".txt");
	    SPGlobal.sync(true);
	    logSync(this.getName(), "Exporting " + header.HEDR.numRecords + " records.");
	}

	header.export(out, srcMod);

	int count = 1;
	for (GRUP g : GRUPs.values()) {
	    if (!g.isEmpty()) {
		SPProgressBarPlug.progress.setStatus(count++, fullGRUPS, "Exporting " + srcMod + ": " + g.getContainedType());
	    }
	    g.export(out, srcMod);
	    if (!g.isEmpty()) {
		SPProgressBarPlug.progress.incrementBar();
	    }
	}

	if (srcMod.isFlag(Mod_Flags.STRING_TABLED)) {
	    SPProgressBarPlug.progress.setStatus(count++, fullGRUPS, "Exporting " + srcMod + ": STRINGS files");
	    exportStringsFile(outStrings, SubStringPointer.Files.STRINGS);
	    exportStringsFile(outDLStrings, SubStringPointer.Files.DLSTRINGS);
	    exportStringsFile(outILStrings, SubStringPointer.Files.ILSTRINGS);
	    SPProgressBarPlug.progress.incrementBar();
	} else {
	    deleteStringsFiles();
	}
	out.close();
	SPProgressBarPlug.progress.setStatus(fullGRUPS, fullGRUPS, "Exporting " + srcMod + ": DONE");


	// Check if any duplicate EDIDS or FormIDS
	Set<String> edids = new HashSet<String>();
	Set<FormID> IDs = new HashSet<FormID>();
	boolean bad = false;
	for (GRUP g : GRUPs.values()) {
	    for (Object o : g.listRecords) {
		MajorRecord m = (MajorRecord) o;
		if (SPGlobal.logging()) {
		    SPGlobal.logSpecial(SPLogger.PrivateTypes.CONSISTENCY, "Export", "Exporting " + m);
		}
		if (edids.contains(m.getEDID())) {
		    SPGlobal.logError("EDID Check", "Error! Duplicate EDID " + m);
		    bad = true;
		} else {
		    edids.add(m.getEDID());
		}
		if (IDs.contains(m.getForm())) {
		    SPGlobal.logError("FormID Check", "Error! Duplicate FormID " + m);
		    bad = true;
		} else {
		    IDs.add(m.getForm());
		}
	    }
	}
	if (bad) {
	    throw new BadRecord("Duplicate EDIDs or FormIDs.  Check logs for a listing.");
	}
	
	if (Consistency.automaticExport) {
	    Consistency.export();
	}
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

    String genStringsPath(SubStringPointer.Files file) {
	return SPGlobal.pathToData + "Strings/" + getNameNoSuffix() + "_" + SPGlobal.language + "." + file;
    }

    void exportStringsFile(ArrayList<String> list, SubStringPointer.Files file) throws FileNotFoundException, IOException {
	int stringLength = 0;
	for (String s : list) {
	    stringLength += s.length() + 1;

	}
	int outLength = stringLength + 8 * list.size() + 8;
	LExporter out = new LExporter(genStringsPath(file));

	out.write(list.size());
	out.write(stringLength);

	int i = 1;  // To prevent indexing starting at 0
	int offset = 0;
	for (String s : list) {
	    out.write(i++);
	    out.write(offset);
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
	    GRUPs.get(GRUP_TYPE.toRecord(type)).parseData(data, masks.get(type));
	} else {
	    GRUPs.get(GRUP_TYPE.toRecord(type)).parseData(data);
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
	return header.flags.get(flag.value);
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
    public GRUP<LVLN> getLeveledCreatures() {
	return leveledCreatures;
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
     * @see GRUP
     * @return The GRUP containing Keyword records
     */
    public GRUP<FLST> getFormLists() {
	return formLists;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Magic Effect records
     */
    public GRUP<MGEF> getMagicEffects() {
	return magicEffects;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Alchemy records
     */
    public GRUP<ALCH> getAlchemy() {
	return alchemy;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Ingredient records
     */
    public GRUP<INGR> getIngredients() {
	return ingredients;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Ammo records
     */
    public GRUP<AMMO> getAmmo() {
	return ammo;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Faction records
     */
    public GRUP<FACT> getFactions() {
	return factions;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Game Setting records
     */
    public GRUP<GMST> getGameSettings() {
	return gameSettings;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Enchantments records
     */
    public GRUP<ENCH> getEnchantments() {
	return enchantments;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing Leveled Items records
     */
    public GRUP<LVLI> getLeveledItems() {
	return leveledItems;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing ActorValue records
     */
    public GRUP<AVIF> getActorValues() {
	return actorValues;
    }

    /**
     *
     * @see GRUP
     * @return The GRUP containing ActorValue records
     */
    public GRUP<ECZN> getEncounterZones() {
	return encounterZones;
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
	return this.getInfo().compareTo(rhs.getInfo());
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

    /**
     *
     * @return An iterator over all the GRUPs in the mod.
     */
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
	SubData ONAM = new SubData(Type.ONAM);
	private static final Type[] type = {Type.TES4};

	TES4() {
	    subRecords.add(HEDR);
	    subRecords.add(author);
	    subRecords.add(description);
	    subRecords.add(masters);
	    subRecords.add(ONAM);
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
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(flags.export(), 4);
	    out.write(fluff1);
	    out.write(fluff2);
	    out.write(fluff3);
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
	
	static int firstAvailableID = 3426;  // first available ID on empty CS plugins

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
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    out.write(version);
	    out.write(numRecords);
	    out.write(nextID);
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
	    nextID = firstAvailableID;  
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 12;
	}

	@Override
	ArrayList<FormID> allFormIDs(boolean deep) {
	    return new ArrayList<FormID>(0);
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
