package skyproc;

import java.util.*;
import skyproc.exceptions.Uninitialized;

/**
 * An organized set of Mods forces load order. It is somewhat unnecessary at the
 * moment, but it will offer more powerful and unique functionality later on as
 * SkyProc develops.
 *
 * @author Justin Swanson
 */
public class SPDatabase implements Iterable<Mod> {

    static ArrayList<ModListing> activePlugins = new ArrayList<ModListing>();
    ArrayList<ModListing> plugins = new ArrayList<ModListing>();
    Map<ModListing, Mod> modLookup = new TreeMap<ModListing, Mod>();
    SPExceptionDbInterface exceptionsDb = new SPExceptionDbInterface<SPExceptionDbInterface.NullException>() {

        @Override
        public SPExceptionDbInterface.NullException getException(Record record) {
            return SPExceptionDbInterface.NullException.NULL;
        }

        @Override
        public Boolean is(Record record, SPExceptionDbInterface.NullException exceptionType) {
            return false;
        }
    };

    /**
     * Creates a new SPDatabase container to load mods into.
     */
    SPDatabase() {
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
        if (modLookup.containsKey(listing)) {
	    plugins.remove(listing);
            modLookup.remove(listing);
        }
    }

    /**
     * Querys the Global Database and returns whether the FormID exists.<br>
     * NOTE: it is recommended you use the version that only searches in
     * specific GRUPs for speed reasons.
     *
     * @param query FormID to look for.
     * @return True if FormID exists in the database.
     * @throws Uninitialized If the Global Database is not initialized.
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
     * @throws Uninitialized If the Global Database is not initialized.
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
     * Querys the Database and returns the first record that matches the FormID.
     * <br> NOTE: it is recommended you use the version that only searches in
     * specific GRUPs for speed reasons.
     *
     * @param query FormID to look for.
     * @param database Database to query
     * @param grup_types GRUPs to look in.
     * @return The first MajorRecord that matches, or null if none were found.
     */
    static public MajorRecord getMajor(FormID query, SPDatabase database, GRUP_TYPE... grup_types) {
        MajorRecord out;
        if (query.getMaster() != null) {
            Mod m = database.modLookup.get(query.getMaster());
            if (m != null) {
                for (GRUP_TYPE g : grup_types) {
                    GRUP grup = m.GRUPs.get(Type.toRecord(g));
                    if (grup.mapRecords.containsKey(query)) {
                        return (MajorRecord) grup.mapRecords.get(query);
                    }
//                Iterator<MajorRecord> iter = m.GRUPs.get(Type.toRecord(g)).iterator();
//                while (iter.hasNext()) {
//                    if ((out = iter.next()).getForm().equals(query)) {
//                        return out;
//                    }
//                }
                }
            }
        }
        return null;
    }

    void setExceptionDbInterface(SPExceptionDbInterface in) {
        exceptionsDb = in;
    }

    void fetchExceptions() {
        for (Mod m : modLookup.values()) {
            m.fetchExceptions(this);
        }
    }

    Enum getException(Record in) throws Uninitialized {
        return exceptionsDb.getException(in);
    }

    /**
     * Adds a mod to the database. If there is a mod with a matching ModListing
     * already in the database, it will be replaced.
     *
     * @param m Mod to add to the database.
     */
    public void add(Mod m) {
        removeMod(m.getInfo());
	plugins.add(m.getInfo());
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
     */
    @Override
    public Iterator<Mod> iterator() {
        return modLookup.values().iterator();
    }
}
