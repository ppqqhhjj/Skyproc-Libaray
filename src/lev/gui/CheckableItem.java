/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
public class CheckableItem<T extends Object> {

    private T obj;
    private boolean isSelected;

    public CheckableItem(T obj) {
	this.obj = obj;
	isSelected = false;
    }

    public void setSelected(boolean b) {
	isSelected = b;
    }

    public boolean isSelected() {
	return isSelected;
    }

    @Override
    public String toString() {
	return obj.toString();
    }

    public T getObject() {
	return obj;
    }

    public void setObject(T obj) {
	this.obj = obj;
    }
}
