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

    /**
     *
     * @param node
     * @return True if children contains a node equal to the parameter.
     */
    public boolean contains(LSwingTreeNode node) {
	return (get(node) != null);
    }

    /**
     * Recursively searches the node's children for one equal
     * to the input and returns it.
     * @param node
     * @return
     */
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
