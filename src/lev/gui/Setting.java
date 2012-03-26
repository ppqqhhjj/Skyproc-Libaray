/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.io.BufferedWriter;
import lev.gui.LUserSetting;
import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
public abstract class Setting<T> {

    protected T data;
    protected String title;
    protected LUserSetting<T> tie;
    public Boolean forGame;

    public Setting(String title_, T data_, Boolean in_game) {
        title = title_;
        data = data_;
        forGame = in_game;
    }

    public T get() {
        return data;
    }

    public Boolean getBool() {
        return (Boolean)data;
    }

    public Integer getInt() {
        return (Integer)data;
    }

    public String getStr() {
        return data.toString();
    }

    public String getTitle() {
        return title;
    }

    public void tie(LUserSetting c) {
        tie = c;
    }

    public void set() {
        if (tie != null) {
            setTo(tie.getValue());
        }
    }

    public void setTo(T input) {
        data = input;
    }

    public void write(BufferedWriter b) throws java.io.IOException {
        b.write("set " + title + " to " + writeContent() + "\n");
    }

    public abstract String writeContent();

    public void readSetting(String input) throws java.io.IOException, BadParameter {
        input = input.substring(input.indexOf(" to ") + 4);
        parse(input.trim());
    }

    public abstract void parse (String in) throws BadParameter;

    public void link () {

    }

    public Boolean isEmpty() {
	return data == null;
    }

    public abstract Setting<T> copyOf ();

}