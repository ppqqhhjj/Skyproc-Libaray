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
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Justin Swanson
 */
public class LTextPane extends LComponent {

    static String header = "Text Pane";
    JScrollPane scroll;
    JTextPane pane;
    Document doc;

    public LTextPane(Dimension size_, Color c) {
        pane = new JTextPane();
        doc = pane.getDocument();
        pane.setSize(size_);
        pane.setOpaque(false);
        pane.setForeground(c);
        add(pane);
        super.setSize(size_);
    }

    public void setText(String in) {
        clearText();
        try {
            doc.insertString(0, in, null);
        } catch (BadLocationException ex) {
            badText();
        }
    }

    public void badText() {
        try {
            doc.insertString(0, "Bad Error", null);
        } catch (BadLocationException ex) {
            Logger.getLogger(LTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearText() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
        }
    }

    public void append(String in) {
        try {
            doc.insertString(doc.getLength(), in, null);
        } catch (BadLocationException ex) {
            badText();
        }
    }

    public void setOpaque(Boolean b) {
        pane.setOpaque(b);
    }

    public boolean isEmpty() {
        return doc.getLength() == 0;
    }

    @Override
    public void setBackground(Color c) {
        pane.setBackground(c);
    }

    public void setCaretColor(Color c) {
        pane.setCaretColor(c);
    }

    public void centerText() {
        StyledDocument doc = pane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
    }

    public void addScroll() {
        this.removeAll();
        scroll = new JScrollPane(pane);
        scroll.setSize(pane.getSize());
        add(scroll);
    }

    public void setEditable(boolean b) {
        pane.setEditable(b);
    }

    public void setFontSize(float size) {
        pane.setFont(pane.getFont().deriveFont(size));
    }

    @Override
    public Dimension getPreferredSize() {
        return pane.getPreferredSize();
    }
}
