/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Justin Swanson
 */
public class LHTMLPane extends LEditorPane {

    public LHTMLPane () {
	super();
	pane.setContentType("text/html");
    }

    public HTMLDocument getDocument() {
	return (HTMLDocument) pane.getDocument();
    }

    public StyleSheet getStyleSheet() {
	return getDocument().getStyleSheet();
    }
}
