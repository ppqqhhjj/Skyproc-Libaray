/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import lev.LExporter;
import lev.LShrinkArray;
import skyproc.Condition.RunOnType;

/**
 *
 * @author Justin Swanson
 */
public class ConditionOption {

    int index;
    Enum script;
    RunOnType runType = RunOnType.Subject;
    FormID reference = new FormID();

    public static ConditionOption getOption(int index) {
	Enum script = Condition.getScript(index);
	ConditionOption out = null;
	Class c = script.getClass();
	if (c.equals(Condition.P_NoParams.CanFlyHere.getClass())) {
	    out = new ConditionOption();
	} else if (c.equals(Condition.P_FormID.CanPayCrimeGold.getClass())) {
	    out = new Cond_FormID();
	}
	out.index = index;
	out.script = script;

	return out;
    }

    public void export(LExporter out) throws IOException {
	exportParam1(out);
	out.write(runType.ordinal());
	reference.export(out);
	exportParam3(out);
    }

    public void parseData(LShrinkArray in) {
	parseParam1(in);
	runType = RunOnType.values()[in.extractInt(4)];
	reference.setInternal(in.extract(4));
	parseParam3(in);
	if (SPGlobal.logging()) {
	    SPGlobal.logSync("", "  Run Type:" + runType + ", Reference: " + reference);
	}
    }

    public ArrayList<FormID> allFormIDs() {
	ArrayList<FormID> out = new ArrayList<>(3);
	out.add(reference);
	return out;
    }

    public void exportParam1(LExporter out) throws IOException {
	out.write(0);
	out.write(0);
    }

    public void exportParam3(LExporter out) throws IOException {
	out.write(0);
    }

    public void parseParam1(LShrinkArray in) {
	in.skip(8);
    }

    public void parseParam3(LShrinkArray in) {
	in.skip(4);
    }

    public static class Cond_FormID extends ConditionOption {

	FormID p1 = new FormID();

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = new ArrayList<>(1);
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(0);
	}

	@Override
	public void parseParam1(LShrinkArray in) {
	    p1.setInternal(in.extract(4));
	    in.skip(4);
	}

    }
}
