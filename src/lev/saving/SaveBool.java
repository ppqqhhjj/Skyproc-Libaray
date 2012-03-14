/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.saving;

import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
public class SaveBool extends Setting<Boolean> {

    public SaveBool(String title_, Boolean data_, Boolean in_game) {
        super(title_, data_, in_game);
    }

    @Override
    public String writeContent() {
        return Ln.convertBoolTo1(data);
    }

    @Override
    public void parse(String in) {
        data = Ln.toBool(in);
    }

    @Override
    public Setting<Boolean> copyOf() {
        SaveBool out = new SaveBool(title, data, forGame);
        out.tie = tie;
        return out;
    }

}
