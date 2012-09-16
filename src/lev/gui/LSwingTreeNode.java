/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.util.ArrayList;
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

    public ArrayList<Object> getAllObjects() {
	return getAllObjects(false);
    }

    public ArrayList<Object> getAllObjects(boolean recursive) {
	ArrayList<Object> out = new ArrayList<>();
	if (children != null) {
	    for (Object o : children) {
		out.add(o);
		if (recursive && o instanceof LSwingTreeNode) {
		    LSwingTreeNode n = (LSwingTreeNode) o;
		    out.addAll(n.getAllObjects(recursive));
		}
	    }
	}
	return out;
    }

    public void print(int depth) {
	for (Object o : getAllObjects()) {
	    if (o instanceof LSwingTreeNode) {
		LSwingTreeNode n = (LSwingTreeNode) o;
		n.print(depth + 1);
	    }
	}
    }
}
