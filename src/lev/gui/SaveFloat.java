/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
class SaveFloat extends Setting<Float> {

    public SaveFloat(String title_, Float data_, Boolean patchChanging) {
        super(title_, data_, patchChanging);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @Override
    public void parse(String in) {
        data = Float.valueOf(in);
    }

    @Override
    public Setting<Float> copyOf() {
        SaveFloat out = new SaveFloat(title, data, patchChanging);
        out.tie = tie;
        return out;
    }

}