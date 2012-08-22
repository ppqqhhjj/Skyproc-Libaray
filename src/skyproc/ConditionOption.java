/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import lev.LExporter;
import lev.LStream;
import lev.LStream;
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
	if (script == null) {
	    SPGlobal.logError("Conditions", "Did not have a script for index: " + index);
	}
	ConditionOption out = null;
	Class c = script.getClass();
	if (c.equals(Condition.P_NoParams.CanFlyHere.getClass())) {
	    out = new ConditionOption();
	} else if (c.equals(Condition.P_Axis.GetAngle.getClass())) {
	    out = new Cond_Axis();
	} else if (c.equals(Condition.P_FormID_CastingSource.IsCurrentSpell.getClass())) {
	    out = new Cond_FormID_CastingSource();
	} else if (c.equals(Condition.P_FormID_Int.GetStageDone.getClass())) {
	    out = new Cond_FormID_Int();
	} else if (c.equals(Condition.P_FormID_FormID.GetFactionCombatReaction.getClass())) {
	    out = new Cond_FormID_FormID();
	} else if (c.equals(Condition.P_CastingSource_FormID.SpellHasKeyword.getClass())) {
	    out = new Cond_CastingSource_FormID();
	} else if (c.equals(Condition.P_FormID.CanPayCrimeGold.getClass())) {
	    out = new Cond_FormID();
	} else if (c.equals(Condition.P_Gender.GetIsSex.getClass())) {
	    out = new Cond_Gender();
	} else if (c.equals(Condition.P_CastingSource.GetCurrentCastingType.getClass())) {
	    out = new Cond_CastingSource();
	} else if (c.equals(Condition.P_Int_FormID_Int.GetEventData.getClass())) {
	    out = new Cond_Int_FormID_Int();
	} else if (c.equals(Condition.P_Int_FormID.GetKeywordDataForAlias.getClass())) {
	    out = new Cond_Int_FormID();
	} else if (c.equals(Condition.P_WardState.IsWardState.getClass())) {
	    out = new Cond_WardState();
	} else if (c.equals(Condition.P_Int.EPModSkillUsage_IsAdvanceAction.getClass())) {
	    out = new Cond_Int();
	} else if (c.equals(Condition.P_FormID_String.GetQuestVariable.getClass())) {
	    out = new Cond_FormID_String();
	} else if (c.equals(Condition.P_FormID_Axis.GetRelativeAngle.getClass())) {
	    out = new Cond_FormID_Axis();
	} else if (c.equals(Condition.P_FormID_CrimeType.GetCrime.getClass())) {
	    out = new Cond_FormID_CrimeType();
	} else if (c.equals(Condition.P_FormID_Float.GetWithinDistance.getClass())) {
	    out = new Cond_FormID_Float();
	} else if (c.equals(Condition.P_Int_Int.GetPlayerControlsDisabled.getClass())) {
	    out = new Cond_Int_Int();
	} else if (c.equals(Condition.P_String.GetGraphVariableFloat.getClass())) {
	    out = new Cond_String();
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

    public void parseData(LStream in) throws IOException {
	parseParam1(in);
	runType = RunOnType.values()[in.extractInt(4)];
	reference.setInternal(in.extract(4));
	parseParam3(in);
	if (SPGlobal.logging()) {
	    SPGlobal.logSync(this.getClass().getName(), "  Run Type: " + runType + ", Reference: " + reference);
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
	out.write(-1);
    }

    public void parseParam1(LStream in) throws IOException{
	in.skip(8);
    }

    public void parseParam3(LStream in) throws IOException {
	in.skip(4);
    }

    public static class Cond_FormID extends ConditionOption {

	FormID p1;

	Cond_FormID() {
	    p1 = new FormID();
	}

	Cond_FormID(FormID id) {
	    p1 = id;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(0);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    in.skip(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1);
	    }
	}
    }

    public static class Cond_Axis extends ConditionOption {

	Axis axis;

	Cond_Axis() {
	}

	Cond_Axis(Axis a) {
	    axis = a;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(axis.toString(), 4);
	    out.write(0);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    axis = Axis.get(in.extractString(1));
	    in.skip(7);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Axis: " + axis);
	    }
	}
    }

    public static class Cond_FormID_CastingSource extends ConditionOption {

	FormID p1;
	CastingSource source;

	Cond_FormID_CastingSource() {
	    p1 = new FormID();
	}

	Cond_FormID_CastingSource(FormID id, CastingSource source) {
	    p1 = id;
	    this.source = source;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(source.ordinal());
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    source = CastingSource.values()[in.extractInt(4)];
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1 + ", Casting Source: " + source);
	    }
	}
    }

    public static class Cond_CastingSource_FormID extends ConditionOption {

	CastingSource source;
	FormID p2;

	Cond_CastingSource_FormID() {
	    p2 = new FormID();
	}

	Cond_CastingSource_FormID(CastingSource source, FormID id) {
	    p2 = id;
	    this.source = source;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p2);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(source.ordinal());
	    p2.export(out);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    source = CastingSource.values()[in.extractInt(4)];
	    p2.setInternal(in.extract(4));
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Casting Source: " + source + ", FormID: " + p2);
	    }
	}
    }

    public static class Cond_FormID_Int extends ConditionOption {

	FormID p1;
	int p2;

	Cond_FormID_Int() {
	    p1 = new FormID();
	}

	Cond_FormID_Int(FormID id, int i) {
	    p1 = id;
	    p2 = i;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(p2);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    p2 = in.extractInt(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1 + ", Int: " + p2);
	    }
	}
    }

    public static class Cond_FormID_FormID extends ConditionOption {

	FormID p1;
	FormID p2;

	Cond_FormID_FormID() {
	    p1 = new FormID();
	    p2 = new FormID();
	}

	Cond_FormID_FormID(FormID id1, FormID id2) {
	    p1 = id1;
	    p2 = id2;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    out.add(p2);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    p2.export(out);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    p2.setInternal(in.extract(4));
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID 1: " + p1 + ", FormID 2: " + p2);
	    }
	}
    }

    public static class Cond_Gender extends ConditionOption {

	Gender g;

	Cond_Gender() {
	}

	Cond_Gender(Gender g) {
	    this.g = g;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(g.ordinal());
	    out.write(0);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    g = Gender.values()[in.extractInt(4)];
	    in.skip(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Gender: " + g);
	    }
	}
    }

    public static class Cond_CastingSource extends ConditionOption {

	CastingSource source;

	Cond_CastingSource() {
	}

	Cond_CastingSource(CastingSource source) {
	    this.source = source;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(source.ordinal());
	    out.write(0);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    source = CastingSource.values()[in.extractInt(4)];
	    in.skip(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Casting Source: " + source);
	    }
	}
    }

    public static class Cond_Int_FormID_Int extends ConditionOption {

	int p1;
	FormID p2;
	int p3;

	Cond_Int_FormID_Int() {
	    p2 = new FormID();
	}

	Cond_Int_FormID_Int(int i1, FormID id, int i2) {
	    p1 = i1;
	    p2 = id;
	    p3 = i2;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p2);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(p1);
	    p2.export(out);
	}

	@Override
	public void exportParam3(LExporter out) throws IOException {
	    out.write(p3);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1 = in.extractInt(4);
	    p2.setInternal(in.extract(4));
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Int 1: " + p1 + ", FormID: " + p1 + ", Int 2: " + p3);
	    }
	}

	@Override
	public void parseParam3(LStream in) throws IOException {
	    p3 = in.extractInt(4);
	}
    }

    public static class Cond_Int_FormID extends ConditionOption {

	int p1;
	FormID p2;

	Cond_Int_FormID() {
	    p2 = new FormID();
	}

	Cond_Int_FormID(int i1, FormID id) {
	    p1 = i1;
	    p2 = id;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p2);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(p1);
	    p2.export(out);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1 = in.extractInt(4);
	    p2.setInternal(in.extract(4));
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Int: " + p1 + ", FormID: " + p2);
	    }
	}
    }

    public static class Cond_WardState extends ConditionOption {

	WardState state;

	Cond_WardState() {
	}

	Cond_WardState(WardState state) {
	    this.state = state;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(state.ordinal());
	    out.write(0);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    state = WardState.values()[in.extractInt(4)];
	    in.skip(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Ward State: " + state);
	    }
	}
    }

    public static class Cond_Int extends ConditionOption {

	int p1;

	Cond_Int() {
	}

	Cond_Int(int i1) {
	    p1 = i1;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(p1);
	    out.write(0);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1 = in.extractInt(4);
	    in.skip(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Int: " + p1);
	    }
	}
    }

    public static class Cond_FormID_String extends ConditionOption {

	FormID p1;
	byte[] p2;

	Cond_FormID_String() {
	    p1 = new FormID();
	}

	Cond_FormID_String(FormID id, String s) {
	    p1 = id;
	    p2 = new byte[]{(byte) 0x2e, (byte) 0xe2, (byte) 0x9d, (byte) 0xf0};
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(p2);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    p2 = in.extract(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1);
	    }
	}
    }

    public static class Cond_FormID_Axis extends ConditionOption {

	FormID p1;
	Axis a;

	Cond_FormID_Axis() {
	    p1 = new FormID();
	}

	Cond_FormID_Axis(FormID id, Axis a) {
	    p1 = id;
	    this.a = a;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(a.toString(), 4);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    a = Axis.get(in.extractString(1));
	    in.skip(3);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1 + ", Axis: " + a);
	    }
	}
    }

    public static class Cond_FormID_CrimeType extends ConditionOption {

	FormID p1;
	CrimeType c;

	Cond_FormID_CrimeType() {
	    p1 = new FormID();
	}

	Cond_FormID_CrimeType(FormID id, CrimeType c) {
	    p1 = id;
	    this.c = c;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(c.ordinal());
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    c = CrimeType.values()[in.extractInt(4)];
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1 + ", Crime Type: " + c);
	    }
	}
    }

    public static class Cond_FormID_Float extends ConditionOption {

	FormID p1;
	float f;

	Cond_FormID_Float() {
	    p1 = new FormID();
	}

	Cond_FormID_Float(FormID id, float f) {
	    p1 = id;
	    this.f = f;
	}

	@Override
	public ArrayList<FormID> allFormIDs() {
	    ArrayList<FormID> out = super.allFormIDs();
	    out.add(p1);
	    return out;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    p1.export(out);
	    out.write(f);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1.setInternal(in.extract(4));
	    f = in.extractFloat();
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  FormID: " + p1 + ", Float: " + f);
	    }
	}
    }

    public static class Cond_Int_Int extends ConditionOption {

	int i1;
	int i2;

	Cond_Int_Int() {
	}

	Cond_Int_Int(int i1, int i2) {
	    this.i1 = i1;
	    this.i2 = i2;
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(i1);
	    out.write(i2);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    i1 = in.extractInt(4);
	    i2 = in.extractInt(4);
	    if (SPGlobal.logging()) {
		SPGlobal.logSync(this.getClass().toString(), "  Int 1: " + i1 + ", Int 2: " + i2);
	    }
	}
    }

    public static class Cond_String extends ConditionOption {

	byte[] p1;

	Cond_String() {
	}

	Cond_String(String s) {
	    p1 = new byte[]{(byte) 0x2e, (byte) 0xe2, (byte) 0x9d, (byte) 0xf0};
	}

	@Override
	public void exportParam1(LExporter out) throws IOException {
	    out.write(p1);
	}

	@Override
	public void parseParam1(LStream in) throws IOException{
	    p1 = in.extract(4);
	}
    }
}
