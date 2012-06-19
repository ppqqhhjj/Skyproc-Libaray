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
public abstract class Setting<T> {

    T data;
    String title;
    LUserSetting<T> tie;
    Boolean forGame;

    /**
     *
     * @param title_
     * @param data_
     * @param in_game
     */
    public Setting(String title_, T data_, Boolean in_game) {
        title = title_;
        data = data_;
        forGame = in_game;
    }

    /**
     *
     * @return
     */
    public T get() {
        return data;
    }

    /**
     *
     * @return Returns the value as a boolean.  Could fail.
     */
    public Boolean getBool() {
        return (Boolean)data;
    }

    /**
     *
     * @return Returns the value as an int.  Could fail.
     */
    public Integer getInt() {
        return (Integer)data;
    }

    /**
     *
     * @return Returns the value as a string.  Could fail.
     */
    public String getStr() {
        return data.toString();
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    void tie(LUserSetting c) {
        tie = c;
    }

    /**
     * Updates the setting to its GUI tie's value
     */
    public void set() {
        if (tie != null) {
            setTo(tie.getValue());
        }
    }

    /**
     *
     * @param input
     */
    public void setTo(T input) {
        data = input;
    }

    /**
     *
     * @param b
     * @throws java.io.IOException
     */
    public void write(BufferedWriter b) throws java.io.IOException {
        b.write("set " + title + " to " + toString() + "\n");
    }

    /**
     *
     * @param input
     * @throws java.io.IOException
     * @throws BadParameter
     */
    public void readSetting(String input) throws java.io.IOException, BadParameter {
        input = input.substring(input.indexOf(" to ") + 4);
        parse(input.trim());
    }

    /**
     * 
     * @param in
     * @throws BadParameter
     */
    public abstract void parse (String in) throws BadParameter;

    /**
     *
     * @return
     */
    public Boolean isEmpty() {
	return data == null;
    }

    /**
     *
     * @return
     */
    public abstract Setting<T> copyOf ();

}