/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.zip.DataFormatException;
import lev.LExporter;
import lev.LShrinkArray;
import lev.LChannel;
import skyproc.exceptions.BadParameter;
import skyproc.exceptions.BadRecord;

/**
 * A set of keywords associated with a major record.
 * @author Justin Swanson
 */
public class KeywordSet extends SubRecord {

    private static final Type[] types = {Type.KSIZ, Type.KWDA};
    SubData counter = new SubData(Type.KSIZ, 0);
    SubFormArray keywords = new SubFormArray(Type.KWDA, 0);

    KeywordSet() {
	super(types);
    }

    @Override
    SubRecord getNew(Type type) {
	return new KeywordSet();
    }

    @Override
    Boolean isValid() {
	return counter.isValid()
		&& keywords.isValid();
    }

    @Override
    int getHeaderLength() {
	return 0;
    }

    @Override
    int getContentLength(Mod srcMod) {
	return counter.getTotalLength(srcMod)
		+ keywords.getTotalLength(srcMod);
    }

    @Override
    void export(LExporter out, Mod srcMod) throws IOException {
	if (isValid()) {
	    counter.export(out, srcMod);
	    keywords.export(out, srcMod);
	}
    }

    @Override
    void parseData(LChannel in) throws BadRecord, DataFormatException, BadParameter {
	switch (getNextType(in)) {
	    case KSIZ:
		counter.parseData(in);
		keywords = new SubFormArray(Type.KWDA, counter.toInt());
		break;
	    case KWDA:
		keywords.parseData(in);
		break;
	}
    }

    @Override
    ArrayList<FormID> allFormIDs() {
	return keywords.IDs;
    }

    /**
     * Returns a COPY of the list of FormIDs associated with this keyword set.
     * @return
     */
    public ArrayList<FormID> getKeywordRefs() {
	return new ArrayList<>(keywords.IDs);
    }

    /**
     * Adds a keyword to the list
     * @param keywordRef A KYWD formID
     */
    public void addKeywordRef(FormID keywordRef) {
	keywords.add(keywordRef);
	counter.modValue(1);
    }

    /**
     * Removes a keyword to the list
     * @param keywordRef A KYWD formID
     */
    public void removeKeywordRef(FormID keywordRef) {
	if (keywords.remove(keywordRef)) {
	    counter.modValue(-1);
	}
    }

    public void clearKeywordRefs () {
	keywords.clear();
	counter.setData(0, 4);
    }

    /**
     *
     * @param set
     * @return True if every keyword in this set is contained in the parameter's set.
     */
    public boolean containedIn(KeywordSet set) {
	return keywords.containedIn(set.keywords);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final KeywordSet other = (KeywordSet) obj;
	if (this.keywords != other.keywords && (this.keywords == null || !this.keywords.equals(other.keywords))) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 89 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
	return hash;
    }

}
