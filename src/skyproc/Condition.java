/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LFlags;
import lev.LShrinkArray;
import lev.Ln;
import skyproc.EmbeddedScripts.EmbeddedScript;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 *
 * @author Justin Swanson
 */
public class Condition extends SubShell {

    static Type[] types = {Type.CTDA, Type.CIS1, Type.CIS2};
    Cond cond = new Cond();
    SubString CIS1 = new SubString(Type.CIS1, true);
    SubString CIS2 = new SubString(Type.CIS2, true);

    Condition() {
	super(types);
	init();
    }

    public Condition(EmbeddedScript function) {
	this();
	cond.script = function;
    }

    final void init() {
	subRecords.add(cond);
	subRecords.add(CIS1);
	subRecords.add(CIS2);
    }

    @Override
    SubRecord getNew(Type type) {
	return new Condition();
    }

    @Override
    Boolean isValid() {
	return cond.isValid();
    }

    class Cond extends SubRecord {

	Operator operator;
	LFlags flags = new LFlags(1);
	byte[] fluff;
	FormID comparisonValueForm = new FormID();
	float comparisonValueFloat;
	EmbeddedScript script;
	byte[] padding;
	FormID[] paramForm = new FormID[3];
	int[] paramInt = new int[3];
	RunOnType runType;
	FormID reference = new FormID();

	Cond() {
	    super(Type.CTDA);
	}

	@Override
	void export(LExporter out, Mod srcMod) throws IOException {
	    super.export(out, srcMod);
	    //Flags and Operator
	    LFlags tmp = new LFlags(Ln.toByteArray(operator.ordinal(), 1));
	    for (int i = 3; i < 8; i++) {
		tmp.set(i, flags.get(i));
	    }
	    out.write(tmp.export(), 1);
	    out.write(fluff, 3);

	    //Value
	    if (get(CondFlag.UseGlobal)) {
		// This FormID is flipped, so it's an odd export.
		out.write(comparisonValueForm.get());
	    } else {
		out.write(comparisonValueFloat);
	    }

	    //Function
	    out.write(script.ordinal(), 2);
	    out.write(padding, 2);

	    //Param1
	    if (script.getType(Param.One) == ParamType.FormID) {
		paramForm[0].export(out);
	    } else {
		out.write(paramInt[0]);
	    }

	    //Param2
	    if (script.getType(Param.Two) == ParamType.FormID) {
		paramForm[1].export(out);
	    } else {
		out.write(paramInt[1]);
	    }

	    out.write(runType.ordinal());
	    reference.export(out);

	    //Param3
	    if (script.getType(Param.Three) == ParamType.FormID) {
		paramForm[2].export(out);
	    } else {
		out.write(paramInt[2]);
	    }
	}

	@Override
	void parseData(LShrinkArray in) throws BadRecord, DataFormatException, BadParameter {
	    super.parseData(in);
	    //Flags and Operator
	    flags.set(in.extract(1));
	    LFlags tmp = new LFlags(flags.export());
	    for (int i = 3; i < 8; i++) {
		tmp.set(i, false);
	    }
	    operator = Operator.values()[Ln.arrayToInt(tmp.export())];
	    fluff = in.extract(3);

	    //Value
	    if (get(CondFlag.UseGlobal)) {
		// Use public set here, because for some reason, this FormID is flipped
		comparisonValueForm.set(in.extract(4));
	    } else {
		comparisonValueFloat = in.extractFloat();
	    }

	    //Function
	    script = EmbeddedScripts.getScript(in.extractInt(2));
	    padding = in.extract(2);

	    //Param1
	    if (script.getType(Param.One) == ParamType.FormID) {
		paramForm[0] = new FormID();
		paramForm[0].setInternal(in.extract(4));
	    } else {
		paramInt[0] = in.extractInt(4);
	    }

	    //Param2
	    if (script.getType(Param.Two) == ParamType.FormID) {
		paramForm[1] = new FormID();
		paramForm[1].setInternal(in.extract(4));
	    } else {
		paramInt[1] = in.extractInt(4);
	    }

	    runType = RunOnType.values()[in.extractInt(4)];
	    reference.setInternal(in.extract(4));

	    //Param3
	    if (script.getType(Param.Three) == ParamType.FormID) {
		paramForm[2] = new FormID();
		paramForm[2].setInternal(in.extract(4));
	    } else {
		paramInt[2] = in.extractInt(4);
	    }

	    if (SPGlobal.logging()) {
		logSync("", "New Condition.  Function: " + script.toString() + ", index: " + script.ordinal());
		logSync("", "  Operator: " + operator + ", flags: " + flags + " useGlobal: " + get(CondFlag.UseGlobal));
		logSync("", "  Comparison Val: " + comparisonValueForm + "|" + comparisonValueFloat + ", Param 1: " + paramForm[0] + "|" + paramInt[0]);
		logSync("", "  Param 2: " + paramForm[1] + "|" + paramInt[1] + ", Param 3: " + paramForm[2] + "|" + paramInt[2]);
		logSync("", "  Run Type:" + runType + ", Reference: " + reference);
	    }
	}

	@Override
	ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(5);
	    out.add(comparisonValueForm);
	    out.add(reference);
	    for (int i = 0 ; i < paramForm.length ; i++) {
		if (paramForm[i] != null) {
		    out.add(paramForm[i]);
		}
	    }
	    return out;
	}

	@Override
	SubRecord getNew(Type type) {
	    return new Condition();
	}

	@Override
	Boolean isValid() {
	    return true;
	}

	@Override
	int getContentLength(Mod srcMod) {
	    return 32;
	}
    }

    public boolean get(CondFlag flag) {
	return cond.flags.get(flag.value);
    }

    public void set(CondFlag flag, boolean on) {
	cond.flags.set(flag.value, on);
    }

    public void setParam(Param param, FormID id) {
    }

    public ParamType getParamType(Param param) {
	return cond.script.getType(param);
    }

    public enum CondFlag {

	OR(0),
	UseAliases(1),
	UseGlobal(3),
	UsePackData(4),
	SwapSubjectAndTarget(5);
	int value;

	CondFlag(int value) {
	    this.value = value;
	}
    }

    public enum RunOnType {

	Subject,
	Target,
	Reference,
	CombatTarget,
	LinkedRef,
	QuestAlias,
	PackageData,
	EventData;
    }

    public enum Operator {

	EqualTo,
	NotEqualTo,
	GreaterThan,
	GreaterThanOrEqual,
	LessThan,
	LessThanOrEqual;
    }

    public enum Param {
	One,
	Two,
	Three;
    }

    public enum ParamType {
	FormID,
	Int,
	String,
	Axis;
    }
}
