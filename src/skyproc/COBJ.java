package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Arkangel
 */
public class COBJ extends MajorRecord {

    private final static Type[] type = {Type.COBJ};

    SubList<SubFormInt> ingredients = new SubList<>(Type.COCT, 4, new SubFormInt(Type.CNTO));
    SubData COED = new SubData(Type.COED);
    SubList<Condition> CONDs = new SubList<>(new Condition());
    SubForm CNAM = new SubForm(Type.CNAM);
    SubForm BNAM = new SubForm(Type.BNAM);
    SubData NAM1 = new SubData(Type.NAM1);

    /**
     *
     */
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

    /**
     *
     * @return
     */
    public ArrayList<Condition> getConditions  () {
	return CONDs.toPublic();
    }

    /**
     *
     * @param c
     */
    public void addCondition (Condition c) {
	CONDs.add(c);
    }

    /**
     *
     * @param c
     */
    public void removeCondition (Condition c) {
	CONDs.remove(c);
    }

    /**
     *
     * @param itemReference
     * @param count
     * @return
     */
    public boolean addIngredient(FormID itemReference, int count) {
	return ingredients.add(new SubFormInt(Type.CNTO, itemReference, count));
    }

    /**
     *
     * @param itemReference
     * @return
     */
    public boolean removeIngredient(FormID itemReference) {
	return ingredients.remove(new SubFormInt(Type.CNTO, itemReference, 1));
    }

    public void clearIngredients() {
	ingredients.clear();
    }

    public ArrayList<SubFormInt> getIngredients() {
	return SubList.subFormIntToPublic(ingredients);
    }

    /**
     *
     * @return
     */
    public FormID getResultFormID() {
        return CNAM.getForm();
    }

    /**
     *
     * @param form
     */
    public void setResultFormID(FormID form) {
        CNAM.setForm(form);
    }

    /**
     *
     * @return
     */
    public FormID getBenchKeywordFormID() {
        return BNAM.getForm();
    }

    /**
     *
     * @param form
     */
    public void setBenchKeywordFormID(FormID form) {
        BNAM.setForm(form);
    }

    /**
     *
     * @return
     */
    public int getOutputQuantity() {
        return NAM1.toInt();
    }

    /**
     *
     * @param n
     */
    public void setOutputQuantity(int n) {
        NAM1.setData(n);
    }

}
