/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Component;
import javax.swing.JScrollPane;

/**
 *
 * @author Justin Swanson
 */
public class LScrollPane extends JScrollPane {

    /**
     *
     * @param c
     */
    public LScrollPane (Component c) {
	super(c);
    }

    @Override
    public void setOpaque(boolean arg0) {
	super.setOpaque(false);
	getViewport().setOpaque(false);
    }

}
