package lev;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Justin Swanson
 */
public class Ln {

    /**
     *
     * @return My documents folder for the system.
     */
    public static File getMyDocuments() {
	JFileChooser fr = new JFileChooser();
	FileSystemView fw = fr.getFileSystemView();
	return fw.getDefaultDirectory();
    }

    /**
     * Returns a copy of the object, or null if the object cannot be serialized.
     *
     * @param orig Object to copy
     * @return A deep copy of the object, completely separate from the original.
     */
    public static Object deepCopy(Object orig) {
	Object obj = null;
	try {
	    // Write the object out to a byte array
	    FastByteArrayOutputStream fbos =
		    new FastByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(fbos);
	    out.writeObject(orig);
	    out.flush();
	    out.close();

	    // Retrieve an input stream from the byte array and read
	    // a copy of the object back in.
	    ObjectInputStream in =
		    new ObjectInputStream(fbos.getInputStream());
	    obj = in.readObject();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ClassNotFoundException cnfe) {
	    cnfe.printStackTrace();
	}
	return obj;
    }

    private static String space(Boolean left, Boolean concat, int spaces, char c, String... input) {
	String output = "";
	for (String x : input) {
	    output = output + x;
	}

	if (spaces < 4) {
	    spaces = 4;
	}

	if (concat && output.length() > spaces) {
	    output = output.substring(0, spaces / 2 - 2) + "..."
		    + output.substring(output.length() - (spaces / 2 - 1));
	}

	spaces = spaces - output.length();
	for (int i = 0; i < spaces; i++) {
	    if (left) {
		output = c + output;
	    } else {
		output = output + c;
	    }
	}

	return output;
    }

    /**
     * Takes the input and adds the desired amount of spaces to get the end
     * result of being "spaces" wide.
     *
     * @param enforce Whether to shrink the input to enforce the spaces size, or
     * let it bleed over to print all of the input
     * @param spaces Width desired including spaces + input.
     * @param c Character to print to achieve desired width.
     * @param input Input to print.
     * @return Final spaced string.
     */
    public static String spaceLeft(Boolean enforce, int spaces, char c, String... input) {
	return space(true, enforce, spaces, c, input);
    }

    /**
     * Takes the input and adds the desired amount of spaces to get the end
     * result of being "spaces" wide. Input is aligned left, so that the spaces
     * are on the right side.
     *
     * @param spaces Width desired including spaces + input.
     * @param c Character to print to achieve desired width.
     * @param input Input to print.
     * @return Final spaced string.
     */
    public static String spaceRight(int spaces, char c, String... input) {
	return space(false, false, spaces, c, input);
    }

    /**
     * Centers the input inside desired width.
     *
     * @param spaces Width desired.
     * @param c Character to fill to the width
     * @param input Input to center.
     * @return Final string
     */
    public static String center(int spaces, char c, String... input) {
	String output = "";
	for (String x : input) {
	    output = output + x;
	}
	int leftSpaces = (spaces - output.length()) / 2;
	for (int i = 0; i < leftSpaces; i++) {
	    output = c + output;
	}
	spaces = spaces - output.length();
	for (int i = 0; i < spaces; i++) {
	    output = output + c;
	}
	return output;
    }

    /**
     * Reverse characters in the string
     *
     * @param input
     * @return
     */
    public static String reverse(String input) {
	return String.copyValueOf(reverse(input.toCharArray()));
    }

    /**
     * Reverses characters in a char array
     *
     * @param input
     * @return
     */
    public static char[] reverse(char[] input) {
	char[] output = new char[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    /**
     * Reverses integers in an int array
     *
     * @param input
     * @return
     */
    public static int[] reverse(int[] input) {
	int[] output = new int[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    /**
     * Reverses bytes in a byte array
     *
     * @param input
     * @return
     */
    public static byte[] reverse(byte[] input) {
	byte[] output = new byte[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    /**
     * Removes comments, and trims whitespace
     *
     * @param line
     * @param comment
     * @return
     */
    public static String cleanLine(String line, String comment) {
	//Shave off comments
	int commentIndex = line.indexOf(comment);
	if (-1 != commentIndex) {
	    line = line.substring(0, commentIndex);
	}

	//Remove whitespace
	line = line.trim();

	return line;
    }

    /**
     * Removes the directory and everything inside of it.
     *
     * @param directory
     * @return
     */
    public static boolean removeDirectory(File directory) {

	if (directory == null) {
	    return false;
	}
	if (!directory.exists()) {
	    return true;
	}
	if (!directory.isDirectory()) {
	    return false;
	}

	String[] list = directory.list();

	// Some JVMs return null for File.list() when the
	// directory is empty.
	if (list != null) {
	    for (int i = 0; i < list.length; i++) {
		File entry = new File(directory, list[i]);

		if (entry.isDirectory()) {
		    if (!removeDirectory(entry)) {
			return false;
		    }
		} else {
		    if (!entry.delete()) {
			return false;
		    }
		}
	    }
	}

	return directory.delete();
    }

    /**
     * Makes the directories associated with a file.
     *
     * @param file
     */
    public static void makeDirs(File file) {
	makeDirs(file.getPath());
    }

    /**
     * Makes the directories associated with a file.
     *
     * @param file
     */
    public static void makeDirs(String file) {
	int index1 = file.lastIndexOf("/");
	int index2;
	if ((index2 = file.lastIndexOf("\\")) > index1) {
	    index1 = index2;
	}
	if (index1 == -1) {
	    return;
	}
	file = file.substring(0, index1);
	File f = new File(file);
	if (!f.exists()) {
	    f.mkdirs();
	}
    }

    /**
     * Moves a file from one directory to another. (eraseOldDirs is not
     * implemented yet)
     *
     * @param src Source file
     * @param dest Destination file
     * @param eraseOldDirs Whether to erase old empty directories.
     * @return The destination file
     */
    public static File moveFile(File src, File dest, boolean eraseOldDirs) {
	makeDirs(dest);
	src.renameTo(dest);
	if (eraseOldDirs) {
	}
	return dest;
    }

    /**
     * Recursively returns all files inside of a directory and its
     * subdirectories.<br> Has min/max depths to exclude undesired levels.
     *
     * @param src Folder to recursively search
     * @param minDepth Min depth to start adding file to the list
     * @param maxDepth Max depth to add files to the list
     * @param addDirs Include directories as files in the list, followed by
     * their contents.
     * @return List of all files inside the folder and subfolders within the
     * depth parameters.
     */
    public static ArrayList<File> generateFileList(File src, int minDepth, int maxDepth, boolean addDirs) {
	ArrayList<File> out = new ArrayList<File>();
	if (src.isDirectory()) {
	    for (File f : src.listFiles()) {
		if (minDepth <= 0 && (f.isFile() || addDirs)) {
		    out.add(f);
		}
		if (f.isDirectory() && maxDepth != 0) {
		    out.addAll(generateFileList(f, minDepth - 1, maxDepth - 1, addDirs));
		}
	    }
	}
	return out;
    }

    public static ArrayList<File> generateFileList(File src, boolean addDirs) {
	return generateFileList(src, -1, -1, addDirs);
    }

    /**
     * Converts an int array to its string equivalent.
     *
     * @param input
     * @return
     */
    public static String arrayToString(int[] input) {
	String output = "";
	for (int i = 0; i < input.length; i++) {
	    output = output + (char) input[i];
	}
	return output;
    }

    /**
     * Converts a byte array to its string equivalent.
     *
     * @param input
     * @return
     */
    public static String arrayToString(byte[] input) {
	String output = "";
	for (int i = 0; i < input.length; i++) {
	    output = output + (char) input[i];
	}
	return output;
    }

    /**
     * Returns true if input is a file (case insensitive check)
     *
     * @param test File to check if exists
     * @return true if input is a file (case insensitive check).
     */
    public static boolean isFileCaseInsensitive(File test) {
	return !getFilepathCaseInsensitive(test).getPath().equals("");
    }

    /**
     * Returns an uppercase version of the test path if it exists.
     *
     * @param test
     * @return Uppercase version of the test file.
     */
    public static File getFilepathCaseInsensitive(File test) {
	File dir = null;
	int index = test.getPath().lastIndexOf('\\');
	if (index != -1) {
	    dir = new File(test.getPath().substring(0, index));
	} else {
	    dir = new File("");
	}

	if (dir.isDirectory()) {
	    for (File file : dir.listFiles()) {
		if (test.getName().toUpperCase().equals(file.getName().toUpperCase())) {
		    return file;
		}
	    }
	}
	return new File("");
    }

    /**
     * Converts int array to a single int, assuming little endian.
     *
     * @param input
     * @return
     */
    public static int arrayToInt(int[] input) {
	return (int) arrayToLong(input);
    }

    /**
     * Converts int array to a single long, assuming little endian.
     *
     * @param input
     * @return
     */
    public static long arrayToLong(int[] input) {
	int multiplier = 1;
	long output = 0;
	for (int i = 0; i < input.length; i++) {
	    output += (int) input[i] * multiplier;
	    multiplier *= 256;
	}
	return output;
    }

    /**
     * Converts byte array to a single int, assuming little endian.
     *
     * @param input
     * @return
     */
    public static int arrayToInt(byte[] input) {
	return (int) arrayToLong(input);
    }

    /**
     * Converts byte array to a single long, assuming little endian.
     *
     * @param input
     * @return
     */
    public static long arrayToLong(byte[] input) {
	int multiplier = 1;
	long output = 0;
	for (int i = 0; i < input.length; i++) {
	    output += bToUInt(input[i]) * multiplier;
	    multiplier *= 256;
	}
	return output;
    }

    /**
     * Prints the integer representation of each index in the array.
     *
     * @param input
     * @return
     */
    public static String arrayPrintInts(int[] input) {
	String output = "";
	for (int i : input) {
	    output = output + Integer.toString(i) + " ";
	}
	return output;
    }

    /**
     * Prints the hex string of the input (assuming its one byte)
     *
     * @param input
     * @return
     */
    public static String printHex(long input) {
	if (input < 16) {
	    return "0" + Long.toHexString(input).toUpperCase();
	} else {
	    return Long.toHexString(input).toUpperCase();
	}
    }

    /**
     * Prints the hex string of the input (assuming its one byte)
     *
     * @param input
     * @return
     */
    public static String printHex(int input) {
	return printHex((long) input);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input integer to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @param minLength Will add filler zeros to achieve min length
     * @return Final string.
     */
    public static String printHex(int input, Boolean space, Boolean reverse, int minLength) {
	return printHex(Ln.toIntArray(input, minLength), space, reverse);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input array to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @return Final string.
     */
    public static String printHex(byte[] input, Boolean space, Boolean reverse) {
	return printHex(toIntArray(input), space, reverse);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input array to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @return Final string.
     */
    public static String printHex(Integer[] input, Boolean space, Boolean reverse) {
	return printHex(toIntArray(input), space, reverse);
    }

    /**
     * Prints the hex string of the input
     *
     * @param input array to print
     * @param space Adds spaces in between bytes
     * @param reverse Reverses byte printout
     * @return Final string.
     */
    public static String printHex(int[] input, Boolean space, Boolean reverse) {
	String output = "";
	for (int i : input) {
	    if (reverse) {
		if (space) {
		    output = " " + output;
		}
		output = printHex(i) + output;
	    } else {
		output = output + printHex(i);
		if (space) {
		    output = output + " ";
		}
	    }
	}
	return output;
    }

    /**
     * Prints input in the form of: "([integer representation]->[hex
     * representation])"
     *
     * @param input Number to print
     * @return Final string
     */
    public static String prettyPrintHex(int input) {
	return prettyPrintHex((long) input);
    }

    /**
     * Prints input in the form of: "([integer representation]->[hex
     * representation])"
     *
     * @param input Number to print
     * @return Final string
     */
    public static String prettyPrintHex(long input) {
	return "{" + Long.toString(input) + "->0x" + printHex(input) + "}";
    }

    /**
     * Prints a double in digit form, no scientific notation.
     *
     * @param in Double to print.
     * @param length number of characters to print.
     * @return Final double print string.
     */
    public static String printDouble(double in, int length) {
	String out = String.valueOf(in);
	if (out.length() > length) {
	    String suffix = "";
	    if (out.contains("E")) {
		suffix = out.substring(out.indexOf('E'));
	    }
	    return out.substring(0, length) + suffix;
	} else {
	    return out;
	}
    }

    /**
     * Removes all instances of the remove string from the input string.
     *
     * @param input Source string
     * @param remove String to remove
     * @return
     */
    public static String removeFromStr(String input, String remove) {

	String[] split = input.split(remove);

	String output = "";
	for (String s : split) {
	    output = output + s;
	}
	return output;
    }

    /**
     * Inserts a string inside the input string at the given index.
     *
     * @param input Source string
     * @param insert String to insert
     * @param location index to insert at.
     * @return
     */
    public static String insertInStr(String input, String insert, int location) {
	if (location <= 0) {
	    return insert + input;
	} else if (location >= input.length()) {
	    return input + insert;
	} else {
	    return input.substring(0, location - 1) + insert + input.substring(location);
	}
    }

    /**
     * Converts "true" to "1" and "false" to "0"
     *
     * @param input
     * @return
     */
    public static String convertBoolTo1(String input) {
	if (input.equals("true")) {
	    return "1";
	} else if (input.equals("false")) {
	    return "0";
	} else {
	    return input;
	}
    }

    /**
     * Converts boolean to "1" or "0"
     *
     * @param input
     * @return
     */
    public static String convertBoolTo1(Boolean input) {
	return convertBoolTo1(input.toString());
    }

    /**
     *
     * @param input
     * @return True if string equals "1" or "true"
     */
    public static Boolean toBool(String input) {
	if (input.equals("1") || input.equals("true")) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Make directories to file path if they don't exist.<br> Deletes old file
     * if delete is on.
     *
     * @param f
     * @param delete
     * @return
     */
    public static File setupFile(File f, Boolean delete) {

	File parent = new File(f.getParent());
	if (parent != null && !parent.isDirectory()) {
	    f.mkdirs();
	}

	if (delete && (f.isFile() || f.isDirectory())) {
	    f.delete();
	}

	return f;
    }

    /**
     * Make directories to file path if they don't exist.<br> Deletes old file
     * if delete is on.
     *
     * @param s
     * @param delete
     * @return
     */
    public static File setupFile(String s, Boolean delete) {
	return setupFile(new File(s), delete);
    }

    /**
     * Expands or minimizes all nodes in a GUI tree.
     *
     * @param tree
     * @param expand
     */
    public static void expandAll(JTree tree, boolean expand) {
	TreeNode root = (TreeNode) tree.getModel().getRoot();

	// Traverse tree from root
	expandAll(tree, new TreePath(root), expand);
    }

    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
	// Traverse children
	TreeNode node = (TreeNode) parent.getLastPathComponent();
	if (node.getChildCount() >= 0) {
	    for (Enumeration e = node.children(); e.hasMoreElements();) {
		TreeNode n = (TreeNode) e.nextElement();
		TreePath path = parent.pathByAddingChild(n);
		expandAll(tree, path, expand);
	    }
	}

	// Expansion or collapse must be done bottom-up
	if (expand) {
	    tree.expandPath(parent);
	} else {
	    tree.collapsePath(parent);
	}
    }

    /**
     * Parses a hex string.<br> Viable formats:<br> 1) "0123"<br> 2) "01 23"<br>
     * 3) "0x01 0x23"
     *
     * @param hex Hex string to parse.
     * @param min Minimum length, will be filled with zeros to fill.
     * @param reverse Reverse the bytes
     * @return Parsed hex string in a byte array.
     */
    public static byte[] parseHexString(String hex, int min, boolean reverse) {
	if (reverse) {
	    return Ln.reverse(parseHexString(hex, min));
	} else {
	    return parseHexString(hex, min);
	}
    }

    /**
     * Parses a hex string.<br> Viable formats:<br> 1) "0123"<br> 2) "01 23"<br>
     * 3) "0x01 0x23"
     *
     * @param hex Hex string to parse.
     * @return Parsed hex string in a byte array.
     */
    public static byte[] parseHexString(String hex) {
	return parseHexString(hex, 0);
    }

    /**
     * Parses a hex string.<br> Viable formats:<br> 1) "0123"<br> 2) "01 23"<br>
     * 3) "0x01 0x23"
     *
     * @param hex Hex string to parse.
     * @param min Minimum length, will be filled with zeros to fill.
     * @return Parsed hex string in a byte array.
     */
    public static byte[] parseHexString(String hex, int min) {
	byte[] tempOutput = new byte[1000];
	int counter = 0;

	// Cut out 0x and whitespace
	String tmpHex = "";
	Scanner scan = new Scanner(hex);
	String next;
	while (scan.hasNext()) {
	    next = scan.next();
	    if (next.indexOf('x') != -1) {
		next = next.substring(next.indexOf('x') + 1);
	    }
	    tmpHex += next;
	}
	hex = tmpHex;

	//Make even length
	if (hex.length() % 2 == 1) {
	    hex = "0" + hex;
	}
	//Increase to minimum length
	while (hex.length() < min * 2) {
	    hex = "00" + hex;
	}

	//Load and convert
	for (int i = 0; i < hex.length(); i = i + 2) {
	    tempOutput[counter++] = (byte) (int) Integer.valueOf(hex.substring(i, i + 2), 16);
	}
	byte[] output = new byte[counter];
	System.arraycopy(tempOutput, 0, output, 0, counter);
	return output;
    }

    /**
     * Converts a byte to an unsigned integer.
     *
     * @param in
     * @return
     */
    public static int bToUInt(byte in) {
	return 0x000000FF & (int) in;
    }

    /**
     * Converts to int array
     *
     * @param in
     * @return
     */
    public static int[] toIntArray(byte[] in) {
	int[] out = new int[in.length];
	for (int i = 0; i < in.length; i++) {
	    out[i] = Ln.bToUInt(in[i]);
	}
	return out;
    }

    private static int[] toIntArray(Integer[] in) {
	int[] out = new int[in.length];
	for (int i = 0; i < in.length; i++) {
	    out[i] = ((int) in[i]);
	}
	return out;
    }

    /**
     *
     * @param input
     * @return
     */
    public static int[] toIntArray(String input) {
	int[] output = new int[input.length()];
	for (int i = 0; i < input.length(); i++) {
	    output[i] = (int) input.charAt(i);
	}
	return output;
    }

    /**
     *
     * @param input
     * @return
     */
    public static int[] toIntArray(int input) {
	return toIntArray((long) input);
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static int[] toIntArray(int input, int minLength, int maxLength) {
	return toIntArray((long) input, minLength, maxLength);
    }

    /**
     *
     * @param input
     * @param minLength
     * @return
     */
    public static int[] toIntArray(int input, int minLength) {
	return toIntArray((long) input, minLength, 0);
    }

    /**
     *
     * @param input
     * @return
     */
    public static int[] toIntArray(long input) {
	return toIntArray(input, 0, 0);
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static int[] toIntArray(long input, int minLength, int maxLength) {
	if (maxLength == 0) {
	    maxLength = 16;
	}
	int[] tmp = new int[maxLength];

	int counter = 0;
	for (int i = 0; i < tmp.length && input != 0; i++) {
	    tmp[i] = (int) (input % 256);
	    input = input / 256;
	    counter++;
	}
	if (counter < minLength) {
	    counter = minLength;
	} else if (counter == 0) {
	    return new int[1];
	}

	int[] output = new int[counter];
	System.arraycopy(tmp, 0, output, 0, counter);

	return output;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(int[] input) {
	byte[] out = new byte[input.length];
	for (int i = 0; i < input.length; i++) {
	    out[i] = (byte) input[i];
	}
	return out;
    }

    /**
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(int input) {
	return new byte[]{
		    (byte) (input), (byte) (input >>> 8),
		    (byte) (input >>> 16), (byte) (input >>> 24)};
    }

    /**
     *
     * @param input
     * @param size
     * @return
     */
    public static byte[] toByteArray(int input, int size) {
	byte[] out = new byte[size];
	for (int i = 0; i < size; i++) {
	    out[i] = (byte) (input >>> (8 * i));
	}
	return out;
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static byte[] toByteArray(int input, int minLength, int maxLength) {
//	if (minLength == 4 && maxLength == 4) {
//	    return toByteArray(input);
//	} else {
	return toByteArray((long) input, minLength, maxLength);
//	}
    }

    /**
     *
     * @param input
     * @param minLength
     * @param maxLength
     * @return
     */
    public static byte[] toByteArray(long input, int minLength, int maxLength) {
	if (maxLength == 0) {
	    maxLength = 16;
	}
	byte[] tmp = new byte[maxLength];

	int counter = 0;
	for (int i = 0; i < tmp.length && input != 0; i++) {
	    tmp[i] = (byte) (input % 256);
	    input = input / 256;
	    counter++;
	}
	if (counter < minLength) {
	    counter = minLength;
	} else if (counter == 0) {
	    return new byte[0];
	}

	byte[] output = new byte[counter];
	System.arraycopy(tmp, 0, output, 0, counter);

	return output;
    }

    /**
     * Converts string to a byte array with no null terminator.
     *
     * @param input
     * @return
     */
    public static byte[] toByteArray(String input) {
	byte[] output = new byte[input.length()];
	for (int i = 0; i < input.length(); i++) {
	    output[i] = (byte) input.charAt(i);
	}
	return output;
    }

    /**
     * Print nanoseconds to a m:s format.
     *
     * @param nanoseconds
     * @return
     */
    public static String nanoTimeString(long nanoseconds) {
	int seconds = (int) (nanoseconds * Math.pow(10, -3));
	int min = seconds / 60;
	seconds = seconds % 50;
	return min + "m:" + seconds + "s";
    }

    /**
     * Replaces the suffix with the desired suffix.
     *
     * @param input
     * @param type
     * @return
     */
    public static String changeFileTypeTo(String input, String type) {
	return input.substring(0, input.lastIndexOf(".") + 1) + type;
    }

    /**
     * Returns the greatest common denominator.
     *
     * @param a
     * @param b
     * @return
     */
    public static int gcd(int a, int b) {
	// Euclidean algorithm
	int t;
	while (b != 0) {
	    t = b;
	    b = a % b;
	    a = t;
	}
	return a;
    }

    /**
     * Returns least common multiple
     *
     * @param a
     * @param b
     * @return
     */
    public static int lcm(int a, int b) {
	return (a * b / gcd(a, b));
    }

    /**
     * Returns least common multiple
     *
     * @param nums
     * @return
     */
    public static int lcmm(int... nums) {
	if (nums.length == 1) {
	    return nums[0];
	} else if (nums.length == 2) {
	    return lcm(nums[0], nums[1]);
	} else {
	    int[] rest = Arrays.copyOfRange(nums, 1, nums.length);
	    return lcm(nums[0], lcmm(rest));
	}
    }

    /**
     * A simple comparison function that compares two files byte by byte, and
     * reports the positions of any differences (eg. file1[i] != file2[i]).
     *
     * @param testFile File to test to keyFile.
     * @param keyFile Validation file to be used as a desired example.
     * @param numErrorsToPrint Number of differences to print.
     * @return True if files matched with NO differences.
     */
    public static boolean validateCompare(String testFile, String keyFile, int numErrorsToPrint) {
	String header = "Validate by Compare";
	try {
	    File good = new File(keyFile);
	    File test = new File(testFile);
	    if (good.isFile() && test.isFile()) {
		BufferedInputStream goodIn = new BufferedInputStream(new FileInputStream(good));
		BufferedInputStream testIn = new BufferedInputStream(new FileInputStream(test));
		Boolean passed = true;
		long locationCounter = 0;
		while (goodIn.available() > 0) {
		    if (goodIn.read() != testIn.read()) {
			System.out.println("Patch differed at " + Ln.prettyPrintHex(locationCounter));
			passed = false;
			if (--numErrorsToPrint == 0) {
			    break;
			}
		    }
		    locationCounter++;
		}
		if (passed) {
		    System.out.println("Patch was validated.");
		    return true;
		} else {
		    System.out.println("Patch was NOT validated.");
		    return false;
		}
	    }
	    System.out.println("Validator could not locate both files (" + testFile + "," + keyFile + ")");
	    return false;
	} catch (Exception e) {
	    System.out.println("Exception thrown while validating patch.");
	    return false;
	}
    }
}
