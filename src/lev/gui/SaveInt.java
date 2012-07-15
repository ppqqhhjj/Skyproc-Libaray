/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
class SaveInt extends Setting<Integer> {

    public SaveInt(String title_, Integer data_, Boolean patchChanging) {
        super(title_, data_, patchChanging);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void parse(String in) {
        data = Integer.valueOf(in);
    }

    @Override
    public Setting<Integer> copyOf() {
        SaveInt out = new SaveInt(title, data, patchChanging);
        out.tie = tie;
        return out;
    }

}
