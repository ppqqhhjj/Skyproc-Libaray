/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
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
    int unknown = 1;
    ScriptData data;
    private static final Type[] types = {Type.VMAD};

    ScriptProperty() {
    }

    ScriptProperty(String name) {
	this.name.set(name);
    }

    public ScriptProperty(String name, boolean b) {
	this(name);
	BooleanData tmp = new BooleanData();
	tmp.data = b;
	data = tmp;
    }

    public ScriptProperty(String name, int in) {
	this(name);
	IntData tmp = new IntData();
	tmp.data = in;
	data = tmp;
    }

    public ScriptProperty(String name, FormID id) {
	this(name);
	FormIDData tmp = new FormIDData();
	tmp.id = id;
	tmp.data = new byte[4];
	tmp.data[2] = -1;
	tmp.data[3] = -1;
	data = tmp;
    }

    public ScriptProperty(String name, float in) {
	this(name);
	FloatData tmp = new FloatData();
	tmp.data = in;
	data = tmp;
    }

    ScriptProperty(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	parseData(in);
    }

    void standardizeMasters(Mod srcMod) {
	if (getType().equals(ScriptPropertyType.FormID)) {
	    ((FormIDData) data).id.standardize(srcMod);
	}
    }

    ArrayList<FormID> allFormIDs(boolean deep) {
	if (deep) {
	    ArrayList<FormID> out = new ArrayList<FormID>(0);
	    if (getType().equals(ScriptPropertyType.FormID)) {
		out.add(((FormIDData) data).id);
	    }
	    return out;
	} else {
	    return new ArrayList<FormID>(0);
	}
    }

    @Override
    final void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	name.set(in.extractString(in.extractInt(2)));
	ScriptPropertyType type = ScriptPropertyType.value(in.extractInt(1));
	unknown = in.extractInt(1);
	if (logging()) {
	    logSync("VMAD", "    Property " + name + " with type " + type + ", unknown: " + unknown);
	}
	switch (type) {
	    case FormID:
		data = new FormIDData();
		break;
	    case String:
		data = new StringData();
		break;
	    case Integer:
		data = new IntData();
		break;
	    case Float:
		data = new FloatData();
		break;
	    case Boolean:
		data = new BooleanData();
		break;
	    default:
		if (logging()) {
		    logSync("VMAD", "    Importing property with UNKNOWN TYPE!");
		    logError("VMAD", "    Importing property with UNKNOWN TYPE!");
		}
		in.extractInts(1000);  // break extraction to exclude NPC from database
	}
	if (data != null) {
	    data.parseData(in);
	}
	if (logging()) {
	    logSync("VMAD", "      Data: " + data.print());
	}
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	name.export(out, srcMod);
	out.write(getType().value, 1);
	out.write(unknown, 1);
	data.export(out, srcMod);
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
	return out + data.getContentLength();
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

    // Data classes 
    interface ScriptData {

	void parseData(LShrinkArray in);

	int getContentLength();

	void export(LExporter out, Mod srcMod) throws IOException;

	ScriptPropertyType getType();

	String print();
    }

    class StringData implements ScriptData {

	String data;

	@Override
	public void parseData(LShrinkArray in) {
	    data = in.extractString(in.extractInt(2));
	}

	@Override
	public int getContentLength() {
	    return 2 + data.length();
	}

	@Override
	public void export(LExporter out, Mod srcMod) throws IOException {
	    out.write(data.length(), 2);
	    out.write(data);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.String;
	}

	@Override
	public String print() {
	    return data;
	}
    }

    class IntData implements ScriptData {

	int data;

	@Override
	public void parseData(LShrinkArray in) {
	    data = in.extractInt(4);
	}

	@Override
	public int getContentLength() {
	    return 4;
	}

	@Override
	public void export(LExporter out, Mod srcMod) throws IOException {
	    out.write(data);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.Integer;
	}

	@Override
	public String print() {
	    return Integer.toString(data);
	}
    }

    class BooleanData implements ScriptData {

	boolean data;

	@Override
	public void parseData(LShrinkArray in) {
	    data = in.extractBool(1);
	}

	@Override
	public int getContentLength() {
	    return 1;
	}

	@Override
	public void export(LExporter out, Mod srcMod) throws IOException {
	    if (data) {
		out.write(1, 1);
	    } else {
		out.write(0, 1);
	    }
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.Boolean;
	}

	@Override
	public String print() {
	    return String.valueOf(data);
	}
    }

    class FormIDData implements ScriptData {

	byte[] data;
	FormID id;

	@Override
	public void parseData(LShrinkArray in) {
	    data = in.extract(4);
	    id = new FormID();
	    id.setInternal(in.extract(4));
	}

	@Override
	public int getContentLength() {
	    return 8;
	}

	@Override
	public void export(LExporter out, Mod srcMod) throws IOException {
	    out.write(data, 4);
	    id.export(out);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.FormID;
	}

	@Override
	public String print() {
	    return id.toString();
	}
    }

    class FloatData implements ScriptData {

	float data;

	@Override
	public void parseData(LShrinkArray in) {
	    data = in.extractFloat();
	}

	@Override
	public int getContentLength() {
	    return 4;
	}

	@Override
	public void export(LExporter out, Mod srcMod) throws IOException {
	    out.write(data);
	}

	@Override
	public ScriptPropertyType getType() {
	    return ScriptPropertyType.Float;
	}

	@Override
	public String print() {
	    return String.valueOf(data);
	}
    }

    // get/set
    public ScriptPropertyType getType() {
	return data.getType();
    }
}