/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 * A customized Text Pane used by Leviathan for GUIs.
 * @author Justin Swanson
 */
public class LTextPane extends LComponent {

    static String header = "Text Pane";
    JScrollPane scroll;
    JTextPane pane;
    Document doc;

    /**
     * 
     * @param size_
     * @param c
     */
    public LTextPane(Dimension size_, Color c) {
        pane = new JTextPane();
        doc = pane.getDocument();
        pane.setOpaque(false);
        pane.setForeground(c);
	setSize(size_);
        add(pane);
    }
    
    @Override
    public void setSize(Dimension size) {
        super.setSize(size);
	pane.setSize(size);
    }

    /**
     * 
     * @param in
     */
    public void setText(String in) {
        clearText();
        try {
            doc.insertString(0, in, null);
        } catch (BadLocationException ex) {
            badText();
        }
    }

    /**
     * 
     */
    public void badText() {
        try {
            doc.insertString(0, "Bad Error", null);
        } catch (BadLocationException ex) {
            Logger.getLogger(LTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     */
    public void clearText() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
        }
    }

    /**
     * 
     * @param in
     */
    public void append(String in) {
        try {
            doc.insertString(doc.getLength(), in, null);
        } catch (BadLocationException ex) {
            badText();
        }
    }

    /**
     * 
     * @param b
     */
    public void setOpaque(Boolean b) {
        pane.setOpaque(b);
    }

    /**
     * 
     * @return
     */
    public boolean isEmpty() {
        return doc.getLength() == 0;
    }

    /**
     * 
     * @param c
     */
    @Override
    public void setBackground(Color c) {
        pane.setBackground(c);
    }

    /**
     * 
     * @param c
     */
    public void setCaretColor(Color c) {
        pane.setCaretColor(c);
    }

    /**
     * 
     */
    public void centerText() {
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    /**
     * 
     */
    public void addScroll() {
        this.removeAll();
        scroll = new JScrollPane(pane);
        scroll.setSize(pane.getSize());
        add(scroll);
    }

    /**
     * 
     * @param b
     */
    public void setEditable(boolean b) {
        pane.setEditable(b);
    }

    /**
     * 
     * @param size
     */
    public void setFontSize(float size) {
        pane.setFont(pane.getFont().deriveFont(size));
    }

    /**
     * 
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return pane.getPreferredSize();
    }
}
