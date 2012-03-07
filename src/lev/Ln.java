package lev;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Scanner;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Justin Swanson
 */
public class Ln {

    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
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
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
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
     * Takes the input and adds the desired amount of spaces to get the end result of being
     * "spaces" wide.
     * @param enforce Whether to shrink the input to enforce the spaces size, or let it bleed over to print
     * all of the input
     * @param spaces Width desired including spaces + input.
     * @param c Character to print to achieve desired width.
     * @param input Input to print.
     * @return Final spaced string.
     */
    public static String spaceLeft(Boolean enforce, int spaces, char c, String... input) {
	return space(true, enforce, spaces, c, input);
    }

    /**
     * Takes the input and adds the desired amount of spaces to get the end result of being
     * "spaces" wide.  Input is aligned left, so that the spaces are on the right side.
     * @param spaces Width desired including spaces + input.
     * @param c Character to print to achieve desired width.
     * @param input Input to print.
     * @return Final spaced string.
     */
    public static String spaceRight(int spaces, char c, String... input) {
	return space(false, false, spaces, c, input);
    }

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

    public static String reverse(String input) {
	return String.copyValueOf(reverse(input.toCharArray()));
    }

    public static char[] reverse(char[] input) {
	char[] output = new char[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    public static int[] reverse(int[] input) {
	int[] output = new int[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    public static byte[] reverse(byte[] input) {
	byte[] output = new byte[input.length];
	for (int i = 0; i < input.length; i++) {
	    output[i] = input[input.length - i - 1];
	}
	return output;
    }

    public static String cleanLine(String line) {
	//Shave off comments
	int commentIndex = line.indexOf(';');
	if (-1 != commentIndex) {
	    line = line.substring(0, commentIndex);
	}

	commentIndex = line.indexOf('#');
	if (-1 != commentIndex) {
	    line = line.substring(0, commentIndex);
	}

	//Remove whitespace
	line = line.trim();

	return line;
    }

    public static boolean removeDirectory(File directory) {

	// System.out.println("removeDirectory " + directory);

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

		//        System.out.println("\tremoving entry " + entry);

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

    public static void makeDirs(File file) {
	makeDirs(file.getPath());
    }

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

    public static File moveFile(File src, File dest, boolean eraseOldDirs) {
	makeDirs(dest);
	src.renameTo(dest);
	if (eraseOldDirs) {
	}
	return dest;
    }

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

    public static String arrayToString(int[] input) {
	String output = "";
	for (int i = 0; i < input.length; i++) {
	    output = output + (char) input[i];
	}
	return output;
    }

    public static String arrayToString(byte[] input) {
	String output = "";
	for (int i = 0; i < input.length; i++) {
	    output = output + (char) input[i];
	}
	return output;
    }

    public static boolean isFileCaseInsensitive(File test) {
	return !getFilepathCaseInsensitive(test).getPath().equals("");
    }

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

    public static int arrayToInt(int[] input) {
	return (int) arrayToLong(input);
    }

    public static long arrayToLong(int[] input) {
	int multiplier = 1;
	long output = 0;
	for (int i = 0; i < input.length; i++) {
	    output += (int) input[i] * multiplier;
	    multiplier *= 256;
	}
	return output;
    }

    public static int arrayToInt(byte[] input) {
	return (int) arrayToLong(input);
    }

    public static long arrayToLong(byte[] input) {
	int multiplier = 1;
	long output = 0;
	for (int i = 0; i < input.length; i++) {
	    output += bToUInt(input[i]) * multiplier;
	    multiplier *= 256;
	}
	return output;
    }

    public static String arrayPrintInts(int[] input) {
	String output = "";
	for (int i : input) {
	    output = output + Integer.toString(i) + " ";
	}
	return output;
    }

    public static String printHex(long input) {
	if (input < 16) {
	    return "0" + Long.toHexString(input).toUpperCase();
	} else {
	    return Long.toHexString(input).toUpperCase();
	}
    }

    public static String printHex(int input) {
	return printHex((long) input);
    }

    public static String printHex(int input, Boolean space, Boolean reverse, int minLength) {
	return printHex(Ln.toIntArray(input, minLength), space, reverse);
    }

    public static String printHex(byte[] input, Boolean space, Boolean reverse) {
	return printHex(toIntArray(input), space, reverse);
    }

    public static String printHex(Integer[] input, Boolean space, Boolean reverse) {
	return printHex(toIntArray(input), space, reverse);
    }

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

    public static String prettyPrintHex(int input) {
	return prettyPrintHex((long) input);
    }

    public static String prettyPrintHex(long input) {
	return "{" + Long.toString(input) + "->0x" + printHex(input) + "}";
    }

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

    public static String removeFromStr(String input, String remove) {

	String[] split = input.split(remove);

	/*
	 * Debug.GUIdebug.w("remove", "input is ", input, " remove is ",
	 * remove); for (String s: split) { Debug.GUIdebug.w("remove", "s is ",
	 * s); }
	 */

	String output = "";
	for (String s : split) {
	    output = output + s;
	}
	return output;
    }

    public static String insertInStr(String input, String insert, int location) {
	//Debug.GUIdebug.w("insert", "input is ", input, " insert is ", insert, " location ", Integer.toString(location));
	if (location <= 0) {
	    return insert + input;
	} else if (location >= input.length()) {
	    return input + insert;
	} else {
	    return input.substring(0, location - 1) + insert + input.substring(location);
	}
    }

    public static String convertBoolTo1(String input) {
	if (input.equals("true")) {
	    return "1";
	} else if (input.equals("false")) {
	    return "0";
	} else {
	    return input;
	}
    }

    public static String convertBoolTo1(Boolean input) {
	return convertBoolTo1(input.toString());
    }

    public static Boolean convertToBool(String input) {
	if (input.equals("1") || input.equals("true")) {
	    return true;
	} else {
	    return false;
	}
    }

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

    public static File setupFile(String s, Boolean delete) {
	return setupFile(new File(s), delete);
    }

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

    public static byte[] parseHexString(String hex, int min, boolean reverse) {
	if (reverse) {
	    return Ln.reverse(parseHexString(hex, min));
	} else {
	    return parseHexString(hex, min);
	}
    }

    public static byte[] parseHexString(String hex) {
	return parseHexString(hex, 0);
    }

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

    public static int bToUInt(byte in) {
	return 0x000000FF & (int) in;
    }

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

    public static int[] toIntArray(String input) {
	int[] output = new int[input.length()];
	for (int i = 0; i < input.length(); i++) {
	    output[i] = (int) input.charAt(i);
	}
	return output;
    }

    public static int[] toIntArray(int input) {
	return toIntArray((long) input);
    }

    public static int[] toIntArray(int input, int minLength, int maxLength) {
	return toIntArray((long) input, minLength, maxLength);
    }

    public static int[] toIntArray(int input, int minLength) {
	return toIntArray((long) input, minLength, 0);
    }

    public static int[] toIntArray(long input) {
	return toIntArray(input, 0, 0);
    }

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

    public static byte[] toByteArray(int[] input) {
	byte[] out = new byte[input.length];
	for (int i = 0; i < input.length; i++) {
	    out[i] = (byte) input[i];
	}
	return out;
    }

    public static byte[] toByteArray(int input) {
	return toByteArray(input, 0);
    }

    public static byte[] toByteArray(int input, int minLength) {
	return toByteArray(input, minLength, 0);
    }

    public static byte[] toByteArray(int input, int minLength, int maxLength) {
	return toByteArray((long) input, minLength, maxLength);
    }

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

    public static byte[] toByteArray(String input) {
	byte[] output = new byte[input.length()];
	for (int i = 0; i < input.length(); i++) {
	    output[i] = (byte) input.charAt(i);
	}
	return output;
    }

    public static String nanoTimeString(long nanoseconds) {
	int seconds = (int) (nanoseconds * Math.pow(10, -3));
	int min = seconds / 60;
	seconds = seconds % 50;
	return min + "m:" + seconds + "s";
    }

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

    public static int lcm(int a, int b) {
	return (a * b / gcd(a, b));
    }

    public static int lcmm(int ... nums) {
	if (nums.length == 2) {
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
