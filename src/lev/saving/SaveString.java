/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.saving;

/**
 *
 * @author Justin Swanson
 */
public class SaveString extends Setting<String> {

    public SaveString(String title_, String data_, Boolean oblivion_) {
        super(title_, data_, oblivion_);
    }

    @Override
    public String writeContent() {
        return data;
    }

    @Override
    public void parse(String in) {
        data = in;
    }

    @Override
    public Boolean isEmpty() {
	return super.isEmpty() || data.equals("");
    }

    @Override
    public Setting<String> copyOf() {
        SaveString out = new SaveString(title, data, forGame);
        out.tie = tie;
        return out;
    }
}
