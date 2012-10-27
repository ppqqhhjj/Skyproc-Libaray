package skyproc;

import java.util.ArrayList;

/**
 *
 * @author Arkangel
 */
public class COBJ extends MajorRecord {

    static final SubRecordsPrototype COBJproto = new SubRecordsPrototype(MajorRecord.majorProto) {

	@Override
	protected void addRecords() {
	    add(new SubList<>(Type.COCT, 4, new SubFormInt(Type.CNTO)));
	    add(new SubData(Type.COED));
	    add(new SubList<>(new Condition()));
	    add(new SubForm(Type.CNAM));
	    add(new SubForm(Type.BNAM));
	    add(new SubData(Type.NAM1));
	    add(new KeywordSet());
	}
    };
    private final static Type[] type = {Type.COBJ};

    COBJ() {
	super();
	subRecords.prototype = COBJproto;
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
    public ArrayList<Condition> getConditions() {
	return subRecords.getSubList(Type.CTDA).toPublic();
    }

    /**
     *
     * @param c
     */
    public void addCondition(Condition c) {
	subRecords.getSubList(Type.CTDA).add(c);
    }

    /**
     *
     * @param c
     */
    public void removeCondition(Condition c) {
	subRecords.getSubList(Type.CTDA).remove(c);
    }

    /**
     *
     * @param itemReference
     * @param count
     * @return
     */
    public boolean addIngredient(FormID itemReference, int count) {
	return subRecords.getSubList(Type.CNTO).add(new SubFormInt(Type.CNTO, itemReference, count));
    }

    /**
     *
     * @param itemReference
     * @return
     */
    public boolean removeIngredient(FormID itemReference) {
	return subRecords.getSubList(Type.CNTO).remove(new SubFormInt(Type.CNTO, itemReference, 1));
    }

    public void clearIngredients() {
	subRecords.getSubList(Type.CNTO).clear();
    }

    public ArrayList<SubFormInt> getIngredients() {
	return SubList.subFormIntToPublic(subRecords.getSubList(Type.CNTO));
    }

    /**
     *
     * @return
     */
    public FormID getResultFormID() {
	return subRecords.getSubForm(Type.CNAM).getForm();
    }

    /**
     *
     * @param form
     */
    public void setResultFormID(FormID form) {
	subRecords.setSubForm(Type.CNAM, form);
    }

    /**
     *
     * @return
     */
    public FormID getBenchKeywordFormID() {
	return subRecords.getSubForm(Type.BNAM).getForm();
    }

    /**
     *
     * @param form
     */
    public void setBenchKeywordFormID(FormID form) {
	subRecords.setSubForm(Type.BNAM, form);
    }

    /**
     *
     * @return
     */
    public int getOutputQuantity() {
	return subRecords.getSubData(Type.NAM1).toInt();
    }

    /**
     *
     * @param n
     */
    public void setOutputQuantity(int n) {
	subRecords.setSubData(Type.NAM1, n);
    }
}
