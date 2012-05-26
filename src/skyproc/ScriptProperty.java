/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
class ScriptProperty extends Record {

    StringNonNull name = new StringNonNull();
    ScriptPropertyType type = ScriptPropertyType.Unknown;
    int unknown = 1;
    byte[] data;
    FormID id;
    private static final Type[] types = {Type.VMAD};

    ScriptProperty() {
    }

    ScriptProperty(String name) {
	this.name.set(name);
    }

    public ScriptProperty(String name, boolean b) {
	this(name);
	type = ScriptPropertyType.Boolean;
	data = new byte[1];
	data[0] = b ? (byte) 1 : 0;
    }

    public ScriptProperty(String name, int in) {
	this(name);
	type = ScriptPropertyType.Integer;
	data = Ln.toByteArray(in);
    }

    public ScriptProperty(String name, FormID id) {
	this(name);
	type = ScriptPropertyType.FormID;
	this.id = id;
	data = new byte[4];
	data[2] = -1;
	data[3] = -1;
    }

    public ScriptProperty(String name, float in) {
	this(name);
	type = ScriptPropertyType.Float;
	data = Ln.toByteArray(Float.floatToIntBits(in));
    }

    ScriptProperty(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	parseData(in);
    }

    void standardizeMasters(Mod srcMod) {
	if (id != null) {
	    id.standardize(srcMod);
	}
    }

    ArrayList<FormID> allFormIDs(boolean deep) {
	if (deep) {
	    ArrayList<FormID> out = new ArrayList<FormID>(0);
	    if (id != null) {
		out.add(id);
	    }
	    return out;
	} else {
	    return new ArrayList<FormID>(0);
	}
    }

    @Override
    final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	name.set(in.extractString(in.extractInt(2)));
	type = ScriptPropertyType.value(in.extractInt(1));
	unknown = in.extractInt(1);
	if (logging()) {
	    logSync("VMAD", "    Property " + name + " with type " + type + ", unknown: " + unknown);
	}
	switch (type) {
	    case FormID:
		data = in.extract(4);
		id = new FormID();
		id.setInternal(in.extract(4));
		break;
	    case String:
		data = in.extract(in.extractInt(2));
		break;
	    case Integer:
		data = in.extract(4);
		break;
	    case Float:
		data = in.extract(4);
		break;
	    case Boolean:
		data = in.extract(1);
		break;
	    default:
		if (logging()) {
		    logSync("VMAD", "    Importing property with UNKNOWN TYPE!");
		    logError("VMAD", "    Importing property with UNKNOWN TYPE!");
		}
		in.extractInts(1000);  // break extraction to exclude NPC from database
	}
	if (logging() && type != ScriptPropertyType.String) {
	    logSync("VMAD", "      Data: " + Ln.printHex(data, true, false));
	} else if (logging()) {
	    logSync("VMAD", "      Data: " + Ln.arrayToString(data));
	}
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	name.export(out, srcMod);
	out.write(type.value, 1);
	out.write(unknown, 1);
	switch (type) {
	    case String:
		out.write(data.length, 2);
		break;
	}
	out.write(data, 0);
	if (type == ScriptPropertyType.FormID) {
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
	switch (type) {
	    case FormID:
		out += id.getContentLength();
		break;
	    case String:
		out += 2;
	}
	return out + data.length;
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

    public enum ScriptPropertyType {

	Unknown(0),
	FormID(1),
	String(2),
	Integer(3),
	Float(4),
	Boolean(5),
	FormIDArr(11),
	StringArr(12),
	IntegerArr(13),
	FloatArr(14),
	BooleanArr(15);
	int value;

	ScriptPropertyType(int value) {
	    this.value = value;
	}

	static ScriptPropertyType value(int value) {
	    for (ScriptPropertyType p : ScriptPropertyType.values()) {
		if (p.value == value) {
		    return p;
		}
	    }
	    return Unknown;
	}
    }

    // get/set
    public ScriptPropertyType getType() {
	return type;
    }
}