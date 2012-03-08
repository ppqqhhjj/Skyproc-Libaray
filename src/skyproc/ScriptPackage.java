/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A script package attached to Major Records.
 * @author Justin Swanson
 */
public class ScriptPackage extends SubRecord {

    int version = 5;
    int unknown = 2;
    ArrayList<VMADscript> scripts = new ArrayList<VMADscript>();

    ScriptPackage() {
        super(Type.VMAD);
    }

    @Override
    SubRecord getNew(Type type) {
        return new ScriptPackage();
    }

    @Override
    public void clear() {
        scripts.clear();
    }

    @Override
    Boolean isValid() {
        return scripts.size() > 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
        int out = 6;
        for (VMADscript s : scripts) {
            out += s.getTotalLength(srcMod);
        }
        return out;
    }

    @Override
    void standardizeMasters(Mod srcMod) {
        super.standardizeMasters(srcMod);
        for (VMADscript s : scripts) {
            s.standardizeMasters(srcMod);
        }
    }

    @Override
    void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
        super.parseData(in);
        version = in.extractInt(2);
        unknown = in.extractInt(2);
        int scriptCount = in.extractInt(2);
        if (logging()) {
            logSync(toString(), "Importing VMAD record with " + scriptCount + " scripts.  Version: " + version + ", unknown: " + unknown);
        }
        for (int i = 0; i < scriptCount; i++) {
            scripts.add(new VMADscript(in));
        }
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
        super.export(out, srcMod);
        if (isValid()) {
            out.write(version, 2);
            out.write(unknown, 2);
            out.write(scripts.size(), 2);
            for (VMADscript s : scripts) {
                s.export(out, srcMod);
            }
        }
    }

    static class VMADscript extends Record implements Iterable<ScriptProperty> {

        StringNonNull name = new StringNonNull();
        int unknown = 0;
        ArrayList<ScriptProperty> properties = new ArrayList<ScriptProperty>();
        private static final Type[] type = {Type.VMAD};

        VMADscript() {
        }

        VMADscript(String name) {
            this.name.set(name);
        }

        VMADscript(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            parseData(in);
        }

        @Override
        final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            name.set(in.extractString(in.extractInt(2)));
            unknown = in.extractInt(1);
            int propertyCount = in.extractInt(2);
            if (logging()) {
                logSync("VMAD", "  Script " + name.toString() + " with " + propertyCount + " properties. Unknown: " + unknown);
            }
            for (int i = 0; i < propertyCount; i++) {
                properties.add(new ScriptProperty(in));
            }
//            rest = in.getAll();
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            name.export(out, srcMod);
            out.write(unknown, 1);
            out.write(properties.size(), 2);
            for (ScriptProperty p : properties) {
                p.export(out, srcMod);
            }
//            out.write(rest, 0);
        }

        void standardizeMasters(Mod srcMod) {
            for (ScriptProperty s : properties) {
                s.standardizeMasters(srcMod);
            }
        }

        @Override
        Boolean isValid() {
            return name != null;
        }

        @Override
        public String toString() {
            return "VMADscript " + name;
        }

        @Override
        public String print() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        Type[] getTypes() {
            return type;
        }

        @Override
        Record getNew() {
            return new VMADscript();
        }

        @Override
        int getHeaderLength() {
            return 0;
        }

        @Override
        int getSizeLength() {
            return 0;
        }

        @Override
        int getFluffLength() {
            return 0;
        }

        @Override
        int getContentLength(Mod srcMod) {
            int out = name.getTotalLength(srcMod) + 3;
            for (ScriptProperty p : properties) {
                out += p.getTotalLength(srcMod);
            }
//            out += rest.length;
            return out;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final VMADscript other = (VMADscript) obj;
            if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        @Override
        public Iterator<ScriptProperty> iterator() {
            return properties.iterator();
        }
    }

    static class ScriptProperty extends Record {

        StringNonNull name = new StringNonNull();
        int type = 0;
        int unknown = 1;
        int size = 0;  // 0 unless it's a string property, in which case + 2
        byte[] data;
        FormID id = new FormID();
        private static final Type[] types = {Type.VMAD};

        ScriptProperty() {
        }

        ScriptProperty(String name) {
            this.name.set(name);
        }

        ScriptProperty(String name, boolean b) {
            this(name);
            type = ScriptPropType.Boolean.ordinal();
            data = new byte[1];
            data[0] = b ? (byte)1 : 0;
        }

        ScriptProperty(String name, int in) {
            this(name);
            type = ScriptPropType.Integer.ordinal();
            data = Ln.toByteArray(in, 4);
        }

        ScriptProperty(String name, FormID id) {
            this(name);
            type = ScriptPropType.FormID.ordinal();
            this.id = id;
            data = new byte[4];
            data[2] = -1;
            data[3] = -1;
        }

        ScriptProperty(String name, float in) {
            this(name);
            type = ScriptPropType.Float.ordinal();
            data = Ln.toByteArray(Float.floatToIntBits(in), 4);
        }

        ScriptProperty(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            parseData(in);
        }

        void standardizeMasters(Mod srcMod) {
            id.standardize(srcMod);
        }

        @Override
        final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
            name.set(in.extractString(in.extractInt(2)));
            type = in.extractInt(1);
            unknown = in.extractInt(1);
            if (logging()) {
                logSync("VMAD", "    Property " + name + " with type " + type + ", unknown: " + unknown);
            }
            switch (type) {
                case 1:  // type1 object reference?
                    data = in.extract(4);
                    id.setInternal(in.extract(4));
                    break;
                case 2:  // String
                    data = in.extract(in.extractInt(2));
                    size += 2;
                    break;
                case 3:  // int32
                    data = in.extract(4);
                    break;
                case 4:  // float
                    data = in.extract(4);
                    break;
                case 5:  // bool
                    data = in.extract(1);
                    break;
                default:
                    if (logging()) {
                        logSync("VMAD", "    Importing property with UNKNOWN TYPE!");
                        logError("VMAD", "    Importing property with UNKNOWN TYPE!");
                    }
                    in.extractInts(1000);  // break extraction to exclude NPC from database
            }
            if (logging() && type != 2) {
                logSync("VMAD", "      Data: " + Ln.printHex(data, true, false));
            } else if (logging()) {
                logSync("VMAD", "      Data: " + Ln.arrayToString(data));
            }
        }

        @Override
        void export(LExporter out, Mod srcMod) throws IOException {
            name.export(out, srcMod);
            out.write(type, 1);
            out.write(unknown, 1);
            switch (type) {
                case 2:
                    out.write(data.length, 2);
                    break;
            }
            out.write(data, 0);
            if (type == 1) {
                id.export(out);
            }
        }

        @Override
        Boolean isValid() {
            return name != null;
        }

        @Override
        public String toString() {
            return "VMADproperty " + name;
        }

        @Override
        public String print() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        Type[] getTypes() {
            return types;
        }

        @Override
        Record getNew() {
            return new ScriptProperty();
        }

        @Override
        int getHeaderLength() {
            return 0;
        }

        @Override
        int getSizeLength() {
            return 0;
        }

        @Override
        int getFluffLength() {
            return 0;
        }

        @Override
        int getContentLength(Mod srcMod) {
            int out = name.getTotalLength(srcMod) + 2;
            if (type == 1) {
                out += id.getContentLength();
            }
            return out + data.length + size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ScriptProperty other = (ScriptProperty) obj;
            if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        enum ScriptPropType {

            Unknown,
            FormID,
            String,
            Integer,
            Float,
            Boolean
        }
    }

    // Get set
    /**
     *
     * @return List of the names of scripts attached.
     */
    public ArrayList<String> getScripts() {
        ArrayList<String> out = new ArrayList<String>(scripts.size());
        for (VMADscript s : scripts) {
            out.add(s.name.data);
        }
        return out;
    }

    /**
     *
     * @param scriptName Adds an empty script with the given name.
     */
    public void addScript(String scriptName) {
        scripts.add(new VMADscript(scriptName));
    }

    /**
     *
     * @param scriptName The name of the script to get properties of.
     * @return A list of property names associated with the script.
     */
    public ArrayList<String> getProperties(String scriptName) {
        VMADscript script = getScript(scriptName);
        ArrayList<String> out = new ArrayList<String>(script.properties.size());
        for (ScriptProperty p : script) {
            out.add(p.name.data);
        }
        return out;
    }

    VMADscript getScript(String scriptName) {
        return scripts.get(scripts.indexOf(new VMADscript(scriptName)));
    }

    /**
     * 
     * @param scriptName Script name to query
     * @return True if package has a script with that name.
     */
    public boolean hasScript(String scriptName) {
        return scripts.contains(new VMADscript(scriptName));
    }

    /**
     * 
     * @param scriptName Script name to query inside for property
     * @param propertyName Property name to query
     * @return True if script exists with that property name
     */
    public boolean hasProperty(String scriptName, String propertyName) {
        return hasScript(scriptName) && getScript(scriptName).properties.contains(new ScriptProperty(propertyName));
    }

    /**
     * 
     * @param scriptName Script name to remove, if present.
     */
    public void removeScript(String scriptName) {
        if (hasScript(scriptName)) {
            scripts.remove(new VMADscript(scriptName));
        }
    }

    /**
     * 
     * @param scriptName Script to target
     * @param propertyName Property to remove, if present
     */
    public void removeProperty(String scriptName, String propertyName) {
        if (hasProperty(scriptName, propertyName)) {
            getScript(scriptName).properties.remove(new ScriptProperty(propertyName, 0));
        }
    }

    /**
     * Adds a boolean property to the script, does not check for duplicates.
     * @param scriptName Script name to add property to
     * @param propertyName Name of the boolean property to add
     * @param booleanProperty What to set the boolean property to.
     */
    void addProperty(String scriptName, String propertyName, boolean booleanProperty) {
        getScript(scriptName).properties.add(new ScriptProperty(propertyName, booleanProperty));
    }

    /**
     * Adds an integer property to the script, does not check for duplicates.
     * @param scriptName Script name to add property to
     * @param propertyName Name of the integer property to add
     * @param integerProperty What to set the integer property to.
     */
    void addProperty(String scriptName, String propertyName, int integerProperty) {
        getScript(scriptName).properties.add(new ScriptProperty(propertyName, integerProperty));
    }

    /**
     * Adds a FormID property to the script, does not check for duplicates.
     * @param scriptName Script name to add property to
     * @param propertyName Name of the FormID property to add
     * @param idProperty What to set the FormID property to.
     */
    void addProperty(String scriptName, String propertyName, FormID idProperty) {
        getScript(scriptName).properties.add(new ScriptProperty(propertyName, idProperty));
    }

    /**
     * Adds a float property to the script, does not check for duplicates.
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    void addProperty(String scriptName, String propertyName, Float floatProperty) {
        getScript(scriptName).properties.add(new ScriptProperty(propertyName, floatProperty));
    }

    /**
     * Adds a boolean property to the script, checks for duplicates.
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param booleanProperty Boolean value to assign to property
     */
    public void setProperty(String scriptName, String propertyName, boolean booleanProperty) {
        removeProperty(scriptName, propertyName);
        addProperty(scriptName, propertyName, booleanProperty);
    }

    /**
     * Adds a int property to the script, checks for duplicates.
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param integerProperty Integer value to assign to property
     */
    public void setProperty(String scriptName, String propertyName, int integerProperty) {
        removeProperty(scriptName, propertyName);
        addProperty(scriptName, propertyName, integerProperty);
    }

    /**
     * Adds a FormID property to the script, checks for duplicates.
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param idProperty FormID value to assign to property
     */
    public void setProperty(String scriptName, String propertyName, FormID idProperty) {
        removeProperty(scriptName, propertyName);
        addProperty(scriptName, propertyName, idProperty);
    }

    /**
     * Adds a float property to the script, checks for duplicates.
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    public void setProperty(String scriptName, String propertyName, float floatProperty) {
        removeProperty(scriptName, propertyName);
        addProperty(scriptName, propertyName, floatProperty);
    }
}