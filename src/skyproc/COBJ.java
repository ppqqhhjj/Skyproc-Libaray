package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Arkangel
 */
public class COBJ extends MajorRecord {

    private static final Type[] type = {Type.COBJ};

    SubList<SubFormInt> ingredients = new SubList<>(Type.COCT, 4, new SubFormInt(Type.CNTO));
    SubData COED = new SubData(Type.COED);
    SubList<Condition> CONDs = new SubList<>(new Condition());
    SubForm CNAM = new SubForm(Type.CNAM);
    SubForm BNAM = new SubForm(Type.BNAM);
    SubData NAM1 = new SubData(Type.NAM1);

    public KeywordSet keywords = new KeywordSet();

    COBJ() {
	super();

        subRecords.add(ingredients);
        subRecords.add(COED);
	subRecords.add(CONDs);
        subRecords.add(CNAM);
        subRecords.add(BNAM);
        subRecords.add(NAM1);
	subRecords.add(keywords);
    }

    @Override
    Type[] getTypes() {
	return type;
    }

    @Override
    Record getNew() {
	return new COBJ();
    }

    public ArrayList<Condition> getConditions  () {
	return CONDs.toPublic();
    }

    public void addCondition (Condition c) {
	CONDs.add(c);
    }

    public void removeCondition (Condition c) {
	CONDs.remove(c);
    }

    public boolean addIngredient(FormID itemReference, int count) {
	return ingredients.add(new SubFormInt(Type.CNTO, itemReference, count));
    }

    public boolean removeIngredient(FormID itemReference) {
	return ingredients.remove(new SubFormInt(Type.CNTO, itemReference, 1));
    }

    public FormID getResultFormID() {
        return CNAM.getForm();
    }

    public void setResultFormID(FormID form) {
        CNAM.setForm(form);
    }

    public FormID getBenchKeywordFormID() {
        return BNAM.getForm();
    }

    public void setBenchKeywordFormID(FormID form) {
        BNAM.setForm(form);
    }

    public int getOutputQuantity() {
        return NAM1.toInt();
    }

    public void setOutputQuantity(int n) {
        NAM1.setData(n);
    }

}
