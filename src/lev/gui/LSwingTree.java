/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.plaf.basic.BasicTreeUI.NodeDimensionsHandler;
import javax.swing.tree.*;
import lev.Ln;

/**
 *
 * @author Justin Swanson
 */
public class LSwingTree extends LComponent {

    /**
     *
     */
    protected JScrollPane scrollPane;
    /**
     *
     */
    protected JPanel panel;
    /**
     *
     */
    protected JTree tree;
    /**
     *
     */
    protected DefaultTreeModel model;
    /**
     *
     */
    protected int topMargin = 0;
    /**
     *
     */
    protected int leftMargin = 0;
    String state;

    /**
     *
     * @param width
     * @param height
     */
    public LSwingTree(int width, int height) {
        this();
        setSize(width, height);
    }

    public LSwingTree() {
	super();
	model = new DefaultTreeModel(new DefaultMutableTreeNode());
        tree = new JTree(model);
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.white);
        add(panel);

        scrollPane = new JScrollPane(tree);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVisible(true);
        panel.add(scrollPane);

	setRowHeight(20);
        setVisible(true);
    }

    /**
     *
     * @param m
     */
    public void setModel(TreeModel m) {
        tree.setModel(m);
    }

    public void clearTree() {
	setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    }

    public void setRoot(TreeNode n) {
	setModel(new DefaultTreeModel(n));
    }

    public void expandToDepth(int depth) {
	Ln.expandToDepth(tree, depth);
    }

    /**
     *
     * @return
     */
    public JTree getTree() {
        return tree;
    }

    /**
     *
     * @param b
     */
    public void expand(Boolean b) {
        Ln.expandAll(tree, b);
    }

    /**
     *
     */
    public void expandRoot() {
        tree.expandPath(new TreePath((TreeNode) tree.getModel().getRoot()));
    }

    /**
     *
     * @param x
     */
    public final void setRowHeight(int x) {
        tree.setRowHeight(x);
    }

    /**
     *
     */
    public void removeBorder() {
        scrollPane.setViewportBorder(null);
        scrollPane.setBorder(null);
    }

    /**
     *
     * @param t
     */
    public void addTreeSelectionListener(TreeSelectionListener t) {
        tree.addTreeSelectionListener(t);
    }

    /**
     *
     * @param t
     */
    public void addTreeExpansionListener(TreeExpansionListener t) {
        tree.addTreeExpansionListener(t);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
	tree.addMouseListener(l);
    }

    /**
     *
     * @param ma
     */
    public void addMouseListener(MouseAdapter ma) {
	tree.addMouseListener(ma);
    }

    /**
     *
     * @return
     */
    public int getRowCount() {
        return tree.getRowCount();
    }

    /**
     *
     * @return
     */
    public int getRowHeight() {
        return tree.getRowHeight();
    }

    /**
     *
     * @return
     */
    public int getTotalRowHeight() {
        return getRowCount() * getRowHeight();
    }

    /**
     *
     * @param b
     */
    public void adaptToScrollbar(BoundedRangeModel b) {
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setModel(b);
    }

    /**
     *
     * @return
     */
    public TreePath[] getSelectionPaths () {
	return tree.getSelectionPaths();
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> getExpandedRows() {
        ArrayList<Integer> out = new ArrayList<Integer>();
        TreePath path;
        for (int i = 0; i < tree.getRowCount(); i++) {
            path = tree.getPathForRow(i);
            if (tree.isExpanded(path)) {
                out.add(i);
            }
        }
        return out;
    }

    /**
     *
     * @param rows
     */
    public void expandRows(ArrayList<Integer> rows) {
        for (int i : rows) {
            TreePath path = tree.getPathForRow(i);
            tree.expandPath(path);
        }
    }

    @Override
    public final void setSize(int x, int y) {
        super.setSize(x, y);
        panel.setSize(x, y);
        resetScrollSize();
    }

    /**
     *
     * @param x
     * @param y
     */
    public void setMargin(int x, int y) {
        topMargin = y;
        leftMargin = x;
        scrollPane.setLocation(x, y);
        resetScrollSize();
    }

    private void resetScrollSize() {
        scrollPane.setSize(getWidth() - leftMargin, getHeight() - topMargin);
    }

    private class CellRenderer implements TreeCellRenderer {

        JPanel renderer;
        JLabel title;

        public CellRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            renderer = new JPanel();
            renderer.setLayout(null);
            renderer.setSize(tree.getWidth(), tree.getRowHeight());
            if (row == 0) {
                renderer.setBackground(new Color(184, 213, 227));
            } else if (rootRows().contains(row)) {
                renderer.setBackground(new Color(230, 230, 230));
            } else {
                renderer.setBackground(Color.white);
            }
            renderer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(122, 138, 153)));
            renderer.setVisible(true);

            title = new JLabel(value.toString());
            title.setLocation(5, 0);
            if (leaf || row == 0) {
                title.setForeground(Color.BLACK);
            } else {
                title.setForeground(new Color(34, 123, 168));
            }
            title.setVisible(true);
            title.setSize(title.getPreferredSize());
            renderer.add(title);

            return renderer;
        }
    }

    /**
     *
     */
    public class CustomTreeUI extends BasicTreeUI {

        @Override
        protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
            return new NodeDimensionsHandler() {

                @Override
                public Rectangle getNodeDimensions(
                        Object value, int row, int depth, boolean expanded,
                        Rectangle size) {
                    Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
                    if (scrollPane != null) {
                        dimensions.width = scrollPane.getWidth() - getRowX(row, depth);
                    }
                    return dimensions;
                }
            };
        }
    }

    /**
     *
     * @return
     */
    public TreeNode getRoot() {
        return (TreeNode) tree.getModel().getRoot();
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> rootRows() {
        ArrayList<Integer> out = new ArrayList<Integer>();

        TreePath rootPath = new TreePath(getRoot());
        TreeNode root = (TreeNode) rootPath.getLastPathComponent();
        if (root.getChildCount() >= 0) {
            for (Enumeration e = root.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = rootPath.pathByAddingChild(n);
                out.add(tree.getRowForPath(path));
            }
        }
        return out;
    }

    /**
     *
     * @param row
     * @return
     */
    public String getExpansionState(int row) {
	return Ln.getExpansionState(tree, row);
    }

    /**
     *
     * @param row
     * @param state
     */
    public void restoreExpansionState (int row, String state) {
	Ln.restoreExpanstionState(tree, row, state);
    }

    /**
     *
     */
    public void saveExpansionState () {
	state = getExpansionState(0);
    }

    /**
     *
     */
    public void restoreExpansionState () {
	restoreExpansionState(0, state);
    }

    /**
     *
     * @param node
     */
    public void nodeChanged (TreeNode node) {
	model.nodeChanged(node);
    }

    public TreeSelectionModel getSelectionModel() {
	return tree.getSelectionModel();
    }

    public Object getLastSelectedPathComponent() {
	return tree.getLastSelectedPathComponent();
    }

    /**
     *
     * @return
     */
    public int getLeadSelectionRow () {
	return tree.getLeadSelectionRow();
    }

    /**
     *
     * @param row
     * @return
     */
    public TreePath getPathForRow(int row) {
	return tree.getPathForRow(row);
    }
}
