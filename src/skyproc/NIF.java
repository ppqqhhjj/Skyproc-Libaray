/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package skyproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import lev.LFileChannel;
import lev.Ln;
import lev.LShrinkArray;
import skyproc.exceptions.BadParameter;

/**
 *
 * @author Justin Swanson
 */
public class NIF {

    private static String header = "NIF";
    String fileName;
    int numBlocks;
    ArrayList<String> blockTypes;
    ArrayList<Node> nodes;
    long headerOffset;

    /**
     * Loads in a nif file from the specified file.  Exceptions can be thrown
     * if the file specified is not a nif file, or malformed.
     * @param f File/path to load nif from.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BadParameter If the nif file is malformed (by SkyProc's standards)
     */
    public NIF(File f) throws FileNotFoundException, IOException, BadParameter {
	LFileChannel in = new LFileChannel(f);
	fileName = f.getPath();
	parseData(new LShrinkArray(in.readInByteBuffer(0, in.available())));
    }
    
    /**
     * Creates a NIF object from the given ShrinkArray.  Throws exceptions
     * if the data is not a proper nif file.
     * @param filename Name to give the NIF object.
     * @param in Nif data to parse and load into the NIF object.
     * @throws BadParameter If the data given to parse is malformed (by SkyProc's standards)
     */
    public NIF(String filename, LShrinkArray in) throws BadParameter {
	this.fileName = filename;
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
            throw new BadParameter(fileName + " was not a NIF file.");
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
            nodes.add(new Node(NodeType.SPvalueOf(blockTypes.get(type))));
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

    /**
     * A single Node and its data in the nif file.  
     */
    public class Node {

	/**
	 * Title assigned to the node.
	 */
	public String title;
	/**
	 * Type of node.
	 */
	public NodeType type;
        int size;
	/**
	 * Raw data contained in the node.
	 */
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

    /**
     * 
     */
    public enum NodeType {

	/**
	 * 
	 */
	NINODE,
	/**
	 * 
	 */
	BSINVMARKER,
	/**
	 * 
	 */
	NITRISHAPE,
	/**
	 * 
	 */
	NITRISHAPEDATA,
	/**
	 * 
	 */
	NISKININSTANCE,
	/**
	 * 
	 */
	NISKINDATA,
	/**
	 * 
	 */
	NISKINPARTITION,
	/**
	 * 
	 */
	BSLIGHTINGSHADERPROPERTY,
	/**
	 * 
	 */
	BSSHADERTEXTURESET,
	/**
	 * 
	 */
	NIALPHAPROPERTY,
	/**
	 * 
	 */
	UNKNOWN;

        static NodeType SPvalueOf(String in) {
            try {
                return valueOf(in.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
    }

    /**
     * 
     * @return List of the node types in the nif file, in order.
     */
    public ArrayList<NodeType> getNodeTypes() {
        ArrayList<NodeType> out = new ArrayList<NodeType>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) {
            out.add(nodes.get(i).type);
        }
        return out;
    }

    /**
     * 
     * @param i
     * @return The ith node in the NIF object.
     */
    public Node getNode(int i) {
        return new Node(nodes.get(i));
    }

    /**
     * 
     * @param i
     * @return The title of the ith node in the NIF object.
     */
    public String getNodeTitle(int i) {
        return nodes.get(i).title;
    }

    /**
     * 
     * @param type Type to retrieve.
     * @return List of all the Node objects matching the given type.
     */
    public ArrayList<Node> getNodes(NodeType type) {
        ArrayList<Node> out = new ArrayList<Node>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).type == type) {
                out.add(getNode(i));
            }
        }
        return out;
    }

    /**
     * A special function that returns sets of nodes each relating to a 
     * NiTriShape package. This return a list of lists containing a NiTriShape 
     * and all the nodes following up until another NiTriShape is encountered,
     * which will start another list.
     * @return List of NiTriShape node sets.
     */
    public ArrayList<ArrayList<Node>> getNiTriShapePackages() {
	ArrayList<ArrayList<Node>> out = new ArrayList<ArrayList<Node>>();
	ArrayList<Node> NiTriShapePackage = new ArrayList<Node>();
	boolean on = false;
	for (Node n : nodes) {
	    if (n.type == NodeType.NITRISHAPE) {
		NiTriShapePackage = new ArrayList<Node>();
		out.add(NiTriShapePackage);
		NiTriShapePackage.add(n);
		on = true;
	    } else if (on) {
		NiTriShapePackage.add(n);
	    }
	}
	return out;
    }
    
    /**
     * 
     * @param n Node to extract texture names from.  Must be a valid BSShaderTextureSet node
     * or the function will fail.
     * @return List of the textures in the node.
     */
    public static ArrayList<String> extractBSTextures(Node n) {
	int numTextures = n.data.extractInt(4);
	ArrayList<String> maps = new ArrayList<String>(numTextures);
	for (int i = 0; i < numTextures; i++) {
	    maps.add(n.data.extractString(n.data.extractInt(4)));
	}
	return maps;
    }
}
