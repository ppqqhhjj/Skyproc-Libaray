package skyproc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A record fully describing and specifying a perk, including level up perks, as
 * well as more obscure hidden perks related to quests and NPCs.
 *
 * @author Justin Swanson
 */
public class PERK extends MajorRecordDescription {

    /**
     * A script package containing scripts and their properties
     */
    public ScriptPackage scripts = new ScriptPackage();
    SubList<CTDA> CTDAs = new SubList<CTDA>(new CTDA());
    SubData DATA = new SubData(Type.DATA);
    SubForm NNAM = new SubForm(Type.NNAM);
    SubString ICON = new SubString(Type.ICON, true);
    SubList<PRKEPackage> perkSections = new SubList<PRKEPackage>(new PRKEPackage());
    private static Type[] type = {Type.PERK};

    PERK() {
        super();
        subRecords.remove(Type.FULL);  // Different order
        subRecords.remove(Type.DESC);

        subRecords.add(scripts);
        subRecords.add(FULL);
        subRecords.add(description);
        subRecords.add(CTDAs);
        subRecords.add(DATA);
        subRecords.add(NNAM);
        subRecords.add(ICON);
        subRecords.add(perkSections);
    }

    @Override
    Type[] getTypes() {
        return type;
    }

    @Override
    Record getNew() {
        return new PERK();
    }

    @Override
    ArrayList<FormID> allFormIDs(boolean deep) {
        ArrayList<FormID> out = super.allFormIDs(deep);
        out.addAll(perkSections.allFormIDs(deep));
        return out;
    }

    // Custom importSubRecords because Bethesda reused header titles in the same record.
    @Override
    void importSubRecords(LShrinkArray in, Mask mask) throws BadRecord, DataFormatException, BadParameter {
        Type nextType;
        Boolean insidePRKE = false;
        while (!in.isEmpty()) {
            nextType = getNextType(in);
            if (nextType == Type.PRKE) {
                insidePRKE = true;
            } else if (nextType == Type.PRKF) {
                insidePRKE = false;
            }
            if (subRecords.contains(nextType)) {

                switch (getNextType(in)) {
                    case DATA:
                        if (DATA.isValid()) {
                            perkSections.parseData(perkSections.extractRecordData(in));
                            break;
                        }
                    case CTDA:
                        if (insidePRKE) {
                            perkSections.parseData(perkSections.extractRecordData(in));
                            break;
                        }
                    default:
                        subRecords.importSubRecord(in, mask);
                }
            } else {
                throw new BadRecord(getTypes()[0].toString() + " doesn't know what to do with a " + nextType.toString() + " record.");
            }
        }
    }

    //Contains everything between PRKE and PRKF.
    static class PRKEPackage extends SubRecord {

        private static Type[] types = {Type.PRKE, Type.PRKF, Type.PRKC, Type.CIS2, Type.CIS1, Type.EPFT, Type.EPFD, Type.EPF2, Type.EPF3};
        SubData PRKE = new SubData(Type.PRKE);
        SubData PRKF = new SubData(Type.PRKF);
        SubRecord subPackage;
        PerkType perkType;

        PRKEPackage() {
            super(types);
            PRKF.forceExport(true);
        }

        PRKEPackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            PRKE.export(out, srcMod);
            subPackage.export(out, srcMod);
            PRKF.export(out, srcMod);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            switch (getNextType(in)) {
                case PRKE:
                    PRKE.parseData(in);
                    perkType = PerkType.values()[PRKE.getData()[0]];
                    switch (perkType) {
                        case QUEST:
                            subPackage = new SubFormData(Type.DATA);
                            break;
                        case ABILITY:
                            subPackage = new SubForm(Type.DATA);
                            break;
                        case COMPLEX:
                            subPackage = new PRKEComplexSubPackage();
                            break;
                    }
                    break;
                case PRKF:
                    PRKF.parseData(in);
                    break;
                case PRKC:
                    int sdf = 0;
                default:
                    subPackage.parseData(in);
                    break;
            }
        }

        @Override
        SubRecord getNew(Type type) {
            return new PRKEPackage();
        }

        @Override
        public void clear() {
            PRKE.clear();
            PRKF.clear();
            subPackage.clear();
        }

        @Override
        Boolean isValid() {
            return PRKE.isValid() && subPackage.isValid();
        }

        @Override
        int getHeaderLength() {
            return 0;
        }

        @Override
        int getContentLength(Mod srcMod) {
            return PRKE.getTotalLength(srcMod) + PRKF.getTotalLength(srcMod)
                    + subPackage.getTotalLength(srcMod);
        }

        @Override
        ArrayList<FormID> allFormIDs(boolean deep) {
            if (deep) {
                return subPackage.allFormIDs(deep);
            } else {
                return new ArrayList<FormID>(0);
            }
        }
    }

    //If PRKE package is type complex, this contains
    //DATA, PRKC packages, and EPFT records
    static class PRKEComplexSubPackage extends SubRecord {

        private static final Type[] types = {Type.DATA, Type.PRKC, Type.CTDA, Type.CIS1, Type.CIS2, Type.EPFT, Type.EPFD, Type.EPF2, Type.EPF3};
        SubData DATA = new SubData(Type.DATA);
        SubList<PRKCpackage> PRKCs = new SubList<PRKCpackage>(new PRKCpackage());
        SubData EPFT = new SubData(Type.EPFT);
        SubRecord EPFD;
        SubData EPF2 = new SubData(Type.EPF2);
        SubData EPF3 = new SubData(Type.EPF3);

        PRKEComplexSubPackage() {
            super(types);
        }

        PRKEComplexSubPackage(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new PRKEComplexSubPackage();
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            DATA.export(out, srcMod);
            PRKCs.export(out, srcMod);
            EPFT.export(out, srcMod);
            EPF2.export(out, srcMod);
            EPF3.export(out, srcMod);
            EPFD.export(out, srcMod);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            switch (getNextType(in)) {
                case DATA:
                    DATA.parseData(in);
                    break;
                case EPFT:
                    EPFT.parseData(in);
                    if (EPFT.toInt() >= 3 && EPFT.toInt() <= 5) {
                        EPFD = new SubForm(Type.EPFD);
                    } else {
                        EPFD = new SubData(Type.EPFD);
                    }
                    break;
                case EPF2:
                    EPF2.parseData(in);
                    break;
                case EPF3:
                    EPF3.parseData(in);
                    break;
                case EPFD:
                    EPFD.parseData(in);
                    break;
                default:
                    PRKCs.parseData(in);
                    break;
            }
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        Boolean isValid() {
            return true;
        }

        @Override
        int getHeaderLength() {
            return 0;
        }

        @Override
        int getContentLength(Mod srcMod) {
            return DATA.getTotalLength(srcMod)
                    + PRKCs.getTotalLength(srcMod)
                    + EPFT.getTotalLength(srcMod)
                    + EPFD.getTotalLength(srcMod)
                    + EPF2.getTotalLength(srcMod)
                    + EPF3.getTotalLength(srcMod);
        }

        @Override
        ArrayList<FormID> allFormIDs(boolean deep) {
            if (deep) {
                return EPFD.allFormIDs(deep);
            } else {
                return new ArrayList<FormID>(0);
            }
        }
    }

    //A PRKC package contains a PRKC record and
    //a number of CTDA packages
    static class PRKCpackage extends SubRecord {

        private static Type[] types = {Type.PRKC, Type.CTDA, Type.CIS1, Type.CIS2};
        SubData PRKC = new SubData(Type.PRKC);
        SubList<Condition> CTDAs = new SubList<Condition>(new Condition());

        PRKCpackage() {
            super(types);
        }

        @Override
        void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            switch (getNextType(in)) {
                case PRKC:
                    PRKC.parseData(in);
                    break;
                default:
                    CTDAs.parseData(in);
                    break;
            }
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            PRKC.export(out, srcMod);
            CTDAs.export(out, srcMod);
        }

        @Override
        int getSizeLength() {
            return 0;
        }

        @Override
        int getHeaderLength() {
            return 0;
        }

        @Override
        SubRecord getNew(Type type) {
            return new PRKCpackage();
        }

        @Override
        public void clear() {
            PRKC.clear();
            CTDAs.clear();
        }

        @Override
        Boolean isValid() {
            return PRKC.isValid()
                    && CTDAs.isValid();
        }

        @Override
        int getContentLength(Mod srcMod) {
            return PRKC.getTotalLength(srcMod)
                    + CTDAs.getTotalLength(srcMod);
        }

        @Override
        ArrayList<FormID> allFormIDs(boolean deep) {
            return new ArrayList<FormID>(0);
        }
    }

    public static class CTDA extends SubRecord implements Serializable {

        boolean valid = false;
        private int comparison = 0;
        private byte[] fluff = new byte[3];
        private float comparisonValue = 0;
        private int comparisonFunction = 0;
        private byte[] fluff2 = new byte[2];
        private int actorValue = 0;
        private byte[] fluff3 = new byte[16];

        CTDA() {
            super(Type.CTDA);
            valid = false;
        }

        CTDA(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            this();
            parseData(in);
        }

        @Override
        SubRecord getNew(Type type) {
            return new CTDA();
        }

        @Override
        final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            super.parseData(in);

            comparison = in.extractInt(1);
            fluff = in.extract(3);
            comparisonValue = in.extractFloat();
            comparisonFunction = in.extractInt(2);
            fluff2 = in.extract(2);
            actorValue = in.extractInt(4);
            fluff3 = in.extract(16);

            if (logging()) {
                logSync("", "CTDA record: ");
                logSync("", "  " + "Comparison: " + comparison + ", Comparison Value: " + comparisonValue);
                logSync("", "  " + "Comparison Function: " + comparisonFunction + ", Actor Value: " + actorValue);
            }

            valid = true;
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            super.export(out, srcMod);
            if (isValid()) {
                out.write(comparison, 1);
                out.write(fluff);
                out.write(comparisonValue);
                out.write(comparisonFunction, 2);
                out.write(fluff2);
                out.write(actorValue, 4);
                out.write(fluff3);
            }
        }

        @Override
        public void clear() {
        }

        @Override
        Boolean isValid() {
            return valid;
        }

        @Override
        int getContentLength(Mod srcMod) {
            if (isValid()) {
                return 32;
            } else {
                return 0;
            }
        }

        @Override
        ArrayList<FormID> allFormIDs(boolean deep) {
            return new ArrayList<FormID>(0);

        }
    }

    public int getSizeOfCTDAs() {
        return CTDAs.size();
    }

    public int getActorValue(int index) {
        return CTDAs.get(index).actorValue;
    }

    public void setActorValue(int actorValue, int index) {
        this.CTDAs.get(index).actorValue = actorValue;
    }

    public int getComparison(int index) {
        return CTDAs.get(index).comparison;
    }

    public void setComparison(int comparison, int index) {
        this.CTDAs.get(index).comparison = comparison;
    }

    public int getComparisonFunction(int index) {
        return CTDAs.get(index).comparisonFunction;
    }

    public void setComparisonFunction(int comparisonFunction, int index) {
        this.CTDAs.get(index).comparisonFunction = comparisonFunction;
    }

    public float getComparisonValue(int index) {
        return CTDAs.get(index).comparisonValue;
    }

    public void setComparisonValue(float comparisonValue, int index) {
        this.CTDAs.get(index).comparisonValue = comparisonValue;
    }

    public FormID getNextPerkForm() {
        return NNAM.getForm();
    }

    enum PerkType {

        QUEST, ABILITY, COMPLEX;
    }
}
