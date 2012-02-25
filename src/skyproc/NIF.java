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
    ArrayList<Node> blocks;
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
                SPGlobal.logSync(header, "  Added block type: " + blockType);
            }
        }


        //Blocks list
        blocks = new ArrayList<Node>(numBlocks);
        for (int i = 0; i < numBlocks; i++) {
            blocks.add(new Node(NodeType.getValue(in.extractInt(2))));
        }

        //Block lengths
        for (int i = 0; i < numBlocks; i++) {
            blocks.get(i).size = in.extractInt(4);
        }

        if (SPGlobal.debugNIFimport && SPGlobal.logging()) {
            SPGlobal.logSync(header, "Block headers: ");
            for (int i = 0; i < numBlocks; i++) {
                SPGlobal.logSync(header, "  Type: " + blocks.get(i).type + ", length: " + Ln.prettyPrintHex(blocks.get(i).size));
            }
        }

        //Strings
        int numStrings = in.extractInt(4);
        in.skip(4); // max Length string
        for (int i = 0; i < numStrings; i++) {
            in.skip(in.extractInt(4));
        }
        in.skip(4); // unknown int

        for (int i = 0; i < numBlocks; i++) {
            blocks.get(i).data = new LShrinkArray(in, blocks.get(i).size);
            in.skip(blocks.get(i).size);
        }
    }

    class Node {

        NodeType type;
        int size;
        LShrinkArray data;

        Node(NodeType n) {
            type = n;
        }
    }

    public enum NodeType {

        NiNode,
        NiTriShape,
        NiTriShapeData,
        NiSkinInstance,
        NiSkinData,
        NiSkinPartition,
        BSLightingShaderProperty,
        BSShaderTextureSet,
        NiAlphaProperty,
        UNKNOWN;

        public static NodeType getValue(int in) {
            if (in < NodeType.values().length) {
                return NodeType.values()[in];
            } else {
                return UNKNOWN;
            }
        }
    }

    public ArrayList<NodeType> getBlocks() {
        ArrayList<NodeType> out = new ArrayList<NodeType>(blocks.size());
        for (int i = 0; i < blocks.size(); i++) {
            out.add(blocks.get(i).type);
        }
        return out;
    }

    public LShrinkArray getBlock(int i) {
        return new LShrinkArray(blocks.get(i).data);
    }

    public ArrayList<LShrinkArray> getBlocks(NodeType type) {
        ArrayList<LShrinkArray> out = new ArrayList<LShrinkArray>();
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).type == type) {
                out.add(getBlock(i));
            }
        }
        return out;
    }
}
