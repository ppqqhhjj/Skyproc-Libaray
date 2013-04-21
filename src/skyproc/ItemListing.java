/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

/**
 *
 * @author Justin Swanson
 */
public class ItemListing extends SubShell {
    
    static SubPrototype itemListingProto = new SubPrototype(){

	@Override
	protected void addRecords() {
	    add(new SubFormInt("CNTO"));
	    add(new SubData("COED"));
	}
    };
    
    public ItemListing(FormID id, int count) {
	this();
	subRecords.setSubFormInt("CNTO", id, count);
    }
    
    public ItemListing(FormID id) {
	this(id, 1);
    }
    
    ItemListing() {
	super(itemListingProto);
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final ItemListing other = (ItemListing) obj;
	if (!getID().equals(other.getID())) {
	    return false;
	}
	return true;
    }

    @Override
    public int hashCode() {
	return getID().hashCode();
    }
    
    
    
    // Get/set
    public FormID getID() {
	return subRecords.getSubFormInt("CNTO").getForm();
    }
    
    public void setID(FormID id) {
	subRecords.setSubFormInt("CNTO", id);
    }
    
    public int getCount() {
	return subRecords.getSubFormInt("CNTO").getNum();
    }
    
    public void setCount(int count) {
	subRecords.setSubFormInt("CNTO", count);
    }
}
