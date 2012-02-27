/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.util.ArrayList;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
public class NIF {

    private static String header = "NIF";
    int numBlocks;
    ArrayList<String> blockTypes;
    ArrayList<Node> nodes;
    long headerOffset;

    public NIF(LShrinkArray in) throws BadParameter {
        parseData(in);
    }

    final void parseData(LShrinkArray in) throws BadParameter {
        loadHeader(in);
    }

    void loadHeader(LShrinkArray in) throws BadParameter {

        //Gamebryo header
        if (SPGlobal.debugNIFimport) {
            SPGlobal.logSync(header, "Loading nif file");
        }
        if (!in.extractString(20).equals("Gamebryo File Format")) {
            throw new BadParameter("Was not a NIF file.");
        }
        in.extractLine();

        //BlockTypes
        numBlocks = in.extractInt(9, 4);
        if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
            SPGlobal.logSync(header, "Num Blocks: " + numBlocks);
        }
        in.skip(in.extractInt(4, 1)); // Author name
        in.skip(in.extractInt(1)); // Export Info 1
        in.skip(in.extractInt(1)); // Export Info 2
        int numBlockTypes = in.extractInt(2);
        if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
            SPGlobal.logSync(header, "Num Block Types: " + numBlockTypes);
        }
        blockTypes = new ArrayList<String>(numBlockTypes);
        for (int i = 0; i < numBlockTypes; i++) {
            String blockType = in.extractString(in.extractInt(4));
            blockTypes.add(blockType);
            if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
                SPGlobal.logSync(header, "  Added block type[" + i + "]: " + blockType);
            }
        }


        //Blocks list
        if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
            SPGlobal.logSync(header, "Block Type list: ");
        }
        nodes = new ArrayList<Node>(numBlocks);
        for (int i = 0; i < numBlocks; i++) {
            int type = in.extractInt(2);
            nodes.add(new Node(NodeType.avValueOf(blockTypes.get(type))));
            if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
                SPGlobal.logSync(header, "  Block list[" + i + "] has block type: " + type + ", " + blockTypes.get(type));
            }
        }

        //Block lengths
        for (int i = 0; i < numBlocks; i++) {
            nodes.get(i).size = in.extractInt(4);
        }

        if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
            SPGlobal.logSync(header, "Block headers: ");
            for (int i = 0; i < numBlocks; i++) {
                SPGlobal.logSync(header, "  [" + i + "]: " + nodes.get(i).type + ", length: " + Ln.prettyPrintHex(nodes.get(i).size));
            }
        }

        //Strings
        if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
            SPGlobal.logSync(header, "Block Titles: ");
        }
        int numStrings = in.extractInt(4);
        in.skip(4); // max Length string
        ArrayList<String> strings = new ArrayList<String>(numStrings);
        for (int i = 0; i < numStrings; i++) {
            strings.add(in.extractString(in.extractInt(4)));
        }
        int j = 0;
        for (int i = 0; i < numBlocks && j < strings.size(); i++) {
            NodeType type = nodes.get(i).type;
            if (type == NodeType.NINODE || type == NodeType.NITRISHAPE || type == NodeType.BSINVMARKER) {
                nodes.get(i).title = strings.get(j++);
                if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
                    SPGlobal.log(header, "  [" + i + "]: " + nodes.get(i).type + ", string: " + nodes.get(i).title);
                }
            }
        }
        in.skip(4); // unknown int

        for (int i = 0; i < numBlocks; i++) {
            nodes.get(i).data = new LShrinkArray(in, nodes.get(i).size);
            in.skip(nodes.get(i).size);
        }
    }

    public class Node {

        public String title;
        public NodeType type;
        int size;
        public LShrinkArray data;

        Node(NodeType n) {
            type = n;
        }

        Node(Node in) {
            this.title = in.title;
            this.type = in.type;
            this.size = in.size;
            this.data = new LShrinkArray(in.data);
        }
    }

    public enum NodeType {

        NINODE,
        BSINVMARKER,
        NITRISHAPE,
        NITRISHAPEDATA,
        NISKININSTANCE,
        NISKINDATA,
        NISKINPARTITION,
        BSLIGHTINGSHADERPROPERTY,
        BSSHADERTEXTURESET,
        NIALPHAPROPERTY,
        UNKNOWN;

        public static NodeType avValueOf(String in) {
            try {
                return valueOf(in.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

    public ArrayList<NodeType> getNodeTypes() {
        ArrayList<NodeType> out = new ArrayList<NodeType>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            out.add(nodes.get(i).type);
        }
        return out;
    }

    public Node getNode(int i) {
        return new Node(nodes.get(i));
    }

    public String getNodeTitle(int i) {
        return nodes.get(i).title;
    }

    public ArrayList<Node> getNode(NodeType type) {
        ArrayList<Node> out = new ArrayList<Node>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).type == type) {
                out.add(getNode(i));
            }
        }
        return out;
    }
}
