package skyproc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import lev.Ln;

/**
 * An organized set of Mods forces load order. It is somewhat unnecessary at the
 * moment, but it will offer more powerful and unique functionality later on as
 * SkyProc develops.
 *
 * @author Justin Swanson
 */
public class SPDatabase implements Iterable<Mod> {

    static ArrayList<ModListing> activePlugins = new ArrayList<>();
    ArrayList<ModListing> addedPlugins = new ArrayList<>();
    Map<ModListing, Mod> modLookup = new TreeMap<>();

    /**
     * Creates a new SPDatabase container to load mods into.
     */
    SPDatabase() {
    }

    void clear() {
	activePlugins.clear();
	addedPlugins.clear();
	modLookup.clear();
    }

    /**
     * Returns the modindex matching the ModListing. -1 if no Mod contained
     * matches the ModListing.
     *
     * @param listing ModListing object to query for.
     * @return Mod's index in the load order.
     */
    public int modIndex(ModListing listing) {
	int counter = 0;
	for (Mod m : modLookup.values()) {
	    if (m.getName().equalsIgnoreCase(listing.print())) {
		return counter;
	    }
	    counter++;
	}
	return -1;
    }

    /**
     *
     * @param listing ModListing object to query for.
     * @return Mod matching the ModListing query.
     */
    public Mod getMod(ModListing listing) {
	if (listing.equals(SPGlobal.getGlobalPatch().getInfo())) {
	    return SPGlobal.getGlobalPatch();
	}
	return modLookup.get(listing);
    }

    /**
     *
     * @param listing ModListing object to query for.
     * @return True if database contains a matching Mod.
     */
    public boolean hasMod(ModListing listing) {
	return modLookup.containsKey(listing);
    }

    /**
     *
     * @param listing ModListing to remove.
     */
    public void removeMod(ModListing listing) {
	addedPlugins.remove(listing);
	modLookup.remove(listing);
    }

    /**
     * Returns the nth mod in the database.
     *
     * @param index
     * @return
     */
    public Mod getMod(int index) {
	return modLookup.get(addedPlugins.get(index));
    }

    /**
     *
     * @return All mod listings including ones on the load order + ones created while patching.
     */
    public ArrayList<ModListing> getMods() {
	return new ArrayList<>(addedPlugins);
    }

    /**
     *
     * @return All mod listings that appear on the load order.
     */
    public ArrayList<ModListing> getImportedModListings() {
	ArrayList<Mod> mods = new ArrayList<>(getImportedMods());
	ArrayList<ModListing> out = new ArrayList<>(mods.size());
	for (Mod m : mods) {
	    out.add(m.getInfo());
	}
	return out;
    }

    public ArrayList<Mod> getImportedMods() {
	ArrayList<Mod> out = new ArrayList<>(activePlugins.size());
	for (ModListing m : activePlugins) {
	    Mod mod = SPGlobal.getDB().getMod(m);
	    if (mod != null) {
		out.add(mod);
	    } else {
		SPGlobal.logError("Get Imported Mods", "Listing " + m + " returned a null Mod.");
	    }
	}
	return out;
    }

    /**
     * Exports the mod load order to "Files/Last Modlist.txt"<br>
     * Used for checking if patches are needed.
     * @param path
     * @throws IOException
     */
    public void exportModList(String path) throws IOException {
	File modListTmp = new File(SPGlobal.pathToInternalFiles + "Last Modlist Temp.txt");
	BufferedWriter writer = new BufferedWriter(new FileWriter(modListTmp));
	for (ModListing m : SPImporter.getActiveModList()) {
	    writer.write(m.toString() + "\n");
	}
	writer.close();

	File modList = new File(path);
	if (modList.isFile()) {
	    modList.delete();
	}

	Ln.moveFile(modListTmp, modList, false);
    }

    /**
     * Returns the number of mods currently loaded into this SPDatabase
     *
     * @return the number of mods
     */
    public int numMods() {
	return addedPlugins.size();
    }

    /**
     * Querys the Global Database and returns whether the FormID exists.<br>
     * NOTE: it is recommended you use the version that only searches in
     * specific GRUPs for speed reasons.
     *
     * @param query FormID to look for.
     * @return True if FormID exists in the database.
     */
    static public boolean queryMajor(FormID query) {
	return queryMajor(query, SPGlobal.getDB());
    }

    /**
     * Querys the Database and returns whether the FormID exists.<br> NOTE: it
     * is recommended you use the version that only searches in specific GRUPs
     * for speed reasons.
     *
     * @param query FormID to look for.
     * @param database Database to query
     * @return True if FormID exists in the database.
     */
    static public boolean queryMajor(FormID query, SPDatabase database) {
	return queryMajor(query, database, GRUP_TYPE.values());
    }

    /**
     * Querys the Global Database and returns whether the FormID exists. It
     * limits its search to the given GRUP types for speed reasons.
     *
     * @param query FormID to look for.
     * @param grup_types GRUPs to look in.
     * @return True if FormID exists in the database.
     */
    static public boolean queryMajor(FormID query, GRUP_TYPE... grup_types) {
	return queryMajor(query, SPGlobal.getDB(), grup_types);
    }

    /**
     * Querys the Database and returns whether the FormID exists. It limits its
     * search to the given GRUP types for speed reasons.
     *
     * @param query FormID to look for.
     * @param database Database to query
     * @param grup_types GRUPs to look in.
     * @return True if FormID exists in the database.
     */
    static public boolean queryMajor(FormID query, SPDatabase database, GRUP_TYPE... grup_types) {
	return getMajor(query, database, grup_types) != null;
    }

    /**
     * Querys the Global Database and returns the first record that matches the
     * FormID. It limits its search to the given GRUP types for speed reasons.
     *
     * @param query FormID to look for.
     * @return The first MajorRecord that matches, or MajorRecord.NULL if none
     * were found.
     */
    static public MajorRecord getMajor(FormID query) {
	return getMajor(query, SPGlobal.getDB());
    }

    /**
     * Querys the Database and returns the first record that matches the FormID.
     * It limits its search to the given GRUP types for speed reasons.
     *
     * @param query FormID to look for.
     * @param database Database to query
     * @return The first MajorRecord that matches, or MajorRecord.NULL if none
     * were found.
     */
    static public MajorRecord getMajor(FormID query, SPDatabase database) {
	return getMajor(query, database, GRUP_TYPE.values());
    }

    /**
     * Querys the Global Database and returns the first record that matches the
     * FormID. <br> NOTE: it is recommended you use the version that only
     * searches in specific GRUPs for speed reasons.
     *
     * @param query FormID to look for.
     * @param grup_types GRUPs to look in.
     * @return The first MajorRecord that matches, or null if none were found.
     */
    static public MajorRecord getMajor(FormID query, GRUP_TYPE... grup_types) {
	return getMajor(query, SPGlobal.getDB(), grup_types);
    }

    /**
     * Querys the Database and returns the record that matches the FormID. The
     * version in the latest mod in the load order will be returned. <br> NOTE:
     * it is recommended you use the version that only searches in specific
     * GRUPs for speed reasons.
     *
     * @param query FormID to look for.
     * @param database Database to query
     * @param grup_types GRUPs to look in.
     * @return The first MajorRecord that matches, or null if none were found.
     */
    static public MajorRecord getMajor(FormID query, SPDatabase database, GRUP_TYPE... grup_types) {
	if (query != null && query.getMaster() != null) {
	    Iterator<Mod> revOrder = database.reverseIter();
	    while (revOrder.hasNext()) {
		Mod next = revOrder.next();
		for (GRUP_TYPE g : grup_types) {
		    GRUP grup = next.GRUPs.get(g);
		    if (grup.mapRecords.containsKey(query)) {
			return (MajorRecord) grup.mapRecords.get(query);
		    }
		}
	    }
	}
	return null;
    }

    /**
     * Adds a mod to the database. If there is a mod with a matching ModListing
     * already in the database, it will be replaced.
     *
     * @param m Mod to add to the database.
     */
    public void add(Mod m) {
	removeMod(m.getInfo());
	addedPlugins.add(m.getInfo());
	modLookup.put(m.getInfo(), m);
    }

    /**
     * Adds a set of mods to the database. If there are any mods with matching
     * ModListings already in the database, they will be replaced
     *
     * @param modSet Set of mods to add into the database.
     */
    public void add(Set<Mod> modSet) {
	for (Mod m : modSet) {
	    add(m);
	}
    }

    /**
     *
     * @return SDBIterator of all the mods in the database, in load order.
     * "Winning" mods will be last.
     */
    @Override
    public Iterator<Mod> iterator() {
	return modLookup.values().iterator();
    }

    /**
     *
     * @return An iterator that goes from the top of the load order down to the
     * lowest priority at the end.
     */
    public Iterator<Mod> reverseIter() {
	Iterator<Mod> iter = iterator();
	ArrayList<Mod> outList = new ArrayList<Mod>(modLookup.size());
	while (iter.hasNext()) {
	    outList.add(0, iter.next());
	}
	return outList.iterator();
    }
}
