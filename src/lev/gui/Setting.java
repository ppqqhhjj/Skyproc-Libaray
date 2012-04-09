/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.io.BufferedWriter;
import skyproc.exceptions.BadParameter;

/**
 * A class representing a saveable value.
 * @param <T> Type of data being saved.
 * @author Justin Swanson
 */
abstract class Setting<T> {

    T data;
    String title;
    LUserSetting<T> tie;
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

    void tie(LUserSetting c) {
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