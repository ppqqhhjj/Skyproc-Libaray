/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author Justin Swanson
 */
public class LSwingTreeNode extends DefaultMutableTreeNode {

    public boolean contains(LSwingTreeNode node) {
	return (get(node) != null);
    }

    public LSwingTreeNode get(LSwingTreeNode node) {
	if (this.children != null) {
	    for (Object rhs : this.children) {
		if (node.equals(rhs)) {
		    return (LSwingTreeNode) rhs;
		}
	    }
	}
	return null;
    }

}
