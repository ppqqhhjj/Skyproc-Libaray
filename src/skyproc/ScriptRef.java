/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A script reference to be added/found in a major record's ScriptPackage object.
 *
 * @author Justin Swanson
 */
public class ScriptRef extends Record implements Iterable<String> {

    StringNonNull name = new StringNonNull();
    int unknown = 0;
    ArrayList<ScriptProperty> properties = new ArrayList<ScriptProperty>();
    private static final Type[] type = {Type.VMAD};

    ScriptRef() {
    }

    /**
     * Creates a script reference with the given name. Adding this to a major
     * record's ScriptPackage will attach the script to the record. Of course, there
     * must be a script with that name in the Data/Scripts/ folder for it to
     * actually do anything in-game.
     *
     * @param name
     */
    public ScriptRef(String name) {
	this.name.set(name);
    }

    ScriptRef(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
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
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	name.export(out, srcMod);
	out.write(unknown, 1);
	out.write(properties.size(), 2);
	for (ScriptProperty p : properties) {
	    p.export(out, srcMod);
	}
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
	return print();
    }

    @Override
    public String print() {
	return name.toString();
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new ScriptRef();
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
	final ScriptRef other = (ScriptRef) obj;
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
    public Iterator<String> iterator() {
	return getProperties().iterator();
    }

    // Get/set
    public void setName(String name) {
	this.name.set(name);
    }

    public String getName () {
	return name.print();
    }

    /**
     *
     * @return A copy of all the property names in the script.
     */
    public ArrayList<String> getProperties() {
	ArrayList<String> out = new ArrayList<String>(properties.size());
	for (ScriptProperty p : properties) {
	    out.add(p.name.data);
	}
	return out;
    }

    void removeProperty(ScriptProperty property) {
	properties.remove(property);
    }

    /**
     * Removes a property with the given name from the script, if one exists.
     * @param propertyName
     */
    public void removeProperty(String propertyName) {
	removeProperty(new ScriptProperty(propertyName, 0));
    }

    /**
     * Adds a boolean property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to
     * @param propertyName Name of the boolean property to add
     * @param booleanProperty What to set the boolean property to.
     */
    void addProperty(String propertyName, boolean booleanProperty) {
	properties.add(new ScriptProperty(propertyName, booleanProperty));
    }

    /**
     * Adds an integer property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to
     * @param propertyName Name of the integer property to add
     * @param integerProperty What to set the integer property to.
     */
    void addProperty(String propertyName, int integerProperty) {
	properties.add(new ScriptProperty(propertyName, integerProperty));
    }

    /**
     * Adds a FormID property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to
     * @param propertyName Name of the FormID property to add
     * @param idProperty What to set the FormID property to.
     */
    void addProperty(String propertyName, FormID idProperty) {
	properties.add(new ScriptProperty(propertyName, idProperty));
    }

    /**
     * Adds a float property to the script, does not check for duplicates.
     *
     * @param scriptName Script name to add property to (must already exist)
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    void addProperty(String propertyName, Float floatProperty) {
	properties.add(new ScriptProperty(propertyName, floatProperty));
    }

    /**
     * Adds a boolean property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param booleanProperty Boolean value to assign to property
     */
    public void setProperty(String propertyName, boolean booleanProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, booleanProperty);
    }

    /**
     * Adds a int property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param integerProperty Integer value to assign to property
     */
    public void setProperty(String propertyName, int integerProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, integerProperty);
    }

    /**
     * Adds a FormID property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param idProperty FormID value to assign to property
     */
    public void setProperty(String propertyName, FormID idProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, idProperty);
    }

    /**
     * Adds a float property to the script, checks for duplicates.
     *
     * @param propertyName Property name to add
     * @param floatProperty Float value to assign to property
     */
    public void setProperty(String propertyName, float floatProperty) {
	removeProperty(propertyName);
	addProperty(propertyName, floatProperty);
    }
}