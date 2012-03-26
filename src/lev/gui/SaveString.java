/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
class SaveString extends Setting<String> {

    public SaveString(String title_, String data_, Boolean inGame) {
        super(title_, data_, inGame);
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
